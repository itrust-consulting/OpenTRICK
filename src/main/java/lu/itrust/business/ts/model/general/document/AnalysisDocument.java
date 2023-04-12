package lu.itrust.business.ts.model.general.document;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

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

	protected AnalysisDocument(String label, String version, String name, byte[] data, long length) {
		super(name, length, data);
		this.label = label;
		this.version = version;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}