/**
 * 
 */
package lu.itrust.business.TS.exportation.word;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Locale;

import org.springframework.context.MessageSource;

import com.atlassian.util.concurrent.atomic.AtomicInteger;

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
	
	ReportTemplate getTemplate();
	
	String getDefaultTableStyle();
	
	ValueFactory getValueFactory();
	
	DecimalFormat getNumberFormat();
	
	MessageSource getMessageSource();
	
	AtomicInteger getBookmarkMaxId();
	
	String getDefaultParagraphStyle();
	
	AtomicInteger getBookmarkCounter();
	
	DecimalFormat getKiloNumberFormat();
}
