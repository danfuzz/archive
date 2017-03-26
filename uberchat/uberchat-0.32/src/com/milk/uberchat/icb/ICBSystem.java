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

import com.milk.asynch.ReaderSender;
import com.milk.asynch.Resender;
import com.milk.asynch.Sender;
import com.milk.uberchat.base.BaseSystem;
import com.milk.uberchat.event.ErrorEvent;
import com.milk.uberchat.event.MessageEvent;
import com.milk.util.BaseEvent;
import com.milk.util.ShouldntHappenException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * This is the ICB-specific <code>ChatSystem</code>.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
public class ICBSystem
extends BaseSystem
{
    private String myHost;
    private int    myPort;
    private String myEmail;
    private String myUserid;
    private String myPassword;
    private String myInitialChannel;

    /** the identity for the system--ICB only allows one active identity
     * per connection */
    private ICBIdentity myIdentity;

    /** the current channel--ICB only allows one active channel */
    private ICBChannel myCurrentChannel;

    /** whether we have "nominally" left the current channel. That is,
     * whether it's okay to actually switch channels (and not complain
     * about not being able to be on more than one channel at once) */
    private boolean myNominallyLeftChannel;

    /** the current interactor object */
    private ICBInteractor myInteractor;

    /** the current input front-end */
    private ReaderSender myReaderSender;

    /** the current socket to use for IO */
    private Socket mySocket;

    /**
     * Construct an <code>ICBSystem</code> from the given template.
     *
     * @param template the template
     */
    public ICBSystem (ICBSystemTemplate template)
    {
	super (template.myName);
	template = (ICBSystemTemplate) template.copy ();
	setTemplate (template);

	myHost = template.myHost;
	myPort = template.myPort;
	myEmail = template.myEmail;
	myUserid = template.myUserid;
	myPassword = template.myPassword;
	myInitialChannel = template.myInitialChannel;

	myIdentity = new ICBIdentity (this);
	myInteractor = null;
	mySocket = null;
	myCurrentChannel = null;
	myNominallyLeftChannel = true;
    }

    // ------------------------------------------------------------------------
    // ChatSystem methods

    /**
     * Turn the given user name into its canonical form. For ICB, this
     * just lower-cases and interns it.
     *
     * @param orig the original name
     * @return the canonical form
     */
    public String canonicalUserName (String orig)
    {
	return orig.toLowerCase ().intern ();
    }

    /**
     * Turn the given channel name into its canonical form. For ICB, this
     * just lower-cases and interns it.
     *
     * @param orig the original name
     * @return the canonical form
     */
    public String canonicalChannelName (String orig)
    {
	return orig.toLowerCase ().intern ();
    }

    // ------------------------------------------------------------------------
    // Required protected methods

    /**
     * BaseSystem calls this method when it actually wants a connection
     * to happen. It will only call this if the system is not actually
     * connected.
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
     * BaseSystem calls this method when it actually wants to disconnect
     * the system. It will only call this if the system is actually
     * connected. Our interactor will call this too, if the server
     * tells it to disconnect.
     */
    protected void systemDisconnect ()
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

	myInteractor = null;
	myReaderSender = null;
	mySocket = null;

	systemDisconnected ();
    }
    
    // ------------------------------------------------------------------------
    // Package methods

    /**
     * Get the current socket.
     *
     * @return the current socket in use
     */
    /*package*/ Socket getSocket ()
    {
	return mySocket;
    }

    /**
     * Get the unique identity for this system.
     *
     * @return the identity
     */
    /*package*/ ICBIdentity getIdentity ()
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
	return myUserid;
    }

    /**
     * Get the password for this system.
     *
     * @return the password
     */
    /*package*/ String getPassword ()
    {
	return myPassword;
    }

    /**
     * Get the current channel.
     *
     * @return the current channel
     */
    /*package*/ ICBChannel getCurrentChannel ()
    {
	return myCurrentChannel;
    }

    /**
     * Set the current channel.
     *
     * @param channel the new current channel
     */
    /*package*/ void setCurrentChannel (ICBChannel channel)
    {
	if (   (myCurrentChannel != null)
	    && (! myNominallyLeftChannel))
	{
	    myCurrentChannel.channelLeave ();
	}

	myCurrentChannel = channel;
	myCurrentChannel.reallyJoinedChannel ();
    }

    /**
     * Find out whether we've nominally left the current channel.
     *
     * @return true if we've nominally left the current channel
     */
    /*package*/ boolean getNominallyLeftChannel ()
    {
	return myNominallyLeftChannel;
    }

    /**
     * Set whether we've nominally left the current channel.
     *
     * @param left true if we've nominally left the current channel
     */
    /*package*/ void setNominallyLeftChannel (boolean left)
    {
	myNominallyLeftChannel = left;
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
     * Do a packet send to the host system.
     *
     * @param type the packet type
     * @param params the packet parameters
     */
    /*package*/ void sendPacket (char type, String[] params)
    {
	if (myInteractor == null)
	{
	    errorReport ("System is not connected.");
	    return;
	}

	myInteractor.doSendPacket (type, params);
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

	// for icb, raw sends are only allowed to be commands
	while (raw.startsWith (" "))
	{
	    raw = raw.substring (1);
	}

	if (raw.endsWith ("\n"))
	{
	    raw = raw.substring (0, raw.length () - 1);
	}

	int spaceAt = raw.indexOf (' ');

	if (spaceAt == -1)
	{
	    if (raw.equals ("PING"))
	    {
		// special case to send ping packet to server
		myInteractor.doSendPacket ('l', new String[0]);
	    }
	    else
	    {
		myInteractor.doSendPacket ('h', new String[] { raw, "" });
	    }
	}
	else
	{
	    String command = raw.substring (0, spaceAt);
	    String args = raw.substring (spaceAt + 1);
	    myInteractor.doSendPacket ('h', new String[] { command, args });
	}
    }

    /**
     * Send a packet and gather the response as an array of MessageEvents.
     *
     * @param type the packet type
     * @param params the packet parameters
     * @return the array of response lines
     */
    /*package*/ MessageEvent[] sendGather (char type, String[] params)
    {
	if (myInteractor == null)
	{
	    errorReport ("System is not connected.");
	    return new MessageEvent[0];
	}

	return myInteractor.doSendGather (type, params);
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
	if (source instanceof ICBChannel)
	{
	    ((ICBChannel) source).callBroadcast (ev);
	}
	else if (source instanceof ICBIdentity)
	{
	    ((ICBIdentity) source).callBroadcast (ev);
	}
	else if (source instanceof ICBSystem)
	{
	    ((ICBSystem) source).callBroadcast (ev);
	}
	else if (source instanceof ICBUser)
	{
	    ((ICBUser) source).callBroadcast (ev);
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
     * This method gets called in a thread when a systemConnect request
     * gets made.
     */
    private void doConnect ()
    {
	InputStreamReader reader;
	OutputStreamWriter writer;

	try
	{
	    mySocket = new Socket (myHost, myPort);
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

	myNominallyLeftChannel = true;
	myCurrentChannel = null;

	Resender inputSink = new Resender ();
	Sender target = new ICBInputFilter (this, inputSink);
	myReaderSender = new ReaderSender (reader, target);
	myInteractor = new ICBInteractor (this, inputSink, writer);

	// BUG--BEGIN debugging code
	if (getName ().startsWith ("debug"))
	{
	    myReaderSender.myDebug = true;
	    myInteractor.myDebug = true;
	    ((ICBInputFilter) target).myDebug = true;
	}
	// BUG--END debugging code

	myInteractor.doLogin (myEmail, myUserid, myPassword, myInitialChannel);

	// synch and make sure we're actually connected
	sendGather ('h', new String[] { "m", "" });

	if (mySocket != null)
	{
	    // if there was a problem during login, then the socket
	    // should be reset to null and we'll notice that fact here
	    systemConnected ();
	    addIdentity (myIdentity);
	}
    }
}
