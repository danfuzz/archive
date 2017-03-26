package com.milk.plastic.modules.base;

import com.milk.plastic.iface.Environment;
import com.milk.plastic.iface.NameRef;
import com.milk.plastic.iface.PlasticException;
import com.milk.plastic.iface.Template;
import com.milk.plastic.util.BaseFactory;
import java.util.Map;

/**
 * A factory for main objects. A main object is a <code>Runnable</code>
 * which, when run, will perform a given number of iterations on a
 * module network.
 *
 * <p>The base arguments for this factory are:</p>
 *
 * <dl>
 * <dt><code>iters: integer</code></dt>
 * <dd>The number of iterations to perform on the network.</dd>
 * <dt><code>count: integer</code></dt>
 * <dd>The number of modules listed as sinks in the network. There should
 * be a corresponding set of extra arguments of the form
 * <code>sink_<i>n</i></code>, for <code><i>n</i></code> starting at
 * <code>0</code>.</dd>
 * </dl>
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
final public class MainFactory
extends BaseFactory
{
    /** short name */
    private static final String SHORT_NAME = "Main";

    /** base template */
    private static final Template TEMPLATE =
	new Template (new String[] { "count", "iters" },
		      new Class[] { Integer.class, Integer.class });

    // ------------------------------------------------------------------------
    // constructor
    
    public MainFactory ()
    {
	super (SHORT_NAME, TEMPLATE);
    }

    // ------------------------------------------------------------------------
    // methods the superclass requires us to implement

    // superclass's comment suffices
    protected Template getObjectTemplate1 (Map args)
	throws PlasticException
    {
	int count = ((Integer) args.get ("count")).intValue ();

	String[] names = new String[count];
	Class[] types = new Class[count];

	for (int i = 0; i < count; i++)
	{
	    names[i] = "sink_" + i;
	    types[i] = NameRef.class;
	}

	return TEMPLATE.withMore (names, types);
    }

    // superclass's comment suffices
    protected Object make1 (Template template, Map args)
	throws PlasticException
    {
	return new Main (args);
    }
}
