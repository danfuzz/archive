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

import com.milk.uberchat.iface.ChatIdentity;
import com.milk.uberchat.iface.ChatLocus;
import com.milk.uberchat.iface.ChatUser;
import com.milk.uberchat.event.MessageEvent;
import com.milk.util.ShouldntHappenException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

/**
 * This class is for "server reply" type messages, most of which are
 * simple info to pass back to a human, but some of which are actually
 * parseable.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
/*package*/ class ServerReply
{
    /** action code for unknown reply */
    static private final int UNKNOWN = -1;

    /** action code for ignoring the reply */
    static private final int IGNORE = -2;

    /** action code for turning a reply into a <code>systemPrivate</code>
     * event to the first channel argument found (or identity, if no
     * channel is found) */
    static private final int TO_CHANNEL = -3;

    /** action code for turning a reply into a <code>systemPrivate</code>
     * event to the locus found in <code>myArgs[1]</code> (or identity, if
     * the locus is invalid) */
    static private final int TO_LOCUS = -4;

    /** action code for turning a reply into a <code>systemPrivate</code>
     * event to the identity */
    static private final int TO_IDENTITY = -5;

    // all of these are handled by this class directly, unless marked with
    // "[other]" (handled in some other class) or "[not-handled]" (not
    // handled, which means that it defaults to outputting the reply, code
    // included, as a systemPrivate event to the identity). additionally,
    // the annotation "[ns]" indicates that the code is not part of the
    // spec (i.e. RFC-1459)
    static public final int RPL_WELCOME           =   1; // [ns]
    static public final int RPL_YOURHOST          =   2; // [ns]
    static public final int RPL_CREATED           =   3; // [ns]
    static public final int RPL_MYINFO            =   4; // [ns]
    static public final int RPL_TRACELINK         = 200; // [not-handled]
    static public final int RPL_TRACECONNECTING   = 201; // [not-handled]
    static public final int RPL_TRACEHANDSHAKE    = 202; // [not-handled]
    static public final int RPL_TRACEUNKNOWN      = 203; // [not-handled]
    static public final int RPL_TRACEOPERATOR     = 204; // [not-handled]
    static public final int RPL_TRACEUSER         = 205; // [not-handled]
    static public final int RPL_TRACESERVER       = 206; // [not-handled]
    static public final int RPL_TRACENEWTYPE      = 208; // [not-handled]
    static public final int RPL_STATSLINKINFO     = 211; // [not-handled]
    static public final int RPL_STATSCOMMANDS     = 212; // [not-handled]
    static public final int RPL_STATSCLINE        = 213; // [not-handled]
    static public final int RPL_STATSNLINE        = 214; // [not-handled]
    static public final int RPL_STATSILINE        = 215; // [not-handled]
    static public final int RPL_STATSKLINE        = 216; // [not-handled]
    static public final int RPL_STATSYLINE        = 218; // [not-handled]
    static public final int RPL_ENDOFSTATS        = 219; // [not-handled]
    static public final int RPL_UMODEIS           = 221; // [not-handled]
    static public final int RPL_STATSLINE         = 241; // [not-handled]
    static public final int RPL_STATSUPTIME       = 242; // [not-handled]
    static public final int RPL_STATSOLINE        = 243; // [not-handled]
    static public final int RPL_STATSHLINE        = 244; // [not-handled]
    static public final int RPL_TRACELOG          = 261; // [not-handled]
    static public final int RPL_LUSERCLIENT       = 251;
    static public final int RPL_LUSEROP           = 252;
    static public final int RPL_LUSERUNKNOWN      = 253;
    static public final int RPL_LUSERCHANNELS     = 254;
    static public final int RPL_LUSERME           = 255;
    static public final int RPL_ADMINME           = 256; // [not-handled]
    static public final int RPL_ADMINLOC1         = 257; // [not-handled]
    static public final int RPL_ADMINLOC2         = 258; // [not-handled]
    static public final int RPL_ADMINEMAIL        = 259; // [not-handled]
    static public final int RPL_LOCALUSERS        = 265; // [ns]
    static public final int RPL_GLOBALUSERS       = 266; // [ns]
    static public final int RPL_AWAY              = 301;
    static public final int RPL_USERHOST          = 302; // [not-handled]
    static public final int RPL_ISON              = 303; // [not-handled]
    static public final int RPL_UNAWAY            = 305; // [not-handled]
    static public final int RPL_NOWAWAY           = 306; // [not-handled]
    static public final int RPL_WHOISUSER         = 311; // [other]
    static public final int RPL_WHOISSERVER       = 312; // [other]
    static public final int RPL_WHOISOPERATOR     = 313; // [other]
    static public final int RPL_WHOWASUSER        = 314; // [not-handled]
    static public final int RPL_ENDOFWHO          = 315;
    static public final int RPL_WHOISIDLE         = 317; // [other]
    static public final int RPL_ENDOFWHOIS        = 318; // [other]
    static public final int RPL_WHOISCHANNELS     = 319; // [other]
    static public final int RPL_LISTSTART         = 321;
    static public final int RPL_LIST              = 322;
    static public final int RPL_LISTEND           = 323;
    static public final int RPL_CHANNELMODEIS     = 324; // [not-handled]
    static public final int RPL_NOTOPIC           = 331; // [not-handled]
    static public final int RPL_TOPIC             = 332;
    static public final int RPL_TOPICWHOTIME      = 333; // [ns]
    static public final int RPL_INVITING          = 341; // [not-handled]
    static public final int RPL_SUMMONING         = 342; // [not-handled]
    static public final int RPL_VERSION           = 351; // [not-handled]
    static public final int RPL_WHOREPLY          = 352;
    static public final int RPL_NAMREPLY          = 353;
    static public final int RPL_LINKS             = 364; // [not-handled]
    static public final int RPL_ENDOFLINKS        = 365; // [not-handled]
    static public final int RPL_ENDOFNAMES        = 366;
    static public final int RPL_BAMLIST           = 367; // [not-handled]
    static public final int RPL_ENDOFBANLIST      = 368; // [not-handled]
    static public final int RPL_ENDOFWHOWAS       = 369; // [not-handled]
    static public final int RPL_INFO              = 371; // [not-handled]
    static public final int RPL_MOTD              = 372;
    static public final int RPL_ENDOFINFO         = 374; // [not-handled]
    static public final int RPL_MOTDSTART         = 375;
    static public final int RPL_ENDOFMOTD         = 376;
    static public final int RPL_YOUREOPER         = 381; // [not-handled]
    static public final int RPL_REHASHING         = 382; // [not-handled]
    static public final int RPL_TIME              = 391; // [not-handled]
    static public final int RPL_USERSSTART        = 392; // [not-handled]
    static public final int RPL_USERS             = 393; // [not-handled]
    static public final int RPL_ENDOFUSERS        = 394; // [not-handled]
    static public final int RPL_NOUSERS           = 395; // [not-handled]
    static public final int ERR_NOSUCHNICK        = 401; 
    static public final int ERR_NOSUCHSERVER      = 402; // [not-handled]
    static public final int ERR_NOSUCHCHANNE      = 403; // [not-handled]
    static public final int ERR_CANNOTSENDTOCHAN  = 404; // [not-handled]
    static public final int ERR_TOOMANYCHANNELS   = 405;
    static public final int ERR_WASNOSUCHNICK     = 406; // [not-handled]
    static public final int ERR_TOOMANYTARGETS    = 407; // [not-handled]
    static public final int ERR_NOORIGIN          = 409; // [not-handled]
    static public final int ERR_NORECIPIENT       = 411; // [not-handled]
    static public final int ERR_NOTEXTTOSEND      = 412; // [not-handled]
    static public final int ERR_NOTOPLEVEL        = 413; // [not-handled]
    static public final int ERR_WILDTOPLEVEL      = 414; // [not-handled]
    static public final int ERR_UNKNOWNCOMMAND    = 421; // [not-handled]
    static public final int ERR_NOMOTD            = 422; // [not-handled]
    static public final int ERR_NOADMININFO       = 423; // [not-handled]
    static public final int ERR_FILEERROR         = 424; // [not-handled]
    static public final int ERR_NONICKNAMEGIVEN   = 431; // [not-handled]
    static public final int ERR_ERRONEUSNICKNAME  = 432; // [other]
    static public final int ERR_NICKNAMEINUSE     = 433; // [other]
    static public final int ERR_NICKCOLLISION     = 436; // [other]
    static public final int ERR_USERNOTINCHANNEL  = 441; // [not-handled]
    static public final int ERR_NOTONCHANNEL      = 442; // [not-handled]
    static public final int ERR_USERONCHANNEL     = 443; // [not-handled]
    static public final int ERR_NOLOGIN           = 444; // [not-handled]
    static public final int ERR_SUMMONDISABLED    = 445; // [not-handled]
    static public final int ERR_USERSDISABLED     = 446; // [not-handled]
    static public final int ERR_NOTREGISTERED     = 451; // [not-handled]
    static public final int ERR_NEEDMOREPARAMS    = 461; // [not-handled]
    static public final int ERR_ALREADYREGISTERED = 462; // [not-handled]
    static public final int ERR_NOPERMFORHOST     = 463; // [not-handled]
    static public final int ERR_PASSWDMISMATCH    = 464; // [other]
    static public final int ERR_YOUREBANNEDCREEP  = 465; // [other]
    static public final int ERR_KEYSET            = 467; // [not-handled]
    static public final int ERR_CHANNELISFULL     = 471;
    static public final int ERR_UNKNOWNMODE       = 472; // [not-handled]
    static public final int ERR_INVITEONLYCHAN    = 473;
    static public final int ERR_BANNEDFROMCHAN    = 474;
    static public final int ERR_BADCHANNELKEY     = 475;
    static public final int ERR_NOPRIVELEGES      = 481; // [not-handled]
    static public final int ERR_CHANOPRIVSNEEDED  = 482; // [not-handled]
    static public final int ERR_CANTKILLSERVER    = 483; // [not-handled]
    static public final int ERR_NOOPERHOST        = 491; // [not-handled]
    static public final int ERR_UMODEUNKNOWNFLAG  = 501; // [not-handled]
    static public final int ERR_USERSDONTMATCH    = 502; // [not-handled]

    /** table mapping reply codes to appropriate actions */
    static private Hashtable TheActions;

    /** the system */
    private IRCSystem mySystem;
    
    /** the reply code */
    private String myReplyCode;
    
    /** the args */
    private String[] myArgs;
    
    /** the reply code as an int (derived from <code>myReplyCode</code>) */
    private int myReplyInt;

    /** the action to take (derived from <code>myReplyCode</code>) */
    private int myAction;

    /** the identity (derived from <code>mySystem</code>) */
    private IRCIdentity myIdentity;

    /**
     * Create a <code>ServerReply</code>.
     *
     * @param system the system to use
     * @param replyCode the reply code as a string
     * @param args the arguments (including a first, target user element)
     */
    public ServerReply (IRCSystem system, String replyCode, String[] args)
    {
	mySystem = system;
	myReplyCode = replyCode;
	myArgs = args;
	myIdentity = system.getIdentity ();

	Integer actobj = (Integer) TheActions.get (replyCode);
	if (actobj != null)
	{
	    myAction = actobj.intValue ();
	}
	else
	{
	    myAction = UNKNOWN;
	}
	myReplyInt = myAction;
    }

    /**
     * Get the reply code.
     *
     * @return the reply code
     */
    public String getReplyCode ()
    {
	return myReplyCode;
    }

    /**
     * Get the reply code as an int.
     *
     * @return the reply code
     */
    public int getReplyInt ()
    {
	if (myReplyInt < 0)
	{
	    char c0 = myReplyCode.charAt (0);
	    char c1 = myReplyCode.charAt (1);
	    char c2 = myReplyCode.charAt (2);
	    myReplyInt = 
		((c0 - '0') * 100) + 
		((c1 - '0') * 10) +
		(c2 - '0');
	}

	return myReplyInt;
    }

    /**
     * Get the arguments of the reply.
     *
     * @return the arguments
     */
    public String[] getArguments ()
    {
	return myArgs;
    }

    /**
     * Handle the reply, doing whatever is necessary.
     * This is called inside <code>IRCInteractor</code> when it
     * has been handed one of these objects.
     */
    public void handleMessage ()
    {
	switch (myAction)
	{
	    case ERR_BADCHANNELKEY:   handleERR_BADCHANNELKEY ();  break;
	    case ERR_BANNEDFROMCHAN:  handleERR_BANNEDFROMCHAN (); break;
	    case ERR_CHANNELISFULL:   handleERR_CHANNELISFULL ();  break;
	    case ERR_INVITEONLYCHAN:  handleERR_INVITEONLYCHAN (); break;
	    case ERR_TOOMANYCHANNELS: handleERR_INVITEONLYCHAN (); break;
	    case RPL_AWAY:            handleRPL_AWAY ();           break;
	    case RPL_LIST:            handleRPL_LIST ();           break;
	    case RPL_NAMREPLY:        handleRPL_NAMREPLY ();       break;
	    case RPL_WHOREPLY:        handleRPL_WHOREPLY ();       break;
	    case RPL_TOPIC:           handleRPL_TOPIC ();          break;
	    case RPL_TOPICWHOTIME:    handleRPL_TOPICWHOTIME ();   break;
	    case IGNORE:              handleIGNORE ();             break;
	    case TO_CHANNEL:          handleTO_CHANNEL ();         break;
	    case TO_LOCUS:            handleTO_LOCUS ();           break;
	    case TO_IDENTITY:         handleTO_IDENTITY ();        break;
	    case UNKNOWN:             handleUNKNOWN ();            break;
	    default: 
	    {
		throw new ShouldntHappenException (
		    "Unhandled ServerReply: " + myReplyCode);
	    }
	}
    }

    /**
     * Turn the given int code into an interned string.
     *
     * @param code the code
     * @return the string form
     */
    public static String codeString (int code)
    {
	char[] digits = new char[3];
	digits[0] = (char) ('0' + (code / 100));
	digits[1] = (char) ('0' + ((code / 10) % 10));
	digits[2] = (char) ('0' + (code % 10));
	return new String (digits).intern ();
    }

    // ------------------------------------------------------------------------
    // Private helper methods

    /**
     * Helper class used for doing the table mappings.
     */
    private static class Mapper
    {
	private char[] digits = new char[3];
	
	public void put (int code, Integer action)
	{
	    digits[0] = (char) ('0' + (code / 100));
	    digits[1] = (char) ('0' + ((code / 10) % 10));
	    digits[2] = (char) ('0' + (code % 10));
	    TheActions.put (new String (digits).intern (), action);
	}
	
	public void put (int code)
	{
	    put (code, new Integer (code));
	}
    }

    /**
     * Build up the table mapping codes to actions.
     */
    static
    {
	TheActions = new Hashtable ();

	Mapper mapper = new Mapper ();
	Integer toChannel = new Integer (TO_CHANNEL);
	Integer toLocus = new Integer (TO_LOCUS);
	Integer toIdentity = new Integer (TO_IDENTITY);
	Integer ignore = new Integer (IGNORE);

	mapper.put (ERR_BADCHANNELKEY);
	mapper.put (ERR_BANNEDFROMCHAN);
	mapper.put (ERR_CHANNELISFULL);
	mapper.put (ERR_INVITEONLYCHAN);
	mapper.put (ERR_NOSUCHNICK,    toLocus);
	mapper.put (ERR_TOOMANYCHANNELS);
	mapper.put (RPL_AWAY);
	mapper.put (RPL_CREATED,       toIdentity);
	mapper.put (RPL_ENDOFMOTD,     ignore);
	mapper.put (RPL_ENDOFNAMES,    ignore);
	mapper.put (RPL_ENDOFWHO,      ignore);
	mapper.put (RPL_GLOBALUSERS,   toIdentity);
	mapper.put (RPL_LIST);
	mapper.put (RPL_LISTEND,       ignore);
	mapper.put (RPL_LISTSTART,     ignore);
	mapper.put (RPL_LOCALUSERS,    toIdentity);
	mapper.put (RPL_LUSERCHANNELS, toIdentity);
	mapper.put (RPL_LUSERCLIENT,   toIdentity);
	mapper.put (RPL_LUSERME,       toIdentity);
	mapper.put (RPL_LUSEROP,       toIdentity);
	mapper.put (RPL_LUSERUNKNOWN,  toIdentity);
	mapper.put (RPL_MOTD,          toIdentity);
	mapper.put (RPL_MOTDSTART,     toIdentity);
	mapper.put (RPL_MYINFO,        toIdentity);
	mapper.put (RPL_NAMREPLY);
	mapper.put (RPL_TOPIC);
	mapper.put (RPL_TOPICWHOTIME);
	mapper.put (RPL_WELCOME,       toIdentity);
	mapper.put (RPL_WHOREPLY);
	mapper.put (RPL_YOURHOST,      toIdentity);
    }

    /**
     * Handle this reply as an IGNORE.
     */
    private void handleIGNORE ()
    {
	// BUG--should have option to print out otherwise-ignored stuff
    }

    /**
     * Handle this reply as a TO_CHANNEL.
     */
    private void handleTO_CHANNEL ()
    {
	ChatLocus dest = null;
	StringBuffer sb = new StringBuffer ();
	for (int i = 1; i < myArgs.length; i++)
	{
	    String arg = myArgs[i];
	    if (   (dest == null)
		&& (arg.length () != 0)
		&& ((arg.charAt (0) == '#') || (arg.charAt (0) == '&')))
	    {
		dest = myIdentity.nameToChannel (arg);
	    }

	    if (i != 1)
	    {
		sb.append (' ');
	    }
	    sb.append (arg);
	}

	if (dest == null)
	{
	    dest = myIdentity;
	}

	mySystem.dispatchBroadcast (
            MessageEvent.systemPrivate (dest, sb.toString ()));
    }

    /**
     * Handle this reply as a TO_LOCUS. The locus must be in
     * <code>myArgs[1]</code>.
     */
    private void handleTO_LOCUS ()
    {
	if (myArgs.length < 2)
	{
	    handleUNKNOWN ();
	    return;
	}

	ChatLocus dest = null;
	char destChar = myArgs[1].charAt (0);

	if ((destChar == '#') || (destChar == '&'))
	{
	    dest = myIdentity.nameToChannel (myArgs[1]);
	}
	else
	{
	    dest = myIdentity.nameToUser (myArgs[1]);
	}

	StringBuffer sb = new StringBuffer ();
	if (dest == null)
	{
	    dest = myIdentity;
	    sb.append (myArgs[1]);
	    sb.append (": ");
	}

	for (int i = 2; i < myArgs.length; i++)
	{
	    if (i != 2)
	    {
		sb.append (' ');
	    }
	    sb.append (myArgs[i]);
	}

	mySystem.dispatchBroadcast (
            MessageEvent.systemPrivate (dest, sb.toString ()));
    }

    /**
     * Handle this reply as a TO_IDENTITY.
     */
    private void handleTO_IDENTITY ()
    {
	StringBuffer sb = new StringBuffer ();
	for (int i = 1; i < myArgs.length; i++)
	{
	    if (i != 1)
	    {
		sb.append (' ');
	    }
	    sb.append (myArgs[i]);
	}

	myIdentity.systemPrivate (sb.toString ());
    }

    /**
     * Handle this reply as an UNKNOWN.
     */
    private void handleUNKNOWN ()
    {
	unknownTo (null);
    }

    /**
     * Handle this reply as an UNKNOWN, sending it to the given locus.
     * If the locus is null, it looks for a channel in the message, and
     * if it finds one, sends to that, otherwise it goes to the identity
     * locus.
     * 
     * @param dest null-ok; the destination locus
     */
    private void unknownTo (ChatLocus dest)
    {
	StringBuffer sb = new StringBuffer ();
	sb.append (myReplyCode);
	sb.append (": ");
	for (int i = 1; i < myArgs.length; i++)
	{
	    String arg = myArgs[i];
	    if (   (dest == null)
		&& (arg.length () != 0)
		&& ((arg.charAt (0) == '#') || (arg.charAt (0) == '&')))
	    {
		dest = myIdentity.nameToChannel (arg);
	    }

	    if (i != 1)
	    {
		sb.append ("; ");
	    }
	    sb.append (arg);
	}

	if (dest == null)
	{
	    dest = myIdentity;
	}

	mySystem.dispatchBroadcast (
            MessageEvent.systemPrivate (dest, sb.toString ()));
    }

    /** 
     * Send a <code>systemPrivate</code> event to the channel indicated
     * by <code>myArgs[1]</code>, and optionally leave the channel.
     *
     * @param msg the message text
     * @param leave if true, leave the channel
     */
    private void channelError (String msg, boolean leave)
    {
	IRCChannel chan = (IRCChannel) myIdentity.nameToChannel (myArgs[1]);
	if (chan == null)
	{
	    handleUNKNOWN ();
	    return;
	}

	StringBuffer sb = new StringBuffer ();
	sb.append (msg);
	if ((myArgs.length == 3) && (myArgs[2].length () != 0))
	{
	    sb.append (": ");
	    sb.append (myArgs[2]);
	}
	else
	{
	    sb.append ('.');
	}

	chan.systemPrivate (sb.toString ());
	if (leave)
	{
	    chan.callLeftChannel ();
	}
    }
 
    // ------------------------------------------------------------------------
    // All of the following are handlers for specific reply codes

    /**
     * Handle this as an ERR_CHANNELISFULL.
     */
    private void handleERR_CHANNELISFULL ()
    {
	channelError ("You cannot join because the channel is full", true);
    }	

    /**
     * Handle this as an ERR_INVITEONLYCHAN.
     */
    private void handleERR_INVITEONLYCHAN ()
    {
	channelError ("You cannot join because the channel is invite-only", 
		      true);
    }	

    /**
     * Handle this as an ERR_TOOMANYCHANNELS.
     */
    private void handleERR_TOOMANYCHANNELS ()
    {
	channelError (
            "You cannot join because you are already on too many channels", 
	    true);
    }	

    /**
     * Handle this as an ERR_BANNEDFROMCHAN.
     */
    private void handleERR_BANNEDFROMCHAN ()
    {
	channelError (
            "You cannot join because you have been banned from the channel", 
	    true);
    }	

    /**
     * Handle this as an ERR_BADCHANNELKEY.
     */
    private void handleERR_BADCHANNELKEY ()
    {
	channelError (
            "You cannot join because you specified a bad channel key",
	    true);
    }	

    /**
     * Handle this as an RPL_AWAY.
     */
    private void handleRPL_AWAY ()
    {
	ChatUser user = myIdentity.nickToUser (myArgs[1]);
	if (user == null)
	{
	    handleUNKNOWN ();
	    return;
	}

	StringBuffer sb = new StringBuffer ();
	sb.append ("User ");
	sb.append (user.getName ());
	sb.append (" is away");
	if (myArgs[2].length () != 0)
	{
	    sb.append (": ");
	    sb.append (myArgs[2]);
	}
	else
	{
	    sb.append ('.');
	}

	((IRCUser) user).systemPrivate (sb.toString ());
    }

    /**
     * Handle this reply as a RPL_NAMREPLY.
     */
    private void handleRPL_NAMREPLY ()
    {
	IRCChannel chan = null;
	Vector users = new Vector ();
	for (int i = 1; i < myArgs.length; i++)
	{
	    String arg = myArgs[i];
	    int len = arg.length ();
	    if (len == 0)
	    {
		// shouldn't happen, but just in case
		continue;
	    }

	    char c = arg.charAt (0);
	    if (   (len == 1) 
		&& (i == 1) 
		&& ((c == '*') || (c == '@') || (c == '=')))
	    {
		// ignore channel type if it's there
		continue;
	    }

	    if ((len == 1) && (c == '*'))
	    {
		// ignore "no actual channel" indicator
		continue;
	    }

	    if ((c == '#') || (c == '&'))
	    {
		chan = (IRCChannel) myIdentity.nameToChannel (arg);
	    }
	    else
	    {
		for (int at = 0; at < len; at++)
		{
		    c = arg.charAt (at);
		    if ((c == ' ') || (c == '@') || (c == '+'))
		    {
			continue;
		    }
		    int nameAt = at;
		    at++;
		    while ((at < len) && (arg.charAt (at) != ' '))
		    {
			at++;
		    }
		    ChatUser user = 
			myIdentity.nickToUser (arg.substring (nameAt, at));
		    if (user != null)
		    {
			users.addElement (user);
		    }
		}
	    }
	}

	if (chan != null)
	{
	    ChatUser[] uarr = new ChatUser[users.size ()];
	    users.copyInto (uarr);
	    chan.callSetUsers (uarr);
	}
	else
	{
	    // it was just a listing of some other users that are online
	    // but not in channels we know; kill off all other channel-free
	    // users, and make sure these guys are listed
	    ChatUser[] known = myIdentity.getKnownUsers ();
	    for (int i = 0; i < known.length; i++)
	    {
		IRCUser u = (IRCUser) known[i];
		if (users.contains (u))
		{
		    // it was already known; all's well
		    users.removeElement (u);
		    u.callSetChannels (null);
		}
		else if (u.getKnownChannelCount () == 0)
		{
		    // it's a user that logged off
		    myIdentity.callRemoveUser (u);
		}
	    }
	    // the rest of the users variable need to be added
	    for (int i = users.size () - 1; i >= 0; i--)
	    {
		IRCUser u = (IRCUser) users.elementAt (i);
		myIdentity.callAddUser (u);
		u.callSetChannels (null);
	    }
	}
    }

    /**
     * Handle this reply as a RPL_LIST.
     */
    private void handleRPL_LIST ()
    {
	// we just do this to send to the identity, instead of the default,
	// which would send each reply line to a different locus! however,
	// given that the system should normally just swallow these as part
	// of its internal handler code, if one of these makes it all the
	// way out to a user, it should have the form of a normal "unknown"
	// reply type
	unknownTo (myIdentity);
    }

    /**
     * Handle this reply as a RPL_WHOREPLY.
     */
    private void handleRPL_WHOREPLY ()
    {
	// we just do this to send to the identity, instead of the default,
	// which would send each reply line to a different locus! however,
	// given that the system should normally just swallow these as part
	// of its internal handler code, if one of these makes it all the
	// way out to a user, it should have the form of a normal "unknown"
	// reply type
	unknownTo (myIdentity);
    }

    /**
     * Handle this as a RPL_TOPIC.
     */
    private void handleRPL_TOPIC ()
    {
	IRCChannel chan = (IRCChannel) myIdentity.nameToChannel (myArgs[1]);

	if (chan == null)
	{
	    handleUNKNOWN ();
	    return;
	}

	StringBuffer sb = new StringBuffer ();
	sb.append ("The topic is: ");
	sb.append (myArgs[2]);
	mySystem.dispatchBroadcast (
            MessageEvent.systemPrivate (chan, sb.toString ()));

	chan.callTopicChanged (myArgs[2]);
    }

    /**
     * Handle this as a RPL_TOPICWHOTIME.
     */
    private void handleRPL_TOPICWHOTIME ()
    {
	IRCChannel chan = (IRCChannel) myIdentity.nameToChannel (myArgs[1]);

	if (chan == null)
	{
	    handleUNKNOWN ();
	    return;
	}

	StringBuffer sb = new StringBuffer ();
	sb.append ("User ");
	sb.append (myArgs[2]);
	sb.append (" set the topic");

 	try
	{
	    long when = Long.parseLong (myArgs[3]);
	    sb.append (" at ");
	    DateFormat df = DateFormat.getDateTimeInstance ();
	    sb.append (df.format (new Date (when * 1000)));
	}
	catch (NumberFormatException ex)
	{
	    // ignore it
	}

	sb.append (".");

	mySystem.dispatchBroadcast (
            MessageEvent.systemPrivate (chan, sb.toString ()));
    }
}
