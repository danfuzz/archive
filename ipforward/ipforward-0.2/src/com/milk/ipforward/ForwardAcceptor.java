package com.milk.ipforward;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This is the workhorse class for doing the forwarding.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved.
 * @author This code may be used for any purpose whatsoever, so long
 * as credit is appropriately given to the author.
 */
public class ForwardAcceptor
{
    /** the host to connect to */
    private String myConnectHost;

    /** the port to connect to */
    private int myConnectPort;

    /** the maximum number of connections to handle */
    private int myMaxConnections;

    /** the socket to listen on */
    private ServerSocket mySocket;

    /** the current number of connections */
    private int myConnectionCount;

    /**
     * Construct a <code>ForwardAcceptor</code>.
     *
     * @param listenPort the port to listen to
     * @param connectHost the host to connect to
     * @param connectPort the port to connect to
     * @param maxConnections the maximum number of connections to handle; if
     * specified as 0, this means there is no limit
     */
    public ForwardAcceptor (int listenPort, 
			    String connectHost, int connectPort,
			    int maxConnections)
    {
	myConnectHost = connectHost;
	myConnectPort = connectPort;
	myMaxConnections = maxConnections;

	myConnectionCount = 0;
	try
	{
	    mySocket = new ServerSocket (listenPort);
	}
	catch (IOException ex)
	{
	    System.err.println ("Could not make socket for listening:");
	    ex.printStackTrace ();
	}
    }

    /**
     * This is called by a <code>Forwarder</code> when it's closing
     * up shop.
     */
    /*package*/ void connectionDied ()
    {
	synchronized (this)
	{
	    myConnectionCount--;
	    this.notifyAll ();
	}
    }

    /**
     * Do the forwarding thing. This will not normally ever return.
     */
    public void doit ()
    {
	if (mySocket == null)
	{
	    return;
	}

	for (;;)
	{
	    try
	    {
		synchronized (this)
		{
		    while (   (myMaxConnections > 0)
			   && (myConnectionCount >= myMaxConnections))
		    {
			try
			{
			    wait ();
			}
			catch (InterruptedException ex)
			{
			    // ignore it
			}
		    }
		}

		Socket s = mySocket.accept ();
		new Forwarder (this, s, myConnectHost, myConnectPort).doit ();

		synchronized (this)
		{
		    myConnectionCount++;
		}
	    }
	    catch (IOException ex)
	    {
		ex.printStackTrace ();
	    }
	}
    }
}
