#include "pkl.h"
#include "pklMacro.h"
#include "pklParse.h"

/* ------------------------------------------------------------------------- */
/* A Brief Word On Naming Conventions:                                       */
/*                                                                           */
/* blah* = internal macro                                                    */
/* .blah = primitive                                                         */
/* $blah = internal variable                                                 */
/* blah  = function                                                          */
/* ------------------------------------------------------------------------- */



/* ------------------------------------------------------------------------- */
/* Extra Constant Names                                                      */
/* ------------------------------------------------------------------------- */

PklRef crDeref;
PklRef crLetExit;
PklRef crLetfnFixup;
PklRef crMakeRef;
PklRef crParm;

PklRef crPrimBegin;
PklRef crPrimGetCell;
PklRef crPrimIf;
PklRef crPrimLambda;
PklRef crPrimLet;
PklRef crPrimLetfn;
PklRef crPrimQuote;
PklRef crPrimSet;

PklRef crVarLabel;
PklRef crVarReturn;
PklRef crVarXSet;

PklRef crCallCC;
PklRef crCreateConstant;
PklRef crCreateGlobal;
PklRef crFalse;
PklRef crGetDeref;
PklRef crGetNth;
PklRef crInvalidateRef;
PklRef crLambda;
PklRef crMakeArefRef;
PklRef crNil;
PklRef crReturn;
PklRef crSetDeref;
PklRef crSetNth;
PklRef crTrue;
PklRef crTruePredicate;
PklRef crUnwindProtect;



/* ------------------------------------------------------------------------- */
/* Utilities                                                                 */
/* ------------------------------------------------------------------------- */

static 
PklRef simpleVar (PklRef var, PklRef val)
{
    return (valMakeList (var, crNoRestriction, val, NULL));
}



static
PklRef simpleLet (PklRef var, PklRef val, PklRef expr)
{
    return
        valMakeList (crPrimLet,
                     valMakeList (simpleVar (var, val), NULL),
                     expr,
                     NULL);
}



/* ------------------------------------------------------------------------- */
/* XSet Expanders                                                            */
/* ------------------------------------------------------------------------- */

static
PklRef expandXSet (PklRef op, PklRef args[])
{
    /* (op= var val)
     *
     * -->
     *
     * (let ((xSet noRestriction (makeRef ,var)))
     *    (setf (deref xSet) (,op (deref xSet) ,val)))
     */

    PklRef derefPart =
        valMakeList (crDeref, crVarXSet, NULL);

    PklRef mainPart =
        valMakeList (crSetf, 
                     derefPart,
                     valMakeList (op, derefPart, args[1], NULL),
                     NULL);

    return
        simpleLet (crVarXSet,
                   valMakeList (crMakeRef, args[0], NULL),
                   mainPart);
}



PklRef mAndSet (PklCount count, PklRef args[])
{
    return (expandXSet (crLand, args));
}



PklRef mDivideSet (PklCount count, PklRef args[])
{
    return (expandXSet (crDivide, args));
}



PklRef mMinusSet (PklCount count, PklRef args[])
{
    return (expandXSet (crMinus, args));
}



PklRef mModuloSet (PklCount count, PklRef args[])
{
    return (expandXSet (crModulo, args));
}



PklRef mOrSet (PklCount count, PklRef args[])
{
    return (expandXSet (crLor, args));
}



PklRef mPlusSet (PklCount count, PklRef args[])
{
    return (expandXSet (crPlus, args));
}



PklRef mShlSet (PklCount count, PklRef args[])
{
    return (expandXSet (crShl, args));
}



PklRef mShrSet (PklCount count, PklRef args[])
{
    return (expandXSet (crShr, args));
}



PklRef mTimesSet (PklCount count, PklRef args[])
{
    return (expandXSet (crTimes, args));
}



PklRef mXorSet (PklCount count, PklRef args[])
{
    return (expandXSet (crLxor, args));
}



/* ------------------------------------------------------------------------- */
/* Scope-Affecting Expanders                                                 */
/* ------------------------------------------------------------------------- */

