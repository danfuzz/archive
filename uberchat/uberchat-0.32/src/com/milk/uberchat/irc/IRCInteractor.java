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

import com.milk.asynch.FifoQueue;
import com.milk.asynch.MailBox;
import com.milk.asynch.Resender;
import com.milk.uberchat.event.ErrorEvent;
import com.milk.uberchat.event.MessageEvent;
import com.milk.util.ShouldntHappenException;
import java.io.EOFException;
import java.io.IOException;
import java.io.Writer;
import java.util.Hashtable;
import java.util.Vector;

/**
 * This class does all the interaction with an IRC server. It takes input
 * in the form of filtered messages (filtered through an IRCInputFilter)
 * and explicit calls to do stuff. It sends stuff back to the server
 * depending on what it's told to do, and messages etc. along to an
 * IRCSystem depending on what gets received from the connection.
 * BUG--when you disconnect, the interactor thread is left around.
 * This is bad.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
/*package*/ class IRCInteractor
{
    /** the system to use */
    private IRCSystem mySystem;

    /** where to send output to go back to the host */
    private Writer myOutput;

    /** the identity to use */
    private IRCIdentity myIdentity;

    /** where to get input from (either from host or commands, etc.) */
    private FifoQueue myInput;

    /** current list of pending commands */
    private Vector myCommands;

    /** the thread that runs our loop */
    private Thread myThread;

    /** true if we've been disconnected */
    private boolean myIsDisconnected;

    /** true if EOF exception is okay (i.e., the user wants to disconnect) */
    private boolean myEOFIsOkay;

    /** the object to send to indicate a shutdown */
    private Object myShutdownMessage;

    /**
     * Construct an <code>IRCInteractor</code>.
     *
     * @param system the system to use
     * @param input the filtered stream of input from the server
     * @param output the raw output stream
     */
    public IRCInteractor (IRCSystem system, Resender input, Writer output)
    {
	mySystem = system;
	myOutput = output;

	myIdentity = system.getIdentity ();
	myCommands = new Vector ();
	myIsDisconnected = false;
	myEOFIsOkay = false;

	myInput = new FifoQueue ();
	input.sendTo (myInput);
	myShutdownMessage = myInput; // good enough for government work

	myThread = new Thread () 
	    {
		public void run ()
		{
		    threadRun ();
		}
	    };
	myThread.setDaemon (true);
	myThread.start ();
    }

    /**
     * Tell the interactor that EOF on the input is okay, i.e., the
     * user asked to close this connection.
     */
    public void dontComplainAboutEOF ()
    {
	if (myIsDisconnected)
	{
	    return;
	}

	myInput.send (new Runnable ()
	{
	    public void run ()
	    {
		myEOFIsOkay = true;
	    }
	});
    }

    /**
     * Tell the interactor to send a command to the host system.
     *
     * @param name the command name
     * @param params the parameters
     */
    public void doSendCommand (final String name, final String[] params)
    {
	if (myIsDisconnected)
	{
	    return;
	}

	myInput.send (new Runnable ()
	{
	    public void run ()
	    {
		sendCommand (name, params);
	    }
	});
    }	

    /**
     * Tell the interactor to send a CTCP command to the host system.
     *
     * @param dest the destination
     * @param name the command name
     * @param text the text
     * @param normal normal (true) or notice (false)
     */
    public void doSendCtcp (final String dest, final String name,
			    final String text, final boolean normal)
    {
	if (myIsDisconnected)
	{
	    return;
	}

	myInput.send (new Runnable ()
	{
	    public void run ()
	    {
		sendCtcp (dest, name, text, normal);
	    }
	});
    }	

    /**
     * Tell the interactor to do a raw send
     *
     * @param raw the raw string to send
     */
    public void doRawSend (final String raw)
    {
	if (myIsDisconnected)
	{
	    return;
	}

	myInput.send (new Runnable ()
	{
	    public void run ()
	    {
		rawSend (raw);
	    }
	});
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
    public void doCommandTap (final String name, final String[] params,
			      final int[] codes, final ReplyTap tap)
    {
	if (myIsDisconnected)
	{
	    return;
	}

	myInput.send (new Runnable ()
	{
	    public void run ()
	    {
		commandTap (name, params, codes, tap);
	    }
	});
    }

    /**
     * Tell the interactor to cease all activity.
     */
    public void doShutdown ()
    {
	if (myIsDisconnected)
	{
	    return;
	}

	myInput.send (myShutdownMessage);
    }

    // ------------------------------------------------------------------------
    // Private helper methods

    /**
     * This is the main loop of the thread that deals with the connection.
     */
    private void threadRun ()
    {
	while (! myIsDisconnected)
	{
	    try
	    {
		while (myCommands.size () != 0)
		{
		    Runnable command = (Runnable) myCommands.firstElement ();
		    myCommands.removeElementAt (0);
		    command.run ();
		}
		receiveMessage ();
	    }
	    catch (ShouldntHappenException ex)
	    {
		mySystem.bugReport (ex);
	    }
	    catch (Exception ex)
	    {
		mySystem.bugReport (
		    new ShouldntHappenException (
                        "Error caught at top of IRC interactor loop.",
			ex));
	    }
	}
    }

    /**
     * This is called when this object wants to let the system know that
     * the connection has been or should be disconnected.
     *
     * @param errorMsg null-ok; if non-null, an error message to report
     * to the user
     */
    private void beDisconnected (String errorMsg)
    {
	myIsDisconnected = true;
	mySystem.callSystemDisconnected ();
	if (errorMsg != null)
	{
	    mySystem.errorReport (errorMsg);
	}
    }

    /**
     * This will receive a message from the input, handling everything
     * appropriately, except for <code>ServerReply</code> objects, which it
     * returns without any processing. Handling appropriately includes
     * adding <code>Runnable</code>s to the command queue and sending the
     * system speech (and other host activity) messages. If it receives and
     * handles a message, it returns null.
     *
     * @return null-ok; the received <code>ServerReply</code>, if any 
     */
    private ServerReply receiveServerReply ()
    {
	Object message = myInput.receive ();
	if (message instanceof ServerReply)
	{
	    return ((ServerReply) message);
	}

	if (message == myShutdownMessage)
	{
	    myIsDisconnected = true;
	}
	else if (message instanceof Runnable)
	{
	    myCommands.addElement (message);
	}
	else if (message instanceof MessageEvent)
	{
	    mySystem.dispatchBroadcast ((MessageEvent) message);
	}
	else if (message instanceof CommandMessage)
	{
	    ((CommandMessage) message).handleMessage ();
	}
	else if (message instanceof CtcpMessage)
	{
	    ((CtcpMessage) message).handleMessage ();
	}
	else if (message instanceof Throwable)
	{
	    // a throwable made its way out of the read stream
	    // we take this to mean the connection died
	    if (myEOFIsOkay && (message instanceof EOFException))
	    {
		beDisconnected (null);
	    }
	    else
	    {
		beDisconnected (
                    "Connection closed unexpectedly with error:\n" +
		    ((Throwable) message).getMessage ());
	    }
	}
	else
	{
	    mySystem.bugReport (
                new ShouldntHappenException (
                    "IRC interactor got weird message:\n" + message));
	}

	return null;
    }

    /**
     * This will receive a message from the input and handle it
     * appropriately. This does everything that
     * <code>receiveServerReply()</code> does, plus it will tell
     * all <code>ServerReply</code>s to handle themselves. 
     */
    private void receiveMessage ()
    {
	ServerReply message = receiveServerReply ();

	if (message != null)
	{
	    message.handleMessage ();
	}
    }

    /**
     * Send the given string to the host system.
     *
     * @param str the string to send
     */
    private void rawSend (String str)
    {
	if (myIsDisconnected)
	{
	    return;
	}

	try
	{
	    myOutput.write (str);
	    myOutput.flush ();
	}
	catch (IOException ex)
	{
	    beDisconnected ("Trouble sending to connection:\n" +
			    ex.getMessage ());
	}
    }

    /**
     * Send a CTCP command to the host system.
     *
     * @param dest the destination
     * @param name the command name
     * @param text the command text
     * @param normal normal (true) or notice (false)
     */
    private void sendCtcp (String dest, String name, String text, 
			   boolean normal)
    {
	StringBuffer pbuf = new StringBuffer (512);
	pbuf.append (normal ? "privmsg " : "notice ");
	pbuf.append (dest);
	pbuf.append (" :\01");
	pbuf.append (name);
	pbuf.append (' ');
	pbuf.append (text);
	pbuf.append ("\01\n");

	int len = pbuf.length ();
	if (len > 512)
	{
	    throw new RuntimeException ("### got too-large packet for IRC");
	}
	rawSend (pbuf.toString ());
    }

    /**
     * Send a command to the host system.
     *
     * @param name the command name
     * @param params the parameters to send
     */
    private void sendCommand (String name, String[] params)
    {
	StringBuffer pbuf = new StringBuffer (512);
	pbuf.append (name);
	for (int i = 0; i < (params.length - 1); i++)
	{
	    String p = params[i];
	    if (   (p.length () == 0)
                || (p.indexOf (' ') != -1)
		|| (p.charAt (0) == ':'))
	    {
		throw new ShouldntHappenException (
                    "Illegal parameter (" + i + ") for \"" + name + 
		    "\" +command: \"" + p + "\"");
	    }
	    pbuf.append (' ');
	    pbuf.append (p);
	}

	if (params.length != 0)
	{
	    pbuf.append (" :");
	    pbuf.append (params[params.length - 1]);
	}

	pbuf.append ('\n');

	int len = pbuf.length ();
	if (len > 512)
	{
	    throw new RuntimeException ("### got too-large packet for IRC");
	}
	rawSend (pbuf.toString ());
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
    private void commandTap (String name, String[] params, int[] codes, 
			     ReplyTap tap)
    {
	sendCommand (name, params);

	int codelen = codes.length;
	boolean cleanExit = false;

	try
	{
	    for (;;)
	    {
		if (myIsDisconnected)
		{
		    break;
		}

		ServerReply sr = receiveServerReply ();
		if (sr == null)
		{
		    continue;
		}

		int rint = sr.getReplyInt ();
		int which;
		for (which = 0; which < codelen; which++)
		{
		    if (rint == codes[which])
		    {
			break;
		    }
		}

		if (which < codelen)
		{
		    if (tap.handleReply (sr))
		    {
			cleanExit = true;
			break;
		    }
		}
		else
		{
		    sr.handleMessage ();
		}
	    }
	}
	finally
	{
	    if (! cleanExit)
	    {
		tap.handleReply (null);
	    }
	}
    }
}
