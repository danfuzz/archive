/* ------------------------------------------------------------------------- */
/* Anagram.c                                                                 */
/*                                                                           */
/* by Dan Bornstein                                                          */
/*                                                                           */
/* I suggest using -O2 for this puppy; works like a charm. Also,             */
/* -funroll-loops and -finline-functions seem to do quite well.              */
/* ------------------------------------------------------------------------- */

#include <stdio.h>

char *myname;

typedef short Letter;
typedef Letter LetCountType[26];

typedef struct
{
    int len;
    char *word;
    Letter *letterCounts;
}
Word;

Word *possibles;
int cursize;
int maxsize;



static
int mylen (char *str)
{
    int l = 0;
    while (*str)
    {
        if (isalpha (*str)) l++;
        str++;
    }

    return (l);
}



static
int mycmp (char *s1, char *s2)
{
    char c1;
    char c2;

    for (;;)
    {
        while (*s1 && ! isalpha (c1 = *s1)) s1++;
        while (*s2 && ! isalpha (c2 = *s2)) s2++;
        c1 = tolower (c1);
        c2 = tolower (c2);
        if (c1 < c2) return (-1);
        if (c1 > c2) return (1);
        if (!c1 && !c2) return (0);
        s1++;
        s2++;
    }
}



static
void initWords ()
{
    maxsize = 100;
    possibles = (Word *) malloc (maxsize * sizeof (Word));
    cursize = 0;
}



static
void addWord (char *word, Letter *letterCounts)
{
    Word *nw;
    
    if (cursize == maxsize)
    {
        possibles = (Word *) realloc (possibles, maxsize * 2 * sizeof (Word));
        maxsize *= 2;
    }

    nw = possibles + cursize;
    cursize++;

    nw->word = (char *) strdup (word);
#if 0
    word = nw->word;
    while (*word)
    {
        if (isalpha (*word))
            *word = tolower (*word);
        word++;
    }
#endif

    nw->len = mylen (nw->word);
    nw->letterCounts = (Letter *) malloc (26 * sizeof (Letter));
    memcpy (nw->letterCounts, letterCounts, 26 * sizeof (Letter));
}



static
void makeCounts (char *word, Letter *letterCounts)
{
    int i;
    char c;
    
    for (i = 0; i < 26; i++)
        letterCounts[i] = 0;

    while (c = *word)
    {
        if (isalpha (c))
            letterCounts[tolower (c) - 'a']++;
        word++;
    }
}



static
void countSub (Letter *c1, Letter *c2)
{
    int i;

    for (i = 0; i < 26; i++)
        c1[i] -= c2[i];
}



static
int canWork (Letter *c1, Letter *c2)
{
    int i;

    for (i = 0; i < 26; i++)
        if (c1[i] < c2[i]) return (0);

    return (1);
}



static
int exactMatch (Letter *c1, Letter *c2)
{
    int i;
    
    for (i = 0; i < 26; i++)
        if (c1[i] != c2[i]) return (0);

    return (1);
}



static
void maybeAdd (char *word, Letter *sourceCounts)
{
    Letter letterCounts[26];

    makeCounts (word, letterCounts);
    if (mylen (word) != 0
        && canWork (sourceCounts, letterCounts))
        addWord (word, letterCounts);
}



static
void addAllWords (Letter *sourceCounts, char *filename)
{
    FILE *f = fopen (filename, "r");
    char buf[100];
    int l;

    if (! f)
    {
        perror (myname);
        exit (1);
    }

    for (;;)
    {
        buf[0] = 0;
        fgets (buf, 100, f);
        if (! buf[0])
            break;
        l = strlen (buf);
        if (buf[l-1] == '\n')
            buf[l-1] = 0;
        maybeAdd (buf, sourceCounts);
    }

    fclose (f);
}



static
int sortWordsFn (Word *w1, Word *w2)
{
    if (w1->len < w2->len)
        return (1);

    if (w1->len > w2->len)
        return (-1);

    return (mycmp (w1->word, w2->word));
}

static
void sortWords (void)
{
    qsort (possibles, cursize, sizeof (Word), sortWordsFn);

    {
        int i;
        for (i = 0; i < cursize; i++)
            printf ("Word: %s\n", possibles[i].word);

        fflush (stdout);
    }
}



static
void findAnagrams (char *source, Letter *sourceCounts)
{
    int *stack = (int *) malloc (strlen (source) * 10 * sizeof (int));
    LetCountType *lcstack = 
        (LetCountType *) malloc (strlen (source) * 10 * sizeof (LetCountType));
    int depth = 0;
    int cur = -1;
    Letter *curCounts = lcstack[0];
    Word *aword;

    memcpy (curCounts, sourceCounts, 26 * sizeof (Letter));

    for (;;)
    {
        cur++;
        if (cur == cursize)
        {
            if (depth == 0)
                break;
            depth--;
            cur = stack[depth];
            curCounts = lcstack[depth];
            continue;
        }

        aword = &possibles[cur];
        
        if (canWork (curCounts, aword->letterCounts))
        {
            if (exactMatch (curCounts, aword->letterCounts))
            {
                int i;
                for (i = 0; i < depth; i++)
                    printf ("%s ", 
                            possibles[stack[i]].word);
                printf ("%s\n", aword->word);
            }
            else
            {
                if (depth == 0)
                    fprintf (stderr, "(first word: %s; %d left)\n", 
                             aword->word, cursize - cur);
                   
                stack[depth] = cur;
                memcpy (lcstack[depth+1], curCounts, 26 * sizeof (Letter));
                depth++;
                curCounts = lcstack[depth];
                countSub (curCounts, aword->letterCounts);
                cur--;
            }
        }
    }
}



int main (int argc, char *argv[])
{
    char *sourceString;
    Letter sourceCounts[26];
    char *file = "/usr/share/dict/words";

    initWords ();

    myname = argv[0];

    if (argc < 2)
    {
        fprintf (stderr, "%s: I want another arg\n", myname);
        exit (1);
    }

    if (argc >= 3)
        file = argv[2];

    sourceString = (char *) malloc (strlen (argv[1]) + 1);
    
    {
        char *s = argv[1];
        char *d = sourceString;

        while (*s)
        {
            if (isalpha (*s))
                *(d++) = tolower (*s);
            s++;
        }
        *d = 0;
    }

    makeCounts (sourceString, sourceCounts);

    addAllWords (sourceCounts, file);
    sortWords ();
    findAnagrams (sourceString, sourceCounts);

    exit (0);
}


