#include "pkl.h"

#define GLOBAL_MAXSIZE (200)

PklCount globalCount;
PklGlobalBinding globalTable[GLOBAL_MAXSIZE];

memDefineType (PklGlobalBinding, NULL, NULL);

static int globalFind (PklRef name)
{
    int i;

    name = internIfAlready (name);
    if (name != NULL)
    {
        for (i = 0; i < globalCount; i++)
        {
            if (name == globalTable[i].name)
                return (i);
        }
    }

    return (-1);
}



static void globalAdd (PklGlobalType type, PklRef name, PklRef restriction,
                       PklRef value)
{
    ASSERT (globalCount < GLOBAL_MAXSIZE);

    globalTable[globalCount].type = type;
    globalTable[globalCount].name = internAlways (name);
    globalTable[globalCount].cell = valMakeCell (value, restriction);

    globalCount++;
}



void globalCreateConstant (PklRef name, PklRef value)
{
    PRECONDITION (globalFind (name) == -1, "Cannot redefine to constant");

    globalAdd (gtConstant, name, NULL, value);
}



void globalCreateMacro (PklRef name, PklRef value)
{
    PRECONDITION (globalFind (name) == -1, "Cannot redefine to macro");

    globalAdd (gtMacro, name, NULL, value);
}



void globalCreateSpecialForm (PklRef name, PklRef value)
{
    PRECONDITION (globalFind (name) == -1, "Cannot redefine to special form");

    globalAdd (gtSpecialForm, name, NULL, value);
}



void globalCreateVariable (PklRef name, PklRef restriction, PklRef value)
{
    PRECONDITION (globalFind (name) == -1, "Cannot redefine to variable");

    globalAdd (gtVariable, name, restriction, value);
}



void globalSetVariable (PklRef name, PklRef value)
{
    int i = globalFind (name);

    PRECONDITION (i != -1, "Assignment to nonexistent global");
    PRECONDITION (globalTable[i].type == gtVariable, 
                  "Assignment to non-variable.");

    valSetValue (globalTable[i].cell, value);
}



PklGlobalType globalGetType (PklRef name)
{
    int i = globalFind (name);

    PRECONDITION (i != -1, "Access of nonexistent global");

    return (globalTable[i].type);
}



PklRef globalGetCell (PklRef name)
{
    int i = globalFind (name);

    PRECONDITION (i != -1, "Access of nonexistent global");

    return (globalTable[i].cell);
}



PklRef globalGetValue (PklRef name)
{
    int i = globalFind (name);
    PklRef val;

    PRECONDITION (i != -1, "Access of nonexistent global");

    return (valGetValue (globalTable[i].cell));
}



PklRef globalGetVariableValue (PklRef name)
{
    int i = globalFind (name);

    PRECONDITION (i != -1, "Access of nonexistent global");
    PRECONDITION (   (globalTable[i].type == gtConstant)
                  || (globalTable[i].type == gtVariable),
                  "Access of non-variable");

    return (valGetValue (globalTable[i].cell));
}



PklRef globalGetIfMacro (PklRef name)
{
    int i = globalFind (name);

    if (   (i != -1)
        && (globalTable[i].type == gtMacro))
    {
        return (valGetValue (globalTable[i].cell));
    }
    else
    {
        return (NULL);
    }
}
