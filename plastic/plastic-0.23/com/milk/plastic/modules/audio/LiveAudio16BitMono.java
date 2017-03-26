package com.milk.plastic.modules.audio;

import com.milk.plastic.ports.DoublePort;
import com.milk.plastic.iface.Factory;
import com.milk.plastic.iface.PlasticException;
import com.milk.plastic.iface.Ref;
import com.milk.plastic.iface.Template;
import com.milk.plastic.util.BaseModule;
import java.util.Map;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * Module for live audio with one channel of 16 bits per sample.
 *
 * <p>Copyright (C) 2000, Dan Bornstein, danfuzz@milk.com. All Rights 
 * Reserved. (Shrill TV degreaser.)</p>
 * 
 * <p>This file is part of the MILK Kodebase. The contents of this file are
 * subject to the MILK Kodebase Public License; you may not use this file
 * except in compliance with the License. A copy of the MILK Kodebase Public
 * License has been included with this distribution, and may be found in the
 * file named "LICENSE.html". You may also be able to obtain a copy of the
 * License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!</p>
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
final public class LiveAudio16BitMono
extends BaseModule
{
    /** sample rate in Hz */
    private int myClock;

    /** size in samples of the buffer to request */
    private int myBufSize;

    /** buffer for an output sample */
    private byte[] myBuffer;

    /** null-ok; the input to sample, set in {@link #bind1} and
     * <code>null</code>ed out in {@link #reset1} */
    private DoublePort myInPort;

    /** null-ok; the line to send audio data to */
    private SourceDataLine myLine;

    // ------------------------------------------------------------------------
    // constructor
    
    /**
     * Construct an instance.
     *
     * @param factory the factory which spawned this instance
     * @param template the full template for this instance
     * @param args the arguments which this instance was constructed from 
     */
    public LiveAudio16BitMono (Factory factory, Template template, Map args)
    {
	super (factory, template, args);

	myClock = ((Integer) args.get ("clock")).intValue ();
	myBufSize = ((Integer) args.get ("bufSize")).intValue ();
	myBuffer = new byte[2];

	// augment the type restriction on the input
	Ref newRef = ((Ref) getField ("in_0")).withType (DoublePort.class);
	setField ("in_0", newRef);
    }

    // ------------------------------------------------------------------------
    // public methods the superclass requires us to implement

    // interface's comment suffices
    public void tick ()
    {
	short value = (short) (myInPort.getDouble () * 32767);

	byte[] buf = myBuffer;
	buf[0] = (byte) value;
	buf[1] = (byte) (value >> 8);

	myLine.write (buf, 0, 2);
    }

    // ------------------------------------------------------------------------
    // protected methods the superclass requires us to implement

    // superclass's comment suffices
    protected void bind1 ()
    {
	myInPort = (DoublePort) getBoundObject ("in_0");

	AudioFormat format = new AudioFormat (myClock, 16, 1, true, false);

	SourceDataLine line;
	DataLine.Info info = new DataLine.Info (SourceDataLine.class, format);
	if (! AudioSystem.isLineSupported (info)) 
	{
	    throw new PlasticException (
                "DataLine format (" + info + ") not supported.");
	}

	int bufBytes = myBufSize * 2; // 2 bytes/frame

	try 
	{
	    myLine = (SourceDataLine) AudioSystem.getLine (info);
	    myLine.open (format, bufBytes);
	} 
	catch (LineUnavailableException ex) 
	{
	    throw new PlasticException ("Trouble opening line", ex);
	}

	myLine.start ();
    }

    // superclass's comment suffices
    protected void reset1 ()
    {
	myInPort = null;

	if (myLine != null)
	{
	    myLine.flush ();
	    myLine.stop ();
	    myLine.close ();
	    myLine = null;
	}
    }
}

