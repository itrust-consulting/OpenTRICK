/**
 * 
 */
package lu.itrust.business.ts.exportation.word;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.context.MessageSource;

import lu.itrust.business.ts.database.service.ServiceTaskFeedback;
import lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ColorSet;
import lu.itrust.business.ts.helper.InstanceManager;
import lu.itrust.business.ts.helper.Task;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.general.document.impl.TrickTemplate;
import lu.itrust.business.ts.model.parameter.helper.ValueFactory;

/**
 * @author eomar
 *
 */
public interface ExportReport {

	String CLIENT_NAME = "Client";

	String MAX_IMPL = "MAX_IMPL";

	String TB_HEADER_0 = "TabHeader0";

	String TB_HEADER_1 = "TabHeader1";

	String TS_TAB_TEXT_1 = "TSTabText1";

	String TS_TAB_TEXT_2 = "TSTabText2";

	String TS_TAB_TEXT_3 = "TSTabText3";

	String NA_MEASURES = "_NA_MEASURES";

	String INTERNAL_WL_VAL = "INTERNAL_WL_VAL";

	String EXTERNAL_WL_VAL = "EXTERNAL_WL_VAL";

	String PROPERTY_REPORT_TYPE = "REPORT_TYPE";

	String NA_MEASURES_27001 = "27001_NA_MEASURES";

	String NA_MEASURES_27002 = "27002_NA_MEASURES";

	String NUMBER_MEASURES_ALL_PHASES = "NUMBER_MEASURES_ALL_PHASES";
	
	String NUMBER_FORMAT = "[>9.99]#\\ ###\\ ###\\ ###\\ ##0\\k\\€;[>0.509]#\\k\\€;#,##0\\k\\€";

	void close();

	File getFile();

	Locale getLocale();
	
	ColorSet getColors();

	String getDarkColor();

	String getLightColor();

	Analysis getAnalysis();

	String getDefaultColor();

	String getZeroCostColor();

	TrickTemplate getTemplate();

	Object getDefaultTableStyle();

	Object getTableStyleOrDefault(String style);

	ValueFactory getValueFactory();

	String getCurrentParagraphId();

	AtomicLong getBookmarkCounter();

	DecimalFormat getNumberFormat();

	AtomicInteger getBookmarkMaxId();

	DecimalFormat getKiloNumberFormat();
	
	default MessageSource getMessageSource() {
		return InstanceManager.getMessageSource();
	}

	default String getMessage(String code) {
		return getMessage(code, null, null, getLocale());
	}

	default String getMessage(String code, Object[] parameters) {
		return getMessage(code, parameters, null, getLocale());
	}

	default String getMessage(String code, String defaultMessage) {
		return getMessage(code, null, defaultMessage, getLocale());
	}

	default String getMessage(String code, Object[] parameters, String defaultMessage) {
		return getMessage(code, parameters, defaultMessage, getLocale());
	}

	default String getMessage(String code, Object[] parameters, String defaultMessage, Locale locale) {
		return getMessageSource().getMessage(code, parameters, defaultMessage, locale);
	}

	void export(TrickTemplate template, Task task, Analysis analysis, ServiceTaskFeedback serviceTaskFeedback);

}
