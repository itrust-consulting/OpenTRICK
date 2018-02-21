package lu.itrust.business.TS.model.general.document;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class AbstractDocument {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idAbstractDocument")
	private long id;
	
	@Column(name = "dtVersion")
	private String version;
	
	@Column(name = "dtLabel")
	private String label;
	
	@Column(name = "dtFilename", unique = true)
	private String filename;
	
	@Column(name = "dtSize")
	private long size;
	
	@Column(name = "dtFile", length = 16777216)
	private byte[] file;
	
	@Column(name = "dtCreated")
	private Timestamp created;

	public AbstractDocument() {
	}
	
	public AbstractDocument(String label, String version, String filename, byte[] file, long size) {
		this.label = label;
		this.version = version;
		this.filename = filename;
		this.size = size;
		this.file = file;
		setCreated(new Timestamp(System.currentTimeMillis()));
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}

}