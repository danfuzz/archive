package com.milk.plastic.modules.base;

import com.milk.plastic.iface.Factory;
import com.milk.plastic.iface.PlasticException;
import com.milk.plastic.iface.Ref;
import com.milk.plastic.iface.Template;
import com.milk.plastic.ports.Port;
import com.milk.plastic.util.BaseModule;
import java.util.Map;

/**
 * Const module. 
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
final public class Const
extends BaseModule
{
    /** the constant value */
    private Object myValue;

    // ------------------------------------------------------------------------
    // constructor
    
    /**
     * Construct an instance.
     *
     * @param factory the factory which spawned this instance
     * @param template the full template for this instance
     * @param args the arguments which this instance was constructed from 
     * @exception PlasticException thrown if there was trouble instantiating
     * any of the blank fields
     */
    public Const (Factory factory, Template template, Map args)
	throws PlasticException
    {
	super (factory, template, args);

	myValue = args.get ("value");
    }

    // ------------------------------------------------------------------------
    // public methods the superclass requires us to implement

    // interface's comment suffices
    public void tick ()
    {
	// don't need to do anything; the port is set just the one
	// time during bind1()
    }

    // ------------------------------------------------------------------------
    // protected methods the superclass requires us to implement

    // superclass's comment suffices
    protected void bind1 ()
    {
	Port outPort = (Port) getField ("out");

	// we only need to set the value once before ticking begins
	outPort.setValue (myValue);
    }

    // superclass's comment suffices
    protected void reset1 ()
    {
	// no need to do anything
    }
}

