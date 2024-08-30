/**
 * 
 */
package lu.itrust.business.ts.asynchronousWorkers;

import static lu.itrust.business.ts.exportation.word.impl.docx4j.Docx4jReportImpl.mergeCell;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.Docx4jReportImpl.verticalMergeCell;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.formatting.Docx4jFormatter.updateRow;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.formatting.Docx4jMeasureFormatter.sum;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.createRow;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.createWorkSheetPart;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.getExtension;
import static lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper.setValue;
import static lu.itrust.business.ts.helper.InstanceManager.loadTemplate;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.docx4j.jaxb.Context;
import org.docx4j.model.table.TblFactory;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorksheetPart;
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.CTVerticalJc;
import org.docx4j.wml.Document;
import org.docx4j.wml.Jc;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase.PStyle;
import org.docx4j.wml.PPrBase.TextAlignment;
import org.docx4j.wml.R;
import org.docx4j.wml.STVerticalJc;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.TblWidth;
import org.docx4j.wml.Tc;
import org.docx4j.wml.TcPr;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;
import org.docx4j.wml.TrPr;
import org.hibernate.Session;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.Worksheet;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import lu.itrust.business.ts.asynchronousWorkers.helper.AsyncCallback;
import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.dao.DAOAnalysis;
import lu.itrust.business.ts.database.dao.DAOUser;
import lu.itrust.business.ts.database.dao.DAOWordReport;
import lu.itrust.business.ts.database.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.ts.database.dao.hbm.DAOUserHBM;
import lu.itrust.business.ts.database.dao.hbm.DAOWordReportHBM;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ExcelHelper;
import lu.itrust.business.ts.form.CSSFExportForm;
import lu.itrust.business.ts.helper.InstanceManager;
import lu.itrust.business.ts.messagehandler.MessageHandler;
import lu.itrust.business.ts.messagehandler.TaskName;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.analysis.AnalysisSetting;
import lu.itrust.business.ts.model.analysis.ExportFileName;
import lu.itrust.business.ts.model.assessment.helper.Estimation;
import lu.itrust.business.ts.model.cssf.RiskProbaImpact;
import lu.itrust.business.ts.model.cssf.RiskProfile;
import lu.itrust.business.ts.model.cssf.RiskStrategy;
import lu.itrust.business.ts.model.cssf.helper.CSSFFilter;
import lu.itrust.business.ts.model.general.document.impl.TrickTemplateType;
import lu.itrust.business.ts.model.general.document.impl.WordReport;
import lu.itrust.business.ts.model.general.helper.ExportType;
import lu.itrust.business.ts.model.general.helper.Utils;
import lu.itrust.business.ts.model.parameter.IImpactParameter;
import lu.itrust.business.ts.model.parameter.helper.ValueFactory;
import lu.itrust.business.ts.model.scale.ScaleType;
import lu.itrust.business.ts.model.standard.measure.Measure;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescription;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescriptionText;
import lu.itrust.business.ts.usermanagement.User;

/**
 * This class represents a worker for exporting a risk sheet. It extends the
 * WorkerImpl class.
 * The WorkerExportRiskSheet class is responsible for exporting risk data to an
 * Excel sheet.
 * It provides methods for canceling the export, retrieving the CSSF export
 * form, setting the locale,
 * running the export process, and setting various properties related to the
 * export.
 */
public class WorkerExportRiskSheet extends WorkerImpl {

	public static volatile String P_STYLE = "BodyOfText";

	public static volatile String TC_P_STYLE = "TabText2";

	public static volatile String DEFAULT_EXCEL_TABLE;

	public static volatile String EXCEL_HEADER_FOOTER_SHEET_NAME = "Hist";

	private String alpha2 = "EN";

	private CSSFExportForm cssfExportForm;

	private DAOAnalysis daoAnalysis;

	private DAOUser daoUser;

	private DAOWordReport daoWordReport;

	private DateFormat dateFormat;

	private int idAnalysis;

	private Locale locale;

	private String pStyleId = P_STYLE;

	private boolean showRawColumn = true;

	private String username;

	/**
	 * Constructs a new instance of the WorkerExportRiskSheet class.
	 *
	 * @param cssfExportForm The CSSFExportForm object.
	 * @param analysisId     The analysis ID.
	 * @param username       The username.
	 */
	public WorkerExportRiskSheet(CSSFExportForm cssfExportForm, Integer analysisId, String username) {
		setCssfExportForm(cssfExportForm);
		setUsername(username);
		setIdAnalysis(analysisId);
	}

	/**
	 * Cancels the current task.
	 * If the task is currently working and not already canceled, it interrupts the
	 * current thread or the thread associated with the current task.
	 * Sets the canceled flag to true.
	 * If an exception occurs during the cancellation process, it sets the error and
	 * logs the exception.
	 * If the task is still working after cancellation, it sets the working flag to
	 * false and updates the finished timestamp.
	 */
	@Override
	public void cancel() {
		try {
			if (isWorking() && !isCanceled()) {
				synchronized (this) {
					if (isWorking() && !isCanceled()) {
						if (getCurrent() == null)
							Thread.currentThread().interrupt();
						else
							getCurrent().interrupt();
						setCanceled(true);
					}
				}
			}
		} catch (Exception e) {
			setError(e);
			TrickLogManager.Persist(e);
		} finally {
			if (isWorking()) {
				synchronized (this) {
					if (isWorking()) {
						setWorking(false);
						setFinished(new Timestamp(System.currentTimeMillis()));
					}
				}
			}
		}
	}

