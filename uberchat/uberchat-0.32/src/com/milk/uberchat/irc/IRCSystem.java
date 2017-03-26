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

import com.milk.asynch.LineFilter;
import com.milk.asynch.ReaderSender;
import com.milk.asynch.Resender;
import com.milk.asynch.Sender;
import com.milk.uberchat.SpeechKinds;
import com.milk.uberchat.base.BaseSystem;
import com.milk.uberchat.event.ErrorEvent;
import com.milk.uberchat.event.MessageEvent;
import com.milk.uberchat.iface.ChatLocus;
import com.milk.util.BaseEvent;
import com.milk.util.ShouldntHappenException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * This is the IRC-specific <code>ChatSystem</code>. It tries to stick
 * pretty closely to the spec (i.e., RFC-1459), but has a couple of
 * bits to make it a little more forgiving of noncompliance.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
public class IRCSystem
extends BaseSystem
{
    /** version string to identify us */
    static public final String VERSION_STRING = 
	"UberChat IRC Adaptor 0.28. Praise Bob!";

    /** speech kind for NOTICE messages */
    static public final String NOTICE = "notice";

    /** the template object */
    private IRCSystemTemplate myTemplate;

    /** the identity for the system--IRC only allows one active identity
     * per connection */
    private IRCIdentity myIdentity;

    /** the current interactor object */
    private IRCInteractor myInteractor;

    /** the current input front-end */
    private ReaderSender myReaderSender;

    /** the current socket to use for IO */
    private Socket mySocket;

    /**
     * Construct an <code>IRCSystem</code> from the given template.
     *
     * @param template the template
     */
    public IRCSystem (IRCSystemTemplate template)
    {
	super (template.myName);
	myTemplate = (IRCSystemTemplate) template.copy ();
	setTemplate (myTemplate);

	myIdentity = new IRCIdentity (this);
	myInteractor = null;
	mySocket = null;
    }

    // ------------------------------------------------------------------------
    // ChatSystem methods

    /**
     * Turn the given user name into its canonical form. For IRC, this
     * makes sure the name is of proper form, and lower-cases and interns
     * it.
     *
     * @param orig the original name
     * @return the canonical form 
     */
    public String canonicalUserName (String orig)
    {
	int len = orig.length ();
	if (len == 0)
	{
	    return null;
	}
	char c = orig.charAt (0);
	if ((c == '#') || (c == '&'))
	{
	    return null;
	}

	return orig.toLowerCase ().intern ();
    }

    /**
     * Turn the given channel name into its canonical form. For IRC, this
     * makes sure that the name is of proper form (starts with '#' or '&'),
     * and lower-cases and interns it.
     *
     * @param orig the original name
     * @return the canonical form
     */
    public String canonicalChannelName (String orig)
    {
	int len = orig.length ();
	if (len < 2)
	{
	    return null;
	}
	char c = orig.charAt (0);
	if ((c != '#') && (c != '&'))
	{
	    return null;
	}

	return orig.toLowerCase ().intern ();
    }

    // ------------------------------------------------------------------------
    // Required protected methods

    /**
     * <code>BaseSystem</code> calls this method when it actually wants a
     * connection to happen. It will only call this if the system is not
     * actually connected. 
     */
    protected void systemConnect ()
    {
	// we do everything in a thread so that the ui (or whatever)
	// that called us won't get all stalled
	Thread t = new Thread ()
	    {
		public void run ()
		{
		    doConnect ();
		}
	    };
	t.setDaemon (true);
	t.start ();
    }

    /**
     * <code>BaseSystem</code> calls this method when it actually wants to
     * disconnect the system. It will only call this if the system is
     * actually connected. Our interactor will call this too, if the server
     * tells it to disconnect. 
     */
    protected void systemDisconnect ()
    {
	if (myInteractor != null)
	{
	    myInteractor.dontComplainAboutEOF ();
	    sendCommand ("quit", new String[] { "Hail Satan!" });
	}
	else
	{
	    systemDisconnected ();
	}
    }
    
    // ------------------------------------------------------------------------
    // Package methods

    /**
     * Get the unique identity for this system.
     *
     * @return the identity
     */
    /*package*/ IRCIdentity getIdentity ()
    {
	return myIdentity;
    }

    /**
     * Get the userid for this system.
     *
     * @return the userid
     */
    /*package*/ String getUserid ()
    {
	return myTemplate.myUserid;
    }

    /**
     * Get the verbose CTCP flag.
     *
     * @return the verbose CTCP flag.
     */
    /*package*/ boolean getVerboseCtcp ()
    {
	return myTemplate.myVerboseCtcp;
    }

    /**
     * Get the auto-join flag.
     *
     * @return the auto-join flag.
     */
    /*package*/ boolean getAutoJoin ()
    {
	return myTemplate.myAutoJoin;
    }

    /**
     * Send an <code>errorReport</code> event to whoever is listening
     * to this system.
     *
     * @param msg the error message text
     */
    /*package*/ void errorReport (String msg)
    {
	broadcast (ErrorEvent.errorReport (this, msg));
    }

    /**
     * Send a <code>bugReport</code> event to whoever is listening
     * to this system.
     *
     * @param ex the exception to pass
     */
    /*package*/ void bugReport (Throwable ex)
    {
	broadcast (ErrorEvent.bugReport (this, ex));
    }

    /**
     * Send a command to the host system.
     *
     * @param name the command name
     * @param params the parameters
     */
    /*package*/ void sendCommand (String name, String[] params)
    {
	if (myInteractor == null)
	{
	    errorReport ("System is not connected.");
	    return;
	}

	myInteractor.doSendCommand (name, params);
    }	

    /**
     * Send a CTCP command to the host system.
     *
     * @param dest the destination name
     * @param name the command name
     * @param text the command text
     * @param normal normal (true) or notice (false)
     */
    /*package*/ void sendCtcp (String dest, String name, String text,
			       boolean normal)
    {
	if (myInteractor == null)
	{
	    errorReport ("System is not connected.");
	    return;
	}

	myInteractor.doSendCtcp (dest, name, text, normal);
    }	

    /**
     * Send a command and tap the given reply codes, sending them to the
     * given tap. When the tap returns true, then the tap is considered
     * done. Alternatively, if the interactor decides the tap is done
     * (because of an error or some other weirdness), then it sends a
     * null to the tap and then stops sending anything else.
     * 
     * @param name the command name
     * @param params the parameters
     * @param codes the reply codes to tap
     * @param tap the object to send the replies to 
     */
    /*package*/ void commandTap (String name, String[] params,
				 int[] codes,  ReplyTap tap)
    {
	if (myInteractor == null)
	{
	    errorReport ("System is not connected.");
	    return;
	}

	myInteractor.doCommandTap (name, params, codes, tap);
    }

    /**
     * Do a raw send to the host system.
     *
     * @param raw the string to send
     */
    /*package*/ void rawSend (String raw)
    {
	if (myInteractor == null)
	{
	    errorReport ("System is not connected.");
	    return;
	}

	if (! raw.endsWith ("\n"))
	{
	    raw += '\n';
	}

	myInteractor.doRawSend (raw);
    }

    /**
     * Handle all the details of speaking to a particular locus.
     *
     * @param locus the locus to speak to
     * @param kind the speech kind, including ctcp types
     * @param text the text to speak
     */
    /*package*/ void speakTo (ChatLocus locus, String kind, String text)
    {
	if (   (kind.equals (SpeechKinds.RAW))
	    || (kind.equals (SpeechKinds.RAW_NO_NL)))
	{
	    rawSend (text);
	    return;
	}

	while (text != null)
	{
	    int nlAt = text.indexOf ('\n');
	    String line;
	    if (nlAt != -1)
	    {
		line = text.substring (0, nlAt);
		text = text.substring (nlAt + 1);
		if (text.length () == 0)
		{
		    text = null;
		}
	    }
	    else
	    {
		line = text;
		text = null;
	    }

	    // do the echoback for the user
	    dispatchBroadcast (
	        MessageEvent.userBroadcast (locus, 
					    myIdentity.getIdentityUser (),
					    kind, line));

	    String destName =
		(locus instanceof IRCUser)
		? ((IRCUser) locus).getIRCUserid ()
		: locus.getName ();

	    if (kind.equals (SpeechKinds.SAYS))
	    {
		if (line.length () == 0)
		{
		    line = " ";
		}
		sendCommand ("privmsg", new String[] { destName, line });
	    }
	    else if (kind.equals (NOTICE))
	    {
		if (line.length () == 0)
		{
		    line = " ";
		}
		sendCommand ("notice", new String[] { destName, line });
	    }
	    else if (kind.startsWith ("ctcp-reply-"))
	    {
		String kindSend = kind.substring (11).toUpperCase ();
		sendCtcp (destName, kindSend, line, false);
	    }
	    else if (kind.startsWith ("ctcp-"))
	    {
		String kindSend = kind.substring (5).toUpperCase ();
		sendCtcp (destName, kindSend, line, true);
	    }
	    else
	    {
		if (kind.equals (SpeechKinds.ME))
		{
		    // do nothing
		}
		else if (kind.equals (SpeechKinds.MY))
		{
		    line = "'s " + line;
		}
		else
		{
		    line = kind + ": " + line;
		}

		sendCtcp (destName, "ACTION", line, true);
	    }
	}
    }

    /**
     * Tell the system that it's now disconnected.
     */
    /*package*/ void callSystemDisconnected ()
    {
	if (myReaderSender != null)
	{
	    myReaderSender.stopSending ();
	
	    try
	    {
		mySocket.close ();
	    }
	    catch (IOException ex)
	    {
		errorReport ("Trouble closing the connection:\n" +
			     ex.getMessage ());
	    }

	    myInteractor.doShutdown ();
	    myInteractor = null;
	    myReaderSender = null;
	    mySocket = null;
	}

	if (getConnectionState () != DISCONNECTED)
	{
	    systemDisconnected ();
	}
    }

    /**
     * Call <code>BaseEntity.broadcast()</code> on an event. This is
     * defined to give this package access to an otherwise-protected
     * method.
     *
     * @param ev the event to broadcast 
     */
    /*package*/ void callBroadcast (BaseEvent ev)
    {
	broadcast (ev);
    }

    /**
     * Cast the source of the given event and call the appropriate
     * <code>callBroadcast()</code> method. This is how one can generically
     * create and send out events without allowing the outside world to
     * have the same power.
     *
     * @param ev the event to broadcast
     */
    /*package*/ void dispatchBroadcast (BaseEvent ev)
    {
	Object source = ev.getSource ();
	if (source instanceof IRCChannel)
	{
	    ((IRCChannel) source).callBroadcast (ev);
	}
	else if (source instanceof IRCIdentity)
	{
	    ((IRCIdentity) source).callBroadcast (ev);
	}
	else if (source instanceof IRCSystem)
	{
	    ((IRCSystem) source).callBroadcast (ev);
	}
	else if (source instanceof IRCUser)
	{
	    ((IRCUser) source).callBroadcast (ev);
	}
	else
	{
	    throw new ShouldntHappenException (
                "dispatchBroadcast() called with bad-source event:\n" + ev);
	}
    }

    // ------------------------------------------------------------------------
    // Private helper methods

    /**
     * This method gets called in a thread when a
     * <code>systemConnect</code> request gets made. 
     */
    private void doConnect ()
    {
	InputStreamReader reader;
	OutputStreamWriter writer;

	try
	{
	    mySocket = new Socket (myTemplate.myHost, myTemplate.myPort);
	    reader = new InputStreamReader (mySocket.getInputStream ());
	    writer = new OutputStreamWriter (mySocket.getOutputStream ());
	}
	catch (IOException ex)
	{
	    mySocket = null;
	    systemDisconnected ();
            errorReport ("Unable to open socket for connection:\n" +
			 ex.getMessage ());
	    return;
	}

	Resender inputSink = new Resender ();

	Sender target = new IRCInputFilter (this, inputSink);
	LineFilter filt = new LineFilter (target);
	myReaderSender = new ReaderSender (reader, filt);

	myInteractor = new IRCInteractor (this, inputSink, writer);

	if (   (myTemplate.myPassword != null)
	    && (myTemplate.myPassword.length () != 0))
	{
	    sendCommand ("pass", new String[] { myTemplate.myPassword });
	}
		    
	String[] args = new String[] { myTemplate.myUserName, 
				       "-", 
				       "-", 
				       myTemplate.myRealName };
	if (args[0].length () == 0)
	{
	    args[0] = myTemplate.myUserid;
	}
	if (args[3].length () == 0)
	{
	    args[3] = args[0];
	}
	sendCommand ("user", args);

	ReplyTap tap = new ReplyTap ()
	{
	    public boolean handleReply (ServerReply reply)
	    {
		int code = (reply != null) 
		    ? reply.getReplyInt () 
		    : 0;

		if (code == ServerReply.RPL_WELCOME)
		{
		    reply.handleMessage ();
		    callSystemConnectedEtc ();
		}
		else
		{
		    String msg;
		    switch (code)
		    {
			case ServerReply.ERR_ERRONEUSNICKNAME:
			{
			    msg = "Nickname has illegal characters.";
			    break;
			}
			case ServerReply.ERR_NICKNAMEINUSE:
			case ServerReply.ERR_NICKCOLLISION:
			{
			    msg = "Nickname is in use.";
			    break;
			}
			case ServerReply.ERR_PASSWDMISMATCH:
			{
			    msg = "Bad / missing password.";
			    break;
			}
			case ServerReply.ERR_YOUREBANNEDCREEP:
			{
			    msg = "You are banned from this server (creep).";
			    break;
			}
			default:
			{
			    msg = "Unknown error / unexpectedly disconnected.";
			    break;
			}
		    }
		    errorReport ("Could not connect because:\n" + msg);
		    systemDisconnect ();
		}

		return true;
	    }
	};

	commandTap ("nick", 
		    new String[] { myTemplate.myUserid },
		    new int[] { ServerReply.RPL_WELCOME,
				ServerReply.ERR_ERRONEUSNICKNAME,
				ServerReply.ERR_NICKNAMEINUSE,
				ServerReply.ERR_NICKCOLLISION,
				ServerReply.ERR_PASSWDMISMATCH,
				ServerReply.ERR_YOUREBANNEDCREEP, },
		    tap);
    }

    /**
     * Do all the final setup when we finally make a successful connection
     * to a system.
     */
    private void callSystemConnectedEtc ()
    {
	systemConnected ();
	((IRCUser) myIdentity.getIdentityUser ()).
	    setLastKnownNickname (myTemplate.myUserid);
	addIdentity (myIdentity);
    }
}
