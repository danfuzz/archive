#include <stdarg.h>
#include <stdio.h>
#include "pklParse.h"
#include "pklMacroExpansion.h"



/* ------------------------------------------------------------------------- */
/* Compiler Utilities                                                        */
/* ------------------------------------------------------------------------- */

void errRep (char *fmt, ...)
{
    va_list args;

    fprintf (stderr, "%d: ", lineNum);

    va_start (args, fmt);
    vfprintf (stderr, fmt, args);
    va_end (args);
    fprintf (stderr, "\n");

    nerrors++;
}



/* ------------------------------------------------------------------------- */
/* Processing                                                                */
/* ------------------------------------------------------------------------- */

PklRef nodeProcessTree (PklRef prog, PklRef tree)
{
    printf ("--- We would process:\n");
    valPrintlnAsTree (stderr, tree);
    tree = macroExpansion (tree);
    printf ("--- into:\n");
    valPrintlnAsTree (stderr, tree);
    valAppendList (prog, tree);
}



void nodeExecute (PklRef tree)
{
    printf ("--- We would execute:\n");
    valPrintln (stderr, tree);
}



/* ------------------------------------------------------------------------- */
/* Predefined Nodes                                                          */
/* ------------------------------------------------------------------------- */

PklRef crAndSet;
PklRef crAref;
PklRef crBand;
PklRef crBnot;
PklRef crBor;
PklRef crDeclareConstant;
PklRef crDeclareGlobal;
PklRef crDivide;
PklRef crDivideSet;
PklRef crEmptyExpression;
PklRef crEq;
PklRef crEqual;
PklRef crExpressions;
PklRef crGr;
PklRef crGreq;
PklRef crIf;
PklRef crInvalid;
PklRef crLabel;
PklRef crLand;
PklRef crLe;
PklRef crLeq;
PklRef crLet;
PklRef crLetfn;
PklRef crList;
PklRef crLnot;
PklRef crLor;
PklRef crLxor;
PklRef crMinus;
PklRef crMinusSet;
PklRef crModulo;
PklRef crModuloSet;
PklRef crNeq;
PklRef crNequal;
PklRef crNil;
PklRef crNoRestriction;
PklRef crNoValue;
PklRef crOrSet;
PklRef crPlus;
PklRef crPlusSet;
PklRef crPower;
PklRef crQuote;
PklRef crSetf;
PklRef crShl;
PklRef crShlSet;
PklRef crShr;
PklRef crShrSet;
PklRef crTimes;
PklRef crTimesSet;
PklRef crUnaryMinus;
PklRef crUnaryPlus;
PklRef crXorSet;



void parseInit (void)
{
    crAndSet          = internNameNoCopy ("&=");
    crAref            = internNameNoCopy ("aref");
    crBand            = internNameNoCopy ("&&");
    crBnot            = internNameNoCopy ("!");
    crBor             = internNameNoCopy ("||");
    crDeclareConstant = internNameNoCopy ("declareConstant*");
    crDeclareGlobal   = internNameNoCopy ("declareGlobal*");
    crDivide          = internNameNoCopy ("/");
    crDivideSet       = internNameNoCopy ("/=");
    crEmptyExpression = internNameNoCopy ("emptyExpression*");
    crEq              = internNameNoCopy ("==");
    crEqual           = internNameNoCopy ("=");
    crExpressions     = internNameNoCopy ("expressions*");
    crGr              = internNameNoCopy (">");
    crGreq            = internNameNoCopy (">=");
    crIf              = internNameNoCopy ("if");
    crInvalid         = internNameNoCopy ("invalid");
    crLabel           = internNameNoCopy ("label");
    crLand            = internNameNoCopy ("&");
    crLe              = internNameNoCopy ("<");
    crLeq             = internNameNoCopy ("<=");
    crLet             = internNameNoCopy ("let*");
    crLetfn           = internNameNoCopy ("letfn*");
    crList            = internNameNoCopy ("list");
    crLnot            = internNameNoCopy ("~");
    crLor             = internNameNoCopy ("|");
    crLxor            = internNameNoCopy ("^");
    crMinus           = internNameNoCopy ("-");
    crMinusSet        = internNameNoCopy ("-=");
    crModulo          = internNameNoCopy ("%");
    crModuloSet       = internNameNoCopy ("%=");
    crNeq             = internNameNoCopy ("!==");
    crNequal          = internNameNoCopy ("!=");
    crNil             = internNameNoCopy ("nil");
    crNoRestriction   = internNameNoCopy ("noRestriction*");
    crNoValue         = internNameNoCopy ("noValue*");
    crOrSet           = internNameNoCopy ("|=");
    crPlus            = internNameNoCopy ("+");
    crPlusSet         = internNameNoCopy ("+=");
    crPower           = internNameNoCopy ("**");
    crQuote           = internNameNoCopy ("quote");
    crSetf            = internNameNoCopy ("setf");
    crShl             = internNameNoCopy ("<<");
    crShlSet          = internNameNoCopy ("<<=");
    crShr             = internNameNoCopy (">>");
    crShrSet          = internNameNoCopy (">>=");
    crTimes           = internNameNoCopy ("*");
    crTimesSet        = internNameNoCopy ("*=");
    crUnaryMinus      = internNameNoCopy ("u-");
    crUnaryPlus       = internNameNoCopy ("u+");
    crXorSet          = internNameNoCopy ("^=");
}
