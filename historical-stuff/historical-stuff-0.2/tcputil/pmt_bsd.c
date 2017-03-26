/* ------------------------------------------------------------------------- */
/* Poor Man's Telnet                                                         */
/*                                                                           */
/* by Danfuzz                                                                */
/*                                                                           */
/* Note: This is very BSD-dependent. It may or may not work on a SysV        */
/* machine depending on how well they did their BSD-compatibility library.   */
/*                                                                           */
/* Hope you appreciate this--it's the result of an evening's worth of        */
/* spelunking through man pages, header files, and even *real books*!        */
/* ------------------------------------------------------------------------- */

/* ------------------------------------------------------------------------- */
/* Some #includes and other definitions                                      */
/* ------------------------------------------------------------------------- */

#include <stdio.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/time.h>
#include <netinet/in.h>
#include <netdb.h>
#include <signal.h>
#include <fcntl.h>

#include <unistd.h>    /* can probably be deleted if there's trouble */
#include <arpa/inet.h> /* can probably be deleted if there's trouble */

extern int errno;



/* ------------------------------------------------------------------------- */
/* Definitions for Poor Man's Telnet                                         */
/* ------------------------------------------------------------------------- */

/* name for this program; you might want to set it from argv[0] */
char *myname = "pmt";

/* Status of a connection. */
typedef enum
{
    hoHum,            /* nothing's happening                           */
    readReady,        /* ready to read, or other end is about to close */
    theyClosed,       /* other end closed on us                        */
    noConnection      /* no connection left                            */
}
Status;

/* Structure to hold connection info; Passed to pmt funcs kinda like a
   FILE * */
typedef struct
{
    int sock;                 /* communication socket (fd) */
    Status s;                 /* current status            */
}
CommLink;

CommLink in;                  /* CommLink for stdin          */
int saveBlock;                /* fopts saved state for stdin */


/* ------------------------------------------------------------------------- */
/* Internal Functions -- you can ignore this stuff                           */
/* ------------------------------------------------------------------------- */

/* Set up our local port-o-entry to the Internet--set a passed socket
   to the fd (file descriptor) we end up with. Return: 0 = success,
   1 = failure */
int setupHere (int *sock)
{
    struct sockaddr_in sa;

    *sock = socket (AF_INET, SOCK_STREAM, 0);
    if (*sock < 0)
    {
        perror (myname);
        return (1);
    }

    sa.sin_family = AF_INET;         /* protocol family       */
    sa.sin_port = 0;                 /* pick any port         */
    sa.sin_addr.s_addr = INADDR_ANY; /* use our local address */

    if (bind (*sock, &sa, sizeof (sa)))
    {
        perror (myname);
        return (1);
    }

    return (0);
}



/* Set up the stuff pointing at the remote address--fill in a sockaddr_in
   (socket address for Internet) with all the vitals. Return: 0 = success,
   1 = failure */
int setupThere (char *address, unsigned short port, struct sockaddr_in *sa)
{
    struct hostent *host;

    sa->sin_family = AF_INET;    /* protocol family */
    sa->sin_port = htons (port); /* port to use     */

    /* Try looking up the address by name, as in "wastelands.kaleida.com" */
    host = gethostbyname (address);
    if (host)
    {
        /* It succeeded... Fill out the inet number from the host struct. */
        memcpy ((char *) &sa->sin_addr, (char *) host->h_addr, host->h_length);
        
        /* sa->sin_addr.s_addr = *((unsigned long *)(*host->h_addr_list)); */
    }
    else
    {
        /* It failed... Maybe this is a numbery thing like 130.43.11.12 */
        sa->sin_addr.s_addr = inet_addr (address);

        if (sa->sin_addr.s_addr == (unsigned long) -1)
        {
            /* That failed too */
            fprintf (stderr, "%s: Can't suss the address \"%s\", Pops.\n",
                     myname, address);
            return (1);
        }
    }
    
    return (0);
}



/* Called when "they" close from the other side */
void cutoff (CommLink *c)
{
    if (c->s != hoHum) return;

    if (c->sock == 0)
       fcntl (c->sock, F_SETFL, saveBlock);
     
    close (c->sock);

    c->s = theyClosed;

    fprintf (stderr, "%s: Connection closed (by \"them\")\n", myname);
}



#if 0
/* Called when you hit ^C etc */
void handleQuit (void)
{
    void pmtShutdown (void);

    pmtShutdown ();
    fprintf (stderr, "\nPoof!\n");
    exit (0);
}



int restoreBlock;

/* Called when you hit ^Z */
void handleStop (void)
{
    fcntl (in.sock, F_GETFL, restoreBlock);
    fcntl (in.sock, F_SETFL, saveBlock);
}

