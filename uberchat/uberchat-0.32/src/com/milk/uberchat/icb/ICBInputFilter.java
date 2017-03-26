// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.uberchat.icb;

import com.milk.asynch.SendFilter;
import com.milk.asynch.Sender;
import com.milk.uberchat.SpeechKinds;
import com.milk.uberchat.event.ErrorEvent;
import com.milk.uberchat.event.MessageEvent;
import com.milk.uberchat.iface.ChatChannel;
import com.milk.uberchat.iface.ChatLocus;
import com.milk.uberchat.iface.ChatUser;
import com.milk.util.ShouldntHappenException;
import java.util.Vector;

/**
 * This class is a filter on incoming data from an ICB connection. It knows
 * what sort of messages to expect, turning everything into events (speech,
 * etc) and various calls (like setting nicknames and topics). It expects
 * to be handed char-stream input from a <code>ReaderSender</code>. Any
 * non-<code>char[]</code> input is just passed straight through.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
/*package*/ class ICBInputFilter
extends SendFilter
{
    private ICBSystem mySystem;
    private ICBIdentity myIdentity;
    private String myPartialLine;
    public boolean myDebug = false;

    /**
     * Construct a filter to send to the given target.
     *
     * @param target the target to resend to
     * @param system the system to use for making messages, etc.
     */
    public ICBInputFilter (ICBSystem system, Sender target)
    {
	super (target);

	mySystem = system;
	myIdentity = mySystem.getIdentity ();

	myPartialLine = null;
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
		processInput (line);
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
    // Private helper methods

    /**
     * Process some input.
     *
     * @param line the input to process
     */
    public void processInput (String line)
    {
	if (myPartialLine != null)
	{
	    line = myPartialLine + line;
	    myPartialLine = null;
	}

	for (;;)
	{
	    int lineLen = line.length ();

	    if (lineLen == 0)
	    {
		// ignore 0-length reads
		return;
	    }

	    if (line.charAt (0) == '\0')
	    {
		// sometimes there are gratuitous nulls that need to be
		// stripped
		line = line.substring (1);
		continue;
	    }

	    int packetLength = (int) line.charAt (0);

	    if (packetLength > lineLen)
	    {
		// line isn't a full packet
		myPartialLine = line;
		return;
	    }

	    char packetType = line.charAt (1);
	    String packetData = line.substring (2, packetLength);

	    if (packetLength < lineLen)
	    {
		line = line.substring (packetLength);
	    }
	    else
	    {
		// so we'll exit on the next iteration
		line = "";
	    }

	    Vector args = new Vector ();
	    for (;;)
	    {
		int delimAt = packetData.indexOf ((char) 1);
		if (delimAt == -1)
		{
		    break;
		}
		args.addElement (packetData.substring (0, delimAt));
		packetData = packetData.substring (delimAt + 1);
	    }
	    args.addElement (packetData);
	    String[] arr = new String[args.size ()];
	    args.copyInto (arr);
	    processPacket (packetType, arr);
	}
    }

    /**
     * Process a fully-formed packet.
     *
     * @param type the packet type
     * @param args the packet arguments
     */
    private void processPacket (char type, String[] args)
    {
	if (myDebug)
	{
	    StringBuffer sb = new StringBuffer (400);
	    sb.append ("ICBInputFilter received: ");
	    sb.append (type);
	    for (int i = 0; i < args.length; i++)
	    {
		sb.append ("; ");
		sb.append (args[i]);
	    }
	    System.err.println (sb.toString ());
	}

	switch (type)
	{
	    case 'a': 
	    {
		// login success
		super.send (
                    MessageEvent.systemPrivate (myIdentity, 
						"Login successful."));
		break;
	    }
	    case 'b':
	    {
		// normal speech on a channel
		ICBUser user = (ICBUser) myIdentity.nickToUser (args[0]);
		if (user == null)
		{
		    throw new ShouldntHappenException (
                        "ICB input filter got weird userid: " + args[0]);
		}

		String[] unmangled = SpeechKinds.unmangleSpeech (args[1]);
		ICBChannel curChan = mySystem.getCurrentChannel ();
		ChatLocus loc;
		if (curChan != null)
		{
		    user.setLastKnownChannel (curChan);
		    loc = curChan;
		}
		else
		{
		    loc = myIdentity;
		}
		super.send (
                    MessageEvent.userBroadcast (loc, user,
						unmangled[0], unmangled[1]));
		break;
	    }
	    case 'c':
	    {
		// private speech
		ICBUser user = (ICBUser) myIdentity.nickToUser (args[0]);
		if (user == null)
		{
		    throw new ShouldntHappenException (
                        "ICB input filter got weird userid: " + args[0]);
		}

		String[] unmangled = SpeechKinds.unmangleSpeech (args[1]);
		super.send (
                    MessageEvent.userBroadcast (user, user,
						unmangled[0], unmangled[1]));
		break;
	    }
	    case 'd':
	    {
		// status message
		if (args.length != 2)
		{
		    String msg = "ICB input filter got weird status message: ";
		    for (int i = 0; i < args.length; i++)
		    {
			if (i != 0)
			{
			    msg += "; ";
			}
			msg += args[i];
		    }

		    throw new ShouldntHappenException (msg);
		}
		else
		{
		    processStatusMessage (args[0], args[1]);
		}
		break;
	    }
	    case 'e':
	    {
		// server error
		super.send (ICBEvent.errorPacket (mySystem, args[0]));
		break;
	    }
	    case 'f':
	    {
		// "important" message
		super.send (ICBEvent.importantPacket (mySystem, args[0]));
		break;
	    }
	    case 'g':
	    {
		// disconnect from server (server doesn't like us)
		super.send (ICBEvent.disconnectPacket (mySystem));
		break;
	    }
	    case 'i':
	    {
		// command output
		processCommandOutput (args);
		break;
	    }
	    case 'j':
	    {
		// protocol version
		processProtocolPacket (args);
		break;
	    }
	    case 'k':
	    {
		// private beep
		ICBUser user = (ICBUser) myIdentity.nickToUser (args[0]);
		if (user == null)
		{
		    throw new ShouldntHappenException (
                        "ICB input filter got weird userid: " + args[0]);
		}

		super.send (
                    MessageEvent.userBroadcast (user, user,
						SpeechKinds.BEEP, ""));
		break;
	    }
	    case 'l':
	    {
		// ping
		super.send (ICBEvent.pingPacket (mySystem));
		break;
	    }
	    case 'm':
	    {
		// pong
		super.send (ICBEvent.pongPacket (mySystem));
		break;
	    }
	    default:
	    {
		String msg = "ICB input filter got weird packet:\n" + type;
		for (int i = 0; i < args.length; i++)
		{
		    msg += "; " + args[i];
		}

		throw new ShouldntHappenException (msg);
	    }
	}
    }

    /**
     * Process a status message.
     *
     * @param category the message category
     * @param text the message text
     */
    private void processStatusMessage (String category, String text)
    {
	// use current channel as source for pretty much all messages
	ICBChannel curChan = mySystem.getCurrentChannel ();
	ChatLocus loc = (curChan == null) ? myIdentity : (ChatLocus) curChan;

	if (   category.equals ("No-Pass")
	    || category.equals ("Echo"))
	{
	    // ignore these
	}
	else if (category.equals ("Name"))
	{
	    // nickname change
	    int firstSpace = text.indexOf (' ');
	    int lastSpace = text.lastIndexOf (' ');
	    if ((firstSpace == -1) || (lastSpace == -1))
	    {
		throw new ShouldntHappenException (
                    "ICB input filter got weird name message:\n" + text);
	    }
	    String oldNick = text.substring (0, firstSpace);
	    String newNick = text.substring (lastSpace + 1);
	    ICBUser user = myIdentity.nickToUser (oldNick);
	    user.setLastKnownNickname (newNick);
	    super.send (MessageEvent.systemBroadcast (loc, text));
	}
	else if (   category.equals ("Arrive")
	         || category.equals ("Sign-on"))
	{
	    // entering the channel
	    int firstSpace = text.indexOf (' ');
	    if (firstSpace == -1)
	    {
		throw new ShouldntHappenException (
                    "ICB input filter got weird arrive/sign-on message:\n" + 
		    text);
	    }
	    String nick = text.substring (0, firstSpace);
	    ICBUser user = myIdentity.nickToUser (nick);
	    user.setLastKnownChannel (curChan);
	    super.send (MessageEvent.systemBroadcast (loc, text));
	}
	else if (category.equals ("Depart"))
	{
	    // leaving channel without logging off
	    int firstSpace = text.indexOf (' ');
	    if (firstSpace == -1)
	    {
		throw new ShouldntHappenException (
                    "ICB input filter got weird depart message:\n" + text);
	    }
	    String nick = text.substring (0, firstSpace);
	    ICBUser user = myIdentity.nickToUser (nick);
	    user.setLastKnownChannel (null);
	    super.send (MessageEvent.systemBroadcast (loc, text));
	}
	else if (category.equals ("Sign-off"))
	{
	    // logging out
	    int firstSpace = text.indexOf (' ');
	    if (firstSpace == -1)
	    {
		throw new ShouldntHappenException (
                    "ICB input filter got weird sign-off message:\n" + text);
	    }
	    String nick = text.substring (0, firstSpace);
	    ICBUser user = myIdentity.nickToUser (nick);
	    myIdentity.callRemoveUser (user);
	    super.send (MessageEvent.systemBroadcast (loc, text));
	}
	else if (category.equals ("Boot"))
	{
	    // boot messages are channel-specific
	    // but note we don't have to explicitly remove the user from
	    // the channel since a subsequent depart message will arrive
	    super.send (MessageEvent.systemBroadcast (loc, text));
	}
	else if (category.equals ("Topic"))
	{
	    int quoteAt = text.indexOf ('\"');
	    int lastQuoteAt = text.lastIndexOf ('\"');
	    if (   (quoteAt == -1) 
		|| (lastQuoteAt == -1) 
		|| (quoteAt >= lastQuoteAt))
	    {
		throw new ShouldntHappenException (
                    "ICB input filter got weird topic message:\n" + text);
	    }
	    String topic = text.substring (quoteAt + 1, lastQuoteAt);
	    super.send (MessageEvent.systemBroadcast (loc, text));
	    curChan.setLastKnownTopic (topic);
	}
	else if (category.equals ("Register"))
	{
	    // request to register the nickname--let it pass to the
	    // interactor, which is better suited to respond appropriately
	    super.send (ICBEvent.registerPacket (mySystem, text));
	}
	else if (   category.equals ("Status")
	         && text.startsWith ("You are now in group "))
	{
	    // handle group (channel) changes
	    String groupName = text.substring (21);
	    int firstSpace = groupName.indexOf (' ');
	    if (firstSpace != -1)
	    {
		groupName = groupName.substring (0, firstSpace);
	    }
	    ICBChannel chan = 
		(ICBChannel) myIdentity.nameToChannel (groupName);
	    if (chan != null)
	    {
		((ICBUser) myIdentity.getIdentityUser ()).
		    setLastKnownChannel (chan);
	    }
	    else
	    {
		// shouldn't happen
		throw new ShouldntHappenException (
                    "ICB input filter got weird group-change message:\n" + 
		    text);
	    }
	    super.send (MessageEvent.systemBroadcast (chan, text));
	}
	else
	{
	    String msg = "[" + category + "] " + text;
	    super.send (MessageEvent.systemPrivate (loc, msg));
	}
    }

    /**
     * Process a command output packet.
     *
     * @param args the args
     */
    private void processCommandOutput (String[] args)
    {
	String outType = args[0];
	if (outType.equals ("co"))
	{
	    int userEnd = args[1].indexOf ("*> ");
	    if (   (userEnd != -1)
		   && (args[1].startsWith ("<*to: ")))
	    {
		String userid = args[1].substring (6, userEnd);

		if (userid.equalsIgnoreCase ("Server"))
		{
		    // ignore the somewhat-flaky you-said-to-server
		    // messages
		    return;
		}

		String text = args[1].substring (userEnd + 3);
		ICBUser user = (ICBUser) myIdentity.nickToUser (userid);
		if (user == null)
		{
		    throw new ShouldntHappenException (
                        "ICB input filter got weird userid:\n" + userid);
		}

		String[] unmangled = SpeechKinds.unmangleSpeech (text);
		super.send (
                    MessageEvent.userBroadcast (user,
						myIdentity.getIdentityUser (), 
						unmangled[0], 
						unmangled[1]));
	    }
	    else
	    {
		super.send (MessageEvent.systemPrivate (myIdentity, args[1]));
	    }
	}
	else if (outType.equals ("wl"))
	{
	    // fields: 
	    //   "m"/"" for moderator/not
	    //   userid
	    //   idle time in seconds
	    //   response time (deprecated)
	    //   login time as seconds since Jan 1, 1970 GMT
	    //   email userid
	    //   client host name
	    //   "nr"/"" for not-registered/registered
	    ICBWho who;

	    try
	    {
		who = new ICBWho (args[1].equals ("m"),
				  args[2],
				  Integer.parseInt (args[3]),
				  Long.parseLong (args[5]),
				  args[6],
				  args[7],
				  args[8].equals (" "));
	    }
	    catch (NumberFormatException ex)
	    {
		throw new ShouldntHappenException (
                    "ICB input filter got weird who line:\n" +
		    args[1] + "; " + args[2] + "; " + args[3] + "; " +
		    args[4] + "; " + args[5] + "; " + args[6] + "; " +
		    args[7] + "; " + args[8]);
	    }

	    super.send (MessageEvent.systemPrivate (myIdentity, who));
	}
	else if (outType.equals ("wh"))
	{
	    // indication to print a "who header"; ignore it
	}
	else
	{
	    String msg = 
		"ICB input filter got weird info type " + outType + ":\n";

	    for (int i = 1; i < args.length; i++)
	    {
		if (i != 1)
		{
		    msg += "; ";
		}
		msg += args[i];
	    }

	    throw new ShouldntHappenException (msg);
	}
    }

    /**
     * Process a protocol message packet
     *
     * @param args the arguments
     */
    private void processProtocolPacket (String[] args)
    {
	String msg = "Server Information: ";

	for (int i = 0; i < args.length; i++)
	{
	    if (i != 0)
	    {
		msg += "; ";
	    }
	    msg += args[i];
	}

	super.send (MessageEvent.systemPrivate (myIdentity, msg));
    }
}

