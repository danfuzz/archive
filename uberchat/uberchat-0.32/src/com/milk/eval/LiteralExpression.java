// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.eval;

/**
 * A <code>LiteralExpression</code> merely encapsulates literal data.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class LiteralExpression
extends Expression
{
    /** the literal data */
    private final Object myData;

    /**
     * Construct a <code>LiteralExpression</code>.
     *
     * @param data the literal data
     */
    public LiteralExpression (Object data)
    {
	myData = data;
    }

    /**
     * Evaluate this expression in the given <code>Environment</code>
     * (ignored in this case).
     *
     * @param environment the environment to evaluate in
     * @return the result of evaluation 
     */
    public Object eval (Environment environment)
    {
	return myData;
    }

    /**
     * Get the data.
     *
     * @return the data
     */
    public Object getData ()
    {
	return myData;
    }
}
