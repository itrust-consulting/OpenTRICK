/**
 * 
 */
package lu.itrust.business.ts.form;

import lu.itrust.business.ts.model.general.CredentialType;

/**
 * @author eomar
 *
 */
public class UserCredentialForm {
	
	private String name;
	
	private String value;

	private String publicUrl;
	
	private int customer;
	
	private CredentialType type = CredentialType.TOKEN;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getCustomer() {
		return customer;
	}

	public void setCustomer(int customer) {
		this.customer = customer;
	}

	public CredentialType getType() {
		return type;
	}

	public void setType(CredentialType type) {
		this.type = type;
	}

	public String getPublicUrl() {
		return publicUrl;
	}

	public void setPublicUrl(String publicUrl) {
		this.publicUrl = publicUrl;
	}


	
}
