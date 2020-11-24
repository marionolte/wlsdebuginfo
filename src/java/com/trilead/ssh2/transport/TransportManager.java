
package com.trilead.ssh2.transport;

import com.trilead.ssh2.crypto.Base64;
import com.trilead.ssh2.crypto.CryptoWishList;
import com.trilead.ssh2.crypto.cipher.BlockCipher;
import com.trilead.ssh2.crypto.digest.MAC;
import com.trilead.ssh2.log.Logger;
import com.trilead.ssh2.packets.PacketDisconnect;
import com.trilead.ssh2.packets.Packets;
import com.trilead.ssh2.packets.TypesReader;
import com.trilead.ssh2.util.Tokenizer;
import com.trilead.ssh2.ConnectionInfo;
import com.trilead.ssh2.ConnectionMonitor;
import com.trilead.ssh2.DHGexParameters;
import com.trilead.ssh2.ProxyData;
import com.trilead.ssh2.ServerHostKeyVerifier;
import com.trilead.ssh2.transport.SocketFactory;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.Vector;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/*
 * Yes, the "standard" is a big mess. On one side, the say that arbitary channel
 * packets are allowed during kex exchange, on the other side we need to blindly
 * ignore the next _packet_ if the KEX guess was wrong. Where do we know from that
 * the next packet is not a channel data packet? Yes, we could check if it is in
 * the KEX range. But the standard says nothing about this. The OpenSSH guys
 * block local "normal" traffic during KEX. That's fine - however, they assume
 * that the other side is doing the same. During re-key, if they receive traffic
 * other than KEX, they become horribly irritated and kill the connection. Since
 * we are very likely going to communicate with OpenSSH servers, we have to play
 * the same game - even though we could do better.
 * 
 * btw: having stdout and stderr on the same channel, with a shared window, is
 * also a VERY good idea... =(
 */

/**
 * TransportManager.
 * 
 * @author Christian Plattner, plattner@trilead.com
 * @version $Id: TransportManager.java,v 1.2 2008/04/01 12:38:09 cplattne Exp $
 */
public class TransportManager extends GenericTransportManager
{
	Socket sock = new Socket();

	/**
	 * There were reports that there are JDKs which use
	 * the resolver even though one supplies a dotted IP
	 * address in the Socket constructor. That is why we
	 * try to generate the InetAdress "by hand".
	 * 
	 * @param host
	 * @return the InetAddress
	 * @throws UnknownHostException
	 */
	static InetAddress createInetAddress(String host) throws UnknownHostException
	{
		/* Check if it is a dotted IP4 address */

		InetAddress addr = parseIPv4Address(host);

		if (addr != null)
			return addr;

		return InetAddress.getByName(host);
	}

	private static InetAddress parseIPv4Address(String host) throws UnknownHostException
	{
		if (host == null)
			return null;

		String[] quad = Tokenizer.parseTokens(host, '.');

		if ((quad == null) || (quad.length != 4))
			return null;

		byte[] addr = new byte[4];

		for (int i = 0; i < 4; i++)
		{
			int part = 0;

			if ((quad[i].length() == 0) || (quad[i].length() > 3))
				return null;

			for (int k = 0; k < quad[i].length(); k++)
			{
				char c = quad[i].charAt(k);

				/* No, Character.isDigit is not the same */
				if ((c < '0') || (c > '9'))
					return null;

				part = part * 10 + (c - '0');
			}

			if (part > 255) /* 300.1.2.3 is invalid =) */
				return null;

			addr[i] = (byte) part;
		}

		return InetAddress.getByAddress(host, addr);
	}

	public TransportManager(String host, int port) throws IOException
	{
		super(host, port);
	}

	protected void establishConnection(ProxyData proxyData, int connectTimeout) throws java.io.IOException
	{
		sock = SocketFactory.open(hostname, port, proxyData, connectTimeout);
	}

	public void setTcpNoDelay(boolean state) throws IOException
	{
		sock.setTcpNoDelay(state);
	}

	public void setSoTimeout(int timeout) throws IOException
	{
		sock.setSoTimeout(timeout);
	}

	protected void transportClose() throws IOException {
		sock.close();
	}

	protected InputStream getInputStream() throws IOException {
		return sock.getInputStream();
	}

	protected OutputStream getOutputStream() throws IOException {
		return sock.getOutputStream();
	}

	public ClientServerHello getClientServerHello() {
		return myCsh;
	}
}
