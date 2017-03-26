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
 * This is an <code>ObjectPredicate</code> which answers false to all
 * <code>test()</code>s.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class FalsePredicate
implements ObjectPredicate
{
    /** the unique instance of this class */
    public static final FalsePredicate TheOne = new FalsePredicate ();

    /**
     * This constructor is private; clients should always refer to the
     * unique instance, <code>TheOne</code>.
     */
    private FalsePredicate ()
    {
	// this space intentionally left blank
    }

    /**
     * Answer the question about the given object. In this case,
     * it always returns false.
     *
     * @param obj the object to test
     * @return false, always
     */
    public boolean test (Object obj)
    {
	return false;
    }
}
