/**
 * 
 */
package lu.itrust.business.TS.controller.form;

import java.util.LinkedList;
import java.util.List;

/**
 * @author eomar
 *
 */
public class DataManagerExportForm {
	
	private CSSFExportForm cssf;
	
	private ExportWordReportForm wordReport;
	
	private List<DataManagerItem> items = new LinkedList<>();
	

	public CSSFExportForm getCssf() {
		return cssf;
	}

	public void setCssf(CSSFExportForm cssf) {
		this.cssf = cssf;
	}

	public ExportWordReportForm getWordReport() {
		return wordReport;
	}

	public void setWordReport(ExportWordReportForm wordReport) {
		this.wordReport = wordReport;
	}

	public List<DataManagerItem> getItems() {
		return items;
	}

	public void setItems(List<DataManagerItem> items) {
		this.items = items;
	}

}
