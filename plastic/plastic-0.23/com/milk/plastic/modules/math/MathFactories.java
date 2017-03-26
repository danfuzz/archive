package com.milk.plastic.modules.math;

import com.milk.plastic.iface.Environment;

/**
 * Where all the math factories are instantiated. This class simply houses
 * a static method that can add the math factory bindings to a given
 * environment.
 *
 * <p>Copyright (C) 2000, Dan Bornstein, danfuzz@milk.com. All Rights 
 * Reserved. (Shrill TV degreaser.)</p>
 * 
 * <p>This file is part of the MILK Kodebase. The contents of this file are
 * subject to the MILK Kodebase Public License; you may not use this file
 * except in compliance with the License. A copy of the MILK Kodebase Public
 * License has been included with this distribution, and may be found in the
 * file named "LICENSE.html". You may also be able to obtain a copy of the
 * License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!</p>
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class MathFactories
{
    // ------------------------------------------------------------------------
    // constructors

    /**
     * This class is uninstantiable.
     */
    private MathFactories ()
    {
	// this space intentionally left blank
    }

    // ------------------------------------------------------------------------
    // static public methods

    /**
     * Add an instance of each of the core factories to the given environment.
     *
     * @param env the environment to add bindings to
     */
    static public void bind (Environment env)
    {
	env.bind ("Abs",   Abs.FACTORY);
	env.bind ("Asin",  Asin.FACTORY);
	env.bind ("Acos",  Acos.FACTORY);
	env.bind ("Atan",  Atan.FACTORY);
	env.bind ("Ceil",  Ceil.FACTORY);
	env.bind ("Cos",   Cos.FACTORY);
	env.bind ("Exp",   Exp.FACTORY);
	env.bind ("Floor", Floor.FACTORY);
	env.bind ("Log",   Log.FACTORY);
	env.bind ("Mul",   new MulFactory ());
	env.bind ("Neg",   Neg.FACTORY);
	env.bind ("Round", Round.FACTORY);
	env.bind ("Sin",   Sin.FACTORY);
	env.bind ("Sqrt",  Sqrt.FACTORY);
	env.bind ("Sum",   new SumFactory ());
	env.bind ("Tan",   Tan.FACTORY);
    }
}
