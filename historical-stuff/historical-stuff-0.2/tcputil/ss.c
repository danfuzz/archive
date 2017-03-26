/* ------------------------------------------------------------------------- */
/* Simple Sockets                                                            */
/*                                                                           */
/* by Dan Bornstein                                                          */
/*                                                                           */
/* Link with -lsocket -lnsl                                                  */
/* ------------------------------------------------------------------------- */

#include "ss.h"

#include <stdarg.h>
#include <string.h>
#include <arpa/inet.h>
#include <errno.h>



/* ------------------------------------------------------------------------- */
/* Variable Definitions                                                      */
/* ------------------------------------------------------------------------- */

int ssVerbose = 0;
int ssErrors = 1;
int ssShowIO = 0;
char *ssProg;
FILE *ssErrout;



/* ------------------------------------------------------------------------- */
/* Private Functions                                                         */
/* ------------------------------------------------------------------------- */

/* set up our local port to the Internet: fill in the passed fd with */
/* the socket we end up with; return 0 = success, non-0 = error code */
/* note: port 0 means any port                                       */
static int ssGetLocalSocket (int *fd, ssPort port)
{
    struct sockaddr_in sa;
 
    *fd = socket (PF_INET, SOCK_STREAM, 0);
    if (*fd < 0)
    {
        return (ssErrCheckErrno);
    }
 
    sa.sin_family = AF_INET;
    sa.sin_port = port;
    sa.sin_addr.s_addr = INADDR_ANY; /* use our local address */
 
    if (bind (*fd, (struct sockaddr *) &sa, sizeof (sa)))
    {
        return (ssErrCheckErrno);
    }
 
    return (0);
}



/* fill in a struct sockaddr_in given the two useful fields */
static void ssFillAddress (struct sockaddr_in *sa, ssAddr *addr, ssPort port)
{
    sa->sin_len = sizeof (struct sockaddr_in);
    sa->sin_family = AF_INET;
    sa->sin_port = port;
    if (addr == INADDR_ANY)
        sa->sin_addr.s_addr = INADDR_ANY;
    else
        sa->sin_addr = *addr;
}



/* clear out a link */
static void ssClearLink (ssLink *l)
{
    l->fd = 0;
    l->status = ssNoConnection;
    memset (&l->dest, 0, sizeof (l->dest));
}



/* called when the other side closes on us */
static void ssTheyClosedOnUs (ssLink *l)
{
    l->status = ssTheyClosed;
    ssMsgVerbose ("Connection closed by foreign host (%s, port %d)",
                  inet_ntoa (l->dest.sin_addr),
                  l->dest.sin_port);
}



/* va_list print a tagged message to error out */
static void ssMsgVa (char *fmt, va_list args)
{
    fprintf (ssErrout, "%s: ", ssProg);
    vfprintf (ssErrout, fmt, args);
    fprintf (ssErrout, "\n");
}



/* spit out a non-control version of the given string */
void ssShowString (char *buf, int len)
{
    static char xdigs[16] = "0123456789abcdef";
    char *outbuf = alloca (len * 6);
    char *o = outbuf;

    while (len-- > 0)
    {
        char c = *(buf++);

        if (((unsigned char) c & 0x80) != 0)
        {
            *(o++) = '\\';
            *(o++) = '+';
            c &= 0x7f;
        }

        if (c < ' ' || c == '\\' || c == '\x7f')
        {
            *(o++) = '\\';
            switch (c)
            {
              case '\n': *(o++) = 'n'; break;
              case '\r': *(o++) = 'r'; break;
              case '\t': *(o++) = 't'; break;
              case '\\': *(o++) = '\\'; break;
              case '\x7f': *(o++) = '?'; break;
              default:
                {
                    *(o++) = 'x';
                    *(o++) = xdigs[(c >> 4) & 0xf];
                    *(o++) = xdigs[c & 0xf];
                    break;
                }
            }
        }
        else
        {
            *(o++) = c;
        }
    }

    *o = '\0';
    fputs (outbuf, ssErrout);
}



