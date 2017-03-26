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
 * This interface is for objects which know how to compare objects for
 * equality and generate relevant hashcodes to make that determination
 * quicker.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public interface Comparator
{
    /**
     * Return true if this <code>Comparator</code> considers the given
     * two objects to be equal.
     *
     * @param obj1 the first object
     * @param obj2 the second object
     * @return the result of comparison
     */
    public boolean compareEquals (Object obj1, Object obj2);

    /**
     * Get the compare-hash for the given object. If two objects have the
     * same compare-hash from a given <code>Comparator</code> then it's
     * possible for them to be considered equal via <code>compare
     * ()</code>. If two objects have different compare-hashes, then they
     * are definitely not equal in terms of <code>compare ()</code>.
     *
     * @param obj the object to figure the hash of
     * @return the compare-hash of the object 
     */
    public int compareHash (Object obj);
}
