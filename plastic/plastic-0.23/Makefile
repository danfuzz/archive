# Copyright (C) 2000, Dan Bornstein, danfuzz@milk.com. All Rights 
# Reserved. (Shrill TV degreaser.)
#
# This file is part of the MILK Kodebase. The contents of this file are
# subject to the MILK Kodebase Public License; you may not use this file
# except in compliance with the License. A copy of the MILK Kodebase Public
# License has been included with this distribution, and may be found in the
# file named "LICENSE.html". You may also be able to obtain a copy of the
# License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

include makefiles/defs.mk

.PHONY: all
all: get-libs compile etc plastic-jar gplas-jar

.PHONY: get-libs
get-libs: 
	mkdir -p $(LIB)
	-cp -a $(HOME)/cvs/java-gnome/lib/* $(LIB)

.PHONY: compile
compile:
	mkdir -p $(CLASSES)
	$(JAVAC) com/milk/util/*.java
	$(JAVAC) com/milk/plastic/*.java
	$(JAVAC) com/milk/plastic/ports/*.java
	$(JAVAC) com/milk/plastic/iface/*.java
	$(MAKE) -C com/milk/plastic/parser
	$(JAVAC) com/milk/plastic/util/*.java
	$(JAVAC) com/milk/plastic/modules/base/*.java
	$(JAVAC) com/milk/plastic/modules/math/*.java
	$(JAVAC) com/milk/plastic/modules/wave/*.java
	$(JAVAC) com/milk/plastic/modules/filter/*.java
	$(JAVAC) com/milk/plastic/modules/audio/*.java
	$(JAVAC) com/milk/plastic/modules/*.java
	$(JAVAC) com/milk/plastic/runner/*.java
	$(JAVAC) com/milk/plastic/gnome/*.java

.PHONY: etc
etc:
	cp $(SRC)/etc/plas.png $(LIB)

.PHONY: plastic-jar
plastic-jar:
	mkdir -p $(LIB)
	mkdir -p $(BIN)
	-cp scripts/* $(BIN)
	manf=$(SRC)/etc/plastic-manifest.txt; \
	cd $(CLASSES); jar cfm $(LIB)/plastic.jar $$manf *

.PHONY: gplas-jar
gplas-jar:
	mkdir -p $(LIB)
	mkdir -p $(BIN)
	-cp scripts/* $(BIN)
	manf=$(SRC)/etc/gplas-manifest.txt; \
	cd $(CLASSES); jar cfm $(LIB)/gplas.jar $$manf *

.PHONY: dist
dist:
	rm -rf $(BUILD)/plastic
	mkdir -p $(BUILD)/plastic
	cp -a $(LIB) $(BIN) $(BUILD)/plastic
	cd $(BUILD); zip -r plastic.zip plastic
