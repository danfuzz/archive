DISTNAME=uberchat-0.32

PACKAGES=\
	com.milk.util \
	com.milk.timer \
	com.milk.asynch \
	com.milk.gui \
	com.milk.objed \
	com.milk.objed.event \
	com.milk.objed.gui \
	com.milk.command \
	com.milk.command.gui \
	com.milk.uberchat \
	com.milk.uberchat.event \
	com.milk.uberchat.iface \
	com.milk.uberchat.base \
	com.milk.uberchat.command \
	com.milk.uberchat.spacebar \
	com.milk.uberchat.icb \
	com.milk.uberchat.irc \
	com.milk.uberchat.gui

all:
	mkdir -p classes
	javac -d classes com/milk/util/*.java
	javac -d classes com/milk/util/test/*.java
	javac -d classes com/milk/timer/*.java
	javac -d classes com/milk/asynch/*.java
	javac -d classes com/milk/gui/*.java
	javac -d classes \
		com/milk/objed/*.java \
		com/milk/objed/event/*.java
	javac -d classes com/milk/objed/gui/*.java
	javac -d classes com/milk/objed/test/*.java
	javac -d classes com/milk/command/*.java
	javac -d classes com/milk/command/gui/*.java
	javac -d classes \
		com/milk/uberchat/*.java \
		com/milk/uberchat/iface/*.java \
		com/milk/uberchat/event/*.java
	javac -d classes com/milk/uberchat/command/*.java
	javac -d classes com/milk/uberchat/base/*.java
	javac -d classes com/milk/uberchat/spacebar/*.java
	javac -d classes com/milk/uberchat/icb/*.java
	javac -d classes com/milk/uberchat/irc/*.java
	javac -d classes com/milk/uberchat/gui/*.java

dist: doc
	mkdir -p $(DISTNAME)/bin
	mkdir -p $(DISTNAME)/info
	-cp bin/* $(DISTNAME)/bin
	-cp info/* $(DISTNAME)/info
	mkdir -p $(DISTNAME)/lib
	rm -f $(DISTNAME)/lib/uberchat.jar
	cd classes; jar cfm ../$(DISTNAME)/lib/uberchat.jar ../manifest.txt com
	rm -f $(DISTNAME)/src.tar
	tar --create --file=$(DISTNAME)/src.tar \
		--exclude='*~' --exclude='#*' --exclude='.??*' \
		Makefile manifest.txt com
	tar --create --file=$(DISTNAME)/javadoc.tar \
		--exclude='*~' --exclude='#*' --exclude='.??*' javadoc
	rm -f $(DISTNAME).tar
	tar --create --file=$(DISTNAME).tar \
		--exclude='*~' --exclude='#*' --exclude='.??*' \
		$(DISTNAME)
	rm -f $(DISTNAME).tar.gz
	gzip -9 $(DISTNAME).tar

doc:
	mkdir -p javadoc
	javadoc -J-mx64M -sourcepath .:$(CLASSPATH) -d javadoc \
		-author -private $(PACKAGES)

run: run-gui

run-gui:
	export CLASSPATH=$(CLASSPATH):`pwd`/classes; \
		java com.milk.uberchat.gui.Main

# run-console:
#	export CLASSPATH=$(CLASSPATH):`pwd`/classes; \
#		java com.milk.uberchat.RawMain

test-objed:
	export CLASSPATH=$(CLASSPATH):`pwd`/classes; \
		java com.milk.objed.test.GuiTest

clean:
	rm -rf classes
	rm -rf javadoc
	rm -rf $(DISTNAME)
