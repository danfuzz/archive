#include "pkl.h"

PklRef constTrue;
PklRef constFalse;
PklRef constNil;
PklRef constInvalid;

void constInit (void)
{
    constTrue = valMakeBoolean (1);
    constFalse = valMakeBoolean (0);
    constNil = valMakeNil ();
    constInvalid = valMakeInvalid ();
}
