package lu.itrust.business.TS.controller.form;

import org.springframework.web.multipart.MultipartFile;

import lu.itrust.business.TS.model.analysis.AnalysisType;

public class ExportWordReportForm {
	
	private int analysis;
	
	private long template;
	
	private AnalysisType type;
	
	private MultipartFile file;
	
	private boolean internal;

	public int getAnalysis() {
		return analysis;
	}

	public void setAnalysis(int analysis) {
		this.analysis = analysis;
	}

	public long getTemplate() {
		return template;
	}

	public void setTemplate(long template) {
		this.template = template;
	}

	public AnalysisType getType() {
		return type;
	}

	public void setType(AnalysisType type) {
		this.type = type;
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public boolean isInternal() {
		return internal;
	}

	public void setInternal(boolean internal) {
		this.internal = internal;
	}
}
