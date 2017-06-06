package lu.itrust.business.TS.exportation.word.impl.docx4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.docx4j.XmlUtils;
import org.docx4j.jaxb.Context;
import org.docx4j.jaxb.XPathBinderAssociationIsPartialException;
import org.docx4j.model.styles.StyleUtil;
import org.docx4j.model.table.TblFactory;
import org.docx4j.openpackaging.contenttype.CTOverride;
import org.docx4j.openpackaging.contenttype.ContentTypeManager;
import org.docx4j.openpackaging.contenttype.ContentTypes;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Body;
import org.docx4j.wml.CTOdso.Table;
import org.docx4j.wml.CTRPrChange.RPr;
import org.docx4j.wml.CTTblPrBase;
import org.docx4j.wml.CTTblPrBase.TblStyle;
import org.docx4j.wml.CTTblPrBase.TblStyleColBandSize;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.Document;
import org.docx4j.wml.FldChar;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase.TextAlignment;
import org.docx4j.wml.R;
import org.docx4j.wml.STFldCharType;
import org.docx4j.wml.Style;
import org.docx4j.wml.Styles;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.TblPr;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;
import org.openxmlformats.schemas.officeDocument.x2006.customProperties.CTProperty;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.springframework.context.MessageSource;

import lu.itrust.business.TS.component.ChartGenerator;
import lu.itrust.business.TS.component.TrickLogManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.exportation.helper.ReportExcelSheet;
import lu.itrust.business.TS.exportation.word.ExportReport;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.model.actionplan.ActionPlanMode;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.asset.AssetType;
import lu.itrust.business.TS.model.general.Phase;
import lu.itrust.business.TS.model.iteminformation.ItemInformation;
import lu.itrust.business.TS.model.iteminformation.helper.ComparatorItemInformation;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.parameter.impl.DynamicParameter;
import lu.itrust.business.TS.model.parameter.impl.SimpleParameter;
import lu.itrust.business.TS.model.riskinformation.RiskInformation;
import lu.itrust.business.TS.model.riskinformation.helper.RiskInformationManager;
import lu.itrust.business.TS.model.scenario.Scenario;
import lu.itrust.business.TS.model.scenario.ScenarioType;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measure.helper.MeasureComparator;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescriptionText;

public abstract class Docx4jWordExporter implements ExportReport {

	protected Analysis analysis = null;

	protected ObjectFactory factory = null;

	private WordprocessingMLPackage wordMLPackage = null;

	protected Document document = null;

	protected String idTask;

