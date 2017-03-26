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
 * <code>Expression</code>s are those things which can be evaluated in an
 * <code>Environment</code>. There are a fixed set of subclasses of
 * expression, which is very intentional. 
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public abstract class Expression
{
    /**
     * The constructor is package scope, because the set of expression
     * classes is closed. If you're even thinking about adding another
     * expression class, then you're probably doing something wrong. You'll
     * confuse other parts of the system, chaos will ensue, and evil demons
     * will roam the land in search of cute little woodland creatures to
     * torment. You have been warned.
     */
    /*package*/ Expression ()
    {
	// this space intentionally left blank
    }

    /**
     * Evaluate this expression in the given <code>Environment</code>.
     *
     * @param environment the environment to evaluate in
     * @return the result of evaluation
     */
    public abstract Object eval (Environment environment);
}

