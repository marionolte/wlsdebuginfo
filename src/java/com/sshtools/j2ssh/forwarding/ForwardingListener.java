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
package com.sshtools.j2ssh.forwarding;

import com.sshtools.j2ssh.SshThread;
import com.sshtools.j2ssh.connection.ConnectionProtocol;
import com.sshtools.j2ssh.util.StartStopState;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

import java.io.IOException;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


/**
 *
 *
 * @author $author$
 * @version $Revision: 1.30 $
 */
public abstract class ForwardingListener extends ForwardingConfiguration
    implements Runnable {
//    private static Log log = LogFactory.getLog(ForwardingListener.class);
    private ConnectionProtocol connection;
    private ServerSocket server;
    private Thread thread;
    private boolean listening;

    /**
     * Creates a new ForwardingListener object.
     *
     * @param name
     * @param connection
     * @param addressToBind
     * @param portToBind
     * @param hostToConnect
     * @param portToConnect
     */
    public ForwardingListener(String name, ConnectionProtocol connection,
        String addressToBind, int portToBind, String hostToConnect,
        int portToConnect) {
        super(name, addressToBind, portToBind, hostToConnect, portToConnect);
        final String func=getFunc();
        printf(func,3,"Creating forwarding listener named '" + name + "'");
        this.connection = connection;

        if (log.isDebugEnabled()) {
            printf(func,2,"Address to bind: " + getAddressToBind());
            printf(func,2,"Port to bind: " + String.valueOf(getPortToBind()));
            printf(func,2,"Host to connect: " + hostToConnect);
            printf(func,2,"Port to connect: " + portToConnect);
        }
    }

    /**
     * Creates a new ForwardingListener object.
     *
     * @param connection
     * @param addressToBind
     * @param portToBind
     */
    public ForwardingListener(ConnectionProtocol connection,
        String addressToBind, int portToBind) {
        this(addressToBind + ":" + String.valueOf(portToBind), connection,
            addressToBind, portToBind, "[Specified by connecting computer]", -1);
    }

    /**
     *
     *
     * @return
     */
    public int getLocalPort() {
        return (server == null) ? (-1) : server.getLocalPort();
    }

    /**
     *
     *
     * @return
     */
    public boolean isListening() {
        return listening;
    }

    /**
     *
     */
    @Override
    public void run() {
        final String func=getFunc();
        try {
            printf(func,3,"Starting forwarding listener thread for '" + name + "'");

            //
            //            ServerSocket server = new ServerSocket(getPortToBind(), 50, InetAddress.getByName(getAddressToBind()));
            //server = new ServerSocket(getPortToBind(), 50, InetAddress.getByName(getAddressToBind()));
            Socket socket;

            while (state.getValue() == StartStopState.STARTED) {
                listening = true;
                socket = server.accept();

                if ((state.getValue() == StartStopState.STOPPED) ||
                        (socket == null)) {
                    break;
                }

                printf(func,3,"Connection accepted, creating forwarding channel");

                try {
                    ForwardingSocketChannel channel = createChannel(hostToConnect,
                            portToConnect, socket);
                    channel.bindSocket(socket);

                    if (connection.openChannel(channel)) {
                        printf(func,3,"Forwarding channel for '" + name +
                            "' is open");
                    } else {
                        printf(func,3,"Failed to open forwarding chanel " + name);
                        socket.close();
                    }
                } catch (Exception ex) {
                    printf(func,3,"Failed to open forwarding chanel " + name, ex);

                    try {
                        socket.close();
                    } catch (IOException ioe) {
                    }
                }
            }
        } catch (IOException ioe) {
            /* only warn if the forwarding has not been stopped */
            if (state.getValue() == StartStopState.STARTED) {
                printf(func,3,"Local forwarding listener to " + hostToConnect + ":" +
                    String.valueOf(portToConnect) + " has failed", ioe);
            }
        } finally {
            stop();
        }
    }

    /**
     *
     *
     * @return
     */
    public boolean isRunning() {
        return (thread != null) && thread.isAlive();
    }

    /**
     *
     *
     * @throws IOException
     */
    public void start() throws IOException {
        /* Set the state by calling the super method */
        super.start();

        /* Bind server socket */
        try {
            server = new ServerSocket(getPortToBind(), 50,
                    InetAddress.getByName(getAddressToBind()));
        } catch (IOException ioe) {
            super.stop();
            throw ioe;
        }

        /* Create a thread and start it */
        thread = new SshThread(this, "Forwarding listener", true);

        /* Create a thread and start it */
        thread = new SshThread(this, "Forwarding listener", true);
        thread.start();
    }

    /**
     *
     */
    @Override
    public void stop() {
        /* Set the state by calling the super method */
        super.stop();
        final String func=getFunc();
        try {
            /* Close the server socket */
            if (server != null) {
                server.close();
            }
        } catch (IOException ioe) {
            printf(func,3,"Forwarding listener failed to stop", ioe);
        }

        thread = null;
        listening = false;
    }

    /**
     *
     *
     * @param hostToConnect
     * @param portToConnect
     * @param socket
     *
     * @return
     *
     * @throws ForwardingConfigurationException
     */
    protected abstract ForwardingSocketChannel createChannel(
        String hostToConnect, int portToConnect, Socket socket)
        throws ForwardingConfigurationException;
}