/* make sure arg is in proper form; add null restriction if appropriate */
static
PklRef packageArg (PklRef arg)
{
    if (valIsList (arg))
    {
        PRECONDITION (valGetSize (arg) == 2,
                      "Wrong form for argument");

        return (valPrependList (arg, crParm));
    }
    else
    {
        PRECONDITION (valIsSymbol (arg),
                      "Wrong object type for argument");

        return (valMakeList (crParm, arg, crNoRestriction, NULL));
    }
}



/* make sure args (as array) are in proper form; return list */
static
PklRef packageArgs (int count, PklRef args[])
{
    PklRef result = constNil;
    
    while (count > 0)
    {
        result = valAppendList (result, packageArg (*args));
        args++;
        count--;
    }

    return (result);
}



/* make sure args (as list) are in proper form; return list */
static
PklRef packageArgList (PklRef args)
{
    PklRef result = constNil;

    while (valIsList (args))
    {
        result = valAppendList (result, packageArg (valCar (args)));
        args = valCdr (args);
    }

    return (result);
}



/* if this is a letlike form, return the transformed functor, otherwise NULL */
static
PklRef getLet (PklRef expr)
{
    if (! valIsList (expr))
    {
        return (0);
    }
    else
    {
        PklRef first = valGetNth (expr, 0);

        if (first == crLet)
        {
            return (crPrimLet);
        }
        else if (first == crLetfn)
        {
            return (crLetfnFixup);
        }
        else
        {
            return (NULL);
        }
    }
}



PklRef mExpressions (PklCount count, PklRef args[])
{
    /* (expressions* expr ...)
     * 
     * -->
     *
     * (let) or (letfn) or (begin) form, as appropriate
     * for the functor of the first expr (either let* or
     * letfn* or something else, respectively). Recurses
     * inside so that subsequent lets are in an inner scope.
     */

    if (count == 0)
    {
        /* turn (expressions) into (emptyExpression) */
        return (valMakeList (crEmptyExpression, NULL));
    }
    else
    {
        /* at each let, add a new scope level */
        PklRef result;
        PklCount inList = 0;
        PklRef let = getLet (args[inList]);

        if (let != NULL)
        {
            result = valMakeList (let, 
                                  valGetNth (args[inList], 1),
                                  NULL);
            inList++;
        }
        else
        {
            result = valMakeList (crPrimBegin, NULL);
        }

        while (inList < count)
        {
            PklRef expr = args[inList];
            if (! valIsList (expr))
            {
                valAppendList (result, expr);
                inList++;
            }
            else
            {
                if (getLet (expr) != NULL)
                {
                    /* found an inner let; build a new (expressions...) */
                    /* to deal with it                                   */
                    PklRef newEx = valMakeList (crExpressions, NULL);
                    while (inList < count)
                    {
                        valAppendList (newEx, args[inList]);
                        inList++;
                    }
                    valAppendList (result, newEx);
                }
                else
                {
                    valAppendList (result, expr);
                    inList++;
                }
            }
        }

        return (result);
    }
}



PklRef mFn (PklCount count, PklRef args[])
{
    /* (fn arg ... body)
     *
     * -->
     *
     * (lambda ,(packageArgs arg ...) body)
     */

    PklRef argPart = packageArgs (count - 1, args);

    return (valMakeList (crPrimLambda,
                         argPart,
                         valMakeList (crLetExit,
                                      crReturn,
                                      args[count - 1],
                                      NULL),
                         NULL));
}



PklRef mLambda (PklCount count, PklRef args[])
{
    /* (lambda<external> arg ... body)
     *
     * -->
     *
     * (lambda ,(packageArgs arg ...) body)
     */

    PklRef argPart = packageArgs (count - 1, args);

    return (valMakeList (crPrimLambda, argPart, args[count - 1], NULL));
}



PklRef mLet (PklCount count, PklRef args[])
{
    failSoft ("Let doesn't make sense except in a block");
}



PklRef mLetfn (PklCount count, PklRef args[])
{
    failSoft ("Letfn doesn't make sense except in a block");
}



