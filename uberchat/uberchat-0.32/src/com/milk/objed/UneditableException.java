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
 * This exception is thrown when something wants to edit an object that
 * isn't editable.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class UneditableException
extends RuntimeException
{
    /** the object that is not editable */
    private Object myUneditable;

    /**
     * Construct an <code>UneditableException</code>.
     *
     * @param uneditable the object that is not editable
     */
    public UneditableException (Object uneditable)
    {
	this (uneditable, null);
    }

    /**
     * Construct an <code>UneditableException</code>.
     *
     * @param uneditable the object that is not editable
     * @param msg null-ok; extra detail message
     */
    public UneditableException (Object uneditable, String msg)
    {
	super ("Object (" + uneditable + ") is not editable" +
	       ((msg == null) ? "." : (":\n" + msg)));
	myUneditable = uneditable;
    }

    /**
     * Get the uneditable object.
     *
     * @return the object
     */
    public Object getUneditable ()
    {
	return myUneditable;
    }
}
