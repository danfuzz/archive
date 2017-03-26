/* --------------------------------------------------------------------------------------------- */
/* DDD: Dan's Dudely Dump                                                                        */
/*                                                                                               */
/* by Dan Bornstein                                                                              */
/* --------------------------------------------------------------------------------------------- */

#include <stdio.h>
#include <ctype.h>
#include <memory.h>
#include <values.h>

static char *helpstr = "\
Options\n\
  -w num      Set line \"width\" to <num> bytes\n\
  -s num      Set start to <num> bytes from the beginning of file\n\
  -e num      Set the end to <num> bytes from the beginning of file\n\
  -v          Set verbosity; repeat lines even if identical\n\
  -b          Set big-endianness for multibyte numerics (default)\n\
  -l          Set little-endianness for multibyte numerics\n\
  -S style    Set the style of dump for easy format specification (see below\n\
                for list of styles). Specifying -S overrides any format given.\n\
  -A          Suppress ASCII sidebar for easy format specification\n\
  -B num      Additional record width is a byte at offset <num> from the\n\
                beginning of the record (if -w given, it means extra to add on
                to value)\n\
  -W num      Additional record width is a word at offset <num> from the\n\
                beginning of the record (if -w given, it means extra to add on\n\
                to value)\n\
  -L num      Additional record width is a long at offset <num> from the\n\
                beginning of the record (if -w given, it means extra to add on\n\
                to value)\n\
  -M num      Multiplier for the record width obtained (used in conjunction with\n\
                -B, -W, or -L)\n\
  -o opt      Od option; simulates the given od option; handles everything but -s,
                -w, and -v (-w and -v are handled without -o); see od for more details\n\
\n\
Styles:\n\
  hexs        hex sequence, no spacing\n\
  hexs.1      same as hexs\n\
  hexs.2      hex sequence, spacing after every other byte\n\
  hexs.4      hex sequence, spacing after every fourth byte\n\
  hex         interpret as hex bytes\n\
  hex.1       same as hex\n\
  hex.2       interpret as hex words\n\
  hex.4       interpret as hex longs\n\
  oct         interpret as octal bytes\n\
  oct.1       same as oct\n\
  oct.2       interpret as octal words\n\
  oct.4       interpret as octal longs\n\
  bin         interpret as binary bytes\n\
  dec         interpret as signed decimal bytes\n\
  dec.1       same as dec\n\
  dec.2       interpret as signed decimal words\n\
  dec.4       interpret as signed decimal longs\n\
  udec        interpret as unsigned decimal bytes\n\
  udec.1      same as udec\n\
  udec.2      interpret as unsigned decimal words\n\
  udec.4      interpret as unsigned decimal longs\n\
  0udec       like udec, but pad with 0s (zeros)\n\
  0udec.1     same as 0udec
  0udec.2     like udec.2, but pad with 0s (zeros)\n\
  0udec.4     like udec.4, but pad with 0s (zeros)\n\
  ascii       interpret as ASCII dump\n\
  char        same as ascii\n\
  oascii      interpret as ASCII dump, displaying C-style escapes or octal for\n\
                non-printing characters\n\
  ochar       same as oascii\n\
  xascii      like oascii, but use hex and not octal\n\
  xchar       same as xascii\n\
  nascii      like oascii, but ignore high bit and print char names\n\
  nchar       same as nascii\n\
  string      interpret as null-terminated string\n\
  float       interpret 4 bytes as a float\n\
  double      interpret 8 bytes as a double\n\
  hexloc      print offset into file in hex (long)\n\
  octloc      print offset into file in octal (long)\n\
  decloc      print offset into file as decimal (long)\n\
  shexloc     print offset into file in hex (short)\n\
  soctloc     print offset into file in octal (short)\n\
  sdecloc     print offset into file as decimal (short)\n\
  nl          output a newline\n\
  n           same as nl\n\
  tab         tab to the given column (if not already past)\n\
  space       add the given number of spaces\n\
