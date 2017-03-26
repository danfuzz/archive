%{
#include "pklParse.h"
#include "pklGram.tab.h"

static PklRef parseIntegerDec (char *s);
static PklRef parseIntegerHex (char *s);
static PklRef parseIntegerOct (char *s);
static PklRef parseIntegerBin (char *s);
static PklRef parseReal (char *s);
static PklRef parseString (char *s);
static PklRef parseIdent (char *s);

int lineNum = 1;
%}

IDENT     [A-Za-z_][A-Za-z0-9_]*
STRCH     [^\"\\\n]
STRESC    \\.
STRPART   ({STRCH}|{STRESC})*
QUICH     [^\'\\\n]
QUIPART   ({QUICH}|{STRESC})*

HEXDIGIT  [0-9a-fA-F]
DECDIGIT  [0-9]
INT       {DECDIGIT}+
OPTINT    {DECDIGIT}*
EXPPART   [Ee][+-]?{INT}
REAL1     {OPTINT}\.{INT}{EXPPART}?
REAL2     {INT}{EXPPART}
REAL3     {INT}\.
REALNUM   {REAL1}|{REAL2}|{REAL3}
INTNUM    [1-9]{OPTINT}
OCTNUM    0{OPTINT}
BINNUM    0b{INT}
HEXNUM    0x{HEXDIGIT}+

%x comment strng quident errrec

%%

[ \t\r\f]+  /* whitespace--no action */
"\n"        { lineNum++;              }
"#".*       /* comment--no action    */

"/*"                    { BEGIN (comment); }     /* Comment code taken */
<comment>[^*\n]+        /* no action */          /* from flexdoc       */
<comment>"*"+[^*/\n]*   /* no action */
<comment>\n             { lineNum++;              }
<comment>"*"+"/"        { BEGIN (INITIAL);        }
<comment><<EOF>>        { errRep ("Unmatched `/*'"); 
                          return (LEXERROREOF); }

\"                 { BEGIN (strng); }
<strng><<EOF>>     { errRep ("Unmatched double-quote (\")");
                     return (LEXERROREOF); }
<strng>\n          { lineNum++; yymore (); }
<strng>\\\n        { yymore (); lineNum++; }
<strng>{STRPART}   { yymore (); }
<strng>\"          { yylval = parseString (yytext);
                     BEGIN (INITIAL);
                     return (yylval == NULL ? LEXERROR : VALUE); }

\'                 { BEGIN (quident); }
<quident><<EOF>>   { errRep ("Unmatched single-quote (\')");
                     return (LEXERROREOF); }
<quident>\n        { lineNum++; yymore (); }
<quident>\\\n      { yymore (); lineNum++; }
<quident>{QUIPART} { yymore (); }
<quident>\'        { yylval = parseString (yytext);
                     if (yylval) yylval = valMakeSymbolFromString (yylval);
                     BEGIN (INITIAL);
                     return (yylval == NULL ? LEXERROR : IDENT); }

{HEXNUM}    { yylval = parseIntegerHex (yytext);
              return (yylval == NULL ? LEXERROR : VALUE); }

{BINNUM}    { yylval = parseIntegerBin (yytext);
              return (yylval == NULL ? LEXERROR : VALUE); }

{OCTNUM}    { yylval = parseIntegerOct (yytext);
              return (yylval == NULL ? LEXERROR : VALUE); }

{INTNUM}    { yylval = parseIntegerDec (yytext);
              return (yylval == NULL ? LEXERROR : VALUE); }

{REALNUM}   { yylval = parseReal (yytext);
              return (yylval == NULL ? LEXERROR : VALUE); }