PklRef mLetfnFixup (PklCount count, PklRef args[])
{
    /* (letfnFixup* (fn ...) expr ...)
     *
     * -->
     *
     * (letfn ,(packageArgList ,(fn ...)) expr ...)
     */

    PklRef newArgs = constNil;
    PklRef oldArgs = args[0];
    PklRef result;
    PklCount n;

    while (valIsList (oldArgs))
    {
        PklRef oldArg = valCar (oldArgs);

        PRECONDITION (valGetSize (oldArg) == 3,
                      "Wrong form for letfn");

        newArgs = valAppendList (newArgs,
                                 valMakeList (valGetNth (oldArg, 0),
                                              packageArgList 
                                                  (valGetNth (oldArg, 1)),
                                              valGetNth (oldArg, 2),
                                              NULL));
        oldArgs = valCdr (oldArgs);
    }

    result = valMakeList (crPrimLetfn, newArgs, NULL);
    for (n = 1; n < count; n++)
    {
        valAppendList (result, args[n]);
    }

    return (result);
}



PklRef mLetExit (PklCount count, PklRef args[])
{
    /* (letExit* var body)
     *
     * -->
     *
     * (callCC (lambda<external> ,(simpleVar var) body))
     */

    return (valMakeList (crCallCC,
                         valMakeList (crLambda,
                                      simpleVar (args[0], NULL),
                                      args[1],
                                      NULL),
                         NULL));
}



/* ------------------------------------------------------------------------- */
/* Flow Of Control Expanders                                                 */
/* ------------------------------------------------------------------------- */

PklRef mBand (PklCount count, PklRef args[])
{
    /* (&& a b)
     *
     * -->
     *
     * (if a b false)
     */

    return (valMakeList (crIf,
                         args[0],
                         args[1],
                         crFalse,
                         NULL));
}



PklRef mBor (PklCount count, PklRef args[])
{
    /* (|| a b)
     *
     * -->
     *
     * (if a true b)
     */

    return (valMakeList (crIf,
                         args[0],
                         crTrue,
                         args[1],
                         NULL));
}



PklRef mIf (PklCount count, PklRef args[])
{
    return (valMakeList (crPrimIf, args[0], args[1], args[2], NULL));
}



PklRef mLabel (PklCount count, PklRef args[])
{
    /* (label lab body)
     *
     * -->
     *
     * (letExit* ,(concat "break-" lab)
     *    (expressions*
     *        (letfn* ((,(concat "continue-" lab) () body)))
     *        (,(concat "continue-" lab))))
     *
     * Universal loop construct.
     */

    PklRef contLabel = valMakeCompoundSymbol (constNil, "continue-",
                                              args[0]);
    
    PklRef letPart =
        valMakeList (crLetfn,
                     valMakeList (valMakeList (contLabel,
                                               constNil,
                                               args[1],
                                               NULL),
                                  NULL),
                     NULL);
    
    return
        valMakeList (crLetExit,
                     valMakeCompoundSymbol (constNil, "break-", args[0]),
                     valMakeList (crExpressions,
                                  letPart, 
                                  valMakeList (contLabel,
                                               NULL),
                                  NULL),
                     NULL);
}



/* ------------------------------------------------------------------------- */
/* Data Access / Modification                                                */
/* ------------------------------------------------------------------------- */

PklRef mAref (PklCount count, PklRef args[])
{
    /* (aref array nth)
     *
     * -->
     *
     * (getNth array nth)
     */

    return (valMakeList (crGetNth, args[0], args[1], NULL));
}



PklRef mDeref (PklCount count, PklRef args[])
{
    /* (deref* (makeRef* var)) --> var
     * (deref* ref) --> (getDeref ref)
     */

    if (   valIsList (args[0])
        && valEqual (valGetNth (args[0], 0), crMakeRef))
    {
        return (valGetNth (args[0], 1));
    }
    else
    {
        return (valMakeList (crGetDeref, args[0], NULL));
    }
}



