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

import com.milk.objed.FieldsEditor;
import com.milk.objed.Editor;
import com.milk.objed.event.EditorEvent;
import com.milk.objed.event.EditorListener;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This is the component for interacting with <code>FieldsEditor</code>s. It
 * just makes a bunch of sub-components and puts them all in a panel.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public class FieldsEditorComponent
extends JPanel
implements EditorComponent, EditorListener
{
    /** the editor we interact with */
    private FieldsEditor myEditor;

    /** the (intentionally-private) object to synchronize on */
    private Object mySynch;

    /** the list of fields as we know them, in order */
    private Editor[] myFields;

    /** the labels corresponding to each field, in order */
    private JLabel[] myFieldLabels;

    /** the components corresponding to each field, in order */
    private JComponent[] myFieldComponents;

    /**
     * Construct a <code>FieldsEditorComponent</code> to interact with
     * the given editor and editor control.
     *
     * @param editor the editor to interact with
     * @param control the editor control to interact with
     */
    public FieldsEditorComponent (Editor editor, EditorControl control)
    {
	myEditor = (FieldsEditor) editor;
	mySynch = new Object ();

	GridBagLayout layout = new GridBagLayout ();
	setLayout (layout);
	
	GridBagConstraints con = new GridBagConstraints ();
	con.insets.top = 2;
	con.insets.bottom = 2;

	// synchronize to avoid events coming in and confusing the initial
	// setup process
	synchronized (mySynch)
	{
	    editor.addListener (this);

	    myFields = myEditor.getFields ();
	    myFieldLabels = new JLabel[myFields.length];
	    myFieldComponents = new JComponent[myFields.length];

	    for (int i = 0; i < myFields.length; i++)
	    {
		String tipText = myFields[i].getDescription ();
		JComponent comp = 
		    (JComponent) control.makeComponent (myFields[i]);
		JLabel label = new JLabel (myFields[i].getLabel ());
		comp.setToolTipText (tipText);
		label.setToolTipText (tipText);
		label.setLabelFor (comp);

		con.gridy = i;

		con.insets.left = 12;
		con.insets.right = 2;
		con.gridx = 0;
		con.weightx = 0;
		con.weighty = 0;
		con.fill = con.NONE;
		con.anchor = con.EAST;
		layout.setConstraints (label, con);
		add (label);

		con.insets.left = 2;
		con.insets.right = 12;
		con.gridx = 1;
		con.weightx = 1;
		con.weighty = 1;
		con.fill = con.HORIZONTAL;
		con.anchor = con.CENTER;
		layout.setConstraints (comp, con);
		add (comp);

		myFieldComponents[i] = comp;
		myFieldLabels[i] = label;
	    }
	}
    }

    // ------------------------------------------------------------------------
    // EditorComponent interface methods

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
    // EditorListener interface methods

    public void valueChanged (EditorEvent event)
    {
	// shouldn't happen, but ignore it
    }

    public void descriptionChanged (EditorEvent event)
    {
	// ignore it
    }

    public void mutabilityChanged (EditorEvent event)
    {
	// ignore it
    }

    public void fieldEvent (EditorEvent event)
    {
	// we really care about the sub-event, and even then, we only
	// care about descriptionChanged events so we can update the
	// tip-text
	event = event.getSubEvent ();
	if (event.getType () != EditorEvent.DESCRIPTION_CHANGED)
	{
	    return;
	}

	Editor source = event.getEditor ();
	String tipText = event.getDescription ();

	// synch to avoid clashing with initial setup
	synchronized (mySynch)
	{
	    int foundAt = -1;
	    for (int i = 0; i < myFields.length; i++)
	    {
		if (myFields[i] == source)
		{
		    foundAt = i;
		    break;
		}
	    }

	    if (foundAt == -1)
	    {
		// shouldn't happen, source is an unknown field
		// since we don't handle fieldAdded yet, it could be
		// because of that
		System.err.println ("### BUG--FieldsEditorComponent got " +
				    "unknown field in sub-event");
		return;
	    }

	    myFieldLabels[foundAt].setToolTipText (tipText);
	    myFieldComponents[foundAt].setToolTipText (tipText);
	}
    }

    public void fieldAdded (EditorEvent event)
    {
	// BUG--do something here
	System.err.println ("### BUG--FieldsEditorComponent doesn't know " +
			    "how to add a field!");
    }

    public void fieldRemoved (EditorEvent event)
    {
	// BUG--do something here
	System.err.println ("### BUG--FieldsEditorComponent doesn't know " +
			    "how to remove a field!");
    }
}
