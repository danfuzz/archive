/**
 * A little program to randomly permute the order of the lines of stdin
 * to stdout.
 */

#include <stdio.h>
#include <stdlib.h>
#include <sys/time.h>
#include <unistd.h>

#define READ_SIZE (1000000)

typedef struct
{
    int count;
    char **lines;
}
Lines;

static char *readAll (FILE *in)
{
    int bufSize = READ_SIZE + 1;
    char *buf = malloc (bufSize);
    char *inBuf = buf;

    for (;;)
    {
	int count = fread (buf, 1, READ_SIZE, in);
	if (count < READ_SIZE)
	{
	    buf[count] = '\0';
	    break;
	}
	buf = realloc (buf, bufSize + READ_SIZE);
	inBuf += READ_SIZE;
    }

    return buf;
}

static void splitLines (char *buf, Lines *result)
{
    int linesSize = 2000;
    char **lines = malloc (linesSize * sizeof (char *));
    int count = 0;

    for (;;)
    {
	if (*buf == '\0')
	{
	    break;
	}
	if (count == linesSize)
	{
	    linesSize = linesSize * 3 / 2;
	    lines = realloc (lines, linesSize * sizeof (char *));
	}
	lines[count] = buf;
	count++;
	while ((*buf != '\n') && (*buf != '\0'))
	{
	    buf++;
	}
	if (*buf == '\n')
	{
	    *buf = '\0';
	    buf++;
	}
    }

    result->count = count;
    result->lines = lines;
}

static void doit (FILE *in, FILE *out)
{
    char *buf = readAll (in);
    Lines linerec;
    int count;
    char **lines;

    splitLines (buf, &linerec);
    count = linerec.count;
    lines = linerec.lines;

    while (count > 0)
    {
	int which = random () % count;
	fprintf (out, "%s\n", lines[which]);
	lines[which] = lines[0];
	lines++;
	count--;
    }
}

static void randomize (void)
{
    struct timeval tv;
    gettimeofday (&tv, NULL);
    srandom (tv.tv_sec ^ tv.tv_usec);
}

int main (int argc, char *argv[])
{
    randomize ();
    doit (stdin, stdout);
}
