
/*  A Bison parser, made from pklGram.y with Bison version GNU Bison version 1.22
  */

#define YYBISON 1  /* Identify Bison output.  */

#define	LEXERROR	258
#define	LEXERROREOF	259
#define	IDENT	260
#define	VALUE	261
#define	CONST	262
#define	ELSE	263
#define	GLOBAL	264
#define	IF	265
#define	LET	266
#define	LETFN	267
#define	THEN	268
#define	TERMINATOR	269
#define	AVOID	270
#define	OPAREN	271
#define	CPAREN	272
#define	OCURL	273
#define	CCURL	274
#define	COLON	275
#define	COMMA	276
#define	SET	277
#define	XSETOP	278
#define	BOR	279
#define	BAND	280
#define	EQOP	281
#define	ORDOP	282
#define	LOGOP	283
#define	SHIFTOP	284
#define	MINUS	285
#define	PLUS	286
#define	MULOP	287
#define	POWER	288
#define	AT	289
#define	UNARYOP	290
#define	OSQUARE	291
#define	CSQUARE	292

#line 1 "pklGram.y"


/* 
bison --verbose --defines pklGram.y
flex -I pklScan.lex
*/

#include "pklParse.h"
#include <stdio.h>

int nerrors = 0;

#define yyerror(msg) (errRep(msg))


#ifndef YYLTYPE
typedef
  struct yyltype
    {
      int timestamp;
      int first_line;
      int first_column;
      int last_line;
      int last_column;
      char *text;
   }
  yyltype;

#define YYLTYPE yyltype
#endif

#ifndef YYSTYPE
#define YYSTYPE int
#endif
#include <stdio.h>

#ifndef __cplusplus
#ifndef __STDC__
#define const
#endif
#endif



#define	YYFINAL		119
#define	YYFLAG		-32768
#define	YYNTBASE	38

#define YYTRANSLATE(x) ((unsigned)(x) <= 292 ? yytranslate[x] : 63)

static const char yytranslate[] = {     0,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
     2,     2,     2,     2,     2,     1,     2,     3,     4,     5,
     6,     7,     8,     9,    10,    11,    12,    13,    14,    15,
    16,    17,    18,    19,    20,    21,    22,    23,    24,    25,
    26,    27,    28,    29,    30,    31,    32,    33,    34,    35,
    36,    37
};

#if YYDEBUG != 0
static const short yyprhs[] = {     0,
     0,     2,     3,     7,    10,    14,    16,    18,    23,    30,
    32,    34,    36,    40,    44,    46,    49,    52,    55,    59,
    63,    67,    71,    75,    79,    83,    87,    91,    95,    97,
   102,   104,   106,   109,   113,   115,   117,   119,   123,   126,
   129,   133,   136,   140,   142,   146,   150,   151,   154,   157,
   162,   165,   168,   171,   174,   177,   180,   182,   186,   191,
   194,   198,   199,   201,   205,   209,   211
};

static const short yyrhs[] = {    39,
     0,     0,    39,    56,    14,     0,    39,    54,     0,    39,
     1,     4,     0,    41,     0,    42,     0,    10,    40,    13,
    40,     0,    10,    40,    13,    40,     8,    40,     0,    44,
     0,    45,     0,    43,     0,    46,    22,    42,     0,    46,
    23,    42,     0,    47,     0,    35,    44,     0,    31,    44,
     0,    30,    44,     0,    42,    33,    42,     0,    42,    32,
    42,     0,    42,    31,    42,     0,    42,    30,    42,     0,
    42,    29,    42,     0,    42,    27,    42,     0,    42,    26,
    42,     0,    42,    28,    42,     0,    42,    25,    42,     0,
    42,    24,    42,     0,    62,     0,    47,    36,    40,    37,
     0,     6,     0,    49,     0,    34,    62,     0,    16,    40,
    17,     0,    52,     0,    48,     0,    46,     0,    47,    50,
    52,     0,    47,    50,     0,    36,    37,     0,    36,    51,
    37,     0,    16,    17,     0,    16,    51,    17,     0,    40,
     0,    51,    21,    40,     0,    18,    53,    19,     0,     0,
    53,    54,     0,    40,    14,     0,    62,    20,    40,    14,
     0,    55,    14,     0,     1,    14,     0,    11,    57,     0,
    12,    60,     0,     9,    57,     0,     7,    57,     0,    58,
     0,    57,    21,    58,     0,    62,    59,    22,    40,     0,
    62,    59,     0,    16,    40,    17,     0,     0,    61,     0,
    60,    21,    61,     0,    62,    50,    52,     0,     5,     0,
    62,     5,     0
};

#endif

#if YYDEBUG != 0
static const short yyrline[] = { 0,
    40,    51,    54,    57,    60,    66,    69,    75,    80,    86,
    89,    92,    98,   101,   106,   109,   112,   115,   121,   124,
   127,   130,   133,   136,   139,   142,   145,   148,   154,   157,
   163,   166,   169,   172,   175,   178,   181,   187,   190,   196,
   199,   205,   208,   214,   217,   223,   229,   232,   238,   241,
   244,   247,   253,   256,   262,   265,   271,   274,   280,   283,
   289,   292,   298,   301,   307,   313,   316
};

