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

import com.milk.uberchat.SpeechKinds;
import com.milk.uberchat.base.BaseSystem;
import com.milk.uberchat.event.ErrorEvent;
import com.milk.uberchat.event.MessageEvent;
import com.milk.uberchat.iface.ChatIdentity;
import com.milk.util.BaseEvent;
import com.milk.util.ShouldntHappenException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * This is the SpaceBar-specific ChatSystem.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998 Dan Bornstein, all rights reserved. 
 */
public class SBSystem
extends BaseSystem
{
    private String myHost;
    private int myPort;
    private String myUserid;
    private String myPassword;

    /** the identity for the system--SpaceBar only allows one active identity
     * per connection */
    private SBIdentity myIdentity;

    /** the current channel--SpaceBar only allows one active channel */
    private SBChannel myCurrentChannel;

    /** whether we have "nominally" left the current channel. That is,
     * whether it's okay to actually switch channels (and not complain
     * about not being able to be on more than one channel at once) */
    private boolean myNominallyLeftChannel;

    /** the connection handler that does a lot of our dirty work */
    private SBConnectionHandler myConnectionHandler;

    /** the current interactor object */
    private SBInteractor myInteractor;

    /** the socket to use */
    private Socket mySocket;

    /**
     * Construct a <code>SBSystem</code> from a template.
     *
     * @param template the template to use
     */
    public SBSystem (SBSystemTemplate template)
    {
	super (template.myName);
	template = (SBSystemTemplate) template.copy ();
	setTemplate (template);

	myHost = template.myHost;
	myPort = template.myPort;
	myUserid = template.myUserid;
	myPassword = template.myPassword;

	myIdentity = new SBIdentity (this);
	myConnectionHandler = new SBConnectionHandler (this, 250);
	myInteractor = null;
	mySocket = null;
	myCurrentChannel = null;
	myNominallyLeftChannel = true;
    }

    // ------------------------------------------------------------------------
    // ChatSystem interface methods

    /**
     * Turn the given user name into its canonical form. For the
     * SpaceBar, it's just lower-cased and interned.
     *
     * @param orig the original name
     * @return the canonical form
     */
    public String canonicalUserName (String orig)
    {
	return orig.toLowerCase ().intern ();
    }

    /**
     * Turn the given channel name into its canonical form. For the
     * SpaceBar, we try to treat everything as a number, padding with a 0
     * if it's only one digit. If it's not a number from 1 to 99, then we
     * return null.
     *
     * @param orig the original name
     * @return the canonical form 
     */
    public String canonicalChannelName (String orig)
    {
	// try to parse it as a number
	int num;
	try
	{
	    num = Integer.parseInt (orig);
	}
	catch (NumberFormatException ex)
	{
	    // not a number, so it can't be a valid channel name
	    return null;
	}

	if ((num < 1) || (num > 99))
	{
	    // out of range
	    return null;
	}

	char[] digits = new char[2];
	digits[0] = (char) ((num / 10) + '0');
	digits[1] = (char) ((num % 10) + '0');

	return new String (digits).intern ();
    }

    // ------------------------------------------------------------------------
    // Protected methods that we override

    /**
     * (Attempt to) connect to this system.
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
     * Disconnect from this system.
     */
    protected void systemDisconnect ()
    {
	removeIdentity (myIdentity);

	// we do this in a thread so that the ui (or whatever)
	// that called us won't get all stalled
	Thread t = new Thread ()
	    {
		public void run ()
		{
		    try
		    {
			myConnectionHandler.disconnect ();
			myInteractor = null;
			callSystemDisconnected ();
		    }
		    catch (Exception ex)
		    {
			bugReport (ex);
		    }
		}
	    };
	t.setDaemon (true);
	t.start ();
    }

    /**
     * Simply call <code>systemDisconnected</code> in our superclass.
     * This method exists to work around a bug in <code>javac</code> where
     * it refused to compile this call inside the inner class in 
     * <code>systemDisconnect</code>, above.
     */
    private void callSystemDisconnected ()
    {
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
    /*package*/ SBIdentity getIdentity ()
    {
	return myIdentity;
    }

    /**
     * Get the current channel.
     *
     * @return the current channel
     */
    /*package*/ SBChannel getCurrentChannel ()
    {
	return myCurrentChannel;
    }

    /**
     * Set the current channel.
     *
     * @param channel the new current channel
     */
    /*package*/ void setCurrentChannel (SBChannel channel)
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
     * Do a raw send to the host system.
     *
     * @param raw the string to send
     */
    /*package*/ void rawSend (String kind, String raw)
    {
	if (kind == SpeechKinds.RAW)
	{
	    raw += '\n';
	}

	myInteractor.doRawSend (raw);
    }

    /**
     * Type a line at the host system (i.e., expect it to be echoed back).
     *
     * @param line the line to type
     */
    /*package*/ void typeLine (String line)
    {
	myInteractor.doTypeLine (line);
    }

    /**
     * Do a send-expect-send to the host system.
     *
     * @param raw the string to send
     *
     * @param send1 the first string to send
     * @param prompt the prompt type to expect
     * @param send2 the second string to send
     */
    /*package*/ void sendExpectSend (String send1, String prompt, 
				     String send2)
    {
	myInteractor.doSendExpectSend (send1, prompt, send2);
    }

    /**
     * Do a send-expect-send-expect-send.
     *
     * @param send1 the first string to send
     * @param prompt1 the first prompt type to expect
     * @param send2 the second string to send
     * @param prompt2 the second prompt type to expect
     * @param send3 the third string to send
     */
    /*package*/ void sendExpectSend2 (String send1, String prompt1, 
				      String send2, String prompt2,
				      String send3)
    {
	myInteractor.doSendExpectSend2 (send1, prompt1, send2, prompt2, send3);
    }

    /**
     * Do a send-gather.
     *
     * @param send the string to send
     * @return an array of lines gathered as the response from the interaction
     */
    /*package*/ String[] sendGather (String send)
    {
	return myInteractor.doSendGather (send);
    }

    /**
     * Do a send-expect-send-gather.
     *
     * @param send1 the first string to send
     * @param prompt the prompt type to expect
     * @param send2 the second string to send
     * @return an array of lines gathered as the response from the interaction
     */
    /*package*/ String[] sendExpectSendGather (String send1, String prompt,
					       String send2)
    {
	return myInteractor.doSendExpectSendGather (send1, prompt, send2);
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
	if (source instanceof SBChannel)
	{
	    ((SBChannel) source).callBroadcast (ev);
	}
	else if (source instanceof SBIdentity)
	{
	    ((SBIdentity) source).callBroadcast (ev);
	}
	else if (source instanceof SBSystem)
	{
	    ((SBSystem) source).callBroadcast (ev);
	}
	else if (source instanceof SBUser)
	{
	    ((SBUser) source).callBroadcast (ev);
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
	try
	{
	    mySocket = new Socket (myHost, myPort);
	    myNominallyLeftChannel = true;
	    myCurrentChannel = null;
	    myConnectionHandler.connected ();
	}
	catch (IOException ex)
	{
	    systemDisconnected ();
            errorReport ("Unable to open socket for connection.\n" +
			 ex.getMessage ());
	    return;
	}

	myInteractor = myConnectionHandler.getInteractor ();

	ErrorEvent problem = myInteractor.doLogin (myUserid, myPassword);

	if (problem != null)
	{
	    systemDisconnected ();
	    dispatchBroadcast (problem);
	}
	else
	{
	    systemConnected ();
	    addIdentity (myIdentity);
	}
    }
}
