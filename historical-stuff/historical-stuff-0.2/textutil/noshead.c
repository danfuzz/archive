/* ------------------------------------------------------------------------- */
/* noshead.c: Get rid of everything up to a #! line in stdin; after          */
/* that, echo in to out.                                                     */
/* ------------------------------------------------------------------------- */

#include <stdio.h>

#define BUFSIZE (32768)

char buf[BUFSIZE];

int main (int argc, char *argv[])
{
    int c;

    if (argc != 1)
    {
        fprintf (stderr, "usage: %s < in > out\n", argv[0]);
        exit (1);
    }

    do
    {
        fgets (buf, BUFSIZE, stdin);
    }
    while (buf[0] != '#' || buf[1] != '!');

    fputs (buf, stdout);
    
    while (! feof (stdin))
    {
        c = fread (buf, 1, BUFSIZE, stdin);
        if (ferror (stdin))
        {
            fprintf (stderr, "%s: error reading\n", argv[0]);
            exit (1);
        }
        fwrite (buf, 1, c, stdout);
        if (ferror (stdout))
        {
            fprintf (stderr, "%s: error writing\n", argv[0]);
            exit (1);
        }
    }

    exit (0);
}