\n\
Format:\n\
  The format is printed as-is, except it may be interspersed with \"commands\"\n\
  of the form \"{style start len}\", where <style> is one of the above styles,\n\
  <start> is the byte offset to start at (defaults to 0) and <len> is the\n\
  number of items to do in the given style (defaults to do as much as\n\
  possible). Two open curly braces in a row (\"{{\") means just output an open\n\
  curly brace.\n\
";


typedef void FormatFunc (int curpos, char *memory, int len, char *buffer);

typedef struct
{
  char *style;
  FormatFunc *func;
  int width;
}
StyleEntry;

typedef struct
{
  FormatFunc *func;
  int width;
  int start;
  int len;
}
FormatEntry;


#define LITTLE (0)
#define BIG    (1)
int endianness;
int normal_endianness;

static
void figure_endianness (void)
{
  char minibuf[2] = "\x01\x02";
  if (*(short *) minibuf == 0x0102) normal_endianness = BIG;
  else normal_endianness = LITTLE;
}


static
double endianize_8 (double input)
{
  union {
    char c[8];
    double d;
  } in, out;

  if (endianness == normal_endianness) return (input);
  in.d = input;
  out.c[0] = in.c[7];
  out.c[1] = in.c[6];
  out.c[2] = in.c[5];
  out.c[3] = in.c[4];
  out.c[4] = in.c[3];
  out.c[5] = in.c[2];
  out.c[6] = in.c[1];
  out.c[7] = in.c[0];
  return (out.d);
}



static
float endianize_4f (float input)
{
  union {
    char c[4];
    float f;
  } in, out;

  if (endianness == normal_endianness) return (input);
  in.f = input;
  out.c[0] = in.c[3];
  out.c[1] = in.c[2];
  out.c[2] = in.c[1];
  out.c[3] = in.c[0];
  return (out.f);
}



static
unsigned int endianize_4 (unsigned int input)
{
  union {
    char c[4];
    unsigned int i;
  } in, out;

  if (endianness == normal_endianness) return (input);
  in.i = input;
  out.c[0] = in.c[3];
  out.c[1] = in.c[2];
  out.c[2] = in.c[1];
  out.c[3] = in.c[0];
  return (out.i);
}



static
unsigned short endianize_2 (unsigned short input)
{
  union {
    char c[2];
    unsigned short i;
  } in, out;

  if (endianness == normal_endianness) return (input);
  in.i = input;
  out.c[0] = in.c[1];
  out.c[1] = in.c[0];
  return (out.i);
}



#define UMEM1(m) (*(unsigned char *) (m))
#define UMEM2(m) (endianize_2 (*(unsigned short *) (m)))
#define UMEM4(m) (endianize_4 (*(unsigned int *) (m)))

#define SMEM1(m) (*(signed char *) (m))
#define SMEM2(m) ((short) endianize_2 (*(unsigned short *) (m)))
#define SMEM4(m) ((int) endianize_4 (*(unsigned int *) (m)))

#define FMEM4(m) (endianize_4f (*(float *) (m)))
#define DMEM8(m) (endianize_8 (*(double *) (m)))



static
void f_hexs (int curpos, char *memory, int len, char *buffer)
{
  while (len) {
    sprintf (buffer, "%02x", UMEM1 (memory));
    while (*buffer) buffer++;
    memory++;
    len--;
  }
}



static
void f_hexs2 (int curpos, char *memory, int len, char *buffer)
{
  int count = 0;
  while (len) {
    sprintf (buffer, "%02x%s", UMEM1 (memory), (len == 1 || (count & 1) != 1) ? "" : " ");
    while (*buffer) buffer++;
    memory++;
    count++;
    len--;
  }
}



static
void f_hexs4 (int curpos, char *memory, int len, char *buffer)
{
  int count = 0;
  while (len) {
    sprintf (buffer, "%02x%s", UMEM1 (memory), (len == 1 || (count & 3) != 3) ? "" : " ");
    while (*buffer) buffer++;
    memory++;
    count++;
    len--;
  }
}