	/**
	 * @return the cssfExportForm
	 */
	public CSSFExportForm getCssfExportForm() {
		return cssfExportForm;
	}

	/**
	 * @return the locale
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * Executes the worker task.
	 * This method is called when the worker thread starts.
	 * It performs the necessary operations to export a risk sheet.
	 * 
	 * @throws TrickException if an error occurs during the export process
	 */
	@Override
	public void run() {
		Session session = null;
		try {
			synchronized (this) {
				if (getWorkersPoolManager() != null && !getWorkersPoolManager().exist(getId())
						&& !getWorkersPoolManager().add(this))
					return;
				if (isCanceled() || isWorking())
					return;
				setWorking(true);
				setStarted(new Timestamp(System.currentTimeMillis()));
				setName(TaskName.EXPORT_RISK_SHEET);
				setCurrent(Thread.currentThread());
			}
			session = getSessionFactory().openSession();
			daoAnalysis = new DAOAnalysisHBM(session);
			daoWordReport = new DAOWordReportHBM(session);
			daoUser = new DAOUserHBM(session);
			session.beginTransaction();
			long reportId = getCssfExportForm().getType() == ExportType.RAW ? exportData() : exportReport();
			session.getTransaction().commit();
			MessageHandler messageHandler = new MessageHandler("success.export.risk_sheet",
					"Risk sheet has been successfully exported", 100);
			if (getCssfExportForm().getType() == ExportType.RAW)
				messageHandler.setAsyncCallbacks(new AsyncCallback("download", "Report", reportId));
			else
				messageHandler.setAsyncCallbacks(new AsyncCallback("download", "Report", reportId),
						new AsyncCallback("reloadSection", "section_riskregister"));
			getServiceTaskFeedback().send(getId(), messageHandler);
		} catch (Exception e) {
			if (session != null) {
				try {
					if (session.getTransaction().getStatus().canRollback())
						session.getTransaction().rollback();
				} catch (Exception e1) {
				}
			}
			MessageHandler messageHandler = null;
			if (e instanceof TrickException)
				messageHandler = new MessageHandler(((TrickException) e).getCode(),
						((TrickException) e).getParameters(), e.getMessage(), e);
			else
				messageHandler = new MessageHandler("error.500.message", "Internal error", e);
			getServiceTaskFeedback().send(getId(), messageHandler);
			TrickLogManager.Persist(e);
		} finally {
			if (session != null) {
				try {
					session.close();
				} catch (Exception e) {
				}
			}
			if (isWorking()) {
				synchronized (this) {
					if (isWorking()) {
						setWorking(false);
						setFinished(new Timestamp(System.currentTimeMillis()));
					}
				}
			}
			getWorkersPoolManager().remove(this);
		}
	}

	/**
	 * @param locale the locale to set
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	@Override
	public synchronized void start() {
		run();
	}

	/**
	 * @return the idAnalysis
	 */
	protected int getIdAnalysis() {
		return idAnalysis;
	}

	/**
	 * @return the username
	 */
	protected String getUsername() {
		return username;
	}

	/**
	 * @param cssfExportForm the cssfExportForm to set
	 */
	protected void setCssfExportForm(CSSFExportForm cssfExportForm) {
		this.cssfExportForm = cssfExportForm;
	}

	/**
	 * @param idAnalysis the idAnalysis to set
	 */
	protected void setIdAnalysis(int idAnalysis) {
		this.idAnalysis = idAnalysis;
	}

	/**
	 * @param pStyleId the pStyleId to set
	 */
	protected void setpStyleId(String pStyleId) {
		this.pStyleId = pStyleId;
	}

	/**
	 * @param username the username to set
	 */
	protected void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Adds estimations to the worksheet.
	 * 
	 * @param worksheet   The worksheet to add the estimations to.
	 * @param estimations The list of estimations to add.
	 * @param types       The list of scale types.
	 */
	private void addEstimation(Worksheet worksheet, List<Estimation> estimations,
			List<ScaleType> types) {
		int size = 16 + types.size() * 3;
		for (Estimation estimation : estimations) {
			Row row = createRow(worksheet.getSheetData(), size);
			String scenarioType = estimation.getScenario().getType().getName();
			String category = getMessage("label.scenario.type." + scenarioType.replace("-", "_").toLowerCase(),
					scenarioType);
			int index = 0;
			setCellString(row, index++, estimation.getIdentifier());
			setCellString(row, index++, category);
			setCellString(row, index++, estimation.getScenario().getName());
			setCellString(row, index++, estimation.getOwner());
			if (showRawColumn) {
				printRiskProba(row, index++, types, estimation.getRawProbaImpact());
				index += types.size() + 1;
			}
			printRiskProba(row, index++, types, estimation.getNetEvaluation());
			index += types.size() + 1;
			printRiskProba(row, index++, types, estimation.getExpProbaImpact());
			index += types.size() + 1;
			setCellString(row, index++, estimation.getScenario().getDescription());
			setCellString(row, index++, estimation.getArgumentation());
			setCellString(row, index++, estimation.getAsset().getName());
			setCellString(row, index++, estimation.getRiskTreatment());
			RiskStrategy strategy = estimation.getRiskStrategy();
			if (strategy == null)
				strategy = RiskStrategy.ACCEPT;
			String response = strategy.getNameToLower();
			setCellString(row, index++, getMessage("label.risk_register.strategy." + response, response));
			List<String> actionPlan = new LinkedList<>();
			for (Measure measure : estimation.getRiskProfile().getMeasures()) {
				MeasureDescription description = measure.getMeasureDescription();
				MeasureDescriptionText descriptionText = description
						.getMeasureDescriptionTextByAlpha2(getLocale().getLanguage());
				String date = dateFormat.format(measure.getPhase().getEndDate());
				actionPlan.add(getMessage("report.risk_profile.action_plan.measure",
						new Object[] { description.getStandard().getName(), description.getReference(),
								descriptionText.getDomain(), date },
						String.format("%s: %s, %s; %s", description.getStandard().getName(), description.getReference(),
								descriptionText.getDomain(), date)));
			}

			if (actionPlan.isEmpty())
				setCellString(row, index++,
						estimation.getActionPlan() == null || estimation.getActionPlan().isEmpty() ? ""
								: estimation.getActionPlan());
			else {
				if (!(estimation.getActionPlan() == null || estimation.getActionPlan().isEmpty()))
					actionPlan.add(estimation.getActionPlan());
				setCellString(row, index++, String.join("\n\r", actionPlan));
			}
		}
	}

