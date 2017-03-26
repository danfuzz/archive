/* ------------------------------------------------------------------------- */
/* Simple Sockets                                                            */
/*                                                                           */
/* by Dan Bornstein                                                          */
/* ------------------------------------------------------------------------- */

#ifndef _ss_h_
#define _ss_h_

#include <sys/types.h>
#include <sys/socket.h>
#include <sys/time.h>
#include <netinet/in.h>
#include <netdb.h>
#include <stdio.h>



/* ------------------------------------------------------------------------- */
/* Type definitions                                                          */
/* ------------------------------------------------------------------------- */

typedef enum
{
    ssConnected = 7,
    ssReadReady,
    ssTheyClosed,
    ssNoConnection,
    ssServing
}
ssStatus;

typedef struct
{
    int fd;                  /* fd for socket or connection   */
    ssStatus status;         /* current status of connection  */
    struct sockaddr_in dest; /* the other end of the conn.    */
    /* Note: for server connections, dest is about *this* end */
}
ssLink;

typedef unsigned short ssPort; /* a port number */
typedef struct in_addr ssAddr; /* an address    */


/* ------------------------------------------------------------------------- */
/* Variable Definitions                                                      */
/* ------------------------------------------------------------------------- */

extern int ssVerbose;  /* bool for verbose messages                     */
extern int ssErrors;   /* bool for error reporting                      */
extern int ssShowIO;   /* bool for show all i/o over connections        */
extern char *ssProg;   /* program name (take from argv[0])              */
extern FILE *ssErrout; /* where to send error output (take from stderr) */



/* ------------------------------------------------------------------------- */
/* Error codes                                                               */
/* ------------------------------------------------------------------------- */

#define ssErr                (-1)
#define ssErrCheckErrno      (-2)
#define ssErrCantResolve     (-3)
#define ssErrNoSpace         (-4)
#define ssErrBadLink         (-5)
#define ssErrConnDrop        (-6)



/* ------------------------------------------------------------------------- */
/* Function Declarations; unless otherwise noted they return 0 on            */
/* success and error code on failure                                         */
/* ------------------------------------------------------------------------- */

/* ------------------------------------------------------------------------- */
/* Initialization & Errors                                                   */
/* ------------------------------------------------------------------------- */

/* initialize the system */
int ssInit (char *prog, FILE *err);

/* print appropriate message for given code */
void ssPerror (int errCode);

/* print a tagged message to our error out */
void ssMsg (char *fmt, ...);

/* print a tagged message to our error out, but only if verbose is on */
void ssMsgVerbose (char *fmt, ...);

/* print a tagged message to our error out, but only if errors are on */
void ssError (char *fmt, ...);

/* fatal message: print and die, no matter what */
void ssFatal (char *fmt, ...);



/* ------------------------------------------------------------------------- */
/* Name / Address resolution                                                 */
/* ------------------------------------------------------------------------- */

/* resolve a stringy host name into a numbery one */
int ssResolveName (char *name, ssAddr *addr);

/* resolve a numbery host into a stringy one. sets len to actual length */
/* including the null; if error is ssErrNoSpace, then len is set to     */
/* to how much space is actually needed, but the string isn't copied.   */
int ssResolveAddr (ssAddr *addr, char *name, int *len);



/* ------------------------------------------------------------------------- */
/* Opening and Closing Connections                                           */
/* ------------------------------------------------------------------------- */

/* open a tcp connection to the given address and port */
int ssOpenTcp (ssLink *l, ssAddr *dest, ssPort port);

/* make a server link for the given port with given backlog size */
int ssOpenServer (ssLink *l, ssPort port, int backlog);

/* open a connection from the given server link */
int ssOpenService (ssLink *l, ssLink *conn);

/* close a telnet or serve connection or a server itself */
int ssClose (ssLink *l);

/* update status on the given link; wait the given number of */
/* seconds for data (0 = return immediately)                 */
int ssGetStatus (ssLink *l, long timeout);



/* ------------------------------------------------------------------------- */
/* Actual I/O                                                                */
/* ------------------------------------------------------------------------- */

/* read a fixed amount of data from a connection; it tries very hard */
/* to read that much but will fail if the connection closes; sets    */
/* len to the actual amount read.                                    */
int ssRead (ssLink *l, void *buf, int *len);

/* read a specific-character-terminated string from a connection;   */
/* will not read more than len characters; does not null-terminate; */
/* sets len to the actual amount read.                              */
int ssReadTerm (ssLink *l, void *buf, int *len, char term);

/* read a newline-terminated string from a connection; null terminates */
/* and includes the newline; will not read more than len-1 characters; */
/* sets len to actual number of characters read.                       */
int ssReadNl (ssLink *l, void *buf, int *len);

/* write a fixed amount of data to a connection; it will try very hard */
/* to write it but will fail if the connection closes */
int ssWrite (ssLink *l, void *buf, int len);

/* write a null-terminated string to a connection (don't write the null) */
int ssWriteString (ssLink *l, char *buf);

#endif
