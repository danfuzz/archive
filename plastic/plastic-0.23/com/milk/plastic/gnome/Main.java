package com.milk.plastic.gnome;

import com.milk.plastic.Plastic;
import gnome.Gnome;
import gnome.GnomeAbout;
import gnome.GnomeApp;
import gtk.Gtk;
import gtk.GtkMenu;
import gtk.GtkMenuBar;
import gtk.GtkMenuItem;
import java.io.File;

/**
 * Main harness for the GNOME front-end for Plastic.
 *
 * <p>Copyright (C) 2000, Dan Bornstein, danfuzz@milk.com. All Rights 
 * Reserved. (Shrill TV degreaser.)</p>
 * 
 * <p>This file is part of the MILK Kodebase. The contents of this file are
 * subject to the MILK Kodebase Public License; you may not use this file
 * except in compliance with the License. A copy of the MILK Kodebase Public
 * License has been included with this distribution, and may be found in the
 * file named "LICENSE.html". You may also be able to obtain a copy of the
 * License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!</p>
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class Main
{
    /** the lib directory where the jarfile lives, including a trailing
     * separator character */
    static private String theLibDir;

    /** the app object */
    private GnomeApp myApp;

    /** null-ok; the about window */
    private GnomeAbout myAbout;

    // ------------------------------------------------------------------------
    // static initialization

    static
    {
	// this assumes we're running in a jar application file, meaning
	// there's only one entry in the classpath
	File jarfile = new File (System.getProperty ("java.class.path"));
	theLibDir = jarfile.getParent ();
	if (! theLibDir.endsWith (File.separator))
	{
	    theLibDir += File.separator;
	}
    }

    // ------------------------------------------------------------------------
    // public static methods

    /**
     * Do the main thang.
     */
    static public void main (String[] args)
    {
	String[] newArgs = new String[args.length + 1];
	System.arraycopy (args, 0, newArgs, 1, args.length);
	newArgs[0] = "plastic";

	Gnome.init (Plastic.APP_NAME, 
		    Plastic.APP_VERSION, 
		    newArgs.length, 
		    newArgs);
	Main m = new Main ();
	Gtk.main ();

	// the audio system leaves some non-daemon threads around, damn its
	// blasted heart
	System.exit (0);
    }

    // ------------------------------------------------------------------------
    // constructors

    /**
     * This class is not publicly instantiable.
     */
    private Main ()
    {
	createMainWindow ();
	createMenus ();
	myApp.show ();
    }

    // ------------------------------------------------------------------------
    // private instance methods

    /**
     * Create the main window.
     */
    private void createMainWindow ()
    {
	myApp = new GnomeApp (Plastic.APP_NAME, Plastic.APP_NAME);
	myApp.setPolicy (false, true, false);
	myApp.setWmclass (Plastic.APP_NAME, Plastic.APP_NAME + "App");

	myApp.setUsize (150, 100);
	myApp.signalConnect ("delete_event", this);
	myApp.signalConnect ("destroy", this);
    }

    /**
     * Create the menus on the main window.
     */
    private void createMenus ()
    {
	GtkMenuBar menubar = new GtkMenuBar ();
	GtkMenuItem file = new GtkMenuItem ("File");
	GtkMenuItem help = new GtkMenuItem ("Help");
	menubar.append (file);
	menubar.append (help);

	// finish the file menu
	GtkMenu fileMenu = new GtkMenu ();
	file.setSubmenu (fileMenu);

	GtkMenuItem fileOpen = new GtkMenuItem ("Open");
	fileOpen.signalConnect ("activate", "fileOpen", this);
	fileMenu.append (fileOpen);
	fileOpen.show ();

	GtkMenuItem fileQuit = new GtkMenuItem ("Quit");
	fileQuit.signalConnect ("activate", "fileQuit", this);
	fileMenu.append (fileQuit);
	fileQuit.show ();

	file.show ();
         
	// finish the help menu
	GtkMenu helpMenu = new GtkMenu ();
	GtkMenuItem helpAbout = new GtkMenuItem ("About");
	helpAbout.signalConnect ("activate", "helpAbout", this);
	help.setSubmenu (helpMenu);
	helpMenu.append (helpAbout);
	helpAbout.show ();
	help.show ();

	menubar.show ();
	myApp.setMenus (menubar);
    }

    // ------------------------------------------------------------------------
    // public instance methods

    public boolean delete_event (int value)
    {
	return true;
    }

    public void destroy ()
    {
	fileQuit ();
    }

    public void fileQuit () 
    {
	Gtk.mainQuit ();
    }

    public void fileOpen () 
    {
	OpenFileDialog.show ();
    }

    public void helpAbout () 
    {
	if (myAbout == null)
	{
	    String title = Plastic.APP_NAME;
	    String version = "Version " + Plastic.APP_VERSION;
	    String license = "MILK Kodebase";
	    String[] authors = { "Dan Bornstein <danfuzz@milk.com>" };
	    String comments = 
		"This is the Gnome front-end for the " + Plastic.APP_NAME + 
		" suite of tools and applications.";
	    String pixmap = theLibDir + "plas.png";
	    myAbout = new GnomeAbout (title, version, license, 
				      authors, comments, pixmap);
	}

	myAbout.show ();
    }
}
