// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.command.gui;

import com.milk.command.Commandable;
import com.milk.objed.gui.EditorControl;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * <p>A <code>JCommandableTree</code> is a <code>JTree</code> that knows how to
 * make and show popup menus for items that are <code>Commandable</code>s.
 * In particular, when the popup menu mouse action occurs over an item in
 * the tree, and that item's user object is an instance of
 * <code>Commandable</code>, then a menu of the commands is created and
 * shown. If a menu item is selected, then the command is executed (if the
 * command has a null argument) or a dialog for argument editing is opened
 * (if the command has a non-null argument).</p>
 *
 * @see com.milk.command.Commandable
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public class JCommandableTree
extends JTree
{
    /** the editor control to use */
    private EditorControl myEditorControl = null;

    /** the self-added mouse listener */
    private MyMouseListener myMouseListener;

    /**
     * Construct a <code>JCommandableTree</code> with the default model.
     */
    public JCommandableTree ()
    {
	setupMouseListener ();
    }

    /**
     * Construct a <code>JCommandableTree</code> with the given model.
     *
     * @param model the model to use
     */
    public JCommandableTree (TreeModel model)
    {
	super (model); // heh
	setupMouseListener ();
    }

    /**
     * Set the editor control to use for non-null argument commands.
     *
     * @param editorControl the editor control to use
     */
    public void setEditorControl (EditorControl editorControl)
    {
	myEditorControl = editorControl;
    }

    // ------------------------------------------------------------------------
    // Private helper methods
   
    /**
     * Set up the mouse listener.
     */
    private void setupMouseListener ()
    {
	myMouseListener = new MyMouseListener ();
	addMouseListener (myMouseListener);
    }

    // ------------------------------------------------------------------------
    // Private helper classes

    /**
     * This is the <code>MouseListener</code> which gets added to this
     * (outer) object in order to handle firing off actions.
     */
    private class MyMouseListener
    extends MouseAdapter
    {
	public void mousePressed (MouseEvent e)
	{
	    if (e.isPopupTrigger ())
	    {
		int x = e.getX ();
		int y = e.getY ();
		TreePath tp = getPathForLocation (x, y);
		if (tp == null)
		{
		    return;
		}

		Object source = tp.getPathComponent (tp.getPathCount () - 1);
		if (source instanceof DefaultMutableTreeNode)
		{
		    source = 
			((DefaultMutableTreeNode) source).getUserObject ();
		}

		if (source instanceof Commandable)
		{
		    JCommandableMenu.show ((Commandable) source, 
					   myEditorControl,
					   JCommandableTree.this,
					   x, y);
		}
	    }
	}
    }
}
