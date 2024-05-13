package lu.itrust.business.ts.model.general.document;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

/**
 * Represents an analysis document, which is a type of document used in the system.
 * This class extends the base Document class and adds additional properties specific to analysis documents.
 */
@MappedSuperclass
@AttributeOverride(name = "length", column = @Column(name = "dtSize"))
@AttributeOverride(name = "name", column = @Column(name = "dtFilename", unique = true))
@AttributeOverride(name = "data", column = @Column(name = "dtFile", length = 16777216))
public class AnalysisDocument extends Document {

	@Column(name = "dtVersion")
	private String version;

	@Column(name = "dtLabel")
	private String label;

	protected AnalysisDocument() {
	}

	/**
	 * Represents an analysis document.
	 */
	protected AnalysisDocument(String label, String version, String name, byte[] data, long length) {
		super(name, length, data);
		this.label = label;
		this.version = version;
	}

	/**
	 * Returns the version of the document.
	 *
	 * @return the version of the document as a String.
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Sets the version of the analysis document.
	 *
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Returns the label of the analysis document.
	 *
	 * @return the label of the analysis document
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets the label of the analysis document.
	 *
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

}