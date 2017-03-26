%{

/* 
bison --verbose --defines pklGram.y
flex -I pklScan.lex
*/

#include "pklParse.h"
#include <stdio.h>

int nerrors = 0;

#define yyerror(msg) (errRep(msg))

%}

%token LEXERROR LEXERROREOF
%token IDENT VALUE
%token CONST ELSE GLOBAL IF LET LETFN THEN
%token TERMINATOR
%nonassoc AVOID
%nonassoc OPAREN CPAREN OCURL CCURL COLON
%left IF THEN ELSE
%left  COMMA
%right SET XSETOP
%left  BOR
%left  BAND
%left  EQOP
%left  ORDOP
%left  LOGOP
%left  SHIFTOP
%left  MINUS PLUS
%left  MULOP
%left  POWER
%right AT UNARYOP
%nonassoc OSQUARE CSQUARE

%%

entirety:        program
                 { 
                     if (nerrors != 0)
                         fprintf (stderr, "Total errors: %d\n", nerrors); 
                     else
                         nodeExecute ($1);
                 }
;



program:         /* empty */
                 { $$ = constNil; }

|                program global_decl TERMINATOR
                 { $$ = nodeProcessTree ($1, $2); }

|                program statement /* includes TERMINATOR */
                 { $$ = nodeProcessTree ($1, $2); }

|                program error LEXERROREOF
                 { return (1); }
;



expression:      if_expression
                 { $$ = $1; }

|                op_expression
                 { $$ = $1; }
;



if_expression:   IF expression THEN expression %prec AVOID
                 { $$ = valMakeList (crIf, $2, $4, 
                                     valMakeList (crEmptyExpression, NULL),
                                     NULL); }

|                IF expression THEN expression ELSE expression
                 { $$ = valMakeList (crIf, $2, $4, $6, NULL); }
;



op_expression:   un_expression
                 { $$ = $1; }

|                bin_expression
                 { $$ = $1; }

|                set_expression
                 { $$ = $1; }
;



set_expression:  var SET op_expression
                 { $$ = valMakeList (crSetf, $1, $3, NULL); }

|                var XSETOP op_expression
                 { $$ = valMakeList ($2, $1, $3, NULL); }



un_expression:   rval %prec AVOID
                 { $$ = $1; }

|                UNARYOP un_expression
                 { $$ = valMakeList ($1, $2, NULL); }

|                PLUS un_expression %prec UNARYOP
                 { $$ = valMakeList (crUnaryPlus, $2, NULL); }

|                MINUS un_expression %prec UNARYOP
                 { $$ = valMakeList (crUnaryMinus, $2, NULL); }
;



bin_expression:  op_expression POWER op_expression
                 { $$ = valMakeList (crPower, $1, $3, NULL); }

|                op_expression MULOP op_expression
                 { $$ = valMakeList ($2, $1, $3, NULL); }

|                op_expression PLUS op_expression
                 { $$ = valMakeList (crPlus, $1, $3, NULL); }

|                op_expression MINUS op_expression
                 { $$ = valMakeList (crMinus, $1, $3, NULL); }

|                op_expression SHIFTOP op_expression
                 { $$ = valMakeList ($2, $1, $3, NULL); }

|                op_expression ORDOP op_expression
                 { $$ = valMakeList ($2, $1, $3, NULL); }

|                op_expression EQOP op_expression
                 { $$ = valMakeList ($2, $1, $3, NULL); }

|                op_expression LOGOP op_expression
                 { $$ = valMakeList ($2, $1, $3, NULL); }

|                op_expression BAND op_expression
                 { $$ = valMakeList (crBand, $1, $3, NULL); }

|                op_expression BOR op_expression
                 { $$ = valMakeList (crBor, $1, $3, NULL); }
;



var:             ident %prec AVOID 
                 { $$ = $1; }

|                rval OSQUARE expression CSQUARE
                 { $$ = valMakeList (crAref, $1, $3, NULL); }
;



rval:            VALUE
                 { $$ = $1; }

|                list_literal
                 { $$ = $1; }

|                AT ident
                 { $$ = valMakeList (crQuote, $2, NULL); }

|                OPAREN expression CPAREN
                 { $$ = $2; }

|                block_literal
                 { $$ = $1; }

|                call_rval
                 { $$ = $1; }

|                var 
                 { $$ = $1; }
;



call_rval:       rval argument_list block_literal
                 { $$ = valPrependList (valAppendList ($2, $3), $1); }

|                rval argument_list
                 { $$ = valPrependList ($2, $1); }
;



list_literal:    OSQUARE CSQUARE
                 { $$ = crNil; }

|                OSQUARE expressions CSQUARE
                 { $$ = valPrependList ($2, crList); }
;



argument_list:   OPAREN CPAREN
                 { $$ = constNil; }

|                OPAREN expressions CPAREN
                 { $$ = $2; }
;



expressions:     expression %prec AVOID
                 { $$ = valMakeList ($1, NULL); }

|                expressions COMMA expression
                 { $$ = valAppendList ($1, $3); }
;



block_literal:   OCURL statements CCURL
                 { $$ = valPrependList ($2, crExpressions); }
;



statements:      /* empty */
                 { $$ = constNil; }

|                statements statement
                 { $$ = valAppendList ($1, $2); }
;



statement:       expression TERMINATOR
                 { $$ = $1; }

|                ident COLON expression TERMINATOR
                 { $$ = valMakeList (crLabel, $1, $3, NULL); }

|                let_statement TERMINATOR
                 { $$ = $1; }

|                error TERMINATOR
                 { yyerrok; $$ = valMakeList (crEmptyExpression, NULL); }
;



let_statement:   LET decl_list
                 { $$ = valMakeList (crLet, $2, NULL); }

|                LETFN funcdef_list
                 { $$ = valMakeList (crLetfn, $2, NULL); }
;



global_decl:     GLOBAL decl_list
                 { $$ = valMakeList (crDeclareGlobal, $2, NULL); }

|                CONST decl_list
                 { $$ = valMakeList (crDeclareConstant, $2, NULL); }
;



decl_list:       declaration
                 { $$ = valMakeList ($1, NULL); }

|                decl_list COMMA declaration
                 { $$ = valAppendList ($1, $3); }
;



declaration:     ident opt_restriction SET expression
                 { $$ = valMakeList ($1, $2, $4, NULL); }

|                ident opt_restriction
                 { $$ = valMakeList ($1, $2, crInvalid, NULL); }
;



opt_restriction: OPAREN expression CPAREN
                 { $$ = $2; }

|                /* empty */
                 { $$ = valMakeList (crNoRestriction, NULL); }
;



funcdef_list:    funcdef
                 { $$ = valMakeList ($1, NULL); }

|                funcdef_list COMMA funcdef
                 { $$ = valAppendList ($1, $3); }
;



funcdef:         ident argument_list block_literal
                 { $$ = valMakeList ($1, $2, $3, NULL); }
;



ident:           IDENT
                 { $$ = $1; }

|                ident IDENT
                 { $$ = valMakeCompoundSymbol ($1, "-", $2); }
;



%%

