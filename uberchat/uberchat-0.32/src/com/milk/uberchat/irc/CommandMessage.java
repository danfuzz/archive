// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.uberchat.irc;

import com.milk.uberchat.SpeechKinds;
import com.milk.uberchat.event.MessageEvent;
import com.milk.uberchat.iface.ChatChannel;
import com.milk.uberchat.iface.ChatIdentity;
import com.milk.uberchat.iface.ChatLocus;
import com.milk.uberchat.iface.ChatUser;
import com.milk.util.ShouldntHappenException;
import java.util.Hashtable;

/**
 * This class represents most IRC command messages coming from the host.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
/*package*/ class CommandMessage
{
    /** command id for unknown command */
    private static final int UNKNOWN = -1;

    /** command id for ERROR */
    private static final int ERROR = 0;

    /** command id for INVITE */
    private static final int INVITE = 1;

    /** command id for JOIN */
    private static final int JOIN = 2;

    /** command id for KICK */
    private static final int KICK = 3;

    /** command id for MODE */
    private static final int MODE = 4;

    /** command id for NICK */
    private static final int NICK = 5;

    /** command id for NOTICE */
    private static final int NOTICE = 6;

    /** command id for PART */
    private static final int PART = 7;

    /** command id for PING */
    private static final int PING = 8;

    /** command id for PRIVMSG */
    private static final int PRIVMSG = 9;

    /** command id for QUIT */
    private static final int QUIT = 10;

    /** command id for TOPIC */
    private static final int TOPIC = 11;

    /** table mapping strings to command ids */
    static private Hashtable TheCommands;

    /** the locus */
    private ChatLocus myLocus;

    /** the user */
    private ChatUser myUser;

    /** the command */
    private String myCommand;

    /** the args */
    private String[] myArgs;

    /** the command id (derived from <code>myCommand</code>) */
    private int myCommandId;

    /** the system (derived from <code>myLocus</code>) */
    private IRCSystem mySystem;

    /** the identity (derived from <code>mySystem</code>) */
    private IRCIdentity myIdentity;

    /**
     * Construct a <code>CommandMessage</code>.
     *
     * @param locus the locus
     * @param user null-ok; the source user
     * @param command the command
     * @param args the args
     * @param commandId the command id
     */
    private CommandMessage (ChatLocus locus, ChatUser user, 
			    String command, String[] args, int commandId)
    {
	myLocus = locus;
	myUser = user;
	myCommand = command;
	myArgs = args;
	myCommandId = commandId;
	mySystem = (IRCSystem) locus.getTargetSystem ();
	myIdentity = mySystem.getIdentity ();

	Integer idobj = (Integer) TheCommands.get (command);
	if (idobj != null)
	{
	    myCommandId = idobj.intValue ();
	}
	else
	{
	    myCommandId = UNKNOWN;
	}
    }

    /**
     * Make a message from the given args. This may end up being either
     * a <code>CommandMessage</code> or a <code>CtcpMessage</code>, depending
     * on the arguments.
     *
     * @param locus the locus
     * @param user the source user
     * @param command the command
     * @param args the args (including arg[0] which is the locus name)
     */
    public static Object makeMessage (ChatLocus locus, ChatUser user, 
				      String command, String[] args)
    {
	int commandId;
	Integer idobj = (Integer) TheCommands.get (command);
	if (idobj != null)
	{
	    commandId = idobj.intValue ();
	}
	else
	{
	    commandId = UNKNOWN;
	}

	if (   ((commandId == PRIVMSG) || (commandId == NOTICE))
	    && (args.length == 3))
	{
	    String ctcpCommand = args[1].toLowerCase ();
	    return new CtcpMessage (locus, user, (commandId == PRIVMSG),
				    ctcpCommand, args[2]);
	}
	else
	{
	    return new CommandMessage (locus, user, command, args, commandId);
	}
    }

    static
    {
	TheCommands = new Hashtable ();
        TheCommands.put ("error",   new Integer (ERROR));
        TheCommands.put ("invite",  new Integer (INVITE));
        TheCommands.put ("join",    new Integer (JOIN));
        TheCommands.put ("kick",    new Integer (KICK));
        TheCommands.put ("mode",    new Integer (MODE));
        TheCommands.put ("nick",    new Integer (NICK));
        TheCommands.put ("notice",  new Integer (NOTICE));
        TheCommands.put ("part",    new Integer (PART));
        TheCommands.put ("ping",    new Integer (PING));
        TheCommands.put ("privmsg", new Integer (PRIVMSG));
        TheCommands.put ("quit",    new Integer (QUIT));
        TheCommands.put ("topic",   new Integer (TOPIC));
    }

    /**
     * Handle the message, doing whatever is necessary.
     * This is called inside <code>IRCInteractor</code> when it
     * has been handed one of these objects.
     */
    public void handleMessage ()
    {
	switch (myCommandId)
	{
	    case ERROR:   handleERROR ();   break;
	    case INVITE:  handleINVITE ();  break;
	    case JOIN:    handleJOIN ();    break;
	    case KICK:    handleKICK ();    break;
	    case MODE:    handleMODE ();    break;
	    case NICK:    handleNICK ();    break;
	    case NOTICE:  handleNOTICE ();  break;
	    case PART:    handlePART ();    break;
	    case PING:    handlePING ();    break;
	    case PRIVMSG: handlePRIVMSG (); break;
	    case QUIT:    handleQUIT ();    break;
	    case TOPIC:   handleTOPIC ();   break;
	    case UNKNOWN: handleUNKNOWN (); break;
	    default: 
	    {
		throw new ShouldntHappenException (
		    "Unhandled CommandMessage: " + myCommand);
	    }
	}
    }

    // ------------------------------------------------------------------------
    // Private helper methods

    /**
     * Handle this message as the ERROR command.
     */
    private void handleERROR ()
    {
	myIdentity.systemPrivate ("ERROR: " + myArgs[0]);
	mySystem.callSystemDisconnected ();
    }

    /**
     * Handle this message as the INVITE command.
     */
    private void handleINVITE ()
    {
	IRCChannel chan = (IRCChannel) myIdentity.nameToChannel (myArgs[1]);

	if ((myUser == null) || (chan == null))
	{
	    handleUNKNOWN ();
	    return;
	}

	StringBuffer sb = new StringBuffer ();
	sb.append ("User ");
	sb.append (myUser.getNameNickCombo ());
	sb.append (" invites you to join this channel.");

	chan.systemPrivate (sb.toString ());

	if (mySystem.getAutoJoin ())
	{
	    chan.join ();
	}
    }

    /**
     * Handle this message as the JOIN command.
     */
    private void handleJOIN ()
    {
	if (! ((myLocus instanceof ChatChannel) && (myUser != null)))
	{
	    handleUNKNOWN ();
	    return;
	}

	StringBuffer sb = new StringBuffer ();
	sb.append ("User ");
	sb.append (myUser.getNameNickCombo ());
	sb.append (" joined the channel.");
	
	((IRCUser) myUser).callAddChannel ((ChatChannel) myLocus);
	((IRCChannel) myLocus).systemBroadcast (sb.toString ());
    }

    /**
     * Handle this message as the KICK command.
     */
    private void handleKICK ()
    {
	if (! ((myLocus instanceof ChatChannel) && (myUser != null)))
	{
	    handleUNKNOWN ();
	    return;
	}

	IRCUser kickedUser = (IRCUser) myIdentity.nickToUser (myArgs[1]);

	if (kickedUser == null)
	{
	    handleUNKNOWN ();
	    return;
	}

	StringBuffer sb = new StringBuffer ();
	if (myUser != null)
	{
	    sb.append ("User ");
	    sb.append (myUser.getNameNickCombo ());
	    sb.append (" kicked user ");
	    sb.append (kickedUser.getNameNickCombo ());
	    sb.append (" from the channel");
	}
	else
	{
	    sb.append ("User ");
	    sb.append (kickedUser.getNameNickCombo ());
	    sb.append (" was kicked from the channel");
	}

	if (myArgs.length >= 3)
	{
	    sb.append (": ");
	    sb.append (myArgs[2]);
	}
	else
	{
	    sb.append ('.');
	}

	kickedUser.callRemoveChannel ((ChatChannel) myLocus);
	((IRCChannel) myLocus).systemBroadcast (sb.toString ());
    }

    /**
     * Handle this message as the MODE command.
     */
    private void handleMODE ()
    {
	if (myLocus instanceof ChatUser)
	{
	    StringBuffer sb = new StringBuffer ();
	    if (   (myUser != null)
		&& (myUser != myIdentity.getIdentityUser ()))
	    {
		sb.append ("User ");
		sb.append (myUser.getNameNickCombo ());
		sb.append (" changed the user mode: ");
	    }
	    else
	    {
		sb.append ("The user mode changed: ");
	    }

	    for (int i = 1; i < myArgs.length; i++)
	    {
		if (i != 1)
		{
		    sb.append (' ');
		}
		sb.append (myArgs[i]);
	    }
	    
	    ((IRCUser) myLocus).systemBroadcast (sb.toString ());
	    
	    // BUG--should actually do something with the info, like setting
	    // user properties
	}
	else if (myLocus instanceof ChatChannel)
	{
	    StringBuffer sb = new StringBuffer ();
	    if (myUser != null)
	    {
		sb.append ("User ");
		sb.append (myUser.getNameNickCombo ());
		sb.append (" changed the channel mode: ");
	    }
	    else
	    {
		sb.append ("The channel mode changed: ");
	    }

	    for (int i = 1; i < myArgs.length; i++)
	    {
		if (i != 1)
		{
		    sb.append (' ');
		}
		sb.append (myArgs[i]);
	    }
	    
	    ((IRCChannel) myLocus).systemBroadcast (sb.toString ());
	    
	    // BUG--should actually do something with the info, like setting
	    // channel properties
	}
	else
	{
	    handleUNKNOWN ();
	}
    }

    /**
     * Handle this message as the NICK command.
     */
    private void handleNICK ()
    {
	if (myUser == null)
	{
	    handleUNKNOWN ();
	    return;
	}

	StringBuffer sb = new StringBuffer ();
	sb.append ("User ");
	sb.append (myUser.getNameNickCombo ());
	sb.append (" changed nickname to ");
	((IRCUser) myUser).setLastKnownNickname (myArgs[0]);
	sb.append (myUser.getNameNickCombo ());
	sb.append ('.');

	userSystemBroadcast (sb.toString ());
    }

    /**
     * Handle this message as the NOTICE command.
     */
    private void handleNOTICE ()
    {
	MessageEvent event;
	if (myUser != null)
	{
	    event = MessageEvent.userBroadcast (myLocus, myUser, 
						IRCSystem.NOTICE, myArgs[1]);
	}
	else if (myLocus instanceof IRCChannel)
	{
	    event = MessageEvent.systemBroadcast (myLocus, myArgs[1]);
	}
	else
	{
	    event = MessageEvent.systemPrivate (myLocus, myArgs[1]);
	}

	mySystem.dispatchBroadcast (event);
    }

    /**
     * Handle this message as the PART command.
     */
    private void handlePART ()
    {
	if (! ((myLocus instanceof ChatChannel) && (myUser != null)))
	{
	    handleUNKNOWN ();
	    return;
	}

	StringBuffer sb = new StringBuffer ();
	sb.append ("User ");
	sb.append (myUser.getNameNickCombo ());
	sb.append (" left the channel.");

	((IRCUser) myUser).callRemoveChannel ((ChatChannel) myLocus);
	((IRCChannel) myLocus).systemBroadcast (sb.toString ());
    }

    /**
     * Handle this message as the PING command.
     */
    private void handlePING ()
    {
	mySystem.sendCommand ("pong", new String[] { myArgs[0] });
    }

    /**
     * Handle this message as the PRIVMSG command.
     */
    private void handlePRIVMSG ()
    {
	MessageEvent event;
	if (myUser != null)
	{
	    event = MessageEvent.userBroadcast (myLocus, myUser, 
						SpeechKinds.SAYS, myArgs[1]);
	}
	else if (myLocus instanceof IRCChannel)
	{
	    event = MessageEvent.systemBroadcast (myLocus, myArgs[1]);
	}
	else
	{
	    event = MessageEvent.systemPrivate (myLocus, myArgs[1]);
	}

	mySystem.dispatchBroadcast (event);
    }

    /**
     * Handle this message as the QUIT command.
     */
    private void handleQUIT ()
    {
	if (myUser == null)
	{
	    handleUNKNOWN ();
	    return;
	}

	StringBuffer sb = new StringBuffer ();
	sb.append ("User ");
	sb.append (myUser.getNameNickCombo ());
	sb.append (" signed off");
	if (myArgs[0].length () != 0)
	{
	    sb.append (": ");
	    sb.append (myArgs[0]);
	}
	else
	{
	    sb.append ('.');
	}

	userSystemBroadcast (sb.toString ());

	myIdentity.callRemoveUser ((IRCUser) myUser);
    }

    /**
     * Handle this message as the TOPIC command.
     */
    private void handleTOPIC ()
    {
	if (! (myLocus instanceof IRCChannel))
	{
	    handleUNKNOWN ();
	    return;
	}

	StringBuffer sb = new StringBuffer ();
	if (myUser != null)
	{
	    sb.append ("User ");
	    sb.append (myUser.getNameNickCombo ());
	    sb.append (" changed the topic to: ");
	}
	else
	{
	    sb.append ("The topic is now: ");
	}
	sb.append (myArgs[1]);

	((IRCChannel) myLocus).callTopicChanged (myArgs[1]);
	((IRCChannel) myLocus).systemBroadcast (sb.toString ());
    }

    /**
     * Handle this message as an unknown command.
     */
    private void handleUNKNOWN ()
    {
	StringBuffer sb = new StringBuffer ();
	sb.append ("Received unknown command \"");
	sb.append (myCommand);
	sb.append ("\" with args: ");
	for (int i = 0; i < myArgs.length; i++)
	{
	    if (i != 0)
	    {
		sb.append ("; ");
	    }
	    sb.append (myArgs[i]);
	}

	if (myUser != null)
	{
	    sb.append (" (source: ");
	    sb.append (myUser.getNameNickCombo ());
	    sb.append (")");
	}

	mySystem.dispatchBroadcast (
            MessageEvent.systemPrivate (myLocus, sb.toString ()));
    }

    /**
     * Send a <code>systemBroadcast</code> event to all the channels that
     * the identity is joined with and the user of this object is known to
     * be on.
     *
     * @param msg the message to send 
     */
    private void userSystemBroadcast (String msg)
    {
	ChatChannel[] chans = myUser.getKnownChannels ();

	for (int i = 0; i < chans.length; i++)
	{
	    if (chans[i].getJoinedState () == ChatChannel.JOINED)
	    {
		((IRCChannel) chans[i]).systemBroadcast (msg);
	    }
	}
    }
}
