/* ------------------------------------------------------------------------- */
/* SourceMunge: just pass the words                                          */
/* ------------------------------------------------------------------------- */

%{
/* #include "Parse.tab.h" */
#include <ctype.h>

int inComment = 0;
int inQuote = 0;

void downcase (char *s)
{
    while (*s)
    {
        if (isupper (*s)) *s = tolower (*s);
	s++;
    }
}

%}

IDENT     [A-Za-z_][A-Za-z0-9_]*
NUMBEROID [\-+A-Za-z0-9._]+

%%

[ \t\r\f\n]+            /* whitespace--no action */

"/*"                    { if (! inQuote) inComment = 1; }
"*"+"/"                 { inComment = 0; inQuote = 0;   }
"\\\""                  /* backslashed quote--no action */
"\""                    { inQuote = ! inQuote; }

{IDENT}                 { 
                            downcase (yytext);
                            printf ("%s%s%s\n", yytext, 
				    inComment ? ";" : "",
				    inQuote ? "~" : "");
			}

{NUMBEROID}             /* no action */

.                       /* no action */

%%

