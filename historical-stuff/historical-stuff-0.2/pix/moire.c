#include <math.h>
#define cg12_var_DEFINED
#include <pixrect/pixrect_hs.h>

typedef struct {
  unsigned char red[256];
  unsigned char green[256];
  unsigned char blue[256];
} colormap;


#define setmap(screen,map) pr_putcolormap (screen, 0, 256, (map).red, (map).green, (map).blue)


void rotate_map (colormap *cm)
{
  int x;
  int r = cm->red[0];
  int g = cm->green[0];
  int b = cm->blue[0];

  for (x = 0; x < 255; x++) {
    cm->red[x] = cm->red[x+1];
    cm->green[x] = cm->green[x+1];
    cm->blue[x] = cm->blue[x+1];
  }

  cm->red[255] = r;
  cm->green[255] = g;
  cm->blue[255] = b;
}



/* commented out because I don't use it in this program. It *does* work,
   though...

void fademap (Pixrect *screen, colormap *cm1, colormap *cm2, int steps)
{
  int dred[256], dgreen[256], dblue[256];
  int red[256], green[256], blue[256];
  colormap cm;
  int x;
  
  for (x = 0; x < 256; x++) {
    red[x] = cm1->red[x] * 65536;
    green[x] = cm1->green[x] * 65536;
    blue[x] = cm1->blue[x] * 65536;
    dred[x] = (cm2->red[x] * 65536 - red[x]) / steps;
    dgreen[x] = (cm2->green[x] * 65536 - green[x]) / steps;
    dblue[x] = (cm2->blue[x] * 65536 - blue[x]) / steps;
  }

  while (steps--) {
    for (x = 0; x < 256; x++) {
      cm.red[x] = red[x] / 65536;
      cm.green[x] = green[x] / 65536;
      cm.blue[x] = blue[x] / 65536;
      red[x] += dred[x];
      green[x] += dgreen[x];
      blue[x] += dblue[x];
    }
    setmap (screen, cm);
  }
}

*/


void faderotmap (Pixrect *screen, colormap *cm1, colormap *cm2, int steps)
{
  int dred[256], dgreen[256], dblue[256];
  int red[256], green[256], blue[256];
  unsigned char rotfact = 0;
  int x;
  
  for (x = 0; x < 256; x++) {
    red[x] = cm1->red[x] * 65536;
    green[x] = cm1->green[x] * 65536;
    blue[x] = cm1->blue[x] * 65536;
    dred[x] = (cm2->red[x] * 65536 - red[x]) / steps;
    dgreen[x] = (cm2->green[x] * 65536 - green[x]) / steps;
    dblue[x] = (cm2->blue[x] * 65536 - blue[x]) / steps;
  }

  while (steps--) {
    for (x = 0; x < 256; x++) {
      cm1->red[(x + rotfact) & 0xFF] = red[x] / 65536;
      cm1->green[(x + rotfact) & 0xFF] = green[x] / 65536;
      cm1->blue[(x + rotfact) & 0xFF] = blue[x] / 65536;
      red[x] += dred[x];
      green[x] += dgreen[x];
      blue[x] += dblue[x];
    }
    setmap (screen, *cm1);
    rotfact -= 2;
  }
}



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
    int redadd = ((ared[i+1] - ared[i]) * 65536) / range;
    int greenadd = ((agreen[i+1] - agreen[i]) * 65536) / range;
    int blueadd = ((ablue[i+1] - ablue[i]) * 65536) / range;
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


void do_moire (Pixrect *screen, colormap *cm, int cx, int cy, int dir)
{
  int x, y;
  unsigned char col = 0;

  for (x = 0; x < screen->pr_size.x; x++) {
    rotate_map (cm);
    setmap (screen, *cm);
    pr_vector (screen, cx, cy, x, 0, PIX_SRC^PIX_DST, col);
    col += dir;
  }

  for (y = 1; y < screen->pr_size.y; y++) {
    rotate_map (cm);
    setmap (screen, *cm);
    pr_vector (screen, cx, cy, screen->pr_size.x - 1, y, PIX_SRC^PIX_DST, col);
    col += dir;
  }

  for (x--; x >= 0; x--) {
    rotate_map (cm);
    setmap (screen, *cm);
    pr_vector (screen, cx, cy, x, screen->pr_size.y - 1, PIX_SRC^PIX_DST, col);
    col += dir;
  }

  for (y--; y >= 0; y--) {
    rotate_map (cm);
    setmap (screen, *cm);
    pr_vector (screen, cx, cy, 0, y, PIX_SRC^PIX_DST, col);
    col += dir;
  }
}


main ()
{
  Pixrect *screen;
  colormap cm;
  colormap cm2;
  int cx1, cy1, cx2, cy2;
  int d1, d2;
  int i;
  
  if ((screen = pr_open ("/dev/fb")) == 0) {
    perror ("/dev/fb");
    exit (1);
  }
  
  srandom (time (0));

  groovy_colors (&cm);
  setmap (screen, cm);
  pr_rop (screen, 0, 0, screen->pr_size.x, screen->pr_size.y, PIX_SRC | PIX_COLOR(0), 0, 0, 0);

  cx1 = random () % (screen->pr_size.x - 100) + 50;
  cy1 = random () % (screen->pr_size.y - 100) + 50;
  d1  = (random () & 1 * 2) - 1;
  cx2 = random () % (screen->pr_size.x - 100) + 50;
  cy2 = random () % (screen->pr_size.y - 100) + 50;
  d2  = (random () & 1 * 2) - 1;
  do_moire (screen, &cm, cx1, cy1, d1);
  
  while (1) {
    for (i = 0; i < 2000; i++) {
      rotate_map (&cm);
      setmap (screen, cm);
    }

    do_moire (screen, &cm, cx2, cy2, d2);
    groovy_colors (&cm2);
    faderotmap (screen, &cm, &cm2, 3000);
    do_moire (screen, &cm, cx1, cy1, d1);

    cx1 = cx2;
    cy1 = cy2;
    d1  = d2;
    cx2 = random () % (screen->pr_size.x - 100) + 50;
    cy2 = random () % (screen->pr_size.y - 100) + 50;
    d2  = (random () & 1 * 2) - 1;
  }
}
  

