// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.objed;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Vector;

/**
 * This class merely holds a static helper method to make creating
 * <code>Editor</code>s for public objects easier.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public final class PublicFields
{
    /** This class is not instantiable. */
    private PublicFields ()
    {
	// this space intentionally left blank
    }

    /**
     * Given an object, get all of its public fields, and return an array
     * of <code>ValueEditor</code> objects, one per public field. By
     * default, the label for the field is the same as the variable name,
     * and the description is blank. However, if a static public string
     * exists on the class with the name
     * <code><i>fieldName</i>_label</code>, then the value of <i>that</i>
     * field becomes the label for the <code>ValueEditor</code>. Similarly,
     * the description is taken from
     * <code><i>fieldName</i>_description</code> if that variable exists.
     *
     * @param source the object to edit
     * @param mutability true if the fields should be made mutable (if
     * possible) and false if they should be made immutable
     * @param allowNull whether (true) or not (false) to allow null values
     * for the fields
     * @return an array of <code>ValueEditor</code>s, one per public field
     * of the source object
     */
    static public ValueEditor[] makeFieldEditors (Object source,
						  boolean mutability,
						  boolean allowNull)
    {
	Class cls = source.getClass ();
	Vector result = new Vector ();
	Field[] fields = cls.getFields ();
	
	for (int i = 0; i < fields.length; i++)
	{
	    Field f = fields[i];
	    if (! Modifier.isPublic (f.getModifiers ()))
	    {
		// only do anything for public fields
		continue;
	    }

	    String name = f.getName ();
	    String label = name;
	    String description = "";

	    // find the label and description, if any
	    Class fieldClass = f.getDeclaringClass ();
	    try
	    {
		label = (String) 
		    fieldClass.getDeclaredField (name + "_label").get (null);
	    }
	    catch (IllegalArgumentException ex)
	    {
		// ignore it; just means that the field was the wrong kind
	    }
	    catch (IllegalAccessException ex)
	    {
		// ignore it; just means that the field was the wrong kind
	    }
	    catch (NoSuchFieldException ex)
	    {
		// ignore it; just means that the field didn't exist
	    }

	    try
	    {
		description = (String) 
		    fieldClass.getDeclaredField (name + "_description").
		    get (null);
	    }
	    catch (IllegalArgumentException ex)
	    {
		// ignore it; just means that the field was the wrong kind
	    }
	    catch (IllegalAccessException ex)
	    {
		// ignore it; just means that the field was the wrong kind
	    }
	    catch (NoSuchFieldException ex)
	    {
		// ignore it; just means that the field didn't exist
	    }

	    result.addElement (
                new FieldValueEditor (label, description,
				      mutability, allowNull,
				      source, f));
	}

	ValueEditor[] rarr = new ValueEditor[result.size ()];
	result.copyInto (rarr);
	return rarr;
    }
}


