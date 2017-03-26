#include "pkl.h"

/* ------------------------------------------------------------------------- */
/* Utility Definitions                                                       */
/* ------------------------------------------------------------------------- */

#define fnDefine(name)                                \
    PklRef prim_##name;                               \
    PklRef (fn_##name) (PklCount count, PklRef *args)

#define fnMakeSym(name, qname, minArgs, maxArgs)                        \
    do                                                                  \
    {                                                                   \
        prim_##name = valMakePrimitive ((minArgs), (maxArgs),           \
                                        fn_##name, #name);              \
        globalCreateConstant (internNameNoCopy ((qname)), prim_##name); \
    }                                                                   \
    while (0)

#define fnMake(name, minArgs, maxArgs) \
    fnMakeSym (name, #name, (minArgs), (maxArgs))



/* ------------------------------------------------------------------------- */
/* The Functions                                                             */
/* ------------------------------------------------------------------------- */

fnDefine (equal)
{
    return (valEqual (args[0], args[1])
            ? constTrue
            : constFalse);
}



fnDefine (truePredicate)
{
    return (constTrue);
}



/* ------------------------------------------------------------------------- */
/* Initialization                                                            */
/* ------------------------------------------------------------------------- */

void fnInit (void)
{
    fnMakeSym (equal, "=", 2, 2);

    fnMake (truePredicate, 0, -1);
}
