package org.spigot.reticle.sockets;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
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
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;


public class CryptManager {
	public static SecretKey createNewSharedKey() {
		try {
			KeyGenerator var0 = KeyGenerator.getInstance("AES");
			var0.init(128);
			return var0.generateKey();
		} catch (NoSuchAlgorithmException var1) {
			throw new Error(var1);
		}
	}

	public static KeyPair createNewKeyPair() {
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

	public static byte[] getServerIdHash(String ServerId, PublicKey publicKey, SecretKey secretKey) {
		try {
			return digestOperation("SHA-1", new byte[][] { ServerId.getBytes("ISO_8859_1"), secretKey.getEncoded(), publicKey.getEncoded() });
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public static byte[] digestOperation(String Algorithm, byte[]... bytes) {
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

	public static SecretKey decryptSharedKey(PrivateKey privateKey, byte[] sharedKey) {
		return new SecretKeySpec(decryptData(privateKey, sharedKey), "AES");
	}

	public static byte[] encryptData(Key key, byte[] data) {
		return cipherOperation(1, key, data);
	}

	public static byte[] decryptData(Key key, byte[] data) {
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

	private static BufferedBlockCipher createBufferedBlockCipher(boolean par0, Key key) {
		BufferedBlockCipher var2 = new BufferedBlockCipher(new CFBBlockCipher(new AESFastEngine(), 8));
		var2.init(par0, new ParametersWithIV(new KeyParameter(key.getEncoded()), key.getEncoded(), 0, 16));
		return var2;
	}

	public static OutputStream encryptOuputStream(SecretKey par0SecretKey, OutputStream par1OutputStream) {
		return new org.bouncycastle.crypto.io.CipherOutputStream(par1OutputStream, createBufferedBlockCipher(true, par0SecretKey));
	}

	public static org.bouncycastle.crypto.io.CipherInputStream decryptInputStream(SecretKey par0SecretKey, InputStream par1InputStream) {
		return new org.bouncycastle.crypto.io.CipherInputStream(par1InputStream, createBufferedBlockCipher(false, par0SecretKey));
	}

	private static Cipher createTheCipherInstance(int p_75886_0_, String p_75886_1_, Key p_75886_2_) {
		try {
			Cipher var3 = Cipher.getInstance(p_75886_1_);
			var3.init(p_75886_0_, p_75886_2_);
			return var3;
		} catch (InvalidKeyException var4) {
			var4.printStackTrace();
		} catch (NoSuchAlgorithmException var5) {
			var5.printStackTrace();
		} catch (NoSuchPaddingException var6) {
			var6.printStackTrace();
		}

		System.err.println("Cipher creation failed!");
		return null;
	}

	public static Cipher func_151229_a(int p_151229_0_, Key p_151229_1_) {
		try {
			Cipher var2 = Cipher.getInstance("AES/CFB8/NoPadding");
			var2.init(p_151229_0_, p_151229_1_, new IvParameterSpec(p_151229_1_.getEncoded()));
			return var2;
		} catch (GeneralSecurityException var3) {
			throw new RuntimeException(var3);
		}
	}
}
