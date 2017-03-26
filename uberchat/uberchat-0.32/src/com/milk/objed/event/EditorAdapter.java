// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.objed.event;

/**
 * This is an adapter for <code>EditorListener</code>. It implements all
 * the methods with empty bodies. Subclasses just override the ones they
 * care about.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public class EditorAdapter
implements EditorListener
{
    public void valueChanged (EditorEvent event)
    {
	// this space intentionally left blank
    }

    public void descriptionChanged (EditorEvent event)
    {
	// this space intentionally left blank
    }

    public void mutabilityChanged (EditorEvent event)
    {
	// this space intentionally left blank
    }

    public void fieldEvent (EditorEvent event)
    {
	// this space intentionally left blank
    }

    public void fieldAdded (EditorEvent event)
    {
	// this space intentionally left blank
    }

    public void fieldRemoved (EditorEvent event)
    {
	// this space intentionally left blank
    }
}
