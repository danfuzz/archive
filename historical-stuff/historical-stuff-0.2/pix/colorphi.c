#include <limits.h>
#include <stdio.h>

#define cg12_var_DEFINED
#include <pixrect/pixrect_hs.h>


typedef struct {
  unsigned char red[256];
  unsigned char green[256];
  unsigned char blue[256];
} colormap;


#define setmap(screen,map) pr_putcolormap (screen, 0, 256, (map).red, (map).green, (map).blue)


colormap cmap;
Pixrect *screen;
int size;
int sep;
int utime;
int dtime;

void setup_colors (void)
{
  int i;

  cmap.red[0]   = 0;
  cmap.green[0] = 0;
  cmap.blue[0]  = 0;

  cmap.red[1]   = 200;
  cmap.green[1] = 64;
  cmap.blue[1]  = 64;

  cmap.red[2]   = 64;
  cmap.green[2] = 200;
  cmap.blue[2]  = 64;

  for (i = 3; i < 255; i++) {
    cmap.red[i] = (i * 5) & 0xff;
    cmap.green[i] = (i * 5) & 0xff;
    cmap.blue[i] = (i * 5) & 0xff;
  }

  setmap (screen, cmap);
}


void doit (void)
{
  int x1, x2, y;

  x1 = 500 - sep - size;
  x2 = 500 + sep;
  y = 450;

  while (1) {
    pr_rop (screen, x1, y, size, size, PIX_SRC | PIX_COLOR(1), 0, 0, 0);
    usleep (utime);
    pr_rop (screen, x1, y, size, size, PIX_SRC | PIX_COLOR(0), 0, 0, 0);
    usleep (dtime);
    pr_rop (screen, x2, y, size, size, PIX_SRC | PIX_COLOR(2), 0, 0, 0);
    usleep (utime);
    pr_rop (screen, x2, y, size, size, PIX_SRC | PIX_COLOR(0), 0, 0, 0);
    usleep (dtime);
  }
}


int main (int argc, char *argv[])
{
  if ((screen = pr_open ("/dev/fb")) == 0) {
    perror ("/dev/fb");
    exit (1);
  }

  if (argc < 5) return (1);

  size = atoi (argv[1]);
  sep = atoi (argv[2]);
  utime = atoi (argv[3]);
  dtime = atoi (argv[4]);
  
  setup_colors ();
  pr_rop (screen, 0, 0, screen->pr_size.x, screen->pr_size.y, PIX_SRC | PIX_COLOR(0), 0, 0, 0);
  doit ();

  return (0);
}
