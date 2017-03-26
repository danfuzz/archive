#ifndef _pklMem_
#define _pklMem_



typedef void MemMarkFunction (void *mem);
typedef void MemFinalizeFunction (void *mem);

typedef struct
{
    unsigned long allocSize;
    char *name;
    MemMarkFunction *mark;
    MemFinalizeFunction *finalize;
}
MemType;

void *memAlloc (MemType *type, unsigned long count);
void memRelease (void *m);

#define memDeclareType(name) \
    extern MemType name##_type

#define memDefineType(name,mark,finalize) \
    MemType name##_type = { sizeof (name), (#name), (mark), (finalize) }

#define memAllocTypeN(name,n) ((name *) memAlloc (&(name##_type), (n)))
#define memAllocType(name) (memAllocTypeN(name,1))



#endif

