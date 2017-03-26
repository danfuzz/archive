#include "desuf.h"
#include <string.h>
#include <stdio.h>

class Stop
{
  public:
    int len;
    char *old;
    char *neo;
};

Stop a_stop[] = {
  { 1, "A", "" },
  { 2, "AN", "" },
  { 3, "ALL", "" },
  { 3, "AND", "" },
  { 0, 0, 0 }
};
Stop b_stop[] = { { 0, 0, 0 } };
Stop c_stop[] = { 
  { 8, "CHILDREN", "CHILD" },
  { 0, 0, 0 }
};
Stop d_stop[] = { 
  { 2, "DO", "" },
  { 0, 0, 0 }
};
Stop e_stop[] = { { 0, 0, 0 } };
Stop f_stop[] = { { 0, 0, 0 } };
Stop g_stop[] = { { 0, 0, 0 } };
Stop h_stop[] = { { 0, 0, 0 } };
Stop i_stop[] = { { 0, 0, 0 } };
Stop j_stop[] = { { 0, 0, 0 } };
Stop k_stop[] = { { 0, 0, 0 } };
Stop l_stop[] = { { 0, 0, 0 } };
Stop m_stop[] = { { 0, 0, 0 } };
Stop n_stop[] = { { 0, 0, 0 } };
Stop o_stop[] = { 
  { 2, "OR", "" },
  { 0, 0, 0 }
};
Stop p_stop[] = { { 0, 0, 0 } };
Stop q_stop[] = { { 0, 0, 0 } };
Stop r_stop[] = { { 0, 0, 0 } };
Stop s_stop[] = { 
  { 4, "SOME", "" },
  { 0, 0, 0 }
};
Stop t_stop[] = {
  { 3, "THE", "" },
  { 4, "THEN", "" },
  { 4, "THAN", "" },
  { 4, "THIS", "" },
  { 4, "THAT", "" },
  { 6, "THEORY", "THEORY" },
  { 0, 0, 0 }
};
Stop u_stop[] = { 
  { 2, "UL", "" }, /* RTF underline directive */
  { 0, 0, 0 }
};
Stop v_stop[] = { { 0, 0, 0 } };
Stop w_stop[] = { { 0, 0, 0 } };
Stop x_stop[] = { { 0, 0, 0 } };
Stop y_stop[] = { { 0, 0, 0 } };
Stop z_stop[] = { { 0, 0, 0 } };

Stop *stops[] = { a_stop, b_stop, c_stop, d_stop, e_stop, 
		  f_stop, g_stop, h_stop, i_stop, j_stop,
		  k_stop, l_stop, m_stop, n_stop, o_stop,
		  p_stop, q_stop, r_stop, s_stop, t_stop,
		  u_stop, v_stop, w_stop, x_stop, y_stop, z_stop };

class Suffix
{
  public:
    int len;
    int dupdel;
    int onlyfirst;
    char *s;
};

