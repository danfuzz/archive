#
# Dan's handy gcc makefile that uses the Sun linker
#
# By Dan Bornstein
# (with help from rb and jfk)
#
#
# Use "make", "make debug", or "make all" to make a set debuggable executables.
# Use "make fast" to make a set of optimized and stripped executables.
# Use "make clean" to get rid of all the .o files.
#

.KEEP_STATE:



#
# Put executable list here!
#

debug fast all: colorphi growth moire pixie spiro colortest gravitate doublegrav

#
# Executables based on multiple .o files should have lines like this:
#
# hmph: hmph.o floozy.o
# 	$(LINK.o) hmph.o floozy.o -o hmph
#

#
# Compiler flags for gcc
#
FLAGS= -finline-functions -fstrength-reduce

#
# Link with these libraries
#
LDLIBS= -lpixrect -lm



###############################################################################
##
## Don't mess with the rest of this or die a slow, painful death.
##
###############################################################################

# linker/loader command
LDCMD= ld -dc -dp -e start -X /usr/lib/crt0.o

# gcc flags depending on debug or not
CFLAGS=$(FLAGS) -g
debug := CFLAGS=$(FLAGS) -g
fast  := CFLAGS=$(FLAGS) -O

# linker/loader flags depending on debug or not
LDFLAGS=
fast  := LDFLAGS=-s

# the gcc and linker/loader automagic command stuff
CC      = gcc
COMPILE.c = $(CC) $(CFLAGS) $(CPPFLAGS) -c
LINK.o = $(LDCMD) $(LDFLAGS) $(LDLIBS) -lc

.c:
	$(COMPILE.c) $<
	$(LINK.o) -o $@ $@.o

.c.o:
	$(COMPILE.c) $<

.o:
	$(LINK.o) -o $@ $<

clean:
	/bin/rm *.o