	/**
	 * Adds a field to the document with the specified title and content.
	 *
	 * @param document the document to add the field to
	 * @param title    the title of the field
	 * @param content  the content of the field
	 */
	private void addField(Document document, String title, String content) {
		addTitle(document, title);
		addFieldContent(document, content);
	}

	/**
	 * Adds the specified content to the given document.
	 * 
	 * @param document the document to add the content to
	 * @param content  the content to be added
	 */
	private void addFieldContent(Document document, String content) {
		if (content == null || content.isEmpty())
			return;
		String[] texts = content.split("(\r\n|\n\r|\r|\n)");
		for (int i = 0; i < texts.length; i++)
			document.getContent().add(setText(setStyle(new P(), P_STYLE), texts[i]));
	}

	/**
	 * Adds content to the specified cell with the given text.
	 * 
	 * @param cell The cell to add content to.
	 * @param text The text to be added to the cell.
	 */
	private void addFieldContent(Tc cell, String text) {
		addFieldContent(cell, text, false);
	}

	/**
	 * Adds content to a given cell in a risk sheet.
	 *
	 * @param cell The cell to add content to.
	 * @param text The text to be added to the cell.
	 * @param add  A flag indicating whether to add the content or create a new
	 *             paragraph.
	 */
	private void addFieldContent(Tc cell, String text, boolean add) {
		P p = (P) (!add && cell.getContent().size() == 1 ? cell.getContent().get(0) : createP(cell));
		if (text == null)
			text = "";
		setpStyleId(TC_P_STYLE);
		String[] texts = text.split("(\r\n|\n\r|\r|\n)");
		for (int i = 0; i < texts.length; i++) {
			if (i > 0)
				p = createP(cell);
			setText(p, texts[i]);
		}
		setpStyleId(P_STYLE);
	}

	/**
	 * Adds the header row to the worksheet.
	 *
	 * @param worksheet The worksheet to add the header row to.
	 * @param factory   The object factory used to create cells and rows.
	 * @param types     The list of scale types.
	 */
	private void addHeader(Worksheet worksheet, List<ScaleType> types) {
		final int rowCount = showRawColumn ? types.size() * 3 + 16 : types.size() * 2 + 14;
		final Row row = createRow(worksheet.getSheetData(), rowCount);
		final int step = 2;
		final int netIndex = (showRawColumn ? 6 + types.size() : 4);
		final int expIndex = netIndex + types.size() + step;

		int index = expIndex + types.size() + step;

		setValue(row.getC().get(0), getMessage("report.risk_sheet.risk_id", "Risk ID"));
		setValue(row.getC().get(1), getMessage("report.risk_sheet.risk_category", "Category"));
		setValue(row.getC().get(2), getMessage("report.risk_sheet.title", "Title"));
		setValue(row.getC().get(3), getMessage("report.risk_sheet.risk_owner", "Risk owner"));
		if (showRawColumn)
			printEvaluationHeader(row, types, 4, "Raw");
		printEvaluationHeader(row, types, netIndex, "Net");
		printEvaluationHeader(row, types, expIndex, "Exp.");
		setValue(row.getC().get(index++), getMessage("report.risk_sheet.risk_description", "Risk description"));
		setValue(row.getC().get(index++), getMessage("report.risk_sheet.argumentation", "Argumentation"));
		setValue(row.getC().get(index++),
				getMessage("report.risk_sheet.customer_concerned", "Financial customers concerned"));
		setValue(row.getC().get(index++), getMessage("report.risk_sheet.risk_treatment", "Risk treatment"));
		setValue(row.getC().get(index++), getMessage("report.risk_sheet.response", "Response strategy"));
		setValue(row.getC().get(index), getMessage("report.risk_sheet.action_plan", "Action plan"));

	}

