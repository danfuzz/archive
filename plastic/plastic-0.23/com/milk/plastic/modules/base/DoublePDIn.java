package com.milk.plastic.modules.base;

import com.milk.plastic.iface.Factory;
import com.milk.plastic.iface.Ref;
import com.milk.plastic.iface.FieldRef;
import com.milk.plastic.iface.Template;
import com.milk.plastic.ports.DoublePort;
import com.milk.plastic.util.BaseModule;
import com.milk.plastic.util.StaticTemplateFactory;
import java.util.Map;

/**
 * Propagation delay input for doubles. This is a module which takes a
 * single input and can "tunnel" it, in conjunction with a propagation
 * delay output, to the subsequent tick, without introducing a cycle in the
 * port network of modules. Instead, the PD sources attach dirctly to the
 * sink object (not an output port of it) and perform magic to get the
 * delayed value.
 *
 * <p>The base arguments for this factory are:</p>
 *
 * <dl>
 * <dt><code>in: double port</code></dt>
 * <dd>the input to delay.</dd>
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
final public class DoublePDIn
extends BaseModule
{
    /** short name */
    private static final String SHORT_NAME = "DoublePDIn";

    /** base template */
    private static final Template TEMPLATE =
	new Template (new String[] { "in" },
		      new Class[] { FieldRef.class });

    /** factory that creates instances of this class */
    public static final Factory FACTORY = 
	new StaticTemplateFactory (SHORT_NAME, DoublePDIn.class, 
				   TEMPLATE, TEMPLATE);

    /** null-ok; bound input port */
    private DoublePort myInPort;

    /** tick parity; this reverses with each tick and is used to know
     * which output to return to a pd-out. This accounts for the fact
     * that a pd-out might actually be ticked after a pd-in in a given
     * cycle */
    private boolean myParity;

    /** output to provide when parity is <code>false</code> */
    private double myFalseOut;

    /** output to provide when parity is <code>true</code> */
    private double myTrueOut;

    // ------------------------------------------------------------------------
    // constructor
    
    /**
     * Construct an instance.
     *
     * @param factory the factory which spawned this instance
     * @param template the full template for this instance
     * @param args the arguments which this instance was constructed from 
     */
    public DoublePDIn (Factory factory, Template template, Map args)
    {
	super (factory, template, args);

	// augment the type restriction on the input
	Ref newRef = ((Ref) getField ("in")).withType (DoublePort.class);
	setField ("in", newRef);
    }

    // ------------------------------------------------------------------------
    // public methods the superclass requires us to implement

    // interface's comment suffices
    public void tick ()
    {
	if (myParity)
	{
	    myFalseOut = myInPort.getDouble ();
	    myParity = false;
	}
	else
	{
	    myTrueOut = myInPort.getDouble ();
	    myParity = true;
	}
    }

    // ------------------------------------------------------------------------
    // protected methods the superclass requires us to implement

    // superclass's comment suffices
    protected void bind1 ()
    {
	myInPort = (DoublePort) getBoundObject ("in");
	myParity = false;
	myFalseOut = 0.0;
	myTrueOut = 0.0;
    }

    // superclass's comment suffices
    protected void reset1 ()
    {
	myInPort = null;
    }

    // ------------------------------------------------------------------------
    // package instance methods

    /**
     * Get the input that was most recently present with the given
     * parity.
     *
     * @param parity the parity
     * @return the corresponding input
     */
    /*package*/ double getPDDouble (boolean parity)
    {
	if (parity)
	{
	    return myTrueOut;
	}
	else
	{
	    return myFalseOut;
	}
    }
}