static const char * const yytname[] = {   "$","error","$illegal.","LEXERROR",
"LEXERROREOF","IDENT","VALUE","CONST","ELSE","GLOBAL","IF","LET","LETFN","THEN",
"TERMINATOR","AVOID","OPAREN","CPAREN","OCURL","CCURL","COLON","COMMA","SET",
"XSETOP","BOR","BAND","EQOP","ORDOP","LOGOP","SHIFTOP","MINUS","PLUS","MULOP",
"POWER","AT","UNARYOP","OSQUARE","CSQUARE","entirety","program","expression",
"if_expression","op_expression","set_expression","un_expression","bin_expression",
"var","rval","call_rval","list_literal","argument_list","expressions","block_literal",
"statements","statement","let_statement","global_decl","decl_list","declaration",
"opt_restriction","funcdef_list","funcdef","ident",""
};
#endif

static const short yyr1[] = {     0,
    38,    39,    39,    39,    39,    40,    40,    41,    41,    42,
    42,    42,    43,    43,    44,    44,    44,    44,    45,    45,
    45,    45,    45,    45,    45,    45,    45,    45,    46,    46,
    47,    47,    47,    47,    47,    47,    47,    48,    48,    49,
    49,    50,    50,    51,    51,    52,    53,    53,    54,    54,
    54,    54,    55,    55,    56,    56,    57,    57,    58,    58,
    59,    59,    60,    60,    61,    62,    62
};

static const short yyr2[] = {     0,
     1,     0,     3,     2,     3,     1,     1,     4,     6,     1,
     1,     1,     3,     3,     1,     2,     2,     2,     3,     3,
     3,     3,     3,     3,     3,     3,     3,     3,     1,     4,
     1,     1,     2,     3,     1,     1,     1,     3,     2,     2,
     3,     2,     3,     1,     3,     3,     0,     2,     2,     4,
     2,     2,     2,     2,     2,     2,     1,     3,     4,     2,
     3,     0,     1,     3,     3,     1,     2
};

static const short yydefact[] = {     2,
     0,     0,    66,    31,     0,     0,     0,     0,     0,     0,
    47,     0,     0,     0,     0,     0,     0,     6,     7,    12,
    10,    11,    37,    15,    36,    32,    35,     4,     0,     0,
    29,     5,    52,    56,    57,    62,    55,     0,    29,    53,
    54,    63,     0,     0,     0,    18,    37,    17,    33,    16,
    40,    44,     0,    49,     0,     0,     0,     0,     0,     0,
     0,     0,     0,     0,     0,     0,     0,     0,    39,    51,
     3,    67,     0,     0,     0,    60,     0,     0,     0,    34,
     0,    46,    48,     0,    41,    28,    27,    25,    24,    26,
    23,    22,    21,    20,    19,    13,    14,    42,     0,     0,
    38,     0,    58,     0,     0,     8,    64,    65,    45,    43,
    30,    50,    61,    59,     0,     9,     0,     0,     0
};

static const short yydefgoto[] = {   117,
     1,    17,    18,    19,    20,    21,    22,    23,    24,    25,
    26,    69,    53,    27,    45,    28,    29,    30,    34,    35,
    76,    41,    42,    39
};

static const short yypact[] = {-32768,
    17,    -2,-32768,-32768,    27,    27,   133,    27,    27,   133,
-32768,   140,   140,    27,   140,    97,    35,-32768,   165,-32768,
-32768,-32768,    50,   -15,-32768,-32768,-32768,-32768,    51,    55,
    10,-32768,-32768,    53,-32768,     0,    53,    21,    73,    53,
    78,-32768,    20,    92,    70,-32768,-32768,-32768,    73,-32768,
-32768,-32768,   -17,-32768,   140,   140,   140,   140,   140,   140,
   140,   140,   140,   140,   140,   140,   106,   133,    96,-32768,
-32768,-32768,   133,    27,   133,    88,   133,    27,    96,-32768,
   107,-32768,-32768,   133,-32768,   174,    64,   181,    26,    13,
     7,   -25,   -25,    86,-32768,   165,   165,-32768,    46,    89,
-32768,   111,-32768,   112,   133,   127,-32768,-32768,-32768,-32768,
-32768,-32768,-32768,-32768,   133,-32768,   130,   144,-32768
};

static const short yypgoto[] = {-32768,
-32768,    -7,-32768,   122,-32768,    72,-32768,   105,-32768,-32768,
-32768,   104,    81,   -38,-32768,   108,-32768,-32768,    56,    76,
-32768,-32768,    74,     5
};


#define	YYLAST		214


