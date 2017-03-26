#
# Simple Makefile for the Adder tutorial.
#
# This file is in the public domain.
#
# Author: Dan Bornstein, danfuzz@milk.com
#

ANTLR_HOME=/usr/local/lib/antlr
VERSION=1.3

all:
	$(ANTLR_HOME)/bin/antlr add.g
	javac -classpath $(ANTLR_HOME):. -d . *.java

run: all
	java -cp $(ANTLR_HOME):. Adder test.txt

clean:
	-rm *.class AddInterpreter.java AddLexer.java AddParser.java \
		AddTokenTypes.java AddTokenTypes.txt

ARCHIVE_NAME=antlr-tutorial-$(VERSION)
tarball:
	-rm -rf $(ARCHIVE_NAME)*
	mkdir $(ARCHIVE_NAME)
	cp *.java *.g *.txt Makefile $(ARCHIVE_NAME)
	tar cf $(ARCHIVE_NAME).tar $(ARCHIVE_NAME)
	gzip -9 $(ARCHIVE_NAME).tar
	-rm -rf $(ARCHIVE_NAME)
