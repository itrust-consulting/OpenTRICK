package lu.itrust.business.TS.exportation.word.impl.docx4j;

import static lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper.setValue;

import java.io.File;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.docx4j.dml.CTRegularTextRun;
import org.docx4j.dml.CTSRgbColor;
import org.docx4j.dml.CTShapeProperties;
import org.docx4j.dml.CTSolidColorFillProperties;
import org.docx4j.dml.chart.CTAxDataSource;
import org.docx4j.dml.chart.CTBarSer;
import org.docx4j.dml.chart.CTNumData;
import org.docx4j.dml.chart.CTNumDataSource;
import org.docx4j.dml.chart.CTNumRef;
import org.docx4j.dml.chart.CTNumVal;
import org.docx4j.dml.chart.CTRadarChart;
import org.docx4j.dml.chart.CTRadarSer;
import org.docx4j.dml.chart.CTRelId;
import org.docx4j.dml.chart.CTSerTx;
import org.docx4j.dml.chart.CTStrData;
import org.docx4j.dml.chart.CTStrRef;
import org.docx4j.dml.chart.CTStrVal;
import org.docx4j.dml.chart.CTUnsignedInt;
import org.docx4j.dml.chart.CTValAx;
import org.docx4j.dml.chart.SerContent;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.docProps.custom.Properties.Property;
import org.docx4j.jaxb.Context;
import org.docx4j.jaxb.XPathBinderAssociationIsPartialException;
import org.docx4j.model.table.TblFactory;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.DocPropsCustomPart;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.DrawingML.Chart;
import org.docx4j.openpackaging.parts.WordprocessingML.EmbeddedPackagePart;
import org.docx4j.openpackaging.parts.relationships.RelationshipsPart.AddPartBehaviour;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.Br;
import org.docx4j.wml.CTBookmark;
import org.docx4j.wml.CTVerticalJc;
import org.docx4j.wml.Document;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.FldChar;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.Numbering;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase.TextAlignment;
import org.docx4j.wml.R;
import org.docx4j.wml.R.Tab;
import org.docx4j.wml.STBrType;
import org.docx4j.wml.STFldCharType;
import org.docx4j.wml.STVerticalJc;
import org.docx4j.wml.Style;
import org.docx4j.wml.Styles;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.TblPr;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;
import org.springframework.context.MessageSource;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.SheetData;

import lu.itrust.business.TS.component.ChartGenerator;
import lu.itrust.business.TS.component.Distribution;
import lu.itrust.business.TS.component.NaturalOrderComparator;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.exportation.word.DocxFormatter;
import lu.itrust.business.TS.exportation.word.ExportReport;
import lu.itrust.business.TS.exportation.word.impl.docx4j.formatting.Docx4jActionPlanFormatter;
import lu.itrust.business.TS.exportation.word.impl.docx4j.formatting.Docx4jAssessmentFormatter;
import lu.itrust.business.TS.exportation.word.impl.docx4j.formatting.Docx4jAssetFormatter;
import lu.itrust.business.TS.exportation.word.impl.docx4j.formatting.Docx4jFormatter;
import lu.itrust.business.TS.exportation.word.impl.docx4j.formatting.Docx4jHeatMapLegendFormatter;
import lu.itrust.business.TS.exportation.word.impl.docx4j.formatting.Docx4jImpactProbaFormatter;
import lu.itrust.business.TS.exportation.word.impl.docx4j.formatting.Docx4jMeasureFormatter;
import lu.itrust.business.TS.exportation.word.impl.docx4j.formatting.Docx4jRiskAcceptanceFormatter;
import lu.itrust.business.TS.exportation.word.impl.docx4j.formatting.Docx4jRiskHeatMapFormatter;
import lu.itrust.business.TS.exportation.word.impl.docx4j.formatting.Docx4jRiskInformationFormatter;
import lu.itrust.business.TS.exportation.word.impl.docx4j.formatting.Docx4jScenarioFormatter;
import lu.itrust.business.TS.exportation.word.impl.docx4j.formatting.Docx4jScopeFormatter;
import lu.itrust.business.TS.exportation.word.impl.docx4j.formatting.Docx4jSummaryFormatter;
import lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ExcelHelper;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.model.actionplan.ActionPlanMode;
import lu.itrust.business.TS.model.actionplan.summary.SummaryStage;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.asset.AssetType;
import lu.itrust.business.TS.model.general.Phase;
import lu.itrust.business.TS.model.iteminformation.ItemInformation;
import lu.itrust.business.TS.model.iteminformation.helper.ComparatorItemInformation;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.parameter.impl.DynamicParameter;
import lu.itrust.business.TS.model.riskinformation.RiskInformation;
import lu.itrust.business.TS.model.riskinformation.helper.RiskInformationManager;
import lu.itrust.business.TS.model.scenario.Scenario;
import lu.itrust.business.TS.model.scenario.ScenarioType;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measure.helper.MeasureComparator;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescriptionText;

public abstract class Docx4jWordExporter implements ExportReport {

	private static volatile DocxFormatter docxFormatter = null;

	protected Analysis analysis = null;

	protected Document document = null;

	protected ObjectFactory factory = null;

	protected org.docx4j.dml.wordprocessingDrawing.ObjectFactory drawingFactory = null;

	protected org.docx4j.dml.ObjectFactory dmlFactory = null;

	protected org.docx4j.dml.chart.ObjectFactory chartFactory = null;

	protected String idTask;

