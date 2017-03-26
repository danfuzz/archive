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

import com.milk.command.Command;
import com.milk.command.Commandable;
import java.util.EventListener;

/**
 * This interface represents any chat object that has an a name
 * and a verbose description. Most objects that clients of this package
 * deal with in fact implement <code>ChatEntity</code>.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public interface ChatEntity
extends Commandable
{
    /**
     * Add a listener for this entity. The listener will get any events
     * that it defines interfaces for. <code>ChatEntity</code> itself only
     * ever sends <code>EntityEvent</code>s, but subclasses of it may send
     * other classes of event. The listener in general doesn't get pre-sent
     * any <code>EntityEvent</code>s, but, again, subclasses may define
     * events that get initially sent upon adding.
     *
     * <p><code>ChatEntity</code> objects are also the source for
     * <code>ErrorEvent</code>s. Adding an <code>ErrorListener</code> to a
     * <code>ChatEntity</code> should cause that listener to be told when
     * the user needs to be told any error arising from that entity.</p>
     *
     * @see com.milk.uberchat.event.EntityEvent
     * @see com.milk.uberchat.event.EntityListener
     * @see com.milk.uberchat.event.ErrorEvent
     * @see com.milk.uberchat.event.ErrorListener
     *
     * @param listener the listener to add 
     */
    public void addListener (EventListener listener);

    /**
     * Remove a listener from this entity that was previously added
     * with <code>addListener</code>.
     *
     * @param listener the listener to remove
     */
    public void removeListener (EventListener listener);

    /**
     * Add a listener that listens to everything in this entity and in all
     * the entities rooted (directly or indirectly) this one. It only gets
     * told of events that it defines interfaces for. It is treated as if
     * it has been added to each appropriate entity, so, if a sub-entity
     * generally sends events immediately upon addition, then that will
     * happen with this method as well.
     *
     * @param listener the listener to add 
     */
    public void addUberListener (EventListener listener);

    /**
     * Remove a listener that was previously added with
     * <code>addUberListener</code>.
     *
     * @param listener the listener to remove
     */
    public void removeUberListener (EventListener listener);

    /**
     * Get the short descriptive name of this object. It should be
     * suitable for human consumption and should generally not change
     * very often. If it <i>does</i> change, a <code>nameChanged</code>
     * event is sent out to announce the fact.
     *
     * @return the name of the object 
     */
    public String getName ();

    /**
     * Get the canonical form of the name of this object. For example,
     * if this entity is identified by something that ignores case, then
     * the name might be returned in all lower-case. Additionally, the
     * return value is always an interned string. If the canonical name
     * ever changes, a <code>nameChanged</code> event is sent out to
     * announce the fact.
     *
     * @return the canonical name of the object 
     */
    public String getCanonicalName ();

    /**
     * Get a verbose string description of this object. It need not be
     * stable.
     *
     * @return the verbose string description
     */
    public String getDescription ();

    /**
     * Get the user commands that may be used with this entity.
     * There are no predefined commands for <code>ChatEntity</code>
     * objects in general, but all the standard sub-interfaces define
     * standard commands.
     *
     * @return the array of commands
     */
    public Command[] getCommands ();
}
