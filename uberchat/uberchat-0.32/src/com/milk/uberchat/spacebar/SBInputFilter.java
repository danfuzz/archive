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

import com.milk.asynch.SendFilter;
import com.milk.asynch.Sender;
import com.milk.uberchat.SpeechKinds;
import com.milk.uberchat.event.ErrorEvent;
import com.milk.uberchat.event.MessageEvent;
import com.milk.uberchat.iface.ChatChannel;
import com.milk.uberchat.iface.ChatLocus;
import com.milk.uberchat.iface.ChatUser;
import com.milk.util.ShouldntHappenException;
import java.util.Hashtable;
import java.util.Vector;

/**
 * This class is a filter on incoming data from a spacebar connection. It
 * knows what sort of prompts to expect and how to coalesce lines, turning
 * everything into events and other objects. It will also synchronously
 * set the last known nicknames of users that are mentioned. It expects to
 * be handed line-type input from a LineFilter. Any non-char[] input is
 * just passed straight through.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998 Dan Bornstein, all rights reserved. 
 */
/*package*/ class SBInputFilter
extends SendFilter
{
    private SBSystem mySystem;
    private SBIdentity myIdentity;
    private String myPartialLine;
    private Hashtable myPromptTable;

    /**
     * Construct a filter to send to the given target.
     *
     * @param target the target to resend to
     * @param system the system to use for making messages, etc.
     */
    public SBInputFilter (SBSystem system, Sender target)
    {
	super (target);

	mySystem = system;
	myIdentity = mySystem.getIdentity ();

	myPartialLine = null;
	makePromptTable ();
    }

    // ------------------------------------------------------------------------
    // SendSource interface methods

    /**
     * Send a message.
     *
     * @param message the message to send
     */
    public synchronized void send (Object message)
    {
	if (message instanceof char[])
	{
	    String line = new String ((char[]) message);
	    try
	    {
		processLine (line);
	    }
	    catch (ShouldntHappenException ex)
	    {
		mySystem.bugReport (ex);
	    }
	    catch (Exception ex)
	    {
		mySystem.bugReport (
		    new ShouldntHappenException (
                        "Caught exception at top of ICB input filter.",
			ex));
	    }
	}
	else
	{
	    super.send (message);
	}
    }

    // ------------------------------------------------------------------------
    // Helper methods

    /**
     * Create the prompt table (myPromptTable instance variable).
     */
    private void makePromptTable ()
    {
	myPromptTable = new Hashtable ();

	myPromptTable.put ("Enter your member name:  ",
			    promptMessage ("userid"));

	myPromptTable.put ("Password: ",
			    promptMessage ("password"));

	myPromptTable.put ("New name: ",
			    promptMessage ("new-name"));

	myPromptTable.put ("New nickname: ",
			    promptMessage ("new-nickname"));

	myPromptTable.put ("Msg: ",
			    promptMessage ("message"));

	myPromptTable.put ("To: ",
			    promptMessage ("to"));

	myPromptTable.put ("New value: ",
			    promptMessage ("new-value"));

	myPromptTable.put ("Channel: ",
			    promptMessage ("channel"));

	myPromptTable.put ("Incorrect password.\n",
			    promptMessage ("incorrect-password"));

	myPromptTable.put (
            "<m>essage, <a>ctive, <d>efault, <q>uit to do nothing: ",
	    promptMessage ("/*"));

	myPromptTable.put (
            "Enter new format, or <CR> to keep or quit. ? for help\n",
	    promptMessage ("enter-new-format"));
    }

    /**
     * Helper method to create a <code>SBPromptMessage</code>.
     *
     * @param name the name of the prompt
     * @return the <code>SBPromptMessage</code>
     */
    private SBPromptMessage promptMessage (String name)
    {
	return new SBPromptMessage (name);
    }

    /**
     * Process a line (or partial line) of input.
     *
     * @param line the line to process
     */
    public void processLine (String line)
    {
	if (myPartialLine != null)
	{
	    if (myPartialLine.startsWith ("~~~")
		&& line.startsWith ("     "))
	    {
		// kill formatting from continuation of single speech line
		line = line.substring (5);
		if (myPartialLine.endsWith ("\n"))
		{
		    myPartialLine = 
			myPartialLine.substring (
			    0, 
			    myPartialLine.length () - 1) + 
			" ";
		}
	    }

	    line = myPartialLine + line;
	    myPartialLine = null;
	}

	if (line.charAt (0) == 0x07 /* BEL */)
	{
	    // kill off an explicit BEL character
	    line = line.substring (1);
	}

	if (   line.startsWith ("~~~")
	    && (! line.endsWith ("~~~\n")))
	{
	    // it's still a partial speech line, so stop now
	    myPartialLine = line;
	    return;
	}

	// may be a prompt; check for that
	SBPromptMessage prompt = (SBPromptMessage) myPromptTable.get (line);
	if (prompt != null)
	{
	    // yes it was, send the prompt instead of the original line
	    super.send (MessageEvent.systemPrivate (myIdentity, prompt));
	    return;
	}

	if (! line.endsWith ("\n"))
	{
	    // partial line and not a prompt, so assume there's more to come
	    myPartialLine = line;
	    return;
	}

	// we have a complete, non-prompt line at this point

	// if it looks like speech, parse it as such
	if (line.startsWith ("~~~")
	    && ! line.equals ("~~~\\P\\_B_ %u~$`%h~$`%m~~~\n"))
	{
	    parseSpeech (line);
	    return;
	}

	// if it looks like a system message, parse it as such
	if (line.startsWith ("-- ") || line.startsWith (" * "))
	{
	    parseSystemMessage (line);
	    return;
	}

	// weird special case, channel change
	if (line.startsWith ("Channel changed to"))
	{
	    parseChannelChange (line);
	    return;
	}

	// weird special case, lines to ignore
	if (   line.startsWith ("Message sent to ")
	    || line.startsWith ("Beep sent"))
	{
	    return;
	}

	// it's nothing we know about specially, assume it's system private
	super.send (
            MessageEvent.systemPrivate (
                myIdentity,
		line.substring (0, line.length () - 1)));
    }

    /**
     * Take a line representing speech and turn it into an appropriate
     * <code>userBroadcast</code> event, sending it to the target.
     *
     * @param line the line of text to parse
     */
    private void parseSpeech (String line)
    {
	int delim1 = line.indexOf (' ');
	int delim2 = line.indexOf ("~$`", delim1 + 1);
	int delim3 = line.indexOf ("~$`", delim2 + 3);
	int delim4 = line.indexOf ("~~~", delim3 + 3);

	char typeChar   = line.charAt (3);
	String userid   = line.substring (delim1 + 1, delim2);
	String nickname = line.substring (delim2 + 3, delim3);
	String text     = line.substring (delim3 + 3, delim4);

	SBUser user = (SBUser) myIdentity.nameToUser (userid);
	if (user == null)
	{
	    throw new ShouldntHappenException (
                "SpaceBar input filter got weird userid: " + userid);
	}
	
	user.setLastKnownNickname (nickname);

	ChatLocus locus;
	if (typeChar == 'P')
	{
	    // private message
	    locus = user;
	}
	else if (typeChar == 'B')
	{
	    // broadcast message
	    locus = myIdentity;
	}
	else
	{
	    // channel message
	    SBChannel curChan = mySystem.getCurrentChannel ();
	    user.setLastKnownChannel (curChan);
	    locus = curChan;
	}

	while (text != null)
	{
	    int nlAt = text.indexOf ('\n');
	    String one;
	    if (nlAt == -1)
	    {
		one = text;
		text = null;
	    }
	    else
	    {
		one = text.substring (0, nlAt);
		text = text.substring (nlAt + 1);
	    }

	    String[] unmangled = SpeechKinds.unmangleSpeech (one);
	    super.send (
                MessageEvent.userBroadcast (locus, user,
					    unmangled[0], unmangled[1]));
	}
    }

    /**
     * Take a system message line in raw form and turn it into an
     * appropriate <code>MessageEvent</code>, sending it to the target.
     *
     * @param line the raw line to parse
     */
    private void parseSystemMessage (String line)
    {
	// get rid of prefix ("-- " or " * ") and trailing newline
	line = line.substring (3, line.length () - 1);

	if (line.charAt (0) == '#')
	{
	    // kill off slot number, if any
	    int i = 1;
	    while (line.charAt (i) != ' ')
	    {
		i++;
	    }
	    line = line.substring (i + 1);
	}
	
	int channelAt = line.indexOf (" channel ");
	if (channelAt != -1)
	{
	    parseUserNoticeLine (line, channelAt);
	    return;
	}

	int isNowAt = line.indexOf (" is now ");
	if (isNowAt != -1)
	{
	    parseNickChangeLine (line, isNowAt);
	    return;
	}

	if (line.startsWith ("You are being *beeped* by"))
	{
	    parseBeepLine (line);
	    return;
	}

	super.send (MessageEvent.systemBroadcast (myIdentity, line));
    }

    /**
     * Take a line containing a notice about a user (e.g., "Disconnected!
     * channel 04: scrooge/mcduck") and process it appropriately.
     *
     * @param line the raw line to parse
     * @param channelAt the index that the string " channel " was found at
     */
    private void parseUserNoticeLine (String line, int channelAt)
    {
	int colonAt = line.indexOf (": ");
	int slashAt = line.indexOf ('/');
	String channel = line.substring (channelAt + 9, colonAt);
	String action = line.substring (0, channelAt);
	String userid = line.substring (colonAt + 2, slashAt);
	String nick = line.substring (slashAt + 1, line.length ());
	SBUser user = (SBUser) myIdentity.nameToUser (userid);
	SBChannel chan;
	if (channel.equals ("**"))
	{
	    chan = null;
	}
	else
	{
	    chan = (SBChannel) myIdentity.nameToChannel (channel);
	}

	user.setLastKnownNickname (nick);

	// note, for most messages, we only send out a systemBroadcast if
	// it happens on the channel we're on
	SBChannel curChan = mySystem.getCurrentChannel ();

	if (   action.equals ("Disconnected!")
	    || action.equals ("Logged off")
	    || action.equals ("Account froze on"))
	{
	    myIdentity.userLoggedOut (user);
	    if (chan == curChan)
	    {
		super.send (MessageEvent.systemBroadcast (chan, line));
	    }
	}
	else if (   action.equals ("Joined")
		 || action.equals ("Logged on"))
	{
	    user.setLastKnownChannel (chan);
	    if (chan == curChan)
	    {
		super.send (MessageEvent.systemBroadcast (chan, line));
	    }
	}
	else if (action.equals ("Left"))
	{
	    user.setLastKnownChannel (null);
	    if (chan == curChan)
	    {
		super.send (MessageEvent.systemBroadcast (chan, line));
	    }
	}
	else
	{
	    // assume anything else is system wide unless it happens
	    // on the channel we're on; and it also implies setting
	    // (not resetting) last known channel
	    user.setLastKnownChannel (chan);
	    ChatLocus loc = 
		(chan == curChan) 
		? (ChatLocus) curChan 
		: (ChatLocus) myIdentity;
	    super.send (MessageEvent.systemBroadcast (loc, line));
	}
    }

    /**
     * Take a line containing a nickname change and process it appropriately.
     *
     * @param line the raw line to parse
     * @param isNowAt the index of the string " is now " in the line
     */
    private void parseNickChangeLine (String line, int isNowAt)
    {
	int slashAt = line.indexOf ('/');
	String userid = line.substring (0, slashAt);
	String newNick = line.substring (isNowAt + 9 + userid.length ());

	SBUser user = (SBUser) myIdentity.nameToUser (userid);
	user.setLastKnownNickname (newNick);

	SBChannel curChan = mySystem.getCurrentChannel ();
	user.setLastKnownChannel (curChan);

	// it's a channel-wide message
	super.send (MessageEvent.systemBroadcast (curChan, line));
    }

    /**
     * Take a line indicating a channel change in raw form and make the
     * implied local state changes.
     *
     * @param line the raw line to parse 
     */
    private void parseChannelChange (String line)
    {
	int periodAt = line.indexOf ('.');
	int colonAt = line.indexOf (':');

	if ((periodAt != -1) && (colonAt != -1) && (periodAt > colonAt))
	{
	    periodAt = -1;
	}

	String chan;
	String topic;

	if (periodAt != -1)
	{
	    chan = line.substring (19, periodAt);
	    topic = "";
	}
	else
	{
	    chan = line.substring (19, colonAt);
	    topic = line.substring (colonAt + 3);
	}

	SBChannel sbc = (SBChannel) myIdentity.nameToChannel (chan);
	if (sbc != null)
	{
	    ((SBUser) myIdentity.getIdentityUser ()).setLastKnownChannel (sbc);
	}
	else
	{
	    throw new ShouldntHappenException (
                "SpaceBar input filter got weird channel change message:\n" + 
		line);
	}
    }

    /**
     * Take a beep line in raw form and turn it into an
     * appropriate <code>MessageEvent</code>, sending it to the target.
     */
    private void parseBeepLine (String line)
    {
	int lastSpace = line.lastIndexOf (' ');
	int lastSlash = line.indexOf ('/', lastSpace);
	if ((lastSpace == -1) || (lastSlash == -1))
	{
	    throw new ShouldntHappenException (
                "SpaceBar input filter got weird beep line:\n" + line);
	}

	String userid = line.substring (lastSpace + 1, lastSlash);
	String nickname = line.substring (lastSlash + 1);

	SBUser user = (SBUser) myIdentity.nameToUser (userid);
	if (user == null)
	{
	    throw new ShouldntHappenException (
                "SpaceBar input filter got weird userid: " + userid);
	}
	
	user.setLastKnownNickname (nickname);
	super.send (
            MessageEvent.userBroadcast (user, user, SpeechKinds.BEEP, ""));
    }
}
