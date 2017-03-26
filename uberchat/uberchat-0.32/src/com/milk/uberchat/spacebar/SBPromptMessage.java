// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.uberchat.spacebar;

import com.milk.uberchat.iface.ChatLocus;

/**
 * This class represents prompts from the SpaceBar host.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
/*package*/ class SBPromptMessage
{
    private String myType;

    /**
     * Make a <code>SBPromptMessage</code>.
     *
     * @param type the prompt type
     */
    public SBPromptMessage (String type)
    {
	myType = type;
    }

    /**
     * Get the string form of this prompt.
     *
     * @return the string form
     */
    public String toString ()
    {
	return "{SBPromptMessage " + myType + "}";
    }

    /**
     * Get the type of this prompt.
     *
     * @return the type
     */
    public String getType ()
    {
	return myType;
    }
}
