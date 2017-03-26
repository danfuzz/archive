// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.objed;

import java.util.EventListener;

/**
 * This interface is mostly just to mark objects which know how to perform
 * edits on another object but does have a couple of actual method
 * definitions. It implies no particular <i>human</i> interface. In order
 * to actually accomplish human editing, one must hand <code>Editor</code>
 * objects to some user-interface or other (for example, the classes in
 * <code>com.milk.objed.gui</code>).
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public interface Editor
{
    /**
     * Add a listener to this object. If the listener is in fact an
     * <code>EditorListener</code> then it will get all the
     * <code>EditorEvent</code>s that get generated for this object.
     * Subclasses may want to define other event classes that get
     * generated, of course.
     *
     * @param listener the listener to add
     */
    public void addListener (EventListener listener);

    /**
     * Remove a listener from this object that was previously added with
     * <code>addListener()</code>.
     *
     * @param listener the listener to remove
     */
    public void removeListener (EventListener listener);

    /**
     * Get a short human-oriented label/description for the object this
     * editor is editing. The description should be suitable as a label
     * next to this editor, and shouldn't ever change during the lifetime
     * of this editor.
     *
     * @return the label 
     */
    public String getLabel ();

    /**
     * Get a full human-oriented description for the object this editor is
     * editing. The description should be suitable for tip-text or the
     * like. It's okay for the description to change, and if it does, a
     * <code>descriptionChanged</code> event will be sent to any registered
     * listeners.
     *
     * @return the full description 
     */
    public String getDescription ();

    /**
     * Return true if this editor is mutable. That is, if this method
     * returns true, then edits can actually happen on this object. If
     * false, this object is for display only. If the mutability of this
     * object changes, a <code>mutabilityChanged</code> event will be sent
     * to any registered listeners.
     *
     * @return true if this editor is mutable 
     */
    public boolean isMutable ();

    /**
     * Ask this editor to update its internal state. This is useful if
     * the end value(s) that the editor manipulates is (are) unable to
     * notify the editor directly when they change. For many editors,
     * this in fact does nothing, for some it merely resends the request
     * to sub-editors, and for some it actually does some sort of value
     * update.
     */
    public void update ();
}
