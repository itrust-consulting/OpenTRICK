/**
 * 
 */
package lu.itrust.business.TS.form;

import lu.itrust.business.TS.model.general.CredentialType;

/**
 * @author eomar
 *
 */
public class UserCredentialForm {
	
	private String name;
	
	private String value;
	
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
}
