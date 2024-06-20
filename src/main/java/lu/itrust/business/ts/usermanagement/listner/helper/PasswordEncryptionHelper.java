/**
 * 
 */
package lu.itrust.business.ts.usermanagement.listner.helper;

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

import org.springframework.util.StringUtils;


/**
 * The PasswordEncryptionHelper class provides methods for encrypting and decrypting passwords using AES encryption.
 * It also provides a method to check if a given string is encrypted.
 */
public final class PasswordEncryptionHelper {

	/**
	 *
	 */
	private static final String UTF_8 = "UTF-8";

	/**
	 *
	 */
	private static final String AES_CBC_PKCS5_PADDING = "AES/CBC/PKCS5Padding";

	private static final String DEFUALT_SALT = "-*98145RTDD£µ%§/.JHZVW+-*/+";
	
	private static String password;

	public static synchronized void setPassword(String passwrd) {
		PasswordEncryptionHelper.password = passwrd;
	}

	public static EncryptedPassword encrypt(String source, String salt) throws Exception {
		Cipher cipher = Cipher.getInstance(AES_CBC_PKCS5_PADDING);
		cipher.init(Cipher.ENCRYPT_MODE, generateKey(salt));
		String encrypted = Base64.getEncoder().encodeToString(cipher.doFinal(source.getBytes(UTF_8)));
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
		Cipher cipher = Cipher.getInstance(AES_CBC_PKCS5_PADDING);
		cipher.init(Cipher.DECRYPT_MODE, generateKey(salt), new IvParameterSpec(iv));
		return new String(cipher.doFinal(cipherText), UTF_8);
	}

	private static SecretKey generateKey(String salt) throws InvalidKeySpecException, NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchPaddingException {
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		KeySpec spec = new PBEKeySpec(password.toCharArray(), (StringUtils.hasText(salt)?  salt: DEFUALT_SALT).getBytes(UTF_8), 65536, 256);
		SecretKey secretKey = factory.generateSecret(spec);
		return new SecretKeySpec(secretKey.getEncoded(), "AES");
	}

	public static boolean isEncrypted(String source, String salt, String ivKey) {
		try {
			if (!(StringUtils.hasText(source) && StringUtils.hasText(ivKey)))
				return false;
			decrypt(source, salt, ivKey);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