Suffix a_suff[] = { 
  { 3, 0, 0, "SIA" },
  { 2, 0, 0, "IA" },
  { 0, 0, 0, 0 }
};
Suffix b_suff[] = { { 0, 0, 0, 0 } };
Suffix c_suff[] = { 
  { 3, 0, 0, "TIC" },
  { 2, 0, 0, "IC" },
  { 0, 0, 0, 0 }
};
Suffix d_suff[] = {
  { 3, 0, 0, "OID" },
  { 2, 1, 0, "ED" },
  { 0, 0, 0, 0 }
};
Suffix e_suff[] = {
  { 6, 0, 0, "IZABLE" },
  { 5, 0, 0, "ESQUE" },
  { 5, 0, 0, "ATIVE" },
  { 5, 0, 0, "ITIVE" },
  { 4, 1, 0, "ABLE" },
  { 4, 1, 0, "IBLE" },
  { 4, 1, 0, "ANCE" },
  { 4, 1, 0, "ENCE" },
  { 4, 0, 0, "LIKE" },
  { 3, 0, 0, "EME" },
  { 3, 1, 0, "AGE" },
  { 3, 0, 0, "ATE" },
  { 3, 0, 0, "ITE" },
  { 3, 0, 0, "IZE" },
  { 3, 0, 0, "IVE" },
  { 3, 0, 0, "INE" },
  { 3, 0, 0, "ESE" },
  { 3, 0, 0, "ASE" },
  { 3, 0, 0, "URE" },
  { 2, 0, 1, "EE" },
  { 1, 0, 1, "E" },
  { 0, 0, 0, 0 }
};
Suffix f_suff[] = { { 0, 0, 0, 0 } };
Suffix g_suff[] = {
  { 3, 1, 0, "ING" },
  { 0, 0, 0, 0 }
};
Suffix h_suff[] = {
  { 3, 0, 0, "ISH" },
  { 0, 0, 0, 0 }
};
Suffix i_suff[] = { { 0, 0, 0, 0 } };
Suffix j_suff[] = { { 0, 0, 0, 0 } };
Suffix k_suff[] = { { 0, 0, 0, 0 } };
Suffix l_suff[] = {
  { 4, 0, 0, "ICAL" },
  { 3, 0, 0, "FUL" },
  { 3, 0, 0, "IAL" },
  { 2, 1, 0, "AL" },
  { 0, 0, 0, 0 }
};
Suffix m_suff[] = {
  { 3, 0, 0, "ISM" },
  { 0, 0, 0, 0 }
};
Suffix n_suff[] = {
  { 7, 0, 0, "IZATION" },
  { 7, 0, 0, "INATION" },
  { 7, 0, 0, "ICATION" },
  { 6, 0, 0, "SATION" },
  { 5, 0, 0, "ATION" },
  { 4, 0, 0, "ISON" },
  { 3, 0, 0, "ION" },
  { 3, 0, 0, "IAN" },
  { 2, 0, 1, "EN" },
  { 2, 0, 0, "AN" },
  { 0, 0, 0, 0 }
};
Suffix o_suff[] = { { 0, 0, 0, 0 } };
Suffix p_suff[] = {
  { 4, 0, 0, "SHIP" },
  { 0, 0, 0, 0 }
};
Suffix q_suff[] = { { 0, 0, 0, 0 } };
Suffix r_suff[] = { 
  { 4, 0, 0, "IZER" },
  { 2, 1, 0, "AR" },
  { 2, 1, 0, "ER" },
  { 2, 1, 0, "OR" },
  { 0, 0, 0, 0 }
};
Suffix s_suff[] = {
  { 5, 0, 0, "INESS" },
  { 4, 0, 0, "ITIS" },
  { 4, 0, 0, "OSIS" },
  { 4, 0, 0, "LESS" },
  { 4, 0, 0, "NESS" },
  { 4, 0, 0, "IOUS" },
  { 3, 0, 1, "IES" },
  { 3, 0, 0, "IUS" },
  { 3, 0, 0, "OUS" },
  { 2, 0, 1, "ES" },
  { 2, 0, 0, "US" },
  { 1, 0, 1, "S" },
  { 0, 0, 0, 0 }
};
Suffix t_suff[] = {
  { 5, 0, 0, "AMENT" },
  { 4, 0, 0, "MENT" },
  { 3, 0, 0, "ANT" },
  { 3, 0, 0, "ENT" },
  { 3, 0, 0, "IST" },
  { 3, 0, 0, "EST" },
  { 0, 0, 0 }
};
Suffix u_suff[] = { { 0, 0, 0, 0 } };
Suffix v_suff[] = { { 0, 0, 0, 0 } };
Suffix w_suff[] = { { 0, 0, 0, 0 } };
Suffix x_suff[] = { { 0, 0, 0, 0 } };
Suffix y_suff[] = {
  { 6, 0, 1, "IOSITY" },
  { 5, 0, 1, "IVITY" },
  { 5, 0, 1, "OSITY" },
  { 4, 0, 1, "ANCY" },
  { 4, 0, 1, "ENCY" },
  { 3, 0, 1, "ARY" },
  { 3, 0, 1, "ITY" },
  { 3, 0, 0, "ILY" },
  { 2, 0, 0, "SY" },
  { 2, 0, 0, "LY" },
  { 1, 1, 1, "Y" },
  { 0, 0, 0 }
};
Suffix z_suff[] = { { 0, 0, 0, 0 } };

Suffix *sufs[] = { a_suff, b_suff, c_suff, d_suff, e_suff,
		   f_suff, g_suff, h_suff, i_suff, j_suff,
		   k_suff, l_suff, m_suff, n_suff, o_suff,
		   p_suff, q_suff, r_suff, s_suff, t_suff,
		   u_suff, v_suff, w_suff, x_suff, y_suff, z_suff };


void deaccent (char *word)
{
  char *out = word;

  while (*word) {
    if (*word == '<') {
      word++;
      if (*word) word++;
    }
    else *(out++) = *(word++);
  }
  *out = '\0';
}


static int do_stop (char *word, int len)
{
  int snum = (int) (*word - 'A');
  if (snum < 0 || snum > 25) return (0);
  Stop *s = stops[snum];

  while (s->len) {
    if (s->len == len && strcmp (word, s->old) == 0) {
      strcpy (word, s->neo);
      return (1);
    }
    s++;
  }

  return (0);
}



void desuffix (char *word)
{
  deaccent (word);

  int len = strlen (word);
  int maxslen = len - 3;
  char *wordend = word + len;
  int delled = 1;
  int isfirst = 1;

  if (do_stop (word, len)) return;

  while (delled && maxslen > 0) {
    int snum = (int) (word[len-1] - 'A');
    if (snum < 0 || snum > 25) break;
    Suffix *s = sufs[snum];

    delled = 0;
    while (s->len) {
      if (s->len <= maxslen && (isfirst || !s->onlyfirst) && 
	  strcmp (s->s, wordend - s->len) == 0) {
	len -= s->len;
	wordend -= s->len;
	maxslen -= s->len;
	*wordend = '\0';
	delled = 1;
	if (s->dupdel && maxslen >= 1 && wordend[-2] == wordend[-1]) {
	  len--;
	  wordend--;
	  maxslen--;
	  *wordend = '\0';
	}
	break;
      }
      s++;
    }
    isfirst = 0;
  }
}
