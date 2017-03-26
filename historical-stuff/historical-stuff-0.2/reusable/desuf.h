/* ---BOCA Project---

Module "Desuf": Suffix removal service

This module provides a suffix removal service for indexing and index lookup.

Routines:

void desuffix (char *word);
word: the word to remove suffixes from
  This will desuffix a given word (and change it to null if it matches a stop list).
  It modifies the word. The word should be in uppercase. It first deaccents the
  word.

void deaccent (char *word);
word: the word to remove accents from
  This will deaccent a given word. It modifies the word. The word should be in uppercase.


Bugs/Problems:

The words shouldn't have to be in upper case.

*/

#ifndef DESUF_H
#define DESUF_H

// desuffix (and deaccent) a word
void desuffix (char *word);

// deaccent a word
void deaccent (char *word);

#endif
