# Copyright (C) 2000, Dan Bornstein, danfuzz@milk.com. All Rights 
# Reserved. (Shrill TV degreaser.)
#
# This file is part of the MILK Kodebase. The contents of this file are
# subject to the MILK Kodebase Public License; you may not use this file
# except in compliance with the License. A copy of the MILK Kodebase Public
# License has been included with this distribution, and may be found in the
# file named "LICENSE.html". You may also be able to obtain a copy of the
# License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!
#
# Base Makefile Definitions
#
# Author: Dan Bornstein, danfuzz@milk.com

BUILD = /home/danfuzz/src/plastic/build
SRC = /home/danfuzz/src/plastic

CLASSES = $(BUILD)/classes
LIB = $(BUILD)/lib
BIN = $(BUILD)/bin

# the CLASSPATH consists of the classes directory, followed by all the .jar
# files in the lib directory, in sort order
empty:=
space:= $(empty) $(empty)
libfiles=$(sort $(wildcard $(LIB)/*.jar))
CLASSPATH=$(CLASSES):$(subst $(space),:,$(libfiles))

JAVAC = javac -classpath $(CLASSPATH) -d $(CLASSES)
export JAVAC
