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
import com.milk.gui.JFrame;
import com.milk.objed.gui.EditorControl;
import com.milk.objed.gui.EditorPanel;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.Box;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This is a panel which has an editor for a command argument, along
 * with "ok" and "cancel" buttons. The "ok" button performs the command,
 * and the "cancel" button dismisses the panel.
 * BUG--we need a generic <code>ErrorSink</code>-type class so that
 * exceptions aren't thrown in unknown (and really unknowable) contexts.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public class CommandPanel
extends EditorPanel
{
    /** the command to use */
    private Command myCommand;

    /** the argument */
    private Object myArgument;

    /** null-ok; the frame we are in, if any */
    private JFrame myFrame;

    /** the label for the command */
    private JLabel myLabel;

    /** the ok button */
    private JButton myOkButton;

    /** null-ok; the keep open checkbox, if the panel has a frame */
    private JCheckBox myKeepOpenBox;

    /** the listener to the command */
    private MyCommandListener myCommandListener;

    /**
     * Create a <code>CommandPanel</code>.
     *
     * @param command the command to execute
     * @param argument the argument to edit
     * @param editorControl the editor control to use
     * @param frame null-ok; the frame this panel is in, if any
     */
    public CommandPanel (Command command,
			 Object argument,
			 EditorControl editorControl,
			 JFrame frame)
    {
	super (editorControl.makeComponent (editorControl.edit (argument)),
	       frame);

	myCommand = command;
	myArgument = argument;
	myFrame = frame;

	JPanel panel = (JPanel) getViewport ().getView ();

	panel.add (Box.createVerticalStrut (4));
	panel.add (Box.createVerticalGlue ());

	Box descriptionBox = Box.createHorizontalBox ();
	panel.add (descriptionBox);
       	myLabel = new JLabel (command.getDescription ());
	descriptionBox.add (Box.createHorizontalGlue ());
	descriptionBox.add (myLabel);
	descriptionBox.add (Box.createHorizontalGlue ());

	panel.add (Box.createVerticalStrut (4));

	Box buttonBox = Box.createHorizontalBox ();
	buttonBox.add (Box.createHorizontalStrut (4));
	panel.add (buttonBox);
	buttonBox.add (Box.createHorizontalGlue ());
	myOkButton = new JButton ("OK");
	buttonBox.add (myOkButton);
	buttonBox.add (Box.createHorizontalStrut (4));
	buttonBox.add (Box.createHorizontalGlue ());
	JButton cancelButton = new JButton ("Cancel");
	buttonBox.add (cancelButton);
	buttonBox.add (Box.createHorizontalStrut (4));
	buttonBox.add (Box.createHorizontalGlue ());
	if (myFrame != null)
	{
	    myKeepOpenBox = new JCheckBox ("Keep Open");
	    buttonBox.add (myKeepOpenBox);
	    buttonBox.add (Box.createHorizontalStrut (4));
	    buttonBox.add (Box.createHorizontalGlue ());
	}

        // register a listener for the buttons
	MyButtonListener listener = new MyButtonListener ();
	myOkButton.addActionListener (listener);
	cancelButton.addActionListener (listener);

	// register a listener for the command, to track enabled changes
	// and description changes
	myCommandListener = new MyCommandListener ();
	myCommand.addListener (myCommandListener);

	// register a listener for the window, to unhook stuff when it
	// closes (boy I wish weak refs were here already!)
	if (frame != null)
	{
	    frame.addWindowListener (new MyWindowListener ());
	}
    }

    /**
     * Create and return a frame with a <code>CommandPanel</code> in it.
     *
     * @param command the command to execute
     * @param argument the argument to edit
     * @param editorControl the editor control to use
     */
    public static JFrame makeFrame (Command command,
				    Object argument,
				    EditorControl editorControl)
    {
	JFrame frame = new JFrame ();
	CommandPanel panel = 
	    new CommandPanel (command, argument, editorControl, frame);
	frame.setDefaultCloseOperation (frame.DISPOSE_ON_CLOSE);
	frame.pack ();
	return frame;
    }

    /**
     * Create and show a frame with a <code>CommandPanel</code> in it.
     *
     * @param command the command to execute
     * @param argument the argument to edit
     * @param editorControl the editor control to use
     */
    public static void showFrame (Command command,
				  Object argument,
				  EditorControl editorControl)
    {
	JFrame frame = makeFrame (command, argument, editorControl);
	frame.setVisible (true);
    }

    // ------------------------------------------------------------------------
    // Private helper classes

    /** 
     * This is an <code>ActionListener</code> that listens to the buttons.
     */
    private class MyButtonListener 
    extends AbstractAction 
    {
	public void actionPerformed (ActionEvent e) 
	{
	    if (   (myFrame != null)
		&& (! myKeepOpenBox.isSelected ()))
	    {
		myFrame.dispose ();
	    }

	    Object source = e.getSource ();

	    if (source != myOkButton)
	    {
		// cancelled
		return;
	    }

	    myCommand.run (myArgument);
	}
    }

    /**
     * This is a listener to the command for tracking description and enabled
     * changes.
     */
    private class MyCommandListener
    implements CommandListener
    {
	public void descriptionChanged (CommandEvent event)
	{
	    myLabel.setText (event.getDescription ());
	}

	public void labelChanged (CommandEvent event)
	{
	    // BUG--should have option to not set frame title ever?
	    if (myFrame == null)
	    {
		return;
	    }

	    myFrame.setTitle (event.getLabel ());
	}
	
	public void enabledChanged (CommandEvent event)
	{
	    myOkButton.setEnabled (event.getEnabled ());
	}
    }

    /**
     * This is a listener to the window for noticing when it closes.
     */
    private class MyWindowListener
    extends WindowAdapter
    {
	public void windowClosing (WindowEvent e)
	{
	    myCommand.removeListener (myCommandListener);
	}
    }
}

