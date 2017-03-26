/* ------------------------------------------------------------------------- */
/* serv                                                                      */
/*                                                                           */
/* A TCP server shell. Set up to do echo right now.                          */
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



/* ------------------------------------------------------------------------- */
/* Main bits                                                                 */
/* ------------------------------------------------------------------------- */

/* ------------------------------------------------------------------------- */
/* doEcho: accept connections on port 8080 and echo the whole                */
/* shmegeggy.                                                                */
/* ------------------------------------------------------------------------- */

void doEcho (void)
{
    ssLink ss, cc;
    char buf[600];
    ssStatus s;
    int done;
    int n;

    if ((n = ssOpenServer (&ss, 8080, 2)) != 0)
    {
        ssPerror (n);
        exit (1);
    }

    for (;;)
    {
        if ((n = ssOpenService (&ss, &cc)) != 0)
        {
            ssPerror (n);
            exit (1);
        }

        done = 0;
        while (! done)
        {
            ssUpdateStatus (&cc, 4);
            switch (cc.status)
            {
              case ssConnected:
                {
                    printf ("hoHum...\n");
                    break;
                }
              case ssReadReady:
                {
                    int len = 600;
                    ssReadNl (&cc, buf, &len);
                    ssWriteString (&cc, buf);
                    break;
                }
              case ssTheyClosed:
                {
                    ssClose (&cc);
                    break;
                }
              case ssNoConnection:
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
    ssInit (argv[0], stderr);
    ssVerbose = 1;

    doEcho ();

    exit (0);
}