static const short yytable[] = {    38,
    67,    32,    44,    84,    72,    31,    63,    64,    52,    36,
    36,    33,    36,    43,    72,    75,    -1,     2,    49,    85,
    68,     3,     4,     5,    72,     6,     7,     8,     9,    73,
   101,     3,    10,    77,    11,    67,    61,    62,    63,    64,
   108,    60,    61,    62,    63,    64,    12,    13,    54,    31,
    14,    15,    16,    59,    60,    61,    62,    63,    64,    52,
   100,    37,   110,    40,    70,   102,    84,   104,    71,   106,
    81,    65,    66,    74,     3,     4,   109,    72,    36,     7,
     8,     9,    43,    46,    48,    10,    50,    11,    82,    57,
    58,    59,    60,    61,    62,    63,    64,   114,    78,    12,
    13,     3,     4,    14,    15,    16,     7,   116,    80,   105,
     3,     4,    10,    11,    11,     7,    47,    47,    64,    47,
    33,    10,    98,    11,   112,   111,    12,    13,   113,   118,
    14,    15,    16,    51,   115,    12,    13,     3,     4,    14,
    15,    16,     7,   119,     3,     4,    79,    99,    10,   103,
    11,   107,    83,     0,     0,    10,     0,    11,     0,     0,
     0,     0,    12,    13,     0,     0,    14,    15,    16,    12,
    13,     0,     0,    14,    15,    16,    86,    87,    88,    89,
    90,    91,    92,    93,    94,    95,    96,    97,    55,    56,
    57,    58,    59,    60,    61,    62,    63,    64,    56,    57,
    58,    59,    60,    61,    62,    63,    64,    58,    59,    60,
    61,    62,    63,    64
};

static const short yycheck[] = {     7,
    16,     4,    10,    21,     5,     1,    32,    33,    16,     5,
     6,    14,     8,     9,     5,    16,     0,     1,    14,    37,
    36,     5,     6,     7,     5,     9,    10,    11,    12,    20,
    69,     5,    16,    13,    18,    16,    30,    31,    32,    33,
    79,    29,    30,    31,    32,    33,    30,    31,    14,    45,
    34,    35,    36,    28,    29,    30,    31,    32,    33,    67,
    68,     6,    17,     8,    14,    73,    21,    75,    14,    77,
     1,    22,    23,    21,     5,     6,    84,     5,    74,    10,
    11,    12,    78,    12,    13,    16,    15,    18,    19,    26,
    27,    28,    29,    30,    31,    32,    33,   105,    21,    30,
    31,     5,     6,    34,    35,    36,    10,   115,    17,    22,
     5,     6,    16,    18,    18,    10,    12,    13,    33,    15,
    14,    16,    17,    18,    14,    37,    30,    31,    17,     0,
    34,    35,    36,    37,     8,    30,    31,     5,     6,    34,
    35,    36,    10,     0,     5,     6,    43,    67,    16,    74,
    18,    78,    45,    -1,    -1,    16,    -1,    18,    -1,    -1,
    -1,    -1,    30,    31,    -1,    -1,    34,    35,    36,    30,
    31,    -1,    -1,    34,    35,    36,    55,    56,    57,    58,
    59,    60,    61,    62,    63,    64,    65,    66,    24,    25,
    26,    27,    28,    29,    30,    31,    32,    33,    25,    26,
    27,    28,    29,    30,    31,    32,    33,    27,    28,    29,
    30,    31,    32,    33
};
/* -*-C-*-  Note some compilers choke on comments on `#line' lines.  */
#line 3 "/usr/ce/lib/bison.simple"

/* Skeleton output parser for bison,
   Copyright (C) 1984, 1989, 1990 Bob Corbett and Richard Stallman

   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 1, or (at your option)
   any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  */


#ifndef alloca
#ifdef __GNUC__
#define alloca __builtin_alloca
#else /* not GNU C.  */
#if (!defined (__STDC__) && defined (sparc)) || defined (__sparc__) || defined (__sparc) || defined (__sgi)
#include <alloca.h>
#else /* not sparc */
#if defined (MSDOS) && !defined (__TURBOC__)
#include <malloc.h>
#else /* not MSDOS, or __TURBOC__ */
#if defined(_AIX)
#include <malloc.h>
 #pragma alloca
#else /* not MSDOS, __TURBOC__, or _AIX */
#ifdef __hpux
#ifdef __cplusplus
extern "C" {
void *alloca (unsigned int);
};
#else /* not __cplusplus */
void *alloca ();
#endif /* not __cplusplus */
#endif /* __hpux */
#endif /* not _AIX */
#endif /* not MSDOS, or __TURBOC__ */
#endif /* not sparc.  */
#endif /* not GNU C.  */
#endif /* alloca not defined.  */

/* This is the parser code that is written into each bison parser
  when the %semantic_parser declaration is not specified in the grammar.
  It was written by Richard Stallman by simplifying the hairy parser
  used when %semantic_parser is specified.  */

/* Note: there must be only one dollar sign in this file.
   It is replaced by the list of actions, each action
   as one case of the switch.  */

#define yyerrok		(yyerrstatus = 0)
#define yyclearin	(yychar = YYEMPTY)
#define YYEMPTY		-2
#define YYEOF		0
#define YYACCEPT	return(0)
#define YYABORT 	return(1)
#define YYERROR		goto yyerrlab1
/* Like YYERROR except do call yyerror.
   This remains here temporarily to ease the
   transition to the new meaning of YYERROR, for GCC.
   Once GCC version 2 has supplanted version 1, this can go.  */
#define YYFAIL		goto yyerrlab
#define YYRECOVERING()  (!!yyerrstatus)
#define YYBACKUP(token, value) \
do								\
  if (yychar == YYEMPTY && yylen == 1)				\
    { yychar = (token), yylval = (value);			\
      yychar1 = YYTRANSLATE (yychar);				\
      YYPOPSTACK;						\
      goto yybackup;						\
    }								\
  else								\
    { yyerror ("syntax error: cannot back up"); YYERROR; }	\
