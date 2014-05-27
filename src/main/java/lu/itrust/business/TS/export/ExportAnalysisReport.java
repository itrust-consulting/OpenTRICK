package lu.itrust.business.TS.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Asset;
import lu.itrust.business.TS.ItemInformation;
import lu.itrust.business.TS.RiskInformation;
import lu.itrust.business.component.RiskInformationManager;
import lu.itrust.business.service.ServiceAnalysis;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;

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

		File template = new File(this.getContext().getRealPath("/WEB-INF/data/TOD_001_itrust-Report-EN_V2.0.dotx"));
		File doctemp = new File(this.getContext().getRealPath("/WEB-INF/tmp/TOD_001_itrust-Report-EN_V2.0.docx"));

		OPCPackage pkg = OPCPackage.open(template.getAbsoluteFile());
		pkg.replaceContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.template.main+xml", "application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml");
		pkg.save(doctemp);

		XWPFDocument document = new XWPFDocument(new FileInputStream(this.getContext().getRealPath("/WEB-INF/tmp/TOD_001_itrust-Report-EN_V2.0.docx")));

		this.document = document;

		generateItemInformation();

		generateAssets();

		generateThreats();
		
		document.write(new FileOutputStream(new File(this.getContext().getRealPath("/WEB-INF/tmp/STA_" + analysis.getLabel() + "_V" + analysis.getVersion() + ".docx"))));

		File file = new File(this.getContext().getRealPath("/WEB-INF/tmp/STA_" + analysis.getLabel() + "_V" + analysis.getVersion() + ".docx"));

		return file;
	}

	private void generateItemInformation() throws Exception {
		XWPFParagraph paragraph = null;
		XWPFRun run = null;
		XWPFTable table = null;

		paragraph = document.createParagraph();
		run = paragraph.createRun();

		run.setText("Scope");
		paragraph.setStyle("Heading2");

		List<ItemInformation> iteminformations = analysis.getItemInformations();

		if (iteminformations.size() > 0) {

			// initialise table with number of rows and columns
			table = document.createTable(iteminformations.size() + 1, 2);

			// set header
			table.getRow(0).getCell(0).setText("Description");
			table.getRow(0).getCell(1).setText("Value");

			int row = 1;

			// set data
			for (ItemInformation iteminfo : iteminformations) {
				table.getRow(row).getCell(0).setText(iteminfo.getDescription());
				table.getRow(row).getCell(1).setText(iteminfo.getValue());
				row++;
			}
		}
	}

	private void generateAssets() {
		XWPFParagraph paragraph = null;
		XWPFRun run = null;
		XWPFTable table = null;

		paragraph = document.createParagraph();
		run = paragraph.createRun();

		run.setText("Asset");
		paragraph.setStyle("Heading2");

		List<Asset> assets = analysis.getAssets();

		if (assets.size() > 0) {

			// initialise table with number of rows and columns
			table = document.createTable(assets.size() + 1, 7);

			// set header
			table.getRow(0).getCell(0).setText("Nr");
			table.getRow(0).getCell(1).setText("Name");
			table.getRow(0).getCell(2).setText("Type");
			table.getRow(0).getCell(3).setText("Value (k€)");
			table.getRow(0).getCell(4).setText("ALE (k€)");
			table.getRow(0).getCell(5).setText("Sel");
			table.getRow(0).getCell(6).setText("Comment");

			int row = 1;

			// set data
			for (Asset asset : assets) {
				table.getRow(row).getCell(0).setText("" + (row));
				table.getRow(row).getCell(1).setText(asset.getName());
				table.getRow(row).getCell(2).setText(asset.getAssetType().getType());
				table.getRow(row).getCell(3).setText("" + asset.getValue());
				table.getRow(row).getCell(4).setText("" + (asset.getALE()));
				if (asset.isSelected())
					table.getRow(row).getCell(5).setText("x");
				else
					table.getRow(row).getCell(5).setText("");
				table.getRow(row).getCell(6).setText(asset.getComment());
				row++;
			}
		}
	}

	private void generateThreats() {
		XWPFParagraph paragraph = null;
		XWPFRun run = null;
		XWPFTable table = null;

		List<RiskInformation> riskInformations = analysis.getRiskInformations();

		Map<String, List<RiskInformation>> riskmapping = RiskInformationManager.Split(riskInformations);

		for (String key : riskmapping.keySet()) {

			paragraph = document.createParagraph();
			run = paragraph.createRun();

			run.setText(key);
			paragraph.setStyle("Heading2");

			List<RiskInformation> elements = riskmapping.get(key);

			if (elements.size() > 0) {

				

				RiskInformation previouselement = null;
				
				int row = 1;
				
				// set data
				
				
				
				for (RiskInformation riskinfo : elements) {
					
					if((previouselement == null) || (!riskinfo.getCategory().equals(previouselement.getCategory()))) {
						
						int elementcount = 0;
						
						for (RiskInformation ri : elements)
							if(ri.getCategory().equals(riskinfo.getCategory()))
								elementcount++;
						
						paragraph = document.createParagraph();
												
						// initialise table with number of rows and columns
						if (riskinfo.getCategory().equals("Threat")) {
							
							table = document.createTable(elementcount+1, 5);
						} else {
							table = document.createTable(elementcount+1, 4);
						}
							

						// set header
						table.getRow(0).getCell(0).setText("Id");
						table.getRow(0).getCell(1).setText(riskinfo.getCategory());

						if (riskinfo.getCategory().equals("Threat")) {
							table.getRow(0).getCell(2).setText("Acro");
							table.getRow(0).getCell(3).setText("Expo.");
							table.getRow(0).getCell(4).setText("Comment");
						} else {
							table.getRow(0).getCell(2).setText("Expo.");
							table.getRow(0).getCell(3).setText("Comment");
						}
						row = 1;
						
					}
					
					previouselement = riskinfo;
					
					table.getRow(row).getCell(0).setText(riskinfo.getChapter());
					table.getRow(row).getCell(1).setText(riskinfo.getLabel());
					if (riskinfo.getCategory().equals("Threat")) {
						table.getRow(row).getCell(2).setText(riskinfo.getAcronym());
						table.getRow(row).getCell(3).setText("" + riskinfo.getExposed());
						table.getRow(row).getCell(4).setText("" + (riskinfo.getComment()));
					} else {
						table.getRow(row).getCell(2).setText("" + riskinfo.getExposed());
						table.getRow(row).getCell(3).setText("" + (riskinfo.getComment()));
					}
					row++;
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