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
import com.milk.uberchat.iface.ChatLocus;
import com.milk.uberchat.iface.ChatUser;
import com.milk.util.ShouldntHappenException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Hashtable;

/**
 * This class encapsulates a CTCP message.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
/*package*/ class CtcpMessage
{
    /** command id for UNKNOWN command */
    static public final int UNKNOWN = -1;

    /** command id for ACTION command */
    static public final int ACTION = 0;

    /** command id for CLIENTINFO command */
    static public final int CLIENTINFO = 1;

    /** command id for DCC command */
    static public final int DCC = 2;

    /** command id for ECHO command */
    static public final int ECHO = 3;

    /** command id for ERRMSG command */
    static public final int ERRMSG = 4;

    /** command id for FINGER command */
    static public final int FINGER = 5;

    /** command id for PING command */
    static public final int PING = 6;

    /** command id for SED command */
    static public final int SED = 7;

    /** command id for TIME command */
    static public final int TIME = 8;

    /** command id for USERINFO command */
    static public final int USERINFO = 9;

    /** command id for UTC command */
    static public final int UTC = 10;

    /** command id for VERSION command */
    static public final int VERSION = 11;

    /** table mapping strings to command ids */
    static private Hashtable TheCommands;

    /** the locus */
    private final ChatLocus myLocus;

    /** the user */
    private final ChatUser myUser;

    /** whether it was a normal (true) or notice (false) message */
    private final boolean myIsNormal;

    /** the command */
    private final String myCommand;
    
    /** the text */
    private final String myText;

    /** the system (derived from <code>myUser</code>) */
    private final IRCSystem mySystem;

    /** the command-id (derived from <code>myCommand</code> */
    public final int myCommandId;

    /**
     * Construct a <code>CtcpMessage</code>. It will cause the 
     * <code>myCommandId</code> variable to be calculated.
     *
     * @param locus the locus
     * @param user the user
     * @param isNormal whether it was a normal (true) or notice (false)
     * message 
     * @param command the command string
     * @param text the text
     */
    public CtcpMessage (ChatLocus locus, ChatUser user, boolean isNormal,
			String command, String text)
    {
	myLocus = locus;
	myUser = user;
	myIsNormal = isNormal;
	myCommand = command;
	myText = text;
	mySystem = (IRCSystem) myUser.getTargetSystem ();

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

    static
    {
	TheCommands = new Hashtable ();
	TheCommands.put ("action",     new Integer (ACTION));
	TheCommands.put ("clientinfo", new Integer (CLIENTINFO));
	TheCommands.put ("dcc",        new Integer (DCC));
	TheCommands.put ("echo",       new Integer (ECHO));
	TheCommands.put ("errmsg",     new Integer (ERRMSG));
	TheCommands.put ("finger",     new Integer (FINGER));
	TheCommands.put ("ping",       new Integer (PING));
	TheCommands.put ("sed",        new Integer (SED));
	TheCommands.put ("time",       new Integer (TIME));
	TheCommands.put ("userinfo",   new Integer (USERINFO));
	TheCommands.put ("utc",        new Integer (UTC));
	TheCommands.put ("version",    new Integer (VERSION));
    }

    /**
     * Handle the message, doing whatever is necessary.
     * This is called inside <code>IRCInteractor</code> when it
     * has been handed one of these objects.
     */
    public void handleMessage ()
    {
	if (! myIsNormal)
	{
	    sendUserBroadcast ();
	    return;
	}

	switch (myCommandId)
	{
	    case ACTION:     handleACTION ();     break;
	    case CLIENTINFO: handleCLIENTINFO (); break;
	    case DCC:        handleDCC ();        break;
	    case ECHO:       handleECHO ();       break;
	    case ERRMSG:     handleERRMSG ();     break;
	    case FINGER:     handleFINGER ();     break;
	    case PING:       handlePING ();       break;
	    case SED:        handleSED ();        break;
	    case TIME:       handleTIME ();       break;
	    case USERINFO:   handleUSERINFO ();   break;
	    case UTC:        handleUTC ();        break;
	    case VERSION:    handleVERSION ();    break;
	    case UNKNOWN:    handleUNKNOWN ();    break;
	    default: 
	    {
		throw new ShouldntHappenException (
		    "Unhandled CtcpMessage: " + myCommand);
	    }
	}
    }

    // ------------------------------------------------------------------------
    // Private helper methods

    /**
     * Handle this message as a ctcp ACTION.
     */
    private void handleACTION ()
    {
	String text = myText;
	String kind = SpeechKinds.ME;
	int spaceAt = text.indexOf (' ');
	int colonAt = text.indexOf (':');
	if ((spaceAt == 2) && (text.startsWith ("'s")))
	{
	    kind = SpeechKinds.MY;
	    text = text.substring (3);
	}
	else if (spaceAt == (colonAt + 1))
	{
	    kind = text.substring (0, colonAt).toLowerCase ().intern ();
	    text = text.substring (spaceAt + 1);
	}
	    
	mySystem.dispatchBroadcast (
            MessageEvent.userBroadcast (myLocus, myUser, kind, text));
    }

    /**
     * Handle this message as a ctcp CLIENTINFO.
     */
    private void handleCLIENTINFO ()
    {
	sendUserBroadcastIfVerbose ();
	myUser.speak ("ctcp-reply-clientinfo",
		      "ACTION CLIENTINFO ECHO ERRMSG FINGER PING TIME " + 
		      "VERSION");
    }

    /**
     * Handle this message as a ctcp DCC.
     */
    private void handleDCC ()
    {
	sendUserBroadcastIfVerbose ();
	replyUnknownCommand ();
    }

    /**
     * Handle this message as a ctcp ECHO.
     */
    private void handleECHO ()
    {
	sendUserBroadcastIfVerbose ();
	myUser.speak ("ctcp-reply-echo", myText);
    }

    /**
     * Handle this message as a ctcp ERRMSG.
     */
    private void handleERRMSG ()
    {
	sendUserBroadcastIfVerbose ();
	myUser.speak ("ctcp-reply-errmsg", myText);
    }

    /**
     * Handle this message as a ctcp FINGER.
     */
    private void handleFINGER ()
    {
	sendUserBroadcastIfVerbose ();
	// BUG--make it real
	myUser.speak ("ctcp-reply-finger",
		      mySystem.getUserid ());
    }

    /**
     * Handle this message as a ctcp PING.
     */
    private void handlePING ()
    {
	// make it real
	sendUserBroadcastIfVerbose ();
	String reply = myText;
	if (reply.length () == 0)
	{
	    reply = "0";
	}
	myUser.speak ("ctcp-reply-ping", reply);
    }

    /**
     * Handle this message as a ctcp SED.
     */
    private void handleSED ()
    {
	sendUserBroadcastIfVerbose ();
	replyUnknownCommand ();
    }

    /**
     * Handle this message as a ctcp TIME.
     */
    private void handleTIME ()
    {
	sendUserBroadcastIfVerbose ();
	DateFormat df = DateFormat.getDateTimeInstance ();
	String reply = df.format (new Date ());
	myUser.speak ("ctcp-reply-time", reply);
    }

    /**
     * Handle this message as a ctcp USERINFO.
     */
    private void handleUSERINFO ()
    {
	sendUserBroadcastIfVerbose ();
	replyUnknownCommand ();
    }

    /**
     * Handle this message as a ctcp UTC.
     */
    private void handleUTC ()
    {
	sendUserBroadcastIfVerbose ();
	replyUnknownCommand ();
    }

    /**
     * Handle this message as a ctcp VERSION.
     */
    private void handleVERSION ()
    {
	sendUserBroadcastIfVerbose ();
	myUser.speak ("ctcp-reply-version",
		      IRCSystem.VERSION_STRING);
    }

    /**
     * Handle this message as an unknown-type ctcp message.
     */
    private void handleUNKNOWN ()
    {
	sendUserBroadcastIfVerbose ();
	replyUnknownCommand ();
    }

    /**
     * Send a <code>userBroadcast</code> event representing this message
     * (in raw form).
     */
    private void sendUserBroadcast ()
    {
	String kind = (myIsNormal ? "ctcp-" : "ctcp-reply-") + myCommand;
	mySystem.dispatchBroadcast (
            MessageEvent.userBroadcast (myLocus, myUser, kind, myText));
    }

    /**
     * Send a <code>userBroadcast</code> event representing this message,
     * but only if the system has verbose CTCP turned on.
     */
    private void sendUserBroadcastIfVerbose ()
    {
	if (mySystem.getVerboseCtcp ())
	{
	    sendUserBroadcast ();
	}
    }

    /**
     * Reply to the original sender that this is an unknown command.
     */
    private void replyUnknownCommand ()
    {
	myUser.speak ("ctcp-reply-errmsg",
		      "CTCP command " + myCommand + " not supported.");
    }
}
