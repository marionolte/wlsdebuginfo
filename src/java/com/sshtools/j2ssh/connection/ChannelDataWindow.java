/*
 *  SSHTools - Java SSH2 API
 *
 *  Copyright (C) 2002-2003 Lee David Painter and Contributors.
 *
 *  Contributions made by:
 *
 *  Brett Smith
 *  Richard Pernavas
 *  Erwin Bolwidt
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.sshtools.j2ssh.connection;

//import org.apache.commons.logging.Log;

import general.Version;

//import org.apache.commons.logging.LogFactory;


/**
 *
 *
 * @author $author$
 * @version $Revision: 1.19 $
 */
public class ChannelDataWindow extends Version{
    //private static Log log = LogFactory.getLog(ChannelDataWindow.class);
    long windowSpace = 0;

    /**
     * Creates a new ChannelDataWindow object.
     */
    public ChannelDataWindow() {
    }

    /**
     *
     *
     * @return
     */
    public synchronized long getWindowSpace() {
        return windowSpace;
    }

    /**
     *
     *
     * @param count
     *
     * @return
     */
    public synchronized long consumeWindowSpace(int count) {
        if (windowSpace < count) {
            waitForWindowSpace(count);
        }

        windowSpace -= count;

        return windowSpace;
    }

    /**
     *
     *
     * @param count
     */
    public synchronized void increaseWindowSpace(long count) {
        final String func = getFunc();
        if (log.isDebugEnabled()) {
            printf(func,2,"Increasing window space by " + String.valueOf(count));
        }

        windowSpace += count;
        notifyAll();
    }

    /**
     *
     *
     * @param minimum
     */
    public synchronized void waitForWindowSpace(int minimum) {
        final String func = getFunc();
        if (log.isDebugEnabled()) {
            printf(func,2,"Waiting for " + String.valueOf(minimum) +
                " bytes of window space");
        }

        while (windowSpace < minimum) {
            try {
                wait(50);
            } catch (InterruptedException e) {
            }
        }
    }
}
