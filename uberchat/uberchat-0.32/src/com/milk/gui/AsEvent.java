// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.gui;

import java.awt.Component;

/**
 * This class merely has utility methods to help deal with the
 * AWT "feature" that you may only call some (many? most? all?)
 * AWT methods from within the AWT event queue or face the dire
 * consequences of difficult-to-debug deadlock problems. Java just
 * sucks that way sometimes.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class AsEvent
{
    /**
     * This class is uninstantiable.
     */
    private AsEvent ()
    {
	// this space intentionally left blank
    }

    /**
     * Run the given <code>Runnable</code> from within the event
     * queue for the given component.
     *
     * @param component the component to ask for an event queue
     * @param runnable the thing to run
     */
    public static void runIn (Component component, Runnable runnable)
    {
	component.getToolkit ().getSystemEventQueue ().invokeLater (runnable);
    }

    /**
     * Perform <code>Component.setVisible()</code> as an event.
     *
     * @param component the component in question
     * @param visibility the visibility value
     */
    public static void setVisible (final Component component, 
				   final boolean visibility)
    {
	runIn (component,
	       new Runnable () 
	       { 
		   public void run () 
		   { 
		       component.setVisible (visibility);
		   }
	       });
    }
}