/* split out a unified declaration section into an expression block */
static
PklRef declareX (PklCount count, PklRef args[], PklRef how)
{
    /* (declareX ((var restriction val) ...))
     *
     * -->
     *
     * (expressions* (createX (quote var) restriction val) ...)
     */

    PklCount s, x;
    PklRef soFar = valMakeList (crExpressions, NULL);

    PRECONDITION (valIsList (args[0]),
                  "Argument to declareConstant/Global must be a list");

    s = valGetSize (args[0]);
    
    for (x = 0; x < s; x++)
    {
        PklRef decl = valGetNth (args[0], x);
        PRECONDITION (valIsList (decl) && valGetSize (decl) == 3,
                      "Sublists of declareConstant/Global must be of size 3");
        valAppendList (soFar,
                       valMakeList (how,
                                    valMakeList (crPrimQuote,
                                                 valGetNth (decl, 0),
                                                 NULL),
                                    valGetNth (decl, 1),
                                    valGetNth (decl, 2),
                                    NULL));
    }

    return (soFar);
}



PklRef mDeclareConstant (PklCount count, PklRef args[])
{
    return (declareX (count, args, crCreateConstant));
}



PklRef mDeclareGlobal (PklCount count, PklRef args[])
{
    return (declareX (count, args, crCreateGlobal));
}



PklRef mMakeRef (PklCount count, PklRef args[])
{
    /* (makeRef* (aref var nth)) --> (makeArefRef var nth)
     * (makeRef* (deref* ref))   --> ref
     * (makeRef* var)            --> (makeVarRef var)
     */

    switch (valGetType (args[0]))
    {
      case tSymbol:
        {
            return (valMakeList (crPrimGetCell,
                                 args[0],
                                 NULL));
        }
        break;
      case tList:
        {
            PklRef func = valGetNth (args[0], 0);
            if (valEqual (func, crAref))
            {
                return (valMakeList (crMakeArefRef,
                                     valGetNth (args[0], 1),
                                     valGetNth (args[0], 2),
                                     NULL));
            }
            else if (valEqual (func, crDeref))
            {
                return (valGetNth (args[0], 1));
            }
            else
            {
                failSoft ("Bad argument functor for makeRef");
            }
        }
        break;
      default:
        {
            failSoft ("Bad argument to makeRef");
        }
        break;
    }
}



PklRef mSetf (PklCount count, PklRef args[])
{
    /* (setf (aref var nth) val) --> (setnth var nth val)
     * (setf (deref ref) val)    --> (setderef ref val)
     * (setf var val)            --> (set var val)
     */

    switch (valGetType (args[0]))
    {
      case tSymbol:
        {
            return (valMakeList (crPrimSet,
                                 args[0],
                                 args[1],
                                 NULL));
        }
        break;
      case tList:
        {
            PklRef func = valGetNth (args[0], 0);
            if (valEqual (func, crAref))
            {
                return (valMakeList (crSetNth,
                                     valGetNth (args[0], 1),
                                     valGetNth (args[0], 2),
                                     args[1],
                                     NULL));
            }
            else if (valEqual (func, crDeref))
            {
                return (valMakeList (crSetDeref,
                                     valGetNth (args[0], 1),
                                     args[1],
                                     NULL));
            }
            else
            {
                failSoft ("Bad first argument functor for setf");
            }
        }
        break;
      default:
        {
            failSoft ("Bad first argument to setf");
        }
        break;
    }
}



/* ------------------------------------------------------------------------- */
/* Other Expanders                                                           */
/* ------------------------------------------------------------------------- */

PklRef mEmptyExpression (PklCount count, PklRef args[])
{
	return (crNil);
}



PklRef mNoRestriction (PklCount count, PklRef args[])
{
    return (crTruePredicate);
}



PklRef mQuote (PklCount count, PklRef args[])
{
    return (valMakeList (crPrimQuote, args[0], NULL));
}



/* ------------------------------------------------------------------------- */
/* Initialization                                                            */
/* ------------------------------------------------------------------------- */

