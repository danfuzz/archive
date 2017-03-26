#
# Dan's handy gcc makefile
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

debug fast all: pkl

#
# Executables based on multiple .o files should have lines like this:
#
# hmph: hmph.o floozy.o
# 	$(CC) hmph.o floozy.o -o hmph
#
pkl: pklGram.tab.o lex.yy.o pkl.o  pklAssert.o pklConst.o pklFn.o pklGlobal.o pklIntern.o pklMacro.o pklMacroExpansion.o pklMem.o pklParse.o pklTypes.o
	$(CC) pklGram.tab.o lex.yy.o pkl.o pklAssert.o pklConst.o pklFn.o pklGlobal.o pklIntern.o pklMacro.o pklMacroExpansion.o pklMem.o pklParse.o pklTypes.o \
	-o pkl -lfl -lm

#
# Compiler flags for gcc
#
FLAGS= -finline-functions -fstrength-reduce

#
# Link with these libraries.
#
LDLIBS=



###############################################################################
##
## Don't mess with the rest of this or die a slow, painful death.
##
###############################################################################

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

.c:
	$(COMPILE.c) $<
	$(LINK.o) -o $@ $@.o

.c.o:
	$(COMPILE.c) $<

.o:
	$(LINK.o) -o $@ $<

clean:
	/bin/rm *.o