/* show reads */
void ssShowRead (char *buf, int len)
{
    static char readBuf[77];
    static int readBufSize = 0;
    static int readBufLeft = 76;

    while (len != 0)
    {
        char *nl = memchr (buf, '\n', len);
        int n = ((nl != NULL) 
                 ? (nl - buf) 
                 : (readBufLeft > len ? len : readBufLeft));
        memcpy (readBuf + readBufSize, buf, n);
        len -= n;
        buf += n;
        readBufSize += n;
        readBufLeft -= n;
        if (   readBufLeft == 0
            || (len > 0 && *buf == '\n'))
        {
            fputs ("<< ", ssErrout);
            ssShowString (readBuf, readBufSize);
            putc ('\n', ssErrout);
            readBufSize = 0;
            readBufLeft = 76;
            if (len > 0 && *buf == '\n')
            {
                buf++;
                len--;
            }
        }
    }
}



/* show writes */
void ssShowWrite (char *buf, int len)
{
    static char writeBuf[77];
    static int writeBufSize = 0;
    static int writeBufLeft = 76;

    while (len != 0)
    {
        char *nl = memchr (buf, '\n', len);
        int n = ((nl != NULL) 
                 ? (nl - buf) 
                 : (writeBufLeft > len ? len : writeBufLeft));
        memcpy (writeBuf + writeBufSize, buf, n);
        len -= n;
        buf += n;
        writeBufSize += n;
        writeBufLeft -= n;
        if (   writeBufLeft == 0
            || (len > 0 && *buf == '\n'))
        {
            fputs (">> ", ssErrout);
            ssShowString (writeBuf, writeBufSize);
            putc ('\n', ssErrout);
            writeBufSize = 0;
            writeBufLeft = 76;
            if (len > 0 && *buf == '\n')
            {
                buf++;
                len--;
            }
        }
    }
}



/* ------------------------------------------------------------------------- */
/* Public Functions                                                          */
/* ------------------------------------------------------------------------- */

/* ------------------------------------------------------------------------- */
/* Initialization & Errors                                                   */
/* ------------------------------------------------------------------------- */

int ssInit (char *prog, FILE *err)
{
    ssProg = prog;
    ssErrout = err;
}



void ssPerror (int errCode)
{
    if (! ssErrors)
        return;

    switch (errCode)
    {
      case ssErr:
        ssError ("Uspecified error");
        break;
      case ssErrCheckErrno:
        perror (ssProg);
        break;
      case ssErrCantResolve:
        ssError ("Can't resolve address");
        break;
      case ssErrNoSpace:
        ssError ("No space to store result");
        break;
      case ssErrBadLink:
        ssError ("Bad link object");
        break;
      case ssErrConnDrop:
        ssError ("Connection has dropped");
        break;
      default:
        ssError ("Unknown error");
        break;
    }
}



/* print a tagged message to our error out */
void ssMsg (char *fmt, ...)
{
    va_list args;

    va_start (args, fmt);
    ssMsgVa (fmt, args);
    va_end (args);
}



/* print a tagged message to our error out, but only if verbose is on */
void ssMsgVerbose (char *fmt, ...)
{
    if (ssVerbose)
    {
        va_list args;
        
        va_start (args, fmt);
        ssMsgVa (fmt, args);
        va_end (args);
    }
}



/* print a tagged message to our error out, but only if errors are on */
void ssError (char *fmt, ...)
{
    if (ssErrors)
    {
        va_list args;
        
        va_start (args, fmt);
        ssMsgVa (fmt, args);
        va_end (args);
    }
}



/* fatal message: print and die, no matter what */
void ssFatal (char *fmt, ...)
{
    va_list args;
        
    va_start (args, fmt);
    ssMsgVa (fmt, args);
    va_end (args);

    exit (1);
}



/* ------------------------------------------------------------------------- */
/* Name / Address resolution                                                 */
/* ------------------------------------------------------------------------- */