while (0)

#define YYTERROR	1
#define YYERRCODE	256

#ifndef YYPURE
#define YYLEX		yylex()
#endif

#ifdef YYPURE
#ifdef YYLSP_NEEDED
#define YYLEX		yylex(&yylval, &yylloc)
#else
#define YYLEX		yylex(&yylval)
#endif
#endif

/* If nonreentrant, generate the variables here */

#ifndef YYPURE

int	yychar;			/*  the lookahead symbol		*/
YYSTYPE	yylval;			/*  the semantic value of the		*/
				/*  lookahead symbol			*/

#ifdef YYLSP_NEEDED
YYLTYPE yylloc;			/*  location data for the lookahead	*/
				/*  symbol				*/
#endif

int yynerrs;			/*  number of parse errors so far       */
#endif  /* not YYPURE */

#if YYDEBUG != 0
int yydebug;			/*  nonzero means print parse trace	*/
/* Since this is uninitialized, it does not stop multiple parsers
   from coexisting.  */
#endif

/*  YYINITDEPTH indicates the initial size of the parser's stacks	*/

#ifndef	YYINITDEPTH
#define YYINITDEPTH 200
#endif

/*  YYMAXDEPTH is the maximum size the stacks can grow to
    (effective only if the built-in stack extension method is used).  */

#if YYMAXDEPTH == 0
#undef YYMAXDEPTH
#endif

#ifndef YYMAXDEPTH
#define YYMAXDEPTH 10000
#endif

/* Prevent warning if -Wstrict-prototypes.  */
#ifdef __GNUC__
int yyparse (void);
#endif

#if __GNUC__ > 1		/* GNU C and GNU C++ define this.  */
#define __yy_bcopy(FROM,TO,COUNT)	__builtin_memcpy(TO,FROM,COUNT)
#else				/* not GNU C or C++ */
#ifndef __cplusplus

/* This is the most reliable way to avoid incompatibilities
   in available built-in functions on various systems.  */
static void
__yy_bcopy (from, to, count)
     char *from;
     char *to;
     int count;
{
  register char *f = from;
  register char *t = to;
  register int i = count;

  while (i-- > 0)
    *t++ = *f++;
}

#else /* __cplusplus */

/* This is the most reliable way to avoid incompatibilities
   in available built-in functions on various systems.  */
static void
__yy_bcopy (char *from, char *to, int count)
{
  register char *f = from;
  register char *t = to;
  register int i = count;

  while (i-- > 0)
    *t++ = *f++;
}

#endif
#endif