static
void f_hex (int curpos, char *memory, int len, char *buffer)
{
  while (len) {
    sprintf (buffer, "%02x%s", UMEM1 (memory), len == 1 ? "" : " ");
    while (*buffer) buffer++;
    memory++;
    len--;
  }
}



static
void f_hex2 (int curpos, char *memory, int len, char *buffer)
{
  while (len) {
    sprintf (buffer, "%04x%s", UMEM2 (memory), len == 1 ? "" : " ");
    while (*buffer) buffer++;
    memory += 2;
    len--;
  }
}



static
void f_hex4 (int curpos, char *memory, int len, char *buffer)
{
  while (len) {
    sprintf (buffer, "%08x%s", UMEM4 (memory), len == 1 ? "" : " ");
    while (*buffer) buffer++;
    memory += 4;
    len--;
  }
}



static
void f_oct (int curpos, char *memory, int len, char *buffer)
{
  while (len) {
    sprintf (buffer, "%03o%s", UMEM1 (memory), len == 1 ? "" : " ");
    while (*buffer) buffer++;
    memory++;
    len--;
  }
}



static
void f_oct2 (int curpos, char *memory, int len, char *buffer)
{
  while (len) {
    sprintf (buffer, "%06o%s", UMEM2 (memory), len == 1 ? "" : " ");
    while (*buffer) buffer++;
    memory += 2;
    len--;
  }
}



static
void f_oct4 (int curpos, char *memory, int len, char *buffer)
{
  while (len) {
    sprintf (buffer, "%011o%s", UMEM4 (memory), len == 1 ? "" : " ");
    while (*buffer) buffer++;
    memory += 4;
    len--;
  }
}



static
void f_bin (int curpos, char *memory, int len, char *buffer)
{
  while (len) {
    int b = UMEM1 (memory);
    int bit;
    for (bit = 7; bit >= 0; bit--)
      *(buffer++) = (b & (1 << bit)) ? '1' : '0';
    if (len != 1) *(buffer++) = ' ';
    memory++;
    len--;
  }
  *buffer = '\0';
}



static
void f_dec (int curpos, char *memory, int len, char *buffer)
{
  while (len) {
    sprintf (buffer, "%4d%s", SMEM1 (memory), len == 1 ? "" : " ");
    while (*buffer) buffer++;
    memory++;
    len--;
  }
}



static
void f_dec2 (int curpos, char *memory, int len, char *buffer)
{
  while (len) {
    sprintf (buffer, "%6d%s", SMEM2 (memory), len == 1 ? "" : " ");
    while (*buffer) buffer++;
    memory += 2;
    len--;
  }
}



static
void f_dec4 (int curpos, char *memory, int len, char *buffer)
{
  while (len) {
    sprintf (buffer, "%11d%s", SMEM4 (memory), len == 1 ? "" : " ");
    while (*buffer) buffer++;
    memory += 4;
    len--;
  }
}



static
void f_udec (int curpos, char *memory, int len, char *buffer)
{
  while (len) {
    sprintf (buffer, "%3u%s", UMEM1 (memory), len == 1 ? "" : " ");
    while (*buffer) buffer++;
    memory++;
    len--;
  }
}



static
void f_udec2 (int curpos, char *memory, int len, char *buffer)
{
  while (len) {
    sprintf (buffer, "%5u%s", UMEM2 (memory), len == 1 ? "" : " ");
    while (*buffer) buffer++;
    memory += 2;
    len--;
  }
}



static
void f_udec4 (int curpos, char *memory, int len, char *buffer)
{
  while (len) {
    sprintf (buffer, "%10u%s", UMEM4 (memory), len == 1 ? "" : " ");
    while (*buffer) buffer++;
    memory += 4;
    len--;
  }
}



static
void f_zudec (int curpos, char *memory, int len, char *buffer)
{
  while (len) {
    sprintf (buffer, "%03u%s", UMEM1 (memory), len == 1 ? "" : " ");
    while (*buffer) buffer++;
    memory++;
    len--;
  }
}



