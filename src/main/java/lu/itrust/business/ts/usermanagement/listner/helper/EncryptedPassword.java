package lu.itrust.business.ts.usermanagement.listner.helper;

import org.apache.commons.lang3.StringUtils;

/**
 * The EncryptedPassword class represents an encrypted password along with its initialization vector (IV).
 * It provides methods to get and set the encryption and IV, as well as methods to merge and split the encrypted password.
 */
public class EncryptedPassword {

	private final static String CONCAT_FORMAT = "{ENCRYPT:%d}%s%s";

	private String encryption;

	private String iv;

	/**
	 * 
	 */
	public EncryptedPassword() {
	}

	/**
	 * @param encryption
	 * @param iv
	 */
	public EncryptedPassword(String encryption, String iv) {
		this.encryption = encryption;
		this.iv = iv;
	}

	public String getIv() {
		return iv;
	}

	public void setIv(String iv) {
		this.iv = iv;
	}

	public String getEncryption() {
		return encryption;
	}

	public void setEncryption(String encryption) {
		this.encryption = encryption;
	}

	/**
	 * Returns the merged string representation of the IV and encryption values.
	 * If either the IV or encryption value is null or empty, it returns the encryption value as is.
	 * Otherwise, it returns a formatted string that concatenates the length of the IV, the IV value, and the encryption value.
	 *
	 * @return the merged string representation of the IV and encryption values
	 */
	public String toMerge() {
		return iv == null || iv.isEmpty() || encryption == null || encryption.isEmpty() ? encryption
				: String.format(CONCAT_FORMAT, iv.length(), iv, encryption);
	}

	/**
	 * Represents an encrypted password.
	 * This class provides methods to parse and create encrypted passwords.
	 */
	public static EncryptedPassword fromMerge(String value) {
		try {
			if (value == null || value.isEmpty())
				return null;
			final int begin = value.indexOf('{'), end = value.indexOf('}') + 1;
			if (begin != 0 || end == 0)
				return null;
			final String header = value.substring(begin, end);
			final String content = value.substring(end);
			final int ivLength = Integer.parseInt(StringUtils.getDigits(header));
			return new EncryptedPassword(content.substring(ivLength), content.substring(0, ivLength));
		} catch (NumberFormatException e) {
			return null;
		}
	}

}
