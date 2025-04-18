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

	/**
	 * Constructs a new UserDocument object with the specified user, identifier, label, version, filename, file, and size.
	 *
	 * @param user      the user associated with the document
	 * @param identifier the identifier of the document
	 * @param label     the label of the document
	 * @param version   the version of the document
	 * @param filename  the filename of the document
	 * @param file      the file content of the document
	 * @param size      the size of the document in bytes
	 */
	protected UserDocument(User user, String identifier, String label, String version, String filename, byte[] file,
						   long size) {
		super(label, version, filename, file, size);
		this.identifier = identifier;
		this.user = user;
	}

	/**
	 * Returns the user associated with the document.
	 *
	 * @return the user associated with the document
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Sets the user associated with the document.
	 *
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * Returns the identifier of the document.
	 *
	 * @return the identifier of the document
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * Sets the identifier of the document.
	 *
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

}
