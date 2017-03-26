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
 * This interface is for objects which know how to impose an order
 * on other objects.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998 Dan Bornstein, all rights reserved. 
 */
public interface Orderer
{
    /**
     * Return either -1, 0, or 1 to indicate that the first object
     * is less than, equal to, or greater than, the second object,
     * respectively.
     *
     * @param obj1 the first object
     * @param obj2 the second object
     * @return the result of comparison
     * @exception UnorderedException thrown if the objects aren't
     * ordered with respect to each other
     */
    int compare (Object obj1, Object obj2)
        throws UnorderedException;
}
