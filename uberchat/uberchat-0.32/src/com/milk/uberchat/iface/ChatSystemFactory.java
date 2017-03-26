// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.uberchat.iface;

/**
 * This interface is for objects which can spew forth
 * <code>ChatSystem</code>s like there's no tomorrow.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
public interface ChatSystemFactory
{
    /**
     * Get the name of the protocol that this <code>ChatSystemFactory</code>
     * handles. It should be suitable for listing in a menu of protocols
     * for a user to choose from.
     *
     * @return the name of the protocol
     */
    public String getName ();

    /**
     * Get a verbose description of the protocol that this
     * <code>ChatSystemFactory</code> handles.
     *
     * @return the description of the protocol
     */
    public String getDescription ();

    /**
     * Make and return a new template for instantiating a
     * <code>ChatSystem</code>.
     *
     * @return a template, suitable for editing and then instantiating
     */
    public ChatSystemTemplate makeTemplate ();
}

