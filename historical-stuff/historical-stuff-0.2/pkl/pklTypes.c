#include "pkl.h"
#include "pklFn.h"

#include <stdarg.h>



/* ------------------------------------------------------------------------- */
/* Type Declarations                                                         */
/* ------------------------------------------------------------------------- */

memDefineType (PklValue, NULL, NULL);
memDefineType (PklFunction, NULL, NULL);
memDefineType (PklString, NULL, NULL);
memDefineType (PklPrimitive, NULL, NULL);
memDefineType (PklRef, NULL, NULL);
memDefineType (PklStringChar, NULL, NULL);



/* ------------------------------------------------------------------------- */
/* Object Creation                                                           */
/* ------------------------------------------------------------------------- */

PklRef valMakeInteger (long val)
{
    PklRef v = memAllocType (PklValue);

    v->type = tInteger;
    v->u.integer = val;

    return (v);
}



PklRef valMakeReal (double val)
{
    PklRef v = memAllocType (PklValue);

    v->type = tReal;
    v->u.real = val;

    return (v);
}



PklRef valMakeString (int isConstant, PklStringChar *val, PklCount size)
{
    PklRef v = memAllocType (PklValue);
    PklString *s = memAllocType (PklString);
    PklStringChar *sc = memAllocTypeN (PklStringChar, size);

    memcpy (sc, val, size);

    v->type = tString;
    v->u.string = s;
    s->isConstant = isConstant;
    s->size = size;
    s->chars = sc;

    return (v);
}



PklRef valMakeStringNoCopy (char *val)
{
    PklRef v = memAllocType (PklValue);
    PklString *s = memAllocType (PklString);
    int len = strlen (val);

    v->type = tString;
    v->u.string = s;
    s->isConstant = 1;
    s->size = len;
    s->chars = (PklStringChar *) val;

    return (v);
}



PklRef valMakeSymbol (PklStringChar *val, PklCount size)
{
    PklRef v = memAllocType (PklValue);
    PklStringChar *sc = memAllocTypeN (PklStringChar, size);

    memcpy (sc, val, size);

    v->type = tSymbol;
    v->u.symbol.size = size;
    v->u.symbol.chars = sc;

    return (v);
}



PklRef valMakeSymbolNoCopy (char *val)
{
    PklRef v = memAllocType (PklValue);

    v->type = tSymbol;
    v->u.symbol.size = strlen (val);
    v->u.symbol.chars = (PklStringChar *) val;

    return (v);
}



PklRef valMakeSymbolFromString (PklRef val)
{
    PklRef v = memAllocType (PklValue);

    ASSERT (val->type == tString);

    v->type = tSymbol;
    v->u.symbol.size = val->u.string->size;
    v->u.symbol.chars = val->u.string->chars;

    return (v);
}



PklRef valMakeCompoundSymbol (PklRef prefix, char *middle, PklRef suffix)
{
    char *prestr = "";
    PklCount prelen = 0;
    char *sufstr = "";
    PklCount suflen = 0;
    PklCount midlen = strlen (middle);
    char *newstr;
    char *inn;
 
    ASSERT (   (prefix->type == tSymbol || prefix->type == tNil)
            && (suffix->type == tSymbol || suffix->type == tNil));

    if (prefix->type == tSymbol)
    {
        prestr = prefix->u.symbol.chars;
        prelen = prefix->u.symbol.size;
    }

    if (suffix->type == tSymbol)
    {
        sufstr = suffix->u.symbol.chars;
        suflen = suffix->u.symbol.size;
    }

    newstr = alloca (prelen + suflen + strlen (middle) + 1);

    memcpy (newstr, prestr, prelen);
    inn = newstr + prelen;
    strcpy (inn, middle);
    inn += midlen;
    memcpy (inn, sufstr, suflen);
    inn += suflen;
    *inn = '\0';

    return (valMakeSymbol (newstr, prelen + midlen + suflen));
}



PklRef valMakeBoolean (int val)
{
    PklRef v = memAllocType (PklValue);

    v->type = tBoolean;
    v->u.boolean = val;

    return (v);
}



