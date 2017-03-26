/* ------------------------------------------------------------------------- */
/* vrfy [--verbose|--quiet] [--host host] address [address ...]              */
/*                                                                           */
/* Do a smtp VRFY command for the given list of addresses and                */
/* nicely format the output.                                                 */
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

#define DEFAULT_HOST ("localhost")



/* ------------------------------------------------------------------------- */
/* Some #includes and other definitions                                      */
/* ------------------------------------------------------------------------- */

#include "ss.h"

#include <stdio.h>
#include <stdarg.h>
#include <errno.h>



/* ------------------------------------------------------------------------- */
/* Smtp stuff                                                                */
/* ------------------------------------------------------------------------- */

/* grab a response line into the given buffer, and return the 3 digit code;
   add an extra 1000 if this is a not-finished response. */
int grabResponse (ssLink *cl, char *respbuf, int len)
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

    if (respbuf[3] == '-')
        stat += 1000;

    while (respbuf[len-1] == '\n' || respbuf[len-1] == '\r')
    {
        respbuf[len - 1] = '\0';
        len--;
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



/* ------------------------------------------------------------------------- */
/* Main bits                                                                 */
/* ------------------------------------------------------------------------- */

/* ------------------------------------------------------------------------- */
/* doVrfy (host, names): Connect to the smtp port of <host> and do a         */
/* VRFY on the given names, nicely formatting the responses.                 */
/* ------------------------------------------------------------------------- */

void doVrfy (char *host, char *names[])
{
    ssLink cl;
    ssAddr a;
    char buf[600];
    int n;
    int first;

    if ((n = ssResolveName (host, &a)) != 0)
    {
        ssPerror (n);
        exit (1);
    }

    if ((n = ssOpenTcp (&cl, &a, 25)) != 0)   /* 25 is the smtp port */
    {
        ssPerror (n);
        exit (1);
    }

    do
    {
        n = grabResponse (&cl, buf, 600); /* 1st response is the hello msg */
    }
    while (n >= 1000);

    while (*names)
    {
        /* send a command */
        sendCommand (&cl, "vrfy %s", *names);

        /* get the response */
        first = 1;
        do
        {
            n = grabResponse (&cl, buf, 600);
            if (n == 550)
            {
                printf ("%s: unknown\n", *names);
            }
            else if (n != 250 && n != 1250)
            {
                if (ssErrors)
                    fprintf (stderr, "%s: Unexpected server response\n",
                             ssProg);
                exit (1);
            }
            else
            {
                if (first)
                {
                    printf ("%s =>\n", *names);
                    first = 0;
                }
                printf ("   %s\n", buf + 4);
            }
        }
        while (n >= 1000);

        names++;
    }

    /* we'll be a courteous citizen */
    sendCommand (&cl, "quit");

    grabResponse (&cl, buf, 600); /* quit response; should be "bye!" */

    ssClose (&cl);
}



/* ------------------------------------------------------------------------- */
/* Simple usage message                                                      */
/* ------------------------------------------------------------------------- */

void usage (void)
{
    fprintf (stderr, 
             "usage: %s [--verbose|--quiet] [--host host] "
             "address [address ...]\n",
             ssProg);
    exit (2);
}



/* ------------------------------------------------------------------------- */
/* Main: the way of life                                                     */
/* ------------------------------------------------------------------------- */

int main (int argc, char *argv[])
{
    char *host = DEFAULT_HOST;

    ssInit (argv[0], stderr);

    if (argc < 2)
        usage ();

    if (strcmp (argv[1], "--verbose") == 0)
    {
        argc--;
        argv++;
        ssShowIO = 1;
        ssVerbose = 1;
    }
    else if (strcmp (argv[1], "--quiet") == 0)
    {
        argc--;
        argv++;
        ssErrors = 0;
    }

    if (strcmp (argv[1], "--host") == 0)
    {
        if (argc < 3)
            usage ();

        host = argv[2];
        argc -= 2;
        argv += 2;
    }

    doVrfy (host, argv + 1);

    exit (0);
}
