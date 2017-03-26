#include <math.h>
#define cg12_var_DEFINED
#include <pixrect/pixrect_hs.h>


typedef struct {
  int f1, f2, f3;
} colorset;


Pixrect *screen;
unsigned char red[256], green[256], blue[256];
colorset cols;


void newf (cs)
colorset *cs;
{
  cs->f1 = random() % 10 + 1;
  cs->f2 = random() % 10 + 1;
  cs->f3 = random() % 10 + 1;
}


void newfs (cs)
colorset *cs;
{
  int x;

  newf (cs);

  for (x = 0; x < 256; x++) {
    if (random () & 0x3) {
      red[x] = green[x] = blue[x] = 0;
    }
    else {
      red[x] = (x * cs->f1) % 127 * 2;
      green[x] = (x * cs->f2) % 127 * 2;
      blue[x] = (x * cs->f3) % 127 * 2;
    }
  }
  pr_putcolormap(screen, 0, 256, red, green, blue);
}
 

void zapmap ()
{
  int color = random () & 0xFF;
  if (random () & 0x3) {
    red[color] = green[color] = blue[color] = 0;
  }
  else {
    red[color] = (color * cols.f1) % 127 * 2;
    green[color] = (color * cols.f2) % 127 * 2;
    blue[color] = (color * cols.f3) % 127 * 2;
  }
  pr_putcolormap(screen, 0, 256, red, green, blue);
}


void transmap ()
{
  colorset newset;
  colorset oldset;
  int tenthou, thouten;
  int times;

  oldset.f1 = cols.f1;
  oldset.f2 = cols.f2;
  oldset.f3 = cols.f3;

  newf (&newset);

  for (tenthou = 0; tenthou < 10000; tenthou++) {
    thouten = 10000 - tenthou;
    cols.f1 = (oldset.f1 * thouten + newset.f1 * tenthou) / 10000;
    cols.f2 = (oldset.f2 * thouten + newset.f2 * tenthou) / 10000;
    cols.f3 = (oldset.f3 * thouten + newset.f3 * tenthou) / 10000;
    for (times = 0; times < 5; times++) zapmap ();
  }
}



main (argc, argv)
int argc;
char **argv;
{
  int color;
  int x, y;
  int dim;
  int a, b;
  
  if ((screen = pr_open("/dev/fb")) == 0) {
    perror("/dev/fb");
    exit(1);
  }
  
  srandom(time(0));

  newfs (&cols);

  for (x = 0; x < screen->pr_size.x; x++) {
    for (y = 0; y < screen->pr_size.y; y++) {
      color = random() &0xFF;
      pr_put (screen, x, y, color);
    }
  }

  x = 0;
  y = 0;
  dim = 2;
  while (1) {
    if (random () & 0x01) zapmap ();
    color = random () & 0xFF;
    pr_rop (screen, x, y, dim, dim, PIX_SRC | PIX_COLOR(color), 0, x, y);
    if (random () & 0x03) a = 1;
    else a = (random () & 0x0F) + 20*(dim == 1) + 5*(dim==2);
    while (a--) {
      y += dim;
      if (y >= screen->pr_size.y) {
	y = 0;
	x += dim;
	if (x >= screen->pr_size.x) {
	  x = 0;
	  dim <<= 1;
	  if (dim > 128) {
	    transmap ();
	    dim = 1;
	  }
	}
      }
    }
  }
}

