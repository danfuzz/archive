README for UberChat
-------------------

###########
# PLEASE NOTE: This is a snapshot of a work-in-progress. It has bugs. It is
# missing functionality. It is not done. I *do* appreciate detailed
# crash/hang reports (including stack traces/debugging VM dumps, and as
# much salient info as can be provided), and your feature requests won't in
# fact fall on deaf ears, but please bear in mind that fixes and new
# features may be slow in coming, as I am but one person and I am not paid
# to work on this. It's a labor of love. 
#
# -dan
###########

UberChat is a "generic" gui chat client system, including a set of chat
framework classes, chat system base classes, and both gui and console
front-ends. It is still a work-in-progress.

The current state of affairs:

* The interfaces and event classes are *mostly* settled.
* The most basic base classes for chat systems are *mostly* done.
* The console front-end is on hold. The interfaces and base classes evolved
out from under it.
* The gui front-end works but is still at the level of prototype code, and
a lot of functionality is still missing from it.
* Three protocols have been (partially) implemented.
* Running the thing at all still requires you to be a bit of a computerhead.

UberChat requires a Java 1.1.* or 1.2 VM. If using 1.1.*, you must also
install Swing (aka JFC; available at <http://www.javasoft.com/products/jfc/>).

To run UberChat:

* On Unix (and variants): If you're running Java 1.1.*, first make sure
your CLASSPATH contains the Swing classes (e.g., the file "swingall.jar").
Simply run the "uberchat" script in the bin directory.

* On the Mac: Obtain the Macintosh UberChat Launcher from:
    <http://www.milk.com/kodebase/downloads/macuberchat.sit.hqx>
Follow the instructions in the README file included with that package.

* On Windows: Someone needs to write and send me a .BAT file and directions.

* Generic Java: If you're running Java 1.1.*, first make sure your
CLASSPATH contains the Swing classes (e.g., the file "swingall.jar"). Put
the file "lib/uberchat.jar" (part of this distribution) in your CLASSPATH,
and then execute the main class "com.milk.uberchat.gui.Main".

UberChat has been successfully run on:

  OS                Arch       Runtime              Swing
  ----------------  -------    ---------------      -----
  RedHat Linux 5.2  Pentium    blackdown:JDK/1.1.7  1.1
  RedHat Linux 5.1  Pentium    blackdown:JDK/1.1.5  1.1
  MacOS 8.1         PowerPC    MRJ 2.1:JDK/1.1.6    1.1 (some anomalies)
  Windows 98        P-II       Javasoft:JDK/1.2     -
  Windows 98        AMD K6-2   Javasoft:JRE/1.2     -
  Solaris 2.6       Sparc      Javasoft:JDK/1.2     - (some anomolies)

If you successfully run UberChat on any other platform, please contact
the author.

Thanks to Uke, Gordie, Sax, Faried, Dmose, and Jim for having the guts to
try this puppy out. Special thanks to Jim for doing all the funky Mac
stuff.

###############################################################################

Note for spacebar users:

The spacebar client plays with the format strings (command "/*") in order
to unambiguously (or, rather, less ambiguously) parse messages coming in
from the server. If you have a problem with this, then *don't* use the
spacebar client. Also, if you *do* use this, and then want to go back to
just using telnet, then you'll have to use the command "/*d" to restore
your settings back to a readable form.

###############################################################################

Note for IRC users:

The code right now has a couple of linear searches and a few other
inefficiencies which make using UberChat slow to a crawl if you are so bold
as to try to get the full list of users or channels from an IRC server
that's part of the big public IRC network. It's much happier connecting to
private, mostly-unconnected servers. The default host listed for IRC is
such a server; in particular, it is the IRC server used by people who are
working on Mozilla and a couple other open source projects.

###############################################################################

Note for Macintosh users:

I don't know what the "popup menu mouse action" is for the Mac. If you
figure it out, let me know. I suspect it's either click-and-hold, or a
combo like ctrl-click or command-click, or something like that.

###############################################################################

Note for Windows users:

I don't know what the "popup menu mouse action" is for Windows. If you
figure it out, let me know. I suspect it's right-click, but I could be
wrong.

###############################################################################

Note for Unix users:

The "popup menu mouse action" is right-click.

The AWT implementation for Unix/X platforms knows about the X CLIPBOARD but
not the X PRIMARY selection. What this amounts to is that while you don't
have the normal X behavior of "select something, and other apps immediately
know about it," you still can get data in and out of the app with a little
help. First of all, by default, the keys ctrl-x, ctrl-c and ctrl-v are
bound to cut, copy, and paste, respectively. You should be able to use
these to move data around within UberChat with no problem.

If you want to interoperate with a Motif app (e.g., Netscape/Mozilla), just
use the File/{Cut,Copy,Paste} menu items provided by the Motif app, as
these use the CLIPBOARD selection to do their business. End of story.

Xterm is a little nastier. It's probably not set up to deal with the
CLIPBOARD by default, so you'll have to muck about with your X resources.
If you add the following resource (changing the name of the app
appropriately, to match whatever it's actually called on your system), you
will now be able to deal with the CLIPBOARD by using the meta (aka alt) key
along with the normal mouseclicks:

    xterm*VT100.Translations: #override\n\
        Meta <Btn1Down> : select-start()\n\
        Meta <Btn1Motion> : select-extend()\n\
        Meta <Btn1Up> : select-end(CLIPBOARD)\n\
        Meta <Btn2Down> : ignore()\n\
        Meta <Btn2Up> : insert-selection(CLIPBOARD)\n\
        Meta <Btn3Down> : start-extend()\n\
        Meta <Btn3Motion> : select-extend()\n\
        Meta <Btn3Up> : select-end(CLIPBOARD)

Emacs is also a bit weird. First of all, this may not work for versions of
emacs prior to 20, and may only work on regular emacs (not xemacs). It
turns out that emacs already binds the meta (aka alt) button clicks almost
correctly, except it uses the almost-unheard-of SECONDARY selection. This
little bit of code, if placed in your ".emacs" file, should fool it into
*actually* using the CLIPBOARD selection:

    (defvar orig-xgs (symbol-function 'x-get-selection) 
	    "original x-get-selection")
    (defvar orig-xss (symbol-function 'x-set-selection) 
            "original x-set-selection")
    (defun x-get-selection (&optional name type)
      (funcall orig-xgs (if (eq name 'SECONDARY) 'CLIPBOARD name) type))
    (defun x-set-selection (&optional name type)
      (funcall orig-xss (if (eq name 'SECONDARY) 'CLIPBOARD name) type))

###############################################################################

Quick tutorial:

Run the program. Click "icb". Enter the following fields:

  name:     <whatever you want>
  host:     icb.evolve.com
  port:     7326
  email:    <your email userid, just the userid--not "@whatever">
  userid:   <your name as it will appear on chat messages>
  password: <may be left blank unless you've registered with the server>
  channel:  <may be left blank>

(Note that as of this writing, the author hangs out on channel "maz".)

Select "connect" from the popup menu by doing the popup menu mouse action
over the system, which should appear just below the buttons. Eventually you
should see a system messages window pop up. You can mostly ignore it, as
not much will be displayed there after the initial login messages. Go back
to the main window and click on the identity, which should have appeared
just below the system. This should cause the two panes below the
system/identity to show the channel that you are on, in the middle panel,
and your userid, in the bottom panel.

The middle panel in general shows the channels that are known, and each
channel may be opened to reveal the users that have been seen on that channel.
The bottom panel in general shows the users that are known, and each user
may be opened to reveal the channels that they are on.

You may select a channel and click on "new chat window" to make a window
for interacting on that channel. However, you must also be joined with
that channel in order to actually do anything on a channel. You may select
a user and click on "new chat window" to make a window for interacting
one-on-one with a particular user. You do not need to "join" with a user
to send them messages.

When you get messages for a channel or user that you don't already have
a window for, a new window will pop up for you. If one hasn't already
popped up for the channel that you originally logged on to, then make one
now by clicking on the channel and then on "new chat window". Type
something. Say hello or whatever. If there are other people there, they
may say hi back.

However, you may have picked a channel that nobody else is on. You can
determine this with the "update users" and "update channels" menu
selections. To make the channels and users panels show more up-to-date
information, use the popup menu for anything but the system itself (i.e.,
identities, channels or users). "Update users" causes a query of the users
under that selection (e.g., find out who all is logged on or just who is on
a particular channel) and "Update channels" causes a query of the channels
for a particular selection (e.g., what channels exist at all or what
channels a particular user is on).

You can switch channels by first leaving the channel you are already on,
and then joining a new channel. Leave the channel you are on by selecting
the "leave" menu item for that channel. Join a new channel by selecting
the "join" menu item for the channel.

Yes, the author knows it's all still very clumsy. He's working on it.

That's about it. Good luck.
