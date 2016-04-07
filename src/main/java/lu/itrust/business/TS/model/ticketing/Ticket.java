/**
 * 
 */
package lu.itrust.business.TS.model.ticketing;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class Ticket {

	@Id
	@Column(name="idTicket")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@Column(name="dtExternalId")
	private String ticketId;
	
	@Column(name="dtReference")
	private String reference;
	
	@Column(name="dtStandard")
	private String standard;
	
	
	/**
	 * 
	 */
	public Ticket() {

	}

	/**
	 * @param ticketId
	 * @param reference
	 * @param standard
	 */
	public Ticket(String ticketId, String reference, String standard) {
		this.ticketId = ticketId;
		this.reference = reference;
		this.standard = standard;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}


	/**
	 * @return the ticketId
	 */
	public String getTicketId() {
		return ticketId;
	}

	/**
	 * @param ticketId the ticketId to set
	 */
	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}


	/**
	 * @return the reference
	 */
	public String getReference() {
		return reference;
	}


	/**
	 * @param reference the reference to set
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}


	/**
	 * @return the standard
	 */
	public String getStandard() {
		return standard;
	}


	/**
	 * @param standard the standard to set
	 */
	public void setStandard(String standard) {
		this.standard = standard;
	}
}
