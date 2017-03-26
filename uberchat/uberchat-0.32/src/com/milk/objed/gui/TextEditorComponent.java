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

import com.milk.objed.Editor;
import com.milk.objed.TextEditor;
import com.milk.objed.event.EditorEvent;
import com.milk.objed.event.EditorListener;
import com.milk.util.BadValueException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JTextField;
import javax.swing.JOptionPane;

/**
 * This is the component for interacting with <code>TextEditor</code>s. It
 * makes a text field and attempts to set the text whenever focus leaves
 * the object, assuming the underlying value is mutable. It sets the text
 * in the field whenever the editor sends out a <code>valueChanged</code>
 * event.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public class TextEditorComponent
extends JTextField
implements EditorComponent, EditorListener, FocusListener, ActionListener
{
    /** the editor we interact with */
    private TextEditor myEditor;

    /**
     * Construct a <code>TextEditorComponent</code> to interact with
     * the given editor.
     *
     * @param editor the editor to interact with
     */
    public TextEditorComponent (Editor editor)
    {
	myEditor = (TextEditor) editor;
	editor.addListener (this);
	addActionListener (this);
	addFocusListener (this);
	setText ((String) myEditor.getValue ());
	setEditable (editor.isMutable ());
	
	int preferredLength = myEditor.getPreferredLength ();
	if (preferredLength != myEditor.NONE)
	{
	    setColumns (preferredLength);
	}
    }

    /**
     * The method called on by <code>EditorControl</code>.
     *
     * @param editor the editor to interact with
     * @param control the editor control to interact with
     * @return the editor component
     */
    public static EditorComponent makeComponent (Editor editor,
						 EditorControl control)
    {
	if (((TextEditor) editor).getHidden ())
	{
	    return new PasswordEditorComponent (editor);
	}
	else
	{
	    return new TextEditorComponent (editor);
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
	setText ((String) myEditor.getValue ());
    }

    public void descriptionChanged (EditorEvent event)
    {
	// ignore it
    }

    public void mutabilityChanged (EditorEvent event)
    {
	boolean mut = myEditor.isMutable ();
	if (mut)
	{
	    setEditable (true);
	}
	else
	{
	    setEditable (false);
	    // set the text in case the user had an "unsaved" change
	    // when the editor became immutable
	    setText ((String) myEditor.getValue ());
	}
    }

    public void fieldEvent (EditorEvent event)
    {
	// ignore it, but it shouldn't happen
    }

    public void fieldAdded (EditorEvent event)
    {
	// ignore it, but it shouldn't happen
    }

    public void fieldRemoved (EditorEvent event)
    {
	// ignore it, but it shouldn't happen
    }

    // ------------------------------------------------------------------------
    // ActionListener interface methods

    public void actionPerformed (ActionEvent event)
    {
	if (! myEditor.isMutable ())
	{
	    // don't bother if the editor is immutable
	    return;
	}

	// attempt to set the value
	try
	{
	    myEditor.setValue (getText ());
	}
	catch (BadValueException ex)
	{
	    // if we fail, reset the value and alert the user
	    setText ((String) myEditor.getValue ());
	    JOptionPane.showMessageDialog (
                this,
		ex.getMessage (),
		"Bad Value",
		JOptionPane.ERROR_MESSAGE);
	}
    }

    // ------------------------------------------------------------------------
    // FocusListener interface methods

    public void focusGained (FocusEvent event)
    {
	// ignore it
    }

    public void focusLost (FocusEvent event)
    {
	// same as actionPerformed, above
	actionPerformed (null);
    }
}
