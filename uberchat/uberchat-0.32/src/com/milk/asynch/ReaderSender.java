// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.asynch;

import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;

/**
 * A <code>ReaderSender</code> merely takes a <code>Reader</code> and makes
 * a thread to send hunks of data, as <code>char[]</code>s, to its
 * designated sender. If the <code>Reader</code> runs out of data, an
 * <code>EOFException</code> is sent, and then nothing else. If reading
 * causes an exception to be thrown, then that exception will also be sent
 * to the designated sender, at which point no more sending will happen.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998-1999 Dan Bornstein, all rights reserved. 
 */
public class ReaderSender
implements SendSource
{
    private Reader myReader;
    private Sender mySender;
    private int mySizeLimit;

    private volatile boolean myShouldStop;
    private Thread myThread;
    private StringBuffer myBuffer;
    private Object mySynch;
    public boolean myDebug = false;

    /**
     * Construct a <code>ReaderSender</code> to read from the given
     * <code>Reader</code> and send to the given <code>Sender</code>.
     *
     * @param reader the given <code>Reader</code>
     * @param sender the given <code>Sender</code>
     * @param sizeLimit the maximum number of characters to send at once,
     * or 0 for no limit 
     */
    public ReaderSender (Reader reader, Sender sender, int sizeLimit)
    {
	myReader = reader;
	mySender = sender;
	if (sizeLimit < 0)
	{
	    throw new IllegalArgumentException ("bad sizeLimit: " + sizeLimit);
	}
	mySizeLimit = sizeLimit;

	myShouldStop = false;
	myBuffer = new StringBuffer ();
	myDebug = false;

	// the buffer is private, so it's a good candidate for synchronizing
	// on; we have a separate variable to make the different use explicit
	mySynch = myBuffer;

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
     * Construct a <code>ReaderSender</code> to read from the given
     * <code>Reader</code> and send to the given <code>Sender</code>, with
     * no limit on the size of messages (i.e., the length of the
     * <code>char[]</code>s sent).
     *
     * @param reader the given <code>Reader</code>
     * @param sender the given <code>Sender</code>
     */
    public ReaderSender (Reader reader, Sender sender)
    {
	this (reader, sender, 0);
    }

    /**
     * Tell this object to cease sending.
     */
    public void stopSending ()
    {
	if (myDebug)
	{
	    System.err.println (this + ".stopSending ()");
	}

	synchronized (mySynch)
	{
	    if (myDebug)
	    {
		System.err.println (this + " is already stopped");
	    }
	    if (myReader == null)
	    {
		// null reader indicates that it's already stopped
		return;
	    }
	    myShouldStop = true;
	}

	myThread.stop ();
	
	// all the following is to deal with the possibility that the
	// thread won't die nicely. There's a common Java
	// implementation bug whereby Thread.interrupt() doesn't
	// actually cause pending I/O to get interrupted.

	try
	{
	    myThread.join (10000);
	}
	catch (InterruptedException ex)
	{
	    // ignore it
	}
	
	if (myThread.isAlive ())
	{
	    synchronized (mySynch)
	    {
		if (myReader == null)
		{
		    // see above
		    return;
		}
	    }
	    // terminate with extreme prejudice
	    if (myDebug)
	    {
		System.err.println (this + "'s thread is being recalcitrant");
	    }
	    myThread.destroy ();
	}

	if (myDebug)
	{
	    System.err.println (this + " is now stopped");
	}
    }

    /**
     * This is the main method that gets called in the thread to service
     * this object.
     */
    private void threadRun ()
    {
	for (;;)
	{
	    Throwable exceptionToSend = null;
	    int limit = mySizeLimit;
	    int len = 0;

	    myBuffer.setLength (0);

	    try
	    {
		if (myDebug)
		{
		    System.err.println (this + "'s thread blocking...");
		}
		// possibly blocking, read a single character
		int c = myReader.read ();
		if (myDebug)
		{
		    System.err.println (this + "'s thread done blocking");
		}

		if (c == -1)
		{
		    if (myDebug)
		    {
			System.err.println (
                            this + "'s thread at end-of-stream");
		    }
		    exceptionToSend = new EOFException ("End of data");
		}
		else
		{
		    myBuffer.append ((char) c);
		    limit--;
		    len++;

		    if (myDebug)
		    {
			System.err.println (
			    this + "'s thread reading (shouldn't block)...");
		    }
		    while ((limit != 0) && myReader.ready ())
		    {
			c = myReader.read ();
			if (c == -1)
			{
			    break;
			}
			myBuffer.append ((char) c);
			limit--;
			len++;
		    }
		    if (myDebug)
		    {
			System.err.println (this + "'s thread done reading");
		    }
		}
	    }
	    catch (Exception ex)
	    {
		exceptionToSend = ex;
	    }

	    char[] toSend;
	    if (len != 0)
	    {
		toSend = new char[len];
		myBuffer.getChars (0, len, toSend, 0);
	    }
	    else
	    {
		toSend = null;
	    }

	    synchronized (mySynch)
	    {
		if (myShouldStop)
		{
		    // don't actually send if we were told to stop
		    break;
		}

		if (exceptionToSend != null)
		{
		    // send last data, if any
		    if (toSend != null)
		    {
			safeSend (toSend);
		    }
		    safeSend (exceptionToSend);
		    myShouldStop = true;
		}
		else
		{
		    safeSend (toSend);
		}

		if (myShouldStop)
		{
		    break;
		}
	    }
	}

	synchronized (mySynch)
	{
	    myReader = null;
	    mySender = null;
	    myBuffer.setLength (0);
	    myBuffer = null;
	    myThread = null;
	}
    }

    /**
     * This does a safe send to the target, catching errors that the
     * target might throw and ignoring them (or spitting them to
     * <code>System.err</code> if debugging is on.
     *
     * @param message the message to send
     */
    private void safeSend (Object message)
    {
	if (myDebug)
	{
	    try
	    {
		System.err.println (this + "'s thread sending: " + 
				    message);
	    }
	    catch (Exception ex)
	    {
		// ignore it
	    }
	}

	try
	{
	    mySender.send (message);
	}
	catch (Exception ex)
	{
	    // it's legit to ignore the exception; see
	    // Sender.send() for more details
	    if (myDebug)
	    {
		System.err.println (this + "'s thread ignored exception:");
		ex.printStackTrace ();
	    }
	}
    }
}
