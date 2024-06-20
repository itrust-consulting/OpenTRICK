package lu.itrust.business.ts.form;

import org.springframework.web.multipart.MultipartFile;

public class ImportAnalysisForm {
	
	private int customer;
	
	private MultipartFile file;
	
	public ImportAnalysisForm() {
	}

	public int getCustomer() {
		return customer;
	}

	public void setCustomer(int customer) {
		this.customer = customer;
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

}
