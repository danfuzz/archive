#include <stdio.h>
#include <sys/ioctl.h>
#include <fcntl.h>
#include <sun/fbio.h>

struct fbcursor cur;

unsigned char r[2] = { 255, 0 };
unsigned char g[2] = { 0, 255 };
unsigned char b[2] = { 0,   0 };

unsigned int  mask1[]  = { 0x00000001, 0x00000003, 0x00000007, 0x0000000f,
                           0x0000001f, 0x0000003f, 0x0000007f, 0x000000ff,
                           0x000001ff, 0x000003ff, 0x000007ff, 0x00000fff,
                           0x00001fff, 0x00003fff, 0x00007fff, 0x0000ffff,
                           0x0001ffff, 0x0003ffff, 0x0007ffff, 0x000fffff,
                           0x001fffff, 0x003fffff, 0x007fffff, 0x00ffffff,
                           0x01ffffff, 0x03ffffff, 0x07ffffff, 0x0fffffff,
                           0x1fffffff, 0x3fffffff, 0x7fffffff, 0xffffffff };

unsigned int image1[]  = { 0x00000001, 0x00000003, 0x00000007, 0x0000000f,
                           0x0000001f, 0x0000003f, 0x0000007f, 0x000000ff,
                           0x000001ff, 0x000003ff, 0x000007ff, 0x00000fff,
                           0x00001fff, 0x00003fff, 0x00007fff, 0x0000ffff,
                           0x0001ffff, 0x0003ffff, 0x00000000, 0x00000000,
                           0x00000000, 0x00000000, 0x00000000, 0x00000000,
                           0x00000000, 0x00000000, 0x00000000, 0x00000000,
                           0x00000000, 0x00000000, 0x00000000, 0x00000000 };

unsigned int  mask2[]  = { 0x80000000, 0xc0000000, 0xe0000000, 0xf0000000,
			   0xf8000000, 0xfc000000, 0xfe000000, 0xff000000,
			   0xff800000, 0xffc00000, 0xffe00000, 0xfff00000,
			   0xfff80000, 0xfffc0000, 0xfffe0000, 0xffff0000,
			   0xffff8000, 0xffffc000, 0xffffe000, 0xfffff000,
			   0xfffff800, 0xfffffc00, 0xfffffe00, 0xffffff00,
			   0xffffff80, 0xffffffc0, 0xffffffe0, 0xfffffff0,
			   0xfffffff8, 0xfffffffc, 0xfffffffe, 0xffffffff };

unsigned int image2[]  = { 0x80000000, 0xc0000000, 0xe0000000, 0xf0000000,
			   0xf8000000, 0xfc000000, 0xfe000000, 0xff000000,
			   0xff800000, 0xffc00000, 0xffe00000, 0xfff00000,
			   0xfff80000, 0xfffc0000, 0xfffe0000, 0xffff0000,
			   0xffff8000, 0xffffc000, 0x00000000, 0x00000000,
                           0x00000000, 0x00000000, 0x00000000, 0x00000000,
                           0x00000000, 0x00000000, 0x00000000, 0x00000000,
                           0x00000000, 0x00000000, 0x00000000, 0x00000000 };

/*
unsigned char image[] = { 0x01, 0x03, 0x07, 0x0f, 0x00, 0x00, 0x00, 0x00 };
unsigned char mask[]  = { 0x01, 0x03, 0x07, 0x0f, 0x1f, 0x3f, 0x7f, 0xff };
*/



main ()
{
  struct fbgattr attr;
  int fd = open ("/dev/fb", O_RDWR);
  int x, y;
  int dx, dy;
  int flipper;
  struct fbcurpos p;

  if (ioctl (fd, FBIOGATTR, &attr) < 0 ||
      attr.fbtype.fb_type != FBTYPE_SUNFAST_COLOR) {
    fprintf (stderr, "device /dev/fb is not a GX frame buffer\n");
    exit (1);
  }
   
  cur.set        = FB_CUR_SETALL;
  cur.enable     = 1;
  cur.pos.x      = 0;
  cur.pos.y      = 0;
  cur.hot.x      = 0;
  cur.hot.y      = 0;
  cur.cmap.index = 0;
  cur.cmap.count = 2;
  cur.cmap.red   = r;
  cur.cmap.green = g;
  cur.cmap.blue  = b;
  cur.size.x     = 32;
  cur.size.y     = 32;
  cur.image      = (char *) image1;
  cur.mask       = (char *) mask1;

  ioctl (fd, FBIOSCURSOR, &cur);

  x = 10;
  y = 10;
  dx = 13;
  dy = 1;
  flipper = 0;
  while (1) {
    x = x + dx;
    if (x < 0) {
      x = 0;
      dx = -dx - 3;
      if (dx < 5) dx = 30;
      if (dy > 0) {
	flipper++;
	if (flipper == 5) {
	  flipper = 0;
	  dy = -dy;
	}
      }
      cur.set = FB_CUR_SETSHAPE | FB_CUR_SETHOT | FB_CUR_SETCMAP 
          | FB_CUR_SETCUR;
      cur.image = (char *) image1;
      cur.mask  = (char *) mask1;
      ioctl (fd, FBIOSCURSOR, &cur);
    }
    else if (x > 1120) {
      x = 1120;
      dx = -dx + 3;
      if (dx > -5) dx = -30;
      if (dy > 0) {
	flipper++;
	if (flipper == 5) {
	  flipper = 0;
	  dy = -dy;
	}
      }
      cur.set = FB_CUR_SETSHAPE | FB_CUR_SETHOT | FB_CUR_SETCMAP 
          | FB_CUR_SETCUR;
      cur.image = (char *) image2;
      cur.mask  = (char *) mask2;
      ioctl (fd, FBIOSCURSOR, &cur);
    }
    y = y + dy;
    if (y < 0) {
      y = 0;
      dy = -dy;
    }
    else if (y > 868) {
      y = 868;
      dy = -dy + 3;
      if (dy > -2) dy = -42;
    }
    dy = dy + 1;
    p.x = x;
    p.y = y;
    ioctl (fd, FBIOSCURPOS, &p);
    usleep (10000);
  }
}