typedef PklRef (*PklMacroFn) (PklCount count, PklRef args[]);
static void defMacro (int minArgs, int maxArgs, char *name, PklMacroFn fn)
{
    globalCreateMacro
        (internNameNoCopy (name),
         valMakePrimitive (minArgs, maxArgs, (PklPrimFunc) fn, name));
}



void macroInit (void)
{
    crDeref      = internNameNoCopy ("deref*");
    crLetExit    = internNameNoCopy ("letExit*");
    crLetfnFixup = internNameNoCopy ("letfnFixup*");
    crMakeRef    = internNameNoCopy ("makeRef*");
    crParm       = internNameNoCopy ("parm*");

    crPrimBegin      = internNameNoCopy (".begin");
    crPrimGetCell    = internNameNoCopy (".getCell");
    crPrimIf         = internNameNoCopy (".if");
    crPrimLambda     = internNameNoCopy (".lambda");
    crPrimLet        = internNameNoCopy (".let");
    crPrimLetfn      = internNameNoCopy (".letfn");
    crPrimQuote      = internNameNoCopy (".quote");
    crPrimSet        = internNameNoCopy (".set");

    crVarLabel  = internNameNoCopy ("$label");
    crVarReturn = internNameNoCopy ("$return");
    crVarXSet   = internNameNoCopy ("$xSet");

    crCallCC         = internNameNoCopy ("callCC");
    crCreateConstant = internNameNoCopy ("createConstant");
    crCreateGlobal   = internNameNoCopy ("createGlobal");
    crFalse          = internNameNoCopy ("false");
    crGetDeref       = internNameNoCopy ("getDeref");
    crGetNth         = internNameNoCopy ("getNth");
    crInvalidateRef  = internNameNoCopy ("invalidateRef");
    crLambda         = internNameNoCopy ("lambda");
    crMakeArefRef    = internNameNoCopy ("makeArefRef");
    crReturn         = internNameNoCopy ("return");
    crSetDeref       = internNameNoCopy ("setDeref");
    crSetNth         = internNameNoCopy ("setNth");
    crTrue           = internNameNoCopy ("true");
    crTruePredicate  = internNameNoCopy ("truePredicate");
    crUnwindProtect  = internNameNoCopy ("unwindProtect");

    /* XSet */
    defMacro (2, 2, "&=",  mAndSet);
    defMacro (2, 2, "/=",  mDivideSet);
    defMacro (2, 2, "-=",  mMinusSet);
    defMacro (2, 2, "%=",  mModuloSet);
    defMacro (2, 2, "|=",  mOrSet);
    defMacro (2, 2, "+=",  mPlusSet);
    defMacro (2, 2, "<<=", mShlSet);
    defMacro (2, 2, ">>=", mShrSet);
    defMacro (2, 2, "*=",  mTimesSet);
    defMacro (2, 2, "^=",  mXorSet);

    /* scope */
    defMacro (0, -1, "expressions*", mExpressions);
    defMacro (1, -1, "fn",           mFn);
    defMacro (1, -1, "lambda",       mLambda);
    defMacro (1, 1,  "let*",         mLet);
    defMacro (1, 1,  "letfn*",       mLetfn);
    defMacro (1, -1, "letfnFixup*",  mLetfnFixup);
    defMacro (2, 2,  "letExit*",     mLetExit);

    /* flow of control */
    defMacro (2, 2, "&&",    mBand);
    defMacro (2, 2, "||",    mBor);
    defMacro (3, 3, "if",    mIf);
    defMacro (2, 2, "label", mLabel);

    /* data access / modification */
    defMacro (2, 2, "aref",             mAref);
    defMacro (1, 1, "deref*",           mDeref);
    defMacro (1, 1, "declareConstant*", mDeclareConstant);
    defMacro (1, 1, "declareGlobal*",   mDeclareGlobal);
    defMacro (1, 1, "makeRef*",         mMakeRef);
    defMacro (2, 2, "setf",             mSetf);

    /* other */
    defMacro (0, 0, "emptyExpression*", mEmptyExpression);
    defMacro (0, 0, "noRestriction*",   mNoRestriction);
    defMacro (1, 1, "quote",            mQuote);
}