int ssResolveName (char *name, ssAddr *addr)
{
    struct hostent *host;
 
    /* try looking up the address by name, as in "wastelands.kaleida.com" */
    host = gethostbyname (name);
    if (host)
    {
        /* it succeeded; fill out the inet number from the host struct. */
        memcpy ((char *) addr, 
                (char *) host->h_addr, 
                sizeof (ssAddr));
    }
    else
    {
        /* it failed; maybe this is a numbery thing like 130.43.11.12 */
        addr->s_addr = inet_addr (name);
 
        if (addr->s_addr == (unsigned long) -1)
        {
            /* that failed too */
            return (ssErrCantResolve);
        }
    }

    return (0);
}



int ssResolveAddr (ssAddr *addr, char *name, int *len)
{
    struct hostent *host;
    char *result = NULL;
    int l = 0;

    /* look it up */
    host = gethostbyaddr ((void *) addr, sizeof (ssAddr), AF_INET);
    if (host == NULL)
    {
        /* it didn't work; we'll just construct an ascii inet number */
        result = inet_ntoa (*addr);
    }
    else
    {
        result = host->h_name;
    }

    l = strlen (result) + 1;
    if (l > *len)
    {
        *len = l;
        return (ssErrNoSpace);
    }

    strcpy (name, result);
    *len = l;
    return (0);
}



/* ------------------------------------------------------------------------- */
/* Opening and Closing Connections                                           */
/* ------------------------------------------------------------------------- */

int ssOpenTcp (ssLink *l, ssAddr *dest, ssPort port)
{
    int result;

    ssFillAddress (&l->dest, dest, port);

    if ((result = ssGetLocalSocket (&l->fd, 0)) != 0)
    {
        return (result);
    }

    ssMsgVerbose ("Trying %s, port %hu", inet_ntoa (*dest), port);

    if (connect (l->fd, (struct sockaddr *) &l->dest, sizeof (l->dest)) != 0)
    {
        return (ssErrCheckErrno);
    }

    ssMsgVerbose ("Connected");

    l->status = ssConnected;
 
    return (0);
}



int ssOpenServer (ssLink *l, ssPort port, int backlog)
{
    int result;

    ssFillAddress (&l->dest, INADDR_ANY, port);

    if ((result = ssGetLocalSocket (&l->fd, port)) != 0)
    {
        return (result);
    }

    if (listen (l->fd, backlog) != 0)
    {
        return (ssErrCheckErrno);
    }

    l->status = ssServing;

    ssMsgVerbose ("Listening on port %hu", port);

    return (0);
}



int ssOpenService (ssLink *l, ssLink *conn)
{
    int len = sizeof (struct sockaddr_in);
    int fd = accept (l->fd, (struct sockaddr *) &conn->dest, &len);
 
    if (fd == -1)
    {
        return (ssErrCheckErrno);
    }

    conn->fd = fd;
    conn->status = ssConnected;

    ssMsgVerbose ("Connected to %s, port %hu", 
                  inet_ntoa (conn->dest.sin_addr),
                  conn->dest.sin_port);

    return (0);
}



int ssClose (ssLink *l)
{
    switch (l->status)
    {
      case ssConnected:
      case ssReadReady:
      case ssTheyClosed:
      case ssServing:
        {
            close (l->fd);
            ssClearLink (l);
            break;
        }
      case ssNoConnection:
        {
            ssClearLink (l);
            break;
        }
    }

    return (0);
}



int ssUpdateStatus (ssLink *l, long timeout)
{
    struct timeval when;
    fd_set ready;

    if (l->status != ssConnected)
    {
        return (0);
    }

    when.tv_sec  = timeout;
    when.tv_usec = 0;
 
    for (;;)
    {
        FD_ZERO (&ready);
        FD_SET (l->fd, &ready);
        if (select (l->fd + 1, &ready, NULL, NULL, &when) < 0)
        {
            if (errno == EINTR)
            {
                /* try again if it's just an interrupted call */
                continue;
            }
            else if (errno == ECONNRESET || errno == ECONNABORTED)
            {
                /* the other side went away */
                ssTheyClosedOnUs (l);
                return (0);
            }

            /* otherwise, it's a real error */
            ssClose (l);
            return (ssErrCheckErrno);
        }

        /* we got a good reading */
        break;
    }
 
    if (FD_ISSET (l->fd, &ready))
    {
        l->status = ssReadReady;
    }
    else
    {
        l->status = ssConnected;
    }

    return (0);
}



