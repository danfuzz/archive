#ifndef _pklGlobal_h_
#define _pklGlobal_h_

#include "pklTypes.h"

typedef enum
{
    gtVariable,
    gtConstant,
    gtMacro,
    gtSpecialForm
}
PklGlobalType;

typedef struct
{
    PklGlobalType type;
    PklRef name;
    PklRef cell;
}
PklGlobalBinding;

memDeclareType (PklGlobalBinding);

void globalCreateConstant (PklRef name, PklRef value);
void globalCreateMacro (PklRef name, PklRef value);
void globalCreateSpecialForm (PklRef name, PklRef value);
void globalCreateVariable (PklRef name, PklRef restriction, PklRef value);
void globalSetVariable (PklRef name, PklRef value);
PklGlobalType globalGetType (PklRef name);
PklRef globalGetCell (PklRef name);
PklRef globalGetValue (PklRef name);
PklRef globalGetVariableValue (PklRef name);
PklRef globalGetIfMacro (PklRef name);



#endif
