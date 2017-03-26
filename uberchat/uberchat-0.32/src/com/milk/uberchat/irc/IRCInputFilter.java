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
 * This class is a filter on incoming data from an IRC connection. It knows
 * what sort of messages to expect, turning everything into appropriate
 * events (speech, etc.) and various calls (like setting nicknames and
 * topics). It expects to be handed lines from a <code>LineFilter</code>.
 * Any non-<code>char[]</code> input is just passed straight through.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
/*package*/ class IRCInputFilter
extends SendFilter
{
    private IRCSystem mySystem;
    private IRCIdentity myIdentity;

    /**
     * Construct a filter to send to the given target.
     *
     * @param target the target to resend to
     * @param system the system to use for making messages, etc.
     */
    public IRCInputFilter (IRCSystem system, Sender target)
    {
	super (target);

	mySystem = system;
	myIdentity = mySystem.getIdentity ();
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
	try
	{
	    if (message instanceof char[])
	    {
		processLine ((char[]) message);
	    }
	    else
	    {
		super.send (message);
	    }
	}
	catch (ShouldntHappenException ex)
	{
	    mySystem.bugReport (ex);
	}
	catch (Exception ex)
	{
	    String duringWhat;
	    if (message instanceof char[])
	    {
		duringWhat = "\"" + new String ((char[]) message) + "\"";
	    }
	    else
	    {
		duringWhat = message.toString ();
	    }

	    mySystem.bugReport (
	        new ShouldntHappenException (
                    "Caught exception at top of IRC input filter while " +
		    "processing message:\n" + duringWhat,
		    ex));
	}
    }

    // ------------------------------------------------------------------------
    // Private helper methods

    /**
     * Process a fully-formed line
     *
     * @param line the line to process
     */
    private void processLine (char[] line)
    {
	String source = "";
	String sourceInfo = "";
	int at = 0;

	if (line[0] == ':')
	{
	    at = 1;
	    while ((line[at] != ' ') && (line[at] != '!'))
	    {
		at++;
	    }
	    source = new String (line, 1, at - 1);
	    if (line[at] == '!')
	    {
		at++;
		int infoAt = at;
		while (line[at] != ' ')
		{
		    at++;
		}
		sourceInfo = new String (line, infoAt, at - infoAt);
	    }
	    while (line[at] == ' ')
	    {
		at++;
	    }
	}

	int commandAt = at;
	while (line[at] != ' ')
	{
	    at++;
	}
	String command = 
	    new String (line, commandAt, at - commandAt).toLowerCase ();

	Vector args = new Vector ();
	for (;;)
	{
	    while (line[at] == ' ')
	    {
		at++;
	    }

	    if (line[at] == '\n')
	    {
		break;
	    }
	    if (line[at] == ':')
	    {
		at++;
		if (   (line[at] == '\01')
		    && (line[line.length - 2] == '\01'))
		{
		    // special case to make CTCP processing vaguely efficient
		    at++;
		    int argAt = at;
		    while ((line[at] != ' ') && (line[at] != '\01'))
		    {
			at++;
		    }
		    args.addElement (new String (line, argAt, at - argAt));
		    while (line[at] == ' ')
		    {
			at++;
		    }
		    args.addElement (
                        new String (line, at, line.length - at - 2));
		    break;
		}
		args.addElement (
                    new String (line, at, line.length - at - 1));
		break;
	    }

	    int argAt = at;
	    while ((line[at] != ' ') && (line[at] != '\n'))
	    {
		at++;
	    }
	    args.addElement (new String (line, argAt, at - argAt));
	}

	String[] argArr = new String[args.size ()];
	args.copyInto (argArr);
	processServerCommand (source, sourceInfo, command, argArr);
    }

    /**
     * Process a server command, that is, a command that came from the
     * server to us.
     *
     * @param source the source field (or <code>""</code> if empty)
     * @param sourceInfo the source info field (or <code>""</code> if empty)
     * @param command the command name (should be lower-cased)
     * @param args the command arguments
     */
    private void processServerCommand (String source, String sourceInfo,
				       String command, String[] args)
    {
	if (Character.isDigit (command.charAt (0)))
	{
	    super.send (new ServerReply (mySystem, command, args));
	}
	else
	{
	    ChatLocus locus;
	    ChatUser user;
	    
	    if (source.length () == 0)
	    {
		locus = myIdentity;
		user = null;
	    }
	    else
	    {
		user = myIdentity.nickToUser (source);
		// BUG--add this when possible
		// user.setInfo (sourceInfo);

		char locChar = 
		    (args[0].length () > 0) ? args[0].charAt (0) : 'x';

		if ((locChar == '#') || (locChar == '&'))
		{
		    locus = myIdentity.nameToChannel (args[0]);
		}
		else
		{
		    locus = user;
		}
	    }

	    super.send (
		CommandMessage.makeMessage (locus, user, command, args));
	}
    }
}