static
void f_zudec2 (int curpos, char *memory, int len, char *buffer)
{
  while (len) {
    sprintf (buffer, "%05u%s", UMEM2 (memory), len == 1 ? "" : " ");
    while (*buffer) buffer++;
    memory += 2;
    len--;
  }
}



static
void f_zudec4 (int curpos, char *memory, int len, char *buffer)
{
  while (len) {
    sprintf (buffer, "%010u%s", UMEM4 (memory), len == 1 ? "" : " ");
    while (*buffer) buffer++;
    memory += 4;
    len--;
  }
}



static
void f_ascii (int curpos, char *memory, int len, char *buffer)
{
  while (len) {
    char c = *(memory++) & 0x7f;
    if (c < ' ' || c == '\x7f') c = '.';
    *(buffer++) = c;
    len--;
  }
  *buffer = '\0';
}



static
void f_oascii (int curpos, char *memory, int len, char *buffer)
{
  while (len) {
    int c = (int) *((unsigned char *) memory++);
    if      (c == 0)  strcpy (buffer, " \\0");
    else if (c == 8)  strcpy (buffer, " \\b");
    else if (c == 9)  strcpy (buffer, " \\t");
    else if (c == 10) strcpy (buffer, " \\n");
    else if (c == 12) strcpy (buffer, " \\f");
    else if (c == 13) strcpy (buffer, " \\r");
    else if (c < ' ' || c >= '\x7f') sprintf (buffer, "%03o", c);
    else sprintf (buffer, "  %c", c);
    buffer += 3;
    if (len) *(buffer++) = ' ';
    len--;
  }
  *buffer = '\0';
}



static
void f_xascii (int curpos, char *memory, int len, char *buffer)
{
  while (len) {
    int c = (int) *((unsigned char *) memory++);
    if      (c == 0)  strcpy (buffer, "\\0");
    else if (c == 8)  strcpy (buffer, "\\b");
    else if (c == 9)  strcpy (buffer, "\\t");
    else if (c == 10) strcpy (buffer, "\\n");
    else if (c == 12) strcpy (buffer, "\\f");
    else if (c == 13) strcpy (buffer, "\\r");
    else if (c < ' ' || c >= '\x7f') sprintf (buffer, "%02x", c);
    else sprintf (buffer, " %c", c);
    buffer += 2;
    if (len) *(buffer++) = ' ';
    len--;
  }
  *buffer = '\0';
}



static
void f_nascii (int curpos, char *memory, int len, char *buffer)
{
  static char names[][4] = { "nul", "soh", "stx", "etx", "eot", "enq", "ack", "bel",
		             " bs", " ht", " nl", " vt", " ff", " cr", " so", " si",
		             "dle", "dc1", "dc2", "dc3", "dc4", "nak", "syn", "etb",
		             "can", " em", "sub", "esc", " fs", " gs", " rs", " us",
		             " sp" };

  while (len) {
    int c = (int) *((unsigned char *) memory++) & 0x7f;
    if      (c <= ' ')  strcpy (buffer, names[c]);
    else if (c == 0x7f) strcpy (buffer, "del");
    else sprintf (buffer, "  %c", c);
    buffer += 3;
    if (len) *(buffer++) = ' ';
    len--;
  }
  *buffer = '\0';
}



static
void f_string (int curpos, char *memory, int len, char *buffer)
{
  while (len) {
    char c = *(memory++) & 0x7f;
    if      (c == 0)  break;
    else if (c == 8)  strcpy (buffer, "\\b");
    else if (c == 9)  strcpy (buffer, "\\t");
    else if (c == 10) strcpy (buffer, "\\n");
    else if (c == 12) strcpy (buffer, "\\f");
    else if (c == 13) strcpy (buffer, "\\r");
    else if (c < ' ' || c == '\x7f') *buffer = '\0';
    else {
      *(buffer++) = c;
      *buffer = '\0';
    }
    while (*buffer) buffer++;
    len--;
  }
  *buffer = '\0';
}



