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

/**
 * This interface is for <code>Editor</code>s that manage an object with
 * field-like sub-parts. One can expect <code>fieldEvent</code>
 * events to be fired off by objects of
 * this type. All actual editing done by a <code>FieldsEditor</code> in
 * fact must happen by accessing the <code>Editor</code>s it hands
 * out in the <code>getField[s]()</code> methods.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public interface FieldsEditor
extends Editor
{
    /**
     * Return the array of <code>Editor</code>s that this object contains.
     * They are returned in the preferred order of presentation to a user.
     *
     * @return the array of sub-editors
     */
    public Editor[] getFields ();

    /**
     * Return the count of editors that this object contains.
     *
     * @return the number of sub-editors
     */
    public int getFieldCount ();

    /**
     * Return the <code>Editor</code> at the given index.
     *
     * @param idx the index
     * @return the <code>Editor</code> at that index
     */
    public Editor getField (int idx);
}
