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

/**
 * This is the component for interacting with <code>BaseFieldsEditor</code>s,
 * sort of. It just has the appropriate <code>makeComponent</code> method,
 * which in fact returns a <code>FieldsEditorComponent</code>. This
 * is done because the latter is only an interface and won't actually get
 * found via a superclass search (which is what <code>EditorControl</code>
 * does).
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class BaseFieldsEditorComponent
{
    /**
     * This class is not instantiable.
     */
    private BaseFieldsEditorComponent ()
    {
	// this space intentionally left blank
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
	return new FieldsEditorComponent (editor, control);
    }
}
