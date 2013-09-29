/*
 * This code was written by Bear Giles <bgiles@coyotesong.com>and he
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Any contributions made by others are licensed to this project under
 * one or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * 
 * Copyright (c) 2013 Bear Giles <bgiles@coyotesong.com>
 */
package com.coyotesong.security.pbk;

import static org.junit.Assert.assertEquals;

import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.util.ResourceBundle;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.junit.Before;
import org.junit.Test;

/**
 * Class that demonstrates creation of Password-Based Encryption keys using the
 * PBKDF2WithHmacSHA1 algorithm. The class also demostrates the use of a Feistel
 * cipher to merge multiple salts and one possible way to create an IV from a
 * PBEKey and salt.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class PbkTest {
	private static final Provider bc = new BouncyCastleProvider();
	private static final ResourceBundle BUNDLE = ResourceBundle
			.getBundle(PbkTest.class.getName());
	private SecretKey cipherKey;
	private AlgorithmParameterSpec ivSpec;

	/**
	 * Perform Feistel cipher using two 20-byte input buffers. The function F is
	 * the SHA1 digest and there is no key. The cipher will not add entropy -
	 * you still need to have good input! - but it will increase the costs to an
	 * attacker.
	 * 
	 * A typical source for the inputs are 1) the contents of a file (or the
	 * digest of the same) and 2) a hard-coded value.
	 * 
	 * See: http://en.wikipedia.org/wiki/Feistel_cipher
	 * 
	 * @param inputs
	 * @param rounds
	 * @return
	 */
	public static byte[][] feistelSha1Hash(byte[][] inputs, int rounds)
			throws NoSuchAlgorithmException {
		final byte[] left = new byte[20];
		final byte[] right = new byte[20];

		System.arraycopy(inputs[0], 0, left, 0, left.length);
		System.arraycopy(inputs[1], 0, right, 0, right.length);

		final MessageDigest digest = MessageDigest.getInstance("SHA1");
		for (int round = 0; round < rounds; round += 2) {
			final byte[] round1 = digest.digest(right);
			for (int i = 0; i < left.length; i++) {
				left[i] ^= round1[i];
			}
			digest.reset();

			final byte[] round2 = digest.digest(left);
			for (int i = 0; i < right.length; i++) {
				right[i] ^= round2[i];
			}
			digest.reset();
		}

		return new byte[][] { left, right };
	}

	/**
	 * Create salt. Two values are provided to support creation of both a cipher
	 * key and IV from a single password.
	 * 
	 * The 'left' salt is pulled from a file outside of the app context. this
	 * makes it much harder for a compromised app to obtain or modify this
	 * value. You could read it as classloader resource but that's not really
	 * different from the properties file used below. Another possibility is to
	 * load it from a read-only value in a database, ideally one with a
	 * different schema than the rest of the application. (It could even be an
	 * in-memory database such as H2 that contains nothing but keying material,
	 * again initialized from a file outside of the app context.)
	 * 
	 * The 'right' salt is pulled from a properties file. It is possible to use
	 * a base64-encoded value but administration is a lot easier if we just take
	 * an arbitrary string and hash it ourselves. At a minimum it should be a
	 * random mix-cased string of at least (120/5 = 24) characters.
	 * 
	 * The generated salts are equally strong.
	 * 
	 * Implementation note: since this is for demonstration purposes a static
	 * string in used in place of reading an external file.
	 */
	public byte[][] createSalt() throws NoSuchAlgorithmException {
		final MessageDigest digest = MessageDigest.getInstance("SHA1");
		final byte[] left = new byte[20]; // fall back to all zeroes
		final byte[] right = new byte[20]; // fall back to all zeroes

		// load value from file or database.
		// note: we use fixed value for demonstration purposes.
		final String leftValue = "this string should be read from file or database";
		if (leftValue != null) {
			System.arraycopy(digest.digest(leftValue.getBytes()), 0, left, 0,
					left.length);
			digest.reset();
		}

		// load value from resource bundle.
		final String rightValue = BUNDLE.getString("salt");
		if (rightValue != null) {
			System.arraycopy(digest.digest(rightValue.getBytes()), 0, right, 0,
					right.length);
			digest.reset();
		}

		final byte[][] salt = feistelSha1Hash(new byte[][] { left, right },
				1000);

		return salt;
	}

	/**
	 * Create secret key and IV from password.
	 * 
	 * Implementation note: I've believe I've seen other code that can extract
	 * the random bits for the IV directly from the PBEKeySpec but I haven't
	 * been able to duplicate it. It might have been a BouncyCastle extension.
	 * 
	 * @throws Exception
	 */
	public void createKeyAndIv(char[] password) throws SecurityException,
			NoSuchAlgorithmException, InvalidKeySpecException {
		final String algorithm = "PBKDF2WithHmacSHA1";
		final SecretKeyFactory factory = SecretKeyFactory
				.getInstance(algorithm);
		final int derivedKeyLength = 128;
		final int iterations = 10000;

		// create salt
		final byte[][] salt = feistelSha1Hash(createSalt(), 1000);

		// create cipher key
		final PBEKeySpec cipherSpec = new PBEKeySpec(password, salt[0],
				iterations, derivedKeyLength);
		cipherKey = factory.generateSecret(cipherSpec);
		cipherSpec.clearPassword();

		// create IV. This is just one of many approaches. You do
		// not want to use the same salt used in creating the PBEKey.
		try {
			final Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", bc);
			cipher.init(Cipher.ENCRYPT_MODE, cipherKey, new IvParameterSpec(
					salt[1], 0, 16));
			ivSpec = new IvParameterSpec(cipher.doFinal(salt[1], 4, 16));
		} catch (NoSuchPaddingException e) {
			throw new SecurityException("unable to create IV", e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new SecurityException("unable to create IV", e);
		} catch (InvalidKeyException e) {
			throw new SecurityException("unable to create IV", e);
		} catch (BadPaddingException e) {
			throw new SecurityException("unable to create IV", e);
		} catch (IllegalBlockSizeException e) {
			throw new SecurityException("unable to create IV", e);
		}
	}

	/**
	 * Obtain password. Architectually we'll want good "separation of concerns"
	 * and we should get the cipher key and IV from a separate place than where
	 * we use it.
	 * 
	 * This is a unit test so the password is stored in a properties file. In
	 * practice we'll want to get it from JNDI from an appserver, or at least a
	 * file outside of the appserver's directory.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setup() throws Exception {
		createKeyAndIv(BUNDLE.getString("password").toCharArray());
	}

	/**
	 * Test encryption.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testEncryption() throws Exception {
		String plaintext = BUNDLE.getString("plaintext");

		Cipher cipher = Cipher.getInstance(BUNDLE.getString("algorithm"), bc);
		cipher.init(Cipher.ENCRYPT_MODE, cipherKey, ivSpec);
		byte[] actual = cipher.doFinal(plaintext.getBytes());
		assertEquals(BUNDLE.getString("ciphertext"),
				new String(Base64.encode(actual), Charset.forName("UTF-8")));
	}

	/**
	 * Test decryption.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testEncryptionAndDecryption() throws Exception {
		String ciphertext = BUNDLE.getString("ciphertext");

		Cipher cipher = Cipher.getInstance(BUNDLE.getString("algorithm"), bc);
		cipher.init(Cipher.DECRYPT_MODE, cipherKey, ivSpec);
		byte[] actual = cipher.doFinal(Base64.decode(ciphertext));

		assertEquals(BUNDLE.getString("plaintext"),
				new String(actual, Charset.forName("UTF-8")));
	}
}
