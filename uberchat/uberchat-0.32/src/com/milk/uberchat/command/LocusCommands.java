// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.uberchat.command;

import com.milk.command.BaseCommand;
import com.milk.command.Command;
import com.milk.objed.Editable;
import com.milk.objed.Editor;
import com.milk.objed.FieldValueEditor;
import com.milk.objed.FixedFieldsEditor;
import com.milk.objed.StringTextEditor;
import com.milk.objed.ValueEditor;
import com.milk.uberchat.event.EntityEvent;
import com.milk.uberchat.event.EntityListener;
import com.milk.uberchat.iface.ChatLocus;
import com.milk.uberchat.SpeechKinds;
import com.milk.util.BadArgumentException;

/**
 * This class contains <code>Command</code>s for <code>ChatLocus</code>
 * objects as inner classes and knows how to manage them.
 *
 * @see com.milk.uberchat.iface.ChatLocus
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class LocusCommands
{
    /** the <code>ChatLocus</code> to use */
    private ChatLocus myLocus;

    /** the listener that listens to the locus */
    private MyLocusListener myLocusListener;

    /** the <code>speak()</code> command object */
    private Speak mySpeak;

    /** the <code>updateUsers()</code> command object */
    private UpdateUsers myUpdateUsers;

    /**
     * Construct a <code>LocusCommands</code> object. Constructing the
     * object automatically causes it to add a listener to the specified
     * locus, but it doesn't actually add any commands (since, as a
     * separate entity, it has no capability to add commands to it).
     *
     * @param locus non-null; the locus to use 
     */
    public LocusCommands (ChatLocus locus)
    {
	myLocus = locus;
	myLocusListener = new MyLocusListener ();
	mySpeak = new Speak ();
	myUpdateUsers = new UpdateUsers ();
	myLocus.addListener (myLocusListener);
    }

    /**
     * Get the commands that this object manages.
     *
     * @return an array of commands
     */
    public Command[] getCommands ()
    {
	return new Command[] { mySpeak, myUpdateUsers };
    }

    // ------------------------------------------------------------------------
    // Private helper classes

    /**
     * This is the listener that listens to the locus object and
     * tweaks the commands as appropriate.
     */
    private class MyLocusListener
    implements EntityListener
    {
	public void descriptionChanged (EntityEvent event)
	{
	    // ignore it
	}

	public void nameChanged (EntityEvent event)
	{
	    String name = event.getName ();
	    mySpeak.mySetDescription (name);
	    myUpdateUsers.mySetDescription (name);
	}
    }

    /**
     * This is the superclass of the commands defined in this class.
     */
    private abstract class MyCommand
    extends BaseCommand
    {
	/** the base description */
	private String myBaseDescription;

	/**
	 * Construct a <code>MyCommand</code>.
	 *
	 * @param baseDescription the base description to use
	 */
	/*package*/ MyCommand (String baseDescription)
	{
	    myBaseDescription = baseDescription;
	    mySetDescription (myLocus.getName ());
	}

	/**
	 * Set the enabled status of the command. This method exists
	 * merely to export an otherwise-protected method. We shouldn't
	 * have to do this--the method should be available without
	 * these shenanigans, but Java just sucks that way sometimes.
	 *
	 * @param enabled the enabled value
	 */
	/*package*/ void mySetEnabled (boolean enabled)
	{
	    setEnabled (enabled);
	}

	/**
	 * Set the description of the command based on the given locus name.
	 *
	 * @param name the new name 
	 */
	/*package*/ void mySetDescription (String name)
	{
	    setDescription (name + ": " + myBaseDescription);
	}
    }

    /** 
     * This is the command associated with the <code>speak()</code>
     * operation. 
     */
    private class Speak
    extends MyCommand
    {
	public Speak ()
	{
	    super ("Speak in this locus.");
	    setLabel ("Speak...");
	    setEnabled (true);
	}

	public Object makeArgument ()
	{
	    return new SpeakArgument ();
	}

	protected Object commandRun (Object argument)
	{
	    if (! (argument instanceof SpeakArgument))
	    {
		throw new BadArgumentException (
                    "LocusCommands.Speak.commandRun",
		    new Object[] { argument },
		    0,
		    "The argument is bad.");
	    }
	    
	    SpeakArgument sa = (SpeakArgument) argument;
	    myLocus.speak (sa.myKind, sa.myText);
	    return null;
	}
    }

    /**
     * This is the argument to a <code>Speak</code>.
     */
    public static class SpeakArgument
    implements Editable
    {
	public String myKind = SpeechKinds.SAYS;
	public String myText = "";
	private Editor myEditor = null;

	public Editor getEditor ()
	{
	    if (myEditor == null)
	    {
		ValueEditor[] fields = new ValueEditor[2];

		fields[0] = 
		    new FieldValueEditor (
                        "kind",
			"the kind of speech",
			true, false, this, "myKind");
		fields[0] = new StringTextEditor (fields[0]);

		fields[1] = 
		    new FieldValueEditor (
                        "text",
			"the text to speak",
			true, false, this, "myText");
		fields[1] = new StringTextEditor (fields[1]);

		myEditor = 
		    new FixedFieldsEditor (
                        "Speech",
			"Enter how and what you want to say.",
			fields);
	    }

	    return myEditor;
	}
    }

    /** 
     * This is the command associated with the <code>updateUsers()</code>
     * operation. 
     */
    private class UpdateUsers
    extends MyCommand
    {
	public UpdateUsers ()
	{
	    super ("Update the users in this locus.");
	    setLabel ("Update Users");
	    setEnabled (true);
	}

	public Object makeArgument ()
	{
	    return null;
	}

	protected Object commandRun (Object argument)
	{
	    if (argument != null)
	    {
		throw new BadArgumentException (
                    "LocusCommands.UpdateUsers.commandRun",
		    new Object[] { argument },
		    0,
		    "The argument must be null.");
	    }
	    
	    myLocus.updateUsers ();
	    return null;
	}
    }
}
