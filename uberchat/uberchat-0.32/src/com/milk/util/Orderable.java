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
 * This interface is for objects which may be ordered with respect to
 * each other.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998 Dan Bornstein, all rights reserved. 
 */
public interface Orderable
{
    /**
     * Return either -1, 0, or 1 to indicate that this object
     * is less than, equal to, or greater than, the given object, respectively.
     *
     * @param obj the object to compare to
     * @return the result of comparison
     * @exception UnorderedException thrown if this object isn't
     * ordered with respect to <code>obj</code>
     */
    public int compare (Object obj)
        throws UnorderedException;
}
