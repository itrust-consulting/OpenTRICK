package lu.itrust.business.TS.usermanagement;

import lu.itrust.business.TS.Customer;

/**
 * userCustomer.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.à.rl. :
 * @version
 * @since Jan 15, 2014
 */
public class UserCustomer {

	/** database id */
	private int id;

	/** user */
	private User user;

	/** customer */
	private Customer customer;

	public UserCustomer(){
		
	}
	
	public UserCustomer(User user, Customer customer) {
		this.user = user;
		this.customer = customer;
	}
	
	/**
	 * getId: <br>
	 * Returns the id field value.
	 * 
	 * @return The value of the id field
	 */
	public int getId() {
		return id;
	}

	/**
	 * setId: <br>
	 * Sets the Field "id" with a value.
	 * 
	 * @param id
	 *            The Value to set the id field
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * getUser: <br>
	 * Returns the user field value.
	 * 
	 * @return The value of the user field
	 */
	public User getUser() {
		return user;
	}

	/**
	 * setUser: <br>
	 * Sets the Field "user" with a value.
	 * 
	 * @param user
	 *            The Value to set the user field
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * getCustomer: <br>
	 * Returns the customer field value.
	 * 
	 * @return The value of the customer field
	 */
	public Customer getCustomer() {
		return customer;
	}

	/**
	 * setCustomer: <br>
	 * Sets the Field "customer" with a value.
	 * 
	 * @param customer
	 *            The Value to set the customer field
	 */
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
}