	/**
	 * Adds the header section for the risk sheet to the given document.
	 *
	 * @param document    The document to add the header section to.
	 * @param riskProfile The risk profile associated with the risk sheet.
	 * @param isFirst     Flag indicating if this is the first risk sheet in the
	 *                    document.
	 */
	private void addRiskSheetHeader(Document document, RiskProfile riskProfile, boolean isFirst) {
		final String scenarioType = riskProfile.getScenario().getType().getName();
		final String category = getMessage("label.scenario.type." + scenarioType.replace("-", "_").toLowerCase(),
				scenarioType);
		final String idRisk = riskProfile.getIdentifier() == null ? "" : riskProfile.getIdentifier();
		final String text = getCssfExportForm().isCssf()
				? getMessage("report.risk_sheet.cssf.page_title", new Object[] { category, idRisk },
						String.format("Category %s - Risk %s", category, idRisk))
				: getMessage("report.risk_sheet.normal.page_title",
						new Object[] { category, riskProfile.getAsset().getName() },
						String.format("Category %s - %s", category, riskProfile.getAsset().getName()));
		P paragraph = null;
		if (isFirst)
			paragraph = (P) document.getContent().parallelStream().filter(P.class::isInstance).findAny().orElse(null);
		if (paragraph == null)
			document.getContent().add(paragraph = new P());

		setStyle(setText(paragraph, text), "Heading2");
		Tbl table = createTable("TableBLight", 2, 3);
		if (table.getTblPr() == null)
			table.setTblPr(Context.getWmlObjectFactory().createTblPr());

		if (table.getTblPr().getTblW() == null)
			table.getTblPr().setTblW(Context.getWmlObjectFactory().createTblWidth());

		table.getTblPr().getTblW().setW(BigInteger.valueOf(5000));
		table.getTblPr().getTblW().setType("pct");

		Tr row = (Tr) table.getContent().get(0);
		setCellText((Tc) row.getContent().get(0), getMessage("report.risk_sheet.risk_id", "Risk ID"));
		setCellText((Tc) row.getContent().get(1), getMessage("report.risk_sheet.risk_category", "Category"));
		setCellText((Tc) row.getContent().get(2), getMessage("report.risk_sheet.title", "Title"));
		row = (Tr) table.getContent().get(1);
		setCellText((Tc) row.getContent().get(0), idRisk);
		setCellText((Tc) row.getContent().get(1), category);
		setCellText((Tc) row.getContent().get(2), riskProfile.getScenario().getName());
		document.getContent().add(format(table));
	}

	/**
	 * Adds a table to the document with the specified title, risk probability and
	 * impact, and list of scale types.
	 *
	 * @param document    The document to which the table will be added.
	 * @param title       The title of the table.
	 * @param probaImpact The risk probability and impact.
	 * @param types       The list of scale types.
	 */
	private void addTable(Document document, String title, RiskProbaImpact probaImpact, List<ScaleType> types) {

		addTitle(document, title);
		Tbl table = createTable("TableBLight", 3, 2 + types.size());
		if (table.getTblPr() == null)
			table.setTblPr(Context.getWmlObjectFactory().createTblPr());

		if (table.getTblPr().getTblW() == null)
			table.getTblPr().setTblW(Context.getWmlObjectFactory().createTblWidth());

		table.getTblPr().getTblW().setW(BigInteger.valueOf(5000));
		table.getTblPr().getTblW().setType("pct");

		if (probaImpact == null)
			probaImpact = new RiskProbaImpact();

		BooleanDefaultTrue booleandefaulttrue = Context.getWmlObjectFactory().createBooleanDefaultTrue();
		JAXBElement<org.docx4j.wml.BooleanDefaultTrue> booleandefaulttrueWrapped = Context.getWmlObjectFactory()
				.createCTTrPrBaseTblHeader(booleandefaulttrue);

		Tr row = (Tr) table.getContent().get(0);
		TextAlignment alignment = new TextAlignment();
		alignment.setVal("center");
		setCellText(setVAlignment((Tc) row.getContent().get(0), "center"),
				getMessage("report.risk_sheet.probability", "Probability (P)"));
		setCellText((Tc) row.getContent().get(1), getMessage("report.risk_sheet.impact", "Impact (i)"), alignment);
		setCellText(setVAlignment((Tc) row.getContent().get(types.size() + 1), "center"),
				getMessage("report.risk_sheet.importance", "Importance"));

		if (row.getTrPr() == null)
			row.setTrPr(new TrPr());

		row.getTrPr().getCnfStyleOrDivIdOrGridBefore().add(booleandefaulttrueWrapped);

		row = (Tr) table.getContent().get(1);
		int index = 1;
		for (ScaleType scaleType : types)
			setCellText((Tc) row.getContent().get(index++),
					getMessage("label.impact." + scaleType.getName().toLowerCase(),
							scaleType.getTranslations().containsKey(alpha2)
									? scaleType.getTranslations().get(alpha2).getName()
									: scaleType.getDisplayName()),
					alignment);

		if (row.getTrPr() == null)
			row.setTrPr(new TrPr());

		row.getTrPr().getCnfStyleOrDivIdOrGridBefore().add(booleandefaulttrueWrapped);

		row = (Tr) table.getContent().get(2);

		alignment = new TextAlignment();
		alignment.setVal("right");

		if (probaImpact.getProbability() == null)
			setCellText((Tc) row.getContent().get(0), "0", alignment);
		else
			setCellText((Tc) row.getContent().get(0), probaImpact.getProbability().getLevel().toString(), alignment);

		index = 1;
		for (ScaleType scaleType : types) {
			IImpactParameter impact = probaImpact.get(scaleType.getName());
			setCellText((Tc) row.getContent().get(index++), impact == null ? "0" : impact.getLevel() + "", alignment);
		}
		setCellText((Tc) row.getContent().get(index), probaImpact.getImportance() + "", alignment);
		verticalMergeCell(table.getContent(), 0, 0, 2, null);
		verticalMergeCell(table.getContent(), index, 0, 2, null);
		mergeCell((Tr) table.getContent().get(0), 1, index - 1, null);
		document.getContent().add(format(table));
	}

