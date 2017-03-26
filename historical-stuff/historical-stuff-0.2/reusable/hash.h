/* ---BOCA Project---

Module "Hash": Keyword hash table

This module provides a hash table for storing index keys and their
associated values.

Methods:

Hash (int startsize);
startsize: an estimate of how many keys will be entered in the table
  This constructs an empty hash table.

~Hash ();
  This just cleans up the memory used by the table.

void add (char *key, unsigned long int index);
key: the key to add
index: the value to associate with it
  This will add a key to the table, or, if it is already in the
  table, replace its value.

int find (char *key);
key: the key to look up
  This will look up the given key and return the value associated with
  it. If the key isn't found, this returns the value HASHnot_found.


Bugs/Problems:

There is a discrepancy between the types of the index taken by add and
returned by find. They should probably both just be (signed) long ints.

*/


#ifndef HASH_H
#define HASH_H

#include "iref.h"

const int HASHcan_add = -1;
const int HASHcannot_add = -2;
const int HASHnot_found = -1;

class Hash
{
  public:
    Hash (int startsize);                  // Constructor
    ~Hash ();                              // Destructor

    void add (char *key,                   // Add a value
	      unsigned long int index);
    void add (Iref *ir);

    int find (char *key);                  // Find a value

  private:
    Iref **table;                          // The hash table
    int size;                              // The size of the table

    unsigned int calc_hash (char *key);    // Calculate hash
    int aux_find (char *key, int *addind); // Auxiliary finder
    void forget_table ();                  // Forget about the table
    void enlarge ();                       // Double the size of the table
};


inline void Hash::add (Iref *ir)
{
  add (ir->string, ir->index);
}


#endif