/* Called when you continue this process */
void handleCont (void)
{
    fcntl (in.sock, F_SETFL, restoreBlock);
}
#endif  


/* ------------------------------------------------------------------------- */
/* External Functions -- you call these puppies                              */
/* ------------------------------------------------------------------------- */

/* Set up the pmt system */
int pmtSetup (void)
{
    int fopt;

    in.sock = 0; /* stdin */
    in.s = hoHum;

    fcntl (in.sock, F_GETFL, &fopt);
    saveBlock = fopt;
    fopt |= O_NONBLOCK;

    signal (SIGPIPE,   SIG_IGN);
#if 0
    signal (SIGHUP,    handleQuit);
    signal (SIGINT,    handleQuit);
    signal (SIGQUIT,   handleQuit);
    signal (SIGILL,    handleQuit);
    signal (SIGTRAP,   handleQuit);
    signal (SIGABRT,   handleQuit);
    signal (SIGEMT,    handleQuit);
    signal (SIGFPE,    handleQuit);
    signal (SIGBUS,    handleQuit);
    signal (SIGSEGV,   handleQuit);
    signal (SIGSYS,    handleQuit);
    signal (SIGALRM,   handleQuit);
    signal (SIGTERM,   handleQuit);
    signal (SIGTSTP,   handleStop);
    signal (SIGCONT,   handleCont);
    signal (SIGXCPU,   handleQuit);
    signal (SIGXFSZ,   handleQuit);
    signal (SIGVTALRM, handleQuit);
    signal (SIGPROF,   handleQuit);
    signal (SIGUSR1,   handleQuit);
    signal (SIGUSR2,   handleQuit);
#endif

    /* fcntl (in.sock, F_SETFL, fopt); */

    return (0);
}



/* Shutdown the pmt system */
void pmtShutdown (void)
{
    fcntl (in.sock, F_SETFL, saveBlock);
    close (in.sock);
}



/* Start up a telnet (TCP/IP) session with the given address and port;
   sets up the given CommLink. Return: 0 = success, 1 = failure */
int telnet (char *address, unsigned short port, CommLink *c)
{
    struct sockaddr_in sa;
    int fopt;

    if (setupHere (&c->sock))
        return (1);

    if (setupThere (address, port, &sa))
        return (1);

    fprintf (stderr, "%s: Trying %s, port %d ...\n",
             myname, inet_ntoa (&sa.sin_addr.s_addr), port);

    if (connect (c->sock, &sa, sizeof (sa)))
    {
        perror (myname);
        fprintf (stderr, "%s: connect failed\n", myname);
        return (1);
    }

    fcntl (in.sock, F_GETFL, &fopt);
    fopt |= O_NONBLOCK;
    fcntl (in.sock, F_SETFL, fopt);

    fprintf (stderr, "%s: Connected\n", myname);

    return (0);
}



/* Close the given CommLink */
void pmtClose (CommLink *c)
{
    if (c->s != hoHum) {
        c->s = noConnection;
        return;
    }

    close (c->sock);
    c->s = noConnection;

    fprintf (stderr, "%s: Connection closed (locally)\n", myname);
}



/* Get status on the given CommLink. -1 timeout means block indefinitely;
   0 means return immediately; positive numbers mean wait that many
   seconds. */
Status getStatus (CommLink *c, long timeout)
{
    struct timeval when;
    fd_set ready;

    if (c->s != hoHum)
        return (c->s);

    when.tv_sec  = timeout;
    when.tv_usec = 0;

    for (;;)
    {
        FD_ZERO (&ready);
        FD_SET (c->sock, &ready);
        if (select (c->sock + 1, &ready, NULL, NULL, 
                    timeout != -1 ? &when : NULL) < 0)
        {
            /* try again if it's just an interrupted call */
            if (errno == EINTR)
                continue;

            perror (myname);
            pmtClose (c);
            return (noConnection);
        }
        else
            break;
    }

    if (FD_ISSET (c->sock, &ready))
        return (readReady);
    else
        return (hoHum);
}



/* Write a string to the CommLink. */
void writeString (CommLink *c, char *s)
{
    if (c->s != hoHum) return;

    write (c->sock, s, strlen (s));
}



/* Read a line from the CommLink (terminated by newline); assumes buffer
   is of sufficient size. Returns number of chars read, including the
   newline, but the newline isn't actually appended to the end of the
   string. Note sometimes a readReady really means that the connection
   is going to close, so you may get zero back from this call. */
