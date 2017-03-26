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
 * This class does all the interaction with a SpaceBar server.
 * It takes input in the form of filtered messages (filtered through
 * a SBInputFilter) and explicit calls to do stuff. It sends stuff back
 * to the server depending on what it's told to do, and messages etc.
 * along to a SBSystem depending on what gets received from the connection.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1998 Dan Bornstein, all rights reserved. 
 */
/*package*/ class SBInteractor
{
    private SBSystem mySystem;
    private Writer myOutput;
    private Object myIdleObject;

    private SBIdentity myIdentity;
    private FifoQueue myInput;
    private Vector myCommands;
    private Thread myThread;
    private SBPromptMessage myLastPrompt;
    private Vector myIgnoreLines;
    private boolean myIsDisconnected;

    /**
     * Construct an SBInteractor.
     *
     * @param system the system to use
     * @param input the filtered stream of input from the server
     * @param output the raw output stream
     * @param idleObject the object sent when an idle event happens
     */
    public SBInteractor (SBSystem system, Resender input, Writer output,
			 Object idleObject)
    {
	mySystem = system;
	myOutput = output;
	myIdleObject = idleObject;

	myIdentity = system.getIdentity ();
	myLastPrompt = null;
	myCommands = new Vector ();
	myIgnoreLines = new Vector ();
	myIsDisconnected = false;

	myInput = new FifoQueue ();
	input.sendTo (myInput);

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
     * @param userid the userid to use
     * @param password the password to use
     * @return null-ok; if non-null, the error explaining why the login
     * failed
     */
    public ErrorEvent doLogin (final String userid, final String password)
    {
	// mailbox used just to get the result back
	final MailBox result = new MailBox ();

	myInput.send (new Runnable ()
	{
	    public void run ()
	    {
		expectPrompt ("userid");
		typeLine (userid + "\n");

		if (gotPrompt (10, "userid"))
		{
		    result.send (
		        ErrorEvent.errorReport (
                            mySystem, 
			    "Incorrect userid."));
		    myIsDisconnected = true;
		    return;
		}

		expectPrompt ("password");
		rawSend (password); // password isn't echoed, so no typeLine()
		typeLine ("\n");

		if (gotPrompt (5, "incorrect-password"))
		{
		    result.send (
		        ErrorEvent.errorReport (
                            mySystem, 
			    "Incorrect password."));
		    myIsDisconnected = true;
		    return;
		}

		// this makes the initial spacebar login stuff appear
		// rather than getting swallowed as part of the ignore,
		// below
		typeLine ("/c");
		expectPrompt ("channel");
		typeLine ("\n");

		typeLine ("/*");
		ignoreUntilPrompt ("/*");
		typeLine ("m\n");
		ignoreUntilPrompt ("enter-new-format");
		typeLine ("~~~\\P\\_B_ %u~$`%h~$`%m~~~\n");
		typeLine ("/*");
		ignoreUntilPrompt ("/*");
		typeLine ("a\n");
		ignoreUntilPrompt ("enter-new-format");
		typeLine ("```%u~$`%h~$`%t~$`%M```\n");
		typeLine ("/3");
		ignoreUntilPrompt ("new-value");
		typeLine ("132\n");
		ignoreLine ("Screen width changed.");

		result.send (null);
	    }
	});

	return ((ErrorEvent) result.receive ());
    }

    /**
     * Tell the interactor to do a logout.
     */
    public void doLogout ()
    {
	doRawSend ("/qy\n");
    }

    /**
     * Tell the interactor to do a standard send-expect-send operation.
     *
     * @param send1 the first string to send
     * @param prompt the prompt type to expect
     * @param send2 the second string to send
     */
    public void doSendExpectSend (final String send1, final String prompt, 
				  final String send2)
    {
	myInput.send (new Runnable ()
	{
	    public void run ()
	    {
		typeLine (send1);
		expectPrompt (prompt);
		typeLine (send2);
	    }
	});
    }

    /**
     * Tell the interactor to do a send-expect-send operation and
     * then expect another prompt and do another send, but if the
     * prompt doesn't come, don't do the following send.
     *
     * @param send1 the first string to send
     * @param prompt1 the first prompt type to expect
     * @param send2 the second string to send
     * @param prompt2 the second prompt type to expect
     * @param send3 the third string to send
     */
    public void doSendExpectSend2 (final String send1, final String prompt1, 
				   final String send2, final String prompt2,
				   final String send3)
    {
	myInput.send (new Runnable ()
	{
	    public void run ()
	    {
		typeLine (send1);
		expectPrompt (prompt1);
		typeLine (send2);
		promptThenType (prompt2, send3);
	    }
	});
    }

    /**
     * Tell the interactor to send a command and then gather the 
     * responses from the server.
     *
     * @param send the string to send
     * @return an array of lines gathered as the response from the interaction
     */
    public String[] doSendGather (final String send)
    {
	// mailbox used just to get the result back
	final MailBox result = new MailBox ();
	
	myInput.send (new Runnable ()
	{
	    public void run ()
	    {
		typeLine (send);
		result.send (gatherResponse ());
	    }
	});

	return ((String[]) result.receive ());
    }

    /**
     * Tell the interactor to send a command, expect a prompt, send a
     * response, and then gather the responses from the server.
     *
     * @param send1 the first string to send
     * @param prompt the prompt type to expect
     * @param send2 the second string to send
     * @return an array of lines gathered as the response from the interaction
     */
    public String[] doSendExpectSendGather (final String send1,
					    final String prompt,
					    final String send2)
    {
	// mailbox used just to get the result back
	final MailBox result = new MailBox ();
	
	myInput.send (new Runnable ()
	{
	    public void run ()
	    {
		typeLine (send1);
		expectPrompt (prompt);
		typeLine (send2);
		result.send (gatherResponse ());
	    }
	});

	return ((String[]) result.receive ());
    }

    /**
     * Tell the interactor to type a line at the host system (i.e., 
     * expect it to be echoed back).
     *
     * @param line the line to type
     */
    public void doTypeLine (final String line)
    {
	myInput.send (new Runnable ()
	{
	    public void run ()
	    {
		typeLine (line);
	    }
	});
    }

    /**
     * Tell the interactor to just send some raw input to the host system.
     *
     * @param raw the string to send
     */
    public void doRawSend (final String raw)
    {
	myInput.send (new Runnable ()
	{
	    public void run ()
	    {
		rawSend (raw);
	    }
	});
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
                        "Error caught at top of SpaceBar interactor loop.",
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
     * telling the system about speech messages. If a message is received
     * and processed that wasn't a non-speech <code>MessageEvent</code>,
     * this method returns null.
     *
     * @return null-ok; the unprocessed <code>MessageEvent</code>, if any */
    private MessageEvent receiveSystemMessage ()
    {
	MessageEvent result = null;
	Object message = myInput.receive ();

	if (message == myIdleObject)
	{
	    // ignore it
	}
	else if (message instanceof Runnable)
	{
	    myCommands.addElement (message);
	}
	else if (message instanceof MessageEvent)
	{
	    result = (MessageEvent) message;
	    int type = result.getType ();
	    Object arg = result.getArgument ();
	    if (type == MessageEvent.USER_BROADCAST)
	    {
		mySystem.dispatchBroadcast (result);
		result = null;
	    }
	    else if (   (type == MessageEvent.SYSTEM_PRIVATE)
		     && (arg instanceof String)
		     && (myIgnoreLines.size () != 0))
	    {
		String igline = (String) myIgnoreLines.firstElement ();
		if (igline.equals (result.getText ()))
		{
		    myIgnoreLines.removeElementAt (0);
		    result = null;
		}
	    }
	}
	else if (message instanceof Throwable)
	{
	    // a throwable made its way out of the read stream
	    // we take this to mean the connection died
	    beDisconnected ("Connection closed unexpectedly with error:\n" +
			    ((Throwable) message).getMessage ());
	    result = null;
	}
	else
	{
	    mySystem.bugReport (
                new ShouldntHappenException (
                    "SpaceBar interactor got weird message:\n" + 
		    message));
	    result = null;
	}

	return result;
    }

    /**
     * This will receive a message from the input and handle it appropriately.
     * This does everything that receiveSystemMessage does, plus it will
     * set myLastPrompt when prompts are received, and it will send all other
     * chat messages to the main system object.
     */
    private void receiveMessage ()
    {
	MessageEvent message = receiveSystemMessage ();

	if (message == null)
	{
	    return;
	}

	Object arg = message.getArgument ();
	if (arg instanceof SBPromptMessage)
	{
	    if (myLastPrompt != null)
	    {
		mySystem.bugReport (
                    new ShouldntHappenException (
                        "SpaceBar interactor got unexpected prompt: " +
			myLastPrompt));
	    }

	    myLastPrompt = (SBPromptMessage) arg;
	    return;
	}

	mySystem.dispatchBroadcast (message);
    }

    /**
     * This will receive and handle messages until a prompt is received;
     * it then makes sure the prompt is the one that's expected. If
     * it's not, then it throws an exception, otherwise it just returns
     * normally.
     *
     * @param type the prompt type
     */
    private void expectPrompt (String type)
    {
	while (myLastPrompt == null)
	{
	    receiveMessage ();
	}
	
	if (myLastPrompt.getType () != type)
	{
	    String promptStr = myLastPrompt.toString ();
	    myLastPrompt = null;
	    throw new ShouldntHappenException (
                "SpaceBar interactor got unexpected prompt: " + promptStr);
	}

	myLastPrompt = null;
    }

    /**
     * This is like expectPrompt, except system messages are silently
     * eaten. This allows, e.g., hiding "informative instructions" about
     * some of the more esoteric commands.
     *
     * @param type the prompt type
     */
    private void ignoreUntilPrompt (String type)
    {
	while (myLastPrompt == null)
	{
	    MessageEvent msg = receiveSystemMessage ();
	    if (msg == null)
	    {
		continue;
	    }

	    Object arg = msg.getArgument ();
	    if (arg instanceof SBPromptMessage)
	    {
		myLastPrompt = (SBPromptMessage) arg;
		break;
	    }
	}
	
	if (myLastPrompt.getType () != type)
	{
	    String promptStr = myLastPrompt.toString ();
	    myLastPrompt = null;
	    throw new ShouldntHappenException (
                "SpaceBar interactor got unexpected prompt: " + promptStr);
	}

	myLastPrompt = null;
    }

    /**
     * This expects the possibility of the given prompt. If the prompt is
     * received within the given number of lines, it returns true. If only
     * non-prompt messages are received, it returns false and sends along
     * the messages for normal processing. If a prompt other than the
     * expected one is received, it immediately returns false, leaving
     * the prompt in the <code>myLastPrompt</code> variable.
     *
     * @param count the count of lines to try 
     * @param type the prompt type 
     */
    private boolean gotPrompt (int count, String type)
    {
	while (count > 0)
	{
	    count--;
	    
	    if (myLastPrompt != null)
	    {
		if (myLastPrompt.getType () != type)
		{
		    return false;
		}
		myLastPrompt = null;
		return true;
	    }

	    receiveMessage ();
	}

	return false;
    }

    /**
     * This expects either the given prompt or a SYSTEM_PRIVATE line.
     * If the prompt is received, it types the given line. If a SYSTEM_PRIVATE
     * is received, it just sends that back to the system. (It was presumably
     * an abort type message such as "flooby not logged in.").
     *
     * @param type the prompt type
     * @param line the line to send
     */
    private void promptThenType (String type, String line)
    {
	while (myLastPrompt == null)
	{
	    MessageEvent msg = receiveSystemMessage ();
	    if (msg == null)
	    {
		continue;
	    }

	    Object arg = msg.getArgument ();
	    if (arg instanceof SBPromptMessage)
	    {
		myLastPrompt = (SBPromptMessage) arg;
		break;
	    }

	    if (msg.getType () == MessageEvent.SYSTEM_PRIVATE)
	    {
		mySystem.dispatchBroadcast (msg);
		return;
	    }
	}

	if (myLastPrompt.getType () != type)
	{
	    String promptStr = myLastPrompt.toString ();
	    myLastPrompt = null;
	    throw new ShouldntHappenException (
                "SpaceBar interactor got unexpected prompt: " + promptStr);
	}

	myLastPrompt = null;
	typeLine (line);
    }

    /**
     * Tell the system that the next line to be ignored is as given.
     *
     * @param line hte line to ignore
     */
    private void ignoreLine (String line)
    {
	int len = line.length ();
	if (line.charAt (len - 1) == '\n')
	{
	    line = line.substring (0, len - 1);
	}

	myIgnoreLines.addElement (line);
    }

    /**
     * Send the given string to the host system as typing. In particular,
     * expect it to be echoed back out, and if it doesn't end with newline,
     * expect a newline too.
     *
     * @param line the line to send
     */
    private void typeLine (String line)
    {
	ignoreLine (line);
	rawSend (line);
    }

    /**
     * Gather a response from the host. Since SpaceBar sucks, we cheesily
     * use an invalid command as the delimiter to know that the response
     * is done.
     *
     * @return the array of lines received as a response
     */
    private String[] gatherResponse ()
    {
	Vector rvec = new Vector ();

	rawSend ("/_");

	for (;;)
	{
	    if (myIsDisconnected)
	    {
		break;
	    }
	    MessageEvent msg = receiveSystemMessage ();
	    if (msg != null)
	    {
		if (msg.getType () == MessageEvent.SYSTEM_PRIVATE)
		{
		    String line = msg.getText ();
		    if (line.equals ("/_"))
		    {
			break;
		    }
		    rvec.addElement (line);
		}
		else
		{
		    mySystem.dispatchBroadcast (msg);
		}
	    }
	}

	ignoreLine ("Invalid command, type /? for help.");

	String[] result = new String[rvec.size ()];
	rvec.copyInto (result);
	return result;
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
	    String msg = ex.getMessage ();
	    // ignore a "success" "error"
	    if (! msg.equals ("Success"))
	    {
		beDisconnected ("Trouble sending to connection:\n" + msg);
	    }
	}
    }
}
