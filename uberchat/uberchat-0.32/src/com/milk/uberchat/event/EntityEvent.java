// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.uberchat.event;

import com.milk.uberchat.iface.ChatEntity;
import com.milk.util.BaseEvent;
import java.util.EventListener;

/**
 * This is the class for events having to do with happenings on
 * a <code>ChatEntity</code>.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class EntityEvent
extends BaseEvent
{
    /** type code for <code>descriptionChanged</code> event */
    public static final int DESCRIPTION_CHANGED = 0;

    /** type code for <code>nameChanged</code> event */
    public static final int NAME_CHANGED = 1;

    /**
     * Construct an <code>EntityEvent</code> of the particular type for the
     * particular entity.
     *
     * @param source the entity in question
     * @param type the event type
     * @param argument the argument 
     */
    private EntityEvent (ChatEntity source, int type, Object argument)
    {
	super (source, type, argument);
    }

    /**
     * Construct and return a <code>descriptionChanged</code> event.
     *
     * @param source the entity whose description changed
     * @param description its new description
     */
    public static EntityEvent descriptionChanged (ChatEntity source,
						  String description)
    {
	return new EntityEvent (source, DESCRIPTION_CHANGED, description);
    }

    /**
     * Construct and return a <code>nameChanged</code> event.
     *
     * @param source the entity whose name changed
     * @param name its new name
     * @param canonicalName its new canonical name
     */
    public static EntityEvent nameChanged (ChatEntity source,
					   String name,
					   String canonicalName)
    {
	return new EntityEvent (source, NAME_CHANGED, 
				new String[] { name, canonicalName });
    }

    /**
     * Get the source of this event as a <code>ChatEntity</code>. It is
     * merely a convenience to avoid having to do the cast yourself.
     *
     * @return the source of this event as a <code>ChatEntity</code> 
     */
    public ChatEntity getEntity ()
    {
	return (ChatEntity) source;
    }

    /**
     * Get the argument of this event as a string description. It is merely a
     * convenience to avoid having to do the cast yourself.
     *
     * @return the argument of this event as a string
     */
    public String getDescription ()
    {
	return (String) myArgument;
    }

    /**
     * Get the name part of the argument of this event.
     *
     * @return the name part of the argument of this event
     */
    public String getName ()
    {
	return ((String[]) myArgument)[0];
    }

    /**
     * Get the canonical name part of the argument of this event.
     *
     * @return the canonical name part of the argument of this event
     */
    public String getCanonicalName ()
    {
	return ((String[]) myArgument)[1];
    }

    // ------------------------------------------------------------------------
    // BaseEvent methods

    /**
     * Turn the given type code into a string.
     *
     * @param type the type code to translate
     * @return the type as a string
     */
    protected String typeToString (int type)
    {
	switch (type)
	{
	    case DESCRIPTION_CHANGED: return "description-changed";
	    case NAME_CHANGED:        return "name-changed";
	    default:                  return "unknown-type-" + type;
	}
    }

    /**
     * Send this event to the given listener.
     *
     * @param listener the listener to send to
     */
    public void sendTo (EventListener listener)
    {
	EntityListener l = (EntityListener) listener;

	switch (myType)
	{
	    case DESCRIPTION_CHANGED: l.descriptionChanged (this); break;
	    case NAME_CHANGED:        l.nameChanged (this);        break;
	    default:
	    {
		throw new RuntimeException (
                    "Attempt to send unknown event type " + myType + ".");
	    }
	}
    }

    /**
     * Return true if this event is appropiate for the given listener.
     *
     * @param listener the listener to check
     * @return true if the listener listens to this kind of event
     */
    public boolean canSendTo (EventListener listener)
    {
	return (listener instanceof EntityListener);
    }
}
