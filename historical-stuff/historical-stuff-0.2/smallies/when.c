#include <stdio.h>
#include <sys/types.h>
#include <time.h>
#include <sys/time.h>

char *chars[16][11] = {
  { "   #######   ",
    "  ##     ##  ",
    " ##       ## ",
    "##         ##",
    "##         ##",
    "##   ###   ##",
    "##         ##",
    "##         ##",
    " ##       ## ",
    "  ##     ##  ",
    "   #######   " },
  { "      ##     ",
    "     ###     ",
    "    ####     ",
    "   ## ##     ",
    "      ##     ",
    "      ##     ",
    "      ##     ",
    "      ##     ",
    "      ##     ",
    "      ##     ",
    "   ########  " },
  { "  ########   ",
    " ##      ##  ",
    "##        ## ",
    "           ##",
    "           ##",
    "          ## ",
    "         ##  ",
    "       ##    ",
    "     ##      ",
    "   ##        ",
    "#############" },
  { "  #########  ",
    " ##       ## ",
    "##         ##",
    "           ##",
    "          ## ",
    "    #######  ",
    "          ## ",
    "           ##",
    "##         ##",
    " ##       ## ",
    "  #########  " },
  { "        ##   ",
    "       ###   ",
    "      ####   ",
    "     ## ##   ",
    "    ##  ##   ",
    "   ##   ##   ",
    "  ##    ##   ",
    " ########### ",
    "        ##   ",
    "        ##   ",
    "        ##   " },
  { "#############",
    "##           ",
    "##           ",
    "##           ",
    "###########  ",
    "          ## ",
    "           ##",
    "           ##",
    "##        ## ",
    " ##      ##  ",
    "  ########   " },
  { "  ########   ",
    " ##          ",
    "##           ",
    "##           ",
    "##########   ",
    "##       ##  ",
    "##        ## ",
    "##         ##",
    "##         ##",
    " ##       ## ",
    "  #########  " },
  { "#############",
    "           ##",
    "           ##",
    "           ##",
    "          ## ",
    "         ##  ",
    "        ##   ",
    "       ##    ",
    "       ##    ",
    "       ##    ",
    "       ##    " },
  { "  #########  ",
    " ##       ## ",
    "##         ##",
    "##         ##",
    " ##       ## ",
    "  #########  ",
    " ##       ## ",
    "##         ##",
    "##         ##",
    " ##       ## ",
    "  #########  " },
  { "  #########  ",
    " ##       ## ",
    "##         ##",
    "##         ##",
    " ##        ##",
    "  ##       ##",
    "   ##########",
    "           ##",
    "           ##",
    "          ## ",
    "   ########  " },
  { "        ",
    "        ",
    "        ",
    "        ",
    "        ",
    "        ",
    "        ",
    "        ",
    "        ",
    "        ",
    "        " },
  { "   ##   ",
    "  ###   ",
    " ####   ",
    "## ##   ",
    "   ##   ",
    "   ##   ",
    "   ##   ",
    "   ##   ",
    "   ##   ",
    "   ##   ",
    "########" },
  { "    ",
    "    ",
    " ## ",
    "####",
    " ## ",
    "    ",
    " ## ",
    "####",
    " ## ",
    "    ",
    "    " },
  { "        ",
    "        ",
    " #####  ",
    "     ## ",
    "     ## ",
    " ###### ",
    "##   ## ",
    "##   ## ",
    " #######",
    "        ",
    "        " },
  { "        ",
    "        ",
    " ###### ",
    " ##   ##",
    " ##   ##",
    " ##   ##",
    " ##   ##",
    " ##   ##",
    " ###### ",
    " ##     ",
    "###     " },
  { "           ",
    "           ",
    "## ##  ##  ",
    " ######### ",
    " ##  ##  ##",
    " ##  ##  ##",
    " ##  ##  ##",
    " ##  ##  ##",
    " ##  ##  ##",
    "           ",
    "           " }
};

#define TENHOUR (10)
#define COLON   (12)
#define AP      (13)
#define EM      (15)

char *days[7] = {
  "Sunday",
  "Monday",
  "Tuesday",
  "Wednesday",
  "Thursday",
  "Friday",
  "Saturday"
};

char *months[12] = {
  "January",
  "February",
  "March",
  "April",
  "May",
  "June",
  "July",
  "August",
  "September",
  "October",
  "November",
  "December"
};

char *dates[31] = {
  "1st",
  "2nd",
  "3rd",
  "4th",
  "5th",
  "6th",
  "7th",
  "8th",
  "9th",
  "10th",
  "11th",
  "12th",
  "13th",
  "14th",
  "15th",
  "16th",
  "17th",
  "18th",
  "19th",
  "20th",
  "21st",
  "22nd",
  "23rd",
  "24th",
  "25th",
  "26th",
  "27th",
  "28th",
  "29th",
  "30th",
  "31st"
};

char *areas[4] = {
  "in the wee hours of the morning",
  "in the morning",
  "in the afternoon",
  "at night"
};
  	   
int main (int argc, char *argv[])
{
  time_t clock = time (0);
  struct tm *t = localtime (&clock);
  char *area;
  int i;
  int h1, h2, m1, m2, ap;
  int hour;

  if      (t->tm_hour == 0) hour = 12;
  else if (t->tm_hour > 12) hour = t->tm_hour - 12;
  else                      hour = t->tm_hour;

  if      (t->tm_hour < 6)  area = areas[0];
  else if (t->tm_hour < 12) area = areas[1];
  else if (t->tm_hour < 18) area = areas[2];
  else                      area = areas[3];

  printf ("\n\n\n\n\nToday is %s, the %s of %s. It is %d:%02d:%02d %s.\n\n",
	  days[t->tm_wday], dates[t->tm_mday - 1], months[t->tm_mon], hour,
	  t->tm_min, t->tm_sec, area);

  h1 = hour / 10;
  h2 = hour % 10;
  m1 = t->tm_min / 10;
  m2 = t->tm_min % 10;
  ap = t->tm_hour >= 12;

  for (i = 0; i < 11; i++) {
    printf ("%s %s %s %s %s %s %s\n", chars[TENHOUR + h1][i], chars[h2][i], chars[COLON][i],
	    chars[m1][i], chars[m2][i], chars[AP + ap][i], chars[EM][i]);
  }

  printf ("\n\n\n\n\n");
}