/* ------------------------------------------------------------------------- */
/* Actual I/O                                                                */
/* ------------------------------------------------------------------------- */

int ssRead (ssLink *l, void *buf, int *len)
{
    int want = *len;

    *len = 0; /* len turns into a so-far count */

    while (want != 0)
    {
        switch (l->status)
        {
          case ssConnected:
            {
                int result = ssUpdateStatus (l, 86400);
                if (result != 0)
                {
                    return (result);
                }
                break;
            }
          case ssReadReady:
          case ssTheyClosed:
            {
                int n = read (l->fd, buf, want);
                if (n <= 0)
                {
                    if (   errno == EWOULDBLOCK
                        || errno == EAGAIN 
                        || errno == EINTR)
                    {
                        /* do nothing--weird retry conditions */
                    }
                    else if (   n == 0
                             || errno == ECONNRESET 
                             || errno == ECONNABORTED)
                    {
                        /* the other side went away */
                        if (l->status != ssTheyClosed)
                        {
                            ssTheyClosedOnUs (l);
                            return (0);
                        }
                    }
                    else
                    {
                        /* a real error */
                        ssClose (l);
                        return (ssErrCheckErrno);
                    }
                }
                else
                {
                    if (ssShowIO)
                    {
                        ssShowRead (buf, n);
                    }
                    want -= n;
                    (*len) += n;
                    buf = (char *) buf + n;
                }

                if (l->status == ssReadReady)
                {
                    l->status = ssConnected;
                }
                break;
            }
          case ssServing:
          case ssNoConnection:
            {
                return (ssErrBadLink);
                break;
            }
        }
    }

    return (0);
}



int ssReadTerm (ssLink *l, void *buf, int *len, char term)
{
    int want = *len;

    *len = 0; /* len turns into a so-far count */

    while (want != 0)
    {
        int got = 1;
        int result = ssRead (l, buf, &got);
        if (result != 0)
        {
            return (result);
        }
        (*len)++;
        want--;
        if (*(char *) buf == term)
        {
            return (0);
        }
        buf = (char *) buf + 1;
        if (l->status == ssTheyClosed)
            return (0);
    }

    return (0);
}



int ssReadNl (ssLink *l, void *buf, int *len)
{
    int result;

    (*len)--;
    result = ssReadTerm (l, buf, len, '\n');

    ((char *)buf)[*len] = '\0';
    return (result);
}



int ssWrite (ssLink *l, void *buf, int len)
{
    if (ssShowIO)
    {
        ssShowWrite (buf, len);
    }

    while (len != 0)
    {
        switch (l->status)
        {
          case ssConnected:
          case ssReadReady:
            {
                int n = write (l->fd, buf, len);
                if (n == -1)
                {
                    if (   errno == EWOULDBLOCK
                        || errno == EAGAIN
                        || errno == EINTR)
                    {
                        /* do nothing--weird retry conditions */
                    }
                    else if (   errno == ECONNRESET 
                             || errno == ECONNABORTED)
                    {
                        /* the other side went away */
                        if (l->status != ssTheyClosed)
                        {
                            ssTheyClosedOnUs (l);
                            return (ssErrConnDrop);
                        }
                    }
                    else
                    {
                        /* a real error */
                        ssClose (l);
                        return (ssErrCheckErrno);
                    }                        
                }
                else
                {
                    len -= n;
                    buf = (char *) buf + n;
                }
                break;
            }
          case ssTheyClosed:
            {
                return (ssErrConnDrop);
                break;
            }
          case ssServing:
          case ssNoConnection:
            {
                return (ssErrBadLink);
                break;
            }
        }
    }
    
    return (0);
}



int ssWriteString (ssLink *l, char *buf)
{
    ssWrite (l, buf, strlen (buf));
}
