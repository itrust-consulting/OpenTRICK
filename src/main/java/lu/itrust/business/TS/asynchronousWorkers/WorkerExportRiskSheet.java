/**
 * 
 */
package lu.itrust.business.TS.asynchronousWorkers;

import static lu.itrust.business.TS.exportation.word.impl.docx4j.Docx4jReportImpl.mergeCell;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.Docx4jReportImpl.verticalMergeCell;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.formatting.Docx4jFormatter.updateRow;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.formatting.Docx4jMeasureFormatter.sum;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.createRow;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.getAddress;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.setValue;

import java.io.File;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.JAXBElement;

import org.docx4j.jaxb.Context;
import org.docx4j.model.table.TblFactory;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.PartName;
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
import org.xlsx4j.sml.CTMergeCell;
import org.xlsx4j.sml.ObjectFactory;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.Worksheet;

import lu.itrust.business.TS.asynchronousWorkers.helper.AsyncCallback;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOUser;
import lu.itrust.business.TS.database.dao.DAOWordReport;
import lu.itrust.business.TS.database.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOUserHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOWordReportHBM;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.form.CSSFExportForm;
import lu.itrust.business.TS.helper.InstanceManager;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.messagehandler.TaskName;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.AnalysisSetting;
import lu.itrust.business.TS.model.assessment.helper.Estimation;
import lu.itrust.business.TS.model.cssf.RiskProbaImpact;
import lu.itrust.business.TS.model.cssf.RiskProfile;
import lu.itrust.business.TS.model.cssf.RiskStrategy;
import lu.itrust.business.TS.model.cssf.helper.CSSFFilter;
import lu.itrust.business.TS.model.general.document.impl.WordReport;
import lu.itrust.business.TS.model.general.helper.ExportType;
import lu.itrust.business.TS.model.parameter.IImpactParameter;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.scale.ScaleType;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescription;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescriptionText;
import lu.itrust.business.TS.usermanagement.User;

/**
 * @author eomar
 *
 */
public class WorkerExportRiskSheet extends WorkerImpl {

	public static String ENG_TEMPLATE;

	public static String FR_TEMPLATE;

	public static String P_STYLE = "BodyOfText";

	public static String TC_P_STYLE = "TabText2";

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

	public WorkerExportRiskSheet(CSSFExportForm cssfExportForm, Integer analysisId, String username) {
		setCssfExportForm(cssfExportForm);
		setUsername(username);
		setIdAnalysis(analysisId);
	}

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

