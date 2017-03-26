#include <limits.h>
#include <stdio.h>

#define cg12_var_DEFINED
#include <pixrect/pixrect_hs.h>


typedef struct {
  unsigned char red[256];
  unsigned char green[256];
  unsigned char blue[256];
} colormap;

Pixrect *screen;

#define setmap(screen,map) pr_putcolormap (screen, 0, 256, (map).red, (map).green, (map).blue)


/*
int red[8]   = { 64, 54, 46, 36, 28, 18, 9,  0  };
int green[8] = { 0,  21, 42, 64, 64, 42, 22, 0  };
int blue[8]  = { 0,  9,  18, 28, 36, 46, 54, 64 };
*/
int red[8]   = { 28, 36, 46, 54, 64, 0,  9,  18 };
int green[8] = { 54, 64, 0,  9,  18, 28, 36, 46 };
int blue[8]  = { 0,  9,  18, 28, 36, 46, 54, 64 };


colormap cm;

void setupmap (void)
{
  int i;

  for (i = 0; i < 256; i++) {
    int r = 0, g = 0, b = 0;
    int bit;
    for (bit = 0; bit < 8; bit++)
      if (i & (1 << bit)) {
	r += red[bit];
	g += green[bit];
	b += blue[bit];
      }
    cm.red[i] = r;
    cm.green[i] = g;
    cm.blue[i] = b;
  }

  setmap (screen, cm);
}



void drawblobs (void)
{
  int x, y;
  int size;
  int maxx, maxy;
  int xoff, yoff;

  maxx = screen->pr_size.x;
  maxy = screen->pr_size.y;

  if (maxx > maxy) size = maxy;
  else size = maxx;
  size = size / 24;

  xoff = (maxx - (size * 16)) / 2;
  yoff = (maxy - (size * 16)) / 2;

  for (x = 0; x < 16; x++)
    for (y = 0; y < 16; y++)
      pr_rop (screen, x * size + xoff, y * size + yoff, size, size, 
	      PIX_SRC | PIX_COLOR (x + y * 16), 0, 0, 0);
}



main ()
{
  if ((screen = pr_open ("/dev/fb")) == 0) {
    perror ("/dev/fb");
    exit (1);
  }
  
  srandom (time (0));

  setupmap ();
  drawblobs ();
}
