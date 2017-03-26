// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.objed.test;

import com.milk.objed.AutoUpdater;
import com.milk.objed.Editor;
import com.milk.objed.DirectValueEditor;
import com.milk.objed.FieldValueEditor;
import com.milk.objed.FixedFieldsEditor;
import com.milk.objed.IntegerTextEditor;
import com.milk.objed.StringTextEditor;
import com.milk.objed.gui.EditorControl;

/**
 * Simple test for the GUI stuff.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public class GuiTest
{
    public int myVal = 0;

    static public void main (String[] args)
    {
	new GuiTest().doit ();
    }

    public void doit ()
    {
	EditorControl control = new EditorControl ();

	DirectValueEditor dve1 = 
	    new DirectValueEditor ("Happy String",
				   "This is the happiest string.",
				   true,
				   "I am so happy.",
				   String.class,
				   false);

	StringTextEditor e1 = new StringTextEditor (dve1);
	e1.setLengthRestrictions (5, 50, 20);

	FieldValueEditor fve2 =
	    new FieldValueEditor ("The Count",
				  "This is the count used for Happy String.",
				  true,
				  false,
				  this,
				  "myVal");

	IntegerTextEditor e2 = new IntegerTextEditor (fve2);
	e2.setValueRestrictions (0, Integer.MAX_VALUE);

	FixedFieldsEditor e3 = 
	    new FixedFieldsEditor ("Both",
				   "Two editors in one!",
				   new Editor[] { e1, e2 });

	AutoUpdater au = new AutoUpdater ();
	au.add (fve2);
	au.setFrequency (3000);

	control.display (e1);
	control.display (e2);
	control.display (e3);
	
	for (;;)
	{
	    myVal++;
	    try
	    {
		Thread.sleep (250);
	    }
	    catch (InterruptedException ex)
	    {
		// ignore it
	    }
	    dve1.setDescription ("This is the happiest string. " + myVal);
	    dve1.setMutability (((myVal / 50) % 2) == 0);
	}
    }
}