	protected DecimalFormat kEuroFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.FRANCE);

	protected String languageAlpha2 = null;

	protected Locale locale = null;

	protected DecimalFormat numberFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.FRANCE);

	protected List<XWPFParagraph> paragraphsToDelete = new LinkedList<>();

	private Map<String, Style> styles = Collections.emptyMap();

	protected ServiceTaskFeedback serviceTaskFeedback;

	protected ValueFactory valueFactory = null;

	private String contextPath;

	private String currentParagraphId;

	private int maxProgress;

	private MessageSource messageSource;

	private int minProgress;

	private int nonApplicableMeasure27001 = 0;

	private int nonApplicableMeasure27002 = 0;

	private int progress;

	private String reportName;

	private File workFile;

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.exportation.word.impl.poi.ExportReport#
	 * exportToWordDocument(lu.itrust.business.TS.model.analysis.Analysis)
	 */
	@Override
	public void exportToWordDocument(Analysis analysis) throws Exception {

		setAnalysis(analysis);
		switch (analysis.getLanguage().getAlpha3().toLowerCase()) {
		case "fra":
			locale = Locale.FRENCH;
			languageAlpha2 = "FR";
			break;
		case "eng":
		default:
			locale = Locale.ENGLISH;
			languageAlpha2 = "EN";
		}

		kEuroFormat.setMaximumFractionDigits(1);
		numberFormat.setMaximumFractionDigits(0);
		serviceTaskFeedback.send(idTask, new MessageHandler("info.create.temporary.word.file", "Create temporary word file", increase(1)));// 1%
		workFile = new File(
				String.format("%s/WEB-INF/tmp/STA_%d_%s_V%s.docm", contextPath, System.nanoTime(), analysis.getLabel().replaceAll("/|-|:|.|&", "_"), analysis.getVersion()));
		if (!workFile.exists())
			workFile.createNewFile();

		valueFactory = new ValueFactory(analysis.getParameters());

		serviceTaskFeedback.send(idTask, new MessageHandler("info.load.word.template", "Loading word template", increase(2)));// 3%

		createDocumentFromTemplate();

		serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.data", "Printing data", increase(2)));// 5%

		setCurrentParagraphId(DEFAULT_PARAGRAHP_STYLE);

		serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.table.item.information", "Printing item information table", increase(5)));// 10%

		generateItemInformation();

		serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.table.asset", "Printing asset table", increase(5)));// 15%

		generateAssets();

		serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.table.scenario", "Printing scenario table", increase(5)));// 20%

		generateScenarios();

		serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.table.estimation", "Printing estimation table", increase(5)));// 25%

		generateAssessements();

		serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.table.threat", "Printing threat table", increase(5)));// 30%

		generateThreats();

		serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.table.impact", "Printing impact table", increase(5)));// 35%

		// generateExtendedParameters(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME);

		serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.table.probabilty", "Printing probabilty table", increase(5)));// 40%

		// generateExtendedParameters(Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME);

		// generateOtherData();

		serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.table.action.plan", "Printing action plan table", increase(5)));// 45%

		// generateActionPlan();

		serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.table.summary", "Printing summary table", increase(5)));// 55%

		// generateActionPlanSummary();

		serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.table.measure", "Printing measure table", increase(5)));// 60%

		// generateMeasures();

		serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.chart", "Printing chart", increase(5)));// 70%

		// generateGraphics();

		// updateProperties();

		wordMLPackage.save(workFile);

	}

	private void createDocumentFromTemplate() throws Docx4JException, URISyntaxException {

		WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new File(String.format("%s/WEB-INF/data/%s.dotm", contextPath, reportName)));

		ContentTypeManager contentTypeManager = wordMLPackage.getContentTypeManager();

		CTOverride ctOverride = contentTypeManager.getOverrideContentType().get(new URI("/word/document.xml"));

		ctOverride.setContentType(ContentTypes.WORDPROCESSINGML_DOCUMENT_MACROENABLED);

		wordMLPackage.save(workFile);

		this.wordMLPackage = WordprocessingMLPackage.load(workFile);

		setDocument(this.wordMLPackage.getMainDocumentPart().getContents());

		factory = Context.getWmlObjectFactory();

		Styles styles = this.wordMLPackage.getMainDocumentPart().getStyleDefinitionsPart().getContents();

		this.styles = styles.getStyle().parallelStream().collect(Collectors.toMap(Style::getStyleId, Function.identity()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.exportation.word.impl.poi.ExportReport#getAnalysis(
	 * )
	 */
	@Override
	public Analysis getAnalysis() {
		return analysis;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.exportation.word.impl.poi.ExportReport#
	 * getContextPath()
	 */
	@Override
	public String getContextPath() {
		return contextPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.exportation.word.impl.poi.ExportReport#
	 * getCurrentParagraphId()
	 */
	@Override
	public String getCurrentParagraphId() {
		return currentParagraphId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.exportation.word.impl.poi.ExportReport#getDocument(
	 * )
	 */
	public Document getDocument() {
		return document;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.exportation.word.impl.poi.ExportReport#getIdTask()
	 */
	@Override
	public String getIdTask() {
		return idTask;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.exportation.word.impl.poi.ExportReport#getLocale()
	 */
	@Override
	public Locale getLocale() {
		return locale;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.exportation.word.impl.poi.ExportReport#
	 * getMaxProgress()
	 */
	@Override
	public int getMaxProgress() {
		if (maxProgress <= 0)
			return 100;
		return maxProgress;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.exportation.word.impl.poi.ExportReport#
	 * getMessageSource()
	 */
	@Override
	public MessageSource getMessageSource() {
		return messageSource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.exportation.word.impl.poi.ExportReport#
	 * getMinProgress()
	 */
	@Override
	public int getMinProgress() {
		return minProgress;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.exportation.word.impl.poi.ExportReport#getProgress(
	 * )
	 */
	@Override
	public int getProgress() {
		return progress;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.exportation.word.impl.poi.ExportReport#
	 * getReportName()
	 */
	@Override
	public String getReportName() {
		return reportName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.exportation.word.impl.poi.ExportReport#
	 * getServiceTaskFeedback()
	 */
	@Override
	public ServiceTaskFeedback getServiceTaskFeedback() {
		return serviceTaskFeedback;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.exportation.word.impl.poi.ExportReport#getWorkFile(
	 * )
	 */
	@Override
	public File getWorkFile() {
		return workFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.exportation.word.impl.poi.ExportReport#increase(
	 * int)
	 */
	@Override
	public int increase(int value) {
		if (!(value < 0 || value > 100)) {
			progress += value;
			if (progress > 100)
				setProgress(100);
		}
		return (int) (minProgress + (maxProgress - minProgress) * 0.01 * progress);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.exportation.word.impl.poi.ExportReport#setAnalysis(
	 * lu.itrust.business.TS.model.analysis.Analysis)
	 */
	@Override
	public void setAnalysis(Analysis analysis) {
		this.analysis = analysis;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.exportation.word.impl.poi.ExportReport#
	 * setContextPath(java.lang.String)
	 */
	@Override
	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.exportation.word.impl.poi.ExportReport#
	 * setCurrentParagraphId(java.lang.String)
	 */
	@Override
	public void setCurrentParagraphId(String currentParagraphId) {
		this.currentParagraphId = currentParagraphId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.exportation.word.impl.poi.ExportReport#setDocument(
	 * org.apache.poi.xwpf.usermodel.XWPFDocument)
	 */
	public void setDocument(Document document) {
		this.document = document;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.exportation.word.impl.poi.ExportReport#setIdTask(
	 * java.lang.String)
	 */
	@Override
	public void setIdTask(String idTask) {
		this.idTask = idTask;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.exportation.word.impl.poi.ExportReport#setLocale(
	 * java.util.Locale)
	 */
	@Override
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.exportation.word.impl.poi.ExportReport#
	 * setMaxProgress(int)
	 */
	@Override
	public void setMaxProgress(int maxProgress) {
		this.maxProgress = maxProgress;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.exportation.word.impl.poi.ExportReport#
	 * setMessageSource(org.springframework.context.MessageSource)
	 */
	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.exportation.word.impl.poi.ExportReport#
	 * setMinProgress(int)
	 */
	@Override
	public void setMinProgress(int minProgress) {
		this.minProgress = minProgress;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.exportation.word.impl.poi.ExportReport#
	 * setReportName(java.lang.String)
	 */
	@Override
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.exportation.word.impl.poi.ExportReport#
	 * setServiceTaskFeedback(lu.itrust.business.TS.database.service.
	 * ServiceTaskFeedback)
	 */
	@Override
	public void setServiceTaskFeedback(ServiceTaskFeedback serviceTaskFeedback) {
		this.serviceTaskFeedback = serviceTaskFeedback;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.exportation.word.impl.poi.ExportReport#setWorkFile(
	 * java.io.File)
	 */
	@Override
	public void setWorkFile(File workFile) {
		this.workFile = workFile;
	}

	protected R addCellNumber(Tc cell, String number) {
		return addCellNumber(cell, number, false);
	}

	protected R addCellNumber(Tc cell, String number, boolean isBold) {
		P paragraph = cell.getContent().size() == 1 ? (P) cell.getContent().get(0) : addCellParagraph(cell);
		setStyle(paragraph, getCurrentParagraphId());
		setText(paragraph, number, createAlignment("right"));
		R r = (R) paragraph.getContent().get(0);
		if (isBold) {
			if (r.getRPr() != null)
				r.setRPr(factory.createRPr());
			r.getRPr().setB(factory.createBooleanDefaultTrue());
		}
		return r;
	}

	protected P addCellParagraph(Tc cell, String text) {
		return addCellParagraph(cell, text, false);
	}

	protected P addCellParagraph(Tc cell, String text, boolean add) {
		P p = (P) (!add && cell.getContent().size() == 1 ? cell.getContent().get(0) : addCellParagraph(cell));
		if (text == null)
			text = "";
		String[] texts = text.split("(\r\n|\n\r|\r|\n)");
		for (int i = 0; i < texts.length; i++) {
			if (i > 0)
				p = addCellParagraph(cell);
			setStyle(p, getCurrentParagraphId());
			setText(p, texts[i], null);
		}
		return p;
	}

	protected P setStyle(P p, String styleId) {
		if (p.getPPr() == null)
			p.setPPr(factory.createPPr());
		if (p.getPPr().getPStyle() == null)
			p.getPPr().setPStyle(factory.createPPrBasePStyle());
		p.getPPr().getPStyle().setVal(styleId);
		return p;
	}

	protected R setText(R r, String message) {
		Text text = factory.createText();
		text.setValue(message);
		r.getContent().add(text);
		return r;
	}
	
	protected R setInstrText(R r, String content) {
		Text text = factory.createText();
		JAXBElement<Text> textWrapped = factory.createRInstrText(text);
		r.getContent().add(textWrapped);
		text.setValue(content);
		text.setSpace("preserve");
		return r;
	}
	
	protected P addTableCaption(String value) {
		P paragraph = setStyle(factory.createP(), "Caption");
		paragraph.getContent().add(setText(factory.createR(), getMessage("report.capttion.table.name", null, "Table", locale)));
		paragraph.getContent().add(createSpecialRun(STFldCharType.BEGIN));
		paragraph.getContent().add(setInstrText(factory.createR(),"SEQ Table \\* ARABIC "));
		paragraph.getContent().add(createSpecialRun(STFldCharType.SEPARATE));
		paragraph.getContent().add(setText(factory.createR(), "1"));
		paragraph.getContent().add(createSpecialRun(STFldCharType.END));
		paragraph.getContent().add(setText(factory.createR(),": "+value));
		return paragraph;
		
		
	}

	protected R createSpecialRun(STFldCharType type) {
		R run = factory.createR();
		FldChar fldChar = factory.createFldChar();
		fldChar.setFldCharType(type);
		run.getContent().add(fldChar);
		return run;
	}


	private P addCellParagraph(Tc cell) {
		P p = factory.createP();
		cell.getContent().add(p);
		return p;
	}

	protected P findTableAnchor(String name) throws XPathBinderAssociationIsPartialException, JAXBException {
		final String xpath = "//w:bookmarkStart[@w:name='" + name + "']/..";
		List<Object> objects = wordMLPackage.getMainDocumentPart().getJAXBNodesViaXPath(xpath, false);
		return objects.stream().findFirst().map(paragraph -> (P) XmlUtils.unwrap(paragraph)).orElse(null);
	}

	protected String formatLikelihood(String likelihood) {
		try {
			return kEuroFormat.format(Double.parseDouble(likelihood));
		} catch (Exception e) {
			return likelihood;
		}
	}

	protected abstract void generateActionPlan() throws Exception;

	protected abstract void generateActionPlanSummary() throws Exception;

	protected abstract void generateAssessements() throws XPathBinderAssociationIsPartialException, JAXBException;

	protected abstract void generateAssets(String name, List<Asset> assets) throws XPathBinderAssociationIsPartialException, JAXBException;

	@SuppressWarnings("unchecked")
	protected void generateComplianceGraphic(ReportExcelSheet reportExcelSheet) throws OpenXML4JException, IOException {
		if (reportExcelSheet == null)
			return;
		String standard = reportExcelSheet.getName().endsWith("27001") ? "27001" : "27002";
		List<Measure> measures = (List<Measure>) analysis.findMeasureByStandard(standard);
		ValueFactory factory = new ValueFactory(analysis.getDynamicParameters());
		if (measures == null)
			return;
		XSSFSheet xssfSheet = reportExcelSheet.getXssfWorkbook().getSheetAt(0);
		Map<String, Object[]> compliances = ChartGenerator.ComputeComplianceBefore(measures, factory);
		int rowCount = 0;
		String phaseLabel = getMessage("label.chart.series.current_level", null, "Current Level", locale);
		if (xssfSheet.getRow(rowCount) == null)
			xssfSheet.createRow(rowCount);
		xssfSheet.getRow(rowCount).createCell(0);
		xssfSheet.getRow(rowCount).createCell(1);
		xssfSheet.getRow(rowCount).getCell(0).setCellValue(getMessage("report.compliance.chapter", null, "Chapter", locale));
		xssfSheet.getRow(rowCount++).getCell(1).setCellValue(phaseLabel);
		for (String key : compliances.keySet()) {
			Object[] compliance = compliances.get(key);
			if (xssfSheet.getRow(rowCount) == null)
				xssfSheet.createRow(rowCount);
			if (xssfSheet.getRow(rowCount).getCell(0) == null)
				xssfSheet.getRow(rowCount).createCell(0);
			if (xssfSheet.getRow(rowCount).getCell(1) == null)
				xssfSheet.getRow(rowCount).createCell(1, CellType.NUMERIC);
			xssfSheet.getRow(rowCount).getCell(0).setCellValue(key);
			xssfSheet.getRow(rowCount++).getCell(1).setCellValue((((Double) compliance[1]).doubleValue() / ((Integer) compliance[0]).doubleValue()) * 0.01);
		}

		Map<Integer, Boolean> actionPlanMeasures = analysis.findIdMeasuresImplementedByActionPlanType(ActionPlanMode.APPN);

		if (!actionPlanMeasures.isEmpty()) {
			List<Phase> phases = analysis.findUsablePhase();
			int columnIndex = 2;
			for (Phase phase : phases) {
				compliances = ChartGenerator.ComputeCompliance(measures, phase, actionPlanMeasures, compliances, factory);
				if (xssfSheet.getRow(rowCount = 0) == null)
					xssfSheet.createRow(rowCount);
				if (xssfSheet.getRow(rowCount).getCell(columnIndex) == null)
					xssfSheet.getRow(rowCount).createCell(columnIndex);
				xssfSheet.getRow(rowCount++).getCell(columnIndex)
						.setCellValue(getMessage("label.chart.phase", new Object[] { phase.getNumber() }, "Phase " + phase.getNumber(), locale));
				for (String key : compliances.keySet()) {
					Object[] compliance = compliances.get(key);
					if (xssfSheet.getRow(rowCount) == null)
						xssfSheet.createRow(rowCount);
					if (xssfSheet.getRow(rowCount).getCell(columnIndex) == null)
						xssfSheet.getRow(rowCount).createCell(columnIndex, CellType.NUMERIC);
					xssfSheet.getRow(rowCount++).getCell(columnIndex).setCellValue((((Double) compliance[1]).doubleValue() / ((Integer) compliance[0]).doubleValue()) * 0.01);
				}
				columnIndex++;
			}
		}
	}

	protected abstract void generateExtendedParameters(String type) throws Exception;

	protected abstract void generateOtherData();

	protected String getDisplayName(AssetType type) {
		return getMessage("label.asset_type." + type.getName().toLowerCase(), null, type.getName(), locale);
	}

	protected String getDisplayName(ScenarioType type) {
		return getMessage("label.scenario.type." + type.getName().toLowerCase(), null, type.getName(), locale);
	}

	protected String getMessage(String code, Object[] parameters, String defaultMessage, Locale locale) {
		return messageSource.getMessage(code, parameters, defaultMessage, locale);
	}

	protected void setCellText(Tc tc, String text) {
		setCellText(tc, text, null);
	}

	protected TblPr getTableStyle(String id) {
		Style style = styles.get(id);
		return style == null ? null : (TblPr) style.getTblPr();
	}

	protected PPr getParagraphStyle(String id) {
		Style style = styles.get(id);
		return style == null ? null : (PPr) style.getPPr();
	}

	protected void setCellText(Tc cell, String text, TextAlignment alignment) {
		if (cell.getContent().isEmpty())
			cell.getContent().add(new P());
		P paragraph = (P) cell.getContent().get(0);
		cell.getContent().parallelStream().filter(p -> p instanceof P).map(p -> (P) p).forEach(p -> setStyle(p, getCurrentParagraphId()));
		setText(paragraph, text, alignment);
	}

	protected void setText(P paragraph, String content) {
		setText(paragraph, content, null);
	}

	protected void setText(P paragraph, String content, TextAlignment alignment) {
		if (alignment != null) {
			if (paragraph.getPPr() == null)
				setStyle(paragraph, getCurrentParagraphId());
			paragraph.getPPr().setTextAlignment(alignment);
		}
		paragraph.getContent().removeIf(r -> r instanceof R);
		R r = factory.createR();
		Text text = factory.createText();
		text.setValue(content);
		r.getContent().add(text);
		paragraph.getContent().add(r);

	}

	protected abstract void writeChart(ReportExcelSheet reportExcelSheet) throws Exception;

	private void generateAssets() throws XPathBinderAssociationIsPartialException, JAXBException {
		generateAssets("Asset", analysis.findSelectedAssets());
		generateAssets("AssetNoSelected", analysis.findNoAssetSelected());
	}

	private void generateGraphics() throws Exception {
		for (PackagePart packagePart : this.document.getPackage().getParts())
			if (packagePart.getPartName().getExtension().contains("xls"))
				writeChart(new ReportExcelSheet(packagePart, String.format("%s/WEB-INF/tmp/", contextPath)));
	}

	private void generateItemInformation() throws Exception {
		setCurrentParagraphId(ExportReport.TS_TAB_TEXT_2);
		P paragraph = findTableAnchor("Scope");
		List<ItemInformation> iteminformations = analysis.getItemInformations();
		Collections.sort(iteminformations, new ComparatorItemInformation());
		if (paragraph != null && iteminformations.size() > 0) {

			// initialise table with 1 row and 1 column after the paragraph
			// cursor
			Tbl table = createTable("TableTSScope", iteminformations.size() + 1, 2);

			TextAlignment alignment = createAlignment("left");

			List<Object> trs = table.getContent();

			Tr row = (Tr) trs.get(0);

			setCellText((Tc) row.getContent().get(0), getMessage("report.scope.title.description", null, "Description", locale));

			setCellText((Tc) row.getContent().get(1), getMessage("report.scope.title.value", null, "Value", locale));

			setRepeatHeader(row);

			int rowIndex = 1;
			// set data
			for (ItemInformation iteminfo : iteminformations) {
				row = (Tr) trs.get(rowIndex++);
				setCellText((Tc) row.getContent().get(0), getMessage("report.scope.name." + iteminfo.getDescription().toLowerCase(), null, iteminfo.getDescription(), locale),
						alignment);
				addCellParagraph((Tc) row.getContent().get(1), iteminfo.getValue());
			}
			document.getContent().add(document.getContent().indexOf(paragraph), table);
		}
	}

	protected void setRepeatHeader(Tr row) {
		if (row.getTrPr() == null)
			row.setTrPr(factory.createTrPr());
		row.getTrPr().getCnfStyleOrDivIdOrGridBefore().add(factory.createCTTrPrBaseTblHeader(factory.createBooleanDefaultTrue()));
	}

	protected Tbl createTable(String styleId, int rows, int cols) {
		int writableWidthTwips = wordMLPackage.getDocumentModel().getSections().get(0).getPageDimensions().getWritableWidthTwips();
		int cellWidthTwips = new Double(Math.floor((writableWidthTwips / cols))).intValue();
		Tbl table = TblFactory.createTable(rows, cols, cellWidthTwips);
		Style value = styles.get(styleId);
		if (value != null)
			table.getTblPr().getTblStyle().setVal(value.getName().getVal());
		return table;
	}

	private void generateMeasures() {
		XWPFParagraph paragraph = null;
		XWPFTable table = null;
		XWPFTableRow row = null;

		paragraph = findTableAnchor("<Measures>");

		// run = paragraph.getRuns().get(0);

		List<AnalysisStandard> analysisStandards = analysis.getAnalysisStandards();
		Map<String, Double> expressionParameters = this.analysis.getDynamicParameters().stream()
				.collect(Collectors.toMap(DynamicParameter::getAcronym, DynamicParameter::getValue));

		if (paragraph != null && analysisStandards.size() > 0) {

			while (!paragraph.getRuns().isEmpty())
				paragraph.removeRun(0);

			boolean isFirst = true;

			Comparator<Measure> comparator = new MeasureComparator();

			setCurrentParagraphId(TS_TAB_TEXT_3);

			for (AnalysisStandard analysisStandard : analysisStandards) {

				// initialise table with 1 row and 1 column after the paragraph
				// cursor
				if (analysisStandard.getMeasures().isEmpty())
					continue;

				if (isFirst)
					isFirst = false;
				else
					paragraph = document.createParagraph();

				paragraph.createRun().addBreak(BreakType.PAGE);

				paragraph = document.createParagraph();

				paragraph.setStyle("TSMeasureTitle");

				paragraph.createRun().setText(analysisStandard.getStandard().getLabel());

				paragraph = document.createParagraph();

				paragraph.setAlignment(ParagraphAlignment.CENTER);

				table = document.insertNewTbl(paragraph.getCTP().newCursor());

				table.setStyleID("TableTSMeasure");
				// set header
				row = table.getRow(0);

				while (row.getTableCells().size() < 16)
					row.createCell();

				setCellText(row.getCell(0), getMessage("report.measure.reference", null, "Ref.", locale));
				setCellText(row.getCell(1), getMessage("report.measure.domain", null, "Domain", locale));
				setCellText(row.getCell(2), getMessage("report.measure.status", null, "ST", locale));
				setCellText(row.getCell(3), getMessage("report.measure.implementation_rate", null, "IR(%)", locale));
				setCellText(row.getCell(4), getMessage("report.measure.internal.workload", null, "IS(md)", locale));
				setCellText(row.getCell(5), getMessage("report.measure.external.workload", null, "ES(md)", locale));
				setCellText(row.getCell(6), getMessage("report.measure.investment", null, "INV(k€)", locale));
				setCellText(row.getCell(7), getMessage("report.measure.life_time", null, "LT(y)", locale));
				setCellText(row.getCell(8), getMessage("report.measure.internal.maintenance", null, "IM(md)", locale));
				setCellText(row.getCell(9), getMessage("report.measure.external.maintenance", null, "EM(md)", locale));
				setCellText(row.getCell(10), getMessage("report.measure.recurrent.investment", null, "RINV(k€)", locale));
				setCellText(row.getCell(11), getMessage("report.measure.cost", null, "CS(k€)", locale));
				setCellText(row.getCell(12), getMessage("report.measure.phase", null, "P", locale));
				setCellText(row.getCell(13), getMessage("report.measure.responsible", null, "Resp.", locale));
				setCellText(row.getCell(14), getMessage("report.measure.to_do", null, "To Do", locale));
				setCellText(row.getCell(15), getMessage("report.measure.comment", null, "Comment", locale));
				// set data
				Collections.sort(analysisStandard.getMeasures(), comparator);

				for (Measure measure : analysisStandard.getMeasures()) {
					row = table.createRow();
					while (row.getTableCells().size() < 2)
						row.createCell();
					setCellText(row.getCell(0), measure.getMeasureDescription().getReference());
					MeasureDescriptionText description = measure.getMeasureDescription().findByLanguage(analysis.getLanguage());
					setCellText(row.getCell(1), description == null ? "" : description.getDomain(), ParagraphAlignment.LEFT);
					if (!measure.getMeasureDescription().isComputable()) {
						String color = measure.getMeasureDescription().getLevel() < 2 ? SUPER_HEAD_COLOR : HEADER_COLOR;
						for (int i = 0; i < 16; i++)
							row.getCell(i).setColor(color);
					} else {
						while (row.getTableCells().size() < 16)
							row.createCell();
						setCellText(row.getCell(2), getMessage("label.measure.status." + measure.getStatus().toLowerCase(), null, measure.getStatus(), locale));
						addCellNumber(row.getCell(3), numberFormat.format(measure.getImplementationRateValue(expressionParameters)));
						addCellNumber(row.getCell(4), kEuroFormat.format(measure.getInternalWL()));
						addCellNumber(row.getCell(5), kEuroFormat.format(measure.getExternalWL()));
						addCellNumber(row.getCell(6), numberFormat.format(measure.getInvestment() * 0.001));
						addCellNumber(row.getCell(7), numberFormat.format(measure.getLifetime()));
						addCellNumber(row.getCell(8), kEuroFormat.format(measure.getInternalMaintenance()));
						addCellNumber(row.getCell(9), kEuroFormat.format(measure.getExternalMaintenance()));
						addCellNumber(row.getCell(10), numberFormat.format(measure.getRecurrentInvestment() * 0.001));
						addCellNumber(row.getCell(11), numberFormat.format(measure.getCost() * 0.001));
						addCellParagraph(row.getCell(12), measure.getPhase().getNumber() + "");
						addCellParagraph(row.getCell(13), measure.getResponsible());
						addCellParagraph(row.getCell(14), measure.getToDo());
						if (Constant.MEASURE_STATUS_NOT_APPLICABLE.equalsIgnoreCase(measure.getStatus()) || measure.getImplementationRateValue(expressionParameters) >= 100) {
							for (int i = 0; i < 16; i++)
								row.getCell(i).setColor(DEFAULT_CELL_COLOR);
							if (measure.getImplementationRateValue(expressionParameters) < 100) {
								if (analysisStandard.getStandard().is(Constant.STANDARD_27002))
									nonApplicableMeasure27002++;
								else if (analysisStandard.getStandard().is(Constant.STANDARD_27001))
									nonApplicableMeasure27001++;
							}
						} else {
							row.getCell(0).setColor(SUB_HEADER_COLOR);
							row.getCell(1).setColor(SUB_HEADER_COLOR);
							row.getCell(11).setColor(measure.getCost() == 0 ? ZERO_COST_COLOR : SUB_HEADER_COLOR);
						}
					}
					addCellParagraph(row.getCell(15), measure.getComment());
				}
			}
		}

	}

	private void generateScenarios() throws XPathBinderAssociationIsPartialException, JAXBException {
		List<Scenario> scenarios = analysis.findSelectedScenarios();
		P paragraph = findTableAnchor("Scenario");
		if (paragraph != null && scenarios.size() > 0) {
			Tbl table = createTable("TableTSScenario", scenarios.size() + 1, 3);
			Tr row = (Tr) table.getContent().get(0);
			setCellText((Tc) row.getContent().get(0), getMessage("report.scenario.title.number.row", null, "Nr", locale));
			setCellText((Tc) row.getContent().get(1), getMessage("report.scenario.title.name", null, "Name", locale));
			setCellText((Tc) row.getContent().get(2), getMessage("report.scenario.title.description", null, "Description", locale));
			setRepeatHeader(row);
			TextAlignment alignment = createAlignment("left");
			int number = 1;
			for (Scenario scenario : scenarios) {
				row = (Tr) table.getContent().get(number);
				setCellText((Tc) row.getContent().get(0), "" + (number++));
				setCellText((Tc) row.getContent().get(1), scenario.getName(), alignment);
				addCellParagraph((Tc) row.getContent().get(2), scenario.getDescription());
			}
			document.getContent().add(document.getContent().indexOf(paragraph), table);
		}
	}

	protected TextAlignment createAlignment(String value) {
		TextAlignment alignment = factory.createPPrBaseTextAlignment();
		alignment.setVal(value);
		return alignment;
	}

	private void generateThreats() throws XPathBinderAssociationIsPartialException, JAXBException {

		List<RiskInformation> riskInformations = analysis.getRiskInformations();

		Map<String, List<RiskInformation>> riskmapping = RiskInformationManager.Split(riskInformations);

		for (String key : riskmapping.keySet()) {

			P paragraph = findTableAnchor(key);

			List<RiskInformation> elements = riskmapping.get(key);

			if (paragraph != null && elements.size() > 0) {
				boolean isFirst = true;
				Tbl table = null;
				Tr row = null;
				int index = 0;
				TextAlignment alignmentLeft = createAlignment("left"), alignmentCenter = createAlignment("center");
				for (RiskInformation riskinfo : elements) {
					if (isFirst) {
						// set header
						if (riskinfo.getCategory().equals("Threat")) {
							table = createTable("TableTS" + key, elements.size() + 1, 6);
							row = (Tr) table.getContent().get(0);
							setCellText((Tc) row.getContent().get(0), getMessage(String.format("report.risk_information.title.%s", "id"), null, "Id", locale));
							setCellText((Tc) row.getContent().get(1),
									getMessage(String.format("report.risk_information.title.%s", key.toLowerCase()), null, key.toLowerCase(), locale));
							setCellText((Tc) row.getContent().get(2), getMessage(String.format("report.risk_information.title.%s", "acro"), null, "Acro", locale));
							setCellText((Tc) row.getContent().get(3), getMessage(String.format("report.risk_information.title.%s", "expo"), null, "Expo.", locale), alignmentLeft);
							setCellText((Tc) row.getContent().get(4), getMessage(String.format("report.risk_information.title.%s", "owner"), null, "Owner", locale));
							setCellText((Tc) row.getContent().get(5), getMessage(String.format("report.risk_information.title.%s", "comment"), null, "Comment", locale));
							setRepeatHeader(row);
						} else {
							table = createTable("TableTS" + key, elements.size() + 1, 5);
							row = (Tr) table.getContent().get(0);
							setCellText((Tc) row.getContent().get(0), getMessage(String.format("report.risk_information.title.%s", "id"), null, "Id", locale));
							setCellText((Tc) row.getContent().get(1),
									getMessage(String.format("report.risk_information.title.%s", key.toLowerCase()), null, key.toLowerCase(), locale));
							setCellText((Tc) row.getContent().get(2), getMessage(String.format("report.risk_information.title.%s", "expo"), null, "Expo.", locale), alignmentLeft);
							setCellText((Tc) row.getContent().get(3), getMessage(String.format("report.risk_information.title.%s", "owner"), null, "Owner", locale));
							setCellText((Tc) row.getContent().get(4), getMessage(String.format("report.risk_information.title.%s", "comment"), null, "Comment", locale));
							setRepeatHeader(row);
						}
						isFirst = false;
						index = 1;
					}

					row = (Tr) table.getContent().get(index++);
					setCellText((Tc) row.getContent().get(0), riskinfo.getChapter());
					setCellText((Tc) row.getContent().get(1),
							getMessage(String.format("label.risk_information.%s.%s", riskinfo.getCategory().toLowerCase(), riskinfo.getChapter().replace(".", "_")), null,
									riskinfo.getLabel(), locale),
							alignmentLeft);
					String color = riskinfo.getChapter().matches("\\d(\\.0){2}") ? HEADER_COLOR : HEADER_COLOR;
					if (riskinfo.getCategory().equals("Threat")) {
						for (int i = 0; i < 3; i++)
							setColor(((Tc) row.getContent().get(i)), color);
						setCellText((Tc) row.getContent().get(2), riskinfo.getAcronym());
						setCellText((Tc) row.getContent().get(3), riskinfo.getExposed(), alignmentCenter);
						setCellText((Tc) row.getContent().get(4), getValueOrEmpty(riskinfo.getOwner()));
						addCellParagraph((Tc) row.getContent().get(5), riskinfo.getComment());
					} else {
						for (int i = 0; i < 2; i++)
							setColor(((Tc) row.getContent().get(i)), color);
						setCellText((Tc) row.getContent().get(2), riskinfo.getExposed(), alignmentCenter);
						setCellText((Tc) row.getContent().get(3), getValueOrEmpty(riskinfo.getOwner()));
						addCellParagraph((Tc) row.getContent().get(4), riskinfo.getComment());
					}
				}
				document.getContent().add(document.getContent().indexOf(paragraph), table);
			}
		}
	}

	protected void setColor(Tc tc, String color) {
		if (tc.getTcPr() == null)
			tc.setTcPr(factory.createTcPr());
		if (tc.getTcPr().getShd() == null)
			tc.getTcPr().setShd(factory.createCTShd());
		tc.getTcPr().getShd().setFill(color);
	}

	private String getValueOrEmpty(String value) {
		return value == null ? "" : value;
	}

	private void setProgress(int progress) {
		this.progress = progress;
	}

	private void updateProperties() {
		Optional<SimpleParameter> maxImplParameter = analysis.getSimpleParameters().stream().filter(parameter -> parameter.getDescription().equals(Constant.SOA_THRESHOLD))
				.findAny();
		if (maxImplParameter.isPresent()) {
			CTProperty soaThresholdProperty = document.getProperties().getCustomProperties().getProperty(MAX_IMPL);
			if (soaThresholdProperty == null)
				document.getProperties().getCustomProperties().addProperty(MAX_IMPL, maxImplParameter.get().getValue().intValue());
			else
				soaThresholdProperty.setLpwstr(maxImplParameter.get().getValue().intValue() + "");
		}

		CTProperty nonApplicable = document.getProperties().getCustomProperties().getProperty(_27001_NA_MEASURES);
		if (nonApplicable == null)
			document.getProperties().getCustomProperties().addProperty(_27001_NA_MEASURES, nonApplicableMeasure27001);
		else
			nonApplicable.setLpwstr(String.valueOf(nonApplicableMeasure27001));

		nonApplicable = document.getProperties().getCustomProperties().getProperty(_27002_NA_MEASURES);

		if (nonApplicable == null)
			document.getProperties().getCustomProperties().addProperty(_27002_NA_MEASURES, nonApplicableMeasure27002);
		else
			nonApplicable.setLpwstr(String.valueOf(nonApplicableMeasure27002));

		document.getProperties().getCoreProperties().setCategory(analysis.getCustomer().getOrganisation());
		document.getProperties().getCoreProperties().setCreator(String.format("%s %s", analysis.getOwner().getFirstName(), analysis.getOwner().getLastName()));
		document.enforceUpdateFields();
	}

	public static void MergeCell(XWPFTableRow row, int begin, int size, String color) {
		int length = begin + size;
		for (int i = 0; i < length; i++) {
			XWPFTableCell cell = row.getCell(i);
			if (cell == null)
				cell = row.addNewTableCell();
			if (color != null)
				cell.setColor(color);
			if (i < begin)
				continue;
			else if (i == begin)
				cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
			else
				cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.exportation.word.ExportReport#close()
	 */
	@Override
	public void close() {
		/*
		 * if (getDocument() != null) { try { getDocument().close(); } catch
		 * (IOException e) { TrickLogManager.Persist(e); } }
		 */

	}

}