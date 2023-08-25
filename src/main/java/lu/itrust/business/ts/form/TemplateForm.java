/**
 * 
 */
package lu.itrust.business.ts.form;

import org.springframework.web.multipart.MultipartFile;

import lu.itrust.business.ts.model.analysis.AnalysisType;
import lu.itrust.business.ts.model.general.document.impl.TrickTemplateType;

/**
 * @author eomar
 *
 */
public class TemplateForm {

	private long id;

	private int customer;

	private int language;

	private TrickTemplateType type;

	private AnalysisType analysisType;

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

	public TrickTemplateType getType() {
		return type;
	}

	public void setType(TrickTemplateType type) {
		this.type = type;
	}

	public AnalysisType getAnalysisType() {
		return analysisType;
	}

	public void setAnalysisType(AnalysisType analysisType) {
		this.analysisType = analysisType;
	}

}
