// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.util;

/**
 * A <code>TableElement</code> holds a key and a value, and is generally
 * used to indicate a mapping from the key to the value in a table of
 * some sort.
 *
 * @see Table
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public interface TableElement
{
    /**
     * Get the key of this object.
     */
    public Object getKey ();

    /**
     * Get the value of this object.
     */
    public Object getValue ();
}
