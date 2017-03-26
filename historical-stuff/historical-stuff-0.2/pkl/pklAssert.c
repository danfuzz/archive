#include "pklAssert.h"
#include <stdio.h>
#include <stdarg.h>



void assertionFailure (char *a, char *file, int line)
{
    fprintf (stderr, 
             "Assertion failed: file \"%s\", line %d\n    %s\n",
             file, line, a);
    exit (1);
}



void failSoft (char *msg, ...)
{
    /* BUG--should report exception */
    va_list args;
    
    va_start (args, msg);
    vfprintf (stderr, msg, args);
    fputs ("\n", stderr);
    va_end (args);

    exit (1);
}



void failHard (char *msg, ...)
{
    va_list args;
    
    va_start (args, msg);
    vfprintf (stderr, msg, args);
    fputs ("\n", stderr);
    va_end (args);
    exit (1);
}
