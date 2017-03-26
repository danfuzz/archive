/* ------------------------------------------------------------------------- */
/* Dan's Markoffer.                                                          */
/*                                                                           */
/* Theory: Turn the text into a series of 16-bit numbers and then use        */
/* the text itself implicitly as the statistic.                              */
/*                                                                           */
/* To be done:                                                               */
/*   Make a read option which parses -dump output.                           */
/*   Make output use a state machine to make it closer to reality, including */
/*     perhaps automatically capitalizing the first letter of a sentence.    */
/*   Fix input state machine to grok times (4:15) and numbers (4,350).       */
/*   Make searching through the text faster: use Boyer-Moore and make        */
/*     getWord more efficient (mod is slow!)                                 */
/* ------------------------------------------------------------------------- */

#include <stdio.h>
#include <ctype.h>

#include <sys/types.h>                       /* all this for random number   */
#include <sys/time.h>                        /* seeds                        */



/* ------------------------------------------------------------------------- */
/* Options                                                                   */
/* ------------------------------------------------------------------------- */

int optCase;                                 /* is case significant?         */
int optPunct;                                /* keep punct as separate words?*/
int optPunctFold;                            /* fold punct into word class?  */
int optDepth;                                /* markoff depth                */
int optWords;                                /* number of words              */
int optSeed;                                 /* random number seed           */
int optStartWord;                            /* start word to use            */
char *optDumpFile;                           /* dump file ("-" = stdout)     */
char *optInFile;                             /* input file ("-" = stdin)     */
char *optOutFile;                            /* output file ("-" = stdout)   */
int optStatus;                               /* print status info            */

char *name = "##danmar.c by danfuzz##";      /* our name                     */



/* ------------------------------------------------------------------------- */
/* Random Globals                                                            */
/* ------------------------------------------------------------------------- */

#define CHUNK_SIZE (100000)                  /* size of chunk of source text */
#define MAX_CHUNKS (42)                      /* max number of chunks of text */
#define MAX_WORDS (20000)                    /* max number of unique words   */

typedef unsigned short WordNum;



char *text[MAX_CHUNKS];                      /* pointers to some text        */
int usedText[MAX_CHUNKS];                    /* whether a text is in use     */
int textId[MAX_CHUNKS];                      /* text id number               */
int numChunks = 0;                           /* how many chunks are there?   */

int numRaws = 0;                             /* how many raw text chunks?    */
int numNTexts = 0;                           /* how many num-text chunks?    */
int numWNums = 0;                            /* how many num-text words?     */

char *words[MAX_WORDS];                      /* pointers to each word        */
int numWords = 0;

#define wordEnd (65535)                      /* end sentinel for numtext     */



/* ------------------------------------------------------------------------- */
/* Chunk Manipulation                                                        */
/* ------------------------------------------------------------------------- */

#define useNot  (0)
#define useWord (1)
#define useText (2)

char *getChunk (int useCode, int id)
{
    int i;

    if (numChunks == MAX_CHUNKS)
    {
        fprintf (stderr, "%s: too much text\n", name);
        exit (1);
    }

    for (i = 0; i < MAX_CHUNKS; i++)
        if (usedText[i] == useNot)
            break;

    if (i == MAX_CHUNKS)
    {
        fprintf (stderr, "%s: out of chunks\n", name);
        exit (1);
    }

    if (! text[i])
    {
        text[i] = (char *) malloc (CHUNK_SIZE);

        if (! text[i])
        {
            fprintf (stderr, "%s: out of memory\n", name);
            exit (1);
        }
    }

    usedText[i] = useCode;
    textId[i] = id;
    numChunks++;

    return (text[i]);
}



char *getId (int useCode, int id)
{
    int i;

    for (i = 0; i < MAX_CHUNKS; i++)
        if (usedText[i] == useCode && textId[i] == id)
            return (text[i]);

    return (0);
}



/* ------------------------------------------------------------------------- */
/* Dictionarification                                                        */
/* ------------------------------------------------------------------------- */

char *curDictChunk = 0;
int dictSpaceLeft = 0;

