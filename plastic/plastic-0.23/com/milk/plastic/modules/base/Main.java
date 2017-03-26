package com.milk.plastic.modules.base;

import com.milk.plastic.iface.Environment;
import com.milk.plastic.iface.Module;
import com.milk.plastic.iface.NameRef;
import com.milk.plastic.util.ModuleNetwork;
import java.util.Map;

/**
 * Main module. 
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
final public class Main
implements Runnable
{
    /** number of iterations to perform */
    private int myIters;

    /** array of references to sinks */
    private NameRef[] mySinkRefs;

    // ------------------------------------------------------------------------
    // constructor
    
    /**
     * Construct an instance.
     *
     * @param factory the factory which spawned this instance
     * @param template the full template for this instance
     * @param args the arguments which this instance was constructed from 
     */
    public Main (Map args)
    {
	myIters = ((Integer) args.get ("iters")).intValue ();
	int count = ((Integer) args.get ("count")).intValue ();

	mySinkRefs = new NameRef[count];

	// get the array of sink references
	for (int i = 0; i < count; i++)
	{
	    mySinkRefs[i] = (NameRef) args.get ("sink_" + i);
	}
    }

    // ------------------------------------------------------------------------
    // public methods

    // interface's comment suffices
    public void run ()
    {
	// resolve the sinks
	Module[] sinks = new Module[mySinkRefs.length];
	for (int i = 0; i < sinks.length; i++)
	{
	    sinks[i] = (Module) mySinkRefs[i].resolve ();
	}

	// make a network for them
	ModuleNetwork net = new ModuleNetwork (sinks);

	// initialize
	net.bind ();

	// and run!
	for (int i = myIters; i > 0; i--)
	{
	    net.tick ();
	}

	// shut down
	net.reset ();
    }
}

