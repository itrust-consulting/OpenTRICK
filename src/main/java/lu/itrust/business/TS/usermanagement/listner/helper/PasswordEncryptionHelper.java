/**
 * 
 */
package lu.itrust.business.TS.usermanagement.listner.helper;

import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author eomar
 *
 */
public final class PasswordEncryptionHelper {

	public static String password;

	public void setPassword(String passwrd) {
		PasswordEncryptionHelper.password = passwrd;
	}

	public static EncryptedPassword encrypt(String source, String salt) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, generateKey(salt));
		String encrypted = Base64.getEncoder().encodeToString(cipher.doFinal(source.getBytes("UTF-8")));
		AlgorithmParameters algorithmParameters = cipher.getParameters();
		String iv = Base64.getEncoder().encodeToString(algorithmParameters.getParameterSpec(IvParameterSpec.class).getIV());
		return new EncryptedPassword(encrypted, iv);

	}

	public static String decrypt(EncryptedPassword encryptedPassword, String salt) throws Exception {
		return decrypt(encryptedPassword.getEncryption(), salt, encryptedPassword.getIv());
	}

	public static String decrypt(String source, String salt, String ivKey) throws Exception {
		byte[] cipherText = Base64.getDecoder().decode(source);
		byte[] iv = Base64.getDecoder().decode(ivKey);
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, generateKey(salt), new IvParameterSpec(iv));
		return new String(cipher.doFinal(cipherText), "UTF-8");
	}

	private static SecretKey generateKey(String salt) throws InvalidKeySpecException, NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchPaddingException {
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes("UTF-8"), 65536, 256);
		SecretKey secretKey = factory.generateSecret(spec);
		return new SecretKeySpec(secretKey.getEncoded(), "AES");
	}

}
