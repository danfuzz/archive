// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.gui;

import com.milk.util.ShouldntHappenException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 * This is just a simple panel which shows any text spit at it through
 * the streams it provides for such spitting.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public class TextOutputPanel
extends JPanel
{
    /** minimum length of buffer to dump without waiting for more */
    private static final int NO_WAIT_BUFFER_SIZE = 500;

    /** amount of time (in msec) to wait for more buffer output */
    private static final int WAIT_TIME = 250;

    /** null-ok; the window we are in, if any */
    private Window myWindow;

    /** null-ok; the <code>OutputStream</code> for writing to us */
    private OutputStream myOutputStream;

    /** null-ok; the <code>Writer</code> for writing to us */
    private Writer myWriter;

    /** the buffer of unappended text */
    private StringBuffer myToBeAppended;

    /** the thread to do all the text appending */
    private Thread myThread;

    /** the document to use */
    DefaultStyledDocument myDocument;

    /** the pane to show the document in */
    JTextPane myDocumentPane;

    /** the scroll pane to put the text pane in */
    private JScrollPane myScrollPane;

    /** the style context to use */
    private StyleContext myStyles;

    /** the "normal" style */
    private Style myNormalStyle;

    /**
     * Construct a <code>TextOutputPanel</code>.
     *
     * @param window the window the panel will be put in
     */
    public TextOutputPanel (Window window)
    {
	myWindow = window;

	setLayout (new BorderLayout ());

	// BUG--this should be based on user prefs
	myStyles = new StyleContext ();

	Style baseStyle = myStyles.addStyle ("base", null);
	StyleConstants.setFontFamily (baseStyle, "DialogInput");
	StyleConstants.setFontSize (baseStyle, 10);
	StyleConstants.setSpaceAbove (baseStyle, 0.0F);
	StyleConstants.setSpaceBelow (baseStyle, 0.0F);
	StyleConstants.setBackground (baseStyle, new Color (0xc0c0c0));
	myNormalStyle = myStyles.addStyle ("normal", baseStyle);

	// the document which will contain all the output
	myDocument = new DefaultStyledDocument ();

	// the pane to hold the document
	myDocumentPane = new JTextPane (myDocument);
	myDocumentPane.setEditable (false);
	myDocumentPane.addComponentListener (new DocResizeListener ());

	// the scrollpane to hold the document pane
	myScrollPane = new JScrollPane (myDocumentPane);
	myScrollPane.setPreferredSize (new Dimension (500, 280)); // BUG
	add ("Center", myScrollPane);

	// the to-be-appended text
	myToBeAppended = new StringBuffer ();

	// the thread to service it
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
     * Get the <code>OutputStream</code> which can be written to to get
     * stuff to show up in this panel. Note that, if possible, you should
     * use <code>getWriter()</code> instead of this method, but the Java
     * <code>System.{out,err}</code> legacy forces the use of
     * <code>OutputStream</code>s. It just sucks that way.
     *
     * @return the <code>OutputStream</code> 
     */
    public OutputStream getOutputStream ()
    {
	if (myOutputStream == null)
	{
	    myOutputStream = new MyOutputStream ();
	}

	return myOutputStream;
    }

    /**
     * Get the <code>Writer</code> which can be written to to get
     * stuff to show up in this panel.
     *
     * @return the <code>Writer</code> 
     */
    public Writer getWriter ()
    {
	if (myWriter == null)
	{
	    myWriter = new MyWriter ();
	}

	return myWriter;
    }

    /**
     * Make and return a frame showing a <code>TextOuputPanel</code>.
     *
     * @param title the title for the frame
     * @return the frame
     */
    public static JFrame makeFrame (String title)
    {
	// make the frame and put a new TextOutputPanel in it
	JFrame frame = new JFrame (title);
	final TextOutputPanel panel = new TextOutputPanel (frame);
	frame.setContentPane (panel);
	frame.setDefaultCloseOperation (frame.HIDE_ON_CLOSE);

	// make the menubar for the frame
	JMenuBar menuBar = new JMenuBar ();
	frame.setJMenuBar (menuBar);

	JMenu fileMenu = new JMenu ("File");
	menuBar.add (fileMenu);
	fileMenu.add (new AbstractAction ("Save As...")
	{
	    public void actionPerformed (ActionEvent event)
	    {
		panel.saveAs ();
	    }
	});

	JMenu editMenu = new JMenu ("Edit");
	menuBar.add (editMenu);
	editMenu.add (new AbstractAction ("Copy")
	{
	    public void actionPerformed (ActionEvent event)
	    {
		panel.myDocumentPane.copy ();
	    }
	});
	editMenu.add (new AbstractAction ("Select All")
	{
	    public void actionPerformed (ActionEvent event)
	    {
		panel.myDocumentPane.selectAll ();
	    }
	});
	editMenu.add (new AbstractAction ("Clear")
	{
	    public void actionPerformed (ActionEvent event)
	    {
		panel.clear ();
	    }
	});

	frame.pack ();
	return frame;
    }

    /**
     * Given a frame showing one, get the <code>TextOutputPanel</code>.
     *
     * @return the panel
     */
    public static TextOutputPanel getPanel (JFrame frame)
    {
	return (TextOutputPanel) frame.getContentPane ();
    }

    // ------------------------------------------------------------------------
    // Private helper methods

    /**
     * This is the method which runs in the thread to cause writes
     * to go through to the actual document.
     */
    private void threadRun ()
    {
	for (;;)
	{
	    String str;

	    synchronized (myToBeAppended)
	    {
		int len = myToBeAppended.length ();
		if (len < NO_WAIT_BUFFER_SIZE)
		{
		    try
		    {
			if (len == 0)
			{
			    myToBeAppended.wait ();
			}
			else
			{
			    myToBeAppended.wait (WAIT_TIME);
			}
		    }
		    catch (InterruptedException ex)
		    {
			// ignore it
		    }
		    int newlen = myToBeAppended.length ();
		    if (newlen == 0)
		    {
			continue;
		    }
		    if (   (newlen > len)
			&& (newlen < NO_WAIT_BUFFER_SIZE))
		    {
			continue;
		    }
		}
		str = myToBeAppended.toString ();
		myToBeAppended.setLength (0);
	    }

	    synchronized (myDocument)
	    {
		try
		{
		    int insertAt = myDocument.getLength ();
		    myDocument.insertString (insertAt, str, myNormalStyle);
		    myDocument.setLogicalStyle (insertAt + 1, myNormalStyle);
		}
		catch (BadLocationException ex)
		{
		    new ShouldntHappenException (ex).printStackTrace ();
		}
	    }

	    if (myWindow != null)
	    {
		AsEvent.setVisible (myWindow, true);
	    }
	}
    }

    /**
     * Save the document to a file as specified by the user
     */
    private void saveAs ()
    {
	JFileChooser chooser = new JFileChooser ();
	chooser.setFileSelectionMode (chooser.FILES_ONLY);
	int result = chooser.showSaveDialog (this);
	if (result == chooser.CANCEL_OPTION)
	{
	    return;
	}

	File f = chooser.getSelectedFile ();
	if (f.exists ())
	{
	    try
	    {
		result = 
		    JOptionPane.showConfirmDialog (
                        this,
			"Overwrite existing file \"" + f.getCanonicalPath () + 
			"\"?",
			"File Exists",
			JOptionPane.YES_NO_OPTION);

		if (result != JOptionPane.YES_OPTION)
		{
		    return;
		}

		f.delete ();
	    }
	    catch (IOException ex)
	    {
		throw new ShouldntHappenException (ex);
	    }
	}

	String text;
	synchronized (myDocument)
	{
	    int len = myDocument.getLength ();
	    try
	    {
		text = myDocument.getText (0, len);
	    }
	    catch (BadLocationException ex)
	    {
		throw new ShouldntHappenException (ex);
	    }
	}

	try
	{
	    FileWriter fw = new FileWriter (f);
	    fw.write (text);
	    fw.flush ();
	    fw.close ();
	}
	catch (IOException ex)
	{
	    JOptionPane.showMessageDialog (null,
					   ex.getMessage (),
					   "Write Error",
					   JOptionPane.ERROR_MESSAGE);
	}
    }

    /**
     * Clear the document.
     */
    private void clear ()
    {
	synchronized (myDocument)
	{
	    int len = myDocument.getLength ();
	    try
	    {
		myDocument.remove (0, len);
	    }
	    catch (BadLocationException ex)
	    {
		throw new ShouldntHappenException (ex);
	    }
	}
    }

    /**
     * Append the given string to the document.
     *
     * @param str the string to append
     */
    private void append (String str)
    {
	synchronized (myToBeAppended)
	{
	    myToBeAppended.append (str);
	    myToBeAppended.notifyAll ();
	}
    }

    // ------------------------------------------------------------------------
    // Private helper classes

    /** 
     * This is a <code>ComponentListener</code> that looks for the text
     * pane to be resized and then does the scroll-to-bottom thing. 
     */
    private class DocResizeListener
    extends ComponentAdapter
    {
	public void componentResized (ComponentEvent e)
	{
	    JViewport viewPort = myScrollPane.getViewport ();
	    int viewHeight = myDocumentPane.getHeight ();
	    int extentHeight = viewPort.getHeight ();
	    viewPort.setViewPosition (
                new Point (0, viewHeight - extentHeight));
	}
    }

    /**
     * This is the <code>OutputStream</code> class that knows how to put
     * stuff in this panel.
     */
    private class MyOutputStream
    extends OutputStream
    {
	public void close ()
	throws IOException
	{
	    // ignore it
	}

	public void flush ()
	throws IOException
	{
	    // ignore it
	}

	public void write (int c)
	throws IOException
	{
	    append (new String (new char[] { (char) c }));
	}

	public void write (byte[] b)
	throws IOException
	{
	    append (new String (b));
	}

	public void write (byte[] b, int off, int len)
	throws IOException
	{
	    append (new String (b, off, len));
	}
    }

    /**
     * This is the <code>Writer</code> class that knows how to put
     * stuff in this panel.
     */
    private class MyWriter
    extends Writer
    {
	public void close ()
	throws IOException
	{
	    // ignore it
	}

	public void flush ()
	throws IOException
	{
	    // ignore it
	}

	public void write (int c)
	throws IOException
	{
	    append (new String (new char[] { (char) c }));
	}

	public void write (char[] c)
	throws IOException
	{
	    append (new String (c));
	}

	public void write (char[] c, int off, int len)
	throws IOException
	{
	    append (new String (c, off, len));
	}

	public void write (String s)
	throws IOException
	{
	    append (s);
	}

	public void write (String s, int off, int len)
	throws IOException
	{
	    append (s.substring (off, len));
	}
    }
}
