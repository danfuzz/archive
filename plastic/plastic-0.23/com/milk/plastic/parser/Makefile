# Copyright (C) 2000, Dan Bornstein, danfuzz@milk.com. All Rights 
# Reserved. (Shrill TV degreaser.)
#
# This file is part of the MILK Kodebase. The contents of this file are
# subject to the MILK Kodebase Public License; you may not use this file
# except in compliance with the License. A copy of the MILK Kodebase Public
# License has been included with this distribution, and may be found in the
# file named "LICENSE.html". You may also be able to obtain a copy of the
# License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

include ../../../../makefiles/defs.mk

ANTLR_OUTPUT_DIR=$(BUILD)/antlr
ANTLR_LIB=/usr/local/lib/antlr

all:
	mkdir -p $(CLASSES)
	cd $(CLASSES); jar xf $(ANTLR_LIB)/antlr.jar
	mkdir -p $(ANTLR_OUTPUT_DIR)
	antlr -o $(ANTLR_OUTPUT_DIR) plastic.g
	$(JAVAC) $(ANTLR_OUTPUT_DIR)/*.java *.java

debug:
	mkdir -p $(CLASSES)/antlr
	cp -r $(ANTLR_LIB)/antlr/* $(CLASSES)/antlr
	chmod -R 755 $(CLASSES)
	mkdir -p $(ANTLR_OUTPUT_DIR)
	antlr -debug -o $(ANTLR_OUTPUT_DIR) plastic.g
	$(JAVAC) $(ANTLR_OUTPUT_DIR)/*.java *.java