PklRef valMakeCons (PklRef car, PklRef cdr)
{
    PklRef v = memAllocType (PklValue);

    v->type = tList;
    v->u.list.car = car;
    v->u.list.cdr = cdr;

    return (v);
}



PklRef valMakeList (PklRef first, ...)
{
    PklRef result;
    PklRef tail;
    PklRef x;
    va_list args;

    if (first == NULL)
    {
        return (constNil);
    }
    
    result = valMakeCons (first, constNil);
    tail = result;

    va_start (args, first);
    while ((x = va_arg (args, PklRef)) != NULL)
    {
        tail->u.list.cdr = valMakeCons (x, constNil);
        tail = tail->u.list.cdr;
    }
    va_end (args);

    return (result);
}



PklRef valMakeNil (void)
{
    PklRef v = memAllocType (PklValue);

    v->type = tNil;
    return (v);
}



PklRef valMakeInvalid (void)
{
    PklRef v = memAllocType (PklValue);

    v->type = tInvalid;
    return (v);
}



PklRef valMakePrimitive (int minArgs, int maxArgs, 
                         PklPrimFunc func, char *name)
{
    PklRef v = memAllocType (PklValue);
    PklPrimitive *p = memAllocType (PklPrimitive);

    v->type = tPrimitive;
    v->u.primitive = p;
    p->minArgs = minArgs;
    p->maxArgs = maxArgs;
    p->func = func;
    p->name = name;

    return (v);
}



PklRef valMakeFunction (int minArgs, int maxArgs,
                        PklCount iSize, char *instructions,
                        PklCount eSize, PklRef *externals)
{
    PklRef v = memAllocType (PklValue);
    PklFunction *f = memAllocType (PklFunction);

    v->type = tFunction;
    v->u.function = f;
    f->minArgs = minArgs;
    f->maxArgs = maxArgs;
    f->iSize = iSize;
    f->instructions = instructions;
    f->eSize = eSize;
    f->externals = externals;

    return (v);
}



PklRef valMakeCell (PklRef value, PklRef restriction)
{
    PklRef v = memAllocType (PklValue);

    if (restriction == prim_truePredicate)
        restriction = NULL;

    if (value == NULL)
        value = constInvalid;

    v->type = tCell;
    v->u.cell.value = value;
    v->u.cell.restriction = restriction;

    return (v);
}



/* ------------------------------------------------------------------------- */
/* Object Inspection                                                         */
/* ------------------------------------------------------------------------- */

long valGetInteger (PklRef val)
{
    ASSERT (val->type == tInteger);

    return (val->u.integer);
}



double valGetReal (PklRef val)
{
    ASSERT (val->type == tReal);

    return (val->u.real);
}



PklStringChar *valGetString (PklRef val)
{
    switch (val->type)
    {
      case tString:
        {
            return (val->u.string->chars);
        }
        break;
      case tSymbol:
        {
            return (val->u.symbol.chars);
        }
        break;
      default:
        {
            failSoft ("Bad object type for valGetString");
        }
        break;
    }
}



int valGetBoolean (PklRef val)
{
    ASSERT (val->type == tBoolean);

    return (val->u.boolean);
}



int valIsConstant (PklRef val)
{
    switch (val->type)
    {
      case tInteger:
      case tReal:
      case tBoolean:
      case tNil:
      case tSymbol:
      case tPrimitive:
      case tFunction:
        {
            return (1);
        }
        break;

      case tList:
        {
            return (0);
        }
        break;

      case tString:
        {
            return (val->u.string->isConstant);
        }
        break;

      default:
        {
            failSoft ("Unknown object type in valIsConstant");
        }
    }
}



PklType valGetType (PklRef val)
{
    return (val->type);
}



PklBool valIsInteger (PklRef val)
{
    return (val->type == tInteger);
}



PklBool valIsReal (PklRef val)
{
    return (val->type == tReal);
}



PklBool valIsString (PklRef val)
{
    return (val->type == tString);
}



PklBool valIsSymbol (PklRef val)
{
    return (val->type == tSymbol);
}



PklBool valIsList (PklRef val)
{
    return (val->type == tList);
}



PklBool valIsPrimitive (PklRef val)
{
    return (val->type == tPrimitive);
}



