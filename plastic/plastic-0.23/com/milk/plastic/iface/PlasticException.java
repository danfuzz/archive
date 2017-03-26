package com.milk.plastic.iface;

import com.milk.util.EmbeddedException;

/**
 * Instances of this class are thrown when there is trouble with 
 * the system.
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
public class PlasticException
extends EmbeddedException
{
    // ------------------------------------------------------------------------
    // constructors

    /**
     * Construct an instance with neither a message nor an embedded exception.
     */
    public PlasticException ()
    {
	// this space intentionally left blank
    }

    /**
     * Construct an instance with just a message but no embedded exception.
     *
     * @param message the message
     */
    public PlasticException (String message)
    {
	super (message);
    }

    /**
     * Construct an instance with just an embedded exception but no message.
     * The message ends up being derived from the embedded exception.
     *
     * @param embeddedException the exception
     */
    public PlasticException (Throwable embeddedException)
    {
	super (embeddedException);
    }

    /**
     * Construct an instance with both message and embedded exception.
     *
     * @param message the message
     * @param embeddedException the exception
     */
    public PlasticException (String message, 
			     Throwable embeddedException)
    {
	super (message, embeddedException);
    }
}
