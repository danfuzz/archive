// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.uberchat.gui;

import javax.swing.event.TreeSelectionListener;

/**
 * This interface is for those components that contain a tree to
 * interact with.
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public interface ContainsTree
{
    /**
     * Add a TreeSelectionListener to listen to our embedded tree.
     *
     * @param listener the listener to add
     */
    public void addTreeSelectionListener (TreeSelectionListener listener);

    /**
     * Remove a TreeSelectionListener to listen from our embedded tree.
     *
     * @param listener the listener to remove
     */
    public void removeTreeSelectionListener (TreeSelectionListener listener);

    /**
     * Clear the selection of the tree, if any.
     */
    public void clearSelection ();
}

