#ifndef _pklTypes_
#define _pklTypes_

#include "pklMem.h"



/* ------------------------------------------------------------------------- */
/* Simple Type Declarations                                                  */
/* ------------------------------------------------------------------------- */

/* generic boolean */
typedef int PklBool;

/* generic unsigned count */
typedef unsigned long PklCount;

/* type for all strings */
typedef unsigned char PklStringChar;
memDeclareType (PklStringChar);

/* wrapper types for object pointers */
typedef struct PklValue PklValue;
memDeclareType (PklValue);

typedef PklValue *PklRef;
memDeclareType (PklRef);

/* standard substrate function call form */
typedef PklRef (*PklPrimFunc) (PklCount count, PklRef *args);



/* ------------------------------------------------------------------------- */
/* Object Type Declarations                                                  */
/* ------------------------------------------------------------------------- */

/* type of object */
typedef enum
{
    tInvalid,
    tInteger,
    tReal,
    tString,
    tBoolean,
    tSymbol,
    tList,
    tPrimitive,
    tFunction,
    tNil,
    tCell
}
PklType;



/* inner structures of objects */

typedef struct
{
    PklRef value;
    PklRef restriction;
}
PklCell;



typedef struct
{
    PklCount size;
    PklStringChar *chars;
}
PklSymbol;



typedef struct
{
    PklRef car;
    PklRef cdr;
}
PklList;



typedef struct
{
    PklBool isConstant;
    PklCount size;
    PklStringChar *chars;
}
PklString;
memDeclareType (PklString);



typedef struct
{
    int minArgs;
    int maxArgs;
    PklPrimFunc func;
    char *name;
}
PklPrimitive;
memDeclareType (PklPrimitive);



typedef struct
{
    int minArgs;
    int maxArgs;
    PklCount iSize;
    char *instructions;
    PklCount eSize;
    PklRef *externals;
}
PklFunction;
memDeclareType (PklFunction);



/* object value */
struct PklValue
{
    PklType type;
    union
    {
        long integer;
        double real;
        PklString *string;
        PklBool boolean;
        PklSymbol symbol;
        PklList list;
        PklPrimitive *primitive;
        PklFunction *function;
        PklCell cell;
    }
    u;
};



/* ------------------------------------------------------------------------- */
/* Known Constant Objects                                                    */
/* ------------------------------------------------------------------------- */

extern PklRef constFalse;
extern PklRef constTrue;
extern PklRef constNil;
extern PklRef constInvalid;



/* ------------------------------------------------------------------------- */
/* Object Creation                                                           */
/* ------------------------------------------------------------------------- */

PklRef valMakeInteger (long val);
PklRef valMakeReal (double val);
PklRef valMakeString (int isConstant, PklStringChar *val, PklCount size);
PklRef valMakeStringNoCopy (char *val); /* assumes val points to const */
PklRef valMakeSymbol (PklStringChar *val, PklCount size);
PklRef valMakeSymbolNoCopy (char *val);
PklRef valMakeSymbolFromString (PklRef val);
PklRef valMakeCompoundSymbol (PklRef prefix, char *middle, PklRef suffix);
PklRef valMakeBoolean (PklBool val);
PklRef valMakeCons (PklRef car, PklRef cdr);
PklRef valMakeList (PklRef first, ...);
PklRef valMakePrimitive (int minArgs, int maxArgs, 
                         PklPrimFunc func, char *name);
PklRef valMakeFunction (int minArgs, int maxArgs,
                        PklCount iSize, char *instructions,
                        PklCount eSize, PklRef *externals);
PklRef valMakeNil (void);
PklRef valMakeInvalid (void);
PklRef valMakeCell (PklRef value, PklRef restriction);



/* ------------------------------------------------------------------------- */
/* Object Inspection                                                         */
/* ------------------------------------------------------------------------- */

/* for any */
PklBool valEqual (PklRef v1, PklRef v2);
PklBool valIsConstant (PklRef val);
PklType valGetType (PklRef val);
PklBool valIsInteger (PklRef val);
PklBool valIsReal (PklRef val);
PklBool valIsString (PklRef val);
PklBool valIsSymbol (PklRef val);
PklBool valIsList (PklRef val);
PklBool valIsPrimitive (PklRef val);
PklBool valIsFunction (PklRef val);

/* for integers */
long valGetInteger (PklRef val);

/* for reals */
double valGetReal (PklRef val);

/* for strings and symbols */
PklStringChar *valGetString (PklRef val);

/* for booleans */
PklBool valGetBoolean (PklRef val);

/* for lists */
PklRef valCar (PklRef val);
PklRef valCdr (PklRef val);
PklCount valGetSize (PklRef val);
PklRef valGetNth (PklRef val, PklCount n);

/* for cells */
PklRef valGetRestriction (PklRef val);
PklRef valGetValue (PklRef val);



/* ------------------------------------------------------------------------- */
/* Object Modification                                                       */
/* ------------------------------------------------------------------------- */

/* for lists */
PklRef valPrependList (PklRef orig, PklRef newbegin);
PklRef valAppendList (PklRef orig, PklRef newend);
PklRef valSpliceList (PklRef orig, PklRef newtail);

/* for cells */
void valSetValue (PklRef orig, PklRef value);



/* ------------------------------------------------------------------------- */
/* Object Use                                                                */
/* ------------------------------------------------------------------------- */

/* for any */
void valPrint (FILE *f, PklRef val);
void valPrintln (FILE *f, PklRef val);
void valPrintAsTree (FILE *f, PklRef val);
void valPrintlnAsTree (FILE *f, PklRef val);

/* for primitives and functions */
PklRef valCall (PklRef val, PklCount count, PklRef *args);



#endif

