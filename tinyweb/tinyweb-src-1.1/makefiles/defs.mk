# Base Makefile Definitions
#
# Author: Dan Bornstein, danfuzz@milk.com

# Copyright (c) 2000-2001 Dan Bornstein, danfuzz@milk.com. All rights 
# reserved, except as follows:
# 
# Permission is hereby granted, free of charge, to any person obtaining a
# copy of this software and associated documentation files (the "Software"),
# to deal in the Software without restriction, including without limitation
# the rights to use, copy, modify, merge, publish, distribute, sublicense,
# and/or sell copies of the Software, and to permit persons to whom the
# Software is furnished to do so, subject to the condition that the above
# copyright notice and this permission notice shall be included in all copies
# or substantial portions of the Software.
# 
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
# FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
# DEALINGS IN THE SOFTWARE.

# find the base dir of this source tree, defined as closest parent
# directory that contains a makefiles subdirectory; (the cd/pwd two-step is
# to make symlinks shake out in a reasonable way)
BASE_DIR:=$(shell until [ -r makefiles -o "x`pwd`" = 'x/' ]; \
		      do cd ..; \
		  done; \
                  cd `/bin/pwd`; \
		  pwd)

# set up source-tree based directory variables; note that the variable
# MAKEFILES has special significance to make, hence MAKEF is the name
# used below
BUILD:=$(BASE_DIR)/build
SRC:=$(BASE_DIR)
MAKEF:=$(SRC)/makefiles

# set up build-directory based variables
CLASSES:=$(BUILD)/classes
BIN:=$(BUILD)/bin
LIB:=$(BUILD)/lib

