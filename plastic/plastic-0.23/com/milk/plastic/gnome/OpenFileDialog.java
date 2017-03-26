package com.milk.plastic.gnome;

import com.milk.plastic.runner.Main;
import gtk.GtkFileSelection;

/**
 * Open file dialog box support.
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
final public class OpenFileDialog
{
    /** null-ok; the dialog box */
    static private GtkFileSelection theDialog = null;
 
    // ------------------------------------------------------------------------
    // public static methods

    /**
     * Show the open file dialog, if it's not already showing. Create
     * it first if need be.
     */
    static public void show ()
    {
	if (theDialog == null)
	{
	    theDialog = new GtkFileSelection ("Open a file");

            theDialog.signalConnect ("destroy", 
				     "destroyed", 
				     OpenFileDialog.class);
	    theDialog.getCancelButton ().
		signalConnect ("clicked", "hide", theDialog);
	    theDialog.getOkButton ().
	        signalConnect ("clicked", "okay", OpenFileDialog.class);
	}

	theDialog.show ();
    }

    /**
     * Called when the dialog box is destroyed. This happens when the
     * user clicks on the "close" button of the window, or takes other
     * equivalent action.
     */
    static public void destroyed ()
    {
	theDialog = null;
    }

    /**
     * Called when the user says "ok" indicating they want to open a
     * file.
     */
    static public void okay ()
    {
	final String name = theDialog.getFilename ();
	theDialog.hide ();

	Thread t = new Thread ()
	{
	    public void run ()
	    {
		Main.loadAndPlay (name);
	    }
	};

	t.start ();
    }
}
