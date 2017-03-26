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
 * This is a comparator which uses <code>==</code> to perform comparisons
 * and <code>System.identityHashCode()</code> as the compare-hash. The
 * class is named after the Lisp concept of <code>eq</code>, which is
 * approximately its version of <code>==</code>.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class EqComparator
implements Comparator
{
    /** the unique instance of this class */
    public static final EqComparator TheOne = new EqComparator ();

    /**
     * This constructor is private; clients should always refer to the
     * unique instance, <code>TheOne</code>.
     */
    private EqComparator ()
    {
	// this space intentionally left blank
    }

    /**
     * Return true if this <code>Comparator</code> considers the given
     * two objects to be equal. In this case, it only returns true if
     * <code>obj1 == obj2</code>.
     *
     * @param obj1 the first object
     * @param obj2 the second object
     * @return the result of comparison
     */
    public boolean compareEquals (Object obj1, Object obj2)
    {
	return obj1 == obj2;
    }

    /**
     * Get the compare-hash for the given object. In this case, it 
     * returns <code>System.identityHashCode(obj)</code>.
     *
     * @param obj the object to figure the hash of
     * @return the compare-hash of the object 
     */
    public int compareHash (Object obj)
    {
	return System.identityHashCode (obj);
    }
}
