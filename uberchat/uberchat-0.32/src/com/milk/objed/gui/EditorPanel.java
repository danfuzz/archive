// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.objed.gui;

import com.milk.gui.JFrame;
import com.milk.objed.Editor;
import com.milk.objed.event.EditorAdapter;
import com.milk.objed.event.EditorEvent;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

/**
 * This class is responsible for making the panel trimmings around
 * an arbitrary editor component.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public class EditorPanel
extends JScrollPane
implements EditorComponent
{
    /** non-null; the editor component to display and interact with */
    EditorComponent myEditorComponent;

    /** null-ok; the frame we are in */
    JFrame myFrame;

    /** non-null; the editor to interact with */
    Editor myEditor;

    /** non-null; the listener that was added to the editor */
    MyEditorListener myEditorListener;

    /** non-null; the label for the description */
    JLabel myDescription;

    /**
     * Construct an <code>EditorPanel</code> to display the given
     * <code>EditorComponent</code>. The frame, if specified, will be told
     * to make this object its content pane.
     *
     * @param editorComponent non-null; the <code>EditorComponent</code> to
     * display and interact with
     * @param frame null-ok; the frame to add this panel to 
     */
    public EditorPanel (EditorComponent editorComponent, JFrame frame)
    {
	myEditorComponent = editorComponent;
	myFrame = frame;
	myEditor = editorComponent.getEditor ();
	myEditorListener = new MyEditorListener ();

	JPanel panel = new JPanel ();
	panel.setLayout (new BoxLayout (panel, BoxLayout.Y_AXIS));
	panel.setBorder (new EmptyBorder (2, 2, 2, 2));

	Box descriptionBox = Box.createHorizontalBox ();
	panel.add (descriptionBox);
	myDescription = new JLabel ();
	descriptionBox.add (Box.createHorizontalGlue ());
	descriptionBox.add (myDescription);
	descriptionBox.add (Box.createHorizontalGlue ());

	panel.add (Box.createVerticalStrut (4));
	panel.add (Box.createVerticalGlue ());
	panel.add ((Component) editorComponent);

	setViewportView (panel);

	myEditor.addListener (myEditorListener);
	myDescription.setText (myEditor.getDescription ());

	if (frame != null)
	{
	    frame.setContentPane (this);
	    frame.setTitle (myEditor.getLabel ());
	    frame.pack ();
	    frame.addWindowListener (new MyWindowListener ());
	}
    }

    /**
     * Construct and return a frame to hold an <code>EditorPanel</code> to
     * show the given <code>EditorComponent</code>.
     *
     * @param editorComponent non-null; the <code>EditorComponent</code> to
     * display and interact with
     * @return the frame to show the editor
     */
    public static JFrame makeFrame (EditorComponent editorComponent)
    {
	JFrame frame = new JFrame ();
	new EditorPanel (editorComponent, frame);
	frame.setDefaultCloseOperation (frame.DISPOSE_ON_CLOSE);
	frame.pack ();
	return frame;
    }

    // ------------------------------------------------------------------------
    // EditorComponent instance methods

    /**
     * Get the <code>Editor</code> that this object interacts with.
     *
     * @return the editor
     */
    public Editor getEditor ()
    {
	return myEditor;
    }

    // ------------------------------------------------------------------------
    // Private helper classes

    /**
     * This class is the <code>WindowListener</code> for this (outer)
     * object. 
     */
    private class MyWindowListener
    extends WindowAdapter
    {
	public void windowClosing (WindowEvent e)
	{
	    // clean up the listener list of the editor
	    myEditor.removeListener (myEditorListener);
	}
    }

    /**
     * This class is the <code>EditorListener</code> for this (outer)
     * object. 
     */
    private class MyEditorListener
    extends EditorAdapter
    {
	public void descriptionChanged (EditorEvent event)
	{
	    myDescription.setText (event.getDescription ());
	}
    }
}
