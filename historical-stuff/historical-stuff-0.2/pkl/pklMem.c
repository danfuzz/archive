#include "pkl.h"

#include <memory.h>
#include <string.h>



typedef struct MemHeader MemHeader;
struct MemHeader
{
    MemType *type;
    PklCount count;
};



void *memAlloc (MemType *type, PklCount count)
{
    long totalSize = count * type->allocSize;
    MemHeader *m = (MemHeader *)
        malloc (totalSize + sizeof (MemHeader));

    m->type = type;
    m->count = count;
    memset (&m[1], 0, totalSize);
    return (&m[1]);
}



void memRelease (void *m)
{
    MemHeader *mm = &((MemHeader *) m)[-1];

    free (mm);
}


