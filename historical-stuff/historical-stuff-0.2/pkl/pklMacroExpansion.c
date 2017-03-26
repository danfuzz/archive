#include "pkl.h"
#include "pklParse.h"
#include "pklMacroExpansion.h"

PklRef macroExpansion (PklRef source)
{
    if (! valIsList (source))
    {
        /* not a tree--no processing */
        return (source);
    }
    else
    {
        PklRef potential = valCar (source);

        if (valIsSymbol (potential))
        {
            PklRef macro = globalGetIfMacro (potential);
            if (macro != NULL)
            {
                PklRef args[10];
                PklCount count;
                PklCount sz = valGetSize (source);

                PRECONDITION (sz <= 11, 
                              "Too many arguments for macro expander");

                fprintf (stderr, "expand: ");
                valPrintlnAsTree (stderr, source);

                for (count = 1; count < sz; count++)
                {
                    args[count-1] = valGetNth (source, count);
                }
                
                source = valCall (macro, count - 1, args);
                fprintf (stderr, "  into: ");
                valPrintlnAsTree (stderr, source);
                return (macroExpansion (source));
            }
            else
            {
                /* variable but not macro, skip the expansion of arg #1 */
                PklRef result = valMakeList (valCar (source), NULL);

                source = valCdr (source);
                while (valIsList (source))
                {
                    valAppendList (result, macroExpansion (valCar (source)));
                    source = valCdr (source);
                }

                PRECONDITION (source == constNil,
                              "Source list didn't end with nil");
                
                return (result);
            }
        }
        else
        {
            PklRef result = constNil;
            PklBool first = 1;
            PklBool firstIsDiff;
            
            while (valIsList (source))
            {
                result = valAppendList (result,
                                        macroExpansion (valCar (source)));
                if (first)
                {
                    firstIsDiff = !valEqual (valCar (source), valCar (result));
                    first = 0;
                }

                source = valCdr (source);
            }
            
            if (firstIsDiff)
            {
                result = macroExpansion (result);
            }

            return (result);
        }
    }
}
