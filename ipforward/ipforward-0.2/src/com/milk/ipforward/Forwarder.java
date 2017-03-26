package com.milk.ipforward;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * This is the class that actually handles a connection once it has
 * been accepted.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved.
 * @author This code may be used for any purpose whatsoever, so long
 * as credit is appropriately given to the author.
 */
public class Forwarder
{
    /** the original acceptor */
    private ForwardAcceptor myAcceptor;

    /** client socket */
    private Socket myClientSocket;

    /** input from the client */
    private InputStream myClientInput;

    /** output to the client */
    private OutputStream myClientOutput;

    /** host socket */
    private Socket myHostSocket;

    /** input from the target host */
    private InputStream myHostInput;

    /** output to the target host */
    private OutputStream myHostOutput;

    /** the thread count for this object (0, 1, 2); used to know
     * when to inform the acceptor that the connection is done */
    private int myThreadCount;

    /**
     * Make a <code>Forwarder</code>.
     *
     * @param acceptor the original acceptor
     * @param socket the client socket
     * @param host the host to connect to
     * @param port the port to connect to
     */
    public Forwarder (ForwardAcceptor acceptor, Socket socket,
		      String host, int port)
    throws IOException
    {
	myAcceptor = acceptor;
	myClientSocket = socket;
	myClientInput = socket.getInputStream ();
	myClientOutput = socket.getOutputStream ();

	myHostSocket = new Socket (host, port);
	myHostInput = myHostSocket.getInputStream ();
	myHostOutput = myHostSocket.getOutputStream ();

	myThreadCount = 0;
    }

    /**
     * Do the actual forwarding thing. This merely sets some threads up
     * and then returns.
     */
    public void doit ()
    {
	synchronized (this)
	{
	    Thread t = new ForwardThread (myClientInput, myHostOutput);
	    t.setDaemon (true);
	    t.start ();
	    
	    t = new ForwardThread (myHostInput, myClientOutput);
	    t.setDaemon (true);
	    t.start ();
	}
    }

    /**
     * This is a thread which knows how to read from input and write
     * to output, ad infinitum.
     */
    private class ForwardThread
    extends Thread
    {
	private InputStream myInput;
	private OutputStream myOutput;

	public ForwardThread (InputStream input, OutputStream output)
	{
	    myInput = input;
	    myOutput = output;
	}

	public void run ()
	{
	    try
	    {
		synchronized (Forwarder.this)
		{
		    myThreadCount++;
		}
		reallyRun ();
	    }
	    catch (Exception ex)
	    {
		System.err.println ("Exception during forward:");
		ex.printStackTrace ();
	    }

	    synchronized (Forwarder.this)
	    {
		myThreadCount--;
		if (myThreadCount == 0)
		{
		    myAcceptor.connectionDied ();
		    try
		    {
			myHostSocket.close ();
		    }
		    catch (IOException ex)
		    {
			ex.printStackTrace ();
		    }
		    try
		    {
			myClientSocket.close ();
		    }
		    catch (IOException ex)
		    {
			ex.printStackTrace ();
		    }
		}
	    }
	}

	public void reallyRun ()
	throws IOException
	{
	    byte[] buf = new byte[1024];

	    for (;;)
	    {
		int available = myInput.available ();
		if (available == 0)
		{
		    int c = myInput.read ();
		    if (c == -1)
		    {
			break;
		    }
		    myOutput.write (c);
		}
		else
		{
		    if (available > buf.length)
		    {
			available = buf.length;
		    }
		    available = myInput.read (buf, 0, available);
		    myOutput.write (buf, 0, available);
		}
	    }
	}
    }
}
