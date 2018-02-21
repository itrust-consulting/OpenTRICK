package lu.itrust.business.TS.model.general.document;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import lu.itrust.business.TS.usermanagement.User;

@MappedSuperclass
public abstract class AnalysisDocument extends AbstractDocument {

	@Column(name = "dtIdentifier", nullable = false)
	private String identifier = null;

	@ManyToOne
	@JoinColumn(name = "fiUser", nullable = false)
	private User user = null;

	public AnalysisDocument() {
	}

	public AnalysisDocument(User user, String identifier, String label, String version, String filename, byte[] file, long size) {
		super(label, version, filename, file, size);
		this.identifier = identifier;
		this.user = user;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @param identifier
	 *            the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

}
