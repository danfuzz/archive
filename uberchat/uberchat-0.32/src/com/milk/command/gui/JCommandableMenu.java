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

import com.milk.command.Command;
import com.milk.command.CommandEvent;
import com.milk.command.CommandListener;
import com.milk.command.Commandable;
import com.milk.command.CommandableEvent;
import com.milk.command.CommandableListener;
import com.milk.objed.gui.EditorControl;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * This class is a <code>JPopupMenu</code> subclass which is set up to track
 * a <code>Commandable</code>'s commands. The list of commands and their
 * enabled state are reflected directly as the menu items in the popup
 * menu. No explicit action should be taken to manipulate the item list.
 *
 * @see com.milk.command.Commandable
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class JCommandableMenu
extends JPopupMenu
{
    /** the commandable to track */
    private Commandable myCommandable;

    /** the editor control to use */
    private EditorControl myEditorControl;

    /** the listener to the commandable */
    private MyCommandableListener myCommandableListener;

    /** the listener to the commands */
    private MyCommandListener myCommandListener;

    /** vector of actions, in order */
    private Vector myActions;

    /** the (intentionally private) object to synch on */
    private Object mySynch;

    /** the sacrificial menu item to use to work around a
     * <code>JPopupMenu</code> bug */
    private JMenuItem mySacrificialMenuItem;

    /**
     * Create a <code>JCommandableMenu</code.
     *
     * @param commandable the commandable to show
     * @param editorControl the editor control to use for editing non-null
     * arguments
     */
    public JCommandableMenu (Commandable commandable, 
			     EditorControl editorControl)
    {
	myCommandable = commandable;
	myEditorControl = editorControl;
	myCommandableListener = new MyCommandableListener ();
	myCommandListener = new MyCommandListener ();
	myActions = new Vector ();
	mySynch = myCommandableListener; // good enough
	mySacrificialMenuItem = add ("sacrifice");
    }

    /**
     * Create and show a menu for the given commandable.
     *
     * @param commandable the commandable to show a menu for
     * @param editorControl the editor control to use for editing non-null
     * arguments
     * @param invoker the parent component to show the menu in
     * @param x the x offset to show it at
     * @param y the y offset to show it at
     */
    public static void show (Commandable commandable, 
			     EditorControl editorControl, Component invoker,
			     int x, int y)
    {
	JCommandableMenu menu = new JCommandableMenu (commandable,
						      editorControl);
	menu.setInvoker (invoker);
	menu.show (invoker, x, y);
    }

    // ------------------------------------------------------------------------
    // Protected methods we override

    protected void firePopupMenuWillBecomeVisible ()
    {
	synchronized (mySynch)
	{
	    remove (0); // get rid of the sacrificial menu item
	    myCommandable.addListener (myCommandableListener);
	}
	super.firePopupMenuWillBecomeVisible ();
    }

    protected void firePopupMenuWillBecomeInvisible ()
    {
	synchronized (mySynch)
	{
	    myCommandable.removeListener (myCommandableListener);
	    for (int i = myActions.size () - 1; i >= 0; i--)
	    {
		MyAction a = (MyAction) myActions.elementAt (i);
		a.myCommand.removeListener (myCommandListener);
		removeAction (a);
	    }
	    add (mySacrificialMenuItem); // re-add the sacrificial menu item
	}
	super.firePopupMenuWillBecomeInvisible ();
    }

    // ------------------------------------------------------------------------
    // Private helper methods

    /**
     * Find the action associated with the given command.
     *
     * @param command the command to find
     * @return the associated action
     */
    private MyAction findActionFor (Command command)
    {
	int sz = myActions.size ();
	for (int i = 0; i < sz; i++)
	{
	    MyAction a = (MyAction) myActions.elementAt (i);
	    if (a.myCommand == command)
	    {
		return a;
	    }
	}

	return null;
    }

    /**
     * Add a new action, inserting it alphabetically according to
     * its label.
     *
     * @param action the action to add
     */
    private void addAction (MyAction action)
    {
	String lowerLabel = action.myLowerLabel;
	int sz = myActions.size ();

	int at;
	for (at = 0; at < sz; at++)
	{
	    MyAction a = (MyAction) myActions.elementAt (at);
	    if (lowerLabel.compareTo (a.myLowerLabel) < 0)
	    {
		break;
	    }
	}

	if (at == sz)
	{
	    myActions.addElement (action);
	    add (action);
	}
	else
	{
	    myActions.insertElementAt (action, at);
	    // stupid JPopupMenu advertises this method, but it
	    // throws an "Error: not implemented":
	    //     insert (action, at);
	    // so we are forced into this workaround. Major suckage.
	    for (int i = sz - 1; i >= at; i--)
	    {
		remove (i);
	    }
	    add (action);
	    sz++;
	    for (int i = at + 1; i < sz; i++)
	    {
		add ((MyAction) myActions.elementAt (i));
	    }
	}
    }

    /**
     * Remove an action.
     *
     * @param action the action to remove
     */
    private void removeAction (MyAction action)
    {
	int sz = myActions.size ();

	int at;
	for (at = 0; at < sz; at++)
	{
	    MyAction a = (MyAction) myActions.elementAt (at);
	    if (a == action)
	    {
		break;
	    }
	}

	if (at == sz)
	{
	    // not found
	    return;
	}

	myActions.removeElementAt (at);
	remove (at);
    }
	

    // ------------------------------------------------------------------------
    // Private helper classes

    /**
     * This is the class for all of the actions in the menu.
     */
    private class MyAction
    extends AbstractAction
    {
	/** the label for the action */
	/*package*/ String myLowerLabel;

	/** the command for the action */
	/*package*/ Command myCommand;

	public MyAction (Command command)
	{
	    super (command.getLabel (), null);
	    myCommand = command;
	    myLowerLabel = command.getLabel ().toLowerCase ();
	    putValue (SHORT_DESCRIPTION, command.getDescription ());
	}

	public void actionPerformed (ActionEvent e)
	{
	    Object argument = myCommand.makeArgument ();
	    if (argument == null)
	    {
		// null argument; run it directly
		myCommand.run (null);
	    }
	    else
	    {
		CommandPanel.showFrame (myCommand, argument, myEditorControl);
	    }
	}
    }

    /**
     * This is the listener for all the commands, used to notice label
     * and enabled changes.
     */
    private class MyCommandListener
    implements CommandListener
    {
	public void descriptionChanged (CommandEvent event)
	{
	    synchronized (mySynch)
	    {
		MyAction a = findActionFor (event.getCommand ());
		a.putValue (a.SHORT_DESCRIPTION, event.getDescription ());
	    }
	}

	public void labelChanged (CommandEvent event)
	{
	    synchronized (mySynch)
	    {
		MyAction a = findActionFor (event.getCommand ());
		removeAction (a);
		String label = event.getLabel ();
		a.myLowerLabel = label.toLowerCase ();
		a.putValue (a.NAME, label);
		addAction (a);
	    }
	}

	public void enabledChanged (CommandEvent event)
	{
	    synchronized (mySynch)
	    {
		MyAction a = findActionFor (event.getCommand ());
		a.setEnabled (event.getEnabled ());
	    }
	}
    }

    /**
     * This is the listener to the commandable, used to add and remove
     * items from the menu.
     */
    private class MyCommandableListener
    implements CommandableListener
    {
	public void commandAdded (CommandableEvent event)
	{
	    synchronized (mySynch)
	    {
		Command c = event.getCommand ();
		MyAction a = new MyAction (c);
		addAction (a);
		c.addListener (myCommandListener);
	    }
	}

	public void commandRemoved (CommandableEvent event)
	{
	    synchronized (mySynch)
	    {
		Command c = event.getCommand ();
		c.removeListener (myCommandListener);
		MyAction a = findActionFor (c);
		removeAction (a);
	    }
	}
    }
}
