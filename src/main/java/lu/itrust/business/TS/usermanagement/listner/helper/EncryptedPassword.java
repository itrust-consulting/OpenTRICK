package lu.itrust.business.TS.usermanagement.listner.helper;

public class EncryptedPassword {

	
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

	private String encryption;
	
	private String iv;

	
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

}
