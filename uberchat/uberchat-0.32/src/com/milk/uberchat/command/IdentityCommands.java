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
import com.milk.objed.CheckboxEditor;
import com.milk.objed.Editable;
import com.milk.objed.Editor;
import com.milk.objed.FieldValueEditor;
import com.milk.objed.FixedFieldsEditor;
import com.milk.objed.StringTextEditor;
import com.milk.objed.ValueEditor;
import com.milk.uberchat.event.EntityEvent;
import com.milk.uberchat.event.EntityListener;
import com.milk.uberchat.iface.ChatIdentity;
import com.milk.uberchat.SpeechKinds;
import com.milk.util.BadArgumentException;

/**
 * This class contains <code>Command</code>s for <code>ChatIdentity</code>
 * objects as inner classes and knows how to manage them.
 *
 * @see com.milk.uberchat.iface.ChatIdentity
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class IdentityCommands
{
    /** the <code>ChatIdentity</code> to use */
    private ChatIdentity myIdentity;

    /** the listener that listens to the identity */
    private MyIdentityListener myIdentityListener;

    /** the <code>joinOrLeaveChannel()</code> command object */
    private JoinOrLeaveChannel myJoinOrLeaveChannel;

    /** the <code>rawSend()</code> command object */
    private RawSend myRawSend;

    /** the <code>setNickname()</code> command object */
    private SetNickname mySetNickname;

    /** the <code>speakToChannel()</code> command object */
    private SpeakToChannel mySpeakToChannel;

    /** the <code>speakToUser()</code> command object */
    private SpeakToUser mySpeakToUser;

    /**
     * Construct an <code>IdentityCommands</code> object. Constructing the
     * object automatically causes it to add a listener to the specified
     * identity, but it doesn't actually add any commands (since, as a
     * separate entity, it has no capability to add commands to it).
     *
     * @param identity non-null; the identity to use 
     */
    public IdentityCommands (ChatIdentity identity)
    {
	myIdentity = identity;
	myIdentityListener = new MyIdentityListener ();
	myJoinOrLeaveChannel = new JoinOrLeaveChannel ();
	myRawSend = new RawSend ();
	mySetNickname = new SetNickname ();
	mySpeakToChannel = new SpeakToChannel ();
	mySpeakToUser = new SpeakToUser ();
	myIdentity.addListener (myIdentityListener);
    }

    /**
     * Get the commands that this object manages.
     *
     * @return an array of commands
     */
    public Command[] getCommands ()
    {
	return new Command[] { myJoinOrLeaveChannel, myRawSend, mySetNickname,
			       mySpeakToChannel, mySpeakToUser };
    }

    // ------------------------------------------------------------------------
    // Private helper classes

    /**
     * This is the listener that listens to the identity object and
     * tweaks the commands as appropriate.
     */
    private class MyIdentityListener
    implements EntityListener
    {
	public void descriptionChanged (EntityEvent event)
	{
	    // ignore it
	}

	public void nameChanged (EntityEvent event)
	{
	    String name = event.getName ();
	    myJoinOrLeaveChannel.mySetDescription (name);
	    myRawSend.mySetDescription (name);
	    mySetNickname.mySetDescription (name);
	    mySpeakToChannel.mySetDescription (name);
	    mySpeakToUser.mySetDescription (name);
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
	    mySetDescription (myIdentity.getName ());
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
     * This is the command associated with the
     * <code>JoinOrLeaveChannel()</code> operation. 
     */
    private class JoinOrLeaveChannel
    extends MyCommand
    {
	public JoinOrLeaveChannel ()
	{
	    super ("Join or leave a channel.");
	    setLabel ("Join/Leave...");
	    setEnabled (true);
	}

	public Object makeArgument ()
	{
	    return new JoinOrLeaveArgument ();
	}

	protected Object commandRun (Object argument)
	{
	    if (! (argument instanceof JoinOrLeaveArgument))
	    {
		throw new BadArgumentException (
                    "IdentityCommands.JoinOrLeave.commandRun",
		    new Object[] { argument },
		    0,
		    "The argument is bad.");
	    }
	    
	    JoinOrLeaveArgument arg = (JoinOrLeaveArgument) argument;
	    myIdentity.joinOrLeaveChannel (arg.myChannelName);
	    return null;
	}
    }

    /**
     * This is the argument to a <code>JoinOrLeave</code>.
     */
    public static class JoinOrLeaveArgument
    implements Editable
    {
	public String myChannelName = "";
	private Editor myEditor = null;

	public Editor getEditor ()
	{
	    if (myEditor == null)
	    {
		ValueEditor[] fields = new ValueEditor[1];

		fields[0] = 
		    new FieldValueEditor (
                        "channel",
			"the channel name",
			true, false, this, "myChannelName");
		fields[0] = new StringTextEditor (fields[0]);

		myEditor = 
		    new FixedFieldsEditor (
                        "Join/Leave",
			"Enter the name of the channel to join or leave.",
			fields);
	    }

	    return myEditor;
	}
    }

    /** 
     * This is the command associated with the
     * <code>rawSend()</code> operation. 
     */
    private class RawSend
    extends MyCommand
    {
	public RawSend ()
	{
	    super ("Raw send to a system.");
	    setLabel ("Raw Send...");
	    setEnabled (true);
	}

	public Object makeArgument ()
	{
	    return new RawSendArgument ();
	}

	protected Object commandRun (Object argument)
	{
	    if (! (argument instanceof RawSendArgument))
	    {
		throw new BadArgumentException (
                    "IdentityCommands.RawSend.commandRun",
		    new Object[] { argument },
		    0,
		    "The argument is bad.");
	    }
	    
	    RawSendArgument arg = (RawSendArgument) argument;
	    myIdentity.speak (arg.myKind, arg.myRaw);
	    return null;
	}
    }

    /**
     * This is the argument to a <code>RawSend</code>.
     */
    public static class RawSendArgument
    implements Editable
    {
	public String myRaw = "";
	public String myKind = SpeechKinds.RAW;
	private Editor myEditor = null;

	public Editor getEditor ()
	{
	    if (myEditor == null)
	    {
		ValueEditor[] fields = new ValueEditor[2];
		CheckboxEditor cbe;

		fields[0] = 
		    new FieldValueEditor (
                        "raw",
			"the raw text to send",
			true, false, this, "myRaw");
		fields[0] = new StringTextEditor (fields[0]);

		fields[1] = 
		    new FieldValueEditor (
                        "add newline",
			"optionally add a newline (if appropriate)",
			true, false, this, "myKind");
		fields[1] = cbe = new CheckboxEditor (fields[1]);
		cbe.setValues (SpeechKinds.RAW, SpeechKinds.RAW_NO_NL);
		cbe.setCheckboxText (
                    "Add a newline (if appropriate)");

		myEditor = 
		    new FixedFieldsEditor (
                        "Raw Send",
			"Send a raw command to the host system.",
			fields);
	    }

	    return myEditor;
	}
    }

    /** 
     * This is the command associated with the
     * <code>SetNickname()</code> operation. 
     */
    private class SetNickname
    extends MyCommand
    {
	public SetNickname ()
	{
	    super ("Set the nickname of this identity.");
	    setLabel ("Set Nickname...");
	    setEnabled (true);
	}

	public Object makeArgument ()
	{
	    return new SetNicknameArgument ();
	}

	protected Object commandRun (Object argument)
	{
	    if (! (argument instanceof SetNicknameArgument))
	    {
		throw new BadArgumentException (
                    "IdentityCommands.SetNickname.commandRun",
		    new Object[] { argument },
		    0,
		    "The argument is bad.");
	    }
	    
	    SetNicknameArgument arg = (SetNicknameArgument) argument;
	    myIdentity.setNickname (arg.myNickname);
	    return null;
	}
    }

    /**
     * This is the argument to a <code>SetNickname</code>.
     */
    public static class SetNicknameArgument
    implements Editable
    {
	public String myNickname = "";
	private Editor myEditor = null;

	public Editor getEditor ()
	{
	    if (myEditor == null)
	    {
		ValueEditor[] fields = new ValueEditor[1];

		fields[0] = 
		    new FieldValueEditor (
                        "nickname",
			"the new nickname",
			true, false, this, "myNickname");
		fields[0] = new StringTextEditor (fields[0]);

		myEditor = 
		    new FixedFieldsEditor (
                        "Set Nickname",
			"Enter a new nickname.",
			fields);
	    }

	    return myEditor;
	}
    }

    /** 
     * This is the command associated with the
     * <code>speakToChannel()</code> operation. 
     */
    private class SpeakToChannel
    extends MyCommand
    {
	public SpeakToChannel ()
	{
	    super ("Speak to a channel.");
	    setLabel ("Speak to Channel...");
	    setEnabled (true);
	}

	public Object makeArgument ()
	{
	    return new SpeakToChannelArgument ();
	}

	protected Object commandRun (Object argument)
	{
	    if (! (argument instanceof SpeakToChannelArgument))
	    {
		throw new BadArgumentException (
                    "IdentityCommands.SpeakToChannel.commandRun",
		    new Object[] { argument },
		    0,
		    "The argument is bad.");
	    }
	    
	    SpeakToChannelArgument arg = (SpeakToChannelArgument) argument;
	    myIdentity.speakToChannel (
                arg.myChannelName, arg.myKind, arg.myText);
	    return null;
	}
    }

    /**
     * This is the argument to a <code>SpeakToChannel</code>.
     */
    public static class SpeakToChannelArgument
    implements Editable
    {
	public String myChannelName = "";
	public String myKind = SpeechKinds.SAYS;
	public String myText = "";
	private Editor myEditor = null;

	public Editor getEditor ()
	{
	    if (myEditor == null)
	    {
		ValueEditor[] fields = new ValueEditor[3];
		CheckboxEditor cbe;

		fields[0] = 
		    new FieldValueEditor (
                        "channel",
			"the channel name",
			true, false, this, "myChannelName");
		fields[0] = new StringTextEditor (fields[0]);

		fields[1] = 
		    new FieldValueEditor (
                        "kind",
			"the kind of speech",
			true, false, this, "myKind");
		fields[1] = new StringTextEditor (fields[1]);

		fields[2] = 
		    new FieldValueEditor (
                        "text",
			"the text to speak",
			true, false, this, "myText");
		fields[2] = new StringTextEditor (fields[2]);

		myEditor = 
		    new FixedFieldsEditor (
                        "Speak to Channel",
			"Speak to a channel.",
			fields);
	    }

	    return myEditor;
	}
    }

    /** 
     * This is the command associated with the
     * <code>speakToUser()</code> operation. 
     */
    private class SpeakToUser
    extends MyCommand
    {
	public SpeakToUser ()
	{
	    super ("Speak to a user.");
	    setLabel ("Speak to User...");
	    setEnabled (true);
	}

	public Object makeArgument ()
	{
	    return new SpeakToUserArgument ();
	}

	protected Object commandRun (Object argument)
	{
	    if (! (argument instanceof SpeakToUserArgument))
	    {
		throw new BadArgumentException (
                    "IdentityCommands.SpeakToUser.commandRun",
		    new Object[] { argument },
		    0,
		    "The argument is bad.");
	    }
	    
	    SpeakToUserArgument arg = (SpeakToUserArgument) argument;
	    myIdentity.speakToUser (
                arg.myUserName, arg.myKind, arg.myText);
	    return null;
	}
    }

    /**
     * This is the argument to a <code>SpeakToUser</code>.
     */
    public static class SpeakToUserArgument
    implements Editable
    {
	public String myUserName = "";
	public String myKind = SpeechKinds.SAYS;
	public String myText = "";
	private Editor myEditor = null;

	public Editor getEditor ()
	{
	    if (myEditor == null)
	    {
		ValueEditor[] fields = new ValueEditor[3];
		CheckboxEditor cbe;

		fields[0] = 
		    new FieldValueEditor (
                        "user",
			"the user name",
			true, false, this, "myUserName");
		fields[0] = new StringTextEditor (fields[0]);

		fields[1] = 
		    new FieldValueEditor (
                        "kind",
			"the kind of speech",
			true, false, this, "myKind");
		fields[1] = new StringTextEditor (fields[1]);

		fields[2] = 
		    new FieldValueEditor (
                        "text",
			"the text to speak",
			true, false, this, "myText");
		fields[2] = new StringTextEditor (fields[2]);

		myEditor = 
		    new FixedFieldsEditor (
                        "Speak to User",
			"Speak to a user.",
			fields);
	    }

	    return myEditor;
	}
    }
}