PklBool valIsFunction (PklRef val)
{
    return (val->type == tFunction);
}



PklRef valCar (PklRef val)
{
    PRECONDITION (val->type == tList,
                  "Bad object for valCar");

    return (val->u.list.car);
}



PklRef valCdr (PklRef val)
{
    PRECONDITION (val->type == tList,
                  "Bad object for valCdr");

    return (val->u.list.cdr);
}



PklRef valGetNth (PklRef val, PklCount n)
{
    PRECONDITION (val->type == tList,
                  "Bad object for valGetNth");

    while (val->type == tList && n > 0)
    {
        val = val->u.list.cdr;
        n--;
    }

    if (val->type != tList)
    {
        failSoft ("Number out of range for valGetNth");
    }

    return (val->u.list.car);
}



PklCount valGetSize (PklRef val)
{
    PklCount result = 0;

    PRECONDITION (val->type == tList,
                  "Bad object for valGetSize");

    while (val->type == tList)
    {
        result++;
        val = val->u.list.cdr;
    }

    PRECONDITION (val == constNil,
                  "Bad object for valGetSize (non-nil tail)");

    return (result);
}



PklRef valGetRestriction (PklRef val)
{
    PRECONDITION (val->type == tCell,
                  "Bad object for valGetRestriction");

    if (val->u.cell.restriction != NULL)
    {
        return (val->u.cell.restriction);
    }
    else
    {
        return (prim_truePredicate);
    }
}



PklRef valGetValue (PklRef val)
{
    PklRef result;

    PRECONDITION (val->type == tCell,
                  "Bad object for valGetValue");

    result = val->u.cell.value;

    if (result == constInvalid)
    {
        if (val->u.cell.restriction != NULL)
        {
            failSoft ("Attempt to access invalid cell value of "
                      "restricted cell");
        }
    }

    return (val->u.cell.value);
}



PklBool valEqual (PklRef v1, PklRef v2)
{
    if (v1 == v2)
        return (1);

    if (v1->type != v2->type)
        return (0);

    switch (v1->type)
    {
      case tInteger:
        {
            return (v1->u.integer == v2->u.integer);
        }
        break;

      case tReal:
        {
            return (v1->u.real == v2->u.real);
        }
        break;

      case tBoolean:
        {
            return (v1->u.boolean == v2->u.boolean);
        }
        break;
        
      case tNil:
        {
            return (1);
        }
        break;

      case tString:
        {
            return
                (   (v1->u.string->size == v2->u.string->size)
                 && (   v1->u.string->isConstant 
                     == v2->u.string->isConstant)
                 && (memcmp (v1->u.string->chars,
                             v2->u.string->chars,
                             v1->u.string->size) == 0));
        }
        break;

      case tSymbol:
        {
            return (   (v1->u.symbol.size == v2->u.symbol.size)
                    && (memcmp (v1->u.symbol.chars,
                                v2->u.symbol.chars,
                                v1->u.symbol.size) == 0));
        }
        break;

      case tList:
        {
            /* BUG: should eventually do deep recursive-safe compare */
            while (v1->type == tList && v2->type == tList)
            {
                if (! valEqual (v1->u.list.car, v2->u.list.car))
                {
                    return (0);
                }

                v1 = v1->u.list.cdr;
                v2 = v2->u.list.cdr;
            }

            return (valEqual (v1, v2));
        }
        break;

      case tPrimitive:
        {
            return (v1 == v2);
        }
        break;

      case tFunction:
        {
            /* BUG: should eventually do real compare */
            return (v1 == v2);
        }
        break;

      default:
        {
            failSoft ("Comparison of unknown type");
        }
        break;
    }
}



/* ------------------------------------------------------------------------- */
/* Object Modification                                                       */
/* ------------------------------------------------------------------------- */

PklRef valPrependList (PklRef orig, PklRef newbegin)
{
    PRECONDITION (orig == constNil || orig->type == tList,
                  "Bad original object for valPrependList");

    return (valMakeCons (newbegin, orig));
}



