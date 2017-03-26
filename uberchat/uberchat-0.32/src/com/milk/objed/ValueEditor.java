// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.objed;

import com.milk.util.BadValueException;
import com.milk.util.ImmutableException;

/**
 * This interface is for editors which are effictively just editing
 * a single value, such as a string or boolean value.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public interface ValueEditor
extends Editor
{
    /**
     * Get the current value from this editor. If this editor is imposing
     * interesting restrictions on the value, and somehow the value doesn't
     * actually abide by the restrictions, this method may throw
     * <code>BadValueException</code>.
     *
     * @return the current value
     * @exception BadValueException thrown if the value is bad in some
     * way
     */
    public Object getValue ();

    /**
     * Set a new value for the editor.
     *
     * @param value the new value
     * @exception BadValueException thrown if the value is inappropriate
     * for this editor
     * @exception ImmutableException thrown if the editor is in fact
     * immutable
     */
    public void setValue (Object value)
    throws BadValueException, ImmutableException;
}
