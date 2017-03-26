// Copyright (c) 2002 Dan Bornstein, danfuzz@milk.com. All rights 
// reserved, except as follows:
// 
// Permission is hereby granted, free of charge, to any person obtaining a
// copy of this software and associated documentation files (the "Software"),
// to deal in the Software without restriction, including without limitation
// the rights to use, copy, modify, merge, publish, distribute, sublicense,
// and/or sell copies of the Software, and to permit persons to whom the
// Software is furnished to do so, subject to the condition that the above
// copyright notice and this permission notice shall be included in all copies
// or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
// DEALINGS IN THE SOFTWARE.

package com.milk.stu.data;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;

/**
 * Abstract base class for coercers. A corercer knows how to take objects
 * and convert them to a particular class, as well as indicate whether it
 * will work with given objects. This class also has static methods to
 * retrieve coercers for specific types.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 */
abstract public class Coercer
{
    /** non-null; map from classes to coercer instances */
    static private final HashMap theCoercerMap;

    /** non-null; the class that this instance converts to */
    private final Class myTargetClass;

    static
    {
	theCoercerMap = new HashMap (100);
	theCoercerMap.put (BigInteger.class, BigIntegerCoercer.theOne);
	theCoercerMap.put (Boolean.TYPE, BooleanCoercer.theOne);
	theCoercerMap.put (Byte.TYPE, ByteCoercer.theOne);
	theCoercerMap.put (Character.TYPE, CharacterCoercer.theOne);
	theCoercerMap.put (Collection.class, CollectionCoercer.theOne);
	theCoercerMap.put (Double.TYPE, DoubleCoercer.theOne);
	theCoercerMap.put (Float.TYPE, FloatCoercer.theOne);
	theCoercerMap.put (Integer.TYPE, IntegerCoercer.theOne);
	theCoercerMap.put (Long.TYPE, LongCoercer.theOne);
	theCoercerMap.put (Short.TYPE, ShortCoercer.theOne);
	theCoercerMap.put (String.class, StringCoercer.theOne);
    }



    // ------------------------------------------------------------------------
    // static public methods

    /**
     * Get the coercer associated with the given type.
     *
     * @param type non-null; the type to look up
     * @return non-null; the corresponding coercer
     */
    static public Coercer get (Class type)
    {
	Coercer c = (Coercer) theCoercerMap.get (type);

	if (c == null)
	{
	    c = new GenericCoercer (type);
	    theCoercerMap.put (type, c);
	}

	return c;
    }



    // ------------------------------------------------------------------------
    // constructor

    /**
     * Construct an instance.
     *
     * @param targetClass non-null; the class that this instance converts
     * to
     */
    public Coercer (Class targetClass)
    {
	if (targetClass == null)
	{
	    throw new NullPointerException ("targetClass == null");
	}

	myTargetClass = targetClass;
    }


    
    // ------------------------------------------------------------------------
    // public instance methods

    /**
     * Get the target class of this instance.
     *
     * @return non-null; the target class
     */
    final public Class getTargetClass ()
    {
	return myTargetClass;
    }

    /**
     * Get the name of the type represented by this instance.
     *
     * @return non-null; the type name
     */
    final public String getName ()
    {
	return myTargetClass.getName ();
    }
    
    /**
     * Return whether or not this coercer is more specific than the given
     * one. Note that this may return non-partially-ordered results when
     * given a coercer whose domain does not intersect with the domain of
     * this instance. For example, {@link ByteCoercer} and {@link
     * CharacterCoercer} think that they are more specific than each other.
     *
     * @param other non-null; the coercer to compare to
     * @return <code>true</code> if this coercer is more specific than
     * the given one; <code>false</code> if not
     */
    abstract public boolean isMoreSpecificThan (Coercer other);

    /**
     * Return whether or not the given object can be successfully coerced
     * by this instance.
     *
     * @param obj null-ok; the object in question
     * @return whether (<code>true</code>) or not (<code>false</code>) the
     * given object is coerceable by this instance.
     */
    abstract public boolean canCoerce (Object obj);

    /**
     * Coerce the given object. If the object is not coerceable by this
     * instance, then this method will either throw an exception or simply
     * return an incorrect result (which is why it is a good idea to
     * protect calls to this method with calls to {@link #canCoerce}. This
     * method may return the object itself if it is already of the
     * appropriate type.
     *
     * @param obj null-ok; the object to coerce
     * @return null-ok; the coerced version of the object
     */
    abstract public Object coerce (Object obj);
}