WordNum addWordToDict (char *word)
{
    int l = strlen (word) + 1;

    if (numWords == MAX_WORDS)
    {
        fprintf (stderr, "%s: too many unique words\n", name);
        exit (1);
    }

    if (l > dictSpaceLeft)
    {
        curDictChunk = getChunk (useWord, 0);
        dictSpaceLeft = CHUNK_SIZE;
    }

    strcpy (curDictChunk, word);
    words[numWords] = curDictChunk;
    numWords++;
    curDictChunk += l;
    dictSpaceLeft -= l;

    if (optStatus && (numWords & 0x1ff) == 0)
        fprintf (stderr, "(%d unique words so far)\n", numWords);

    /* fprintf (stderr, "[%d=%s]\n", numWords - 1, word); */

    return (numWords - 1);
}



WordNum findWord (char *word)
{
    int i;

    for (i = 0; i < numWords; i++)
        if (*word == *words[i] && (strcmp (word, words[i]) == 0))
            return (i);

    return (wordEnd);
}



/* ------------------------------------------------------------------------- */
/* Add word number to number-text                                            */
/* ------------------------------------------------------------------------- */

WordNum *curNumChunk = 0;
int numSpaceLeft = 0;

void addWordNum (WordNum wn)
{
    if (numSpaceLeft == 0)
    {
        curNumChunk = (WordNum *) getChunk (useText, numNTexts);
        numNTexts++;
        numSpaceLeft = CHUNK_SIZE / (sizeof (WordNum)) - 1;
    }

    *(curNumChunk++) = wn;
    *curNumChunk = wordEnd;

    numSpaceLeft--;
    numWNums++;

    if (optStatus && (numWNums & 0xfff) == 0)
        fprintf (stderr, "(%d words of source text so far)\n", numWNums);
}



WordNum getWord (long offset)
{
    int id;
    WordNum *t;

    if (offset < 0)
    {
        while (offset < 0)
            offset += numWNums;
    }
    else if (offset >= numWNums)
        offset %= numWNums;

    id =     offset / (CHUNK_SIZE / (sizeof (WordNum)) - 1);
    offset = offset % (CHUNK_SIZE / (sizeof (WordNum)) - 1);

    t = (WordNum *) getId (useText, id);
    return (t[offset]);
}



/* ------------------------------------------------------------------------- */
/* Dumping                                                                   */
/* ------------------------------------------------------------------------- */

char *wordFwap (WordNum wn)
{
    static char *table = "0123456789abcdefghijklmnopqrstuv"
                         "wxyzABCDEFGHIJKLMNOPQRSTUVWXYZ+$";
    static char buf[4] = "xxx";
    int i;

    for (i = 2; i >= 0; i--)
    {
        buf[i] = table[wn & 0x3f];
        wn >>= 6;
    }

    return (buf);
}
    


void dumpInfo (FILE *f)
{
    int i;
    long totchar = 0;

    for (i = 0; i < numWords; i++)
        totchar += strlen (words[i]) + 1;

    fprintf (f, "[%d words / %d chars]\n", numWords, totchar);
    for (i = 0; i < numWords; i++)
        fprintf (f, "%s\n", words[i]);
    
    fprintf (f, "[%d words of source text]\n", numWNums);
    for (i = 0; i < numWNums; i++)
        fprintf (f, "%s%c", wordFwap (getWord (i)),
                 (i & 0xf) == 0xf ? '\n' : ' ');

    if ((i & 0xf) != 0)
        fprintf (f, "\n");
}



/* ------------------------------------------------------------------------- */
/* Wordification                                                             */
/* ------------------------------------------------------------------------- */

#define classWhite (0)
#define classWord  (1)
#define classPunct (2)
#define classApost (3)

int classTable[128] =
{
    0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0,
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
    0, 2, 2, 2, 2, 2, 2, 3, 2, 2, 2, 2, 2, 2, 2, 2,
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2,
    2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2,
    2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
    1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 0
};



void punctFold (void)
{
    int i;

    for (i = 0; i < 128; i++)
        if (classTable[i] == classPunct || classTable[i] == classApost)
            classTable[i] = classWord;

    classTable['.'] = classTable[','] = classTable[':'] = classTable[';']
                    = classTable['?'] = classTable['!'] = classPunct;
}



void downcase (char *buf)
{
    char c;

    while ((c = *buf))
    {
        if (isupper (c))
            *buf = tolower (c);
        buf++;
    }
}



void spewWord (char *word)
{
    WordNum wn;
    
    if (! optCase)
        downcase (word);

    if (!*word || (classTable[*word] == classPunct && !optPunct))
        return;

    wn = findWord (word);
    if (wn == wordEnd)
        wn = addWordToDict (word);

    addWordNum (wn);
}