"+"         { return (PLUS);       }
"-"         { return (MINUS);      }
"**"        { return (POWER);      }
"&&"        { return (BAND);       }
"||"        { return (BOR);        }
":="        { return (SET);        }
"+="        { yylval = crPlusSet;   return (XSETOP);  }
"-="        { yylval = crMinusSet;  return (XSETOP);  }
"*="        { yylval = crTimesSet;  return (XSETOP);  }
"/="        { yylval = crDivideSet; return (XSETOP);  }
"%="        { yylval = crModuloSet; return (XSETOP);  }
"&="        { yylval = crAndSet;    return (XSETOP);  }
"^="        { yylval = crXorSet;    return (XSETOP);  }
"|="        { yylval = crOrSet;     return (XSETOP);  }
"<<="       { yylval = crShlSet;    return (XSETOP);  }
">>="       { yylval = crShrSet;    return (XSETOP);  }
"!"         { yylval = crBnot;      return (UNARYOP); }
"~"         { yylval = crLnot;      return (UNARYOP); }
"*"         { yylval = crTimes;     return (MULOP);   }
"/"         { yylval = crDivide;    return (MULOP);   }
"%"         { yylval = crModulo;    return (MULOP);   }
"<<"        { yylval = crShl;       return (SHIFTOP); }
">>"        { yylval = crShr;       return (SHIFTOP); }
"<"         { yylval = crLe;        return (ORDOP);   }
"<="        { yylval = crLeq;       return (ORDOP);   }
">"         { yylval = crGr;        return (ORDOP);   }
">="        { yylval = crGreq;      return (ORDOP);   }
"=="        { yylval = crEq;        return (EQOP);    }
"!=="       { yylval = crNeq;       return (EQOP);    }
"="         { yylval = crEqual;     return (EQOP);    }
"!="        { yylval = crNequal;    return (EQOP);    }
"&"         { yylval = crLand;      return (LOGOP);   }
"^"         { yylval = crLxor;      return (LOGOP);   }
"|"         { yylval = crLor;       return (LOGOP);   }

"@"         { return (AT);         }
","         { return (COMMA);      }
":"         { return (COLON);      }
"("         { return (OPAREN);     }
")"         { return (CPAREN);     }
"["         { return (OSQUARE);    }
"]"         { return (CSQUARE);    }
"{"         { return (OCURL);      }
"}"         { return (CCURL);      }
";"         { return (TERMINATOR); }

"const"     { return (CONST);      }
"else"      { return (ELSE);       }
"global"    { return (GLOBAL);     }
"if"        { return (IF);         }
"let"       { return (LET);        }
"letfn"     { return (LETFN);      }
"then"      { return (THEN);       }

{IDENT}     { yylval = parseIdent (yytext);
              return (IDENT); }

.                       { BEGIN (errrec);         }
<errrec>[^ \n\t\r\f]+   /* no action             */
<errrec>\n              {
                          lineNum++;
                          BEGIN (INITIAL); 
                          return (LEXERROR);
                        }
<errrec>[ \n\t\r\f]     { 
                          BEGIN (INITIAL); 
                          return (LEXERROR);
                        }
<errrec><<EOF>>         { return (LEXERROREOF);   }

<<EOF>>                 { yyterminate (); }

%%

#include <math.h> /* for pow() */

static PklRef parseIntegerDec (char *s)
{
    long v = 0;                       /* running value    */
    long neg = ((*s != '-') * 2) - 1; /* are we negative? */

    if (*s == '-' || *s == '+') s++;

    while (*s) 
    {
        long nv = v * 10;
        if ((nv / 10) != v || nv < v)
            return (NULL);
        v = nv;
        if (*s >= '0' && *s <= '9')
            v += (*s - '0');
        else
            return (NULL);
        s++;
    }

    return (valMakeInteger (v * neg));
}



static PklRef parseIntegerHex (char *s)
{
    long v = 0;                       /* running value    */
    long neg = ((*s != '-') * 2) - 1; /* are we negative? */
    
    if (*s == '-' || *s == '+') s++;
    s += 2; /* over the 0x */
    
    while (*s) 
    {
        long nv = v << 4;
        if ((nv >> 4) != v || nv < v)
            return (NULL);
        v = nv;
        if (*s >= '0' && *s <= '9')
            v += (*s - '0');
        else if (*s >= 'a' && *s <= 'f')
            v += (*s - ('a' - 10));
        else if (*s >= 'A' && *s <= 'F')
            v += (*s - ('A' - 10));
        else
            return (NULL);
        s++;
    }
    
    return (valMakeInteger (v * neg));
}



static PklRef parseIntegerOct (char *s)
{
    long v = 0;                       /* running value    */
    long neg = ((*s != '-') * 2) - 1; /* are we negative? */

    if (*s == '-' || *s == '+') s++;
    s++; /* over the 0 */

    while (*s) 
    {
        long nv = v << 3;
        if ((nv >> 3) != v || nv < v)
            return (NULL);
        v = nv;
        if (*s >= '0' && *s <= '7')
            v += (*s - '0');
        else
            return (NULL);
        s++;
    }

    return (valMakeInteger (v * neg));
}



