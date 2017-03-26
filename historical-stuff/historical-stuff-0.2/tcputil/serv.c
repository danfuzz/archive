/* ------------------------------------------------------------------------- */
/* serv                                                                      */
/*                                                                           */
/* A TCP server shell. Set up to do echo right now.                          */
/*                                                                           */
/* by Danfuzz                                                                */
/*                                                                           */
/* Link with -lsocket -lnsl.                                                 */
/* ------------------------------------------------------------------------- */



/* ------------------------------------------------------------------------- */
/* Some #includes and other definitions                                      */
/* ------------------------------------------------------------------------- */

#include <stdio.h>
#include <stdarg.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/time.h>
#include <netinet/in.h>
#include <netdb.h>
#include <signal.h>

#include <unistd.h>    /* can probably be deleted if there's trouble */
#include <arpa/inet.h> /* can probably be deleted if there's trouble */

extern int errno;



/* ------------------------------------------------------------------------- */
/* Definitions for Poor Man's Sockets                                        */
/* ------------------------------------------------------------------------- */

/* name for this program; you might want to set it from argv[0] */
char *myname = "serv";

/* Status of a connection. */
typedef enum
{
    hoHum,            /* nothing's happening                           */
    readReady,        /* ready to read, or other end is about to close */
    theyClosed,       /* other end closed on us                        */
    noConnection      /* no connection left                            */
}
Status;

/* Structure to hold connection info; Passed to funcs kinda like a
   FILE * */
typedef struct
{
    int fd;                   /* i/o file descriptor/socket  */
    Status s;                 /* current status              */
    struct sockaddr_in dest;  /* other end of the link       */
}
CommLink;

int verbose = 1;
int errors = 1;



/* ------------------------------------------------------------------------- */
/* Internal Functions -- you can ignore this stuff                           */
/* ------------------------------------------------------------------------- */

/* Set up our local port-o-entry to the Internet--set a passed socket
   to the fd (file descriptor) we end up with. Return: 0 = success,
   1 = failure */
int setupHere (int *sock, unsigned short port)
{
    struct sockaddr_in sa;

    *sock = socket (PF_INET, SOCK_STREAM, 0);
    if (*sock < 0)
    {
        perror (myname);
        return (1);
    }

    sa.sin_family = AF_INET;         /* protocol family       */
    sa.sin_port = port;
    sa.sin_addr.s_addr = INADDR_ANY; /* use our local address */

    if (bind (*sock, (struct sockaddr *) &sa, sizeof (sa)))
    {
        perror (myname);
        return (1);
    }

    return (0);
}



/* Called when "they" close from the other side */
void cutoff (CommLink *c)
{
    if (c->s != hoHum) return;

    close (c->fd);

    c->s = theyClosed;

    if (verbose) 
        fprintf (stderr, "%s: Connection closed (by \"them\")\n", myname);
}



/* ------------------------------------------------------------------------- */
/* External Functions -- you call these puppies                              */
/* ------------------------------------------------------------------------- */

/* Become a server for the given port, with the given limit on
   the number of connections. Return: 0 = success, 1 = failure */
int serve (unsigned short port, int backlog, CommLink *c)
{
    if (setupHere (&c->fd, port))
        return (1);

    c->dest.sin_addr.s_addr = 0x00000000;

    if (listen (c->fd, backlog))
    {
        perror (myname);
        return (1);
    }

    if (verbose)
    {
        fprintf (stderr, "%s: Listening on port %hu\n", myname, port);
    }

    return (0);
}



/* Stop serving */
int unServe (CommLink *c)
{
    close (c->fd);
    return (0);
}



/* Pick up a call--that is accept an incoming connection. */
int pickUp (CommLink *l, CommLink *c)
{
    int len = sizeof (struct sockaddr_in);
    int fd = accept (l->fd, (struct sockaddr *) &c->dest, &len);

    if (fd == -1)
    {
        perror (myname);
        return (1);
    }

    if (verbose)
    {
        fprintf (stderr, "%s: Connected to 0x%08x\n", 
                 myname, c->dest.sin_addr.s_addr);
    }

    c->fd = fd;
    c->s = hoHum;
    return (0);
}



/* Close the given CommLink */
void pmtClose (CommLink *c)
{
    if (c->s != hoHum) 
    {
        c->s = noConnection;
        return;
    }

    close (c->fd);
    c->s = noConnection;

    if (verbose)
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
        FD_SET (c->fd, &ready);
        if (select (c->fd + 1, &ready, NULL, NULL, 
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

    if (FD_ISSET (c->fd, &ready))
        return (readReady);
    else
        return (hoHum);
}



/* Write a string to the CommLink. */
void writeString (CommLink *c, char *s)
{
    if (c->s != hoHum) return;

    write (c->fd, s, strlen (s));
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
        rn = read (c->fd, &ch, 1);
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
/* Main bits                                                                 */
/* ------------------------------------------------------------------------- */

/* ------------------------------------------------------------------------- */
/* doEcho: accept connections on port 8088 and echo the whole                */
/* shmegeggy.                                                                */
/* ------------------------------------------------------------------------- */

void doEcho (void)
{
    CommLink ss, cc;
    char buf[600];
    Status s;
    int done;

    if (serve (8088, 2, &ss))
    {
        exit (1);
    }

    for (;;)
    {
        if (pickUp (&ss, &cc))
        {
            exit (1);
        }

        done = 0;
        while (! done)
        {
            s = getStatus (&cc, 2);
            switch (s)
            {
              case hoHum:
                {
                    printf ("hoHum...\n");
                    break;
                }
              case readReady:
                {
                    readString (&cc, buf);
                    writeString (&cc, buf);
                    writeString (&cc, "\n");
                    break;
                }
              case theyClosed:
                {
                    pmtClose (&cc);
                    break;
                }
              case noConnection:
                {
                    done = 1;
                    break;
                }
            }
        }
    }
}



/* ------------------------------------------------------------------------- */
/* Main: the way of life                                                     */
/* ------------------------------------------------------------------------- */

int main (int argc, char *argv[])
{
    myname = argv[0];

    doEcho ();

    exit (0);
}
