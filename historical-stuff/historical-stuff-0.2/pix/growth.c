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


#define ANYCHAN (0.0015)
#define SINGCHAN (0.5)


#define XOFF (63)
#define YOFF (2)
#define XSIZE (8)
#define YSIZE (8)
#define WIDTH (128)
#define HEIGHT (112)

/* another legit set of defines
#define XOFF (456)
#define YOFF (345)
#define XSIZE (3)
#define YSIZE (3)
#define WIDTH (80)
#define HEIGHT (70)
*/


#define ORTHLIM (1.0)
#define DIAGLIM (1.414)

typedef struct cell_s {
  int x, y;
  double speed;
  double growth;
  unsigned char col;
  struct cell_s *(adj)[3][3];
  int isnext;
  double nextspeed;
  unsigned char nextcol;
  struct cell_s *next;
  struct cell_s *prev;
} cell;

cell arr[WIDTH][HEIGHT];  
cell *head;
cell *tail;
int blastcount;
int docount;
Pixrect *screen;
colormap cm;

void setup_colors (void)
{
  unsigned char i, n;

  for (n = 0; n < 16; n++) {
    i = ((n & 0x08) != 0) * 64;
    cm.blue[n]  = ((n & 0x04) != 0) * 128 + i;
    cm.green[n] = ((n & 0x02) != 0) * 128 + i;
    cm.red[n]   = ((n & 0x01) != 0) * 128 + i;
  }

  cm.red[8] = 255;
  cm.green[8] = 255;
  cm.blue[8] = 255;

  setmap (screen, cm);
}


void drawblock (int x, int y, unsigned char c)
{
  pr_rop (screen, x * XSIZE + XOFF, y * YSIZE + YOFF, XSIZE, YSIZE,
	  PIX_SRC | PIX_COLOR(c), 0, 0, 0);
}



void setup_arr (void)
{
  int x, y;
  int i, j;
  int a, b;

  for (y = 0; y < HEIGHT; y++)
    for (x = 0; x < WIDTH; x++) {
      arr[x][y].x = x;
      arr[x][y].y = y;
      arr[x][y].speed = 0.0;
      arr[x][y].growth = 0.0;
      arr[x][y].col = 0;
      arr[x][y].isnext = 0;
      arr[x][y].next = 0;
      arr[x][y].prev = 0;
      for (i = 0; i < 3; i++) {
	a = x + i - 1;
	if (a < 0) a = WIDTH - 1;
	else if (a >= WIDTH) a = 0;
	for (j = 0; j < 3; j++) {
	  b = y + j - 1;
	  if (b < 0) b = HEIGHT - 1;
	  else if (b >= HEIGHT) b = 0;
	  arr[x][y].adj[i][j] = &arr[a][b];
	}
	drawblock (x, y, 0);
      }
    }

  head = (cell *) malloc (sizeof (cell));
  tail = (cell *) malloc (sizeof (cell));
  head->next = tail;
  head->prev = head;
  tail->next = tail;
  tail->prev = head;
}



void newcell (cell *c, unsigned char col, double sp)
{
  if (! c) return;

  if (c->col == col) return;

  c->nextcol = col;
  c->nextspeed = sp;
  c->isnext = 1;

  if (c->prev == 0) {
    c->next = head->next;
    c->prev = head;
    head->next = c;
    c->next->prev = c;
  }
}


void killcell (cell *c)
{
  c->prev->next = c->next;
  c->next->prev = c->prev;
  c->prev = 0;
  c->speed = 0.0;
  drawblock (c->x, c->y, c->col);
}


#define RANDOUBLE ((double) (random ()) / INT_MAX)

void randblip (int doit)
{
  int n;
  int b = 0;
  if (!doit && docount-- && (blastcount-- >= 0) && RANDOUBLE > ANYCHAN) return;

  if (blastcount < 0) {
    b = 1;
    n = 2;
    blastcount = random () % 1000 + 500;
    docount = 130;
  }
  else if (RANDOUBLE <= SINGCHAN) n = 2;
  else n = random () % 3 + 3;

  while (n--) {
    int x = random () % WIDTH;
    int y = random () % HEIGHT;
    int c = random () % 7 + 1;
    double s = RANDOUBLE / 7 + 0.07;
    if (b) {
      s = 0.6 + RANDOUBLE / 20;
      c = 0;
    }
    newcell (&arr[x][y], c, s);
  }
}


void update (void)
{
  cell *a;

  for (a = head->next; a != tail; a = a->next) {
    if (a->speed == 0) continue;
    a->growth += a->speed;
    if (a->growth >= ORTHLIM) {
      newcell (a->adj[0][1], a->col, a->speed);
      newcell (a->adj[2][1], a->col, a->speed);
      newcell (a->adj[1][0], a->col, a->speed);
      newcell (a->adj[1][2], a->col, a->speed);
    }
    if (a->growth >= DIAGLIM) {
      newcell (a->adj[0][0], a->col, a->speed);
      newcell (a->adj[0][2], a->col, a->speed);
      newcell (a->adj[2][0], a->col, a->speed);
      newcell (a->adj[2][2], a->col, a->speed);
      killcell (a);
    }
  }

  randblip (0);

  for (a = head->next; a != tail; a = a->next)
    if (a->isnext) {
      a->isnext = 0;
      a->speed = a->nextspeed;
      a->growth = 0.0;
      a->col = a->nextcol;
      drawblock (a->x, a->y, a->col | 0x08);
    }
}



main ()
{
  if ((screen = pr_open ("/dev/fb")) == 0) {
    perror ("/dev/fb");
    exit (1);
  }
  
  srandom (time (0));

  setup_colors ();
  pr_rop (screen, 0, 0, screen->pr_size.x, screen->pr_size.y, PIX_SRC | PIX_COLOR(0), 0, 0, 0);
  setup_arr ();

  blastcount = 700;
  docount = 805;

  randblip (1);

  while (1) {
    update ();
  }
}
  