static
void f_float (int curpos, char *memory, int len, char *buffer)
{
  while (len) {
    sprintf (buffer, "%14.8g%s", FMEM4 (memory), len == 1 ? "" : " ");
    while (*buffer) buffer++;
    memory += 4;
    len--;
  }
}



static
void f_double (int curpos, char *memory, int len, char *buffer)
{
  while (len) {
    sprintf (buffer, "%21.14g%s", DMEM8 (memory), len == 1 ? "" : " ");
    while (*buffer) buffer++;
    memory += 8;
    len--;
  }
}



static
void f_hexloc (int curpos, char *memory, int len, char *buffer)
{
  sprintf (buffer, "%08x", curpos);
}



static
void f_octloc (int curpos, char *memory, int len, char *buffer)
{
  sprintf (buffer, "%011o", curpos);
}



static
void f_decloc (int curpos, char *memory, int len, char *buffer)
{
  sprintf (buffer, "%010u", curpos);
}



static
void f_shexloc (int curpos, char *memory, int len, char *buffer)
{
  sprintf (buffer, "%04x", curpos & 0xffff);
}



static
void f_soctloc (int curpos, char *memory, int len, char *buffer)
{
  sprintf (buffer, "%07o", curpos & 0xffff);
}



static
void f_sdecloc (int curpos, char *memory, int len, char *buffer)
{
  sprintf (buffer, "%05u", curpos & 0xffff);
}



static
void f_null (int curpos, char *memory, int len, char *buffer)
{
}



#define TAB_NUM (-1)
#define NL_NUM  (-2)
#define SPC_NUM (-3)

static
StyleEntry styles[] = {
  { "tab",     f_null,    TAB_NUM },
  { "nl",      f_null,    NL_NUM  },
  { "n",       f_null,    NL_NUM  },
  { "space",   f_null,    SPC_NUM },
  { "hexs",    f_hexs,    1       },
  { "hexs.1",  f_hexs,    1       },
  { "hexs.2",  f_hexs2,   1       },
  { "hexs.4",  f_hexs4,   1       },
  { "hex",     f_hex,     1       },
  { "hex.1",   f_hex,     1       },
  { "hex.2",   f_hex2,    2       },
  { "hex.4",   f_hex4,    4       },
  { "oct",     f_oct,     1       },
  { "oct.1",   f_oct,     1       },
  { "oct.2",   f_oct2,    2       },
  { "oct.4",   f_oct4,    4       },
  { "bin",     f_bin,     1       },
  { "dec",     f_dec,     1       },
  { "dec.1",   f_dec,     1       },
  { "dec.2",   f_dec2,    2       },
  { "dec.4",   f_dec4,    4       },
  { "udec",    f_udec,    1       },
  { "udec.1",  f_udec,    1       },
  { "udec.2",  f_udec2,   2       },
  { "udec.4",  f_udec4,   4       },
  { "0udec",   f_zudec,   1       },
  { "0udec.1", f_zudec,   1       },
  { "0udec.2", f_zudec2,  2       },
  { "0udec.4", f_zudec4,  4       },
  { "ascii",   f_ascii,   1       },
  { "char",    f_ascii,   1       },
  { "oascii",  f_oascii,  1       },
  { "ochar",   f_oascii,  1       },
  { "xascii",  f_xascii,  1       },
  { "xchar",   f_xascii,  1       },
  { "nascii",  f_nascii,  1       },
  { "nchar",   f_nascii,  1       },
  { "string",  f_string,  1       },
  { "float",   f_float,   4       },
  { "double",  f_double,  8       },
  { "hexloc",  f_hexloc,  1       },
  { "octloc",  f_octloc,  1       },
  { "decloc",  f_decloc,  1       },
  { "shexloc", f_shexloc, 1       },
  { "soctloc", f_soctloc, 1       },
  { "sdecloc", f_sdecloc, 1       },
  { 0,         0,         0       }
};
  


