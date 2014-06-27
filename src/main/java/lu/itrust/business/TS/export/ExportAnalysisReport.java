package lu.itrust.business.TS.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.AnalysisNorm;
import lu.itrust.business.TS.Assessment;
import lu.itrust.business.TS.Asset;
import lu.itrust.business.TS.ExtendedParameter;
import lu.itrust.business.TS.ItemInformation;
import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.Parameter;
import lu.itrust.business.TS.RiskInformation;
import lu.itrust.business.TS.Scenario;
import lu.itrust.business.TS.actionplan.ActionPlanAsset;
import lu.itrust.business.TS.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.actionplan.ActionPlanMode;
import lu.itrust.business.TS.actionplan.SummaryStage;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.component.ActionPlanManager;
import lu.itrust.business.component.AssessmentManager;
import lu.itrust.business.component.RiskInformationManager;
import lu.itrust.business.component.helper.ALE;
import lu.itrust.business.service.ServiceAnalysis;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.hibernate.Hibernate;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.springframework.context.MessageSource;

/**
 * ExportReport.java: <br>
 * Detailed description...
 * 
 * @author smenghi itrust consulting s.a.rl.:
 * @version
 * @since May 27, 2014
 */
public class ExportAnalysisReport {

	private XWPFDocument document = null;

	private ServiceAnalysis serviceAnalysis = null;

	private ServletContext context = null;

	private Analysis analysis = null;

	private Locale locale = null;

	private MessageSource messageSource;

	public ExportAnalysisReport() {
	}

	/**
	 * exportToWordDocument: <br>
	 * Description
	 * 
	 * @param analysisId
	 * @param context
	 * @param serviceAnalysis
	 * @return
	 * @throws Exception
	 */
	public File exportToWordDocument(Integer analysisId, ServletContext context, ServiceAnalysis serviceAnalysis, boolean template) throws Exception {

		if (!serviceAnalysis.exists(analysisId)) {
			throw new IllegalArgumentException("error.analysis.not_exist");
		} else if (serviceAnalysis.isProfile(analysisId)) {
			throw new IllegalArgumentException("error.analysis.is_profile");
		} else if (!serviceAnalysis.hasData(analysisId)) {
			throw new IllegalArgumentException("error.analysis.no_data");
		}

		Analysis analysis = serviceAnalysis.get(analysisId);
		
		if(!(analysis.getLanguage() == null || analysis.getLanguage().getAlpha3().equalsIgnoreCase("fra")))
			locale  = Locale.ENGLISH;
		else locale = Locale.FRENCH;

		this.analysis = analysis;

		this.context = context;

		this.serviceAnalysis = serviceAnalysis;

		XWPFDocument document = null;

		File doctemp = new File(this.getContext().getRealPath("/WEB-INF/tmp/STA_" + analysis.getLabel() + "_V" + analysis.getVersion() + ".docx"));

		if (!doctemp.exists())
			doctemp.createNewFile();

		if (template) {
			File doctemplate = new File(this.getContext().getRealPath("/WEB-INF/data/TOD_001_analysis-report-FR_V2.1.dotx"));
			OPCPackage pkg = OPCPackage.open(doctemplate.getAbsoluteFile());
			pkg.replaceContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.template.main+xml",
					"application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml");
			pkg.save(doctemp);
			document = new XWPFDocument(new FileInputStream(doctemp));
		} else {
			XWPFDocument templateDocx = new XWPFDocument(new FileInputStream(new File(this.getContext().getRealPath("/WEB-INF/data/TOD_001_analysis-report-FR_V2.1.dotx"))));
			document = new XWPFDocument();
			XWPFStyles xwpfStyles = document.createStyles();
			xwpfStyles.setStyles(templateDocx.getStyle());
		}

		this.document = document;

		for (XWPFTable table : this.document.getTables())
			System.out.println(table.getStyleID());

		if (!template)
			generatePlaceholders();

		generateItemInformation();

		generateAssets();

		generateScenarios();

		generateAssessements();

		generateThreats();

		generateExtendedParameters(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME);

		generateExtendedParameters(Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME);

		generateActionPlan();

		generateActionPlanSummary();

		generateMeasures();

		document.write(new FileOutputStream(doctemp));

