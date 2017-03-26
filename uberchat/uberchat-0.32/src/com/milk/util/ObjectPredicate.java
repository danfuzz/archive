// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.util;

/**
 * An <code>ObjectPredicate</code> merely knows how to answer a true/false
 * question about individual objects. It is used, for example, to define
 * types for use with collections.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public interface ObjectPredicate
{
    /**
     * Answer the question about the given object.
     *
     * @param obj the object to test
     * @return true or false, depending on the object
     */
    public boolean test (Object obj);
}
