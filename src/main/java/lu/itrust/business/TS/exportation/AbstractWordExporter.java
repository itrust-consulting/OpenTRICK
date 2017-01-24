package lu.itrust.business.TS.exportation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
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
import org.openxmlformats.schemas.officeDocument.x2006.customProperties.CTProperty;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.springframework.context.MessageSource;

import lu.itrust.business.TS.component.ChartGenerator;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.exportation.helper.ReportExcelSheet;
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

public abstract class AbstractWordExporter {

	public static final String DEFAULT_PARAGRAHP_STYLE = "TabText1";

	protected static final String HEADER_COLOR = "CCC0D9";

	protected static final String SUB_HEADER_COLOR = "E5DFEC";

	private static final String _27001_NA_MEASURES = "27001_NA_MEASURES";

	private static final String _27002_NA_MEASURES = "27002_NA_MEASURES";

	private static final String DEFAULT_CELL_COLOR = "FFFFFF";

	private static final String MAX_IMPL = "MAX_IMPL";

	private static final String SUPER_HEAD_COLOR = HEADER_COLOR;

	protected static final String LIGHT_CELL_COLOR = SUB_HEADER_COLOR;

	private static final String ZERO_COST_COLOR = "e6b8b7";

	protected Analysis analysis = null;

	protected XWPFDocument document = null;

	protected ValueFactory valueFactory = null;

	protected String idTask;

