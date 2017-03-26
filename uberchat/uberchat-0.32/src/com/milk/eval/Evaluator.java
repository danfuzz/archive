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
 * An <code>Evaluator</code> provides a mapping from one set of values (an
 * expression set) to another sets of values (a result set).
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public interface Evaluator
{
    /**
     * Return true if the given object is valid as an expression to
     * the evaluator. If an object is a valid expression, then the
     * evaluator is, in general, able to <code>eval()</code> it into
     * a result (or at least reasonably attempt to do so).
     *
     * @param value the value to check
     * @return true if it is valid as an expression, false if not
     */
    public boolean isExpression (Object value);

    /**
     * Return true if the given object is a possible result of evaluation.
     * If this returns true, then it is conceivable that the given object
     * was the result of a call to <code>eval()</code> on this evaluator.
     *
     * @param value the value to check
     * @return true if it is a possible result, false if not
     */
    public boolean isResult (Object value);

    /**
     * Evaluate the given value as an expression, yielding some result
     * value.
     *
     * @param expression the thing to evaluate
     * @return the result of evaluation
     */
    public Object eval (Object expression);
}