PklRef valAppendList (PklRef orig, PklRef newend)
{
    PRECONDITION (orig == constNil || orig->type == tList,
                  "Bad original object for valAppendList");

    if (orig == constNil)
    {
        return (valMakeCons (newend, constNil));
    }
    
    while (orig->u.list.cdr->type == tList)
    {
        orig = orig->u.list.cdr;
    }

    PRECONDITION (orig->u.list.cdr == constNil,
                  "Bad original object for valAppendList (non-nil tail)");

    orig->u.list.cdr = valMakeCons (newend, constNil);
}



PklRef valSpliceList (PklRef orig, PklRef newtail)
{
    PRECONDITION (orig == constNil || orig->type == tList,
                  "Bad original object for valSpliceList");
    PRECONDITION (newtail == constNil || newtail->type == tList,
                  "Bad new tail object for valSpliceList");

    if (orig == constNil)
    {
        return (newtail);
    }
    else if (newtail == constNil)
    {
        return (orig);
    }

    while (orig->u.list.cdr->type == tList)
    {
        orig = orig->u.list.cdr;
    }

    PRECONDITION (orig->u.list.cdr == constNil,
                  "Bad original object for valSpliceList (non-nil tail)");

    orig->u.list.cdr = newtail;
}



void valSetValue (PklRef orig, PklRef value)
{
    PRECONDITION (orig->type == tCell,
                  "Bad object for valSetValue");

    if (   (orig->u.cell.restriction != NULL)
        && (valCall (orig->u.cell.restriction, 1, &value) == constFalse))
    {
        failSoft ("Value for cell did not match restriction");
    }

    orig->u.cell.value = value;
}



/* ------------------------------------------------------------------------- */
/* Object Use                                                                */
/* ------------------------------------------------------------------------- */

PklRef valCall (PklRef val, PklCount count, PklRef *args)
{
    PRECONDITION (val->type == tPrimitive || val->type == tFunction,
                  "Wrong object type for call");

    switch (val->type)
    {
      case tPrimitive:
        {
            PRECONDITION 
                (   (count >= val->u.primitive->minArgs)
                 && (   (val->u.primitive->maxArgs == -1)
                     || (count <= val->u.primitive->maxArgs)),
                 "Call with wrong number of arguments");

            return (val->u.primitive->func (count, args));
        }
        break;
      case tFunction:
        {
            ASSERT ("Interpreter not yet implemented");
        }
      default:
        {
            failSoft ("Bad object type for valCall");
        }
    }
}



/* ------------------------------------------------------------------------- */
/* Display                                                                   */
/* ------------------------------------------------------------------------- */

static
void valPrintStringlike (FILE *f, char delim, PklBool escapeNl,
                         PklStringChar *s, PklCount sleft)
{
    PklStringChar buf[400];
    PklStringChar *b = buf;
    int bused = 0;

    putc (delim, f);
    while (sleft > 0)
    {
        PklStringChar c = *s;
        s++;
        sleft--;

        if (bused > 392)
        {
            fwrite (buf, 1, bused, f);
            b = buf;
            bused = 0;
        }
                
        if (c == delim)
        {
            b[0] = '\\';
            b[1] = delim;
            b += 2;
            bused += 2;
        }
        else if (   (c >= ' ' && c < 0x7f) 
                 || (c >= 0xa0)
                 || (c == '\n' && !escapeNl))
        {
            *b = c;
            b++;
            bused++;
        }
        else
        {
            *b = '\\';
            b++;
            bused++;

            switch (c)
            {
              case 0x7f: *b = '?'; b++; bused++; break;
              case '\a': *b = 'a'; b++; bused++; break;
              case '\b': *b = 'b'; b++; bused++; break;
              case '\f': *b = 'f'; b++; bused++; break;
              case '\n': *b = 'n'; b++; bused++; break;
              case '\r': *b = 'r'; b++; bused++; break;
              case '\t': *b = 't'; b++; bused++; break;
              case '\v': *b = 'v'; b++; bused++; break;
              default:
                {
                    b[0] = ((c >> 6) & 7) + '0';
                    b[1] = ((c >> 3) & 7) + '0';
                    b[2] =  (c       & 7) + '0';
                    b += 3;
                    bused += 3;
                }
            }
        }
    }
    if (bused != 0)
    {
        fwrite (buf, 1, bused, f);
    }

    putc (delim, f);
}



