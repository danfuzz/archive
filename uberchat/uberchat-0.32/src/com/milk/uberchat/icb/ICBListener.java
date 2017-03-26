// Copyright (C) 1992-1999, Dan Bornstein, danfuzz@milk.com. All Rights 
// Reserved. (Shrill TV degreaser.)
//
// This file is part of the MILK Kodebase. The contents of this file are
// subject to the MILK Kodebase Public License; you may not use this file
// except in compliance with the License. A copy of the MILK Kodebase Public
// License has been included with this distribution, and may be found in the
// file named "LICENSE.html". You may also be able to obtain a copy of the
// License at <http://www.milk.com/kodebase/legal/LICENSE.html>. Yum!

package com.milk.uberchat.icb;

import java.util.EventListener;

/**
 * This is the interface for objects that care about <code>ICBEvent</code>s.
 *
 * @see ICBEvent
 *
 * @author Dan Bornstein, danfuzz@milk.com
 * @author Copyright 1999 Dan Bornstein, all rights reserved. 
 */
public interface ICBListener
extends EventListener
{
    /**
     * This is called when the server sends a disconnect packet.
     *
     * @param event the event commemorating the moment
     */
    public void disconnectPacket (ICBEvent event);

    /**
     * This is called when the server sends an error packet.
     *
     * @param event the event commemorating the moment
     */
    public void errorPacket (ICBEvent event);

    /**
     * This is called when the server sends an important packet.
     *
     * @param event the event commemorating the moment
     */
    public void importantPacket (ICBEvent event);

    /**
     * This is called when the server sends a ping packet.
     *
     * @param event the event commemorating the moment
     */
    public void pingPacket (ICBEvent event);

    /**
     * This is called when the server sends a pong packet.
     *
     * @param event the event commemorating the moment
     */
    public void pongPacket (ICBEvent event);

    /**
     * This is called when the server sends a register packet.
     *
     * @param event the event commemorating the moment
     */
    public void registerPacket (ICBEvent event);

    /**
     * This is called when the server sends a who packet.
     *
     * @param event the event commemorating the moment
     */
    public void whoPacket (ICBEvent event);
}