static PklRef parseIntegerBin (char *s)
{
    long v = 0;                       /* running value    */
    long neg = ((*s != '-') * 2) - 1; /* are we negative? */

    if (*s == '-' || *s == '+') s++;
    s += 2; /* over the 0b */
    
    while (*s) 
    {
        long nv = v << 1;
        if ((nv >> 1) != v || nv < v)
            return (NULL);
        v = nv;
        if (*s == '1')
            v++;
        else if (*s == '0')
            /* empty */;
        else
            return (NULL);
        s++;
    }
    
    return (valMakeInteger (v * neg));
}



static PklRef parseReal (char *s)
{
    double v = 0.0;                  /* running value    */
    int neg = ((*s != '-') * 2) - 1; /* are we negative? */

    if (*s == '-' || *s == '+') s++;

    /* left-hand side of decimal point */
    while (*s && *s != '.' && *s != 'e' && *s != 'E') 
    {
        v *= 10.0;
        if (*s >= '0' && *s <= '9')
            v += (*s - '0');
        else
            return (NULL);
        s++;
    }

    /* right hand side of decimal point */
    if (*s == '.') 
    {
        double mul = 1.0;
        s++;
        while (*s && *s != 'e' && *s != 'E') 
        {
            mul /= 10.0;
            if (*s >= '0' && *s <= '9')
                v += (*s - '0') * mul;
            else
                return (NULL);
            s++;
        }
    }

    /* exponent */
    if (*s == 'e' || *s == 'E') 
    {
        double ex = 0.0;
        double exn;
        s++;
        exn = ((*s != '-') * 2) - 1;
        if (*s == '-' || *s == '+') s++;
        while (*s) 
        {
            ex *= 10.0;
            if (*s >= '0' && *s <= '9')
                ex += (*s - '0');
            else
                return (NULL);
            s++;
        }
        v *= pow (10.0, ex * exn);
    }
    
    if (*s) 
        return (NULL);

    return (valMakeReal (v * neg));
}



static char x2c (char c)
{
    if      (c >= '0' && c <= '9') return (c - '0');
    else if (c >= 'a' && c <= 'f') return (c - 'a' + 10);
    else if (c >= 'A' && c <= 'F') return (c - 'A' + 10);
    else                           return (0);
}



static int mungeString (char *olds, char *news)
{
    int len = 0;

    while (*olds) 
    {
        if (*olds != '\\') 
        {
            *(news++) = *(olds++);
            len++;
        }
        else if (olds[1] >= '0' && olds[1] <= '7')
        {
            int v;
            olds++;
            v = *(olds++) - '0';
            while (*olds >= '0' && *olds <= '7')
                v = v * 8 + *(olds++) - '0';
            *(news++) = v & 0xff;
            len++;
        }
        else
        {
            olds++;
            switch (*(olds++)) 
            {
              case '\n': break; /* \n: ignore  */
              case '.':  break; /* dot: ignore */
              case '?':  *(news++) = '\x7f'; len++; break;
              case 'a':  *(news++) = '\a'; len++; break;
              case 'b':  *(news++) = '\b'; len++; break;
              case 'f':  *(news++) = '\f'; len++; break;
              case 'n':  *(news++) = '\n'; len++; break;
              case 'r':  *(news++) = '\r'; len++; break;
              case 't':  *(news++) = '\t'; len++; break;
              case 'v':  *(news++) = '\v'; len++; break;
              case 'x':
                {
                    char v = 0;
                    int odd = 0;
                    char *scan = olds;
                    while (isxdigit (*scan))
                    {
                        scan++;
                        odd++;
                    }
                    if (odd == 0) 
                        return (-1);
                    odd &= 1;
                    while (isxdigit (*olds)) 
                    {
                        v = (v << 4) + x2c (*(olds++));
                        odd ^= 1;
                        if (! odd) {
                            *(news++) = v;
                            len++;
                            v = 0;
                        }
                    }
                    break;
                }
              default: *(news++) = olds[-1]; len++; break;
            }
        }
    }

    len--;
    return (len);
}



static PklRef parseString (char *s)
{
    char *news = (char *) alloca (strlen (s) + 1);
    int len;

    len = mungeString (s, news);
    if (len < 0) return (NULL);

    return (valMakeString (1, news, len));
}



static PklRef parseIdent (char *s)
{
    return (valMakeSymbol (s, strlen (s)));
}
