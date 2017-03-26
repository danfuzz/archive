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

import java.util.EventListener;

/**
 * This is the interface for objects that care about
 * <code>EditorEvent</code>s.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public interface EditorListener
extends EventListener
{
    /**
     * This is called when the value handled by the editor changes in
     * any way.
     *
     * @param event the event commemorating the moment 
     */
    public void valueChanged (EditorEvent event);

    /**
     * This is called when the description of the editor changes.
     *
     * @param event the event commemorating the moment
     */
    public void descriptionChanged (EditorEvent event);

    /**
     * This is called when the mutability of the editor changes.
     *
     * @param event the event commemorating the moment
     */
    public void mutabilityChanged (EditorEvent event);

    /**
     * This is called when an event happens in a sub-editor field
     * handled by a <code>FieldsEditor</code>.
     *
     * @param event the event commemorating the moment 
     */
    public void fieldEvent (EditorEvent event);

    /**
     * This is called when a new field is added to a
     * <code>MutableFieldsEditor</code>.
     *
     * @param event the event commemorating the moment 
     */
    public void fieldAdded (EditorEvent event);

    /**
     * This is called when a field is removed from a
     * <code>MutableFieldsEditor</code>.
     *
     * @param event the event commemorating the moment 
     */
    public void fieldRemoved (EditorEvent event);
}
