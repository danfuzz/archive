/* ------------------------------------------------------------------------- */
/* nntpdo [--verbose|--quiet] host group number[..number]                    */
/* nntpdo [--verbose|--quiet] host message-id                                */
/* nntpdo [--verbose|--quiet] host --commands cmd ...                        */
/*                                                                           */
/* Grab article <number> in <group> from the news server on <host>. If       */
/* <number> is negative, get the <number>th from last article. With the      */
/* form number..number, grab all articles in that range, inclusive           */
/*                                                                           */
/* -or-                                                                      */
/*                                                                           */
/* Grab message <message-id> (with or without angle brackets) from the       */
/* news server on <host>.                                                    */
/*                                                                           */
/* -or-                                                                      */
/*                                                                           */
/* Execute the list of <cmd>s on the news server on <host>. Show full server */
/* responses, with no filtering (of "." lines).                              */
/*                                                                           */
/* With --verbose, explicitly shows commands sent (prepended by ">>") and    */
/* server status responses (prepended by "<<"). It also prints out           */
/* connection and disconnection messages.                                    */
/*                                                                           */
/* With --quiet, suppresses error messages.                                  */
/*                                                                           */
/* by Danfuzz                                                                */
/*                                                                           */
/* Link with ss.o -lsocket -lnsl.                                            */
/* ------------------------------------------------------------------------- */

/* ------------------------------------------------------------------------- */
/* Some #includes and other definitions                                      */
/* ------------------------------------------------------------------------- */

#include "ss.h"

#include <stdio.h>
#include <stdarg.h>

int realResponses = 0;



/* ------------------------------------------------------------------------- */
/* News stuff                                                                */
/* ------------------------------------------------------------------------- */

/* grab a status line into the given buffer, and return the 3 digit code */
int grabStatus (ssLink *cl, char *respbuf, int len)
{
    int stat;
    int resp;

    if ((resp = ssReadNl (cl, respbuf, &len)) != 0)
        return (resp);

    if (len < 4)
        return (ssErr);

    stat =   ((respbuf[0] - '0') * 100) 
           + ((respbuf[1] - '0') * 10)
           +  (respbuf[2] - '0');

    while (respbuf[len-1] == '\n' || respbuf[len-1] == '\r')
    {
        respbuf[len - 1] = '\0';
        len--;
    }

    if (realResponses)
        printf ("%s\n", respbuf);

    return (stat);
}



/* grab a full response--print included text, but print response line */
/* only if being verbose. Return response 3 digit response code, and  */
/* leaves the final status line in the passed buffer.                 */
int grabResponse (ssLink *cl, char *respbuf, int len)
{
    int stat;
    int n;
    char buf[2048];

    stat = grabStatus (cl, respbuf, len);

    if (   stat != 100
        && stat != 215
        && stat != 220
        && stat != 221
        && stat != 222
        && stat != 230
        && stat != 231)
    {
        /* This response doesn't include more text */
        return (stat);
    }

    for (;;)
    {
        int len = 2048;
        n = ssReadNl (cl, buf, &len);
        if (n != 0)
        {
            return (n);
        }

        while (buf[len-1] == '\n' || buf[len-1] == '\r')
        {
            buf[len - 1] = '\0';
            len--;
        }

        if (buf[0] == '.')
        {
            if (buf[1] == '\0')
            {
                printf (realResponses ? ".\n" : "\n");
                return (stat);
            }

            printf ("%s\n", realResponses ? buf : buf + 1);
        }
        else
        {
            printf ("%s\n", buf);
        }
    }

    return (stat);
}



void sendCommand (ssLink *cl, char *fmt, ...)
{
    va_list args;
    char buf[512];

    va_start (args, fmt);
    vsprintf (buf, fmt, args);
    va_end (args);

    ssWriteString (cl, buf);
    ssWriteString (cl, "\n");
}



int openNews (ssLink *l, char *host)
{
    ssAddr a;
    int n;
    char buf[600];

    if ((n = ssResolveName (host, &a)) != 0)
    {
        return (n);
    }

    if ((n = ssOpenTcp (l, &a, 119)) != 0)   /* 119 is the nntp port */
    {
        return (n);
    }

    if ((n = grabResponse (l, buf, 600)) != 200)
    {
        /* 1st response is supposted to be 200, the hello message */
        return (n < 0 ? n : ssErr);
    }

    return (0);
}



int closeNews (ssLink *l)
{
    char buf[600];
    int n;

    /* we'll be a courteous citizen */
    sendCommand (l, "quit");

    if ((n = grabResponse (l, buf, 600)) != 205)
    {
        /* response is supposted to be 205, the goodbye message */
        return (n < 0 ? n : ssErr);
    }

    ssClose (l);
}



/* ------------------------------------------------------------------------- */
/* Main bits                                                                 */
/* ------------------------------------------------------------------------- */

/* ------------------------------------------------------------------------- */
/* getGroupArticle (host, group, number): Connect to the nntp port of        */
/* <host>, and grab the <number>th article in <group>. If <number> is        */
/* negative, get the <number>th from last article.                           */
/* ------------------------------------------------------------------------- */

