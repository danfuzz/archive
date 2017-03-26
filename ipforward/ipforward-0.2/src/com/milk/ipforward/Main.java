package com.milk.ipforward;

import java.util.StringTokenizer;

/**
 * This is a very simple program which knows how to accept TCP/IP
 * connections on one port, and merely react by opening a TCP/IP connection
 * to some other host/port and forwarding. Firewalls be damned!
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved.
 * @author This code may be used for any purpose whatsoever, so long
 * as credit is appropriately given to the author.
 */
public class Main
{
    /**
     * <p>Do the forwarding thing. The options understood are:</p>
     *
     * <dl>
     * <dt>--help</dt><dd>print a brief help message</dd>
     * <dt>--listen <i>port</i></dt><dd>the port to listen on</dd>
     * <dt>--connect <i>host</i>:<i>port</i></dt><dd>the target host
     * to connect to</dd>
     * <dt>--max <i>count</i></dt><dd>the maximum number of connections
     * to forward</dd>
     * </dl>
     *
     * @param args the arguments
     */
    public static void main (String[] args)
    {
	int listenPort = -1;
	String connectHost = null;
	int connectPort = -1;
	int maxConnections = 0;
	boolean showHelp = false;

	for (int i = 0; i < args.length; i++)
	{
	    String arg = args[i];
	    try
	    {
		if (arg.equals ("--help"))
		{
		    showHelp = true;
		    break;
		}
		else if (arg.equals ("--listen"))
		{
		    i++;
		    listenPort = Integer.parseInt (args[i]);
		}
		else if (arg.equals ("--connect"))
		{
		    i++;
		    StringTokenizer st = new StringTokenizer (args[i], ":");
		    connectHost = st.nextToken ();
		    connectPort = Integer.parseInt (st.nextToken ());
		}
		else if (arg.equals ("--max"))
		{
		    i++;
		    maxConnections = Integer.parseInt (args[i]);
		}
	    }
	    catch (Exception ex)
	    {
		System.err.println ("bad argument: " + args[i]);
		showHelp = true;
	    }
	}

	if (   (listenPort == -1)
	    || (connectPort == -1))
	{
	    System.err.println (
                "You must specify the \"--listen\" and " +
		"\"--connect\" options.");
	    showHelp = true;
	}

	if (showHelp)
	{
	    System.err.println (
                "Options:\n" +
		"  --help  print this message\n" +
		"  --listen <port>  the port to listen to\n" +
		"  --connect <host>:<port>  the target to connect to\n" +
		"  --max <count>  the maximum number of connections to handle"
		);
	    System.exit (0);
	}

	ForwardAcceptor fa = 
	    new ForwardAcceptor (listenPort, 
				 connectHost, connectPort, 
				 maxConnections);
	fa.doit ();
    }
}
