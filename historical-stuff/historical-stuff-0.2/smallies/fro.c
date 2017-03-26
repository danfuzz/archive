/* ------------------------------------------------------------------------- */
/* fro.c: replacement for from                                               */
/*                                                                           */
/* by Danfuzz Bornstein                                                      */
/*                                                                           */
/* usage: fro [--help] [--verbose] [--file name]                             */
/* ------------------------------------------------------------------------- */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

char *prog = "fro";
char *filename = NULL;
int verbose = 0;
int number = 0;


void truncbuf (char *buf, int len, int pad)
{
    int reallen = strlen (buf);

    if (reallen > len)
    {
        buf[len] = '\0';
    }
    else if (pad)
    {
        while (reallen < len)
        {
            buf[reallen] = ' ';
            reallen++;
        }
        buf[reallen] = '\0';
    }
}



void ridnl (char *buf)
{
    char *n = strchr (buf, '\n');
    if (n != NULL)
    {
        *n = '\0';
    }
}



void massageaddr (char *src, char *dest)
{
    char *addrpart;
    char *namepart;
    char *more;
    int lev;
    int foundmore = 0;

    more = src;
    lev = 0;
    while (*more)
    {
        if (*more == '(' || *more == '{')
        {
            lev++;
        }
        else if (*more == '}' || *more == ')')
        {
            lev--;
        }
        else if (*more == ',' && lev == 0)
        {
            foundmore = 1;
            *more = '\0';
            break;
        }
        more++;
    }

    addrpart = strchr (src, '<');

    if (addrpart != NULL)
    {
        char *end = strchr (addrpart, '>');
        if (end == NULL)
        {
            fprintf (stderr, "%s: unexpected malformed angle address; "
                     "improvising\n",
                     prog);
            fprintf (stderr, "%s: line in question: %s\n",
                     prog, src);
            end = addrpart + strlen(addrpart);
        }
        addrpart++;
        *end = '\0';
        namepart = src;
        end = addrpart - 2;
        while (end >= src && (*end == ' ' || *end == '\t'))
        {
            end--;
        }
        end++;
        *end = '\0';
    }
    else
    {
        char *end = strchr (src, '(');
        if (end == NULL)
        {
            namepart = "";
            addrpart = src;
        }
        else
        {
            addrpart = src;
            namepart = end + 1;
            end--;
            while (end >= src && (*end == ' ' || *end == '\t'))
            {
                end--;
            }
            end++;
            *end = '\0';
            end = strchr (namepart, ')');
            if (end == NULL)
            {
                fprintf (stderr, "%s: unexpected malformed paren address\n",
                         prog);
                exit (1);
            }
            *end = '\0';
        }
    }

    if (*namepart && *addrpart)
    {
        sprintf (dest, "%s <%s>", namepart, addrpart);
    }
    else if (*namepart)
    {
        strcpy (dest, namepart);
    }
    else if (*addrpart)
    {
        strcpy (dest, addrpart);
    }
    else
    {
        strcpy (dest, "nobody");
    }

    if (foundmore)
    {
        strcat (dest, ", ...");
    }
}



void readHeader (FILE *f, char *from, char *to, char *sub, char *date)
{
    char line[500];
    char word1[50];
    char word2[50];
    char *allbut1;
    int contentlen = -1;
    int gotfirstfrom = 0;

    from[0] = to[0] = sub[0] = date[0] = '\0';

    while (! feof (f))
    {
        line[0] = '\0';
        fgets (line, 500, f);
        if (line[0] == '\0')
        {
            return;
        }
        ridnl (line);
        word1[0] = word2[0] = '\0';
        allbut1 = "";
        sscanf (line, "%s %s", word1, word2);
        if (word2[0] != '\0')
        {
            allbut1 = line + strlen (word1) + 1;
            while (*allbut1 == ' ' || *allbut1 == '\t')
            {
                allbut1++;
            }
        }
        if (! gotfirstfrom)
        {
            if (strcmp (word1, "From") == 0)
            {
                gotfirstfrom = 1;
                strcpy (from, word2);
            }
        }
        else
        {
            if (line[0] == '\0')
            {
                if (contentlen > 0)
                {
                    fseek (f, contentlen + 1, SEEK_CUR);
                }
                return;
            }
            if (strcasecmp (word1, "from:") == 0)
            {
                massageaddr (allbut1, from);
            }
            else if (strcasecmp (word1, "to:") == 0)
            {
                massageaddr (allbut1, to);
            }
            else if (strcasecmp (word1, "subject:") == 0)
            {
                strcpy (sub, allbut1);
            }
            else if (strcasecmp (word1, "date:") == 0)
            {
                strcpy (date, allbut1);
            }
            else if (strcasecmp (word1, "content-length:") == 0)
            {
                contentlen = atoi (word2);
            }
        }
    }
}



void doFrom (void)
{
    FILE *f = fopen (filename, "r");
    char frombuf[500];
    char tobuf[500];
    char subbuf[500];
    char datebuf[500];
    int curNum = 0;

    if (! f)
    {
        fprintf (stderr, "%s: couldn't open file `%s'\n", prog, filename);
        exit (1);
    }

    while (! feof (f))
    {
        readHeader (f, frombuf, tobuf, subbuf, datebuf);
        if (frombuf[0] == '\0')
            break;
        if (verbose)
        {
            if (number) 
            {
                printf ("Number:  %d", curNum);
            }
            truncbuf (frombuf, 70, 0);
            truncbuf (tobuf, 70, 0);
            truncbuf (subbuf, 70, 0);
            truncbuf (datebuf, 70, 0);
            printf ("From:    %s\n"
                    "To:      %s\n"
                    "Subject: %s\n"
                    "Date:    %s\n\n",
                    frombuf, tobuf, subbuf, datebuf);
        }
        else
        {
            if (number) 
            {
                printf ("%05d ", curNum);
            }
            truncbuf (frombuf, 15, 1);
            truncbuf (tobuf, 15, 1);
            truncbuf (subbuf, number ? 37 : 43, 0);
            printf ("[%s] [%s] %s\n", frombuf, tobuf, subbuf);
        }
        curNum++;
    }

    fclose (f);
}



int main (int argc, char *argv[])
{
    int i;
    prog = *argv;

    for (i = 1; i < argc; i++)
    {
        char *arg = argv[i];
        if (   strcmp (arg, "--help") == 0
            || strcmp (arg, "-h") == 0)
        {
            fprintf (stderr, "usage: %s [--help] [--verbose] [--number] [--file name]\n",
                     prog);
            exit (1);
        }
        else if (strcmp (arg, "--verbose") == 0)
        {
            verbose = 1;
        }
        else if (strcmp (arg, "--number") == 0)
        {
            number = 1;
        }
        else if (strcmp (arg, "--file") == 0)
        {
            i++;
            if (i == argc)
            {
                fprintf (stderr, "%s: no file specified\n", prog);
                exit (1);
            }
            filename = argv[i];
        }
        else
        {
            fprintf (stderr, "%s: unrecognized option `%s'\n", prog, arg);
            exit (1);
        }
    }

    if (filename == NULL)
    {
        filename = getenv ("mail");
    }

    if (filename == NULL)
    {
        filename = malloc (200);
        strcpy (filename, "/home/");
        strcat (filename, (char *) getenv ("USER"));
        strcat (filename, "/inbox");
    }

    doFrom ();
    exit (0);
}