#define actIgnore (0)
#define actAdd    (1)
#define actNew    (2)
#define actBack   (3)

int stateTable[4][8] =
{
    { actIgnore, 0, actAdd, 2, actAdd,  1, actAdd,  1 },
    { actNew,    0, actNew, 0, actNew,  0, actNew,  0 },
    { actNew,    0, actAdd, 2, actNew,  0, actAdd,  3 },
    { actBack,   1, actAdd, 2, actBack, 1, actBack, 1 }
};
    


void processFile (FILE *f)
{
    char word[1024];
    char *inword = word;
    int state = 0;
    char readahead = 0;
    char buf[65537];
    char *inbuf;

    while (! feof (f))
    {
        {
            int amtread = fread (buf, 1, 65536, f);
            buf[amtread] = 0;
            /* fprintf (stderr, "Read a chunk of len %d\n", amtread); */
        }

        inbuf = buf;

        for (;;)
        {
            char c;
            int cclass;
            int action;

            if (readahead)
            {
                c = readahead;
                readahead = 0;
            }
            else
            {
                c = *(inbuf++);
                if (!c)
                    break;
            }
            
            c &= 0x7f;
            cclass = classTable[(int) c] << 1;
            action = stateTable[state][cclass];
            state  = stateTable[state][cclass + 1];
            
            switch (action)
            {
                case actIgnore:
                {
                    break;
                }
                case actAdd:
                {
                    *(inword++) = c;
                    break;
                }
                case actNew:
                {
                    *inword = 0;
                    spewWord (word);
                    inword = word;
                    inbuf--;
                    break;
                }
                case actBack:
                {
                    inbuf--;
                    readahead = *(--inword);
                    break;
                }
            }
        }
    }

    *inword = 0;
    spewWord (word);
}

/* ------------------------------------------------------------------------- */
/* s = whitespace                                                            */
/* w = word-char                                                             */
/* p = punct                                                                 */
/* a = apostrophe (')                                                        */
/*                                                                           */
/* words are:                                                                */
/*   w+                                                                      */
/*   p                                                                       */
/*   w+aw+                                                                   */
/*   a                                                                       */
/*                                                                           */
/* I = ignore char/advance                                                   */
/* A = add char/advance                                                      */
/* N = new word (don't advance)                                              */
/* B = remove char/backup                                                    */
/*                                                                           */
/* state machine:                                                            */
/* 00: s:I/00      (in state 0, on whitespace, ignore and go to state 0)     */
/*     w:A/02      (in state 0, on word char, add it and go to state 2)      */
/*     p:A/01      (etc.)                                                    */
/*     a:A/01                                                                */
/*                                                                           */
/* 01: ?:N/00                                                                */
/*                                                                           */
/* 02: s:N/00                                                                */
/*     w:A/02                                                                */
/*     p:N/00                                                                */
/*     a:A/03                                                                */
/*                                                                           */
/* 03: s:B/01                                                                */
/*     w:A/02                                                                */
/*     p:B/01                                                                */
/*     a:B/01                                                                */
/* ------------------------------------------------------------------------- */

   

/* ------------------------------------------------------------------------- */
/* Markoff Madness                                                           */
/* ------------------------------------------------------------------------- */

int outCol = 0;
int lastPunct = 0;
int lastOpen = 0;

void printWord (WordNum wn, FILE *f)
{
    char *thisWord = words[wn];
    char first = *thisWord;
    int punc = punc != '\"' 
        && classTable[first] == classPunct 
        && thisWord[1] == 0;
    int op = punc && (first == '(' || first == '[' || first == '{');

    if (!lastOpen && (!punc || op))
    {
        fprintf (f, " ");
        outCol++;
    }

    if (strlen (thisWord) + outCol > 75)
    {
        fprintf (f, "\n");
        outCol = 0;
    }

    while (*thisWord)
    {
        if (*thisWord == 0x08)
            outCol--;
        else
            outCol++;
        thisWord++;
    }
    
    fprintf (f, "%s", words[wn]);
    fflush (f);

    lastPunct = punc;
    lastOpen = op;
}



