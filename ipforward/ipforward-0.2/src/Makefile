DISTNAME=ipforward-0.2
JARNAME=ipforward.jar

PACKAGES=\
	com.milk.ipforward

all:
	mkdir -p classes
	javac -d classes com/milk/ipforward/*.java

dist: javadoc
	mkdir -p $(DISTNAME)
	cp -r bin $(DISTNAME)
	cp -r info $(DISTNAME)
	mkdir -p $(DISTNAME)/lib
	rm -f $(DISTNAME)/lib/$(JARNAME)
	cd classes; jar cf ../$(DISTNAME)/lib/$(JARNAME) com
	rm -f $(DISTNAME)/src.tar
	tar --create --file=$(DISTNAME)/src.tar \
		--exclude='*~' --exclude='#*' --exclude='.??*' \
		Makefile setup-environment com
	rm -f $(DISTNAME).tar
	tar --create --file=$(DISTNAME).tar \
		--exclude='*~' --exclude='#*' --exclude='.??*' \
		$(DISTNAME)
	rm -f $(DISTNAME).tar.gz
	gzip -9 $(DISTNAME).tar

javadoc:
	mkdir -p doc
	javadoc -J-mx64M -sourcepath .:$(CLASSPATH) -d doc -author -private \
		$(PACKAGES)

clean:
	rm -rf classes
	rm -rf doc
	rm -rf $(DISTNAME)
