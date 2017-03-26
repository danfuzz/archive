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

import com.milk.objed.Editable;

/**
 * <p>This is the interface that all templates for <code>ChatSystem</code>
 * creation must adhere to. In addition to the methods here, actual
 * template classes will have an interesting set of fields having to do
 * with the specifics of the system they are for.</p>
 *
 * <p>This interface is defined to extend <code>Editable</code> as both the
 * way for users to tweak the parameters of a system, and in order to
 * facilitate saving and loading.</p>
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public interface ChatSystemTemplate
extends Editable
{
    /**
     * Make a copy of this template. We don't just use
     * <code>Object.clone()</code> because that method is declared to throw
     * a checked exception, and it's a big pain in the butt when you have a
     * method that's declared to throw a checked exception
     * (<code>CloneNotSupportedException</code>, natch), but you
     * <i>know</i> in your heart of hearts that it will never
     * <i>actually</i> throw it because of all the other restrictions
     * you've placed on the system, and how you're using it, etc. Java just
     * sucks that way sometimes.
     *
     * @return a copy of this template 
     */
    public ChatSystemTemplate copy ();

    /**
     * Turn this template into an actual <code>ChatSystem</code>.
     *
     * @return a <code>ChatSystem</code> corresponding to this template
     */
    public ChatSystem makeSystem ();
}
