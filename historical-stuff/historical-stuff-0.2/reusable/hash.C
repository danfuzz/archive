//
// Module "Hash"
//

#include "hash.h"
#include <stdio.h>
#include <memory.h>
#include <string.h>
#include <limits.h>

extern "C" void exit (int status);


static int sizelist[] = {
  7, 17, 37, 89, 157, 293, 457, 701, 1051, 1511, 2011, 3049, 4001, 5003,
  6101, 7001, 8009, 9013, 10007, 15013, 20011, 30029, 40009, 50021,
  60077, 70001, 80021, 90001, 100003, 150001, 200003, 300017, 400009, 
  500009, 600043, 700001, 800011, 900001, 1000003
};
static const int NUMSIZES = 39;

static const int MAXCOLLISIONS = 32;



Hash::Hash (int startsize)
{
  startsize *= 2;
  if (startsize > 500000) startsize -= 30000;
  else if (startsize > 50000) startsize -= 3000;
  else if (startsize > 5000) startsize -= 300;

  int index = 0;
  while (index < NUMSIZES && sizelist[index] < startsize)
    index++;

  if (index == NUMSIZES) {
    fprintf (stderr, "Hash: too large\n");
    exit (1);
  }
  size = sizelist[index];

  table = new Iref *[size];
  memset (table, 0, size * sizeof (Iref *));
}



Hash::~Hash ()
{
  if (table) {
    int i;
    Iref **r;
    for (i = 0, r = table; i < size; i++, r++) 
      if (*r) delete *r;
    delete table;
  }
}
       


void Hash::forget_table ()
{
  table = 0;
}



void Hash::add (char *key, unsigned long int index)
{
  int foundat;
  int addloc;

  if (! table) return;

  while ((foundat = aux_find (key, &addloc)) == HASHcannot_add)
    enlarge ();

  if (foundat != HASHcan_add) {                             /* already in the table; just set it */
    table[foundat]->index = index;
    return;
  }
  
  table[addloc] = new Iref;
  strcpy (table[addloc]->string, key);
  table[addloc]->index = index;
}



int Hash::find (char *key)
{
  if (! table) return (HASHnot_found);
  int foundat = aux_find (key, 0);

  if (foundat < 0) return (HASHnot_found);
  return (table[foundat]->index);
}



void Hash::enlarge ()
{
  Hash newh (size * 2);
  int i;
  Iref **r;

  for (i = 0, r = table; i < size; i++, r++) 
    if (*r) newh.add (*r);
  
  delete table;
  table = newh.table;
  size = newh.size;
  newh.forget_table ();
}



unsigned int Hash::calc_hash (char *key)
{
  unsigned int sofar = 0;

  while (*key) 
    sofar = (sofar >> 24) ^ ((sofar << 4) + *(key++));

  return (sofar);
}



int Hash::aux_find (char *key, int *addind)
{
  unsigned int rawhash = calc_hash (key);
  int x = rawhash % size;
  int dx = 16 - rawhash % 16;
  int collisions;
  int addplace = -1;
  int foundat = -1;

  for (collisions = 0; collisions < MAXCOLLISIONS; collisions++, x = (x + dx) % size) {
    if (table[x]) {
      if (strcmp (table[x]->string, key) == 0) {
	foundat = x;
	break;
      }
    }
    else {
      addplace = x;
      break;
    }
  }

  if (foundat != -1) return (foundat);
  if (addplace != -1) {
    if (addind) *addind = addplace;
    return (HASHcan_add);
  }
  return (HASHcannot_add);
}
