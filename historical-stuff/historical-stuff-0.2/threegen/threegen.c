/* ------------------------------------------------------------------------- */
/* ThreeGen.c                                                                */
/*                                                                           */
/* Dan Bornstein                                                             */
/*                                                                           */
/* Copyright 1993 All rights reserved.                                       */
/* ------------------------------------------------------------------------- */

#include <stdio.h>

char clist[] = "@#$%&234567890QWERTYUIOPASDFGHJKLZXCVBNM?qwertyuop"
               "asdfghklzxcvbnm";
#define clen (sizeof (clist) - 1)

#define maxWidth (300)

/* int patWidth = 15; */
int patWidth = 10;
int lineWidth = 78;

#define zeroOffset (64)



char randChar (void)
{
    static char last = 0;
    char c = 0;

    do
    {
        c = clist[random () % clen];
    }
    while (c == last);

    last = c;
    return (c);
}



void preprocessLine (char *line)
{
    int cur;
    int last = 0;

    while (*line)
    {
        cur = *line;
        if (isdigit (cur))
            cur -= '0';
        else
            cur = 0;
        
        *line = (last - cur) + zeroOffset + 1;
        /* for focus-past: *line = (cur - last) + zeroOffset + 1; */

        last = cur;
        line++;
    }
}



void oneLine (char *inLine, char *outLine)
{
    int inpat = 0;
    int i;

    preprocessLine (inLine);

    for (i = 0; i < patWidth; i++)
    {
        outLine[i] = randChar ();
    }

    for (; i < lineWidth; i++)
    {
        if (*inLine)
            inpat += (*(inLine++) - zeroOffset);
        else
            inpat++;
        outLine[i] = outLine[inpat];
    }

    outLine[i] = 0;
}



void manyLines (FILE *in, FILE *out)
{
    char inLine[maxWidth];
    char outLine[maxWidth];

    while (! feof (in))
    {
        inLine[0] = 0;
        fgets (inLine, maxWidth, in);
        if (inLine[0] == 0)
            break;
        oneLine (inLine, outLine);
        fputs (outLine, out);
        fputs ("\n", out);
    }
}



int main (int argc, char *argv[])
{
    srandom (time(0));

    manyLines (stdin, stdout);
}
           
    

