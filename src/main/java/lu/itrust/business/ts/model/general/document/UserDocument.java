package lu.itrust.business.ts.model.general.document;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;

import lu.itrust.business.ts.usermanagement.User;

@MappedSuperclass
public abstract class UserDocument extends AnalysisDocument {

	@Column(name = "dtIdentifier", nullable = false)
	private String identifier = null;

	@ManyToOne
	@JoinColumn(name = "fiUser", nullable = false)
	private User user = null;

	protected UserDocument() {
	}

	protected UserDocument(User user, String identifier, String label, String version, String filename, byte[] file,
			long size) {
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
	 *             the user to set
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
	 *                   the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

}