void getGroupArticle (char *host, char *group, char *number)
{
    ssLink cl;
    char buf[600];
    int n;
    int articlenum = atol (number);
    int articlenum2;

    if ((n = openNews (&cl, host)) != 0)
    {
        ssPerror (n);
        exit (1);
    }

    sendCommand (&cl, "group %s", group);

    n = grabResponse (&cl, buf, 600); /* should be "211 n1 n2 n3 groupname" */
    if (n != 211)
        goto cleanup;

    {
        int min;
        int max;
        char *more;
        
        if (sscanf (buf + 3, "%*d %d %d", &min, &max) != 2)
            goto cleanup;

        articlenum = strtol (number, &more, 10);
        if (more == number)
        {
            ssError ("Bad article number");
            goto cleanup;
        }

        if (*more == '\0')
        {
            articlenum2 = articlenum;
        }
        else
        {
            if (   more[0] != '.' 
                || more[1] != '.' 
                || (articlenum2 = strtol (more + 2, 0, 10)) == 0)
            {
                ssError ("Bad article number");
                goto cleanup;
            }
        }
    
        if (articlenum < 0)
            articlenum = max + 1 + articlenum;

        if (articlenum2 < 0)
            articlenum2 = max + 1 + articlenum2;

        if (articlenum < min || articlenum > max)
        {
            ssError ("Article %d out of range; truncating",
                     articlenum);
            if (articlenum < min)
                articlenum = min;
            else
                articlenum = max;
        }

        if (articlenum2 < min || articlenum2 > max)
        {
            ssError ("Article %d out of range; truncating",
                     articlenum2);
            if (articlenum2 < min)
                articlenum2 = min;
            else
                articlenum2 = max;
        }
    }

    if (articlenum < articlenum2)
        articlenum--;
    else
        articlenum++;

    while (articlenum != articlenum2)
    {
        if (articlenum < articlenum2)
            articlenum++;
        else
            articlenum--;

        sendCommand (&cl, "article %d", articlenum);
        n = grabResponse (&cl, buf, 600);
        if (n != 220)
        {
            ssError ("Article %d not found\n", articlenum);
        }
    }

/*L*/ cleanup:

    closeNews (&cl);
}



/* ------------------------------------------------------------------------- */
/* getMessageId (host, id):  Connect to the nntp port of <host>, and get     */
/* the message with id <id>.                                                 */
/* ------------------------------------------------------------------------- */

void getMessageId (char *host, char *id)
{
    ssLink cl;
    char buf[600];
    int n;

    if ((n = openNews (&cl, host)) != 0)
    {
        ssPerror (n);
        exit (1);
    }

    if (*id == '<')
        sendCommand (&cl, "article %s", id);
    else
        sendCommand (&cl, "article <%s>", id);

    n = grabResponse (&cl, buf, 600);

    if (n != 220)
    {
        ssError ("Article %s not found\n", id);
    }

    closeNews (&cl);
}



/* ------------------------------------------------------------------------- */
/* doNewsCommands (host, commands):  Connect to the nntp port of <host>, and */
/* do the given list of <commands>.                                          */
/* ------------------------------------------------------------------------- */

void doNewsCommands (char *host, char *commands[])
{
    ssLink cl;
    char buf[600];
    int n;

    if ((n = openNews (&cl, host)) != 0)
    {
        ssPerror (n);
        exit (1);
    }

    realResponses = 1;

    while (*commands)
    {
        /* send a command */
        sendCommand (&cl, "%s", *commands);

        /* get the response */
        grabResponse (&cl, buf, 600);

        commands++;
    }

    realResponses = 0;

    closeNews (&cl);
}



/* ------------------------------------------------------------------------- */
/* Simple usage message                                                      */
/* ------------------------------------------------------------------------- */

void usage (void)
{
    fprintf (stderr, 
             "usage: %s [--verbose|--quiet] host group number[..number]\n",
             ssProg);
    fprintf (stderr, "usage: %s [--verbose|--quiet] host message-id\n",
             ssProg);
    fprintf (stderr, "usage: %s [--verbose|--quiet] host --commands cmd ...\n",
             ssProg);
    exit (2);
}



/* ------------------------------------------------------------------------- */
/* Main: the way of life                                                     */
/* ------------------------------------------------------------------------- */

int main (int argc, char *argv[])
{
    ssInit (argv[0], stderr);

    if (argc < 3)
        usage ();

    if (strcmp (argv[1], "--verbose") == 0)
    {
        argc--;
        argv++;
        ssVerbose = 1;
        ssShowIO = 1;
    }
    else if (strcmp (argv[1], "--quiet") == 0)
    {
        argc--;
        argv++;
        ssErrors = 0;
    }

    if (strcmp (argv[2], "--commands") == 0)
    {
        doNewsCommands (argv[1], argv + 3);
    }
    else
    {
        if (argc < 3 || argc > 4)
            usage ();

        if (argc == 3)
            getMessageId (argv[1], argv[2]);
        else
            getGroupArticle (argv[1], argv[2], argv[3]);
    }

    exit (0);
}