	protected DecimalFormat kEuroFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.FRANCE);

	protected String languageAlpha2 = null;

	protected Locale locale = null;

	protected DecimalFormat numberFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.FRANCE);

	protected ServiceTaskFeedback serviceTaskFeedback;

	protected ValueFactory valueFactory = null;

	protected String contextPath;

	private String currentParagraphId;

	private int maxProgress;

	private MessageSource messageSource;

	private int minProgress;

	private int nonApplicableMeasure27001 = 0;

	private int nonApplicableMeasure27002 = 0;

	private Long drawingIndex = null;

	private int progress;

	private String reportName;

	private Map<String, Style> styles = Collections.emptyMap();

	protected WordprocessingMLPackage wordMLPackage = null;

	private File workFile;

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.exportation.word.ExportReport#close()
	 */
	@Override
	public void close() {
	}

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
				String.format("%s/WEB-INF/tmp/STA_%d_%s_v%s.docx", contextPath, System.nanoTime(), analysis.getLabel().replaceAll("/|-|:|.|&", "_"), analysis.getVersion()));
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

		generateExtendedParameters(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME);

		serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.table.probabilty", "Printing probabilty table", increase(5)));// 40%

		generateExtendedParameters(Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME);

		generateOtherData();

		serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.table.action.plan", "Printing action plan table", increase(5)));// 45%

		generateActionPlan();

		serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.table.summary", "Printing summary table", increase(5)));// 55%

		generateActionPlanSummary();

		serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.table.measure", "Printing measure table", increase(5)));// 60%

		generateMeasures();

		serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.chart", "Printing chart", increase(5)));// 70%

		generatePhase();

		generateGraphics();

		updateProperties();

		document.getContent().parallelStream().forEach(data -> getDocxFormatter().format(data, getType()));

		wordMLPackage.save(workFile);

	}

	private void generatePhase() throws Docx4JException, JAXBException {
		Numbering numbering = wordMLPackage.getMainDocumentPart().getNumberingDefinitionsPart().getContents();
		BigInteger abstractNumId = numbering.getAbstractNum().parallelStream()
				.filter(abstractNum -> !abstractNum.getLvl().isEmpty() && abstractNum.getLvl().get(0).getLvlText().getVal().startsWith("Phase"))
				.map(abstractNum -> abstractNum.getAbstractNumId()).findAny().orElse(null);
		if (abstractNumId == null)
			return;
		BigInteger numId = numbering.getNum().parallelStream().filter(num -> num.getAbstractNumId().getVal().equals(abstractNumId)).map(num -> num.getNumId()).findAny()
				.orElse(null);
		P paragraphOriginal = findTableAnchor("Phase");
		if (paragraphOriginal == null)
			return;
		List<Object> contents = new LinkedList<>();
		List<SummaryStage> summaryStages = getSummaryStage();
		setCustomProperty("PHASE_COUNT", summaryStages.size() - 1);
		analysis.getPhases().stream().filter(phase -> phase.getNumber() > 0 && summaryStages.stream().anyMatch(stage -> stage.getStage().equals("Phase " + phase.getNumber())))
				.forEach(phase -> {
					SummaryStage summaryStage = summaryStages.stream().filter(stage -> stage.getStage().equals("Phase " + phase.getNumber())).findAny().orElse(null);
					P paragraph = setStyle(factory.createP(), "ListParagraph");
					Calendar begin = Calendar.getInstance(), end = Calendar.getInstance();
					begin.setTime(phase.getBeginDate());
					end.setTime(phase.getEndDate());
					int monthBegin = begin.get(Calendar.MONTH) + 1, monthEnd = end.get(Calendar.MONTH) + 1;
					setText(paragraph, getMessage("report.risk.treatment.plan.summary", new Object[] { (monthBegin < 10 ? "0" : "") + monthBegin, begin.get(Calendar.YEAR) + "",
							(monthEnd < 10 ? "0" : "") + monthEnd, end.get(Calendar.YEAR) + "", summaryStage.getMeasureCount() + "" }, null, locale));
					paragraph.getPPr().setNumPr(factory.createPPrBaseNumPr());
					paragraph.getPPr().getNumPr().setNumId(factory.createPPrBaseNumPrNumId());
					paragraph.getPPr().getNumPr().getNumId().setVal(numId);
					paragraph.getPPr().getNumPr().setIlvl(factory.createPPrBaseNumPrIlvl());
					paragraph.getPPr().getNumPr().getIlvl().setVal(BigInteger.valueOf(0));
					paragraph.getPPr().setInd(factory.createPPrBaseInd());
					paragraph.getPPr().getInd().setHanging(BigInteger.valueOf(993));
					paragraph.getPPr().getInd().setLeft(BigInteger.valueOf(993));
					contents.add(paragraph);
				});
		insertAllBefore(paragraphOriginal, contents);
	}

	protected List<SummaryStage> getSummaryStage() {
		return analysis.getSummary(getActionPlanType());
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
			if (r.getRPr() == null)
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

	protected void addTab(P paragraph) {
		Tab tab = factory.createRTab();
		R run = factory.createR();
		run.getContent().add(tab);
		paragraph.getContent().add(run);
	}

	protected P addTableCaption(String value) {
		P paragraph = setStyle(factory.createP(), "Caption");
		R run = setText(factory.createR(), getMessage("report.caption.table.name", null, "Table", locale));
		run.getContent().stream().filter(text -> text instanceof Text).map(text -> (Text) text).forEach(text -> text.setSpace("preserve"));
		paragraph.getContent().add(run);
		paragraph.getContent().add(createSpecialRun(STFldCharType.BEGIN));
		paragraph.getContent().add(setInstrText(factory.createR(), "SEQ Table \\* ARABIC "));
		paragraph.getContent().add(createSpecialRun(STFldCharType.SEPARATE));
		paragraph.getContent().add(setText(factory.createR(), "1"));
		paragraph.getContent().add(createSpecialRun(STFldCharType.END));
		paragraph.getContent().add(setText(factory.createR(), (languageAlpha2.equals("FR") ? "\u00A0:\u00A0" : ": ") + value));
		return paragraph;

	}

	protected P addFigureCaption(String value) {
		P paragraph = setStyle(factory.createP(), "Caption");
		R run = setText(factory.createR(), getMessage("report.caption.figure.name", null, "Figure", locale));
		run.getContent().stream().filter(text -> text instanceof Text).map(text -> (Text) text).forEach(text -> text.setSpace("preserve"));
		paragraph.getContent().add(run);
		paragraph.getContent().add(createSpecialRun(STFldCharType.BEGIN));
		paragraph.getContent().add(setInstrText(factory.createR(), "SEQ Figure \\* ARABIC "));
		paragraph.getContent().add(createSpecialRun(STFldCharType.SEPARATE));
		paragraph.getContent().add(setText(factory.createR(), "1"));
		paragraph.getContent().add(createSpecialRun(STFldCharType.END));
		paragraph.getContent().add(setText(factory.createR(), (languageAlpha2.equals("FR") ? "\u00A0:\u00A0" : ": ") + value));
		return paragraph;

	}

	protected P addText(P paragraph, String value) {
		R run = factory.createR();
		Text text = factory.createText();
		run.getContent().add(text);
		text.setValue(value);
		paragraph.getContent().add(run);
		return paragraph;
	}

	protected TextAlignment createAlignment(String value) {
		TextAlignment alignment = factory.createPPrBaseTextAlignment();
		alignment.setVal(value);
		return alignment;
	}

	protected R createSpecialRun(STFldCharType type) {
		R run = factory.createR();
		FldChar fldChar = factory.createFldChar();
		fldChar.setFldCharType(type);
		run.getContent().add(fldChar);
		return run;
	}

	protected Tbl createTable(String styleId, int rows, int cols) {
		Tbl table = TblFactory.createTable(rows, cols, 1);
		Style value = styles.get(styleId);
		if (value != null)
			table.getTblPr().getTblStyle().setVal(value.getName().getVal());
		if (table.getTblPr().getJc() == null)
			table.getTblPr().setJc(factory.createJc());
		table.getTblPr().getJc().setVal(JcEnumeration.CENTER);
		if (table.getTblPr().getTblW() == null)
			table.getTblPr().setTblW(factory.createTblWidth());
		table.getTblPr().getTblW().setType("auto");
		table.getTblPr().getTblW().setW(BigInteger.valueOf(0));
		return table;
	}

	protected CTVerticalJc createVerticalAlignment(STVerticalJc alignment) {
		CTVerticalJc ctVerticalJc = factory.createCTVerticalJc();
		ctVerticalJc.setVal(alignment);
		return ctVerticalJc;
	}

	protected int findIndex(Object reference) {
		return reference == null ? -1 : document.getContent().indexOf(reference);
	}

	protected P findTableAnchor(String name) throws XPathBinderAssociationIsPartialException, JAXBException {
		return (P) document.getContent().parallelStream().filter(p -> (p instanceof P) && ((P) p).getContent().parallelStream().anyMatch(
				b -> (b instanceof JAXBElement) && ((JAXBElement<?>) b).getValue() instanceof CTBookmark && ((CTBookmark) ((JAXBElement<?>) b).getValue()).getName().equals(name)))
				.findAny().orElse(null);

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

	protected abstract void generateAssessements() throws Exception;

	protected abstract void generateAssets(String name, List<Asset> assets) throws XPathBinderAssociationIsPartialException, JAXBException;

	protected void generateComplianceGraphic() throws Exception {

		Map<String, Part> parts = createComplianceGraphicParts();

		List<Phase> phases = analysis.findUsablePhase();

		for (Entry<String, Part> entry : parts.entrySet()) {

			String path = entry.getValue().getRelationshipsPart().getRelationships().getRelationship().parallelStream().filter(r -> r.getTarget().endsWith(".xlsx"))
					.map(Relationship::getTarget).findAny().orElse(null);
			if (path == null)
				continue;

			Part excel = wordMLPackage.getParts().get(new PartName("/word" + path.replace("..", "")));
			if (excel == null)
				continue;

			Docx4jExcelSheet reportExcelSheet = null;
			try {
				reportExcelSheet = new Docx4jExcelSheet((EmbeddedPackagePart) excel, String.format("%s/WEB-INF/tmp/", contextPath));
				Chart chart = (Chart) entry.getValue();
				CTRadarChart radarChart = (CTRadarChart) chart.getContents().getChart().getPlotArea().getAreaChartOrArea3DChartOrLineChart().parallelStream()
						.filter(web -> web instanceof CTRadarChart).findAny().orElse(null);
				if (radarChart == null)
					continue;
				radarChart.getSer().clear();

				CTRegularTextRun r = (CTRegularTextRun) chart.getContents().getChart().getTitle().getTx().getRich().getP().get(0).getEGTextRun().get(0);

				if (entry.getKey().equals(Constant.STANDARD_27001) || entry.getKey().equals(Constant.STANDARD_27002))
					r.setT(getMessage("report.compliance.iso", new Object[] { entry.getKey() }, "Compliance ISO " + entry.getKey(), locale));
				else {
					r.setT(getMessage("report.compliance.custom", new Object[] { entry.getKey() }, "Compliance " + entry.getKey(), locale));
					reportExcelSheet.setName("Compliance" + entry.getKey());
					reportExcelSheet.getWorkbook().getContents().getSheets().getSheet().get(0).setName(reportExcelSheet.getName());
				}

				chart.getContents().getChart().getPlotArea().getValAxOrCatAxOrDateAx().parallelStream().filter(valAx -> valAx instanceof CTValAx).map(valAx -> (CTValAx) valAx)
						.forEach(valAx -> {
							valAx.getNumFmt().setSourceLinked(false);
							valAx.getNumFmt().setFormatCode("0%");
						});

				List<Measure> measures = analysis.getAnalysisStandards().stream().filter(analysisStandard -> analysisStandard.getStandard().is(entry.getKey()))
						.map(AnalysisStandard::getMeasures).findAny().orElse(Collections.emptyList());

				SheetData sheet = reportExcelSheet.getWorkbook().getWorksheet(0).getContents().getSheetData();
				Map<String, Object[]> compliances = ChartGenerator.ComputeComplianceBefore(measures, valueFactory);

				String phaseLabel = getMessage("label.chart.series.current_level", null, "Current Level", locale);

				int rowCount = 0;

				Row row = ExcelHelper.getRow(sheet, rowCount++, compliances.size() + 1);
				setValue(row, 0, getMessage("report.compliance.chapter", null, "Chapter", locale));
				setValue(row, 1, phaseLabel);

				CTRadarSer ser = createChart(String.format("%s!$B$1", reportExcelSheet.getName()), 0, phaseLabel, new CTRadarSer());
				ser.getVal().getNumRef().getNumCache().setFormatCode("0%");

				for (String key : compliances.keySet()) {
					Object[] compliance = compliances.get(key);
					double value = (((Double) compliance[1]).doubleValue() / ((Integer) compliance[0]).doubleValue()) * 0.01;

					CTStrVal catName = new CTStrVal();
					catName.setV(key);
					catName.setIdx(ser.getCat().getStrRef().getStrCache().getPt().size());
					ser.getCat().getStrRef().getStrCache().getPt().add(catName);
					CTNumVal numVal = new CTNumVal();
					numVal.setIdx(ser.getVal().getNumRef().getNumCache().getPt().size());
					ser.getVal().getNumRef().getNumCache().getPt().add(numVal);

					if (Double.isNaN(value))
						value = 0;
					numVal.setV(value + "");

					row = ExcelHelper.getRow(sheet, rowCount++, compliances.size() + 1);
					setValue(row, 0, key);
					setValue(row, 1, value);
				}

				ser.getCat().getStrRef().setF(String.format("%s!$A$2:$A$%d", reportExcelSheet.getName(), ser.getCat().getStrRef().getStrCache().getPt().size() + 1));
				ser.getVal().getNumRef().setF(String.format("%s!$B$2:$B$%d", reportExcelSheet.getName(), ser.getCat().getStrRef().getStrCache().getPt().size() + 1));

				radarChart.getSer().add(ser);

				Map<Integer, Boolean> actionPlanMeasures = analysis.findIdMeasuresImplementedByActionPlanType(getActionPlanType());

				if (!actionPlanMeasures.isEmpty()) {

					int columnIndex = 2;
					for (Phase phase : phases) {

						char col = (char) (((int) 'A') + columnIndex);

						phaseLabel = getMessage("label.chart.phase", new Object[] { phase.getNumber() }, "Phase " + phase.getNumber(), locale);

						ser = createChart(ser.getCat(), String.format("%s!$%s$1", reportExcelSheet.getName(), col), columnIndex - 1, phaseLabel, new CTRadarSer());
						ser.getVal().getNumRef().getNumCache().setFormatCode("0%");

						compliances = ChartGenerator.ComputeCompliance(measures, phase, actionPlanMeasures, compliances, valueFactory);

						setValue(sheet.getRow().get(rowCount = 0), columnIndex, phaseLabel);

						for (String key : compliances.keySet()) {
							Object[] compliance = compliances.get(key);
							double value = (((Double) compliance[1]).doubleValue() / ((Integer) compliance[0]).doubleValue()) * 0.01;

							CTNumVal numVal = new CTNumVal();
							numVal.setIdx(ser.getVal().getNumRef().getNumCache().getPt().size());
							ser.getVal().getNumRef().getNumCache().getPt().add(numVal);
							if (Double.isNaN(value))
								value = 0;
							numVal.setV(value + "");
							setValue(sheet.getRow().get(++rowCount), columnIndex, value);
						}

						ser.getVal().getNumRef()
								.setF(String.format("%s!$%s$2:$%s$%d", reportExcelSheet.getName(), col, col, ser.getCat().getStrRef().getStrCache().getPt().size() + 1));

						radarChart.getSer().add(ser);

						columnIndex++;
					}
				}
			} finally {
				if (reportExcelSheet != null)
					reportExcelSheet.save();

			}

		}
	}

	private <T extends SerContent> T createChart(CTAxDataSource cat, String reference, long index, String phaseLabel, T ser) {

		setupTitle(reference, index, phaseLabel, ser);

		ser.setCat(cat);

		ser.setVal(new CTNumDataSource());

		ser.getVal().setNumRef(new CTNumRef());

		ser.getVal().getNumRef().setNumCache(new CTNumData());

		return ser;
	}

	protected <T extends SerContent> T createChart(String reference, long index, String title, T ser) {

		CTAxDataSource ctAxDataSource = new CTAxDataSource();

		ctAxDataSource.setStrRef(new CTStrRef());

		ctAxDataSource.getStrRef().setStrCache(new CTStrData());

		return createChart(ctAxDataSource, reference, index, title, ser);

	}

	protected <T extends SerContent> void setupTitle(String reference, long index, String title, T ser) {
		ser.setOrder(new CTUnsignedInt());
		ser.getOrder().setVal(index);
		ser.setIdx(new CTUnsignedInt());
		ser.getIdx().setVal(index);
		ser.setTx(new CTSerTx());
		ser.getTx().setStrRef(new CTStrRef());
		ser.getTx().getStrRef().setStrCache(new CTStrData());
		ser.getTx().getStrRef().setF(reference);
		CTStrVal valTitle = new CTStrVal();
		valTitle.setIdx(0);
		valTitle.setV(title);
		ser.getTx().getStrRef().getStrCache().getPt().add(valTitle);
		ser.getTx().getStrRef().getStrCache().setPtCount(new CTUnsignedInt());
		ser.getTx().getStrRef().getStrCache().getPtCount().setVal(ser.getTx().getStrRef().getStrCache().getPt().size());
	}

	protected abstract ActionPlanMode getActionPlanType();

	protected abstract void generateExtendedParameters(String type) throws Exception;

	protected abstract void generateOtherData() throws XPathBinderAssociationIsPartialException, JAXBException, Exception;

	protected String getDisplayName(AssetType type) {
		return getMessage("label.asset_type." + type.getName().toLowerCase(), null, type.getName(), locale);
	}

	protected String getDisplayName(ScenarioType type) {
		return getMessage("label.scenario.type." + type.getName().toLowerCase(), null, type.getName(), locale);
	}

	protected String getMessage(String code, Object[] parameters, String defaultMessage, Locale locale) {
		return messageSource.getMessage(code, parameters, defaultMessage, locale);
	}

	protected PPr getParagraphStyle(String id) {
		Style style = styles.get(id);
		return style == null ? null : (PPr) style.getPPr();
	}

	

	protected TblPr getTableStyle(String id) {
		Style style = styles.get(id);
		return style == null ? null : (TblPr) style.getTblPr();
	}

	protected abstract AnalysisType getType();

	protected boolean insertAfter(Object reference, Object element) {
		int index = findIndex(reference);
		if (index == -1)
			return false;
		document.getContent().add(index + 1, element);
		return true;
	}

	protected boolean insertAllAfter(Object reference, List<Object> elements) {
		int index = findIndex(reference);
		if (index == -1)
			return false;
		document.getContent().addAll(index + 1, elements);
		return true;
	}

	protected boolean insertAllBefore(Object reference, List<Object> elements) {
		int index = findIndex(reference);
		if (index == -1)
			return false;
		document.getContent().addAll(index, elements);
		return true;
	}

	protected boolean insertBofore(Object reference, Object element) {
		int index = findIndex(reference);
		if (index == -1)
			return false;
		document.getContent().add(index, element);
		return true;
	}

	protected boolean replace(Object reference, Object element) {
		int index = findIndex(reference);
		if (index == -1)
			return false;
		document.getContent().set(index, element);
		return true;
	}

	protected P setAlignment(P paragraph, TextAlignment alignment) {
		if (paragraph.getPPr() != null)
			paragraph.setPPr(factory.createPPr());
		if (paragraph.getParent() instanceof Tc) {
			if (paragraph.getPPr().getJc() == null)
				paragraph.getPPr().setJc(factory.createJc());
			paragraph.getPPr().getJc().setVal(JcEnumeration.fromValue(alignment.getVal()));
		} else
			paragraph.getPPr().setTextAlignment(alignment);
		return paragraph;
	}

	protected Tc setAlignment(Tc cell, TextAlignment alignment) {
		cell.getContent().parallelStream().filter(p -> p instanceof P).forEach(p -> setAlignment((P) p, alignment));
		return cell;
	}

	protected void setCellText(Tc tc, String text) {
		setCellText(tc, text, null);
	}

	protected void setCellText(Tc cell, String text, TextAlignment alignment) {
		if (cell.getContent().isEmpty())
			cell.getContent().add(new P());
		P paragraph = (P) cell.getContent().get(0);
		cell.getContent().parallelStream().filter(p -> p instanceof P).map(p -> (P) p).forEach(p -> setStyle(p, getCurrentParagraphId()));
		setText(paragraph, text, alignment);
	}

	protected R setInstrText(R r, String content) {
		Text text = factory.createText();
		JAXBElement<Text> textWrapped = factory.createRInstrText(text);
		r.getContent().add(textWrapped);
		text.setValue(content);
		text.setSpace("preserve");
		return r;
	}

	protected void setRepeatHeader(Tr row) {
		if (row.getTrPr() == null)
			row.setTrPr(factory.createTrPr());
		row.getTrPr().getCnfStyleOrDivIdOrGridBefore().add(factory.createCTTrPrBaseTblHeader(factory.createBooleanDefaultTrue()));
	}

	protected P setStyle(P p, String styleId) {
		if (p.getPPr() == null)
			p.setPPr(factory.createPPr());
		if (p.getPPr().getPStyle() == null)
			p.getPPr().setPStyle(factory.createPPrBasePStyle());
		p.getPPr().getPStyle().setVal(styleId);
		return p;
	}

	protected P setText(P paragraph, String content) {
		return setText(paragraph, content, null);
	}

	protected P setText(P paragraph, String content, boolean bold) {
		R r = factory.createR();
		Text text = factory.createText();
		r.getContent().add(text);
		text.setValue(content);
		if (r.getRPr() == null)
			r.setRPr(factory.createRPr());
		r.getRPr().setB(factory.createBooleanDefaultTrue());
		paragraph.getContent().add(r);
		return paragraph;
	}

	protected P setText(P paragraph, String content, TextAlignment alignment) {
		if (alignment != null) {
			if (paragraph.getPPr() == null)
				setStyle(paragraph, getCurrentParagraphId());
			if (paragraph.getParent() instanceof Tc) {
				if (paragraph.getPPr().getJc() == null)
					paragraph.getPPr().setJc(factory.createJc());
				paragraph.getPPr().getJc().setVal(JcEnumeration.fromValue(alignment.getVal()));
			} else
				paragraph.getPPr().setTextAlignment(alignment);
		}
		paragraph.getContent().removeIf(r -> r instanceof R);
		R r = factory.createR();
		Text text = factory.createText();
		text.setValue(content);
		r.getContent().add(text);
		paragraph.getContent().add(r);
		return paragraph;

	}

	protected R setText(R r, String value) {
		Text text = factory.createText();
		text.setValue(value == null ? "" : value);
		r.getContent().add(text);
		return r;
	}

	protected void setVerticalAlignment(Tc cell, CTVerticalJc alignment) {
		if (cell.getTcPr() == null)
			cell.setTcPr(factory.createTcPr());
		cell.getTcPr().setVAlign(alignment);
	}

	protected abstract void writeChart(Docx4jExcelSheet reportExcelSheet) throws Exception;

	private P addBreak(P paragraph, STBrType type) {
		R run = factory.createR();
		Br br = factory.createBr();
		run.getContent().add(br);
		br.setType(type);
		paragraph.getContent().add(run);
		return paragraph;
	}

	private P addCellParagraph(Tc cell) {
		P p = factory.createP();
		cell.getContent().add(p);
		return p;
	}

	private void createDocumentFromTemplate() throws Docx4JException, URISyntaxException {

		WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new File(String.format("%s/WEB-INF/data/docx/%s.docx", contextPath, reportName)));

		wordMLPackage.save(workFile);

		this.wordMLPackage = WordprocessingMLPackage.load(workFile);

		setDocument(this.wordMLPackage.getMainDocumentPart().getContents());

		factory = Context.getWmlObjectFactory();

		chartFactory = new org.docx4j.dml.chart.ObjectFactory();

		dmlFactory = new org.docx4j.dml.ObjectFactory();

		drawingFactory = new org.docx4j.dml.wordprocessingDrawing.ObjectFactory();

		Styles styles = this.wordMLPackage.getMainDocumentPart().getStyleDefinitionsPart().getContents();

		this.styles = styles.getStyle().parallelStream().collect(Collectors.toMap(Style::getStyleId, Function.identity()));

		if (wordMLPackage.getDocPropsCustomPart() == null)
			wordMLPackage.addDocPropsCustomPart();
	}

	private void generateAssets() throws XPathBinderAssociationIsPartialException, JAXBException {
		generateAssets("Asset", analysis.findSelectedAssets());
		generateAssets("AssetNotSelected", analysis.findNoAssetSelected());
	}

	private void generateGraphics() throws Exception {
		generateComplianceGraphic();
		updateGraphics();
	}

	protected abstract void updateGraphics() throws Exception;

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
			insertBofore(paragraph, table);
		}
	}

	private void generateMeasures() throws XPathBinderAssociationIsPartialException, JAXBException {

		List<AnalysisStandard> analysisStandards = analysis.getAnalysisStandards();
		Map<String, Double> expressionParameters = this.analysis.getDynamicParameters().stream()
				.collect(Collectors.toMap(DynamicParameter::getAcronym, DynamicParameter::getValue));

		if (analysisStandards.size() > 0) {

			setCurrentParagraphId(TS_TAB_TEXT_3);

			Comparator<Measure> comparator = new MeasureComparator();

			TextAlignment alignmentLeft = createAlignment("left");

			List<Object> contents = new LinkedList<>();

			for (AnalysisStandard analysisStandard : analysisStandards) {

				// initialise table with 1 row and 1 column after the paragraph
				// cursor
				if (analysisStandard.getMeasures().isEmpty())
					continue;

				document.getContent().add(addBreak(factory.createP(), STBrType.PAGE));

				P paragraph = setText(setStyle(factory.createP(), "TSMeasureTitle"), analysisStandard.getStandard().getLabel());

				document.getContent().add(paragraph);

				Tbl table = createTable("TableTSMeasure", analysisStandard.getMeasures().size() + 1, 16);

				table.getTblPr().getTblW().setType("dxa");

				table.getTblPr().getTblW().setW(BigInteger.valueOf(16157));

				document.getContent().add(table);

				Tr row = (Tr) table.getContent().get(0);

				setCellText((Tc) row.getContent().get(0), getMessage("report.measure.reference", null, "Ref.", locale));
				setCellText((Tc) row.getContent().get(1), getMessage("report.measure.domain", null, "Domain", locale));
				setCellText((Tc) row.getContent().get(2), getMessage("report.measure.status", null, "ST", locale));
				setCellText((Tc) row.getContent().get(3), getMessage("report.measure.implementation_rate", null, "IR(%)", locale));
				setCellText((Tc) row.getContent().get(4), getMessage("report.measure.internal.workload", null, "IS(md)", locale));
				setCellText((Tc) row.getContent().get(5), getMessage("report.measure.external.workload", null, "ES(md)", locale));
				setCellText((Tc) row.getContent().get(6), getMessage("report.measure.investment", null, "INV(k€)", locale));
				setCellText((Tc) row.getContent().get(7), getMessage("report.measure.life_time", null, "LT(y)", locale));
				setCellText((Tc) row.getContent().get(8), getMessage("report.measure.internal.maintenance", null, "IM(md)", locale));
				setCellText((Tc) row.getContent().get(9), getMessage("report.measure.external.maintenance", null, "EM(md)", locale));
				setCellText((Tc) row.getContent().get(10), getMessage("report.measure.recurrent.investment", null, "RINV(k€)", locale));
				setCellText((Tc) row.getContent().get(11), getMessage("report.measure.cost", null, "CS(k€)", locale));
				setCellText((Tc) row.getContent().get(12), getMessage("report.measure.phase", null, "P", locale));
				setCellText((Tc) row.getContent().get(13), getMessage("report.measure.responsible", null, "Resp.", locale));
				setCellText((Tc) row.getContent().get(14), getMessage("report.measure.to_do", null, "To Do", locale));
				setCellText((Tc) row.getContent().get(15), getMessage("report.measure.comment", null, "Comment", locale));

				setRepeatHeader(row);
				// set data
				Collections.sort(analysisStandard.getMeasures(), comparator);

				int index = 1;

				for (Measure measure : analysisStandard.getMeasures()) {
					row = (Tr) table.getContent().get(index++);
					setCellText((Tc) row.getContent().get(0), measure.getMeasureDescription().getReference());
					MeasureDescriptionText description = measure.getMeasureDescription().findByLanguage(analysis.getLanguage());
					setCellText((Tc) row.getContent().get(1), description == null ? "" : description.getDomain(), alignmentLeft);
					if (!measure.getMeasureDescription().isComputable()) {
						String color = measure.getMeasureDescription().getLevel() < 2 ? SUPER_HEAD_COLOR : HEADER_COLOR;
						for (int i = 0; i < 16; i++)
							setColor((Tc) row.getContent().get(i), color);
						MergeCell(row, 1, 14, color);
					} else {
						setCellText((Tc) row.getContent().get(2), getMessage("label.measure.status." + measure.getStatus().toLowerCase(), null, measure.getStatus(), locale));
						addCellNumber((Tc) row.getContent().get(3), numberFormat.format(measure.getImplementationRateValue(expressionParameters)));
						addCellNumber((Tc) row.getContent().get(4), kEuroFormat.format(measure.getInternalWL()));
						addCellNumber((Tc) row.getContent().get(5), kEuroFormat.format(measure.getExternalWL()));
						addCellNumber((Tc) row.getContent().get(6), numberFormat.format(measure.getInvestment() * 0.001));
						addCellNumber((Tc) row.getContent().get(7), numberFormat.format(measure.getLifetime()));
						addCellNumber((Tc) row.getContent().get(8), kEuroFormat.format(measure.getInternalMaintenance()));
						addCellNumber((Tc) row.getContent().get(9), kEuroFormat.format(measure.getExternalMaintenance()));
						addCellNumber((Tc) row.getContent().get(10), numberFormat.format(measure.getRecurrentInvestment() * 0.001));
						addCellNumber((Tc) row.getContent().get(11), numberFormat.format(measure.getCost() * 0.001));
						addCellParagraph((Tc) row.getContent().get(12), measure.getPhase().getNumber() + "");
						addCellParagraph((Tc) row.getContent().get(13), measure.getResponsible());
						addCellParagraph((Tc) row.getContent().get(14), measure.getToDo());
						if (Constant.MEASURE_STATUS_NOT_APPLICABLE.equalsIgnoreCase(measure.getStatus()) || measure.getImplementationRateValue(expressionParameters) >= 100) {
							for (int i = 0; i < 16; i++)
								setColor((Tc) row.getContent().get(i), DEFAULT_CELL_COLOR);
							if (measure.getImplementationRateValue(expressionParameters) < 100) {
								if (analysisStandard.getStandard().is(Constant.STANDARD_27002))
									nonApplicableMeasure27002++;
								else if (analysisStandard.getStandard().is(Constant.STANDARD_27001))
									nonApplicableMeasure27001++;
							}
						} else {
							setColor((Tc) row.getContent().get(0), SUB_HEADER_COLOR);
							setColor((Tc) row.getContent().get(1), SUB_HEADER_COLOR);
							setColor((Tc) row.getContent().get(11), measure.getCost() == 0 ? ZERO_COST_COLOR : SUB_HEADER_COLOR);
						}
					}
					addCellParagraph((Tc) row.getContent().get(15), measure.getComment());
				}

				if (!(analysisStandard.getStandard().is(Constant.STANDARD_27001) || analysisStandard.getStandard().is(Constant.STANDARD_27002)))
					contents.add(setText(setStyle(factory.createP(), "ListParagraph"), analysisStandard.getStandard().getLabel()));
			}

			if (!contents.isEmpty()) {
				P paragraph = findTableAnchor("ListCollection");
				if (paragraph != null) {
					contents.parallelStream().forEach(p -> ((P) p).setPPr(paragraph.getPPr()));
					insertAllAfter(paragraph, contents);
				}
			}
		}

	}

	private synchronized Long findDrawingId() throws XPathBinderAssociationIsPartialException, JAXBException {
		if (drawingIndex == null) {
			drawingIndex = document.getContent().parallelStream().filter(p -> p instanceof P).flatMap(p -> ((P) p).getContent().parallelStream()).filter(r -> r instanceof R)
					.flatMap(r -> ((R) r).getContent().parallelStream()).filter(d -> d instanceof JAXBElement).map(d -> ((JAXBElement<?>) d).getValue())
					.filter(d -> d instanceof Drawing).flatMap(d -> ((Drawing) d).getAnchorOrInline().parallelStream()).filter(i -> i instanceof Inline)
					.mapToLong(i -> ((Inline) i).getDocPr().getId()).max().orElse(0);
		}
		return ++drawingIndex;
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
			insertBofore(paragraph, table);
		}
	}

	protected Map<String, Part> createComplianceGraphicParts() throws Docx4JException, JAXBException {
		Map<String, Part> parts = new LinkedHashMap<>();
		parts.put(Constant.STANDARD_27001, findChart("ChartCompliance27001"));
		parts.put(Constant.STANDARD_27002, findChart("ChartCompliance27002"));
		List<AnalysisStandard> analysisStandards = analysis.getAnalysisStandards().stream()
				.filter(analysisStandard -> !(analysisStandard.getStandard().is(Constant.STANDARD_27001) || analysisStandard.getStandard().is(Constant.STANDARD_27002)))
				.collect(Collectors.toList());
		if (!analysisStandards.isEmpty()) {
			P collectionCustom = findTableAnchor("AdditionalCollection");
			Chart part = (Chart) parts.get(Constant.STANDARD_27001);
			if (collectionCustom != null) {
				List<Object> contents = new LinkedList<>();
				for (AnalysisStandard analysisStandard : analysisStandards) {
					ClonePartResult result = cloneChart(part, analysisStandard.getStandard().getLabel(), "Compliance" + analysisStandard.getStandard().getLabel());

					parts.put(analysisStandard.getStandard().getLabel(), result.getPart());

					contents.add(setText(setStyle(factory.createP(), "Heading3"), getMessage("report.additional.collection.title",
							new Object[] { analysisStandard.getStandard().getLabel() }, analysisStandard.getStandard().getLabel(), locale)));

					contents.add(setText(setStyle(factory.createP(), "BodyOfText"), getMessage("report.additional.collection.description",
							new Object[] { analysisStandard.getStandard().getLabel() }, analysisStandard.getStandard().getLabel(), locale)));

					contents.add(result.getP());

					contents.add(addFigureCaption(getMessage("report.additional.collection.caption", new Object[] { analysisStandard.getStandard().getLabel() },
							analysisStandard.getStandard().getLabel(), locale)));

				}
				insertAllBefore(collectionCustom, contents);
			}
		}
		return parts;
	}

	protected ClonePartResult cloneChart(Chart part, String name, String description) throws Docx4JException, InvalidFormatException, JAXBException {
		Part copy = PartClone.clone(part, null);
		Relationship relationship = wordMLPackage.getMainDocumentPart().addTargetPart(copy, AddPartBehaviour.RENAME_IF_NAME_EXISTS);
		part.getRelationshipsPart().getContents().getRelationship().stream().sorted((r1, r2) -> NaturalOrderComparator.compareTo(r1.getId(), r2.getId())).forEach(re -> {
			try {
				if (re.getTarget().startsWith(".."))
					copy.addTargetPart(PartClone.clone(wordMLPackage.getParts().get(new PartName("/word" + re.getTarget().replace("..", ""))), null),
							AddPartBehaviour.RENAME_IF_NAME_EXISTS);
				else
					copy.addTargetPart(PartClone.clone(wordMLPackage.getParts().get(new PartName("/word/charts/" + re.getTarget())), null), AddPartBehaviour.RENAME_IF_NAME_EXISTS);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		return new ClonePartResult(copy, relationship, createGraphic(name, description, relationship.getId()));
	}

	private P createGraphic(String name, String description, String refId) throws XPathBinderAssociationIsPartialException, JAXBException {
		P paragraph = setStyle(factory.createP(), "FigurewithCaption");
		R run = factory.createR();
		run.setRPr(factory.createRPr());
		run.getRPr().setNoProof(factory.createBooleanDefaultTrue());
		Drawing drawing = factory.createDrawing();
		paragraph.getContent().add(run);
		run.getContent().add(factory.createRDrawing(drawing));
		Inline inline = drawingFactory.createInline();
		drawing.getAnchorOrInline().add(inline);
		inline.setDocPr(dmlFactory.createCTNonVisualDrawingProps());
		inline.getDocPr().setDescr(description);
		inline.getDocPr().setName(name);
		inline.getDocPr().setId(findDrawingId());
		inline.setGraphic(dmlFactory.createGraphic());
		inline.setExtent(dmlFactory.createCTPositiveSize2D());
		inline.getExtent().setCx(5486400);
		inline.getExtent().setCy(3200400);
		inline.setEffectExtent(drawingFactory.createCTEffectExtent());
		inline.getEffectExtent().setB(0);
		inline.getEffectExtent().setL(0);
		inline.getEffectExtent().setR(0);
		inline.getEffectExtent().setT(0);
		inline.setDistB(0L);
		inline.setDistL(0L);
		inline.setDistT(0L);
		inline.setDistR(0L);
		inline.setCNvGraphicFramePr(dmlFactory.createCTNonVisualGraphicFrameProperties());
		inline.getGraphic().setGraphicData(dmlFactory.createGraphicData());
		inline.getGraphic().getGraphicData().setUri("http://schemas.openxmlformats.org/drawingml/2006/chart");
		CTRelId relId = chartFactory.createCTRelId();
		relId.setId(refId);
		inline.getGraphic().getGraphicData().getAny().add(chartFactory.createChart(relId));
		return paragraph;
	}

	protected Part findChart(String name) throws InvalidFormatException, XPathBinderAssociationIsPartialException, JAXBException {
		String id = findChartId(name);
		if (id == null)
			return null;
		Relationship relationship = wordMLPackage.getMainDocumentPart().getRelationshipsPart().getRelationships().getRelationship().parallelStream()
				.filter(part -> part.getId().equals(id)).findAny().orElse(null);
		if (relationship == null)
			return null;
		return wordMLPackage.getParts().get(new PartName("/word/" + relationship.getTarget()));
	}

	private String findChartId(String name) throws XPathBinderAssociationIsPartialException, JAXBException {
		P paragraph = findTableAnchor(name);
		if (paragraph == null)
			return document.getContent().parallelStream().filter(p -> p instanceof P).flatMap(p -> ((P) p).getContent().parallelStream()).filter(r -> r instanceof R)
					.flatMap(r -> ((R) r).getContent().parallelStream()).filter(d -> d instanceof JAXBElement).map(d -> ((JAXBElement<?>) d).getValue())
					.filter(d -> d instanceof Drawing).flatMap(d -> ((Drawing) d).getAnchorOrInline().parallelStream())
					.filter(i -> (i instanceof Inline) && ((Inline) i).getDocPr() != null && name.equals(((Inline) i).getDocPr().getDescr()))
					.flatMap(i -> ((Inline) i).getGraphic().getGraphicData().getAny().parallelStream()).map(v -> ((CTRelId) ((JAXBElement<?>) v).getValue()).getId()).findAny()
					.orElse(null);
		else
			return paragraph.getContent().parallelStream().filter(r -> r instanceof R).flatMap(r -> ((R) r).getContent().parallelStream()).filter(d -> d instanceof JAXBElement)
					.map(d -> ((JAXBElement<?>) d).getValue()).filter(d -> d instanceof Drawing).flatMap(d -> ((Drawing) d).getAnchorOrInline().parallelStream())
					.filter(i -> (i instanceof Inline)).flatMap(i -> ((Inline) i).getGraphic().getGraphicData().getAny().parallelStream())
					.map(v -> ((CTRelId) ((JAXBElement<?>) v).getValue()).getId()).findAny().orElse(null);
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
				insertBofore(paragraph, table);
			}
		}
	}

	private String getValueOrEmpty(String value) {
		return value == null ? "" : value;
	}

	private void setProgress(int progress) {
		this.progress = progress;
	}

	private void updateProperties() throws Docx4JException {
		setCustomProperty(_27001_NA_MEASURES, nonApplicableMeasure27001);
		setCustomProperty(_27002_NA_MEASURES, nonApplicableMeasure27002);

		setCustomProperty(MAX_IMPL,
				analysis.getSimpleParameters().stream().filter(p -> p.getDescription().equals(Constant.SOA_THRESHOLD)).map(p -> p.getValue().doubleValue()).findAny().orElse(0D));

		setCustomProperty("EXTERNAL_WL_VAL", analysis.getSimpleParameters().stream().filter(p -> p.getDescription().equals(Constant.PARAMETER_EXTERNAL_SETUP_RATE))
				.map(p -> p.getValue().doubleValue()).findAny().orElse(0D));

		setCustomProperty("INTERNAL_WL_VAL", analysis.getSimpleParameters().stream().filter(p -> p.getDescription().equals(Constant.PARAMETER_INTERNAL_SETUP_RATE))
				.map(p -> p.getValue().doubleValue()).findAny().orElse(0D));

		wordMLPackage.getDocPropsCorePart().getContents().setCategory(analysis.getCustomer().getOrganisation());

		wordMLPackage.getDocPropsCorePart().getContents().getCreator().getContent().clear();

		wordMLPackage.getDocPropsCorePart().getContents().getCreator().getContent()
				.add(String.format("%s %s", analysis.getOwner().getFirstName(), analysis.getOwner().getLastName()));

		wordMLPackage.getMainDocumentPart().getDocumentSettingsPart().getContents().setUpdateFields(factory.createBooleanDefaultTrue());
	}

	protected void setCustomProperty(String name, Object value) throws Docx4JException {
		if (value instanceof Number) {
			if (value instanceof Double)
				createProperty(name, false).setR8(((Number) value).doubleValue());
			else
				createProperty(name, false).setI4(((Number) value).intValue());
		} else if (value instanceof Boolean)
			createProperty(name, false).setBool((Boolean) value);
		else
			createProperty(name, false).setLpwstr(value.toString());
	}

	protected Property getProperty(String name) throws Docx4JException {
		return createProperty(name, true);
	}

	protected void setColor(CTBarSer ser, String color) {
		if (ser.getSpPr() == null) {
			ser.setSpPr(new CTShapeProperties());
			ser.getSpPr().setSolidFill(new CTSolidColorFillProperties());
			ser.getSpPr().getSolidFill().setSrgbClr(getColor(color));
		}
	}

	protected CTSRgbColor getColor(String color) {
		CTSRgbColor rgbColor = new CTSRgbColor();
		rgbColor.setVal(color.substring(1));
		return rgbColor;
	}

	protected Property createProperty(String name, boolean resued) throws Docx4JException {
		Property property = wordMLPackage.getDocPropsCustomPart().getProperty(name);
		if (!(property == null || resued)) {
			wordMLPackage.getDocPropsCustomPart().getContents().getProperty().remove(property);
			property = createProperty(name);
		} else if (property == null)
			property = createProperty(name);
		return property;
	}

	protected Property createProperty(String name) throws Docx4JException {
		org.docx4j.docProps.custom.ObjectFactory factory = new org.docx4j.docProps.custom.ObjectFactory();
		Property property = factory.createPropertiesProperty();
		property.setName(name);
		wordMLPackage.getDocPropsCustomPart();
		property.setFmtid(DocPropsCustomPart.fmtidValLpwstr);
		property.setPid(wordMLPackage.getDocPropsCustomPart().getNextPid());
		wordMLPackage.getDocPropsCustomPart().getContents().getProperty().add(property);
		return property;
	}

	protected List<Part> duplicateChart(int size, String chartName, String name) throws JAXBException, Docx4JException {
		int count = 1;
		if (size > Constant.CHAR_SINGLE_CONTENT_MAX_SIZE)
			count = Distribution.Distribut(size, Constant.CHAR_MULTI_CONTENT_SIZE, Constant.CHAR_MULTI_CONTENT_MAX_SIZE).getDivisor();
		Part part = findChart(chartName);
		if (part == null)
			return Collections.emptyList();
		List<Part> parts = new ArrayList<>(count);
		parts.add(part);
		if (count > 1) {
			List<Object> contents = new LinkedList<>();
			for (int i = 1; i < count; i++) {
				ClonePartResult result = cloneChart((Chart) part, name + i, name + i);
				contents.add(result.getP());
				parts.add(result.getPart());
			}
			insertAllAfter(findTableAnchor(chartName), contents);
		}
		return parts;
	}

	/**
	 * @return the docxFormatter
	 */
	public static DocxFormatter getDocxFormatter() {
		if (docxFormatter == null) {
			synchronized (Docx4jWordExporter.class) {
				if (docxFormatter == null)
					docxFormatter = buildFormatter();

			}

		}
		return docxFormatter;
	}

	public static void MergeCell(Tr row, int begin, int size, String color) {
		int length = begin + size;
		for (int i = 0; i < length; i++) {
			Tc cell = (Tc) row.getContent().get(i);
			if (color != null)
				setColor(cell, color);
			if (i < begin)
				continue;
			else {
				if (cell.getTcPr() == null)
					cell.setTcPr(Context.getWmlObjectFactory().createTcPr());
				if (i == begin) {
					cell.getTcPr().setHMerge(Context.getWmlObjectFactory().createTcPrInnerHMerge());
					cell.getTcPr().getHMerge().setVal("restart");
				} else {
					cell.getTcPr().setHMerge(Context.getWmlObjectFactory().createTcPrInnerHMerge());
					cell.getTcPr().getHMerge().setVal("continue");
				}
			}
		}
	}

	public static Tc setColor(Tc tc, String color) {
		if (tc.getTcPr() == null)
			tc.setTcPr(Context.getWmlObjectFactory().createTcPr());
		if (tc.getTcPr().getShd() == null)
			tc.getTcPr().setShd(Context.getWmlObjectFactory().createCTShd());
		tc.getTcPr().getShd().setFill(color);
		return tc;
	}

	public static void VerticalMergeCell(List<?> rows, int col, int begin, int size, String color) {
		int length = begin + size;
		for (int i = 0; i < length; i++) {
			Tc cell = (Tc) ((Tr) rows.get(i)).getContent().get(col);
			if (color != null)
				setColor(cell, color);
			if (i < begin)
				continue;
			else {
				if (cell.getTcPr() == null)
					cell.setTcPr(Context.getWmlObjectFactory().createTcPr());
				if (i == begin) {
					cell.getTcPr().setVMerge(Context.getWmlObjectFactory().createTcPrInnerVMerge());
					cell.getTcPr().getVMerge().setVal("restart");
				} else {
					cell.getTcPr().setVMerge(Context.getWmlObjectFactory().createTcPrInnerVMerge());
					cell.getTcPr().getVMerge().setVal("continue");
				}
			}
		}
	}

	private static DocxFormatter buildFormatter() {
		Docx4jFormatter docx4jFormatter = new Docx4jRiskAcceptanceFormatter();
		docx4jFormatter = new Docx4jHeatMapLegendFormatter(docx4jFormatter);
		docx4jFormatter = new Docx4jRiskHeatMapFormatter(docx4jFormatter);
		docx4jFormatter = new Docx4jSummaryFormatter(docx4jFormatter);
		docx4jFormatter = new Docx4jScopeFormatter(docx4jFormatter);
		docx4jFormatter = new Docx4jScenarioFormatter(docx4jFormatter);
		docx4jFormatter = new Docx4jRiskInformationFormatter(docx4jFormatter);
		docx4jFormatter = new Docx4jMeasureFormatter(docx4jFormatter);
		docx4jFormatter = new Docx4jImpactProbaFormatter(docx4jFormatter);
		docx4jFormatter = new Docx4jAssetFormatter(docx4jFormatter);
		docx4jFormatter = new Docx4jAssessmentFormatter(docx4jFormatter);
		docx4jFormatter = new Docx4jActionPlanFormatter(docx4jFormatter);
		return docx4jFormatter;
	}

}