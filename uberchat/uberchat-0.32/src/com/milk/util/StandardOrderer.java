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
 * <p>This class implements the "standard" <code>Orderer</code> class, which
 * merely assumes that its first <code>compare()</code> argument is an
 * <code>Orderable</code> and asks it to order itself with respect to the
 * second argument. If the assumption turns out to be false, then it throws
 * the expected <code>UnorderedException</code>.</p>
 *
 * <p><strong>NOTE:</strong> This class is not instantiable. Clients should
 * merely refer to the unique instance, <code>TheOne</code>.</p>
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
public final class StandardOrderer
implements Orderer
{
    /** the unique instance of this class */
    static public final StandardOrderer TheOne = new StandardOrderer ();

    /**
     * This class is only privately instantiable. Clients should merely
     * refer to the unique instance, <code>TheOne</code>.
     */
    private StandardOrderer ()
    {
	// this space intentionally left blank
    }

    /**
     * Return either -1, 0, or 1 to indicate that the first object
     * is less than, equal to, or greater than, the second object,
     * respectively.
     *
     * @param obj1 the first object
     * @param obj2 the second object
     * @return the result of comparison
     * @exception UnorderedException thrown if the two objects aren't
     * ordered with respect to each other 
     */
    public int compare (Object obj1, Object obj2)
        throws UnorderedException
    {
	Orderable ord1;
	try
	{
	    ord1 = (Orderable) obj1;
	}
	catch (ClassCastException ex)
	{
	    throw new UnorderedException ("Unordered objects");
	}

	return ord1.compare (obj2);
    }
}
