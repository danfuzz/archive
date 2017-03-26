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

import com.milk.asynch.FifoQueue;
import com.milk.asynch.MailBox;
import com.milk.asynch.Resender;
import com.milk.uberchat.event.ErrorEvent;
import com.milk.uberchat.event.MessageEvent;
import com.milk.util.ShouldntHappenException;
import java.io.IOException;
import java.io.Writer;
import java.util.Vector;

/**
 * This class does all the interaction with an ICB server. It takes input
 * in the form of filtered messages (filtered through an ICBInputFilter)
 * and explicit calls to do stuff. It sends stuff back to the server
 * depending on what it's told to do, and messages etc. along to an
 * ICBSystem depending on what gets received from the connection.
 * BUG--when you disconnect, the interactor thread is left around.
 * This is bad.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
/*package*/ class ICBInteractor
{
    /** the period (in msec) between server pings when no other activity
     * is happening */
    static private final long PING_PERIOD = 120000;

    private ICBSystem mySystem;
    private Writer myOutput;

    private ICBIdentity myIdentity;
    private FifoQueue myInput;
    private Vector myCommands;
    private Thread myThread;
    private boolean myIsDisconnected;
    private MyPinger myPinger;

    /** whether to spew debugging info or not */
    public boolean myDebug = false;

    /** internal instance used to sort out <code>ICBEvent</code>s that
     * get sent to us by the <code>ICBInputFilter</code>. */
    private MyICBListener myICBListener;

    /**
     * Construct an ICBInteractor.
     *
     * @param system the system to use
     * @param input the filtered stream of input from the server
     * @param output the raw output stream
     */
    public ICBInteractor (ICBSystem system, Resender input, Writer output)
    {
	mySystem = system;
	myOutput = output;

	myIdentity = system.getIdentity ();
	myCommands = new Vector ();
	myIsDisconnected = false;

	myInput = new FifoQueue ();
	input.sendTo (myInput);

	myICBListener = new MyICBListener ();
	myPinger = new MyPinger ();

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
     * Tell the interactor to do a login.
     *
     * @param email the email address to use
     * @param userid the userid to use
     * @param password the password to use
     * @param initialChannel the initial channel to join
     */
    public void doLogin (final String email, final String userid, 
			 final String password, final String initialChannel)
    {
	myInput.send (new Runnable ()
	{
	    public void run ()
	    {
		String em = email.equals ("") ? userid : email;

		sendPacket ('a', 
			    new String[] { em, userid, initialChannel, 
					   "login", password });
		sendPacket ('h', 
			    new String[] { "echoback", "verbose" });
	    }

	    // for debugging
	    public String toString ()
	    {
		return ("doLogin() runnable");
	    }
	});
    }

    /**
     * Tell the interactor to send a packet to the host system.
     *
     * @param type the packet type
     * @param params the packet parameters
     */
    public void doSendPacket (final char type, final String[] params)
    {
	myInput.send (new Runnable ()
	{
	    public void run ()
	    {
		sendPacket (type, params);
	    }

	    // for debugging
	    public String toString ()
	    {
		return ("sendPacket() runnable");
	    }
	});
    }	

    /**
     * Tell the interactor to Send a packet and gather the response as 
     * an array of MessageEvents.
     *
     * @param type the packet type
     * @param params the packet parameters
     * @return the array of response lines
     */
    public MessageEvent[] doSendGather (final char type, 
					final String[] params)
    {
	// mailbox used just to get the result back
	final MailBox result = new MailBox ();
	
	myInput.send (new Runnable ()
	{
	    public void run ()
	    {
		try
		{
		    result.send (sendGather (type, params));
		}
		catch (Exception ex)
		{
		    result.send (null);
		    if (! (ex instanceof ShouldntHappenException))
		    {
			ex = 
			    new ShouldntHappenException (
                                "Error caught during sendGather operation.",
				ex);
		    }
		    mySystem.bugReport (ex);
		}
	    }

	    // for debugging
	    public String toString ()
	    {
		return ("sendGather() runnable");
	    }
	});

	return ((MessageEvent[]) result.receive ());
    }	

    // ------------------------------------------------------------------------
    // Private helper methods

    /**
     * This is the main loop of the thread that deals with the connection.
     */
    private void threadRun ()
    {
	for (;;)
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
                        "Error caught at top of ICB interactor loop.",
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
	mySystem.systemDisconnect ();
	if (errorMsg != null)
	{
	    mySystem.errorReport (errorMsg);
	}
    }

    /**
     * This will receive a message from the input, handling everything
     * appropriately, except for non-speech <code>MessageEvent</code>s,
     * which it returns without any processing. Handling appropriately
     * includes adding <code>Runnable</code>s to the command queue and
     * telling the system about speech messages. If it receives and handles
     * a message, it returns null.
     *
     * @return null-ok; the received non-speech MessageEvent, if any 
     */
    private MessageEvent receiveSystemMessage ()
    {
	MessageEvent result = null;
	Object message = myInput.receive (PING_PERIOD);

	if (myDebug)
	{
	    System.err.println ("ICBInteractor received: " + message);
	}

	if (message == null)
	{
	    // timed out; queue up a ping
	    myCommands.addElement (myPinger);
	}
	else if (message instanceof Runnable)
	{
	    myCommands.addElement (message);
	}
	else if (message instanceof ICBEvent)
	{
	    ((ICBEvent) message).sendTo (myICBListener);
	    result = myICBListener.getResult ();
	}
	else if (message instanceof MessageEvent)
	{
	    result = (MessageEvent) message;
	    if (result.getType () == MessageEvent.USER_BROADCAST)
	    {
		mySystem.dispatchBroadcast (result);
		result = null;
	    }
	}
	else if (message instanceof Throwable)
	{
	    // a throwable made its way out of the read stream
	    // we take this to mean the connection died
	    beDisconnected ("Connection closed unexpectedly with error:\n" +
			    ((Throwable) message).getMessage ());
	}
	else
	{
	    mySystem.bugReport (
                new ShouldntHappenException (
                    "ICB interactor got weird message:\n" + message));
	}

	return result;
    }

    /**
     * This will receive a message from the input and handle it appropriately.
     * This does everything that receiveSystemMessage does, plus it will
     * send SystemMessages to the main system object.
     */
    private void receiveMessage ()
    {
	MessageEvent message = receiveSystemMessage ();

	if (message == null)
	{
	    return;
	}

	mySystem.dispatchBroadcast (message);
    }

    /**
     * Send a packet of the given type with the given parameter fields
     * (array of strings).
     *
     * @param type the type of the packet
     * @param params the parameters to send
     */
    private void sendPacket (char type, String[] params)
    {
	if (myIsDisconnected)
	{
	    return;
	}

	if (myDebug)
	{
	    StringBuffer sb = new StringBuffer (400);
	    sb.append ("ICBInteractor sending: ");
	    sb.append (type);
	    for (int i = 0; i < params.length; i++)
	    {
		sb.append ("; ");
		sb.append (params[i]);
	    }
	    System.err.println (sb.toString ());
	}

	StringBuffer pbuf = new StringBuffer (256);
	pbuf.append (' '); // placeholder for length byte
	pbuf.append (type);
	for (int i = 0; i < params.length; i++)
	{
	    if (i != 0)
	    {
		pbuf.append ((char) 1);
	    }
	    pbuf.append (params[i]);
	}
	int len = pbuf.length ();
	if (len > 255)
	{
	    throw new RuntimeException ("### got too-large packet for ICB");
	}
	pbuf.setCharAt (0, (char) len);
	pbuf.append ('\0');

	try
	{
	    myOutput.write (pbuf.toString ());
	    myOutput.flush ();
	}
	catch (IOException ex)
	{
	    beDisconnected ("Trouble sending to connection:\n" +
			    ex.getMessage ());
	}
    }

    /**
     * Send a packet then gather a response from the host. Since ICB sucks,
     * we cheesily use an invalid command as the delimiter to know that the
     * response is done.
     *
     * @param type the packet type
     * @param params the packet parameters
     * @return the array of lines received as a response 
     */
    private MessageEvent[] sendGather (char type, String[] params)
    {
	Vector rvec = new Vector ();
	boolean inResponse = false;

	if (myDebug)
	{
	    System.err.println ("ICBInteractor starting sendGather...");
	}
	
	sendPacket ('h', new String[] { "splort" });
	sendPacket (type, params);
	sendPacket ('h', new String[] { "splort" });

	for (;;)
	{
	    if (myIsDisconnected)
	    {
		break;
	    }
	    MessageEvent msg = receiveSystemMessage ();
	    if (msg != null)
	    {
		if (   (msg.getType () == MessageEvent.SYSTEM_PRIVATE)
		    && (msg.getText ().equals ("[Server] Unknown command")))
		{
		    if (inResponse)
		    {
			break;
		    }
		    else
		    {
			inResponse = true;
		    }
		}
		else if (inResponse)
		{
		    rvec.addElement (msg);
		}
		else
		{
		    mySystem.dispatchBroadcast (msg);
		}
	    }
	}

	if (myDebug)
	{
	    System.err.println ("ICBInteractor done with sendGather.");
	}

	MessageEvent[] result = new MessageEvent[rvec.size ()];
	rvec.copyInto (result);
	return result;
    }

    // ------------------------------------------------------------------------
    // Private helper classes

    /**
     * This class is used as an inner instance to sort out
     * <code>ICBEvent</code>s that get sent from the
     * <code>ICBInputFilter</code>. After sending to this listener,
     * if a result needs to get sent along, then <code>getResult()</code>
     * returns something non-null.
     */
    private class MyICBListener
    implements ICBListener
    {
	/** message to send along as a result of processing */
	private MessageEvent myResult = null;

	/**
	 * Get the message to send downstream as a result of event
	 * processing. This should be called immediately after sending
	 * an event to this object. If it returns non-null, then the
	 * return value should be passed along.
	 *
	 * @return null-ok; the message to pass along
	 */
	public MessageEvent getResult ()
	{
	    MessageEvent result = myResult;
	    myResult = null;
	    return result;
	}

	// --------------------------------------------------------------------
	// ICBListener interface methods

	/**
	 * This is called when the server sends a disconnect packet.
	 *
	 * @param event the event commemorating the moment
	 */
	public void disconnectPacket (ICBEvent event)
	{
	    // deal with it directly
	    beDisconnected (null);
	}
	
	/**
	 * This is called when the server sends an error packet.
	 *
	 * @param event the event commemorating the moment
	 */
	public void errorPacket (ICBEvent event)
	{
	    myResult = 
		MessageEvent.systemPrivate (
                    myIdentity,
		    "[Error] " + event.getMessage ());
	}

	/**
	 * This is called when the server sends an important packet.
	 *
	 * @param event the event commemorating the moment
	 */
	public void importantPacket (ICBEvent event)
	{
	    myResult = 
		MessageEvent.systemPrivate (
                    myIdentity,
		    "[Important] " + event.getMessage ());
	}

	/**
	 * This is called when the server sends a ping packet.
	 *
	 * @param event the event commemorating the moment
	 */
	public void pingPacket (ICBEvent event)
	{
	    // deal with it directly
	    sendPacket ('m', new String[0]);
	}
	
	/**
	 * This is called when the server sends a pong packet.
	 *
	 * @param event the event commemorating the moment
	 */
	public void pongPacket (ICBEvent event)
	{
	    myResult = 
		MessageEvent.systemPrivate (
                    myIdentity,
		    "[PONG] Pong from server");
	}

	/**
	 * This is called when the server sends a register packet.
	 *
	 * @param event the event commemorating the moment
	 */
	public void registerPacket (ICBEvent event)
	{
	    // deal with register request directly; what we do is send
	    // the password message if both we have a password to send
	    // and the current nickname is the same as the originally
	    // specified userid
	    String userid = mySystem.getUserid ();
	    String realUserid = 
		((ICBUser) myIdentity.getIdentityUser ()).getICBUserid ();
	    String password = mySystem.getPassword ();
	    if (   realUserid.equalsIgnoreCase (userid)
		&& (! password.equals ("")))
	    {
		sendPacket ('h', new String[] { "m", "server p " + password });
	    }
	}

	/**
	 * This is called when the server sends a who packet.
	 *
	 * @param event the event commemorating the moment
	 */
	public void whoPacket (ICBEvent event)
	{
	    myResult = MessageEvent.systemPrivate (myIdentity,
						   event.getWho ());
	}
    }

    /**
     * Object that gets queued up to do periodic pings.
     */
    private class MyPinger
	implements Runnable
    {
	private final String[] myParams = new String[0];

	public void run ()
	{
	    sendPacket ('n', myParams);
	}
    }
}
