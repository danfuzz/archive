package com.milk.plastic.modules.base;

import com.milk.plastic.iface.FieldRef;
import com.milk.plastic.iface.Module;
import com.milk.plastic.iface.NameRef;
import com.milk.plastic.iface.PlasticException;
import com.milk.plastic.iface.Template;
import com.milk.plastic.util.BaseFactory;
import java.util.Map;

/**
 * A factory for null modules. Null modules take an arbitrary number of
 * inputs and simply pass them through to the corresponding outputs.
 * This module exists primarily as an educational example.
 *
 * <p>The base arguments for this factory are:</p>
 *
 * <dl>
 * <dt><code>type: type</code></dt>
 * <dd>The type that each of the inputs and outputs should be.</dd>
 * <dt><code>count: integer</code></dt>
 * <dd>The number of inputs and outputs to have. The inputs and outputs are
 * named simply <code>in_<i>n</i></code> and <code>out_<i>n</i></code>, for
 * <code><i>n</i></code> starting at <code>0</code>.</dd>
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
final public class NullFactory
extends BaseFactory
{
    /** short name */
    private static final String SHORT_NAME = "Null";

    /** base template */
    private static final Template TEMPLATE =
	new Template (new String[] { "count", "type" },
		      new Class[] { Integer.class, NameRef.class });

    // ------------------------------------------------------------------------
    // constructor
    
    public NullFactory ()
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
	Class type = (Class) ((NameRef) args.get ("type")).resolve ();

	String[] names = new String[count * 2];
	Class[] types = new Class[count * 2];

	for (int i = 0; i < count; i++)
	{
	    names[i]         = "in_" + i;
	    types[i]         = FieldRef.class;
	    names[i + count] = "out_" + i;
	    types[i + count] = type;
	}

	return TEMPLATE.withMore (names, types);
    }

    // superclass's comment suffices
    protected Object make1 (Template template, Map args)
	throws PlasticException
    {
	return new Null (this, template, args);
    }
}

