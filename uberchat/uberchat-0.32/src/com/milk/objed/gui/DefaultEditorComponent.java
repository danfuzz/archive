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
import javax.swing.JLabel;

/**
 * This is the default, and mostly non-functional, component for interacting
 * with editors that don't have any special class for them.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public class DefaultEditorComponent
extends JLabel
implements EditorComponent
{
    /** the editor we interact with, nominally */
    private Editor myEditor;

    /**
     * Construct a <code>DefaultEditorComponent</code> to interact with
     * the given editor.
     *
     * @param editor the editor to interact with
     */
    public DefaultEditorComponent (Editor editor)
    {
	// BUG--something better?
	setText ("UNEDITABLE: " + editor.toString ());
	myEditor = editor;
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
}
