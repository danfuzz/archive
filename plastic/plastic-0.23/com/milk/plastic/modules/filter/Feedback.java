package com.milk.plastic.modules.filter;

import com.milk.plastic.iface.Factory;
import com.milk.plastic.iface.FieldRef;
import com.milk.plastic.iface.Ref;
import com.milk.plastic.iface.Template;
import com.milk.plastic.ports.DoublePort;
import com.milk.plastic.util.BaseFactory;
import com.milk.plastic.util.BaseModule;
import com.milk.plastic.util.StaticTemplateFactory;
import java.util.Map;

/**
 * Feedback module. Instances take a single input and delay it by a
 * variable number of ticks with a preset maximum, and mix the
 * delayed output with the live input.
 *
 * <p>The base arguments for this factory are:</p>
 *
 * <dl>
 * <dt><code>maxDelay: int</code></dt>
 * <dd>The manimum number of ticks to delay.</dd>
 * <dt><code>in_wave: double port</code></dt>
 * <dd>The signal to process.</dd>
 * <dt><code>in_delay: double port</code></dt>
 * <dd>The amount to delay in ticks.</dd>
 * <dt><code>in_feed: double port</code></dt>
 * <dd>The amplitude of the feedback. 0.0 means no feedback; 1.0 means
 * full feedback.</dd>
 * </dl>
 *
 * <p>The resulting modules always have a single output port named
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
final public class Feedback
extends BaseModule
{
    /** short name */
    private static final String SHORT_NAME = "Feedback";

    /** base template */
    private static final Template TEMPLATE =
	new Template (new String[] { "maxDelay", "in_wave", "in_delay", 
				     "in_feed" },
		      new Class[] { Integer.class, FieldRef.class, 
				    FieldRef.class, FieldRef.class });

    /** full template */
    private static final Template FULL_TEMPLATE =
	BaseFactory.templateWithOut (TEMPLATE);

    /** factory that creates instances of this class */
    public static final Factory FACTORY = 
	new StaticTemplateFactory (SHORT_NAME, Feedback.class, 
				   TEMPLATE, FULL_TEMPLATE);

    /** max amount of delay in ticks */
    private int myMaxDelay;

    /** the output port */
    private DoublePort myOutPort;

    /** null-ok; array containing the delayed values, used as a circular
     * buffer, set in {@link #bind1} and <code>null</code>ed out in {@link
     * #reset1} */
    private double[] myBuf;

    /** null-ok; the resolved wave input port */
    private DoublePort myInWave;

    /** null-ok; the resolved delay input port */
    private DoublePort myInDelay;

    /** null-ok; the resolved feedback level input port */
    private DoublePort myInFeed;

    /** where in the buffer to place the next input value */
    private int myIndex;

    // ------------------------------------------------------------------------
    // constructor
    
    /**
     * Construct an instance.
     *
     * @param factory the factory which spawned this instance
     * @param template the full template for this instance
     * @param args the arguments which this instance was constructed from 
     */
    public Feedback (Factory factory, Template template, Map args)
    {
	super (factory, template, args);

	myMaxDelay = ((Integer) args.get ("maxDelay")).intValue ();

	// augment the type restriction on the inputs
	Ref newRef = ((Ref) getField ("in_wave")).withType (DoublePort.class);
	setField ("in_wave", newRef);
	newRef = ((Ref) getField ("in_delay")).withType (DoublePort.class);
	setField ("in_delay", newRef);
	newRef = ((Ref) getField ("in_feed")).withType (DoublePort.class);
	setField ("in_feed", newRef);

	// get the outputs
	myOutPort = (DoublePort) getField ("out");
    }

    // ------------------------------------------------------------------------
    // public methods the superclass requires us to implement

    // interface's comment suffices
    public void tick ()
    {
	// efficiency dictates that we get a local copy of these
	double[] buf = myBuf;
	int index = myIndex;
	int maxDelay = myMaxDelay;

	double value = myInWave.getDouble ();
	double feed = myInFeed.getDouble ();

	int delay = (int) myInDelay.getDouble ();
	if (delay < 1)
	{
	    delay = 1;
	}
	else if (delay > maxDelay)
	{
	    delay = maxDelay;
	}

	value += buf[(index + delay) % maxDelay] * feed;
	buf[index] = value;
	myOutPort.setDouble (value);

	if (index == 0)
	{
	    myIndex = maxDelay - 1;
	}
	else
	{
	    myIndex = index - 1;
	}
    }

    // ------------------------------------------------------------------------
    // protected methods the superclass requires us to implement

    // superclass's comment suffices
    protected void bind1 ()
    {
	myBuf = new double[myMaxDelay];
	myIndex = 0;

	myInWave = (DoublePort) getBoundObject ("in_wave");
	myInDelay = (DoublePort) getBoundObject ("in_delay");
	myInFeed = (DoublePort) getBoundObject ("in_feed");
    }

    // superclass's comment suffices
    protected void reset1 ()
    {
	myBuf = null;
    }
}
