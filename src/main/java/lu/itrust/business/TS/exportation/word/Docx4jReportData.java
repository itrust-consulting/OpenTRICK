/**
 * 
 */
package lu.itrust.business.TS.exportation.word;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author eomar
 *
 */
public interface Docx4jReportData extends ExportReportData {
	
	AtomicLong getDrawingIndex();
	
	org.docx4j.wml.Document getDocument();
	
	org.docx4j.wml.ObjectFactory getFactory();
	
	org.docx4j.dml.ObjectFactory getDmlFactory();
	
	org.docx4j.dml.chart.ObjectFactory getChartFactory();
	
	org.docx4j.dml.wordprocessingDrawing.ObjectFactory getDrawingFactory();
	
	org.docx4j.openpackaging.packages.WordprocessingMLPackage getWordMLPackage();
	
	String HTTP_SCHEMAS_OPENXMLFORMATS_ORG_DRAWINGML_2006_CHART = "http://schemas.openxmlformats.org/drawingml/2006/chart";
	
}
