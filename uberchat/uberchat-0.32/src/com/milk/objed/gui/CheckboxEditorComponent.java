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
import com.milk.objed.CheckboxEditor;
import com.milk.objed.event.EditorEvent;
import com.milk.objed.event.EditorListener;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This is the component for interacting with <code>CheckboxEditor</code>s. It
 * makes a checkbox field and attempts to set the value, as appropriate.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public class CheckboxEditorComponent
extends JCheckBox
implements EditorComponent, EditorListener, ChangeListener
{
    /** the editor we interact with */
    private CheckboxEditor myEditor;

    /**
     * Construct a <code>CheckboxEditorComponent</code> to interact with
     * the given editor.
     *
     * @param editor the editor to interact with
     */
    public CheckboxEditorComponent (Editor editor)
    {
	myEditor = (CheckboxEditor) editor;
	editor.addListener (this);
	addChangeListener (this);
	setText (myEditor.getCheckboxText ());
	setEnabled (myEditor.isMutable ());
	setSelected (((Boolean) myEditor.getValue ()).booleanValue ());
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
	return new CheckboxEditorComponent (editor);
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
	setSelected (((Boolean) myEditor.getValue ()).booleanValue ());
    }

    public void descriptionChanged (EditorEvent event)
    {
	// ignore it
    }

    public void mutabilityChanged (EditorEvent event)
    {
	setEnabled (myEditor.isMutable ());
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
    // ChangeListener interface methods

    /**
     * Called when the user changes the checkbox state.
     *
     * @param event the event
     */
    public void stateChanged (ChangeEvent e)
    {
	myEditor.setValue (isSelected () ? Boolean.TRUE : Boolean.FALSE);
    }
}