		return doctemp;
	}

	private void generatePlaceholders() {
		/*
		 * document.createParagraph().createRun().setText("<Scope>");
		 * document.createParagraph().createRun().setText("<Asset>");
		 * document.createParagraph().createRun().setText("<Scenario>");
		 * document.createParagraph().createRun().setText("<Assessment>");
		 * document.createParagraph().createRun().setText("<Threat>");
		 * document.createParagraph().createRun().setText("<Vul>");
		 * document.createParagraph().createRun().setText("<Risk>");
		 * document.createParagraph().createRun().setText("<Impact>");
		 * document.createParagraph().createRun().setText("<Proba>");
		 * document.createParagraph().createRun().setText("<ActionPlan>");
		 * document.createParagraph().createRun().setText("<Summary>");
		 */
		document.createParagraph().createRun().setText("<Measures>");
	}

	private void generateMeasures() {
		XWPFParagraph paragraph = null;
		XWPFTable table = null;
		XWPFTableRow row = null;

		paragraph = findParagraphByText("<Measures>");

		// run = paragraph.getRuns().get(0);

		List<AnalysisNorm> anorms = analysis.getAnalysisNorms();

		if (paragraph != null && anorms.size() > 0) {

			paragraph.getRuns().removeAll(paragraph.getRuns());

			for (AnalysisNorm anorm : anorms) {

				// initialise table with 1 row and 1 column after the paragraph
				// cursor

				paragraph.createRun().setText(anorm.getNorm().getLabel());

				table = document.insertNewTbl(paragraph.getCTP().newCursor());

				table.setStyleID("TableTSMeasure");

				CTTblWidth width = table.getCTTbl().addNewTblPr().addNewTblW();
				width.setW(BigInteger.valueOf(10000));

				// set header

				row = table.getRow(0);

				for (int i = 1; i < 14; i++)
					row.createCell();

				row.getCell(0).setText(messageSource.getMessage("rapport.measure.reference", null, "Ref.", locale));
				row.getCell(1).setText(messageSource.getMessage("rapport.measure.domain", null, "Domain", locale));
				row.getCell(2).setText(messageSource.getMessage("rapport.measure.status", null, "ST", locale));
				row.getCell(3).setText(messageSource.getMessage("rapport.measure.implementation_rate", null, "IR(%)", locale));
				row.getCell(4).setText(messageSource.getMessage("rapport.measure.internal.workload", null, "IS(md)", locale));
				row.getCell(5).setText(messageSource.getMessage("rapport.measure.external.workload", null, "ES(md)", locale));
				row.getCell(6).setText(messageSource.getMessage("rapport.measure.investment", null, "INV(k€)", locale));
				row.getCell(7).setText(messageSource.getMessage("rapport.measure.life_time", null, "LT(y)", locale));
				row.getCell(8).setText(messageSource.getMessage("rapport.measure.internal.maintenance", null, "IM(md)", locale));
				row.getCell(9).setText(messageSource.getMessage("rapport.measure.external.maintenance", null, "EM(md)", locale));
				row.getCell(10).setText(messageSource.getMessage("rapport.measure.recurrent.investment", null, "RINV(k€)", locale));
				row.getCell(11).setText(messageSource.getMessage("rapport.measure.cost", null, "CS(k€)", locale));
				row.getCell(12).setText(messageSource.getMessage("rapport.measure.comment", null, "Comment", locale));
				row.getCell(13).setText(messageSource.getMessage("rapport.measure.to_do", null, "To Do", locale));
				// set data
				for (Measure measure : anorm.getMeasures()) {
					row = table.createRow();
					Hibernate.initialize(measure);
					Hibernate.initialize(measure.getPhase());
					Hibernate.initialize(measure.getMeasureDescription());

					if (measure.getMeasureDescription().getLevel() < 3) {
						row.getCell(0).setText(measure.getMeasureDescription().findByLanguage(analysis.getLanguage()).getDomain());
					} else {
						// System.out.println(entry.toString());
						row.getCell(0).setText(measure.getMeasureDescription().getReference());
						row.getCell(1).setText(measure.getMeasureDescription().findByLanguage(analysis.getLanguage()).getDomain());
						row.getCell(2).setText(measure.getStatus());
						XWPFParagraph paragraph2 = row.getCell(3).addParagraph();
						paragraph2.setAlignment(ParagraphAlignment.RIGHT);
						paragraph2.createRun().setText(new DecimalFormat("#").format(measure.getImplementationRateValue()));
						paragraph2 = row.getCell(4).addParagraph();
						paragraph2.createRun().setText(new DecimalFormat("#.#").format(measure.getInternalWL()));
						paragraph2 = row.getCell(5).addParagraph();
						paragraph2.createRun().setText(new DecimalFormat("#.#").format(measure.getExternalWL()));
						paragraph2 = row.getCell(6).addParagraph();
						paragraph2.createRun().setText(new DecimalFormat("#").format(measure.getInvestment() / 1000.0));
						paragraph2 = row.getCell(7).addParagraph();
						paragraph2.createRun().setText(new DecimalFormat("#").format(measure.getLifetime()));
						paragraph2 = row.getCell(8).addParagraph();
						paragraph2.createRun().setText(new DecimalFormat("#.#").format(measure.getInternalMaintenance()));
						paragraph2 = row.getCell(9).addParagraph();
						paragraph2.createRun().setText(new DecimalFormat("#.#").format(measure.getExternalMaintenance()));
						paragraph2 = row.getCell(10).addParagraph();
						paragraph2.createRun().setText(new DecimalFormat("#").format(measure.getRecurrentInvestment() / 1000.0));
						paragraph2 = row.getCell(11).addParagraph();
						paragraph2.createRun().setText(new DecimalFormat("#").format(measure.getCost() / 1000.0));
						row.getCell(12).setText(measure.getComment());
						row.getCell(13).setText(measure.getToDo());
					}
				}

			}
		}
	}

	private void generateActionPlanSummary() throws Exception {
		XWPFParagraph paragraph = null;
		XWPFTable table = null;
		XWPFTableRow row = null;

		paragraph = findParagraphByText("<Summary>");

		// run = paragraph.getRuns().get(0);

		List<SummaryStage> summary = analysis.getSummary(ActionPlanMode.APPN);

		if (paragraph != null && summary.size() > 0) {

			paragraph.getRuns().removeAll(paragraph.getRuns());

			// initialise table with 1 row and 1 column after the paragraph
			// cursor

			table = document.insertNewTbl(paragraph.getCTP().newCursor());

			table.setStyleID("TableTSSummary");

			CTTblWidth width = table.getCTTbl().addNewTblPr().addNewTblW();
			width.setW(BigInteger.valueOf(10000));

			// set header

			row = table.getRow(0);

			for (int i = 1; i < 3; i++)
				row.addNewTableCell();

			int rownumber = 0;

			while (rownumber < 22) {

				if (rownumber == 0)
					row = table.getRow(rownumber);
				else
					row = table.createRow();

				switch (rownumber) {
				case 0: {
					int cellnumber = 0;
					row.getCell(cellnumber).setText("Phase characteristics");
					for (SummaryStage stage : summary) {
						cellnumber++;
						row.getCell(cellnumber).setText(stage.getStage());
					}
					break;
				}
				case 1: {
					int cellnumber = 0;
					row.getCell(cellnumber).setText("Beginning date");
					for (SummaryStage stage : summary) {
						cellnumber++;
						if (cellnumber == 1)
							continue;
						// row.getCell(cellnumber).setText(stage.get);
					}
					break;
				}
				case 2: {
					int cellnumber = 0;
					row.getCell(cellnumber).setText("End date");
					cellnumber++;
					// row.getCell(cellnumber).setText(stage.getStage());

					break;
				}
				case 3: {
					int cellnumber = 0;
					row.getCell(cellnumber).setText("Compliance level 27001 (%)...");
					for (SummaryStage stage : summary) {
						cellnumber++;

						Double value = stage.getConformance27001();
						String svalue = String.valueOf(value.intValue() * 100);

						row.getCell(cellnumber).setText(svalue);
					}
					break;
				}
				case 4: {
					int cellnumber = 0;
					row.getCell(cellnumber).setText("Compliance level 27002 (%)...");
					for (SummaryStage stage : summary) {
						cellnumber++;
						Double value = stage.getConformance27002();
						String svalue = String.valueOf(value.intValue() * 100);
						row.getCell(cellnumber).setText(svalue);
					}
					break;
				}
				case 5: {
					int cellnumber = 0;
					row.getCell(cellnumber).setText("Number of measures for phase");
					for (SummaryStage stage : summary) {
						cellnumber++;
						row.getCell(cellnumber).setText("" + stage.getImplementedMeasuresCount());
					}
					break;
				}
				case 6: {
					int cellnumber = 0;
					row.getCell(cellnumber).setText("Implemented measures (number)...");
					for (SummaryStage stage : summary) {
						cellnumber++;
						row.getCell(cellnumber).setText("" + stage.getImplementedMeasuresCount());
					}
					break;
				}
				case 7: {
					int cellnumber = 0;
					row.getCell(cellnumber).setText("Profitability");
					// merge columns
					break;
				}
				case 8: {
					int cellnumber = 0;
					row.getCell(cellnumber).setText("ALE (k€/y)... at end");
					for (SummaryStage stage : summary) {
						cellnumber++;
						Double value = stage.getTotalALE() / 1000;
						String svalue = new DecimalFormat("#").format(value);
						row.getCell(cellnumber).setText(svalue);
					}
					break;
				}
				case 9: {
					int cellnumber = 0;
					row.getCell(cellnumber).setText("Risk reduction (k€/y)");
					for (SummaryStage stage : summary) {
						cellnumber++;
						Double value = stage.getDeltaALE() / 1000;
						String svalue = new DecimalFormat("#").format(value);
						row.getCell(cellnumber).setText(svalue);
					}
					break;
				}
				case 10: {
					int cellnumber = 0;
					row.getCell(cellnumber).setText("Average yearly cost of phase (k€/y)");
					for (SummaryStage stage : summary) {
						cellnumber++;
						Double value = stage.getCostOfMeasures() / 1000;
						String svalue = new DecimalFormat("#").format(value);
						row.getCell(cellnumber).setText(svalue);
					}
					break;
				}
				case 11: {
					int cellnumber = 0;
					row.getCell(cellnumber).setText("ROSI (k€/y)");
					for (SummaryStage stage : summary) {
						cellnumber++;
						Double value = stage.getROSI() / 1000;
						String svalue = new DecimalFormat("#").format(value);
						row.getCell(cellnumber).setText(svalue);
					}
					break;
				}
				case 12: {
					int cellnumber = 0;
					row.getCell(cellnumber).setText("Relative ROSI");
					for (SummaryStage stage : summary) {
						cellnumber++;
						Double value = stage.getRelativeROSI() / 1000;
						String svalue = new DecimalFormat("#").format(value);
						row.getCell(cellnumber).setText(svalue);
					}
					break;
				}
				case 13: {
					int cellnumber = 0;
					row.getCell(cellnumber).setText("Resource planning");
					// mrege columns
					break;
				}
				case 14: {
					int cellnumber = 0;
					row.getCell(cellnumber).setText("Internal workload (md)");
					for (SummaryStage stage : summary) {
						cellnumber++;
						row.getCell(cellnumber).setText("" + stage.getInternalWorkload());
					}
					break;
				}
				case 15: {
					int cellnumber = 0;
					row.getCell(cellnumber).setText("External workload (md)");
					for (SummaryStage stage : summary) {
						cellnumber++;
						row.getCell(cellnumber).setText("" + stage.getExternalWorkload());
					}
					break;
				}
				case 16: {
					int cellnumber = 0;
					row.getCell(cellnumber).setText("Investment (k€)");
					for (SummaryStage stage : summary) {
						cellnumber++;
						Double value = stage.getInvestment() / 1000;
						String svalue = new DecimalFormat("#").format(value);
						row.getCell(cellnumber).setText(svalue);
					}
					break;
				}
				case 17: {
					int cellnumber = 0;
					row.getCell(cellnumber).setText("Internal maintenance (md)");
					for (SummaryStage stage : summary) {
						cellnumber++;
						row.getCell(cellnumber).setText("" + stage.getInternalMaintenance());
					}
					break;
				}
				case 18: {
					int cellnumber = 0;
					row.getCell(cellnumber).setText("External maintenance (md)");
					for (SummaryStage stage : summary) {
						cellnumber++;
						row.getCell(cellnumber).setText("" + stage.getExternalMaintenance());
					}
					break;
				}
				case 19: {
					int cellnumber = 0;
					row.getCell(cellnumber).setText("Recurrent investment (k€)");
					for (SummaryStage stage : summary) {
						cellnumber++;
						Double value = stage.getRecurrentInvestment() / 1000;
						String svalue = new DecimalFormat("#").format(value);
						row.getCell(cellnumber).setText(svalue);
					}
					break;
				}
				case 20: {
					int cellnumber = 0;
					row.getCell(cellnumber).setText("Recurrent costs (k€)");
					for (SummaryStage stage : summary) {
						cellnumber++;
						Double value = stage.getRecurrentCost() / 1000;
						String svalue = new DecimalFormat("#").format(value);
						row.getCell(cellnumber).setText(svalue);
					}
					break;
				}
				case 21: {
					int cellnumber = 0;
					row.getCell(cellnumber).setText("Total cost of phase (k€)");
					for (SummaryStage stage : summary) {
						cellnumber++;
						Double value = stage.getTotalCostofStage() / 1000;
						String svalue = new DecimalFormat("#").format(value);
						row.getCell(cellnumber).setText(svalue);
					}
					break;
				}

				}

				rownumber++;

			}

		}

	}

	private void generateActionPlan() throws Exception {
		XWPFParagraph paragraph = null;
		XWPFTable table = null;
		XWPFTableRow row = null;

		paragraph = findParagraphByText("<ActionPlan>");

		// run = paragraph.getRuns().get(0);

		List<ActionPlanEntry> actionplan = analysis.getActionPlan(ActionPlanMode.APPN);

		if (paragraph != null && actionplan.size() > 0) {

			paragraph.getRuns().removeAll(paragraph.getRuns());

			// initialise table with 1 row and 1 column after the paragraph
			// cursor

			table = document.insertNewTbl(paragraph.getCTP().newCursor());

			table.setStyleID("TableTSActionPlan");

			CTTblWidth width = table.getCTTbl().addNewTblPr().addNewTblW();
			width.setW(BigInteger.valueOf(10000));

			// set header

			row = table.getRow(0);

			for (int i = 1; i < 12; i++)
				row.addNewTableCell();

			row.getCell(0).setText("Nr");
			row.getCell(1).setText("Norm");
			row.getCell(2).setText("Ref.");
			row.getCell(3).setText("Description");
			row.getCell(4).setText("ALE (k€/y)");
			row.getCell(5).setText("ΔNr");
			row.getCell(6).setText("CS (k€/y)");
			row.getCell(7).setText("ROSI (k€/y");
			row.getCell(8).setText("IS");
			row.getCell(9).setText("ES");
			row.getCell(10).setText("INV (k€)");
			row.getCell(11).setText("P");

			// add asset names
			List<Asset> assets = ActionPlanManager.getAssetsByActionPlanType(actionplan);
			for (Asset asset : assets)
				row.addNewTableCell().setText(asset.getName());

			int nr = 0;

			// set data
			for (ActionPlanEntry entry : actionplan) {
				row = table.createRow();
				nr++;
				Hibernate.initialize(entry);
				Hibernate.initialize(entry.getMeasure());
				Hibernate.initialize(entry.getActionPlanAssets());
				// System.out.println(entry.toString());
				row.getCell(0).setText("" + nr);
				row.getCell(1).setText(entry.getMeasure().getAnalysisNorm().getNorm().getLabel());
				row.getCell(2).setText(entry.getMeasure().getMeasureDescription().getReference());
				row.getCell(3).setText(entry.getMeasure().getMeasureDescription().findByLanguage(analysis.getLanguage()).getDomain() + ":");
				row.getCell(3).addParagraph().createRun().setText(entry.getMeasure().getToDo());
				Double value = entry.getTotalALE() / 1000;
				String svalue = new DecimalFormat("#").format(value);
				row.getCell(4).setText(svalue);
				row.getCell(5).setText(entry.getPosition());
				value = entry.getMeasure().getCost() / 1000;
				svalue = new DecimalFormat("#").format(value);
				row.getCell(6).setText(svalue);
				value = entry.getROI() / 1000;
				svalue = new DecimalFormat("#").format(value);
				row.getCell(7).setText(svalue);
				row.getCell(8).setText("" + entry.getMeasure().getInternalWL());
				row.getCell(9).setText("" + entry.getMeasure().getExternalWL());
				value = entry.getMeasure().getInvestment() / 1000;
				svalue = new DecimalFormat("#").format(value);
				row.getCell(10).setText(svalue);
				row.getCell(11).setText("" + entry.getMeasure().getPhase().getNumber());
				List<ActionPlanAsset> tmpassets = entry.getActionPlanAssets();
				for (int i = 12; i < assets.size() + 12; i++) {
					for (ActionPlanAsset aasset : tmpassets) {
						value = aasset.getCurrentALE() / 1000;
						svalue = new DecimalFormat("#").format(value);
						row.getCell(i).setText(svalue);
					}
				}
			}

			// Set the table style. If the style is not defined, the table style
			// will become
			// "Normal".
			// table.getCTTbl().getTblPr().addNewTblStyle().setVal("TableTS");

			// table.setStyleID("TableTS");

		}
	}

	private void generateExtendedParameters(String type) throws Exception {
		XWPFParagraph paragraph = null;
		XWPFTable table = null;
		XWPFTableRow row = null;
		String parmetertype = "";
		if (type.equals(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME))
			parmetertype = "Proba";
		else if (type.equals(Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME))
			parmetertype = "Impact";

		paragraph = findParagraphByText("<" + parmetertype + ">");

		List<Parameter> parameters = analysis.getParameters();

		List<ExtendedParameter> extendedParameters = new ArrayList<ExtendedParameter>();

		for (Parameter parameter : parameters) {
			if (parameter.getType().getLabel().equals(type))
				extendedParameters.add((ExtendedParameter) parameter);
		}

		if (paragraph != null && extendedParameters.size() > 0) {

			paragraph.getRuns().removeAll(paragraph.getRuns());

			// initialise table with 1 row and 1 column after the paragraph
			// cursor

			table = document.insertNewTbl(paragraph.getCTP().newCursor());

			table.setStyleID("TableTS" + parmetertype);

			CTTblWidth width = table.getCTTbl().addNewTblPr().addNewTblW();
			width.setW(BigInteger.valueOf(10000));

			// set header

			row = table.getRow(0);

			for (int i = 1; i < 6; i++)
				row.addNewTableCell();

			row.getCell(0).setText("Level");
			row.getCell(1).setText("Acro");
			row.getCell(2).setText("Qualification");
			row.getCell(3).setText("Value");
			row.getCell(4).setText("Value From [");
			row.getCell(5).setText("Value To [");

			// set data
			for (ExtendedParameter extendedParameter : extendedParameters) {
				row = table.createRow();
				row.getCell(0).setText("" + extendedParameter.getLevel());
				row.getCell(1).setText(extendedParameter.getAcronym());
				row.getCell(2).setText(extendedParameter.getDescription());

				Double value = 0.;
				String svalue = "";

				value = extendedParameter.getValue();
				if (type.equals(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME))
					value /= 1000.;
				svalue = new DecimalFormat("#.##").format(value);
				row.getCell(3).setText(svalue);

				value = extendedParameter.getBounds().getFrom();
				if (type.equals(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME))
					value /= 1000.;
				svalue = new DecimalFormat("#.##").format(value);
				row.getCell(4).setText(svalue);

				if (extendedParameter.getLevel() == 10)
					row.getCell(5).setText("+∞");
				else {
					value = extendedParameter.getBounds().getTo();
					if (type.equals(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME))
						value /= 1000.;
					svalue = new DecimalFormat("#.##").format(value);
					row.getCell(5).setText(svalue);
				}
			}

			// Set the table style. If the style is not defined, the table style
			// will become
			// "Normal".
			// table.getCTTbl().getTblPr().addNewTblStyle().setVal("TableTS");

			// table.setStyleID("TableTS");

		}
	}

	private void generateThreats() {
		XWPFParagraph paragraph = null;
		XWPFTableRow row = null;
		XWPFTable table = null;

		List<RiskInformation> riskInformations = analysis.getRiskInformations();

		Map<String, List<RiskInformation>> riskmapping = RiskInformationManager.Split(riskInformations);

		for (String key : riskmapping.keySet()) {

			paragraph = findParagraphByText("<" + key + ">");

			List<RiskInformation> elements = riskmapping.get(key);

			if (paragraph != null && elements.size() > 0) {

				paragraph.getRuns().removeAll(paragraph.getRuns());

				RiskInformation previouselement = null;

				// set data

				for (RiskInformation riskinfo : elements) {

					if ((previouselement == null) || (!riskinfo.getCategory().equals(previouselement.getCategory()))) {

						if (previouselement != null)
							document.insertNewParagraph(paragraph.getCTP().newCursor());

						table = document.insertNewTbl(paragraph.getCTP().newCursor());

						table.setStyleID("TableTS"+key);

						CTTblWidth width = table.getCTTbl().addNewTblPr().addNewTblW();
						width.setW(BigInteger.valueOf(10000));

						// set header
						row = table.getRow(0);
						row.getCell(0).setText("Id");
						row.addNewTableCell();
						table.getRow(0).getCell(1).setText(riskinfo.getCategory());

						if (riskinfo.getCategory().equals("Threat")) {
							row.addNewTableCell();
							row.getCell(2).setText("Acro");
							row.addNewTableCell();
							row.getCell(3).setText("Expo.");
							row.addNewTableCell();
							row.getCell(4).setText("Comment");
						} else {
							row.addNewTableCell();
							row.getCell(2).setText("Expo.");
							row.addNewTableCell();
							row.getCell(3).setText("Comment");
						}
					}

					previouselement = riskinfo;

					row = table.createRow();
					row.getCell(0).setText(riskinfo.getChapter());
					row.getCell(1).setText(riskinfo.getLabel());
					if (riskinfo.getCategory().equals("Threat")) {
						row.getCell(2).setText(riskinfo.getAcronym());
						row.getCell(3).setText("" + riskinfo.getExposed());
						row.getCell(4).setText("" + (riskinfo.getComment()));
					} else {
						row.getCell(2).setText("" + riskinfo.getExposed());
						row.getCell(3).setText("" + (riskinfo.getComment()));
					}
				}
			}
		}
	}

	private void generateAssessements() {
		XWPFParagraph paragraph = null;
		XWPFTable table = null;
		XWPFTableRow row = null;

		paragraph = findParagraphByText("<Assessment>");

		List<Assessment> assessments = analysis.getSelectedAssessments();

		double totalale = 0;

		for (Assessment assessment : assessments)
			totalale += assessment.getALE();

		if (paragraph != null && assessments.size() > 0) {

			paragraph.getRuns().removeAll(paragraph.getRuns());

			Map<String, ALE> alesmap = new LinkedHashMap<String, ALE>();
			Map<String, List<Assessment>> assessementsmap = new LinkedHashMap<String, List<Assessment>>();

			AssessmentManager.SplitAssessment(assessments, alesmap, assessementsmap);

			table = document.insertNewTbl(paragraph.getCTP().newCursor());

			table.setStyleID("TableTSAssessment");

			CTTblWidth width = table.getCTTbl().addNewTblPr().addNewTblW();
			width.setW(BigInteger.valueOf(10000));

			// set header

			row = table.getRow(0);

			// set header
			row.getCell(0).setText("Assets");

			row.getCell(0).getCTTc().addNewTcPr();

			if (row.getCell(0).getCTTc().getTcPr().getGridSpan() == null)
				row.getCell(0).getCTTc().getTcPr().addNewGridSpan();
			row.getCell(0).getCTTc().getTcPr().getGridSpan().setVal(BigInteger.valueOf(5));

			row = table.createRow();

			for (int i = 1; i < 5; i++)
				row.addNewTableCell();

			row.getCell(0).setText("Scenarios");
			row.getCell(1).setText("Fin.");
			row.getCell(2).setText("P.");
			row.getCell(3).setText("ALE(k€/y");
			row.getCell(4).setText("Comment");

			row = table.createRow();
			for (int i = 1; i < 5; i++)
				row.addNewTableCell();
			row.getCell(0).setText("Total ALE of Assets");
			Double value = totalale / 1000.;
			String svalue = new DecimalFormat("#").format(value);
			row.getCell(3).setText(svalue);

			for (String assetname : assessementsmap.keySet()) {
				List<Assessment> assessmentsofasset = assessementsmap.get(assetname);
				ALE ale = alesmap.get(assetname);
				row = table.createRow();
				for (int i = 1; i < 5; i++)
					row.addNewTableCell();
				row.getCell(0).setText(ale.getAssetName());
				value = ale.getValue() / 1000.;
				svalue = new DecimalFormat("#").format(value);
				row.getCell(3).setText(svalue);
				for (Assessment assessment : assessmentsofasset) {
					row = table.createRow();
					for (int i = 1; i < 5; i++)
						row.addNewTableCell();
					row.getCell(0).setText(assessment.getScenario().getName());
					row.getCell(1).setText(assessment.getImpactFin());
					row.getCell(2).setText(assessment.getLikelihood());
					value = assessment.getALE() / 1000.;
					svalue = new DecimalFormat("#").format(value);
					row.getCell(3).setText(svalue);
					row.getCell(4).setText(assessment.getComment());
				}
			}

		}
	}

	private void generateScenarios() {
		XWPFParagraph paragraph = null;
		XWPFTable table = null;
		XWPFTableRow row = null;

		paragraph = findParagraphByText("<Scenario>");

		List<Scenario> scenarios = analysis.getSelectedScenarios();

		if (paragraph != null && scenarios.size() > 0) {

			paragraph.getRuns().removeAll(paragraph.getRuns());

			table = document.insertNewTbl(paragraph.getCTP().newCursor());

			table.setStyleID("TableTSScenario");

			CTTblWidth width = table.getCTTbl().addNewTblPr().addNewTblW();
			width.setW(BigInteger.valueOf(10000));

			// set header

			row = table.getRow(0);

			for (int i = 1; i < 3; i++)
				row.addNewTableCell();

			// set header
			table.getRow(0).getCell(0).setText("Nr");
			table.getRow(0).getCell(1).setText("Name");
			table.getRow(0).getCell(2).setText("Description");

			int number = 0;

			// set data
			for (Scenario scenario : scenarios) {
				row = table.createRow();
				number++;
				row.getCell(0).setText("" + (number));
				row.getCell(1).setText(scenario.getName());
				row.getCell(2).setText(scenario.getDescription());
			}
		}
	}

	private void generateAssets() {
		XWPFParagraph paragraph = null;
		XWPFTable table = null;
		XWPFTableRow row = null;

		paragraph = findParagraphByText("<Asset>");

		List<Asset> assets = analysis.getSelectedAssets();

		if (paragraph != null && assets.size() > 0) {

			paragraph.getRuns().removeAll(paragraph.getRuns());

			table = document.insertNewTbl(paragraph.getCTP().newCursor());

			table.setStyleID("TableTSAsset");

			CTTblWidth width = table.getCTTbl().addNewTblPr().addNewTblW();
			width.setW(BigInteger.valueOf(10000));

			// set header

			row = table.getRow(0);

			for (int i = 1; i < 6; i++)
				row.addNewTableCell();

			// set header
			table.getRow(0).getCell(0).setText("Nr");
			table.getRow(0).getCell(1).setText("Name");
			table.getRow(0).getCell(2).setText("Type");
			table.getRow(0).getCell(3).setText("Value (k€)");
			table.getRow(0).getCell(4).setText("ALE (k€)");
			table.getRow(0).getCell(5).setText("Comment");

			int number = 0;

			// set data
			for (Asset asset : assets) {
				row = table.createRow();
				number++;
				row.getCell(0).setText("" + (number));
				row.getCell(1).setText(asset.getName());
				row.getCell(2).setText(asset.getAssetType().getType());
				Double value = asset.getValue() / 1000.;
				String svalue = new DecimalFormat("#").format(value);
				row.getCell(3).setText(svalue);
				value = asset.getALE() / 1000.;
				svalue = new DecimalFormat("#").format(value);
				row.getCell(4).setText(svalue);
				row.getCell(5).setText(asset.getComment());
			}
		}
	}

	private void generateItemInformation() throws Exception {
		XWPFParagraph paragraph = null;
		XWPFTable table = null;
		XWPFTableRow row = null;

		paragraph = findParagraphByText("<Scope>");

		List<ItemInformation> iteminformations = analysis.getItemInformations();

		if (paragraph != null && iteminformations.size() > 0) {

			paragraph.getRuns().removeAll(paragraph.getRuns());

			// initialise table with 1 row and 1 column after the paragraph
			// cursor

			table = document.insertNewTbl(paragraph.getCTP().newCursor());

			table.setStyleID("TableTSScope");

			CTTblWidth width = table.getCTTbl().addNewTblPr().addNewTblW();
			width.setW(BigInteger.valueOf(10000));

			// set header

			row = table.getRow(0);

			for (int i = 1; i < 2; i++)
				row.addNewTableCell();

			row.getCell(0).setText("Description");
			row.getCell(1).setText("Value");

			// set data
			for (ItemInformation iteminfo : iteminformations) {
				row = table.createRow();
				row.getCell(0).setText(iteminfo.getDescription());
				row.getCell(1).setText(iteminfo.getValue());
			}
		}
	}

	private XWPFParagraph findParagraphByText(String text) {

		List<XWPFParagraph> paragraphs = document.getParagraphs();

		for (XWPFParagraph paragraph : paragraphs) {
			if (paragraph.getParagraphText().equals(text))
				return paragraph;
		}
		return null;
	}

	/**
	 * getAnalysis: <br>
	 * Returns the analysis field value.
	 * 
	 * @return The value of the analysis field
	 */
	public Analysis getAnalysis() {
		return analysis;
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

	/**
	 * getContext: <br>
	 * Returns the context field value.
	 * 
	 * @return The value of the context field
	 */
	public ServletContext getContext() {
		return context;
	}

	/**
	 * setContext: <br>
	 * Sets the Field "context" with a value.
	 * 
	 * @param context
	 *            The Value to set the context field
	 */
	public void setContext(ServletContext context) {
		this.context = context;
	}

	/**
	 * getServiceAnalysis: <br>
	 * Returns the serviceAnalysis field value.
	 * 
	 * @return The value of the serviceAnalysis field
	 */
	public ServiceAnalysis getServiceAnalysis() {
		return serviceAnalysis;
	}

	/**
	 * setServiceAnalysis: <br>
	 * Sets the Field "serviceAnalysis" with a value.
	 * 
	 * @param serviceAnalysis
	 *            The Value to set the serviceAnalysis field
	 */
	public void setServiceAnalysis(ServiceAnalysis serviceAnalysis) {
		this.serviceAnalysis = serviceAnalysis;
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

	public MessageSource getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
}