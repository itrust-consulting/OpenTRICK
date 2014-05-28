package lu.itrust.business.TS.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Assessment;
import lu.itrust.business.TS.Asset;
import lu.itrust.business.TS.ItemInformation;
import lu.itrust.business.TS.RiskInformation;
import lu.itrust.business.TS.Scenario;
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

		XWPFDocument document = new XWPFDocument(new FileInputStream(this.getContext().getRealPath("/WEB-INF/tmp/TOD_001_analysis-report-FR_V2.1.docx")));

		this.document = document;

		generateItemInformation();

		generateAssets();

		generateScenarios();

		generateAssessements();

		generateThreats();

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

		paragraph.removeRun(0);

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