void markoff (long depth, long numWords, long startWord, FILE *f)
{
    long place;
    int i;
    WordNum neww;
    WordNum pat[200];

    /* init the pattern */
    for (i = 0; i < depth; i++)
    {
        pat[i] = getWord (startWord + i);
        printWord (pat[i], f);
    }

    while (numWords--)
    {
        /* pick a place where the pattern matches */
        if (depth == 0)                      /* special case for depth 0     */
            place = lrand48 () % numWNums;
        else
        {
            int numPlaces = 0;
            long places[20000];

            for (i = 0; i < numWNums; i++)
            {
                int j;
                for (j = 0; j < depth; j++)
                    if (getWord (i + j) != pat[j])
                        break;
                if (j == depth)
                    places[numPlaces++] = i;
            }

            place = places[lrand48 () % numPlaces];
        }
        
        /* get the word after the pattern */
        neww = getWord (place + depth);
        if (neww == wordEnd)
            neww = getWord (startWord);

        printWord (neww, f);

        /* rotate the pattern */
        for (i = 1; i < depth; i++)
            pat[i - 1] = pat[i];
        pat[depth - 1] = neww;
    }

    fprintf (f, "\n");
}


/* ------------------------------------------------------------------------- */
/* Option parsing                                                            */
/* ------------------------------------------------------------------------- */

long longArg (char *opt, char *arg)
{
    char *sptr;
    long val = strtol (arg, &sptr, 0);
    
    if (sptr == arg)
    {
        fprintf (stderr, "%s: bad numeric argument \"%s\" to option \"%s\"\n",
                 name, arg, opt);
        exit (1);
    }

    return (val);
}



void opt_case (char *opt, char *arg)
{
    optCase = 1;
}

void opt_nocase (char *opt, char *arg)
{
    optCase = 0;
}

void opt_punct (char *opt, char *arg)
{
    optPunct = 1;
}

void opt_nopunct (char *opt, char *arg)
{
    optPunct = 0;
}

void opt_punctFold (char *opt, char *arg)
{
    optPunctFold = 1;
}

void opt_noPunctFold (char *opt, char *arg)
{
    optPunctFold = 0;
}

void opt_status (char *opt, char *arg)
{
    optStatus = 1;
}

void opt_nostatus (char *opt, char *arg)
{
    optStatus = 0;
}

void opt_inFile (char *opt, char *arg)
{
    optInFile = arg;
}

void opt_outFile (char *opt, char *arg)
{
    optOutFile = arg;
}

void opt_dump (char *opt, char *arg)
{
    if (strcmp (arg, "not") == 0)
        optDumpFile = "";
    else
        optDumpFile = arg;
}

void opt_depth (char *opt, char *arg)
{
    optDepth = longArg (opt, arg);

    if (optDepth < 0)
    {
        fprintf (stderr, "%s: option \"%s\" requires a non-negative number\n",
                 name, opt);
        exit (1);
    }
}

void opt_words (char *opt, char *arg)
{
    optWords = longArg (opt, arg);

    if (optWords <= 0)
    {
        fprintf (stderr, "%s: option \"%s\" requires a positive number\n",
                 name, opt);
        exit (1);
    }
}

void opt_seed (char *opt, char *arg)
{
    if (strcmp (arg, "random") == 0)
        optSeed = (long) time (0) + (long) getpid ();
    else
        optSeed = longArg (opt, arg);
}

void opt_startWord (char *opt, char *arg)
{
    if (strcmp (arg, "random") == 0)
        optStartWord = 0;
    else
    {
        optStartWord = longArg (opt, arg);

        if (optStartWord <= 0)
        {
            fprintf (stderr, "%s: option \"%s\" requires a positive number "
                     "or \"random\"\n",
                     name, opt);
            exit (1);
        }
    }
}



typedef void Action (char *, char *);

typedef struct
{
    char *optName;
    int hasArg;
    char *deflt;
    Action *action;
    char *argDesc;
    char *desc;
}
Option;

void opt_help (char *, char *);

Option opts[] = 
{
    {"-case",      0, "n",    opt_case,
         "", "case sensitive"},
    {"-nocase",    0, "y",    opt_nocase,
         "", "case insensitive"},
    {"-punct",     0, "y",    opt_punct,
         "", "punctuation counts as words"},
    {"-nopunct",   0, "n",    opt_nopunct,
         "", "punctuation doesn't count as words"},
    {"-cassidy",   0, "n",    opt_punctFold,
         "", "count most punctuation as word-characters"},
    {"-nocassidy", 0, "y",    opt_noPunctFold,
         "", "don't count most punctuation as word-characters"},
    {"-depth",     1, "1",    opt_depth, 
         "num", "Markoff depth"},
    {"-words",     1, "1000", opt_words,
         "num", "number of words to spew out"},
    {"-seed",      1, "random", opt_seed,
         "num", "random number seed"},
    {"-start",     1, "random", opt_startWord,
         "num", "start word"},
    {"-in",        1, "-",    opt_inFile,
         "name", "input file"},
    {"-out",       1, "-",    opt_outFile,
         "name", "output file"},
    {"-dump",      1, "not",  opt_dump,
         "name", "file to dump tables to (\"not\" for no dump)"},
    {"-status",    0, "y",    opt_status,
         "", "print status info as we chug"},
    {"-nostatus",  0, "n",    opt_nostatus,
         "", "don't print status info as we chug"},
    {"-h",         0, "n",    opt_help,
         "", "get help"},
    {"-help",      0, "n",    opt_help,
         "", "get help"},
    {0, 0, 0, 0, 0, 0}
};