#line 184 "/usr/ce/lib/bison.simple"
int
yyparse()
{
  register int yystate;
  register int yyn;
  register short *yyssp;
  register YYSTYPE *yyvsp;
  int yyerrstatus;	/*  number of tokens to shift before error messages enabled */
  int yychar1 = 0;		/*  lookahead token as an internal (translated) token number */

  short	yyssa[YYINITDEPTH];	/*  the state stack			*/
  YYSTYPE yyvsa[YYINITDEPTH];	/*  the semantic value stack		*/

  short *yyss = yyssa;		/*  refer to the stacks thru separate pointers */
  YYSTYPE *yyvs = yyvsa;	/*  to allow yyoverflow to reallocate them elsewhere */

#ifdef YYLSP_NEEDED
  YYLTYPE yylsa[YYINITDEPTH];	/*  the location stack			*/
  YYLTYPE *yyls = yylsa;
  YYLTYPE *yylsp;

#define YYPOPSTACK   (yyvsp--, yyssp--, yylsp--)
#else
#define YYPOPSTACK   (yyvsp--, yyssp--)
#endif

  int yystacksize = YYINITDEPTH;

#ifdef YYPURE
  int yychar;
  YYSTYPE yylval;
  int yynerrs;
#ifdef YYLSP_NEEDED
  YYLTYPE yylloc;
#endif
#endif

  YYSTYPE yyval;		/*  the variable used to return		*/
				/*  semantic values from the action	*/
				/*  routines				*/

  int yylen;

#if YYDEBUG != 0
  if (yydebug)
    fprintf(stderr, "Starting parse\n");
#endif

  yystate = 0;
  yyerrstatus = 0;
  yynerrs = 0;
  yychar = YYEMPTY;		/* Cause a token to be read.  */

  /* Initialize stack pointers.
     Waste one element of value and location stack
     so that they stay on the same level as the state stack.
     The wasted elements are never initialized.  */

  yyssp = yyss - 1;
  yyvsp = yyvs;
#ifdef YYLSP_NEEDED
  yylsp = yyls;
#endif

/* Push a new state, which is found in  yystate  .  */
/* In all cases, when you get here, the value and location stacks
   have just been pushed. so pushing a state here evens the stacks.  */
yynewstate:

  *++yyssp = yystate;

  if (yyssp >= yyss + yystacksize - 1)
    {
      /* Give user a chance to reallocate the stack */
      /* Use copies of these so that the &'s don't force the real ones into memory. */
      YYSTYPE *yyvs1 = yyvs;
      short *yyss1 = yyss;
#ifdef YYLSP_NEEDED
      YYLTYPE *yyls1 = yyls;
#endif

      /* Get the current used size of the three stacks, in elements.  */
      int size = yyssp - yyss + 1;

#ifdef yyoverflow
      /* Each stack pointer address is followed by the size of
	 the data in use in that stack, in bytes.  */
#ifdef YYLSP_NEEDED
      /* This used to be a conditional around just the two extra args,
	 but that might be undefined if yyoverflow is a macro.  */
      yyoverflow("parser stack overflow",
		 &yyss1, size * sizeof (*yyssp),
		 &yyvs1, size * sizeof (*yyvsp),
		 &yyls1, size * sizeof (*yylsp),
		 &yystacksize);
#else
      yyoverflow("parser stack overflow",
		 &yyss1, size * sizeof (*yyssp),
		 &yyvs1, size * sizeof (*yyvsp),
		 &yystacksize);
#endif

      yyss = yyss1; yyvs = yyvs1;
#ifdef YYLSP_NEEDED
      yyls = yyls1;
#endif
#else /* no yyoverflow */
      /* Extend the stack our own way.  */
      if (yystacksize >= YYMAXDEPTH)
	{
	  yyerror("parser stack overflow");
	  return 2;
	}
      yystacksize *= 2;
      if (yystacksize > YYMAXDEPTH)
	yystacksize = YYMAXDEPTH;
      yyss = (short *) alloca (yystacksize * sizeof (*yyssp));
      __yy_bcopy ((char *)yyss1, (char *)yyss, size * sizeof (*yyssp));
      yyvs = (YYSTYPE *) alloca (yystacksize * sizeof (*yyvsp));
      __yy_bcopy ((char *)yyvs1, (char *)yyvs, size * sizeof (*yyvsp));
#ifdef YYLSP_NEEDED
      yyls = (YYLTYPE *) alloca (yystacksize * sizeof (*yylsp));
      __yy_bcopy ((char *)yyls1, (char *)yyls, size * sizeof (*yylsp));
#endif
#endif /* no yyoverflow */

      yyssp = yyss + size - 1;
      yyvsp = yyvs + size - 1;
#ifdef YYLSP_NEEDED
      yylsp = yyls + size - 1;
#endif

#if YYDEBUG != 0
      if (yydebug)
	fprintf(stderr, "Stack size increased to %d\n", yystacksize);
#endif

      if (yyssp >= yyss + yystacksize - 1)
	YYABORT;
    }

#if YYDEBUG != 0
  if (yydebug)
    fprintf(stderr, "Entering state %d\n", yystate);
#endif

  goto yybackup;
 yybackup:

/* Do appropriate processing given the current state.  */
/* Read a lookahead token if we need one and don't already have one.  */
/* yyresume: */

  /* First try to decide what to do without reference to lookahead token.  */

  yyn = yypact[yystate];
  if (yyn == YYFLAG)
    goto yydefault;

  /* Not known => get a lookahead token if don't already have one.  */

  /* yychar is either YYEMPTY or YYEOF
     or a valid token in external form.  */

  if (yychar == YYEMPTY)
    {
#if YYDEBUG != 0
      if (yydebug)
	fprintf(stderr, "Reading a token: ");
#endif
      yychar = YYLEX;
    }

  /* Convert token to internal form (in yychar1) for indexing tables with */

  if (yychar <= 0)		/* This means end of input. */
    {
      yychar1 = 0;
      yychar = YYEOF;		/* Don't call YYLEX any more */

#if YYDEBUG != 0
      if (yydebug)
	fprintf(stderr, "Now at end of input.\n");
#endif
    }
  else
    {
      yychar1 = YYTRANSLATE(yychar);

#if YYDEBUG != 0
      if (yydebug)
	{
	  fprintf (stderr, "Next token is %d (%s", yychar, yytname[yychar1]);
	  /* Give the individual parser a way to print the precise meaning
	     of a token, for further debugging info.  */
#ifdef YYPRINT
	  YYPRINT (stderr, yychar, yylval);
#endif
	  fprintf (stderr, ")\n");
	}
#endif
    }

  yyn += yychar1;
  if (yyn < 0 || yyn > YYLAST || yycheck[yyn] != yychar1)
    goto yydefault;

  yyn = yytable[yyn];

  /* yyn is what to do for this token type in this state.
     Negative => reduce, -yyn is rule number.
     Positive => shift, yyn is new state.
       New state is final state => don't bother to shift,
       just return success.
     0, or most negative number => error.  */

  if (yyn < 0)
    {
      if (yyn == YYFLAG)
	goto yyerrlab;
      yyn = -yyn;
      goto yyreduce;
    }
  else if (yyn == 0)
    goto yyerrlab;

  if (yyn == YYFINAL)
    YYACCEPT;

  /* Shift the lookahead token.  */

#if YYDEBUG != 0
  if (yydebug)
    fprintf(stderr, "Shifting token %d (%s), ", yychar, yytname[yychar1]);
#endif

  /* Discard the token being shifted unless it is eof.  */
  if (yychar != YYEOF)
    yychar = YYEMPTY;

  *++yyvsp = yylval;
#ifdef YYLSP_NEEDED
  *++yylsp = yylloc;
#endif

  /* count tokens shifted since error; after three, turn off error status.  */
  if (yyerrstatus) yyerrstatus--;

  yystate = yyn;
  goto yynewstate;

/* Do the default action for the current state.  */
yydefault:

  yyn = yydefact[yystate];
  if (yyn == 0)
    goto yyerrlab;

/* Do a reduction.  yyn is the number of a rule to reduce with.  */
yyreduce:
  yylen = yyr2[yyn];
  if (yylen > 0)
    yyval = yyvsp[1-yylen]; /* implement default value of the action */

#if YYDEBUG != 0
  if (yydebug)
    {
      int i;

      fprintf (stderr, "Reducing via rule %d (line %d), ",
	       yyn, yyrline[yyn]);

      /* Print the symbols being reduced, and their result.  */
      for (i = yyprhs[yyn]; yyrhs[i] > 0; i++)
	fprintf (stderr, "%s ", yytname[yyrhs[i]]);
      fprintf (stderr, " -> %s\n", yytname[yyr1[yyn]]);
    }
#endif


  switch (yyn) {

case 1:
#line 41 "pklGram.y"
{ 
                     if (nerrors != 0)
                         fprintf (stderr, "Total errors: %d\n", nerrors); 
                     else
                         nodeExecute (yyvsp[0]);
                 ;
    break;}
case 2:
#line 52 "pklGram.y"
{ yyval = constNil; ;
    break;}
case 3:
#line 55 "pklGram.y"
{ yyval = nodeProcessTree (yyvsp[-2], yyvsp[-1]); ;
    break;}
case 4:
#line 58 "pklGram.y"
{ yyval = nodeProcessTree (yyvsp[-1], yyvsp[0]); ;
    break;}
case 5:
#line 61 "pklGram.y"
{ return (1); ;
    break;}
case 6:
#line 67 "pklGram.y"
{ yyval = yyvsp[0]; ;
    break;}
case 7:
#line 70 "pklGram.y"
{ yyval = yyvsp[0]; ;
    break;}
case 8:
#line 76 "pklGram.y"
{ yyval = valMakeList (crIf, yyvsp[-2], yyvsp[0], 
                                     valMakeList (crEmptyExpression, NULL),
                                     NULL); ;
    break;}
case 9:
#line 81 "pklGram.y"
{ yyval = valMakeList (crIf, yyvsp[-4], yyvsp[-2], yyvsp[0], NULL); ;
    break;}
case 10:
#line 87 "pklGram.y"
{ yyval = yyvsp[0]; ;
    break;}
case 11:
#line 90 "pklGram.y"
{ yyval = yyvsp[0]; ;
    break;}
case 12:
#line 93 "pklGram.y"
{ yyval = yyvsp[0]; ;
    break;}
case 13:
#line 99 "pklGram.y"
{ yyval = valMakeList (crSetf, yyvsp[-2], yyvsp[0], NULL); ;
    break;}
case 14:
#line 102 "pklGram.y"
{ yyval = valMakeList (yyvsp[-1], yyvsp[-2], yyvsp[0], NULL); ;
    break;}
case 15:
#line 107 "pklGram.y"
{ yyval = yyvsp[0]; ;
    break;}
case 16:
#line 110 "pklGram.y"
{ yyval = valMakeList (yyvsp[-1], yyvsp[0], NULL); ;
    break;}
case 17:
#line 113 "pklGram.y"
{ yyval = valMakeList (crUnaryPlus, yyvsp[0], NULL); ;
    break;}
case 18:
#line 116 "pklGram.y"
{ yyval = valMakeList (crUnaryMinus, yyvsp[0], NULL); ;
    break;}
case 19:
#line 122 "pklGram.y"
{ yyval = valMakeList (crPower, yyvsp[-2], yyvsp[0], NULL); ;
    break;}
case 20:
#line 125 "pklGram.y"
{ yyval = valMakeList (yyvsp[-1], yyvsp[-2], yyvsp[0], NULL); ;
    break;}
case 21:
#line 128 "pklGram.y"
{ yyval = valMakeList (crPlus, yyvsp[-2], yyvsp[0], NULL); ;
    break;}
case 22:
#line 131 "pklGram.y"
{ yyval = valMakeList (crMinus, yyvsp[-2], yyvsp[0], NULL); ;
    break;}
case 23:
#line 134 "pklGram.y"
{ yyval = valMakeList (yyvsp[-1], yyvsp[-2], yyvsp[0], NULL); ;
    break;}
case 24:
#line 137 "pklGram.y"
{ yyval = valMakeList (yyvsp[-1], yyvsp[-2], yyvsp[0], NULL); ;
    break;}
case 25:
#line 140 "pklGram.y"
{ yyval = valMakeList (yyvsp[-1], yyvsp[-2], yyvsp[0], NULL); ;
    break;}
case 26:
#line 143 "pklGram.y"
{ yyval = valMakeList (yyvsp[-1], yyvsp[-2], yyvsp[0], NULL); ;
    break;}
case 27:
#line 146 "pklGram.y"
{ yyval = valMakeList (crBand, yyvsp[-2], yyvsp[0], NULL); ;
    break;}
case 28:
#line 149 "pklGram.y"
{ yyval = valMakeList (crBor, yyvsp[-2], yyvsp[0], NULL); ;
    break;}
case 29:
#line 155 "pklGram.y"
{ yyval = yyvsp[0]; ;
    break;}
case 30:
#line 158 "pklGram.y"
{ yyval = valMakeList (crAref, yyvsp[-3], yyvsp[-1], NULL); ;
    break;}
case 31:
#line 164 "pklGram.y"
{ yyval = yyvsp[0]; ;
    break;}
case 32:
#line 167 "pklGram.y"
{ yyval = yyvsp[0]; ;
    break;}
case 33:
#line 170 "pklGram.y"
{ yyval = valMakeList (crQuote, yyvsp[0], NULL); ;
    break;}
case 34:
#line 173 "pklGram.y"
{ yyval = yyvsp[-1]; ;
    break;}
case 35:
#line 176 "pklGram.y"
{ yyval = yyvsp[0]; ;
    break;}
case 36:
#line 179 "pklGram.y"
{ yyval = yyvsp[0]; ;
    break;}
case 37:
#line 182 "pklGram.y"
{ yyval = yyvsp[0]; ;
    break;}
case 38:
#line 188 "pklGram.y"
{ yyval = valPrependList (valAppendList (yyvsp[-1], yyvsp[0]), yyvsp[-2]); ;
    break;}
case 39:
#line 191 "pklGram.y"
{ yyval = valPrependList (yyvsp[0], yyvsp[-1]); ;
    break;}
case 40:
#line 197 "pklGram.y"
{ yyval = crNil; ;
    break;}
case 41:
#line 200 "pklGram.y"
{ yyval = valPrependList (yyvsp[-1], crList); ;
    break;}
case 42:
#line 206 "pklGram.y"
{ yyval = constNil; ;
    break;}
case 43:
#line 209 "pklGram.y"
{ yyval = yyvsp[-1]; ;
    break;}
case 44:
#line 215 "pklGram.y"
{ yyval = valMakeList (yyvsp[0], NULL); ;
    break;}
case 45:
#line 218 "pklGram.y"
{ yyval = valAppendList (yyvsp[-2], yyvsp[0]); ;
    break;}
case 46:
#line 224 "pklGram.y"
{ yyval = valPrependList (yyvsp[-1], crExpressions); ;
    break;}
case 47:
#line 230 "pklGram.y"
{ yyval = constNil; ;
    break;}
case 48:
#line 233 "pklGram.y"
{ yyval = valAppendList (yyvsp[-1], yyvsp[0]); ;
    break;}
case 49:
#line 239 "pklGram.y"
{ yyval = yyvsp[-1]; ;
    break;}
case 50:
#line 242 "pklGram.y"
{ yyval = valMakeList (crLabel, yyvsp[-3], yyvsp[-1], NULL); ;
    break;}
case 51:
#line 245 "pklGram.y"
{ yyval = yyvsp[-1]; ;
    break;}
case 52:
#line 248 "pklGram.y"
{ yyerrok; yyval = valMakeList (crEmptyExpression, NULL); ;
    break;}
case 53:
#line 254 "pklGram.y"
{ yyval = valMakeList (crLet, yyvsp[0], NULL); ;
    break;}
case 54:
#line 257 "pklGram.y"
{ yyval = valMakeList (crLetfn, yyvsp[0], NULL); ;
    break;}
case 55:
#line 263 "pklGram.y"
{ yyval = valMakeList (crDeclareGlobal, yyvsp[0], NULL); ;
    break;}
case 56:
#line 266 "pklGram.y"
{ yyval = valMakeList (crDeclareConstant, yyvsp[0], NULL); ;
    break;}
case 57:
#line 272 "pklGram.y"
{ yyval = valMakeList (yyvsp[0], NULL); ;
    break;}
case 58:
#line 275 "pklGram.y"
{ yyval = valAppendList (yyvsp[-2], yyvsp[0]); ;
    break;}
case 59:
#line 281 "pklGram.y"
{ yyval = valMakeList (yyvsp[-3], yyvsp[-2], yyvsp[0], NULL); ;
    break;}
case 60:
#line 284 "pklGram.y"
{ yyval = valMakeList (yyvsp[-1], yyvsp[0], crInvalid, NULL); ;
    break;}
case 61:
#line 290 "pklGram.y"
{ yyval = yyvsp[-1]; ;
    break;}
case 62:
#line 293 "pklGram.y"
{ yyval = valMakeList (crNoRestriction, NULL); ;
    break;}
case 63:
#line 299 "pklGram.y"
{ yyval = valMakeList (yyvsp[0], NULL); ;
    break;}
case 64:
#line 302 "pklGram.y"
{ yyval = valAppendList (yyvsp[-2], yyvsp[0]); ;
    break;}
case 65:
#line 308 "pklGram.y"
{ yyval = valMakeList (yyvsp[-2], yyvsp[-1], yyvsp[0], NULL); ;
    break;}
case 66:
#line 314 "pklGram.y"
{ yyval = yyvsp[0]; ;
    break;}
case 67:
#line 317 "pklGram.y"
{ yyval = valMakeCompoundSymbol (yyvsp[-1], "-", yyvsp[0]); ;
    break;}
}
   /* the action file gets copied in in place of this dollarsign */
#line 465 "/usr/ce/lib/bison.simple"

  yyvsp -= yylen;
  yyssp -= yylen;
#ifdef YYLSP_NEEDED
  yylsp -= yylen;
#endif

#if YYDEBUG != 0
  if (yydebug)
    {
      short *ssp1 = yyss - 1;
      fprintf (stderr, "state stack now");
      while (ssp1 != yyssp)
	fprintf (stderr, " %d", *++ssp1);
      fprintf (stderr, "\n");
    }
#endif

  *++yyvsp = yyval;

#ifdef YYLSP_NEEDED
  yylsp++;
  if (yylen == 0)
    {
      yylsp->first_line = yylloc.first_line;
      yylsp->first_column = yylloc.first_column;
      yylsp->last_line = (yylsp-1)->last_line;
      yylsp->last_column = (yylsp-1)->last_column;
      yylsp->text = 0;
    }
  else
    {
      yylsp->last_line = (yylsp+yylen-1)->last_line;
      yylsp->last_column = (yylsp+yylen-1)->last_column;
    }
#endif

  /* Now "shift" the result of the reduction.
     Determine what state that goes to,
     based on the state we popped back to
     and the rule number reduced by.  */

  yyn = yyr1[yyn];

  yystate = yypgoto[yyn - YYNTBASE] + *yyssp;
  if (yystate >= 0 && yystate <= YYLAST && yycheck[yystate] == *yyssp)
    yystate = yytable[yystate];
  else
    yystate = yydefgoto[yyn - YYNTBASE];

  goto yynewstate;

yyerrlab:   /* here on detecting error */

  if (! yyerrstatus)
    /* If not already recovering from an error, report this error.  */
    {
      ++yynerrs;

#ifdef YYERROR_VERBOSE
      yyn = yypact[yystate];

      if (yyn > YYFLAG && yyn < YYLAST)
	{
	  int size = 0;
	  char *msg;
	  int x, count;

	  count = 0;
	  /* Start X at -yyn if nec to avoid negative indexes in yycheck.  */
	  for (x = (yyn < 0 ? -yyn : 0);
	       x < (sizeof(yytname) / sizeof(char *)); x++)
	    if (yycheck[x + yyn] == x)
	      size += strlen(yytname[x]) + 15, count++;
	  msg = (char *) malloc(size + 15);
	  if (msg != 0)
	    {
	      strcpy(msg, "parse error");

	      if (count < 5)
		{
		  count = 0;
		  for (x = (yyn < 0 ? -yyn : 0);
		       x < (sizeof(yytname) / sizeof(char *)); x++)
		    if (yycheck[x + yyn] == x)
		      {
			strcat(msg, count == 0 ? ", expecting `" : " or `");
			strcat(msg, yytname[x]);
			strcat(msg, "'");
			count++;
		      }
		}
	      yyerror(msg);
	      free(msg);
	    }
	  else
	    yyerror ("parse error; also virtual memory exceeded");
	}
      else
#endif /* YYERROR_VERBOSE */
	yyerror("parse error");
    }

  goto yyerrlab1;
yyerrlab1:   /* here on error raised explicitly by an action */

  if (yyerrstatus == 3)
    {
      /* if just tried and failed to reuse lookahead token after an error, discard it.  */

      /* return failure if at end of input */
      if (yychar == YYEOF)
	YYABORT;

#if YYDEBUG != 0
      if (yydebug)
	fprintf(stderr, "Discarding token %d (%s).\n", yychar, yytname[yychar1]);
#endif

      yychar = YYEMPTY;
    }

  /* Else will try to reuse lookahead token
     after shifting the error token.  */

  yyerrstatus = 3;		/* Each real token shifted decrements this */

  goto yyerrhandle;

yyerrdefault:  /* current state does not do anything special for the error token. */

#if 0
  /* This is wrong; only states that explicitly want error tokens
     should shift them.  */
  yyn = yydefact[yystate];  /* If its default is to accept any token, ok.  Otherwise pop it.*/
  if (yyn) goto yydefault;
#endif

yyerrpop:   /* pop the current state because it cannot handle the error token */

  if (yyssp == yyss) YYABORT;
  yyvsp--;
  yystate = *--yyssp;
#ifdef YYLSP_NEEDED
  yylsp--;
#endif

#if YYDEBUG != 0
  if (yydebug)
    {
      short *ssp1 = yyss - 1;
      fprintf (stderr, "Error: state stack now");
      while (ssp1 != yyssp)
	fprintf (stderr, " %d", *++ssp1);
      fprintf (stderr, "\n");
    }
#endif

yyerrhandle:

  yyn = yypact[yystate];
  if (yyn == YYFLAG)
    goto yyerrdefault;

  yyn += YYTERROR;
  if (yyn < 0 || yyn > YYLAST || yycheck[yyn] != YYTERROR)
    goto yyerrdefault;

  yyn = yytable[yyn];
  if (yyn < 0)
    {
      if (yyn == YYFLAG)
	goto yyerrpop;
      yyn = -yyn;
      goto yyreduce;
    }
  else if (yyn == 0)
    goto yyerrpop;

  if (yyn == YYFINAL)
    YYACCEPT;

#if YYDEBUG != 0
  if (yydebug)
    fprintf(stderr, "Shifting error token, ");
#endif

  *++yyvsp = yylval;
#ifdef YYLSP_NEEDED
  *++yylsp = yylloc;
#endif

  yystate = yyn;
  goto yynewstate;
}
#line 322 "pklGram.y"


