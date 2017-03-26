#ifndef _pklIntern_h_
#define _pklIntern_h_

#include "pklTypes.h"

PklRef internAlways (PklRef val);
PklRef internIfAlready (PklRef val);
PklRef internNameNoCopy (char *name);

#endif
