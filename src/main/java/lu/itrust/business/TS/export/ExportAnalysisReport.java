package lu.itrust.business.TS.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Assessment;
import lu.itrust.business.TS.Asset;
import lu.itrust.business.TS.ExtendedParameter;
import lu.itrust.business.TS.ItemInformation;
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
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlCursor;
import org.hibernate.Hibernate;

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
	public File exportToWordDocument(Integer analysisId, ServletContext context, ServiceAnalysis serviceAnalysis) throws Exception {

		if (!serviceAnalysis.exists(analysisId)) {
			throw new IllegalArgumentException("error.analysis.not_exist");
		} else if (serviceAnalysis.isProfile(analysisId)) {
			throw new IllegalArgumentException("error.analysis.is_profile");
		} else if (!serviceAnalysis.hasData(analysisId)) {
			throw new IllegalArgumentException("error.analysis.no_data");
		}

		Analysis analysis = serviceAnalysis.get(analysisId);

		this.analysis = analysis;

		this.context = context;

		this.serviceAnalysis = serviceAnalysis;

		File template = new File(this.getContext().getRealPath("/WEB-INF/data/TOD_001_analysis-report-FR_V2.1.dotx"));
		File doctemp = new File(this.getContext().getRealPath("/WEB-INF/tmp/TOD_001_analysis-report-FR_V2.1.docx"));

		OPCPackage pkg = OPCPackage.open(template.getAbsoluteFile());
		pkg.replaceContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.template.main+xml", "application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml");
		pkg.save(doctemp);

		XWPFDocument templatedoc = new XWPFDocument(new FileInputStream(template));
		
		XWPFDocument document = new XWPFDocument(new FileInputStream(this.getContext().getRealPath("/WEB-INF/tmp/TOD_001_analysis-report-FR_V2.1.docx")));

		document.createStyles().setStyles(templatedoc.getStyle());;
		
		this.document = document;

		generateItemInformation();

		generateAssets();

		generateScenarios();

		generateAssessements();

		generateThreats();

		generateExtendedParameters(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME);

		generateExtendedParameters(Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME);

		generateActionPlan();

		generateActionPlanSummary();

		document.write(new FileOutputStream(new File(this.getContext().getRealPath("/WEB-INF/tmp/STA_" + analysis.getLabel() + "_V" + analysis.getVersion() + ".docx"))));

		File file = new File(this.getContext().getRealPath("/WEB-INF/tmp/STA_" + analysis.getLabel() + "_V" + analysis.getVersion() + ".docx"));

		return file;
	}

	private XWPFParagraph findParagraphByText(String text) {

		List<XWPFParagraph> paragraphs = document.getParagraphs();

		for (XWPFParagraph paragraph : paragraphs) {
			if (paragraph.getParagraphText().equals(text))
				return paragraph;
		}
		return null;
	}

	private void generateActionPlanSummary() throws Exception {
		XWPFParagraph paragraph = null;
		XWPFTable table = null;
		XWPFTableRow row = null;

		paragraph = findParagraphByText("<Summary>");

		// run = paragraph.getRuns().get(0);

		paragraph.removeRun(0);

		List<SummaryStage> summary = analysis.getSummary(ActionPlanMode.APPN);

		if (summary.size() > 0) {

			// initialise table with 1 row and 1 column after the paragraph cursor

			table = document.insertNewTbl(paragraph.getCTP().newCursor());

			// set header

			row = table.getRow(0);

			for (int i = 1; i < 3; i++)
				row.addNewTableCell();

			int rownumber = 0;

			while (rownumber < 22) {

				if(rownumber == 0)			
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
							row.getCell(cellnumber).setText("" + stage.getConformance27001()*100);
						}
						break;
					}
					case 4: {
						int cellnumber = 0;
						row.getCell(cellnumber).setText("Compliance level 27002 (%)...");
						for (SummaryStage stage : summary) {
							cellnumber++;
							row.getCell(cellnumber).setText("" + stage.getConformance27002()*100);
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
							row.getCell(cellnumber).setText(""+stage.getImplementedMeasuresCount());
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
							row.getCell(cellnumber).setText("" + stage.getTotalALE());
						}
						break;
					}
					case 9: {
						int cellnumber = 0;
						row.getCell(cellnumber).setText("Risk reduction (k€/y)");
						for (SummaryStage stage : summary) {
							cellnumber++;
							row.getCell(cellnumber).setText("" + stage.getDeltaALE());
						}
						break;
					}
					case 10: {
						int cellnumber = 0;
						row.getCell(cellnumber).setText("Average yearly cost of phase (k€/y)");
						for (SummaryStage stage : summary) {
							cellnumber++;
							row.getCell(cellnumber).setText("" + stage.getCostOfMeasures());
						}
						break;
					}
					case 11: {
						int cellnumber = 0;
						row.getCell(cellnumber).setText("ROSI (k€/y)");
						for (SummaryStage stage : summary) {
							cellnumber++;
							row.getCell(cellnumber).setText("" + stage.getROSI());
						}
						break;
					}
					case 12: {
						int cellnumber = 0;
						row.getCell(cellnumber).setText("Relative ROSI");
						for (SummaryStage stage : summary) {
							cellnumber++;
							row.getCell(cellnumber).setText("" + stage.getRelativeROSI());
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
							row.getCell(cellnumber).setText("" + stage.getInvestment());
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
							row.getCell(cellnumber).setText("" + stage.getRecurrentInvestment());
						}
						break;
					}
					case 20: {
						int cellnumber = 0;
						row.getCell(cellnumber).setText("Recurrent costs (k€)");
						for (SummaryStage stage : summary) {
							cellnumber++;
							row.getCell(cellnumber).setText("" + stage.getRecurrentCost());
						}
						break;
					}
					case 21: {
						int cellnumber = 0;
						row.getCell(cellnumber).setText("Total cost of phase (k€)");
						for (SummaryStage stage : summary) {
							cellnumber++;
							row.getCell(cellnumber).setText("" + stage.getTotalCostofStage());
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

		paragraph.removeRun(0);

		List<ActionPlanEntry> actionplan = analysis.getActionPlan(ActionPlanMode.APPN);

		if (actionplan.size() > 0) {

			// initialise table with 1 row and 1 column after the paragraph cursor

			table = document.insertNewTbl(paragraph.getCTP().newCursor());

			// set header

			row = table.getRow(0);

			for (int i = 1; i < 11; i++)
				row.addNewTableCell();

			row.getCell(0).setText("Nr");
			row.getCell(1).setText("Norm");
			row.getCell(2).setText("Ref.");
			row.getCell(3).setText("Description");
			row.getCell(4).setText("ALE (k€/y)");
			row.getCell(5).setText("ΔNr");
			row.getCell(6).setText("CS (k€/y)");
			row.getCell(6).setText("ROSI (k€/y");
			row.getCell(7).setText("IS");
			row.getCell(8).setText("ES");
			row.getCell(9).setText("INV (k€)");
			row.getCell(10).setText("P");

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
				row.getCell(4).setText("" + entry.getTotalALE());
				row.getCell(5).setText(Integer.valueOf(entry.getPosition()).toString());
				row.getCell(6).setText("" + entry.getMeasure().getCost());
				row.getCell(7).setText("" + entry.getMeasure().getInternalWL());
				row.getCell(8).setText("" + entry.getMeasure().getExternalWL());
				row.getCell(9).setText("" + entry.getMeasure().getInvestment());
				row.getCell(10).setText("" + entry.getMeasure().getPhase().getNumber());
				List<ActionPlanAsset> tmpassets = entry.getActionPlanAssets();
				for (int i = 11; i < assets.size() + 11; i++) {
					for (ActionPlanAsset aasset : tmpassets) {
						row.getCell(i).setText("" + aasset.getCurrentALE());
					}
				}
			}

			// Set the table style. If the style is not defined, the table style will become
			// "Normal".
			// table.getCTTbl().getTblPr().addNewTblStyle().setVal("Table TS 1");

			// table.setStyleID("Table TS 1");

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

		// run = paragraph.getRuns().get(0);

		paragraph.removeRun(0);

		List<Parameter> parameters = analysis.getParameters();

		List<ExtendedParameter> extendedParameters = new ArrayList<ExtendedParameter>();

		for (Parameter parameter : parameters) {
			if (parameter.getType().getLabel().equals(type))
				extendedParameters.add((ExtendedParameter) parameter);
		}

		if (extendedParameters.size() > 0) {

			// initialise table with 1 row and 1 column after the paragraph cursor

			table = document.insertNewTbl(paragraph.getCTP().newCursor());

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
				row.getCell(3).setText("" + extendedParameter.getValue());
				row.getCell(4).setText("" + extendedParameter.getBounds().getFrom());
				row.getCell(5).setText("" + extendedParameter.getBounds().getTo());
			}

			// Set the table style. If the style is not defined, the table style will become
			// "Normal".
			// table.getCTTbl().getTblPr().addNewTblStyle().setVal("Table TS 1");

			// table.setStyleID("Table TS 1");

		}
	}

	private void generateItemInformation() throws Exception {
		XWPFParagraph paragraph = null;
		XWPFTable table = null;
		XWPFTableRow row = null;

		paragraph = findParagraphByText("<Scope>");

		// run = paragraph.getRuns().get(0);

		paragraph.removeRun(0);

		List<ItemInformation> iteminformations = analysis.getItemInformations();

		if (iteminformations.size() > 0) {

			// initialise table with 1 row and 1 column after the paragraph cursor

			table = document.insertNewTbl(paragraph.getCTP().newCursor());

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

			// Set the table style. If the style is not defined, the table style will become
			// "Normal".
			table.getCTTbl().getTblPr().addNewTblStyle().setVal("Table TS 1");

			table.setStyleID("Table TS 1");

		}
	}

	private void generateAssets() {
		XWPFParagraph paragraph = null;
		XWPFTable table = null;
		XWPFTableRow row = null;

		paragraph = findParagraphByText("<Asset>");

		for (int i = 0; i< paragraph.getRuns().size();i++)
			paragraph.removeRun(i);

		List<Asset> assets = analysis.getSelectedAssets();

		if (assets.size() > 0) {

			table = document.insertNewTbl(paragraph.getCTP().newCursor());

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
				row.getCell(3).setText("" + asset.getValue());
				row.getCell(4).setText("" + (asset.getALE()));
				row.getCell(5).setText(asset.getComment());
			}
		}
	}

	private void generateScenarios() {
		XWPFParagraph paragraph = null;
		XWPFTable table = null;
		XWPFTableRow row = null;

		paragraph = findParagraphByText("<Scenario>");

		paragraph.removeRun(0);

		List<Scenario> scenarios = analysis.getSelectedScenarios();

		if (scenarios.size() > 0) {

			table = document.insertNewTbl(paragraph.getCTP().newCursor());

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

	private void generateAssessements() {
		XWPFParagraph paragraph = null;
		XWPFTable table = null;
		XWPFTableRow row = null;

		paragraph = findParagraphByText("<Assessment>");

		paragraph.removeRun(0);

		List<Assessment> assessments = analysis.getSelectedAssessments();

		double totalale = 0;

		for (Assessment assessment : assessments)
			totalale += assessment.getALE();

		if (assessments.size() > 0) {

			Map<String, ALE> alesmap = new LinkedHashMap<String, ALE>();
			Map<String, List<Assessment>> assessementsmap = new LinkedHashMap<String, List<Assessment>>();

			AssessmentManager.SplitAssessment(assessments, alesmap, assessementsmap);

			table = document.insertNewTbl(paragraph.getCTP().newCursor());

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
			row.getCell(3).setText("" + totalale * 0.0001);

			for (String assetname : assessementsmap.keySet()) {
				List<Assessment> assessmentsofasset = assessementsmap.get(assetname);
				ALE ale = alesmap.get(assetname);
				row = table.createRow();
				for (int i = 1; i < 5; i++)
					row.addNewTableCell();
				row.getCell(0).setText(ale.getAssetName());
				row.getCell(3).setText("" + ale.getValue());
				for (Assessment assessment : assessmentsofasset) {
					row = table.createRow();
					for (int i = 1; i < 5; i++)
						row.addNewTableCell();
					row.getCell(0).setText(assessment.getScenario().getName());
					row.getCell(1).setText(assessment.getImpactFin());
					row.getCell(2).setText(assessment.getLikelihood());
					row.getCell(3).setText("" + assessment.getALE());
					row.getCell(4).setText(assessment.getComment());
				}
			}

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

			paragraph.removeRun(0);

			List<RiskInformation> elements = riskmapping.get(key);

			if (elements.size() > 0) {

				RiskInformation previouselement = null;

				// set data

				for (RiskInformation riskinfo : elements) {

					if ((previouselement == null) || (!riskinfo.getCategory().equals(previouselement.getCategory()))) {

						table = document.insertNewTbl(paragraph.getCTP().newCursor());

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
}