void defaults (void)
{
    Option *o = opts;

    while (o->optName)
    {
        if (o->hasArg || *o->deflt == 'y')
            o->action (o->optName, o->deflt);
        o++;
    }
}



void opt_help (char *ignore1, char *ignore2)
{
    Option *o = opts;

    fprintf (stderr, "possible options:\n");

    while (o->optName)
    {
        if (o->hasArg)
            fprintf (stderr, "  %s %s -- %s [default \"%s\"]\n", 
                     o->optName, o->argDesc, o->desc, o->deflt);
        else
            fprintf (stderr, "  %s -- %s%s\n", o->optName, o->desc,
                     *o->deflt == 'y' ? " [the default]" : "");
        o++;
    }

    fprintf (stderr, "default: %s", name);

    o = opts;
    while (o->optName)
    {
        if (o->hasArg)
            fprintf (stderr, " %s %s", o->optName, o->deflt);
        else if (*o->deflt == 'y')
            fprintf (stderr, " %s", o->optName);
        o++;
    }

    fprintf (stderr, "\n");
    exit (0);
}



void parseOpts (int argc, char *argv[])
{
    int i = 1;

    name = argv[0];
    defaults ();

    while (i < argc)
    {
        char *ostr = argv[i];
        Option *o = opts;

        while (o->optName)
        {
            if (strcmp (o->optName, ostr) == 0)
                break;
            o++;
        }

        if (! o->optName)
        {
            fprintf (stderr, "%s: unrecognized option \"%s\"\n", 
                     name, ostr);
            opt_help (0, 0);
            exit (1);
        }

        if (o->hasArg)
        {
            if (i == argc - 1)
            {
                fprintf (stderr, "%s: missing argument for option \"%s\"\n",
                         name, o->optName);
                exit (1);
            }

            i++;
            o->action (o->optName, argv[i]);
        }
        else
            o->action (o->optName, "y");

        i++;
    }
}



/* ------------------------------------------------------------------------- */
/* Mainness                                                                  */
/* ------------------------------------------------------------------------- */

FILE *myfopen (char *name, char *mode)
{
    if (strcmp (name, "-") == 0)
        return ((*mode == 'r') ? stdin : stdout);

    return (fopen (name, mode));
}



int main (int argc, char *argv[])
{
    FILE *inFile;
    FILE *outFile;
    FILE *dumpFile;

    parseOpts (argc, argv);

    /* seed the random-number generator */
    srand48 (optSeed);

    /* do the Cassidy Curtis punctuation option */
    if (optPunctFold)
        punctFold ();

    inFile = myfopen (optInFile, "r");
    if (! inFile)
    {
        fprintf (stderr, "%s: trouble opening input file \"%s\"\n", 
                 name, optInFile);
        exit (1);
    }

    outFile = myfopen (optOutFile, "w");
    if (! outFile)
    {
        fprintf (stderr, "%s: trouble opening output file \"%s\"\n", 
                 name, optOutFile);
        exit (1);
    }

    processFile (inFile);

    if (inFile != stdin) fclose (inFile);

    if (*optDumpFile)
    {
        dumpFile = myfopen (optDumpFile, "w");
        if (! dumpFile)
        {
            fprintf (stderr, "%s: trouble opening dump file \"%s\"\n", 
                     name, optDumpFile);
            exit (1);
        }

        dumpInfo (dumpFile);
        if (dumpFile != stdout) fclose (dumpFile);
    }

    if (optStartWord == 0)
        optStartWord = lrand48 () % numWNums;
    else
        optStartWord--;

    markoff (optDepth, optWords, optStartWord, outFile);

    return (0);
}

