/**
 * 
 */
package lu.itrust.business.TS.form;

import org.springframework.web.multipart.MultipartFile;

import lu.itrust.business.TS.model.analysis.AnalysisType;

/**
 * @author eomar
 *
 */
public class ReportTemplateForm {
	
	private long id;
	
	private int customer;
	
	private int language;
	
	private AnalysisType type;
	
	private String label;
	
	private String version;
	
	private String filename;
	
	private MultipartFile file;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getCustomer() {
		return customer;
	}

	public void setCustomer(int customer) {
		this.customer = customer;
	}

	public int getLanguage() {
		return language;
	}

	public void setLanguage(int language) {
		this.language = language;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public AnalysisType getType() {
		return type;
	}

	public void setType(AnalysisType type) {
		this.type = type;
	}

}
