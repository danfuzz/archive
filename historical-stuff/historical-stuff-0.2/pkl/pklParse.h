#ifndef _pklParse_
#define _pklParse_

#include "pkl.h"



/* ------------------------------------------------------------------------- */
/* Compiler Variables, Utilities                                             */
/* ------------------------------------------------------------------------- */

extern int lineNum;
extern int nerrors;
void errRep (char *msg, ...);

#undef YYSTYPE
#define YYSTYPE PklRef


/* ------------------------------------------------------------------------- */
/* Initialization Functions                                                  */
/* ------------------------------------------------------------------------- */

void parseInit (void);



/* ------------------------------------------------------------------------- */
/* Processing                                                                */
/* ------------------------------------------------------------------------- */

PklRef nodeProcessTree (PklRef prog, PklRef tree);
void nodeExecute (PklRef tree);



/* ------------------------------------------------------------------------- */
/* Predefined Nodes                                                          */
/* ------------------------------------------------------------------------- */

extern PklRef crAndSet;
extern PklRef crAref;
extern PklRef crBand;
extern PklRef crBnot;
extern PklRef crBor;
extern PklRef crDeclareConstant;
extern PklRef crDeclareGlobal;
extern PklRef crDivide;
extern PklRef crDivideSet;
extern PklRef crEmptyExpression;
extern PklRef crEq;
extern PklRef crEqual;
extern PklRef crExpressions;
extern PklRef crGr;
extern PklRef crGreq;
extern PklRef crIf;
extern PklRef crInvalid;
extern PklRef crLabel;
extern PklRef crLand;
extern PklRef crLe;
extern PklRef crLeq;
extern PklRef crLet;
extern PklRef crLetfn;
extern PklRef crList;
extern PklRef crLnot;
extern PklRef crLor;
extern PklRef crLxor;
extern PklRef crMinus;
extern PklRef crMinusSet;
extern PklRef crModulo;
extern PklRef crModuloSet;
extern PklRef crNeq;
extern PklRef crNequal;
extern PklRef crNil;
extern PklRef crNoRestriction;
extern PklRef crOrSet;
extern PklRef crPlus;
extern PklRef crPlusSet;
extern PklRef crPower;
extern PklRef crQuote;
extern PklRef crSetf;
extern PklRef crShl;
extern PklRef crShlSet;
extern PklRef crShr;
extern PklRef crShrSet;
extern PklRef crTimes;
extern PklRef crTimesSet;
extern PklRef crUnaryMinus;
extern PklRef crUnaryPlus;
extern PklRef crXorSet;



#endif
