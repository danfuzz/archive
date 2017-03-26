#include <math.h>
#include <pixrect/pixrect_hs.h>

typedef struct {
  unsigned char red[256];
  unsigned char green[256];
  unsigned char blue[256];
} colormap;

#define setmap(screen,map) pr_putcolormap ((screen), 0, 256, (map).red, (map).green, (map).blue)

typedef struct {
  unsigned char red[16];
  unsigned char green[16];
  unsigned char blue[16];
} dbufmap;

void db2cmap (dbufmap *dbmap, colormap *cl, colormap *ch)
{
  int i, j;

  for (i = 0; i < 16; i++) {
    unsigned char r = dbmap->red[i];
    unsigned char g = dbmap->green[i];
    unsigned char b = dbmap->blue[i];
    for (j = i; j < 256; j += 16) {
      cl->red[j] = r;
      cl->green[j] = g;
      cl->blue[j] = b;
    }
    for (j = i * 16; j < i * 16 + 16; j++) {
      ch->red[j] = r;
      ch->green[j] = g;
      ch->blue[j] = b;
    }
  }
}

unsigned char dbcolor[16] = { 0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77,
			      0x88, 0x99, 0xAA, 0xBB, 0xCC, 0xDD, 0xEE, 0xFF };
int lowplanes = 0x0F;
int hiplanes = 0xF0;
int allplanes = 0xFF;

#define DBLOW(screen) pr_putattributes ((screen),&lowplanes)
#define DBHI(screen) pr_putattributes ((screen),&hiplanes)
#define DBALL(screen) pr_putattributes ((screen),&allplanes)


void test (Pixrect *screen)
{
  dbufmap dm;
  colormap cl;
  colormap ch;
  int i;
  int x, y, xd, yd;
  char foo[200];
  
  for (i = 0; i < 16; i++) {
    dm.red[i] = random () & 0xFF;
    dm.green[i] = random () & 0xFF;
    dm.blue[i] = random () & 0xFF;
  }

  db2cmap (&dm, &cl, &ch);

  pr_rop (screen, 0, 0, screen->pr_size.x, screen->pr_size.y, PIX_SRC | PIX_COLOR(0), 0, 0, 0);

  x = 543;
  y = 328;
  xd = yd = 4;
  i = 1;
  while (1) {
    x += xd;
    if (x > 1052 || x < 1) xd = -xd;
    y += yd;
    if (y > 800 || y < 1) yd = -yd;
    i = !i;
    if (i) {
      DBLOW(screen);
    }
    else {
      DBHI(screen);
    }
    pr_rop (screen, 0, 0, screen->pr_size.x, screen->pr_size.y, PIX_SRC | PIX_COLOR(0), 0, 0, 0);
    pr_rop (screen, x, y, 100, 100, PIX_SRC | PIX_COLOR(dbcolor[1]), 0, 0, 0);
    if (i) setmap (screen, cl);
    else setmap (screen, ch);
    usleep (10000);
  }
}


#define BOT (500)
void threeview (Pixrect *screen, int x, int y)
{
  int blx = x >> 8 - 5;
  int bly = y >> 8 - 1;
  int blxx = blx << 8;
  int blyy = bly << 8;
  int blc = dbcolor[blx + bly & 1 + 1];
  int npts = 1; /* always 1 */
  struct pr_pos vlist[4];

  vlist[0].x = 
  pr_polygon_2 (screen, 0, 0, 1, &npts, vlist, PIX_SRC | PIX_COLOR(blc), 0, 0, 0);
...
}


main ()
{
  Pixrect *screen;
  
  if ((screen = pr_open ("/dev/fb")) == 0) {
    perror ("/dev/fb");
    exit (1);
  }
  
  srandom (time (0));
  test (screen);
}
  

