package lu.itrust.business.ts.usermanagement.listner.helper;

import org.apache.commons.lang3.StringUtils;

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

	public String toMerge() {
		return iv == null || iv.isEmpty() || encryption == null || encryption.isEmpty() ? encryption
				: String.format(CONCAT_FORMAT, iv.length(), iv, encryption);
	}

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
