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

import com.milk.util.Table;

/**
 * An <code>Environment</code> embodies a mapping of names to values,
 * a concept of expressions in the environment, and a way to evaluate
 * those expressions.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public interface Environment
{
    /**
     * Get the name-value table of this environment.
     *
     * @return the name-value table
     */
    public Table getNameValueTable ();

    /**
     * Return true if the given object is valid as an expression for
     * this environment.
     *
     * @param expression the object to test
     * @return true if it is a valid expression, false if not
     */
    public boolean isExpression (Object expression);

    /**
     * Evaluate the given expression
}
