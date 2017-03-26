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
 * This is a comparator which uses <code>Object.equals()</code> to perform
 * comparisons and <code>Object.hashCode()</code> as the compare-hash. The
 * exception is that null is never considered equal to any other object. (A
 * non-null object never gets asked to compare itself to null.) Also, an
 * object is always considered equal to itself. (It is never asked to
 * compare it to itself.)
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class EqualsComparator
implements Comparator
{
    /** the unique instance of this class */
    public static final EqualsComparator TheOne = new EqualsComparator ();

    /**
     * This constructor is private; clients should always refer to the
     * unique instance, <code>TheOne</code>.
     */
    private EqualsComparator ()
    {
	// this space intentionally left blank
    }

    /**
     * Return true if this <code>Comparator</code> considers the given
     * two objects to be equal. In this case, it only returns the result
     * of <code>obj1.equals(obj2)</code>.
     *
     * @param obj1 the first object
     * @param obj2 the second object
     * @return the result of comparison
     */
    public boolean compareEquals (Object obj1, Object obj2)
    {
	if (obj1 == obj2)
	{
	    return true;
	}

	if ((obj1 == null) || (obj2 == null))
	{
	    return false;
	}

	return obj1.equals (obj2);
    }

    /**
     * Get the compare-hash for the given object. In this case, it 
     * returns <code>obj.hashCode()</code>.
     *
     * @param obj the object to figure the hash of
     * @return the compare-hash of the object 
     */
    public int compareHash (Object obj)
    {
	return obj.hashCode ();
    }
}