	/**
	 * Represents a table cell in a document.
	 */
	private Tc setVAlignment(Tc tc, String alignment) {
		if (tc.getTcPr() == null)
			tc.setTcPr(new TcPr());
		if (tc.getTcPr().getVAlign() == null)
			tc.getTcPr().setVAlign(new CTVerticalJc());
		tc.getTcPr().getVAlign().setVal(STVerticalJc.fromValue(alignment));
		return tc;
	}

	/**
	 * Adds an action table to the document with the specified title and risk
	 * profile.
	 * 
	 * @param document    the document to add the action table to
	 * @param title       the title of the action table
	 * @param riskProfile the risk profile containing the measures and action plan
	 */
	private void addActionTable(Document document, String title, RiskProfile riskProfile) {

		addTitle(document, title);
		if (!riskProfile.getMeasures().isEmpty()) {
			Tbl table = createTable("TableBLight", riskProfile.getMeasures().size() + 1, 4);

			if (table.getTblPr() == null)
				table.setTblPr(Context.getWmlObjectFactory().createTblPr());

			if (table.getTblPr().getTblW() == null)
				table.getTblPr().setTblW(Context.getWmlObjectFactory().createTblWidth());

			table.getTblPr().getTblW().setW(BigInteger.valueOf(5000));
			table.getTblPr().getTblW().setType("pct");

			Tr row = (Tr) table.getContent().get(0);
			setCellText((Tc) row.getContent().get(0), getMessage("report.risk_sheet.measure.standard", "Standard"));
			setCellText((Tc) row.getContent().get(1), getMessage("report.risk_sheet.measure.reference", "Reference"));
			setCellText((Tc) row.getContent().get(2), getMessage("report.risk_sheet.measure.domain", "Domain"));
			setCellText((Tc) row.getContent().get(3), getMessage("report.risk_sheet.measure.due_date", "Due date"));
			int index = 1;
			for (Measure measure : riskProfile.getMeasures()) {
				row = (Tr) table.getContent().get(index++);
				MeasureDescription description = measure.getMeasureDescription();
				MeasureDescriptionText descriptionText = description
						.getMeasureDescriptionTextByAlpha2(getLocale().getLanguage());
				addFieldContent((Tc) row.getContent().get(0), description.getStandard().getName());
				addFieldContent((Tc) row.getContent().get(1), description.getReference());
				addFieldContent((Tc) row.getContent().get(2), descriptionText.getDomain());
				addFieldContent((Tc) row.getContent().get(3), dateFormat.format(measure.getPhase().getEndDate()));
			}
			document.getContent().add(format(table));
		}

		if (StringUtils.hasText(riskProfile.getActionPlan()))
			addFieldContent(document, riskProfile.getActionPlan());

	}

	/**
	 * Adds a title to the document.
	 *
	 * @param document the document to add the title to
	 * @param title    the title to be added
	 */
	private void addTitle(Document document, String title) {
		document.getContent().add(setStyle(setText(new P(), title), "Heading5"));
	}

	/**
	 * Represents a paragraph element in a document.
	 */
	private P createP(Tc cell) {
		P p = new P();
		cell.getContent().add(p);
		return p;
	}

	/**
	 * Represents a table in a document.
	 */
	private Tbl createTable(String styleId, int rows, int cols) {
		Tbl table = TblFactory.createTable(rows, cols, 1);
		if (styleId != null)
			table.getTblPr().getTblStyle().setVal(styleId);
		if (table.getTblPr().getJc() == null)
			table.getTblPr().setJc(new Jc());
		table.getTblPr().getJc().setVal(JcEnumeration.CENTER);
		if (table.getTblPr().getTblW() == null)
			table.getTblPr().setTblW(new TblWidth());
		table.getTblPr().getTblW().setType("auto");
		table.getTblPr().getTblW().setW(BigInteger.valueOf(0));
		return table;
	}