static
void valPrintSymbol (FILE *f, PklStringChar *s, PklCount size)
{
    PklCount i;
    PklStringChar c = s[0];

    fputc ('@', f);

    if (size == 0)
    {
        fputs ("\'\'", f);
        return;
    }

    if (! (   (c >= 'A' && c <= 'Z')
           || (c >= 'a' && c <= 'z')
           || (c == '_')))
    {
        valPrintStringlike (f, '\'', 1, s, size);
        return;
    }
    
    for (i = 1; i < size; i++)
    {
        c = s[i];

        if (! (   (c >= 'A' && c <= 'Z')
               || (c >= 'a' && c <= 'z')
               || (c >= '0' && c <= '9')
               || (c == '_')))
        {
            valPrintStringlike (f, '\'', 1, s, size);
            return;
        }
    }
    
    fwrite (s, 1, size, f);
}



void valPrint (FILE *f, PklRef val)
{
    switch (val->type)
    {
      case tInteger:
        {
            fprintf (f, "%d", val->u.integer);
        }
        break;

      case tReal:
        {
            fprintf (f, "%g", val->u.real);
        }
        break;

      case tNil:
        {
            fputs ("nil", f);
        }
        break;

      case tSymbol:
        {
            valPrintSymbol (f, val->u.symbol.chars, val->u.symbol.size);
        }
        break;

      case tBoolean:
        {
            fputs (val->u.boolean ? "true" : "false", f);
        }
        break;

      case tString:
        {
            valPrintStringlike (f, 
                                '\"', 
                                0,
                                val->u.string->chars, 
                                val->u.string->size);
        }
        break;

      case tList:
        {
            PklBool first = 1;
            fputc ('[', f);

            while (val->type == tList)
            {
                if (first) first = 0; else fputs (", ", f);
                valPrint (f, val->u.list.car);
                val = val->u.list.cdr;
            }

            if (val != constNil)
            {
                fputs (" :: ", f);
                valPrint (f, val);
            }

            fputc (']', f);
        }
        break;

      case tPrimitive:
        {
            fprintf (f, "#<primitive %s>", val->u.primitive->name);
        }
        break;

      case tFunction:
        {
            fprintf (f, "#<fn @ 0x%x>", val);
        }
        break;

      default:
        {
            fprintf (f, "#<unknown type %d>", val->type);
        }
        break;
    }
}



void valPrintln (FILE *f, PklRef val)
{
    valPrint (f, val);
    fputc ('\n', f);
}



static
void valPrintSymbolAsTree (FILE *f, PklStringChar *s, PklCount size)
{
    PklCount i;
    PklStringChar c = s[0];

    if (size == 0)
    {
        fputs ("\'\'", f);
        return;
    }

    for (i = 0; i < size; i++)
    {
        c = s[i];

        if (c <= ' ' || c >= 0x7f || c == '\'')
        {
            valPrintStringlike (f, '\'', 1, s, size);
            return;
        }
    }
    
    fwrite (s, 1, size, f);
}



void valPrintAsTree (FILE *f, PklRef val)
{
    switch (val->type)
    {
      case tNil:
        {
            fputs ("()", f);
        }
        break;

      case tSymbol:
        {
            valPrintSymbolAsTree (f, val->u.symbol.chars, val->u.symbol.size);
        }
        break;

      case tBoolean:
        {
            fputs (val->u.boolean ? "#<true>" : "#<false>", f);
        }
        break;

      case tList:
        {
            PklBool first = 1;
            fputc ('(', f);

            while (val->type == tList)
            {
                if (first) first = 0; else fputc (' ', f);
                valPrintAsTree (f, val->u.list.car);
                val = val->u.list.cdr;
            }

            if (val != constNil)
            {
                fputs (" :: ", f);
                valPrintAsTree (f, val);
            }

            fputc (')', f);
        }
        break;

      default:
        {
            valPrint (f, val);
        }
        break;
    }
}



void valPrintlnAsTree (FILE *f, PklRef val)
{
    valPrintAsTree (f, val);
    fputc ('\n', f);
}
