/**
 * 
 */
package lu.itrust.business.TS.exportation.word;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.context.MessageSource;


import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.general.document.impl.ReportTemplate;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;

/**
 * @author eomar
 *
 */
public interface ExportReportData {
	
	void close();
	
	File getFile();
	
	String getPath();
	
	int getProgess();
	
	Locale getLocale();
	
	String getDarkColor();
	
	String getReportName();
	
	String getLightColor();
	
	Analysis getAnalysis();
	
	String getDefaultColor();
	
	String getZeroCostColor();
	
	ReportTemplate getTemplate();
	
	String getDefaultTableStyle();
	
	ValueFactory getValueFactory();
	
	String getCurrentParagraphId();
	
	AtomicLong getBookmarkCounter();
	
	DecimalFormat getNumberFormat();
	
	MessageSource getMessageSource();
	
	AtomicInteger getBookmarkMaxId();
	
	String getDefaultParagraphStyle();
	
	DecimalFormat getKiloNumberFormat();
}
