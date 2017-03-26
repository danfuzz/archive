#include <limits.h>
#include <stdio.h>
#include <math.h>
#include <memory.h>

#define cg12_var_DEFINED
#include <pixrect/pixrect_hs.h>

#define FIXRAD (128)
#define AWAYDIST (40 * FIXRAD)
#define MAXMOVE (10 * FIXRAD)

#define PTSIZE (6)

typedef struct {
  unsigned char red[256];
  unsigned char green[256];
  unsigned char blue[256];
} colormap;

Pixrect *screen;
colormap cm;

#define setmap(screen,map) pr_putcolormap (screen, 0, 256, (map).red, (map).green, (map).blue)



void groovy_colors (colormap *cm)
{
  int anchors[8];
  unsigned char ared[8], agreen[8], ablue[8];
  char taken[256];
  int numanchors = random () % 5 + 2;
  int i, j;

  for (i = 0; i < numanchors; i++) {
    ared[i] = random () & 0xFF;
    agreen[i] = random () & 0xFF;
    ablue[i] = random () & 0xFF;
  }
  ared[numanchors] = ared[0];
  agreen[numanchors] = agreen[0];
  ablue[numanchors] = ablue[0];

  for (i = 10; i < 246; i++) taken[i] = 0;
  for (i = 0; i < 10; i++) {
    taken[i] = 1;
    taken[i+246] = 1;
  }

  anchors[0] = 0;
  anchors[numanchors] = 255;

  for (i = 1; i < numanchors; i++) {
    int poten;
    do {
      poten = random () % 240 + 10;
    }
    while (taken[poten]);
    anchors[i] = poten;
    for (j = poten - 10; j < poten + 11; j++) taken[j] = 1;
  }

  for (i = 1; i < numanchors - 1; i++) {
    for (j = i + 1; j < numanchors; j++) {
      if (anchors[i] > anchors[j]) {
	int temp = anchors[i];
	anchors[i] = anchors[j];
	anchors[j] = temp;
      }
    }
  }

  for (i = 0; i < numanchors; i++) {
    int low = anchors[i];
    int high = anchors[i+1];
    int range = high - low;
    int red = ared[i] * 65536;
    int green = agreen[i] * 65536;
    int blue = ablue[i] * 65536;
    int redadd = (int) ((ared[i+1] - ared[i]) * 65536) / range;
    int greenadd = (int) ((agreen[i+1] - agreen[i]) * 65536) / range;
    int blueadd = (int) ((ablue[i+1] - ablue[i]) * 65536) / range;
    for (j = low; j <= high; j++) {
      cm->red[j] = red / 65536;
      cm->green[j] = green / 65536;
      cm->blue[j] = blue / 65536;
      red += redadd;
      green += greenadd;
      blue += blueadd;
    }
  }
}



typedef struct
{
  int x;
  int y;
  int g;
  int col;
} point;

point plist[500];
int numpt;
int maxx, maxy;

#define ROTAMT (0.05)
double theSin;
double theCos;

void initgrav (void)
{
  int i;
  
  theSin = sin (ROTAMT);
  theCos = cos (ROTAMT);

  maxx = screen->pr_size.x * FIXRAD - PTSIZE * FIXRAD;
  maxy = screen->pr_size.y * FIXRAD - PTSIZE * FIXRAD;

  for (i = 0; i < numpt; i++) {
    point *p = &plist[i];
    p->x = random () % maxx;
    p->y = random () % maxy;
    p->g = (i + 1) % numpt;
    p->col = (i * 2) % 255 + 1;
  }
}



void switchem (void)
{
  int i;

  for (i = 0; i < numpt; i++) {
    int j = random () % numpt;
    point *p = &plist[i];
    point *q = &plist[j];
    point t;
    memcpy (&t, p, sizeof (point));
    memcpy (p,  q, sizeof (point));
    memcpy (q, &t, sizeof (point));
    p->col = (i * 2) % 255 + 1;
    q->col = (j * 2) % 255 + 1;
    p->g = (i + 1) % numpt;
    q->g = (j + 1) % numpt;
  }

  groovy_colors (&cm);
  cm.red[0] = 0;
  cm.green[0] = 0;
  cm.blue[0] = 0;
  setmap (screen, cm);
}



void movem (void)
{
  int i;
  double xvec, yvec;
  double xtarg, ytarg;
  double dist;

  for (i = 0; i < numpt; i++) {
    point *p = &plist[i];
    point *np = &plist[p->g];
    int n = i-1;
    if (n < 0) n = numpt - 1;

    pr_rop (screen, p->x / FIXRAD, p->y / FIXRAD,
	    PTSIZE, PTSIZE, PIX_SRC | PIX_COLOR (0), 0, 0, 0);

    xvec = (p->x - np->x);
    yvec = (p->y - np->y);
    dist = sqrt (xvec * xvec + yvec * yvec);
    xtarg = np->x + xvec / dist * AWAYDIST;
    ytarg = np->y + yvec / dist * AWAYDIST;

    np = &plist[np->g];
    xvec = (p->x - np->x);
    yvec = (p->y - np->y);
    dist = sqrt (xvec * xvec + yvec * yvec);
    xtarg += np->x + xvec / dist * AWAYDIST * 2;
    ytarg += np->y + yvec / dist * AWAYDIST * 2;

    np = &plist[n];
    xvec = (p->x - np->x);
    yvec = (p->y - np->y);
    dist = sqrt (xvec * xvec + yvec * yvec);
    xtarg += np->x + xvec / dist * AWAYDIST;
    ytarg += np->y + yvec / dist * AWAYDIST;

    xvec = (xtarg/3 - p->x) * 2.5;
    yvec = (ytarg/3 - p->y) * 2.5;
    dist = sqrt (xvec * xvec + yvec * yvec);
    if (dist > MAXMOVE)
    {
      xvec = (xvec / dist) * MAXMOVE;
      yvec = (yvec / dist) * MAXMOVE;
    }
    p->x = p->x + xvec;
    p->y = p->y + yvec;

    if (p->x < 0) p->x = MAXMOVE;
    else if (p->x > maxx) p->x = maxx - MAXMOVE;
    if (p->y < 0) p->y = MAXMOVE;
    else if (p->y > maxy) p->y = maxy - MAXMOVE;

    p->col++;
    if (p->col == 256) p->col = 1;
    pr_rop (screen, p->x / FIXRAD, p->y / FIXRAD, 
	    PTSIZE, PTSIZE, PIX_SRC | PIX_COLOR (p->col), 0, 0, 0);
  }
  usleep (1000000 / 12);
}



void gravitate (int num)
{
  int count = 1000 - numpt;
  numpt = num;

  initgrav ();

  while (1) {
    movem ();
    if (! count--) {
      switchem ();
      count = 500 + random () % 1000 - numpt;
    }
  }
}



int main (int argc, char *argv[])
{
  int num = argc > 1 ? atoi (argv[1]) : FIXRAD;
  if (num < 10) num = 10;
  else if (num > 500) num = 500;

  if ((screen = pr_open ("/dev/fb")) == 0) {
    perror ("/dev/fb");
    exit (1);
  }
  
  srandom (time (0));

  groovy_colors (&cm);
  cm.red[0] = 0;
  cm.green[0] = 0;
  cm.blue[0] = 0;
  setmap (screen, cm);
  pr_rop (screen, 0, 0, screen->pr_size.x, screen->pr_size.y, PIX_SRC | PIX_COLOR(0), 0, 0, 0);

  gravitate (num);
}