static FormatEntry formatlist[250];
static int formatsize;



static
int interpret_format (char *myname, char *format)
{
  char *out = format;
  char entry[400];
  char style[100];
  char dummy[100];
  int start;
  int len;
  int parmcount;
  char *ine;
  StyleEntry *s;

  formatsize = 0;

  while (*format) {
    while (*format && *format != '{') {
      if (isspace (*format) || *format >= ' ') *(out++) = *format;
      format++;
    }
    if (! *format) break;
    format++;
    if (*format == '{' || *format == '\0') {
      *(out++) = '{';
      if (*format == '{') format++;
      continue;
    }
    *(out++) = '\x01';

    ine = entry;
    while (*format && *format != '}')
      *(ine++) = *(format++);
    *ine = '\0';
    if (*format != '}') {
      fprintf (stderr, "%s: Missing }\n", myname);
      return (1);
    }
    format++;

    start = 0;
    len = -1;
    parmcount = sscanf (entry, "%s %i %i %s", style, &start, &len, &dummy);
    if (parmcount > 3) {
      fprintf (stderr, "%s: Too many parameters in {%s}\n", myname, entry);
      return (1);
    }
    if (start < 0) start = 0;
    if (len < 1) len = -1;

    s = styles;
    while (s->style && strcmp (s->style, style) != 0)
      s++;
    if (! s->style) {
      fprintf (stderr, "%s: Unknown style %s\n", myname, style);
      return (1);
    }

    formatlist[formatsize].func  = s->func;
    formatlist[formatsize].width = s->width;
    formatlist[formatsize].start = start;
    formatlist[formatsize].len   = len;
    formatsize++;
  }

  *out = '\0';

  return (0);
}
    


static
void format_memory (char *format, int curpos, char *memory, int width, char *buffer)
{
  int pos = 0;
  int curtab = 0;
  int s, l, w;
  int maxlen;

  while (*format) {
    if (*format != '\x01') {
      *(buffer++) = *format;
      curtab++;
      if (*format == '\n') curtab = 0;
    }
    else if (pos < formatsize) {
      s = formatlist[pos].start;
      l = formatlist[pos].len;
      w = formatlist[pos].width;
      if (w > 0) {
	maxlen = (width - s) / w;
	if (l == -1 || l > maxlen) l = maxlen;
	if (s <= width && l != 0) {
	  formatlist[pos].func (curpos, memory + s, l, buffer);
	  while (*buffer) {
	    if      (*buffer == '\n') curtab = 0;
	    else if (*buffer == '\b') curtab--;
	    else curtab++;
	    buffer++;
	  }
	}
      }
      else {
	switch (w) {
	  case NL_NUM:
	    *(buffer++) = '\n';
	    curtab = 0;
	    break;
          case SPC_NUM:
	    while (s--) {
	      *(buffer++) = ' ';
	      curtab++;
	    }
	    break;
          case TAB_NUM:
	    while (curtab < s) {
	      *(buffer++) = ' ';
	      curtab++;
	    }
	    break;
	}
      }
      pos++;
    }
    format++;
  }
  *(buffer++) = '\n';
  *buffer = '\0';
}



#define MAX_CHUNK (16384)