	/**
	 * Exports the data for generating a risk sheet.
	 *
	 * @return The ID of the generated Word report.
	 * @throws Docx4JException If an error occurs during the export process.
	 * @throws JAXBException   If an error occurs during the export process.
	 * @throws IOException     If an error occurs during the export process.
	 */
	private long exportData() throws Docx4JException, JAXBException, IOException {
		final Analysis analysis = daoAnalysis.get(idAnalysis);
		final File file = loadTemplate(analysis.getCustomer(), TrickTemplateType.DEFAULT_EXCEL,
				analysis.getLanguage());
		try {
			getServiceTaskFeedback().send(getId(),
					new MessageHandler("info.preparing.risk_sheet.data", "Preparing risk sheet template", 2));
			final SpreadsheetMLPackage spreadsheetMLPackage = SpreadsheetMLPackage.load(file);

			final List<ScaleType> scaleTypes = analysis.findImpacts();
			final CSSFFilter cssfFilter = cssfExportForm.getFilter();

			final ValueFactory valueFactory = new ValueFactory(analysis.getParameters());

			final List<Estimation> directs = new LinkedList<>();
			final List<Estimation> indirects = new LinkedList<>();

			final List<Estimation> cias = new LinkedList<>();

			setLocale(new Locale(analysis.getLanguage().getAlpha2()));
			if (getLocale().getLanguage().equals("fr"))
				dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			else
				dateFormat = new SimpleDateFormat("MM-dd-yyyy");
			showRawColumn = analysis.findSetting(AnalysisSetting.ALLOW_RISK_ESTIMATION_RAW_COLUMN);

			scaleTypes.removeIf(scale -> scale.getName().equals(Constant.DEFAULT_IMPACT_NAME));
			final String name = getMessageSource().getMessage("label.raw.risk_sheet", null, "Raw risk sheet",
					getLocale());
			final WorksheetPart worksheetPart = createWorkSheetPart(spreadsheetMLPackage, name);

			Estimation.GenerateEstimation(analysis, cssfFilter, valueFactory, directs, indirects, cias);

			getServiceTaskFeedback().send(getId(),
					new MessageHandler("info.generating.risk_sheet", "Generating risk sheet", 10));

			addHeader(worksheetPart.getContents(), scaleTypes);

			getServiceTaskFeedback().send(getId(),
					new MessageHandler("info.generating.risk_sheet", "Generating risk sheet", 12));

			if (!directs.isEmpty())
				addEstimation(worksheetPart.getContents(), directs, scaleTypes);

			getServiceTaskFeedback().send(getId(),
					new MessageHandler("info.generating.risk_sheet", "Generating risk sheet", 50));

			if (!indirects.isEmpty())
				addEstimation(worksheetPart.getContents(), indirects, scaleTypes);

			getServiceTaskFeedback().send(getId(),
					new MessageHandler("info.generating.risk_sheet", "Generating risk sheet", 80));

			if (!cias.isEmpty())
				addEstimation(worksheetPart.getContents(), cias, scaleTypes);

			getServiceTaskFeedback().send(getId(),
					new MessageHandler("info.saving.risk_sheet", "Saving risk sheet", 90));

			ExcelHelper.applyHeaderAndFooter(EXCEL_HEADER_FOOTER_SHEET_NAME, name, spreadsheetMLPackage);

			final String filename = String.format(Constant.ITR_FILE_NAMING_WIHT_CTRL,
					Utils.cleanUpFileName(analysis.findSetting(ExportFileName.RISK_SHEET_EXCEL)),
					Utils.cleanUpFileName(analysis.getCustomer().getOrganisation()),
					Utils.cleanUpFileName(analysis.getLabel()), "RiskSheet", analysis.getVersion(),
					getExtension(spreadsheetMLPackage), System.nanoTime());

			spreadsheetMLPackage.save(file);

			final WordReport report = WordReport.BuildRawRiskSheet(analysis.getIdentifier(), analysis.getLabel(),
					analysis.getVersion(), daoUser.get(username), filename,
					file.length(), FileCopyUtils.copyToByteArray(file));
			daoWordReport.saveOrUpdate(report);
			return report.getId();
		} finally {
			if (file != null)
				InstanceManager.getServiceStorage().delete(file.getName());
		}

	}

	/**
	 * Exports a risk sheet report.
	 *
	 * @return The ID of the generated report.
	 * @throws Docx4JException If an error occurs during the export process.
	 * @throws IOException     If an error occurs during the export process.
	 */
	private long exportReport() throws Docx4JException, IOException {
		final User user = daoUser.get(username);
		final Analysis analysis = daoAnalysis.get(idAnalysis);
		if (analysis == null)
			throw new TrickException("error.analysis.not_found", "Analysis cannot be found");
		if (user == null)
			throw new TrickException("error.user.not_found", "User cannot be found");

		int progress = 2;
		int max = 60;
		int index = 0;

		setLocale(new Locale(analysis.getLanguage().getAlpha2()));

		dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		final File workFile = loadTemplate(analysis.getCustomer(), TrickTemplateType.RISK_SHEET,
				analysis.getLanguage());
		Document document = null;
		MessageHandler messageHandler = null;
		boolean isFirst = true;
		try {
			showRawColumn = analysis.findSetting(AnalysisSetting.ALLOW_RISK_ESTIMATION_RAW_COLUMN);
			getServiceTaskFeedback().send(getId(), messageHandler = new MessageHandler("info.risk_register.compute",
					"Computing risk register", progress));
			final List<Estimation> estimations = Estimation.GenerateEstimation(analysis,
					new ValueFactory(analysis.getParameters()), cssfExportForm.getFilter(),
					Estimation.IdComparator());
			getServiceTaskFeedback().send(getId(),
					messageHandler = new MessageHandler("info.loading.risk_sheet.template",
							"Loading risk sheet template", progress += 5));
			WordprocessingMLPackage wordprocessingMLPackage = WordprocessingMLPackage.load(workFile);
			getServiceTaskFeedback().send(getId(), messageHandler = new MessageHandler("info.preparing.risk_sheet.data",
					"Preparing risk sheet template", progress += 8));
			getServiceTaskFeedback().send(getId(), messageHandler = new MessageHandler("info.generating.risk_sheet",
					"Generating risk sheet", progress += 8));
			if (cssfExportForm.hasOwner())
				estimations.removeIf(estimation -> !cssfExportForm.getOwner().equals(estimation.getOwner()));
			document = wordprocessingMLPackage.getMainDocumentPart().getContents();
			List<ScaleType> types = analysis.findImpacts();
			types.removeIf(scale -> scale.getName().equals(Constant.DEFAULT_IMPACT_NAME));
			for (Estimation estimation : estimations) {
				RiskProfile riskProfile = estimation.getRiskProfile();
				addRiskSheetHeader(document, estimation.getRiskProfile(), isFirst);
				if (isFirst)
					isFirst = false;
				addField(document, getMessage("report.risk_sheet.risk_owner", "Risk owner"), estimation.getOwner());
				addField(document, getMessage("report.risk_sheet.risk_description", "Risk description"),
						riskProfile.getScenario().getDescription());
				if (showRawColumn)
					addTable(document, getMessage("report.risk_sheet.raw_evaluation", "Raw evaluation"),
							estimation.getRawProbaImpact(), types);
				addField(document, getMessage("report.risk_sheet.argumentation", "Argumentation"),
						estimation.getArgumentation());
				if (getCssfExportForm().isCssf())
					addField(document,
							getMessage("report.risk_sheet.customer_concerned", "Financial customers concerned"),
							riskProfile.getAsset().getName());
				addField(document, getMessage("report.risk_sheet.risk_treatment", "Risk treatment"),
						estimation.getRiskTreatment());
				addTable(document, getMessage("report.risk_sheet.net_evaluation", "Net evaluation"),
						estimation.getNetEvaluation(), types);
				RiskStrategy strategy = riskProfile.getRiskStrategy();
				if (strategy == null)
					strategy = RiskStrategy.ACCEPT;
				String response = strategy.getNameToLower();
				addField(document, getMessage("report.risk_sheet.response", "Response strategy"),
						getMessage("label.risk_register.strategy." + response, response));
				if (strategy != RiskStrategy.ACCEPT || !riskProfile.getMeasures().isEmpty()
						|| StringUtils.hasText(riskProfile.getActionPlan())) {
					addActionTable(document, getMessage("report.risk_sheet.action_plan", "Action plan"), riskProfile);
					addTable(document, getMessage("report.risk_sheet.exp_evaluation", "Expected evaluation"),
							riskProfile.getExpProbaImpact(), types);
				}
				messageHandler
						.setProgress((int) (progress + (++index / (double) estimations.size()) * (max - progress)));
			}
			getServiceTaskFeedback().send(getId(),
					new MessageHandler("info.saving.risk_sheet", "Saving risk sheet", max));
			wordprocessingMLPackage.save(workFile);

			final String filename = String.format(Constant.ITR_FILE_NAMING_WIHT_CTRL,
					Utils.cleanUpFileName(analysis.findSetting(ExportFileName.RISK_SHEET_WORD)),
					Utils.cleanUpFileName(analysis.getCustomer().getOrganisation()),
					Utils.cleanUpFileName(analysis.getLabel()), "RiskSheet", analysis.getVersion(),
					"docx", System.nanoTime());

			final WordReport report = WordReport.BuildRiskSheet(analysis.getIdentifier(), analysis.getLabel(),
					analysis.getVersion(), user, filename, workFile.length(),
					FileCopyUtils.copyToByteArray(workFile));
			daoWordReport.saveOrUpdate(report);
			daoAnalysis.saveOrUpdate(analysis);
			return report.getId();
		} finally {
			InstanceManager.getServiceStorage().delete(workFile.getName());
		}
	}

