/* blah */

#include <sys/types.h>
#include <sys/socket.h>
#include <netdb.h>
#include <stdio.h>

#define MAXNAME (64)
#define CACHESIZE (0x800)
#define CACHEMASK (0x7ff)

typedef struct
{
    long hash;
    char in[MAXNAME];
    char out[MAXNAME];
}
CacheEntry;

CacheEntry cache[CACHESIZE];

int wantNums = 0;



void clearCache (void)
{
    int i;
    for (i = 0; i < CACHESIZE; i++)
    {
        cache[i].in[0] = '\0';
        cache[i].out[0] = '\0';
    }
}

int hashOf (char *in)
{
    int len = 0;
    unsigned int hash = 42;
    char c;

    while ((c = *in) != 0)
    {
        len++;
        if (len == MAXNAME)
            return (-1);
        hash = ((hash >> 27) + (hash << 3)) ^ c;
        in++;
    }

    hash ^= (hash >> 16);
    return (hash & CACHEMASK);
}

char *find (char *in, int hash)
{
    if (cache[hash].hash == hash)
    {
        if (strcmp (cache[hash].in, in) == 0)
            return (cache[hash].out);
    }

    return (0);
}

void add (char *in, char *out, int hash)
{
    if (strlen (out) > MAXNAME - 1)
        return;

    cache[hash].hash = hash;
    strcpy (cache[hash].in, in);
    strcpy (cache[hash].out, out);
}



void downcase (char *str)
{
    while (*str)
    {
        *str = tolower (*str);
        str++;
    }
}



struct hostent *figureOut (char *one)
{
    struct hostent *frob;
    long addr[4];

    if (sscanf (one, "%d.%d.%d.%d",
                &addr[0], &addr[1], &addr[2], &addr[3]) == 4)
    {
        unsigned char ca[4];

        ca[0] = addr[0];
        ca[1] = addr[1];
        ca[2] = addr[2];
        ca[3] = addr[3];

        frob = gethostbyaddr (ca, 4, AF_INET);
    }
    else
    {
        frob = gethostbyname (one);
    }

    if (frob && (frob->h_name[0] != '\0'))
        return (frob);
    else
        return (0);
}



void doOne (char *one)
{
    struct hostent *frob;
    int hash = hashOf (one);
    char outbuf[200];

    if (hash != -1)
    {
        char *out = find (one, hash);
        if (out != NULL)
        {
            printf ("%s\n", out);
            return;
        }
    }

    frob = figureOut (one);

    if (frob)
    {
        if (wantNums)
        {
            int a[4];
            unsigned char *addr = frob->h_addr_list[0];
            a[0] = addr[0];
            a[1] = addr[1];
            a[2] = addr[2];
            a[3] = addr[3];

            sprintf (outbuf, "%d.%d.%d.%d", a[0], a[1], a[2], a[3]);
        }
        else
        {
            sprintf (outbuf, "%s", frob->h_name);
        }
    }
    else
    {
        sprintf (outbuf, "%s [?]", one);
    }

    downcase (outbuf);
    if (hash != -1)
    {
        add (one, outbuf, hash);
    }
    printf ("%s\n", outbuf);
}



int main (int argc, char *argv[])
{
    clearCache ();

    while (argc > 1)
    {
        if (strcmp (argv[1], "-n") == 0)
        {
            wantNums = 1;
        }
        else if (   (strcmp (argv[1], "-h") == 0)
                 || (strcmp (argv[1], "-help") == 0))
        {
            fprintf (stderr, "usage: %s [-n] [-h] [-help] [hostNameOrAddr]\n",
                     argv[0]);
            exit (0);
        }
        else
        {
            break;
        }
        argc--;
        argv++;
    }

    if (argc == 2)
        doOne (argv[1]);
    else
    {
        while (! feof (stdin))
        {
            char buf[1024];
            buf[0] = 0;
            fgets (buf, 1024, stdin);
            if (buf[0] == 0)
                break;
            buf[strlen (buf) - 1] = 0;
            doOne (buf);
        }
    }

    exit (0);
}