static
void process_file (FILE *f, int start, int end, int verbose, char *format, int width, 
		   int recwidloc, int recwidlen, int recwidmult)
{
  int curpos = 0;
  char buffer[32768];
  char memory1[MAX_CHUNK];
  char memory2[MAX_CHUNK];
  char *memory = memory1;
  int width1;
  int width2 = -1;
  int *curwidth = &width1;
  int readwidth = width;
  int starred = 0;

  if (end < start) end = MAXINT;
  if (recwidloc != -1) readwidth = recwidloc + recwidlen;

  if (fseek (f, start, 0) == 0) curpos = start; /* What about if start > size of file??? */
  else { /* non-seekable file */
    while (curpos != start) {
      int dif = start - curpos;
      int add;
      if (dif > MAX_CHUNK) dif = MAX_CHUNK;
      add = fread (memory, 1, dif, f);
      if (add != dif) return; /* start is past end of file! */
      curpos += add;
    }
  }

  while (! feof (f) && curpos < end) {
    *curwidth = fread (memory, 1, readwidth, f);
    if (*curwidth == 0) break;
    if (recwidloc != -1) {
      int addwidth = width;
      switch (recwidlen) {
	case 1:
          addwidth += UMEM1 (memory + recwidloc) * recwidmult;
	  break;
        case 2:
	  addwidth += UMEM2 (memory + recwidloc) * recwidmult;
	  break;
	case 4:
	  addwidth += UMEM4 (memory + recwidloc) * recwidmult;
	  break;
      }
      if (addwidth + *curwidth > MAX_CHUNK) addwidth = MAX_CHUNK - *curwidth;
      *curwidth += fread (memory + readwidth, 1, addwidth, f);
    }
    if (!verbose && width1 == width2 && memcmp (memory1, memory2, width1) == 0) {
      if (! starred) printf ("*\n");
      starred = 1;
    }
    else {
      starred = 0;
      format_memory (format, curpos, memory, *curwidth, buffer);
      printf ("%s", buffer);
    }
    curpos += *curwidth;
    if (memory == memory1) {
      memory = memory2;
      curwidth = &width2;
    }
    else {
      memory = memory1;
      curwidth = &width1;
    }
  }
}



typedef struct {
  char opt;
  char *format;
}
OdFormat;

static OdFormat odform[] = {
  { 'a', "{soctloc}  {nascii}"  },
  { 'b', "{soctloc}  {oct.1}"   },
  { 'B', "{soctloc}  {oct.2}"   },
  { 'c', "{soctloc}  {oascii}"  },
  { 'd', "{soctloc}  {0udec.2}" },
  { 'D', "{soctloc}  {0udec.4}" },
  { 'f', "{soctloc}  {float}"   },
  { 'F', "{soctloc}  {double}"  },
  { 'h', "{soctloc}  {hex.2}"   },
  { 'H', "{soctloc}  {hex.4}"   },
  { 'i', "{soctloc}  {dec.2}"   },
  { 'I', "{soctloc}  {dec.4}"   },
  { 'l', "{soctloc}  {dec.4}"   },
  { 'L', "{soctloc}  {dec.4}"   },
  { 'o', "{soctloc}  {oct.2}"   },
  { 'O', "{soctloc}  {oct.4}"   },
  { 'x', "{soctloc}  {hex.2}"   },
  { 'X', "{soctloc}  {hex.4}"   },
  { 0,   0                     }
};



char *od_form (char opt)
{
  OdFormat *f = odform;

  while (f->opt && f->opt != opt) f++;

  return (f->format);
}



static char *default_format = "{hexloc}: {hexs.2}  |{ascii}|";

