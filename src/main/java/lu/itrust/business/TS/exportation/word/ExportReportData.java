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

import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.helper.Task;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.general.document.impl.ReportTemplate;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;

/**
 * @author eomar
 *
 */
public interface ExportReportData {

	String CLIENT_NAME = "Client";

	String MAX_IMPL = "MAX_IMPL";

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

	void close();

	File getFile();

	String getPath();

	Locale getLocale();

	String getDarkColor();

	String getLightColor();

	Analysis getAnalysis();

	String getDefaultColor();

	String getZeroCostColor();

	ReportTemplate getTemplate();

	Object getDefaultTableStyle();

	ValueFactory getValueFactory();

	String getCurrentParagraphId();

	AtomicLong getBookmarkCounter();

	DecimalFormat getNumberFormat();

	MessageSource getMessageSource();

	AtomicInteger getBookmarkMaxId();

	DecimalFormat getKiloNumberFormat();

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

	void export(ReportTemplate template, Task task, Analysis analysis, ServiceTaskFeedback serviceTaskFeedback);

}