	@Override
	public void run() {
		Session session = null;
		try {
			synchronized (this) {
				if (getWorkersPoolManager() != null && !getWorkersPoolManager().exist(getId()))
					if (!getWorkersPoolManager().add(this))
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
			MessageHandler messageHandler = new MessageHandler("success.export.risk_sheet", "Risk sheet has been successfully exported", 100);
			if (getCssfExportForm().getType() == ExportType.RAW)
				messageHandler.setAsyncCallbacks(new AsyncCallback("download", "Report", reportId));
			else
				messageHandler.setAsyncCallbacks(new AsyncCallback("download", "Report", reportId), new AsyncCallback("reloadSection", "section_riskregister"));
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
				messageHandler = new MessageHandler(((TrickException) e).getCode(), ((TrickException) e).getParameters(), e.getMessage(), e);
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

	private void addEstimation(Worksheet worksheet, ObjectFactory factory, List<Estimation> estimations, List<ScaleType> types) {
		int size = 16 + types.size() * 3;
		for (Estimation estimation : estimations) {
			Row row = createRow(worksheet.getSheetData(), size);
			String scenarioType = estimation.getScenario().getType().getName();
			String category = getMessage("label.scenario.type." + scenarioType.replace("-", "_").toLowerCase(), scenarioType);
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
				MeasureDescriptionText descriptionText = description.getMeasureDescriptionTextByAlpha2(getLocale().getLanguage());
				String date = dateFormat.format(measure.getPhase().getEndDate());
				actionPlan.add(getMessage("report.risk_profile.action_plan.measure",
						new Object[] { description.getStandard().getLabel(), description.getReference(), descriptionText.getDomain(), date },
						String.format("%s: %s, %s; %s", description.getStandard().getLabel(), description.getReference(), descriptionText.getDomain(), date)));
			}

			if (actionPlan.isEmpty())
				setCellString(row, index++, estimation.getActionPlan() == null || estimation.getActionPlan().isEmpty() ? "" : estimation.getActionPlan());
			else {
				if (!(estimation.getActionPlan() == null || estimation.getActionPlan().isEmpty()))
					actionPlan.add(estimation.getActionPlan());
				setCellString(row, index++, String.join("\n\r", actionPlan));
			}
		}
	}

	private void addField(Document document, String title, String content) {
		addTitle(document, title);
		addFieldContent(document, content);
	}

	private void addFieldContent(Document document, String content) {
		if (content == null || content.isEmpty())
			return;
		String[] texts = content.split("(\r\n|\n\r|\r|\n)");
		for (int i = 0; i < texts.length; i++)
			document.getContent().add(setText(new P(), texts[i]));
	}

	private void addFieldContent(Tc cell, String text) {
		addFieldContent(cell, text, false);
	}

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

	private void addHeader(Worksheet worksheet, ObjectFactory factory, List<ScaleType> types) {
		int rowCount = showRawColumn ? types.size() * 3 + 16 : types.size() * 2 + 14;
		Row row = factory.createRow(), row1 = factory.createRow();
		for (int i = 0; i < rowCount; i++) {
			row.getC().add(factory.createCell());
			row1.getC().add(factory.createCell());
		}

		worksheet.getSheetData().getRow().add(row);
		worksheet.getSheetData().getRow().add(row1);

		int step = 2, size = types.size() + step, netIndex = (showRawColumn ? 6 + types.size() : 4), expIndex = netIndex + types.size() + step,
				index = expIndex + types.size() + step;
		setValue(row.getC().get(0), getMessage("report.risk_sheet.risk_id", "Risk ID"));
		setValue(row.getC().get(1), getMessage("report.risk_sheet.risk_category", "Category"));
		setValue(row.getC().get(2), getMessage("report.risk_sheet.title", "Title"));
		setValue(row.getC().get(3), getMessage("report.risk_sheet.risk_owner", "Risk owner"));
		if (showRawColumn)
			setValue(row.getC().get(4), getMessage("report.risk_sheet.raw_evaluation", "Raw evaluation"));
		setValue(row.getC().get(netIndex), getMessage("report.risk_sheet.net_evaluation", "Net evaluation"));
		setValue(row.getC().get(expIndex), getMessage("report.risk_sheet.exp_evaluation", "Expected evaluation"));
		setValue(row.getC().get(index++), getMessage("report.risk_sheet.risk_description", "Risk description"));
		setValue(row.getC().get(index++), getMessage("report.risk_sheet.argumentation", "Argumentation"));
		setValue(row.getC().get(index++), getMessage("report.risk_sheet.customer_concerned", "Financial customers concerned"));
		setValue(row.getC().get(index++), getMessage("report.risk_sheet.risk_treatment", "Risk treatment"));
		setValue(row.getC().get(index++), getMessage("report.risk_sheet.response", "Response strategy"));
		setValue(row.getC().get(index++), getMessage("report.risk_sheet.action_plan", "Action plan"));
		if (showRawColumn)
			printEvaluationHeader(row1, types, 4);
		printEvaluationHeader(row1, types, netIndex);
		printEvaluationHeader(row1, types, expIndex);

		worksheet.setMergeCells(factory.createCTMergeCells());

		for (int i = 0; i < 4; i++) {
			CTMergeCell mergeCell = factory.createCTMergeCell();
			worksheet.getMergeCells().getMergeCell().add(mergeCell);
			mergeCell.setRef(getAddress(1, i, 2, i));
		}

		for (int i = 4; i <= expIndex; i += size) {
			CTMergeCell mergeCell = factory.createCTMergeCell();
			worksheet.getMergeCells().getMergeCell().add(mergeCell);
			mergeCell.setRef(getAddress(1, i, 1, i + size - 1));
		}

		for (int i = expIndex + types.size() + 2; i < index; i++) {
			CTMergeCell mergeCell = factory.createCTMergeCell();
			worksheet.getMergeCells().getMergeCell().add(mergeCell);
			mergeCell.setRef(getAddress(1, i, 2, i));
		}
	}

	private void addRiskSheetHeader(Document document, RiskProfile riskProfile, boolean isFirst) {
		String scenarioType = riskProfile.getScenario().getType().getName();
		String category = getMessage("label.scenario.type." + scenarioType.replace("-", "_").toLowerCase(), scenarioType),
				idRisk = riskProfile.getIdentifier() == null ? "" : riskProfile.getIdentifier();
		String text = getCssfExportForm().isCssf()
				? getMessage("report.risk_sheet.cssf.page_title", new Object[] { category, idRisk }, String.format("Category %s - Risk %s", category, idRisk))
				: getMessage("report.risk_sheet.normal.page_title", new Object[] { category, riskProfile.getAsset().getName() },
						String.format("Category %s - %s", category, riskProfile.getAsset().getName()));
		P paragraph = null;
		if (isFirst)
			paragraph = (P) document.getContent().parallelStream().filter(p -> p instanceof P).findAny().orElse(null);
		if (paragraph == null)
			document.getContent().add(paragraph = new P());

		setStyle(setText(paragraph, text), "Heading1");
		Tbl table = createTable("TSTABLERISK", 2, 3);
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

	private void addTable(Document document, String title, RiskProbaImpact probaImpact, List<ScaleType> types) {

		addTitle(document, title);
		Tbl table = createTable("TSTABLEEVALUATION", 3, 2 + types.size());
		if (probaImpact == null)
			probaImpact = new RiskProbaImpact();

		BooleanDefaultTrue booleandefaulttrue = Context.getWmlObjectFactory().createBooleanDefaultTrue();
		JAXBElement<org.docx4j.wml.BooleanDefaultTrue> booleandefaulttrueWrapped = Context.getWmlObjectFactory().createCTTrPrBaseTblHeader(booleandefaulttrue);

		Tr row = (Tr) table.getContent().get(0);
		TextAlignment alignment = new TextAlignment();
		alignment.setVal("center");
		setCellText(setVAlignment((Tc) row.getContent().get(0), "center"), getMessage("report.risk_sheet.probability", "Probability (P)"));
		setCellText((Tc) row.getContent().get(1), getMessage("report.risk_sheet.impact", "Impact (i)"), alignment);
		setCellText(setVAlignment((Tc) row.getContent().get(types.size() + 1), "center"), getMessage("report.risk_sheet.importance", "Importance"));

		if (row.getTrPr() == null)
			row.setTrPr(new TrPr());

		row.getTrPr().getCnfStyleOrDivIdOrGridBefore().add(booleandefaulttrueWrapped);

		row = (Tr) table.getContent().get(1);
		int index = 1;
		for (ScaleType scaleType : types)
			setCellText((Tc) row.getContent().get(index++), getMessage("label.impact." + scaleType.getName().toLowerCase(),
					scaleType.getTranslations().containsKey(alpha2) ? scaleType.getTranslations().get(alpha2).getName() : scaleType.getDisplayName()), alignment);

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

	private Tc setVAlignment(Tc tc, String alignment) {
		if (tc.getTcPr() == null)
			tc.setTcPr(new TcPr());
		if (tc.getTcPr().getVAlign() == null)
			tc.getTcPr().setVAlign(new CTVerticalJc());
		tc.getTcPr().getVAlign().setVal(STVerticalJc.fromValue(alignment));
		return tc;
	}

	private void addActionTable(Document document, String title, RiskProfile riskProfile) {

		addTitle(document, title);
		if (!riskProfile.getMeasures().isEmpty()) {
			Tbl table = createTable("TSTABLEMEASURE", riskProfile.getMeasures().size() + 1, 4);
			Tr row = (Tr) table.getContent().get(0);
			setCellText((Tc) row.getContent().get(0), getMessage("report.risk_sheet.measure.standard", "Standard"));
			setCellText((Tc) row.getContent().get(1), getMessage("report.risk_sheet.measure.reference", "Reference"));
			setCellText((Tc) row.getContent().get(2), getMessage("report.risk_sheet.measure.domain", "Domain"));
			setCellText((Tc) row.getContent().get(3), getMessage("report.risk_sheet.measure.due_date", "Due date"));
			int index = 1;
			for (Measure measure : riskProfile.getMeasures()) {
				row = (Tr) table.getContent().get(index++);
				MeasureDescription description = measure.getMeasureDescription();
				MeasureDescriptionText descriptionText = description.getMeasureDescriptionTextByAlpha2(getLocale().getLanguage());
				addFieldContent((Tc) row.getContent().get(0), description.getStandard().getLabel());
				addFieldContent((Tc) row.getContent().get(1), description.getReference());
				addFieldContent((Tc) row.getContent().get(2), descriptionText.getDomain());
				addFieldContent((Tc) row.getContent().get(3), dateFormat.format(measure.getPhase().getEndDate()));
			}
			document.getContent().add(format(table));
		}

		if (!StringUtils.isEmpty(riskProfile.getActionPlan()))
			addFieldContent(document, riskProfile.getActionPlan());

	}

	private void addTitle(Document document, String title) {
		document.getContent().add(setStyle(setText(new P(), title), "TSTitle"));
	}

	private P createP(Tc cell) {
		P p = new P();
		cell.getContent().add(p);
		return p;
	}

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

	private long exportData() throws Exception {
		final File file = InstanceManager.getServiceStorage().createTmpFile();
		try {
			getServiceTaskFeedback().send(getId(), new MessageHandler("info.preparing.risk_sheet.data", "Preparing risk sheet template", 2));
			final ObjectFactory factory = org.xlsx4j.jaxb.Context.getsmlObjectFactory();
			final SpreadsheetMLPackage spreadsheetMLPackage = SpreadsheetMLPackage.createPackage();
			final Analysis analysis = daoAnalysis.get(idAnalysis);
			final List<ScaleType> scaleTypes = analysis.findImpacts();
			final CSSFFilter cssfFilter = cssfExportForm.getFilter();
			final ValueFactory valueFactory = new ValueFactory(analysis.getParameters());
			final List<Estimation> directs = new LinkedList<>(), indirects = new LinkedList<>(), cias = new LinkedList<>();
			setLocale(new Locale(analysis.getLanguage().getAlpha2()));
			if (getLocale().getLanguage().equals("fr"))
				dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			else
				dateFormat = new SimpleDateFormat("MM-dd-yyyy");

			showRawColumn = analysis.findSetting(AnalysisSetting.ALLOW_RISK_ESTIMATION_RAW_COLUMN);
			scaleTypes.removeIf(scale -> scale.getName().equals(Constant.DEFAULT_IMPACT_NAME));
			final String internalName = String.format("RISK_SHEET_%s_v%s.xlsx", analysis.getLabel().replaceAll("/|-|:|.|&", "_"), analysis.getVersion());
			final WorksheetPart worksheetPart = spreadsheetMLPackage.createWorksheetPart(new PartName("/xl/worksheets/sheet1.xml"),
					getMessageSource().getMessage("label.raw.risk_sheet", null, "Raw risk sheet", getLocale()), 1);

			Estimation.GenerateEstimation(analysis, cssfFilter, valueFactory, directs, indirects, cias);
			getServiceTaskFeedback().send(getId(), new MessageHandler("info.generating.risk_sheet", "Generating risk sheet", 10));
			addHeader(worksheetPart.getContents(), factory, scaleTypes);
			getServiceTaskFeedback().send(getId(), new MessageHandler("info.generating.risk_sheet", "Generating risk sheet", 12));
			addEstimation(worksheetPart.getContents(), factory, directs, scaleTypes);
			getServiceTaskFeedback().send(getId(), new MessageHandler("info.generating.risk_sheet", "Generating risk sheet", 50));
			if (!indirects.isEmpty())
				addEstimation(worksheetPart.getContents(), factory, indirects, scaleTypes);
			getServiceTaskFeedback().send(getId(), new MessageHandler("info.generating.risk_sheet", "Generating risk sheet", 80));
			if (!cias.isEmpty())
				addEstimation(worksheetPart.getContents(), factory, cias, scaleTypes);
			getServiceTaskFeedback().send(getId(), new MessageHandler("info.saving.risk_sheet", "Saving risk sheet", 90));
			spreadsheetMLPackage.save(file);
			WordReport report = WordReport.BuildRawRiskSheet(analysis.getIdentifier(), analysis.getLabel(), analysis.getVersion(), daoUser.get(username), internalName,
					file.length(), FileCopyUtils.copyToByteArray(file));
			daoWordReport.saveOrUpdate(report);
			return report.getId();
		} finally {
			if (file != null)
				InstanceManager.getServiceStorage().delete(file.getName());
		}

	}

	private long exportReport() throws Exception {
		final User user = daoUser.get(username);
		final Analysis analysis = daoAnalysis.get(idAnalysis);
		if (analysis == null)
			throw new TrickException("error.analysis.not_found", "Analysis cannot be found");
		if (user == null)
			throw new TrickException("error.user.not_found", "User cannot be found");
		int progress = 2, max = 60, index = 0;
		setLocale(new Locale(analysis.getLanguage().getAlpha2()));
		dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		final File workFile = InstanceManager.getServiceStorage().createTmpFile();
		Document document = null;
		MessageHandler messageHandler = null;
		boolean isFirst = true;
		try {
			showRawColumn = analysis.findSetting(AnalysisSetting.ALLOW_RISK_ESTIMATION_RAW_COLUMN);
			getServiceTaskFeedback().send(getId(), messageHandler = new MessageHandler("info.risk_register.compute", "Computing risk register", progress));
			final List<Estimation> estimations = Estimation.GenerateEstimation(analysis, new ValueFactory(analysis.getParameters()), cssfExportForm.getFilter(),
					Estimation.IdComparator());
			getServiceTaskFeedback().send(getId(), messageHandler = new MessageHandler("info.loading.risk_sheet.template", "Loading risk sheet template", progress += 5));
			final String filename = String.format("RISK_SHEET_%s_v%s.docx", analysis.getLabel().replaceAll("/|-|:|.|&", "_"), analysis.getVersion());
			final String templatePath = String.format("docx/%s.docx", analysis.getLanguage().getAlpha2().equalsIgnoreCase("fr") ? FR_TEMPLATE : ENG_TEMPLATE);
			InstanceManager.getServiceStorage().copy(templatePath, workFile.getName());
			WordprocessingMLPackage wordprocessingMLPackage = WordprocessingMLPackage.load(workFile);
			getServiceTaskFeedback().send(getId(), messageHandler = new MessageHandler("info.preparing.risk_sheet.data", "Preparing risk sheet template", progress += 8));
			getServiceTaskFeedback().send(getId(), messageHandler = new MessageHandler("info.generating.risk_sheet", "Generating risk sheet", progress += 8));
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
				addField(document, getMessage("report.risk_sheet.risk_description", "Risk description"), riskProfile.getScenario().getDescription());
				if (showRawColumn)
					addTable(document, getMessage("report.risk_sheet.raw_evaluation", "Raw evaluation"), estimation.getRawProbaImpact(), types);
				addField(document, getMessage("report.risk_sheet.argumentation", "Argumentation"), estimation.getArgumentation());
				if (getCssfExportForm().isCssf())
					addField(document, getMessage("report.risk_sheet.customer_concerned", "Financial customers concerned"), riskProfile.getAsset().getName());
				addField(document, getMessage("report.risk_sheet.risk_treatment", "Risk treatment"), estimation.getRiskTreatment());
				addTable(document, getMessage("report.risk_sheet.net_evaluation", "Net evaluation"), estimation.getNetEvaluation(), types);
				RiskStrategy strategy = riskProfile.getRiskStrategy();
				if (strategy == null)
					strategy = RiskStrategy.ACCEPT;
				String response = strategy.getNameToLower();
				addField(document, getMessage("report.risk_sheet.response", "Response strategy"), getMessage("label.risk_register.strategy." + response, response));
				if (strategy != RiskStrategy.ACCEPT || !(riskProfile.getMeasures().isEmpty() && StringUtils.isEmpty(riskProfile.getActionPlan()))) {
					addActionTable(document, getMessage("report.risk_sheet.action_plan", "Action plan"), riskProfile);
					addTable(document, getMessage("report.risk_sheet.exp_evaluation", "Expected evaluation"), riskProfile.getExpProbaImpact(), types);
				}
				messageHandler.setProgress((int) (progress + (++index / (double) estimations.size()) * (max - progress)));
			}
			getServiceTaskFeedback().send(getId(), new MessageHandler("info.saving.risk_sheet", "Saving risk sheet", max));
			wordprocessingMLPackage.save(workFile);
			WordReport report = WordReport.BuildRiskSheet(analysis.getIdentifier(), analysis.getLabel(), analysis.getVersion(), user, filename, workFile.length(),
					FileCopyUtils.copyToByteArray(workFile));
			daoWordReport.saveOrUpdate(report);
			daoAnalysis.saveOrUpdate(analysis);
			return report.getId();
		} finally {
			InstanceManager.getServiceStorage().delete(workFile.getName());
		}
	}

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

	private Tbl formatTitle(Tbl table) {
		int[] headers = { 2172, 2226, 5239 }, cols = { 1127, 1155, 2718 };
		table.getTblPr().getTblW().setType("pct");
		table.getTblPr().getTblW().setW(BigInteger.valueOf(5000));
		for (int i = 0; i < headers.length; i++)
			table.getTblGrid().getGridCol().get(i).setW(BigInteger.valueOf(headers[i]));
		table.getContent().parallelStream().map(tr -> (Tr) tr).forEach(tr -> updateRow(tr, cols, "pct"));
		return table;
	}

	private Tbl formatMeasure(Tbl table) {
		int[] headers = { 1276, 1357, 5616, 1388 }, cols = { 662, 704, 2913, 720 };
		table.getTblPr().getTblW().setType("pct");
		table.getTblPr().getTblW().setW(BigInteger.valueOf(5000));
		for (int i = 0; i < headers.length; i++)
			table.getTblGrid().getGridCol().get(i).setW(BigInteger.valueOf(headers[i]));
		table.getContent().parallelStream().map(tr -> (Tr) tr).forEach(tr -> updateRow(tr, cols, "pct"));
		return table;
	}

	private Tbl formatEvolution(Tbl table) {
		int[] headers = createCols(5000, table.getTblGrid().getGridCol().size()), mergeCols = { headers[0], sum(1, headers.length - 2, headers), headers[headers.length - 1] };
		table.getTblPr().getTblW().setType("pct");
		table.getTblPr().getTblW().setW(BigInteger.valueOf(5000));
		for (int i = 0; i < headers.length; i++)
			table.getTblGrid().getGridCol().get(i).setW(BigInteger.valueOf(headers[i]));
		table.getContent().parallelStream().map(tr -> (Tr) tr).forEach(tr -> updateRow(tr, tr.getContent().size() == mergeCols.length ? mergeCols : headers, "pct"));
		return table;
	}

	private int[] createCols(int size, int count) {
		int col = size / count;
		int[] cols = new int[count];
		for (int i = 0; i < cols.length; i++)
			cols[i] = col;
		return cols;
	}

	private String getMessage(String code, Object[] parameters, String defaultMeassge) {
		return getMessageSource().getMessage(code, parameters, defaultMeassge, getLocale());
	}

	private String getMessage(String code, String defaultMeassge) {
		return getMessageSource().getMessage(code, null, defaultMeassge, getLocale());
	}

	/**
	 * @return the pStyleId
	 */
	private String getpStyleId() {
		return pStyleId;
	}

	private void printEvaluationHeader(Row row, List<ScaleType> types, int index) {
		setValue(row.getC().get(index++), getMessage("report.risk_sheet.probability", "Probability (P)"));
		for (ScaleType scaleType : types)
			setValue(row.getC().get(index++), getMessage("label.impact." + scaleType.getName().toLowerCase(),
					scaleType.getTranslations().containsKey(alpha2) ? scaleType.getTranslations().get(alpha2).getName() : scaleType.getDisplayName()));
		setValue(row.getC().get(index++), getMessage("report.risk_sheet.importance", "Importance"));
	}

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

	private void setCellInt(Row row, int index, int value) {
		setValue(row.getC().get(index), value);
	}

	private void setCellString(Row row, int index, String value) {
		setValue(row.getC().get(index), value);
	}

	private void setCellText(Tc tc, String text) {
		setCellText(tc, text, null);
	}

	private void setCellText(Tc cell, String text, TextAlignment alignment) {
		if (cell.getContent().isEmpty())
			cell.getContent().add(new P());
		P paragraph = (P) cell.getContent().get(0);
		cell.getContent().parallelStream().filter(p -> p instanceof P).map(p -> (P) p).forEach(p -> setStyle(p, TC_P_STYLE));
		setText(paragraph, text, alignment);
	}

	private P setStyle(P p, String styleId) {
		if (p.getPPr() == null)
			p.setPPr(new PPr());
		if (p.getPPr().getPStyle() == null)
			p.getPPr().setPStyle(new PStyle());
		p.getPPr().getPStyle().setVal(styleId);
		return p;
	}

	private P setText(P paragraph, String content) {
		return setText(paragraph, content, null);
	}

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