int main (int argc, char *argv[])
{
  int arg;
  int done;
  int first;
  
  char *files[200];
  int numfiles = 0;

  int opt_width = -1;
  int opt_start = 0;
  int opt_end = -1;
  int opt_verbose = 0;
  int opt_endian = BIG;
  int opt_ascii = 1;
  int opt_recwidloc = -1;
  int opt_recwidlen;
  int opt_recwidmult = 1;
  char opt_style[200];
  char opt_odopt[200];
  char format[1000];

  opt_style[0] = '\0';
  opt_odopt[0] = '\0';

  figure_endianness ();

  for (arg = 1, done = 0; arg < argc && ! done; arg++) {
    if (argv[arg][0] == '-') {
      int *intset = 0;
      char *strset = 0;
      switch (argv[arg][1]) {
	case 'h':
	  fprintf (stderr, "Format is: %s [options] [files] [-- format]\n%s", argv[0], helpstr);
	  return (0);
	  break;
	case 'w':
	  intset = &opt_width;
	  break;
        case 's':
	  intset = &opt_start;
	  break;
	case 'e':
	  intset = &opt_end;
	  break;
        case 'v':
	  opt_verbose = 1;
	  break;
        case 'b':
	  opt_endian = BIG;
	  break;
        case 'l':
	  opt_endian = LITTLE;
	  break;
        case 'S':
	  strset = opt_style;
	  break;
        case 'A':
	  opt_ascii = 0;
	  break;
        case 'B':
	  opt_recwidlen = 1;
	  intset = &opt_recwidloc;
	  break;
        case 'W':
	  opt_recwidlen = 2;
	  intset = &opt_recwidloc;
	  break;
        case 'L':
	  opt_recwidlen = 4;
	  intset = &opt_recwidloc;
	  break;
	case 'M':
	  intset = &opt_recwidmult;
	  break;
        case 'o':
	  strset = opt_odopt;
	  break;
        case '\0':
	  done = 1;
	  break;
        case '-':
	  done = 1;
	  arg--;
	  break;
        default:
	  fprintf (stderr, "%s: Bad option: %c\n", argv[0], argv[arg][1]);
	  return (1);
      }
      if (intset) {
	int num = 0;
	if (isdigit (argv[arg][2]))
	  sscanf (&argv[arg][2], "%i", &num);
	else if (argv[arg+1] && isdigit (argv[arg+1][0])) {
	  sscanf (argv[arg+1], "%i", &num);
	  arg++;
	}
	else {
	  fprintf (stderr, "%s: Missing argument\n", argv[0]);
	  return (1);
	}
	*intset = num;
      }
      else if (strset) {
	if (argv[arg][2]) strcpy (strset, &argv[arg][2]);
	else if (argv[arg+1]) {
	  arg++;
	  strcpy (strset, argv[arg]);
	}
	else {
	  fprintf (stderr, "%s: Missing argument\n", argv[0]);
	  return (1);
	}
      }
    }
    else {
      arg--;
      done = 1;
    }
  }

  if (opt_end >= 0) opt_end++;
  if (opt_width == -1) {
    if (opt_recwidloc == -1) opt_width = 16;
    else opt_width = 0;
  }
         
  for (; arg < argc && strcmp (argv[arg], "--") != 0; arg++) {
    files[numfiles] = argv[arg];
    numfiles++;
  }

  arg++;
  format[0] = '\0';
  for (first = 1; arg < argc; arg++) {
    if (! first) strcat (format, " ");
    else first = 0;
    strcat (format, argv[arg]);
  }


  if (opt_odopt[0]) {
    char *odf = od_form (opt_odopt[0]);
    if (! odf) {
      fprintf (stderr, "%s: Bad od option: %c\n", argv[0], opt_odopt[0]);
      return (1);
    }
    strcpy (format, odf);
  }
  else if (opt_style[0])
    sprintf (format, "{hexloc}: {%s}%s", opt_style, opt_ascii ? "  |{ascii}|" : "");
  else if (format[0] == '\0') strcpy (format, default_format);
  if (interpret_format (argv[0], format)) return (1);

  endianness = opt_endian;

  if (numfiles == 0) {
    if (isatty (0)) {
      fprintf (stderr, "%s: No point in formatting tty input!\n", argv[0]);
      return (1);
    }
    process_file (stdin, opt_start, opt_end, opt_verbose, format, opt_width, opt_recwidloc,
		  opt_recwidlen, opt_recwidmult);
  }
  else {
    int i;
    FILE *f;
    for (i = 0; i < numfiles; i++) {
      if (numfiles != 1) printf ("%s:\n", files[i]);
      f = fopen (files[i], "r");
      if (! f) fprintf (stderr, "%s: Could not open file %s\n", argv[0], files[i]);
      else {
	process_file (f, opt_start, opt_end, opt_verbose, format, opt_width, opt_recwidloc,
		      opt_recwidlen, opt_recwidmult);
	fclose (f);
      }
    }
  }

  return (0);
}
