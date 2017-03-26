#include "pkl.h"
#include "pklMacro.h"
#include "pklFn.h"

void pklInit (void)
{
    constInit ();
    parseInit ();
    macroInit ();
    fnInit ();
}

int yywrap (void) 
{
    return (0);
}


/* ------------------------------------------------------------------------- */
/* Temp main                                                                 */
/* ------------------------------------------------------------------------- */

main ()
{
    pklInit ();
    yyparse ();
}
