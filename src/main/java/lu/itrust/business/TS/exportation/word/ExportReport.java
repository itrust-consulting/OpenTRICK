package lu.itrust.business.TS.exportation.word;

import java.io.File;
import java.util.Locale;

import org.springframework.context.MessageSource;

import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.model.analysis.Analysis;

public interface ExportReport {

	final String TS_TAB_TEXT_1 = "TSTabText1";

	final String TS_TAB_TEXT_2 = "TSTabText2";

	final String TS_TAB_TEXT_3 = "TSTabText3";

	final String DEFAULT_PARAGRAHP_STYLE = TS_TAB_TEXT_2;
	
	final String HEADER_COLOR = "CCC0D9";
	
	final String SUB_HEADER_COLOR = "E5DFEC";

	final String LIGHT_CELL_COLOR = SUB_HEADER_COLOR;

	final String _27001_NA_MEASURES = "27001_NA_MEASURES";

	final String _27002_NA_MEASURES = "27002_NA_MEASURES";

	final String DEFAULT_CELL_COLOR = "FFFFFF";

	final String MAX_IMPL = "MAX_IMPL";

	final String SUPER_HEAD_COLOR = HEADER_COLOR;

	final String ZERO_COST_COLOR = "e6b8b7";

	/**
	 * exportToWordDocument: <br>
	 * Description
	 * 
	 * @param analysisId
	 * @param context
	 * @param serviceAnalysis
	 * 
	 * @return
	 * @throws Exception
	 */
	void exportToWordDocument(Analysis analysis) throws Exception;

	/**
	 * getAnalysis: <br>
	 * Returns the analysis field value.
	 * 
	 * @return The value of the analysis field
	 */
	Analysis getAnalysis();

	String getContextPath();

	/**
	 * @return the currentParagraphId
	 */
	String getCurrentParagraphId();

	
	String getIdTask();

	Locale getLocale();

	int getMaxProgress();

	MessageSource getMessageSource();

	int getMinProgress();

	int getProgress();

	String getReportName();

	ServiceTaskFeedback getServiceTaskFeedback();

	File getWorkFile();

	int increase(int value);

	/**
	 * setAnalysis: <br>
	 * Sets the Field "analysis" with a value.
	 * 
	 * @param analysis
	 *            The Value to set the analysis field
	 */
	void setAnalysis(Analysis analysis);

	void setContextPath(String contextPath);

	/**
	 * @param currentParagraphId
	 *            the currentParagraphId to set
	 */
	void setCurrentParagraphId(String currentParagraphId);


	void setIdTask(String idTask);

	void setLocale(Locale locale);

	void setMaxProgress(int maxProgress);

	void setMessageSource(MessageSource messageSource);

	void setMinProgress(int minProgress);

	void setReportName(String reportName);

	void setServiceTaskFeedback(ServiceTaskFeedback serviceTaskFeedback);

	void setWorkFile(File workFile);
	
	void close();

}