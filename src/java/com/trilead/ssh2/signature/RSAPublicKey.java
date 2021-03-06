package com.trilead.ssh2.signature;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * RSAPublicKey.
 * 
 * @author Christian Plattner, plattner@trilead.com
 * @version $Id: RSAPublicKey.java,v 1.1 2007/10/15 12:49:57 cplattne Exp $
 */
public class RSAPublicKey implements Serializable
{
	BigInteger e;
	BigInteger n;

	public RSAPublicKey() {
	}

	public RSAPublicKey(BigInteger e, BigInteger n)
	{
		this.e = e;
		this.n = n;
	}

	public BigInteger getE()
	{
		return e;
	}

	public BigInteger getN()
	{
		return n;
	}
}