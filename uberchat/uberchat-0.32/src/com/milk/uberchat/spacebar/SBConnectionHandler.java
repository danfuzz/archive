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

import com.milk.asynch.IdleFilter;
import com.milk.asynch.LineFilter;
import com.milk.asynch.MailBox;
import com.milk.asynch.ReaderSender;
import com.milk.asynch.Resender;
import com.milk.asynch.Sender;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * This class is a front end on the actual socket connecting to SpaceBar.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998 Dan Bornstein, all rights reserved. 
 */
/*package*/ class SBConnectionHandler
{
    /** constant for sending as a command */
    static private final Object NOP = new Object ();

    /** the SBSystem to use */
    private SBSystem mySystem;

    /** the idle time to wait before sending partial lines */
    private long myIdleTime;

    /** the current connection socket object */
    private Socket mySocket;

    /** the current interactor object */
    private SBInteractor myInteractor;

    /** the current input front-end */
    private ReaderSender myReaderSender;

    /**
     * Construct a SBConnectionHandler that uses the given SBSystem.
     *
     * @param system the system to use
     * @param idleTime the idle time to wait before sending partial lines;
     * 0 means wait forever
     */
    /*package*/ SBConnectionHandler (SBSystem system, long idleTime)
    {
	if (idleTime < 0)
	{
	    throw new IllegalArgumentException ("bad idleTime: " + idleTime);
	}

	mySystem = system;
	myIdleTime = idleTime;

	myInteractor = null;
	myReaderSender = null;
    }

    /**
     * Inform this object that a new connection has been established.
     */
    /*package*/ void connected ()
    throws IOException
    {
	if (myInteractor != null)
	{
	    throw new RuntimeException ("shouldn't happen: already connected");
	}

	mySocket = mySystem.getSocket ();
	InputStreamReader reader = 
	    new InputStreamReader (mySocket.getInputStream ());
	OutputStreamWriter writer =
	    new OutputStreamWriter (mySocket.getOutputStream ());

	Resender inputSink = new Resender ();

	Sender target = 
	    new LineFilter (new SBInputFilter (mySystem, inputSink));

	if (myIdleTime != 0)
	{
	    target = new IdleFilter (target, NOP, myIdleTime);
	}

	myReaderSender = 
	    new ReaderSender (new BufferedReader (reader), target);

	myInteractor = new SBInteractor (mySystem, inputSink, writer, NOP);
    }

    /**
     * Tell the connection to shut down.
     */
    /*package*/ void disconnect ()
    {
	if (myInteractor == null)
	{
	    throw new RuntimeException ("shouldn't happen: not connected");
	}

	myInteractor.doLogout ();
	myReaderSender.stopSending ();
	
	try
	{
	    mySocket.close ();
	}
	catch (IOException ex)
	{
	    // BUG--do something better
	    ex.printStackTrace ();
	}

	myInteractor = null;
	myReaderSender = null;
	mySocket = null;
    }

    /**
     * Get the current interactor.
     *
     * @return the current interactor
     */
    /*package*/ SBInteractor getInteractor ()
    {
	return myInteractor;
    }
}