int readString (CommLink *c, char *s)
{
    char ch;
    int n = 0;
    int rn;

    if (c->s != hoHum)
    {
        *s = '\0';
        return (0);
    }

    for (;;)
    {
        errno = 0;
        rn = read (c->sock, &ch, 1);
        if (rn <= 0)
        {
            if (errno == EWOULDBLOCK || errno == EAGAIN)
            {
                /* do nothing--this shouldn't happen, though */
            }
            else if (rn == 0 || errno == ECONNRESET || errno == ECONNABORTED)
            {
                /* the other side went away */
                cutoff (c);
                break;
            }
            else
            {
                /* something weird */
                perror (myname);
                pmtClose (c);
                break;
            }
        }

        if (ch == '\n')
        {
            /* exit the loop on a newline */
            n++;
            break;
        }
        else if (ch < ' ')
            ; /* ignore any other control character (or null) */
        else if (ch)
        {
            *(s++) = ch;
            n++;
        }
    }

    *s = '\0';
    return (n);
}



/* ------------------------------------------------------------------------- */
/* Sample One. This connects to the finger port of argv[2], sends argv[3],   */
/* and then gets responses until the other side closes. Call it like:        */
/*   pmt finger localhost danfuzz                                            */
/* ------------------------------------------------------------------------- */

int pmtFinger (int argc, char *argv[])
{
    CommLink cl;
    char buf[500];
    Status s;
    int n;

    if (pmtSetup ())
        exit (1);

    if (argc < 4)
    {
        fprintf (stderr, "usage: %s %s host userid\n", myname, argv[1]);
        pmtShutdown ();
        exit (1);
    }

    if (telnet (argv[2], 79, &cl))    /* 79 is the finger port */
    {
        pmtShutdown ();
        exit (1);
    }

    writeString (&cl, argv[3]);
    writeString (&cl, "\n");

    s = hoHum;
    while (s != noConnection)
    {
        s = getStatus (&cl, 2);      /* wait for at most 2 seconds for data */

        switch (s)
        {
          case hoHum:
            /* we timed out */
            printf ("Ho hum...\n");
            break;
          case theyClosed:
            /* the other side closed on us, so we'll close off too */
            printf ("They Closed...\n");
            pmtClose (&cl);
            break;
          case noConnection:
            /* No more connection; we'll exit the loop */
            printf ("No Connection...\n");
            break;
          case readReady:
            /* Oo! Something to read (maybe) */
            n = readString (&cl, buf);
            if (n == 0)
                break; /* the connection probably got cut off */
            printf (">> %s\n", buf);
            break;
        }
    }

    pmtShutdown (); /* important! otherwise your shell may barf (mine did) */
    exit (0);
}



/* ------------------------------------------------------------------------- */
/* Sample two. This one connects to the echo port of argv[2] and will        */
/* send anything you type at the keyboard. It also prints everything coming  */
/* from the other end (i.e. echos). If it reads a line that begins with 'Z'  */
/* it'll send something else. Call it like:                                  */
/*   pmt echo localhost                                                      */
/* ------------------------------------------------------------------------- */

int pmtEcho (int argc, char *argv[])
{
    CommLink cl;
    char buf[500];
    Status s;
    int n;

    if (pmtSetup ())
        exit (1);

    if (argc < 3)
    {
        fprintf (stderr, "usage: %s %s host userid\n", myname, argv[1]);
        pmtShutdown ();
        exit (1);
    }

    if (telnet (argv[2], 7, &cl))    /* 7 is the echo port */
    {
        pmtShutdown ();
        exit (1);
    }

    s = hoHum;
    while (s != noConnection)
    {
        s = getStatus (&in, 0);      /* see if user typed anything */
        if (s == readReady)
        {
            /* They did--read in a line and write it out to the port */
            n = readString (&in, buf);
            writeString (&cl, buf);
            writeString (&cl, "\n");
        }
        else if (s == theyClosed)
        {
            pmtClose (&cl);
        }

        s = getStatus (&cl, 1);      /* wait for at most 1 second for data */
                                     /* from the other side                */

        switch (s)
        {
          case hoHum:
            /* we timed out */
            printf ("Ho hum...\n");
            break;
          case theyClosed:
            /* the other side closed on us, so we'll close off too */
            printf ("They Closed...\n");
            pmtClose (&cl);
            break;
          case noConnection:
            /* No more connection; we'll exit the loop */
            printf ("No Connection...\n");
            break;
          case readReady:
            /* Oo! Something to read (maybe) */
            n = readString (&cl, buf);
            if (n == 0)
                break; /* the connection probably got cut off */
            printf (">> %s\n", buf);
            if (buf[0] == 'Z')
                writeString (&cl, "Yowza!\n");
            break;
        }
    }

    pmtShutdown ();
    exit (0);
}


int main (int argc, char *argv[])
{
    pmtEcho (argc, argv);
}
