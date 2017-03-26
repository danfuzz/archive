package com.milk.plastic.modules.base;

import com.milk.plastic.iface.Factory;
import com.milk.plastic.iface.Ref;
import com.milk.plastic.iface.Template;
import com.milk.plastic.ports.DoublePort;
import com.milk.plastic.util.BaseModule;
import com.milk.plastic.util.StaticTemplateFactory;
import java.util.Map;

/**
 * Propagation delay output for doubles. This is a module which produces
 * a single output based on the previous tick's input at a given
 * pd output. See {@link DoublePDIn} for more details.
 *
 * <p>The base arguments for this factory are:</p>
 *
 * <dl>
 * <dt><code>from: DoublePDIn</code></dt>
 * <dd>the pd input to get data from.</dd>
 * </dl>
 *
 * <p>The resulting modules always has a single output port named
 * <code>out</code>.</p>
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
final public class DoublePDOut
extends BaseModule
{
    /** short name */
    private static final String SHORT_NAME = "DoublePDOut";

    /** base template */
    private static final Template TEMPLATE =
	new Template (new String[] { "from" },
		      new Class[] { Ref.class });

    /** full template */
    private static final Template FULL_TEMPLATE =
	TEMPLATE.withMore (new String[] { "out" },
			   new Class[] { DoublePort.class });

    /** factory that creates instances of this class */
    public static final Factory FACTORY = 
	new StaticTemplateFactory (SHORT_NAME, DoublePDOut.class, 
				   TEMPLATE, FULL_TEMPLATE);

    /** the output port */
    private DoublePort myOutPort;

    /** null-ok; bound pd input */
    private DoublePDIn myPDIn;

    /** tick parity
     * @see DoublePDOut#myParity */
    private boolean myParity;

    // ------------------------------------------------------------------------
    // constructor
    
    /**
     * Construct an instance.
     *
     * @param factory the factory which spawned this instance
     * @param template the full template for this instance
     * @param args the arguments which this instance was constructed from 
     */
    public DoublePDOut (Factory factory, Template template, Map args)
    {
	super (factory, template, args);

	// get the output port
	myOutPort = (DoublePort) getField ("out");
    }

    // ------------------------------------------------------------------------
    // public methods the superclass requires us to implement

    // interface's comment suffices
    public void tick ()
    {
	boolean parity = myParity;
	myOutPort.setDouble (myPDIn.getPDDouble (parity));
	myParity = ! parity;
    }

    // ------------------------------------------------------------------------
    // protected methods the superclass requires us to implement

    // superclass's comment suffices
    protected void bind1 ()
    {
	myPDIn = (DoublePDIn) getBoundObject ("from");
	myParity = false;
    }

    // superclass's comment suffices
    protected void reset1 ()
    {
	myPDIn = null;
    }
}
