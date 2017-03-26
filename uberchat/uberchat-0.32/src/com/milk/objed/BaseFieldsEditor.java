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
 * This class merely serves as an abstract class extending
 * <code>BaseEditor</code> and implementing (fully abstractly)
 * <code>FieldsEditor</code>. It is used so that the human interface code
 * can have a real class to look up when it's trying to find a component to
 * display a <code>FieldsEditor</code>.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public abstract class BaseFieldsEditor
extends BaseEditor
implements FieldsEditor
{
    /**
     * Construct a <code>BaseFieldsEditor</code>.
     *
     * @param label the label
     * @param description the (initial) description
     * @param mutability the (initial) mutability
     */
    public BaseFieldsEditor (String label, String description, 
			     boolean mutability)
    {
	super (label, description, mutability);
    }

    /**
     * Return the array of <code>Editor</code>s that this object contains.
     * They are returned in the preferred order of presentation to a user.
     *
     * @return the array of sub-editors
     */
    public abstract Editor[] getFields ();

    /**
     * Return the count of editors that this object contains.
     *
     * @return the number of sub-editors
     */
    public abstract int getFieldCount ();

    /**
     * Return the <code>Editor</code> at the given index.
     *
     * @param idx the index
     * @return the <code>Editor</code> at that index
     */
    public abstract Editor getField (int idx);
}

