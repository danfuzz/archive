#ifndef _pklAssert_
#define _pklAssert_



void assertionFailure (char *a, char *file, int line);
void failSoft (char *msg, ...);
void failHard (char *msg, ...);

#define ASSERT(a)                                        \
    do                                                   \
    {                                                    \
        if (! (a))                                       \
            assertionFailure ((#a), __FILE__, __LINE__); \
    }                                                    \
    while (0);



/* should be soft failure; report exception or whatever */
#define PRECONDITION(a,msg) \
    do                      \
    {                       \
        if (! (a))          \
            failSoft (msg); \
    }                       \
    while (0);



#endif

