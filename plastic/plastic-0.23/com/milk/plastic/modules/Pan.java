package com.milk.plastic.modules;

import com.milk.plastic.iface.Factory;
import com.milk.plastic.iface.Ref;
import com.milk.plastic.iface.FieldRef;
import com.milk.plastic.iface.Template;
import com.milk.plastic.ports.DoublePort;
import com.milk.plastic.util.BaseModule;
import com.milk.plastic.util.StaticTemplateFactory;
import java.util.Map;

/**
 * Pan module. This takes a pair of inputs representing a signal and
 * a position. When the position is 0.0, the signal goes at half amplitude
 * to both outputs. As the position goes towards 1.0, the amplitude
 * increases in the second output as it decreases (to 0.0 at the limit)
 * in the first output. The opposite happens as the position goes towards
 * -1.0.
 *
 * <p>The base arguments for this factory are:</p>
 *
 * <dl>
 * <dt><code>in_wave: double port</code></dt>
 * <dd>The signal to pan.</dd>
 * <dt><code>in_pos: double port</code></dt>
 * <dd>The pan position.</dd>
 * </dl>
 *
 * <p>The resulting modules always have a pair of output ports named
 * <code>out_0</code> and <code>out_1</code>.</p>
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
final public class Pan
extends BaseModule
{
    /** short name */
    private static final String SHORT_NAME = "Pan";

    /** base template */
    private static final Template TEMPLATE =
	new Template (new String[] { "in_wave", "in_pos" },
		      new Class[] { FieldRef.class, FieldRef.class });

    /** full template */
    private static final Template FULL_TEMPLATE =
	TEMPLATE.withMore (new String[] { "out_0", "out_1" },
			   new Class[] { DoublePort.class, DoublePort.class });

    /** factory that creates instances of this class */
    public static final Factory FACTORY = 
	new StaticTemplateFactory (SHORT_NAME, Pan.class, 
				   TEMPLATE, FULL_TEMPLATE);

    /** null-ok; bound input port for the waveform */
    private DoublePort myInWave;

    /** null-ok; bound input port for the position */
    private DoublePort myInPos;

    /** first output port */
    private DoublePort myOutPort0;

    /** second output port */
    private DoublePort myOutPort1;

    // ------------------------------------------------------------------------
    // constructor
    
    /**
     * Construct an instance.
     *
     * @param factory the factory which spawned this instance
     * @param template the full template for this instance
     * @param args the arguments which this instance was constructed from 
     */
    public Pan (Factory factory, Template template, Map args)
    {
	super (factory, template, args);

	// augment the type restriction on the inputs
	Ref newRef = ((Ref) getField ("in_wave")).withType (DoublePort.class);
	setField ("in_wave", newRef);
	newRef = ((Ref) getField ("in_pos")).withType (DoublePort.class);
	setField ("in_pos", newRef);

	// get the outputs
	myOutPort0 = (DoublePort) getField ("out_0");
	myOutPort1 = (DoublePort) getField ("out_1");
    }

    // ------------------------------------------------------------------------
    // public methods the superclass requires us to implement

    // interface's comment suffices
    public void tick ()
    {
	double pos = myInPos.getDouble ();
	double value = myInWave.getDouble ();

	if (pos < -1.0)
	{
	    myOutPort0.setDouble (value);
	    myOutPort1.setDouble (0.0);
	}
	else if (pos > 1.0)
	{
	    myOutPort0.setDouble (0.0);
	    myOutPort1.setDouble (value);
	}
	else
	{
	    pos = (pos + 1.0) / 2.0;
	    double value1 = value * pos;
	    myOutPort1.setDouble (value1);
	    myOutPort0.setDouble (value - value1);
	}
    }

    // ------------------------------------------------------------------------
    // protected methods the superclass requires us to implement

    // superclass's comment suffices
    protected void bind1 ()
    {
	myInWave = (DoublePort) getBoundObject ("in_wave");
	myInPos = (DoublePort) getBoundObject ("in_pos");
    }

    // superclass's comment suffices
    protected void reset1 ()
    {
	myInWave = null;
	myInPos = null;
    }
}
