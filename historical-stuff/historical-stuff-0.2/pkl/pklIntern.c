#include "pkl.h"

#define INTERN_MAXSIZE (500)

PklCount internCount;
PklRef internTable[INTERN_MAXSIZE];



static int internFind (PklRef val)
{
    int i;

    for (i = 0; i < internCount; i++)
    {
        if (valEqual (val, internTable[i]))
            return (i);
    }

    return (-1);
}



static void internAdd (PklRef val)
{
    ASSERT (internCount < INTERN_MAXSIZE);
    internTable[internCount] = val;
    internCount++;
}



PklRef internAlways (PklRef val)
{
    int i = internFind (val);

    if (i == -1)
    {
        internAdd (val);
        return (val);
    }
    else
    {
        return (internTable[i]);
    }
}



PklRef internIfAlready (PklRef val)
{
    int i = internFind (val);

    return (i == -1 ? NULL : internTable[i]);
}



PklRef internNameNoCopy (char *name)
{
    return (internAlways (valMakeSymbolNoCopy (name)));
}