	protected DecimalFormat kEuroFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.FRANCE);

	protected Locale locale = null;

	protected DecimalFormat numberFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.FRANCE);

	protected List<XWPFParagraph> paragraphsToDelete = new LinkedList<>();

	protected ServiceTaskFeedback serviceTaskFeedback;

	private String contextPath;

	private int maxProgress;

	private MessageSource messageSource;

	private int minProgress;

	private int nonApplicableMeasure27001 = 0;

	private int nonApplicableMeasure27002 = 0;

	private int progress;

	private String reportName;

	private File workFile;

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
	public void exportToWordDocument(Analysis analysis) throws Exception {
		InputStream inputStream = null;

		OutputStream outputStream = null;

		try {

			setAnalysis(analysis);

			switch (analysis.getLanguage().getAlpha3().toLowerCase()) {
			case "fra":
				locale = Locale.FRENCH;
				break;
			case "eng":
			default:
				locale = Locale.ENGLISH;
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

			File doctemplate = new File(String.format("%s/WEB-INF/data/%s.dotm", contextPath, reportName));
			OPCPackage pkg = OPCPackage.open(doctemplate.getAbsoluteFile());
			pkg.replaceContentType("application/vnd.ms-word.template.macroEnabledTemplate.main+xml", "application/vnd.ms-word.document.macroEnabled.main+xml");
			pkg.save(workFile);

			document = new XWPFDocument(inputStream = new FileInputStream(workFile));

			serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.data", "Printing data", increase(2)));// 5%

			generatePlaceholders();

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

			generateGraphics();

			updateProperties();

			CTBody body = document.getDocument().getBody();

			paragraphsToDelete.forEach(paragraph -> {
				int index = 0, find = -1;
				for (CTP ctp : body.getPArray()) {
					if (ctp.equals(paragraph.getCTP())) {
						find = index;
						break;
					} else
						index++;
				}
				body.removeP(find);
			});

			document.write(outputStream = new FileOutputStream(workFile));

			outputStream.flush();

		} finally {
			if (inputStream != null)
				inputStream.close();
			if (outputStream != null)
				outputStream.close();
		}
	}

	protected abstract void generateOtherData();

	/**
	 * getAnalysis: <br>
	 * Returns the analysis field value.
	 * 
	 * @return The value of the analysis field
	 */
	public Analysis getAnalysis() {
		return analysis;
	}

	public String getContextPath() {
		return contextPath;
	}

	/**
	 * getDocument: <br>
	 * Returns the document field value.
	 * 
	 * @return The value of the document field
	 */
	public XWPFDocument getDocument() {
		return document;
	}

	public String getIdTask() {
		return idTask;
	}

	public Locale getLocale() {
		return locale;
	}

	public int getMaxProgress() {
		if (maxProgress <= 0)
			return 100;
		return maxProgress;
	}

	public MessageSource getMessageSource() {
		return messageSource;
	}

	public int getMinProgress() {
		return minProgress;
	}

	public int getProgress() {
		return progress;
	}

	public String getReportName() {
		return reportName;
	}

	public ServiceTaskFeedback getServiceTaskFeedback() {
		return serviceTaskFeedback;
	}

	public File getWorkFile() {
		return workFile;
	}

	public int increase(int value) {
		if (!(value < 0 || value > 100)) {
			progress += value;
			if (progress > 100)
				setProgress(100);
		}
		return (int) (minProgress + (maxProgress - minProgress) * 0.01 * progress);
	}

	/**
	 * setAnalysis: <br>
	 * Sets the Field "analysis" with a value.
	 * 
	 * @param analysis
	 *            The Value to set the analysis field
	 */
	public void setAnalysis(Analysis analysis) {
		this.analysis = analysis;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	/**
	 * setDocument: <br>
	 * Sets the Field "document" with a value.
	 * 
	 * @param document
	 *            The Value to set the document field
	 */
	public void setDocument(XWPFDocument document) {
		this.document = document;
	}

	public void setIdTask(String idTask) {
		this.idTask = idTask;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public void setMaxProgress(int maxProgress) {
		this.maxProgress = maxProgress;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setMinProgress(int minProgress) {
		this.minProgress = minProgress;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public void setServiceTaskFeedback(ServiceTaskFeedback serviceTaskFeedback) {
		this.serviceTaskFeedback = serviceTaskFeedback;
	}

	public void setWorkFile(File workFile) {
		this.workFile = workFile;
	}

	protected XWPFRun addCellNumber(XWPFTableCell cell, String number) {
		return addCellNumber(cell, number, false);
	}

	protected XWPFRun addCellNumber(XWPFTableCell cell, String number, boolean isBold) {
		XWPFParagraph paragraph = cell.getParagraphs().size() == 1 ? cell.getParagraphs().get(0) : cell.addParagraph();
		paragraph.setStyle(AbstractWordExporter.DEFAULT_PARAGRAHP_STYLE);
		paragraph.setAlignment(ParagraphAlignment.RIGHT);
		XWPFRun run = paragraph.createRun();
		run.setBold(isBold);
		run.setText(number);
		return run;
	}

	protected XWPFParagraph addCellParagraph(XWPFTableCell cell, String text) {
		return addCellParagraph(cell, text, false);
	}

	protected XWPFParagraph addCellParagraph(XWPFTableCell cell, String text, boolean add) {
		XWPFParagraph paragraph = !add && cell.getParagraphs().size() == 1 ? cell.getParagraphs().get(0) : cell.addParagraph();
		if (text == null)
			text = "";
		String[] texts = text.split("(\r\n|\n\r|\r|\n)");
		for (int i = 0; i < texts.length; i++) {
			if (i > 0)
				paragraph = cell.addParagraph();
			paragraph.setStyle(AbstractWordExporter.DEFAULT_PARAGRAHP_STYLE);
			paragraph.createRun().setText(texts[i]);
		}
		return paragraph;
	}

	protected XWPFParagraph findTableAnchor(String text) {
		Optional<XWPFParagraph> result = document.getParagraphs().stream().filter(paragraph -> paragraph.getParagraphText().equals(text)).findAny();
		if (result.isPresent()) {
			result.get().setStyle("Figure");
			return result.get();
		}
		return null;
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

	protected abstract void generateAssessements();

	protected abstract void generateAssets(String name, List<Asset> assets);

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
				xssfSheet.getRow(rowCount++).getCell(columnIndex).setCellValue(getMessage("label.chart.phase", null, "Phase", locale) + " " + phase.getNumber());
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

	protected String getMessage(String code, Object[] parameters, String defaultMessage, Locale locale) {
		return messageSource.getMessage(code, parameters, defaultMessage, locale);
	}

	protected void setCellText(XWPFTableCell cell, String text, ParagraphAlignment alignment) {
		cell.setText(text == null ? "" : text);
		XWPFParagraph paragraph = cell.getParagraphs().get(0);
		paragraph.setStyle(AbstractWordExporter.DEFAULT_PARAGRAHP_STYLE);
		if (alignment != null)
			paragraph.setAlignment(alignment);
	}

	protected void setCellText(XWPFTableCell cell, String text) {
		setCellText(cell, text, null);
	}

	protected abstract void writeChart(ReportExcelSheet reportExcelSheet) throws Exception;

	private void generateAssets() {
		generateAssets("<Asset>", analysis.findSelectedAssets());
		generateAssets("<Asset-no-selected>", analysis.findNoAssetSelected());
	}

	private void generateGraphics() throws Exception {
		for (PackagePart packagePart : this.document.getPackage().getParts())
			if (packagePart.getPartName().getExtension().contains("xls"))
				writeChart(new ReportExcelSheet(packagePart, String.format("%s/WEB-INF/tmp/", contextPath)));
	}

	private void generateItemInformation() throws Exception {
		XWPFParagraph paragraph = null;
		XWPFTable table = null;
		XWPFTableRow row = null;

		paragraph = findTableAnchor("<Scope>");

		List<ItemInformation> iteminformations = analysis.getItemInformations();

		Collections.sort(iteminformations, new ComparatorItemInformation());

		if (paragraph != null && iteminformations.size() > 0) {

			// initialise table with 1 row and 1 column after the paragraph
			// cursor

			table = document.insertNewTbl(paragraph.getCTP().newCursor());

			table.setStyleID("TableTSScope");

			// set header

			row = table.getRow(0);

			for (int i = 1; i < 2; i++)
				row.addNewTableCell();

			setCellText(row.getCell(0), getMessage("report.scope.title.description", null, "Description", locale));

			setCellText(row.getCell(1), getMessage("report.scope.title.value", null, "Value", locale));

			// set data
			for (ItemInformation iteminfo : iteminformations) {
				row = table.createRow();
				setCellText(row.getCell(0), getMessage("report.scope.name." + iteminfo.getDescription().toLowerCase(), null, iteminfo.getDescription(), locale));
				addCellParagraph(row.getCell(1), iteminfo.getValue());
			}
		}

		if (paragraph != null)
			paragraphsToDelete.add(paragraph);
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
					setCellText(row.getCell(1), description == null ? "" : description.getDomain());
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
								switch (analysisStandard.getStandard().getLabel()) {
								case Constant.STANDARD_27001:
									nonApplicableMeasure27001++;
									break;
								case Constant.STANDARD_27002:
									nonApplicableMeasure27002++;
									break;
								}
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

	private void generatePlaceholders() {
		document.createParagraph().createRun().setText("<Measures>");
	}

	private void generateScenarios() {
		XWPFParagraph paragraph = null;
		XWPFTable table = null;
		XWPFTableRow row = null;

		paragraph = findTableAnchor("<Scenario>");

		List<Scenario> scenarios = analysis.findSelectedScenarios();

		if (paragraph != null && scenarios.size() > 0) {

			table = document.insertNewTbl(paragraph.getCTP().newCursor());

			table.setStyleID("TableTSScenario");

			// set header

			row = table.getRow(0);

			for (int i = 1; i < 3; i++)
				row.addNewTableCell();

			// set header
			setCellText(row.getCell(0), getMessage("report.scenario.title.number.row", null, "Nr", locale));
			setCellText(row.getCell(1), getMessage("report.scenario.title.name", null, "Name", locale));
			setCellText(row.getCell(2), getMessage("report.scenario.title.description", null, "Description", locale));

			int number = 1;

			// set data
			for (Scenario scenario : scenarios) {
				row = table.createRow();
				setCellText(row.getCell(0), "" + (number++));
				addCellParagraph(row.getCell(1), scenario.getName());
				addCellParagraph(row.getCell(2), scenario.getDescription());
			}
		}

		if (paragraph != null)
			paragraphsToDelete.add(paragraph);
	}

	private void generateThreats() {
		XWPFParagraph paragraph = null;
		XWPFTableRow row = null;
		XWPFTable table = null;

		List<RiskInformation> riskInformations = analysis.getRiskInformations();

		Map<String, List<RiskInformation>> riskmapping = RiskInformationManager.Split(riskInformations);

		for (String key : riskmapping.keySet()) {

			paragraph = findTableAnchor("<" + key + ">");

			List<RiskInformation> elements = riskmapping.get(key);

			if (paragraph != null && elements.size() > 0) {

				RiskInformation previouselement = null;

				// set data

				for (RiskInformation riskinfo : elements) {

					if ((previouselement == null) || (!riskinfo.getCategory().equals(previouselement.getCategory()))) {

						if (previouselement != null)
							document.insertNewParagraph(paragraph.getCTP().newCursor());

						table = document.insertNewTbl(paragraph.getCTP().newCursor());

						table.setStyleID("TableTS" + key);

						// set header
						row = table.getRow(0);
						setCellText(row.getCell(0), getMessage(String.format("report.risk_information.title.%s", "id"), null, "Id", locale));
						row.addNewTableCell();
						setCellText(row.getCell(1), getMessage(String.format("report.risk_information.title.%s", key.toLowerCase()), null, key.toLowerCase(), locale));
						if (riskinfo.getCategory().equals("Threat")) {
							row.addNewTableCell();
							setCellText(row.getCell(2), getMessage(String.format("report.risk_information.title.%s", "acro"), null, "Acro", locale));
							row.addNewTableCell();
							setCellText(row.getCell(3), getMessage(String.format("report.risk_information.title.%s", "expo"), null, "Expo.", locale), ParagraphAlignment.CENTER);
							row.addNewTableCell();
							setCellText(row.getCell(4), getMessage(String.format("report.risk_information.title.%s", "owner"), null, "Owner", locale));
							row.addNewTableCell();
							setCellText(row.getCell(5), getMessage(String.format("report.risk_information.title.%s", "comment"), null, "Comment", locale));
						} else {
							row.addNewTableCell();
							setCellText(row.getCell(2), getMessage(String.format("report.risk_information.title.%s", "expo"), null, "Expo.", locale), ParagraphAlignment.CENTER);
							row.addNewTableCell();
							setCellText(row.getCell(3), getMessage(String.format("report.risk_information.title.%s", "owner"), null, "Owner", locale));
							row.addNewTableCell();
							setCellText(row.getCell(4), getMessage(String.format("report.risk_information.title.%s", "comment"), null, "Comment", locale));
						}
					}

					previouselement = riskinfo;
					row = table.createRow();
					setCellText(row.getCell(0), riskinfo.getChapter());
					setCellText(row.getCell(1),
							getMessage(String.format("label.risk_information.%s.%s", riskinfo.getCategory().toLowerCase(), riskinfo.getChapter().replace(".", "_")), null,
									riskinfo.getLabel(), locale));
					String color = riskinfo.getChapter().matches("\\d(\\.0){2}") ? HEADER_COLOR : HEADER_COLOR;
					if (riskinfo.getCategory().equals("Threat")) {
						for (int i = 0; i < 3; i++)
							row.getCell(i).setColor(color);
						setCellText(row.getCell(2), riskinfo.getAcronym());
						setCellText(row.getCell(3), riskinfo.getExposed(), ParagraphAlignment.CENTER);
						setCellText(row.getCell(4), getValueOrEmpty(riskinfo.getOwner()));
						addCellParagraph(row.getCell(5), riskinfo.getComment());
					} else {
						for (int i = 0; i < 2; i++)
							row.getCell(i).setColor(color);
						setCellText(row.getCell(2), riskinfo.getExposed(), ParagraphAlignment.CENTER);
						setCellText(row.getCell(3), getValueOrEmpty(riskinfo.getOwner()));
						addCellParagraph(row.getCell(4), riskinfo.getComment());
					}
				}
			}

			if (paragraph != null)
				paragraphsToDelete.add(paragraph);
		}
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

	protected String getDisplayName(ScenarioType type) {
		return getMessage("label.scenario.type." + type.getName().toLowerCase(), null, type.getName(), locale);
	}

	protected String getDisplayName(AssetType type) {
		return getMessage("label.asset_type." + type.getType().toLowerCase(), null, type.getType(), locale);
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

}