	/**
	 * Represents a table.
	 */
	private Tbl format(Tbl table) {
		switch (table.getTblPr().getTblStyle().getVal()) {
			case "TSTABLERISK":
				return formatTitle(table);
			case "TSTABLEEVALUATION":
				return formatEvolution(table);
			case "TSTABLEMEASURE":
				return formatMeasure(table);
		}
		return table;
	}

	/**
	 * Represents a table in a document.
	 */
	private Tbl formatTitle(Tbl table) {
		int[] headers = { 2172, 2226, 5239 }, cols = { 1127, 1155, 2718 };
		table.getTblPr().getTblW().setType("pct");
		table.getTblPr().getTblW().setW(BigInteger.valueOf(5000));
		for (int i = 0; i < headers.length; i++)
			table.getTblGrid().getGridCol().get(i).setW(BigInteger.valueOf(headers[i]));
		table.getContent().parallelStream().map(tr -> (Tr) tr).forEach(tr -> updateRow(tr, cols, "pct"));
		return table;
	}

	/**
	 * Represents a table in a document.
	 */
	private Tbl formatMeasure(Tbl table) {
		int[] headers = { 1276, 1357, 5616, 1388 }, cols = { 662, 704, 2913, 720 };
		table.getTblPr().getTblW().setType("pct");
		table.getTblPr().getTblW().setW(BigInteger.valueOf(5000));
		for (int i = 0; i < headers.length; i++)
			table.getTblGrid().getGridCol().get(i).setW(BigInteger.valueOf(headers[i]));
		table.getContent().parallelStream().map(tr -> (Tr) tr).forEach(tr -> updateRow(tr, cols, "pct"));
		return table;
	}

	/**
	 * Represents a table in a document.
	 */
	private Tbl formatEvolution(Tbl table) {
		int[] headers = createCols(5000, table.getTblGrid().getGridCol().size()),
				mergeCols = { headers[0], sum(1, headers.length - 2, headers), headers[headers.length - 1] };
		table.getTblPr().getTblW().setType("pct");
		table.getTblPr().getTblW().setW(BigInteger.valueOf(5000));
		for (int i = 0; i < headers.length; i++)
			table.getTblGrid().getGridCol().get(i).setW(BigInteger.valueOf(headers[i]));
		table.getContent().parallelStream().map(tr -> (Tr) tr)
				.forEach(tr -> updateRow(tr, tr.getContent().size() == mergeCols.length ? mergeCols : headers, "pct"));
		return table;
	}

	/**
	 * Creates an array of column sizes based on the given size and count.
	 * 
	 * @param size  the total size of the columns
	 * @param count the number of columns to create
	 * @return an array of column sizes
	 */
	private int[] createCols(int size, int count) {
		int col = size / count;
		int[] cols = new int[count];
		for (int i = 0; i < cols.length; i++)
			cols[i] = col;
		return cols;
	}

