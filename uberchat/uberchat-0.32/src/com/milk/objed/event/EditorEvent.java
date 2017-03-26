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

import com.milk.objed.Editor;
import com.milk.util.BaseEvent;
import java.util.EventListener;

/**
 * This is the class for events having to do with <code>Editor</code>
 * objects.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class EditorEvent
extends BaseEvent
{
    /** type code for <code>valueChanged</code> event */
    public static final int VALUE_CHANGED = 0;

    /** type code for <code>descriptionChanged</code> event */
    public static final int DESCRIPTION_CHANGED = 1;

    /** type code for <code>mutabilityChanged</code> event */
    public static final int MUTABILITY_CHANGED = 2;

    /** type code for <code>fieldEvent</code> event */
    public static final int FIELD_EVENT = 3;

    /** type code for <code>fieldAdded</code> event */
    public static final int FIELD_ADDED = 4;

    /** type code for <code>fieldRemoved</code> event */
    public static final int FIELD_REMOVED = 5;

    /**
     * Construct an <code>EditorEvent</code> of the particular type for the
     * particular object.
     *
     * @param source the object in question
     * @param type the event type
     * @param argument the argument
     */
    private EditorEvent (Editor source, int type, Object argument)
    {
	super (source, type, argument);
    }

    /**
     * Return a new <code>EditorEvent</code> which is just like this one,
     * except with a new source.
     *
     * @param source the new source
     * @return the new event
     */
    public EditorEvent withNewSource (Editor source)
    {
	return new EditorEvent (source, myType, myArgument);
    }

    /**
     * Construct and return a valueChanged event.
     *
     * @param source the editor in charge
     * @param value the new value
     */
    public static EditorEvent valueChanged (Editor source,
					    Object value)
    {
	return new EditorEvent (source, VALUE_CHANGED, value);
    }

    /**
     * Construct and return a descriptionChanged event.
     *
     * @param source the editor whose description changed
     * @param description its new description
     */
    public static EditorEvent descriptionChanged (Editor source,
						  String description)
    {
	return new EditorEvent (source, DESCRIPTION_CHANGED, description);
    }

    /**
     * Construct and return a mutabilityChanged event.
     *
     * @param source the editor in charge
     * @param mutability the new mutability value
     */
    public static EditorEvent mutabilityChanged (Editor source,
						 boolean mutability)
    {
	return new EditorEvent (source, MUTABILITY_CHANGED, 
				new Boolean (mutability));
    }

    /**
     * Construct and return a <code>fieldEvent</code> event.
     *
     * @param source the editor in charge
     * @param event the event for the field that changed
     */
    public static EditorEvent fieldEvent (Editor source,
					  EditorEvent event)
    {
	return new EditorEvent (source, FIELD_EVENT, event);
    }

    /**
     * Construct and return a <code>fieldAdded</code> event.
     *
     * @param source the editor in charge
     * @param field the sub-editor that was added
     * @param index the index it was added at
     */
    public static EditorEvent fieldAdded (Editor source,
					  Editor field,
					  int index)
    {
	return new EditorEvent (source, FIELD_ADDED, 
				new Object[] { field, new Integer (index) });
    }

    /**
     * Construct and return a <code>fieldRemoved</code> event.
     *
     * @param source the editor in charge
     * @param field the sub-editor that was removed
     * @param index the index it was at
     */
    public static EditorEvent fieldRemoved (Editor source,
					    Editor field,
					    int index)
    {
	return new EditorEvent (source, FIELD_REMOVED, 
				new Object[] { field, new Integer (index) });
    }

    /**
     * Get the source of this event as an <code>Editor</code>. It is merely a
     * convenience to avoid having to do the cast yourself.
     *
     * @return the source of this event as an <code>Editor</code>
     */
    public Editor getEditor ()
    {
	return (Editor) source;
    }

    /**
     * Get the value of this event, that is, the value from a 
     * <code>valueChanged</code> event. It's the same as 
     * <code>getArgument()</code> but is named to match the event name.
     *
     * @return the value of this event
     */
    public Object getValue ()
    {
	return myArgument;
    }

    /**
     * Get the argument of this event as a mutability value, that is, the
     * mutability from a <code>mutabilityChanged</code> event. It merely
     * casts the argument to save you the trouble.
     *
     * @return the argument of this event as a boolean 
     */
    public boolean getMutability ()
    {
	return ((Boolean) myArgument).booleanValue ();
    }

    /**
     * Get the argument of this event as a string description, that is, the
     * description from a <code>descriptionChanged</code> event. It merely
     * casts the argument to save you the trouble.
     *
     * @return the argument of this event as a <code>String</code>
     */
    public String getDescription ()
    {
	return (String) myArgument;
    }

    /**
     * Get the argument of this event as a sub-event, that is, the
     * event from a <code>fieldEvent</code> event. It merely
     * casts the argument to save you the trouble.
     *
     * @return the argument of this event as an <code>EditorEvent</code>
     */
    public EditorEvent getSubEvent ()
    {
	return (EditorEvent) myArgument;
    }

    /**
     * Get the index of this event, that is, the index from a
     * <code>fieldAdded</code> or <code>fieldRemoved</code> event.
     *
     * @return the index of this event
     */
    public int getIndex ()
    {
	return ((Integer) ((Object[]) myArgument)[1]).intValue ();
    }

    /**
     * Get the field of this event, that is, the sub-editor field from a
     * <code>fieldAdded</code> or <code>fieldRemoved</code> event.
     *
     * @return the field of this event
     */
    public Editor getField ()
    {
	return (Editor) ((Object[]) myArgument)[0];
    }

    // ------------------------------------------------------------------------
    // BaseEvent methods

    /**
     * Turn the given type code into a string.
     *
     * @param type the type code to translate
     * @return the type as a string */
    protected String typeToString (int type)
    {
	switch (type)
	{
	    case VALUE_CHANGED:       return "value-changed";
	    case DESCRIPTION_CHANGED: return "description-changed";
	    case MUTABILITY_CHANGED:  return "mutability-changed";
	    case FIELD_EVENT:         return "field-event";
	    case FIELD_ADDED:         return "field-added";
	    case FIELD_REMOVED:       return "field-removed";
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
	EditorListener l = (EditorListener) listener;

	switch (myType)
	{
	    case VALUE_CHANGED:       l.valueChanged (this);       break;
	    case DESCRIPTION_CHANGED: l.descriptionChanged (this); break;
	    case MUTABILITY_CHANGED:  l.mutabilityChanged (this);  break;
	    case FIELD_EVENT:         l.fieldEvent (this);         break;
	    case FIELD_ADDED:         l.fieldAdded (this);         break;
	    case FIELD_REMOVED:       l.fieldRemoved (this);       break;
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
	return (listener instanceof EditorListener);
    }
}