# this makes FINAL_CLASSPATH be the build/classes directory, followed by
# the list of all the zips/jars in the build/lib directory, followed by
# what the (environment variable) CLASSPATH is; note the GNU make magic to
# use a space as a parameter to the subst function
empty:=
space:= $(empty) $(empty)
libfiles=$(sort $(wildcard $(LIB)/*.jar) $(wildcard $(LIB)/*.zip))
FINAL_CLASSPATH=$(CLASSES):$(subst $(space),:,$(libfiles))

# set up the javac command. Note: the javac command should *not* end up
# using -sourcepath or -Xdepend. If you think you want to use one of these
# options, then think again. (They're both broken and probably don't do
# what you'd expect.)
JAVAC=javac -deprecation -g -d $(CLASSES) -classpath $(FINAL_CLASSPATH)

# set up the java command, for targets that want to run java with a
# CLASSPATH that includes the classes directory and any jars/zips in the
# lib directory.
JAVA=java -classpath $(FINAL_CLASSPATH)



#
# variable definitions based on the java sources
#
ifdef JAVA_SOURCES
PACKAGE:=$(shell $(MAKEF)/package-name $(JAVA_SOURCES))
PACKAGE_DIR:=$(CLASSES)/$(subst .,/,$(PACKAGE))
endif



#
# these rules are defined so it's possible to override the default behaviors
# relatively easily
#
ifndef SUPPRESS_ALL_TARGET
all: default-all
endif

ifndef SUPPRESS_CLEAN_TARGET
clean: default-clean
endif



#
# the rule to do everything for a normal make
#
.PHONY: default-all
default-all: environment-test announce required-packages \
	compile-java done



#
# the rule to do everything for a normal make clean
#
.PHONY: default-clean
default-clean: clean-local clean-required-packages done



#
# the rule to clean local stuff but not traverse to other directories
#
.PHONY: clean-local
clean-local: environment-test announce clean-java done



#
# the definitions to do pre-make sanity checks
#
NEW_VISITED_TARGS:=\
	$(VISITED_TARGS) \
	$(patsubst $(SRC)/%,%,$(CURDIR))$(patsubst %,:%,$(MAKECMDGOALS))
ifeq ($(sort $(NEW_VISITED_TARGS)), $(sort $(VISITED_TARGS)))
VISITED_TARGS:=error
else
VISITED_TARGS:=$(NEW_VISITED_TARGS)
export VISITED_TARGS
endif

.PHONY: environment-test
environment-test:
	@$(MAKEF)/sanity-check
ifeq ($(BASE_DIR), /) 
	@echo "You don't seem to be in a source tree."
	@echo "Please check your directory structure."
	@false
endif
ifeq ($(PACKAGE), error)
	@echo "Trouble with package specs. (Note that on Linux, this sometimes"
	@echo "spuriously happens. The problem may just-go-away if you retry"
	@echo "the make command.)"
	@false
endif
ifeq ($(VISITED_TARGS), error)
	@echo "Requirement circularity detected:"
	@printf "    %s\n" $(NEW_VISITED_TARGS)
	@false
endif



#
# the rule to print a little announcement at the beginning of the make
#

ifdef PACKAGE
ANNOUNCE_STRING:=package: $(PACKAGE)
else
ANNOUNCE_STRING:=directory: $(CURDIR)
endif

.PHONY: announce
announce:
	@echo " "
	@echo "In $(ANNOUNCE_STRING)"



#
# the rule to print a little announcement at the end of the make
#
.PHONY: done
done:
	@echo "Done with $(ANNOUNCE_STRING)"
	@echo " "



#
# rules to do Java package, external component, and arbitrary directory
# dependencies; the "touch file" used is actually the file that notes the
# source directory of the build (src-dir.txt).
#
.PHONY: required-packages clean-required-packages clean-src-dir

REQUIRED_MAKEFILES:=
REQUIRED_DIRS:=

ifdef REQUIRED_PACKAGES
REQUIRED_PARTIALS=$(subst .,/,$(REQUIRED_PACKAGES))
REQUIRED_DIRS+=$(patsubst %,$(SRC)/%,$(REQUIRED_PARTIALS))
REQUIRED_SOURCES=$(foreach one,$(REQUIRED_DIRS),$(wildcard $(one)/*.java))
REQUIRED_MAKEFILES+=$(patsubst %,%/Makefile,$(REQUIRED_DIRS))
endif

ifdef REQUIRED_DIRECTORIES
REQUIRED_DIRS+= $(REQUIRED_DIRECTORIES)
REQUIRED_MAKEFILES+= $(patsubst %,%/Makefile,$(REQUIRED_DIRECTORIES))
endif

ifdef PACKAGE
SRC_DIR_FILE=$(PACKAGE_DIR)/src-dir.txt

$(SRC_DIR_FILE): $(REQUIRED_SOURCES) $(REQUIRED_MAKEFILES)
	@origdir=`pwd` && \
	$(foreach one,$(sort $(dir $?)),\
	cd $$origdir && cd $(one) && $(MAKE) && ) \
	true
ifneq ($(strip $(REQUIRED_MAKEFILES)),)
	@echo "Back in $(ANNOUNCE_STRING)"
endif
	@-mkdir -p $(PACKAGE_DIR)
	@-echo $(CURDIR) >$(SRC_DIR_FILE)

required-packages: $(SRC_DIR_FILE)

clean-src-dir:
	-rm -f $(SRC_DIR_FILE)

clean-required-packages: clean-src-dir
else
# if we're not in a Java package, we have no choice but to always traverse
# into the required packages/directories
required-packages:
	@origdir=`pwd` && \
	$(foreach one,$(REQUIRED_DIRS) $(EXTERNAL_DIRS),\
		cd $$origdir && cd $(one) && $(MAKE) && ) true
ifneq ($(strip $(REQUIRED_MAKEFILES)),)
	@echo "Back in $(ANNOUNCE_STRING)"
endif
endif

clean-required-packages:
	@origdir=`pwd` && \
	$(foreach one,$(REQUIRED_DIRS) $(EXTERNAL_DIRS),\
		cd $$origdir && cd $(one) && $(MAKE) clean && ) true
ifneq ($(strip $(REQUIRED_MAKEFILES)),)
	@echo "Back in $(ANNOUNCE_STRING)"
endif



#
# rules to compile out-of-date java sources
#
.PHONY: compile-java clean-java
ifdef JAVA_SOURCES
CLASS_TARGETS:=$(patsubst %.java,$(PACKAGE_DIR)/%.class,$(JAVA_SOURCES))
NEEDS_COMPILATION_FILE=$(PACKAGE_DIR)/needs-compilation.txt
NEEDS_COMPILATION_CLASSES=$(PACKAGE_DIR)/needs-compilation-classes.txt

# the class:source dependency doesn't actually do a compile so that
# all the files can be compiled together with one javac invocation,
# which is done in the compile-java rule, below.
$(PACKAGE_DIR)/%.class: %.java
	@rm -f $@
	@touch $@
	@echo $@ >> $(NEEDS_COMPILATION_CLASSES)
	@echo $? >> $(NEEDS_COMPILATION_FILE)

compile-java: environment-test required-packages $(CLASS_TARGETS)
	@-mkdir -p $(PACKAGE_DIR)
	@if [ -r $(NEEDS_COMPILATION_FILE) ]; then \
	    tocompile=`sort -u $(NEEDS_COMPILATION_FILE)` && \
	    printf 'Compiling: %s\n' $$tocompile && \
	    rm -f `sort -u $(NEEDS_COMPILATION_CLASSES)` && \
	    rm -f $(NEEDS_COMPILATION_FILE) $(NEEDS_COMPILATION_CLASSES) && \
	    $(JAVAC) $$tocompile; \
	fi

clean-java:
	-rm -f $(PACKAGE_DIR)/*.class $(NEEDS_COMPILATION_FILE)
else
compile-java:
clean-java:
endif



#
# rule to perform a "scorched earth" clean; it removes the build directory,
# and leaves an empty replacement.
#
.PHONY: scorched-earth
scorched-earth: environment-test
	-rm -rf $(BUILD)
	mkdir -p $(BUILD)