	/**
	 * Retrieves a localized message for the given code, with optional parameters
	 * and a default message.
	 *
	 * @param code           The code of the message to retrieve.
	 * @param parameters     Optional parameters to be used in the message.
	 * @param defaultMessage The default message to be used if the code is not
	 *                       found.
	 * @return The localized message.
	 */
	private String getMessage(String code, Object[] parameters, String defaultMeassge) {
		return getMessageSource().getMessage(code, parameters, defaultMeassge, getLocale());
	}

	/**
	 * Retrieves a message from the message source based on the provided code.
	 *
	 * @param code           The code of the message to retrieve.
	 * @param defaultMeassge The default message to return if the code is not found.
	 * @return The retrieved message as a {@link String}.
	 */
	private String getMessage(String code, String defaultMeassge) {
		return getMessageSource().getMessage(code, null, defaultMeassge, getLocale());
	}

	/**
	 * @return the pStyleId
	 */
	private String getpStyleId() {
		return pStyleId;
	}

	/**
	 * Prints the evaluation header in the specified row.
	 *
	 * @param row   The row to print the header in.
	 * @param types The list of scale types.
	 * @param index The starting index for the header cells.
	 */
	private void printEvaluationHeader(Row row, List<ScaleType> types, int index, String type) {
		setValue(row.getC().get(index++), type + " " + getMessage("report.risk_sheet.probability", "Probability (P)"));
		for (ScaleType scaleType : types)
			setValue(row.getC().get(index++),
					type + " " + getMessage("label.impact." + scaleType.getName().toLowerCase(),
							scaleType.getTranslations().containsKey(alpha2)
									? scaleType.getTranslations().get(alpha2).getName()
									: scaleType.getDisplayName()));
		setValue(row.getC().get(index), type + " " + getMessage("report.risk_sheet.importance", "Importance"));
	}

	/**
	 * Prints the risk probability and impact values to the specified row.
	 *
	 * @param row         the row to print the values to
	 * @param index       the starting index of the cells in the row
	 * @param scaleTypes  the list of scale types
	 * @param probaImpact the risk probability and impact values
	 */
	private void printRiskProba(Row row, int index, List<ScaleType> scaleTypes, RiskProbaImpact probaImpact) {
		if (probaImpact == null)
			probaImpact = new RiskProbaImpact();
		setCellInt(row, index++, probaImpact.getProbabilityLevel());
		for (ScaleType scaleType : scaleTypes) {
			IImpactParameter parameter = probaImpact.get(scaleType.getName());
			setCellInt(row, index++, parameter == null ? 0 : parameter.getLevel());
		}

		setCellInt(row, index++, probaImpact.getImportance());
	}

	/**
	 * Sets the value of a cell in a row at the specified index to an integer value.
	 *
	 * @param row   the row containing the cell
	 * @param index the index of the cell in the row
	 * @param value the integer value to set in the cell
	 */
	private void setCellInt(Row row, int index, int value) {
		setValue(row.getC().get(index), value);
	}

	/**
	 * Sets the value of a cell in a row at the specified index with the given
	 * string value.
	 *
	 * @param row   the row containing the cell
	 * @param index the index of the cell in the row
	 * @param value the string value to set in the cell
	 */
	private void setCellString(Row row, int index, String value) {
		setValue(row.getC().get(index), value);
	}

	/**
	 * Sets the text of a cell in a table cell (Tc) element.
	 *
	 * @param tc   the table cell element
	 * @param text the text to set in the cell
	 */
	private void setCellText(Tc tc, String text) {
		setCellText(tc, text, null);
	}

	/**
	 * Sets the text and alignment of a cell in a risk sheet.
	 *
	 * @param cell      the cell to set the text for
	 * @param text      the text to set in the cell
	 * @param alignment the alignment of the text in the cell
	 */
	private void setCellText(Tc cell, String text, TextAlignment alignment) {
		if (cell.getContent().isEmpty())
			cell.getContent().add(new P());
		P paragraph = (P) cell.getContent().get(0);
		cell.getContent().parallelStream().filter(p -> p instanceof P).map(p -> (P) p)
				.forEach(p -> setStyle(p, TC_P_STYLE));
		setText(paragraph, text, alignment);
	}

	/**
	 * Represents a paragraph in a document.
	 */
	private P setStyle(P p, String styleId) {
		if (p.getPPr() == null)
			p.setPPr(new PPr());
		if (p.getPPr().getPStyle() == null)
			p.getPPr().setPStyle(new PStyle());
		p.getPPr().getPStyle().setVal(styleId);
		return p;
	}

	/**
	 * Represents a paragraph element in a document.
	 */
	private P setText(P paragraph, String content) {
		return setText(paragraph, content, null);
	}

	/**
	 * Represents a paragraph in a document.
	 */
	private P setText(P paragraph, String content, TextAlignment alignment) {
		if (alignment != null) {
			if (paragraph.getPPr() == null)
				setStyle(paragraph, getpStyleId());
			if (paragraph.getParent() instanceof Tc) {
				if (paragraph.getPPr().getJc() == null)
					paragraph.getPPr().setJc(new Jc());
				paragraph.getPPr().getJc().setVal(JcEnumeration.fromValue(alignment.getVal()));
			} else
				paragraph.getPPr().setTextAlignment(alignment);
		}
		paragraph.getContent().removeIf(r -> r instanceof R);
		R r = new R();
		Text text = new Text();
		text.setValue(content);
		r.getContent().add(text);
		paragraph.getContent().add(r);
		return paragraph;

	}
}
