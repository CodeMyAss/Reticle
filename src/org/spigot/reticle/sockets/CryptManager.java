package org.spigot.reticle.sockets;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class CryptManager {
	/**
	 * Creates new random shared key
	 * @return
	 */
	public static SecretKey createNewSharedKey() {
		try {
			KeyGenerator gen = KeyGenerator.getInstance("AES");
			gen.init(128);
			return gen.generateKey();
		} catch (NoSuchAlgorithmException var1) {
			throw new Error(var1);
		}
	}

	protected static KeyPair createNewKeyPair() {
		try {
			KeyPairGenerator var0 = KeyPairGenerator.getInstance("RSA");
			var0.initialize(1024);
			return var0.generateKeyPair();
		} catch (NoSuchAlgorithmException var1) {
			var1.printStackTrace();
			System.err.println("Key pair generation failed!");
			return null;
		}
	}

	/**
	 * Calculates server hash
	 * @param ServerId
	 * @param publicKey
	 * @param secretKey
	 * @return
	 */
	public static byte[] getServerIdHash(String ServerId, PublicKey publicKey, SecretKey secretKey) {
		try {
			return digestOperation("SHA-1", new byte[][] { ServerId.getBytes("ISO_8859_1"), secretKey.getEncoded(), publicKey.getEncoded() });
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	protected static byte[] digestOperation(String Algorithm, byte[]... bytes) {
		try {
			MessageDigest digest = MessageDigest.getInstance(Algorithm);
			byte[][] params = bytes;
			int len = bytes.length;

			for (int i = 0; i < len; ++i) {
				byte[] encr = params[i];
				digest.update(encr);
			}
			return digest.digest();
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

	/**
	 * Decodes public key
	 * @param key
	 * @return
	 */
	public static PublicKey decodePublicKey(byte[] key) {
		try {
			X509EncodedKeySpec keyspec = new X509EncodedKeySpec(key);
			KeyFactory keyfact = KeyFactory.getInstance("RSA");
			return keyfact.generatePublic(keyspec);
		} catch (NoSuchAlgorithmException var3) {
		} catch (InvalidKeySpecException var4) {
		}
		System.err.println("Public key reconstitute failed!");
		return null;
	}

	protected static SecretKey decryptSharedKey(PrivateKey privateKey, byte[] sharedKey) {
		return new SecretKeySpec(decryptData(privateKey, sharedKey), "AES");
	}

	/**
	 * Encrypts data
	 * @param key
	 * @param data
	 * @return
	 */
	public static byte[] encryptData(Key key, byte[] data) {
		return cipherOperation(1, key, data);
	}

	protected static byte[] decryptData(Key key, byte[] data) {
		return cipherOperation(2, key, data);
	}

	private static byte[] cipherOperation(int op, Key key, byte[] data) {
		try {
			return createTheCipherInstance(op, key.getAlgorithm(), key).doFinal(data);
		} catch (IllegalBlockSizeException e) {
		} catch (BadPaddingException e) {
		}
		System.err.println("Cipher data failed!");
		return null;
	}

	private static Cipher createTheCipherInstance(int code, String String, Key Key) {
		try {
			Cipher cipher = Cipher.getInstance(String);
			cipher.init(code, Key);
			return cipher;
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.err.println("Cipher creation failed!");
		return null;
	}
}
