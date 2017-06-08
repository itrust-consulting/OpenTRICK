/**
 * 
 */
package lu.itrust.business.TS.exportation.word.impl.docx4j;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableCell.XWPFVertAlign;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.docx4j.jaxb.XPathBinderAssociationIsPartialException;
import org.docx4j.wml.FldChar;
import org.docx4j.wml.P;
import org.docx4j.wml.PPrBase.TextAlignment;
import org.docx4j.wml.R;
import org.docx4j.wml.STFldCharType;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STShd;
import org.springframework.context.MessageSource;

import lu.itrust.business.TS.component.ChartGenerator;
import lu.itrust.business.TS.component.NaturalOrderComparator;
import lu.itrust.business.TS.component.chartJS.Chart;
import lu.itrust.business.TS.component.chartJS.Dataset;
import lu.itrust.business.TS.component.chartJS.helper.ColorBound;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.exportation.helper.ReportExcelSheet;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.model.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.model.actionplan.ActionPlanMode;
import lu.itrust.business.TS.model.actionplan.summary.SummaryStage;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.parameter.IBoundedParameter;
import lu.itrust.business.TS.model.parameter.impl.ImpactParameter;
import lu.itrust.business.TS.model.parameter.impl.RiskAcceptanceParameter;
import lu.itrust.business.TS.model.parameter.value.IValue;
import lu.itrust.business.TS.model.scale.ScaleType;
import lu.itrust.business.TS.model.scale.Translation;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescriptionText;

/**
 * @author eomar
 *
 */
public class Docx4jQualitativeReportExporter extends Docx4jWordExporter {

	private List<ColorBound> colorBounds = Collections.emptyList();

	public Docx4jQualitativeReportExporter(MessageSource messageSource, ServiceTaskFeedback serviceTaskFeedback, String realPath) {
		setMessageSource(messageSource);
		setServiceTaskFeedback(serviceTaskFeedback);
		setContextPath(realPath);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.exportation.AbstractWordExporter#generateActionPlan
	 * ()
	 */
	@Override
	protected void generateActionPlan() throws Exception {

		P paragraph = findTableAnchor("ActionPlan");

		List<ActionPlanEntry> actionplan = analysis.getActionPlan(ActionPlanMode.APQ);

		if (paragraph != null && actionplan != null && actionplan.size() > 0) {
			Tbl table = createTable("TableTSActionPlan", actionplan.size() + 1, 11);
			setCurrentParagraphId(TS_TAB_TEXT_2);
			Tr row = (Tr) table.getContent().get(0);
			setCellText((Tc) row.getContent().get(0), getMessage("report.action_plan.row_number", null, "Nr", locale));
			setCellText((Tc) row.getContent().get(1), getMessage("report.action_plan.norm", null, "Stds", locale));
			setCellText((Tc) row.getContent().get(2), getMessage("report.action_plan.reference", null, "Ref.", locale));
			setCellText((Tc) row.getContent().get(3), getMessage("report.action_plan.description", null, "Description", locale));
			setCellText((Tc) row.getContent().get(4), getMessage("report.action_plan.risk_count", null, "NR", locale));
			setCellText((Tc) row.getContent().get(5), getMessage("report.action_plan.cost", null, "CS", locale));
			setCellText((Tc) row.getContent().get(6), getMessage("report.action_plan.internal.workload", null, "IS", locale));
			setCellText((Tc) row.getContent().get(7), getMessage("report.action_plan.external.workload", null, "ES", locale));
			setCellText((Tc) row.getContent().get(8), getMessage("report.action_plan.investment", null, "INV", locale));
			setCellText((Tc) row.getContent().get(9), getMessage("report.measure.phase", null, "P", locale));
			setCellText((Tc) row.getContent().get(10), getMessage("report.measure.responsable", null, "Resp.", locale));
			setRepeatHeader(row);
			int nr = 1;
			// set data
			for (ActionPlanEntry entry : actionplan) {
				row = (Tr) table.getContent().get(nr);
				setCellText((Tc) row.getContent().get(0), "" + (nr++));
				setCellText((Tc) row.getContent().get(1), entry.getMeasure().getAnalysisStandard().getStandard().getLabel());
				setCellText((Tc) row.getContent().get(2), entry.getMeasure().getMeasureDescription().getReference());
				MeasureDescriptionText descriptionText = entry.getMeasure().getMeasureDescription().findByLanguage(analysis.getLanguage());
				addCellParagraph((Tc) row.getContent().get(3), descriptionText == null ? "" : descriptionText.getDomain() + (locale == Locale.FRENCH ? "\u00A0:" : ":"));
				((Tc) row.getContent().get(3)).getContent().parallelStream().flatMap(p -> ((P) p).getContent().parallelStream()).map(r -> (R) r).forEach(r -> {
					if (r.getRPr() == null)
						r.setRPr(factory.createRPr());
					r.getRPr().setB(factory.createBooleanDefaultTrue());
				});
				addCellParagraph((Tc) row.getContent().get(3), entry.getMeasure().getToDo(), true);
				addCellNumber((Tc) row.getContent().get(4), numberFormat.format(entry.getRiskCount()));
				addCellNumber((Tc) row.getContent().get(5), numberFormat.format(entry.getMeasure().getCost() * 0.001));
				numberFormat.setMaximumFractionDigits(1);
				addCellNumber((Tc) row.getContent().get(6), numberFormat.format(entry.getMeasure().getInternalWL()));
				addCellNumber((Tc) row.getContent().get(7), numberFormat.format(entry.getMeasure().getExternalWL()));
				numberFormat.setMaximumFractionDigits(0);
				addCellNumber((Tc) row.getContent().get(8), numberFormat.format(entry.getMeasure().getInvestment() * 0.001));
				addCellNumber((Tc) row.getContent().get(9), entry.getMeasure().getPhase().getNumber() + "");
				addCellNumber((Tc) row.getContent().get(10), entry.getMeasure().getResponsible());
			}
			document.getContent().add(document.getContent().indexOf(paragraph), table);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.exportation.AbstractWordExporter#
	 * generateActionPlanSummary()
	 */
	@Override
	protected void generateActionPlanSummary() throws Exception {
		XWPFParagraph paragraph = null;
		XWPFTable table = null;
		XWPFTableRow row = null;

		paragraph = findTableAnchor("<Summary>");

		if (paragraph == null)
			return;

		List<SummaryStage> summary = analysis.getSummary(ActionPlanMode.APQ);

		if (summary.isEmpty()) {
			paragraphsToDelete.add(paragraph);
			return;
		}

		// initialise table with 1 row and 1 column after the paragraph
		// cursor

		table = document.insertNewTbl(paragraph.getCTP().newCursor());

		table.setStyleID("TableTSSummary");
		setCurrentParagraphId(TS_TAB_TEXT_2);

		// set header

		row = table.getRow(0);

		for (int i = 1; i < 3; i++)
			row.addNewTableCell();

		int rownumber = 0;

		while (rownumber < 24) {

			if (rownumber == 0)
				row = table.getRow(rownumber);
			else
				row = table.createRow();

			switch (rownumber) {
			case 0: {
				int cellnumber = 0;
				setCellText((Tc) row.getContent().get(cellnumber), getMessage("report.summary_stage.phase.characteristics", null, "Phase characteristics", locale));
				for (SummaryStage stage : summary) {
					XWPFTableCell cell = (Tc) row.getContent().get(++cellnumber);
					if (cell == null)
						cell = row.addNewTableCell();
					setCellText(cell, stage.getStage().equalsIgnoreCase("Start(P0)") ? getMessage("report.summary_stage.phase.start", null, stage.getStage(), locale)
							: getMessage("report.summary_stage.phase", stage.getStage().split(" "), stage.getStage(), locale));
				}
				break;
			}

			case 1:
				MergeCell(row, 0, summary.size() + 1, null);
				setCellText((Tc) row.getContent().get(0), "1	" + getMessage("report.summary_stage.phase_duration", null, "Phase duration", locale));
				break;
			case 2: {
				setCellText((Tc) row.getContent().get(0), "1.1	" + getMessage("report.summary_stage.date.beginning", null, "Beginning date", locale));
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
				for (int i = 1; i < summary.size(); i++) {
					addCellParagraph((Tc) row.getContent().get(i + 1), dateFormat.format(analysis.findPhaseByNumber(i).getBeginDate()));
				}
				break;
			}
			case 3: {
				setCellText((Tc) row.getContent().get(0), "1.2	" + getMessage("report.summary_stage.date.end", null, "End date", locale));
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
				for (int i = 1; i < summary.size(); i++)
					addCellParagraph((Tc) row.getContent().get(i + 1), dateFormat.format(analysis.findPhaseByNumber(i).getEndDate()));
				break;
			}

			case 4:
				MergeCell(row, 0, summary.size() + 1, null);
				setCellText((Tc) row.getContent().get(0), "2	" + getMessage("report.summary_stage.compliance", null, "Compliance", locale));
				break;
			case 5: {
				int cellnumber = 0;
				setCellText((Tc) row.getContent().get(cellnumber),
						"2.1	" + getMessage("report.summary_stage.compliance.level", new Object[] { "27001" }, "Compliance level 27001 (%)...", locale));
				for (SummaryStage stage : summary)
					addCellNumber((Tc) row.getContent().get(++cellnumber),
							numberFormat.format(stage.getSingleConformance("27001") == null ? 0 : stage.getSingleConformance("27001") * 100));
				break;
			}
			case 6: {
				int cellnumber = 0;
				setCellText((Tc) row.getContent().get(cellnumber),
						"2.2	" + getMessage("report.summary_stage.compliance.level", new Object[] { "27002" }, "Compliance level 27002 (%)...", locale));
				for (SummaryStage stage : summary)
					addCellNumber((Tc) row.getContent().get(++cellnumber),
							numberFormat.format(stage.getSingleConformance("27002") == null ? 0 : stage.getSingleConformance("27002") * 100));
				break;
			}

			case 7: {
				int cellnumber = 0;
				setCellText((Tc) row.getContent().get(cellnumber),
						"2.3	" + getMessage("report.characteristic.count.not_compliant_measure", new Object[] { "27001" }, "Non-compliant measures of the 27001", locale));
				for (SummaryStage stage : summary)
					addCellNumber((Tc) row.getContent().get(++cellnumber), stage.getNotCompliantMeasure27001Count() + "");
				break;
			}

			case 8: {
				int cellnumber = 0;
				setCellText((Tc) row.getContent().get(cellnumber),
						"2.4	" + getMessage("report.characteristic.count.not_compliant_measure", new Object[] { "27002" }, "Non-compliant measures of the 27002", locale));
				for (SummaryStage stage : summary)
					addCellNumber((Tc) row.getContent().get(++cellnumber), stage.getNotCompliantMeasure27002Count() + "");
				break;
			}

			case 9:
				MergeCell(row, 0, summary.size() + 1, null);
				setCellText((Tc) row.getContent().get(0),
						"3	" + getMessage("report.summary_stage.evolution_of_implemented_measure", null, "Evolution of implemented measures", locale));
				break;
			case 10: {
				int cellnumber = 0;
				setCellText((Tc) row.getContent().get(cellnumber),
						"3.1	" + getMessage("report.summary_stage.number_of_measure_for_phase", null, "Number of measures for phase", locale));
				for (SummaryStage stage : summary)
					addCellNumber((Tc) row.getContent().get(++cellnumber), "" + stage.getMeasureCount());
				break;
			}
			case 11: {
				int cellnumber = 0;
				setCellText((Tc) row.getContent().get(cellnumber),
						"3.2	" + getMessage("report.summary_stage.implementted_measures", null, "Implemented measures (number)...", locale));
				for (SummaryStage stage : summary)
					addCellNumber((Tc) row.getContent().get(++cellnumber), "" + stage.getImplementedMeasuresCount());
				break;
			}
			case 12: {
				MergeCell(row, 0, summary.size() + 1, null);
				setCellText((Tc) row.getContent().get(0), "5	" + getMessage("report.summary_stage.resource.planning", null, "Resource planning", locale));
				// mrege columns
				break;
			}

			case 13: {
				MergeCell(row, 0, summary.size() + 1, null);
				setCellText((Tc) row.getContent().get(0), "5.1	" + getMessage("report.summary_stage.implementation.cost", null, "Implementation costs", locale));
				// mrege columns
				break;
			}
			case 14: {
				int cellnumber = 0;
				setCellText((Tc) row.getContent().get(cellnumber), "5.1.1	" + getMessage("report.summary_stage.workload.internal", null, "Internal workload (md)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber((Tc) row.getContent().get(++cellnumber), numberFormat.format(stage.getInternalWorkload()));
				numberFormat.setMaximumFractionDigits(0);
				break;
			}
			case 15: {
				int cellnumber = 0;
				setCellText((Tc) row.getContent().get(cellnumber), "5.1.2	" + getMessage("report.summary_stage.workload.external", null, "External workload (md)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber((Tc) row.getContent().get(++cellnumber), numberFormat.format(stage.getExternalWorkload()));
				numberFormat.setMaximumFractionDigits(0);
				break;
			}
			case 16: {
				int cellnumber = 0;
				setCellText((Tc) row.getContent().get(cellnumber), "5.1.3	" + getMessage("report.summary_stage.investment", null, "Investment (k€)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber((Tc) row.getContent().get(++cellnumber), numberFormat.format(Math.floor(stage.getInvestment() * 0.001)));
				numberFormat.setMaximumFractionDigits(0);
				break;
			}

			case 17: {
				int cellnumber = 0;
				setCellText((Tc) row.getContent().get(cellnumber),
						"5.1.4	" + getMessage("report.summary_stage.total.implement.phase.cost", null, "Total implement cost of phase (k€)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber((Tc) row.getContent().get(++cellnumber), numberFormat.format(stage.getImplementCostOfPhase() * 0.001), true);
				numberFormat.setMaximumFractionDigits(0);
				break;
			}

			case 18: {
				MergeCell(row, 0, summary.size() + 1, null);
				setCellText((Tc) row.getContent().get(0), "5.2	" + getMessage("report.summary_stage.cost.recurrent", null, "Recurrent costs", locale));
				break;
			}

			case 19: {
				int cellnumber = 0;
				setCellText((Tc) row.getContent().get(cellnumber), "5.2.1	" + getMessage("report.summary_stage.maintenance.internal", null, "Internal maintenance (md)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber((Tc) row.getContent().get(++cellnumber), numberFormat.format(stage.getInternalMaintenance()));
				numberFormat.setMaximumFractionDigits(0);
				break;
			}
			case 20: {
				int cellnumber = 0;
				setCellText((Tc) row.getContent().get(cellnumber), "5.2.2	" + getMessage("report.summary_stage.maintenance.external", null, "External maintenance (md)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber((Tc) row.getContent().get(++cellnumber), numberFormat.format(stage.getExternalMaintenance()));
				numberFormat.setMaximumFractionDigits(0);
				break;
			}
			case 21: {
				int cellnumber = 0;
				setCellText((Tc) row.getContent().get(cellnumber), "5.2.3	" + getMessage("report.summary_stage.investment.recurrent", null, "Recurrent investment (k€)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber((Tc) row.getContent().get(++cellnumber), numberFormat.format(stage.getRecurrentInvestment() * 0.001));
				numberFormat.setMaximumFractionDigits(0);
				break;
			}

			case 22: {
				int cellnumber = 0;
				setCellText((Tc) row.getContent().get(cellnumber),
						"5.2.4	" + getMessage("report.summary_stage.total.cost.recurrent", null, "Total recurrent costs (k€)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber((Tc) row.getContent().get(++cellnumber), numberFormat.format(stage.getRecurrentCost() * 0.001), true);
				numberFormat.setMaximumFractionDigits(0);
				break;
			}
			case 23: {
				int cellnumber = 0;
				setCellText((Tc) row.getContent().get(cellnumber), "5.3	" + getMessage("report.summary_stage.cost.total_of_phase", null, "Total cost of phase (k€)", locale));
				numberFormat.setMaximumFractionDigits(1);
				for (SummaryStage stage : summary)
					addCellNumber((Tc) row.getContent().get(++cellnumber), numberFormat.format(stage.getTotalCostofStage() * 0.001), true);
				numberFormat.setMaximumFractionDigits(0);
				break;
			}
			}
			rownumber++;
		}
		paragraphsToDelete.add(paragraph);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.exportation.AbstractWordExporter#
	 * generateAssessements()
	 */
	@Override
	protected void generateAssessements() throws XPathBinderAssociationIsPartialException, JAXBException {
		P paragraphOrigin = findTableAnchor("Assessment");
		setCurrentParagraphId(TS_TAB_TEXT_2);
		Map<Asset, List<Assessment>> assessementsByAsset = analysis.findSelectedAssessmentByAsset();
		if (paragraphOrigin != null && assessementsByAsset.size() > 0) {
			List<ScaleType> scaleTypes = analysis.getImpacts();
			scaleTypes.removeIf(scale -> scale.getName().equals(Constant.DEFAULT_IMPACT_NAME));
			int colLength = 4 + scaleTypes.size(), colIndex = 0, rawLength = 0;
			TextAlignment alignmentLeft = createAlignment("left"), alignmentCenter = createAlignment("center");
			for (Asset asset : assessementsByAsset.keySet()) {
				P paragraph = factory.createP();
				setText(paragraph, asset.getName());
				setStyle(paragraph, "TSEstimationTitle");
				document.getContent().add(document.getContent().indexOf(paragraphOrigin), paragraph);
				List<Assessment> assessments = assessementsByAsset.get(asset);
				Tbl table = createTable("TableTSAssessment", assessments.size() + 1, colLength);
				Tr row = (Tr) table.getContent().get(rawLength = 0);
				setCellText((Tc) row.getContent().get(colIndex++), getMessage("report.assessment.scenarios", null, "Scenarios", locale), alignmentLeft);
				for (ScaleType scaleType : scaleTypes)
					setCellText((Tc) row.getContent().get(colIndex++), scaleType.getShortName(languageAlpha2), alignmentCenter);
				setCellText((Tc) row.getContent().get(colIndex++), getMessage("report.assessment.probability", null, "P.", locale), alignmentCenter);
				setCellText((Tc) row.getContent().get(colIndex++), getMessage("report.assessment.owner", null, "Owner", locale));
				setCellText((Tc) row.getContent().get(colIndex++), getMessage("report.assessment.comment", null, "Comment", locale));
				for (Assessment assessment : assessments) {
					row = (Tr) table.getContent().get(rawLength++);
					colIndex = 0;
					setCellText((Tc) row.getContent().get(colIndex++), assessment.getScenario().getName());
					for (ScaleType scaleType : scaleTypes) {
						IValue impact = assessment.getImpact(scaleType.getName());
						setCellText((Tc) row.getContent().get(colIndex++),
								impact == null || impact.getLevel() == 0 ? getMessage("label.status.na", null, "na", locale) : impact.getLevel() + "", alignmentCenter);
					}
					int probaLevel = valueFactory.findProbLevel(assessment.getLikelihood());
					setCellText((Tc) row.getContent().get(colIndex++), probaLevel == 0 ? getMessage("label.status.na", null, "na", locale) : probaLevel + "", alignmentCenter);
					addCellParagraph((Tc) row.getContent().get(colIndex++), assessment.getOwner());
					addCellParagraph((Tc) row.getContent().get(colIndex++), assessment.getComment());
				}
				document.getContent().add(document.getContent().indexOf(paragraphOrigin), table);
				document.getContent().add(document.getContent().indexOf(paragraphOrigin), addTableCaption(getMessage("report.assessment.table.caption",
						new Object[] { asset.getName() }, String.format("Risk estimation for the asset %s", asset.getName()), locale)));
				colIndex = 0;
			}
			assessementsByAsset.clear();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.exportation.AbstractWordExporter#generateAssets(
	 * java.lang.String, java.util.List)
	 */
	@Override
	protected void generateAssets(String name, List<Asset> assets) throws XPathBinderAssociationIsPartialException, JAXBException {
		P paragraph = (P) findTableAnchor(name);
		if (paragraph != null) {
			setCurrentParagraphId(TS_TAB_TEXT_2);
			Tbl table = createTable("TableTSAsset", assets.size() + 1, 5);
			Tr row = (Tr) table.getContent().get(0);
			TextAlignment alignment = createAlignment("left");
			// set header
			setCellText((Tc) row.getContent().get(0), getMessage("report.asset.title.number.row", null, "Nr", locale));
			setCellText((Tc) row.getContent().get(1), getMessage("report.asset.title.name", null, "Name", locale));
			setCellText((Tc) row.getContent().get(2), getMessage("report.asset.title.type", null, "Type", locale));
			setCellText((Tc) row.getContent().get(3), getMessage("report.asset.title.value", null, "Value(k€)", locale));
			setCellText((Tc) row.getContent().get(4), getMessage("report.asset.title.comment", null, "Comment", locale));
			setRepeatHeader(row);
			int number = 1;
			// set data
			for (Asset asset : assets) {
				row = (Tr) table.getContent().get(number);
				setCellText((Tc) row.getContent().get(0), "" + (number++));
				setCellText((Tc) row.getContent().get(1), asset.getName(), alignment);
				setCellText((Tc) row.getContent().get(2), getDisplayName(asset.getAssetType()));
				addCellNumber((Tc) row.getContent().get(3), kEuroFormat.format(asset.getValue() * 0.001));
				addCellParagraph((Tc) row.getContent().get(4), asset.getComment());
			}
			document.getContent().add(document.getContent().indexOf(paragraph), table);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.exportation.AbstractWordExporter#
	 * generateExtendedParameters(java.lang.String)
	 */
	@Override
	protected void generateExtendedParameters(String type) throws Exception {
		XWPFParagraph paragraph = null;
		String parmetertype = "", languuage = locale.getLanguage().toUpperCase();
		if (type.equals(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME))
			parmetertype = "Impact";
		else if (type.equals(Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME))
			parmetertype = "Proba";

		paragraph = findTableAnchor("<" + parmetertype + ">");

		if (paragraph != null) {
			setCurrentParagraphId(TS_TAB_TEXT_2);
			if (parmetertype == "Proba")
				buildImpactProbabilityTable(paragraph, getMessage("report.parameter.title." + type.toLowerCase(), null, type, locale), parmetertype,
						analysis.getLikelihoodParameters());
			else {
				Map<ScaleType, List<ImpactParameter>> impacts = analysis.getImpactParameters().stream().filter(parameter -> !parameter.isMatch(Constant.DEFAULT_IMPACT_NAME))
						.collect(Collectors.groupingBy(ImpactParameter::getType));
				generateImpactList(impacts.keySet());
				for (ScaleType scaleType : impacts.keySet()) {
					Translation title = scaleType.get(languuage);
					buildImpactProbabilityTable(paragraph, title == null ? scaleType.getDisplayName() : title.getName(), parmetertype, impacts.get(scaleType));
				}

			}
			paragraphsToDelete.add(paragraph);
		}

	}

	private void generateImpactList(Set<ScaleType> impacts) {

		XWPFParagraph paragraph = findTableAnchor("<impact-list>");
		if (paragraph == null)
			return;
		String style = "BulletL1";
		while (!paragraph.getRuns().isEmpty())
			paragraph.removeRun(0);
		boolean isFirst = true;
		for (ScaleType scaleType : impacts) {
			if (!isFirst)
				paragraph = document.insertNewParagraph(paragraph.getCTP().newCursor());
			else
				isFirst = false;
			paragraph.setStyle(style);
			paragraph.createRun().setText(scaleType.getTranslate(languageAlpha2));
		}
	}

	private void buildImpactProbabilityTable(XWPFParagraph paragraph, String title, String type, List<? extends IBoundedParameter> parameters) {
		XWPFParagraph titleParagraph = document.insertNewParagraph(paragraph.getCTP().newCursor());
		titleParagraph.createRun().setText(title);
		titleParagraph.setStyle("TSEstimationTitle");
		XWPFTable table = document.insertNewTbl(paragraph.getCTP().newCursor());
		table.setStyleID("TableTS" + type);
		setCurrentParagraphId(TS_TAB_TEXT_2);
		// set header
		XWPFTableRow row = table.getRow(0);
		for (int i = 1; i < 3; i++) {
			XWPFTableCell cell = (Tc) row.getContent().get(i);
			if (cell != null)
				cell.setColor(HEADER_COLOR);
			else
				row.addNewTableCell().setColor(HEADER_COLOR);
		}
		setCellText((Tc) row.getContent().get(0), getMessage("report.parameter.level", null, "Level", locale));
		setCellText((Tc) row.getContent().get(1), getMessage("report.parameter.label", null, "Label", locale));
		setCellText((Tc) row.getContent().get(2), getMessage("report.parameter.qualification", null, "Qualification", locale));
		// set data
		for (IBoundedParameter parameter : parameters) {
			if (parameter.getLevel() == 0)
				continue;
			row = table.createRow();
			while (row.getTableCells().size() < 3)
				row.addNewTableCell();
			setCellText((Tc) row.getContent().get(0), "" + parameter.getLevel());
			setCellText((Tc) row.getContent().get(1), parameter.getLabel());
			setCellText((Tc) row.getContent().get(2), parameter.getDescription());

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.exportation.AbstractWordExporter#writeChart(lu.
	 * itrust.business.TS.exportation.helper.ReportExcelSheet)
	 */
	@Override
	protected void writeChart(ReportExcelSheet reportExcelSheet) throws Exception {
		try {
			switch (reportExcelSheet.getName()) {
			case "Compliance27001":
			case "Compliance27002":
				if (reportExcelSheet.getName().equalsIgnoreCase("Compliance27001"))
					serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.chart.data.compliance.27001", "Printing compliance 27001 excel sheet", increase(2)));// 72%
				else
					serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.chart.data.compliance.27002", "Printing compliance 27002 excel sheet", increase(2)));// 74%
				generateComplianceGraphic(reportExcelSheet);
				break;
			case "RiskByScenarioType":
				serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.chart.data.risk.by.scenario.type", "Printing risk by scenario type excel sheet", increase(3)));// 77%
				generateRiskByScenarioTypeGraphic(reportExcelSheet);
				break;
			case "RiskByScenario":
				serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.chart.data.risk.by.scenario", "Printing risk by scenario excel sheet", increase(5)));// 82%
				generateRiskByScenarioGraphic(reportExcelSheet);
				break;
			case "RiskByAssetType":
				serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.chart.data.risk.by.asset.type", "Printing risk by asset type excel sheet", increase(2)));// 84%
				generateRiskByAssetTypeGraphic(reportExcelSheet);
				break;
			case "RiskByAsset":
				serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.chart.data.risk.by.asset", "Printing risk by asset excel sheet", increase(5)));// 89%
				generateRiskByAssetGraphic(reportExcelSheet);
				break;
			}
		} finally {
			reportExcelSheet.save();
		}
	}

	private void generateRiskByAssetGraphic(ReportExcelSheet reportExcelSheet) {
		Map<String, List<Assessment>> assessmentByAsset = analysis.getAssessments().parallelStream().filter(Assessment::isSelected).sorted((a1, a2) -> {
			return NaturalOrderComparator.compareTo(a1.getAsset().getName(), a2.getAsset().getName());
		}).collect(Collectors.groupingBy(assessment -> assessment.getAsset().getName()));
		generateRiskGraphic(reportExcelSheet, assessmentByAsset);
	}

	private void generateRiskByAssetTypeGraphic(ReportExcelSheet reportExcelSheet) {
		Map<String, List<Assessment>> assessments = analysis.getAssessments().parallelStream().filter(Assessment::isSelected).sorted((a1, a2) -> {
			return NaturalOrderComparator.compareTo(getDisplayName(a1.getAsset().getAssetType()), getDisplayName(a2.getAsset().getAssetType()));
		}).collect(Collectors.groupingBy(assessment -> getDisplayName(assessment.getAsset().getAssetType())));
		generateRiskGraphic(reportExcelSheet, assessments);
	}

	private void generateRiskByScenarioGraphic(ReportExcelSheet reportExcelSheet) {
		Map<String, List<Assessment>> assessments = analysis.getAssessments().parallelStream().filter(Assessment::isSelected).sorted((a1, a2) -> {
			return NaturalOrderComparator.compareTo(a1.getScenario().getName(), a2.getScenario().getName());
		}).collect(Collectors.groupingBy(assessment -> assessment.getScenario().getName()));

		generateRiskGraphic(reportExcelSheet, assessments);
	}

	private void generateRiskByScenarioTypeGraphic(ReportExcelSheet reportExcelSheet) {
		Map<String, List<Assessment>> assessments = analysis.getAssessments().parallelStream().filter(Assessment::isSelected).sorted((a1, a2) -> {
			return NaturalOrderComparator.compareTo(getDisplayName(a1.getScenario().getType()), getDisplayName(a2.getScenario().getType()));
		}).collect(Collectors.groupingBy(assessment -> getDisplayName(assessment.getScenario().getType())));
		generateRiskGraphic(reportExcelSheet, assessments);
	}

	@Override
	protected void generateOtherData() {
		serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.table.risk_heat.map", "Printing risk heat map", increase(3)));
		generateRiskHeatMap();

		serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.table.risk_acceptance", "Printing risk acceptance table", increase(2)));
		generateRiskAcceptance();
	}

	private void generateRiskAcceptance() {
		XWPFParagraph paragraph = null;
		XWPFTable table = null;
		XWPFTableRow row = null;
		paragraph = findTableAnchor("<risk-acceptance>");
		if (paragraph != null) {
			table = document.insertNewTbl(paragraph.getCTP().newCursor());
			table.setStyleID("TableTSRiskAcceptance");
			setCurrentParagraphId(TS_TAB_TEXT_2);
			// set header
			row = table.getRow(0);
			for (int i = 1; i < 2; i++)
				row.addNewTableCell();
			// set header
			table.getRow(0).getCell(0).setText(getMessage("report.risk_acceptance.title.level", null, "Risk level", locale));
			table.getRow(0).getCell(1).setText(getMessage("report.risk_acceptance.title.acceptance.criteria", null, "Risk acceptance criteria", locale));
			// set data
			for (RiskAcceptanceParameter parameter : analysis.getRiskAcceptanceParameters()) {
				row = table.createRow();
				XWPFTableCell cell = (Tc) row.getContent().get(0);
				addCellParagraph(cell, parameter.getLabel()).setAlignment(ParagraphAlignment.CENTER);
				addCellParagraph(cell, getMessage("report.risk_acceptance.importance_threshold.value", new Object[] { parameter.getValue().intValue() },
						"Importance threshold: " + parameter.getValue().intValue(), locale), true);
				addCellParagraph((Tc) row.getContent().get(1), parameter.getDescription());
				if (!parameter.getColor().isEmpty())
					cell.setColor(parameter.getColor().substring(1));
			}
		}

		if (paragraph != null)
			paragraphsToDelete.add(paragraph);
	}

	private void generateRiskHeatMap() {
		Chart chart = ChartGenerator.generateRiskHeatMap(analysis, valueFactory);
		generateRiskHeatMap(chart, "<risk-heat-map-summary>");
		generateRiskHeatMap(chart, "<risk-heat-map>");
		List<RiskAcceptanceParameter> riskAcceptanceParameters = analysis.getRiskAcceptanceParameters();
		colorBounds = new ArrayList<>(riskAcceptanceParameters.size());
		for (int i = 0; i < riskAcceptanceParameters.size(); i++) {
			RiskAcceptanceParameter parameter = riskAcceptanceParameters.get(i);
			if (colorBounds.isEmpty())
				colorBounds.add(new ColorBound(parameter.getColor(), parameter.getLabel(), 0, parameter.getValue().intValue()));
			else if (riskAcceptanceParameters.size() == (i + 1))
				colorBounds.add(new ColorBound(parameter.getColor(), parameter.getLabel(), riskAcceptanceParameters.get(i - 1).getValue().intValue(), Integer.MAX_VALUE));
			else
				colorBounds.add(
						new ColorBound(parameter.getColor(), parameter.getLabel(), riskAcceptanceParameters.get(i - 1).getValue().intValue(), parameter.getValue().intValue()));
		}
	}

	@SuppressWarnings("unchecked")
	private void generateRiskHeatMap(Chart chart, String anchor) {
		XWPFParagraph paragraphOriginal = null;
		XWPFTable table = null;
		XWPFTableRow row = null;
		int rowIndex = 0;
		paragraphOriginal = findTableAnchor(anchor);
		if (paragraphOriginal != null) {
			XWPFParagraph paragraph = document.insertNewParagraph(paragraphOriginal.getCTP().newCursor());
			paragraph.setStyle("BodyOfText");
			int index[] = { chart.getLegends().size() };
			chart.getLegends().forEach(legend -> {
				XWPFRun run = paragraph.createRun();
				run.setText(legend.getLabel());
				CTRPr pr = run.getCTR().isSetRPr() ? run.getCTR().getRPr() : run.getCTR().addNewRPr();
				CTShd cTShd = pr.isSetShd() ? pr.getShd() : pr.addNewShd();
				cTShd.setVal(STShd.CLEAR);
				cTShd.setColor("auto");
				cTShd.setFill(legend.getColor().substring(1));
				if (--index[0] > 0) {
					paragraph.createRun().addTab();
					paragraph.createRun().addTab();
				}
			});

			paragraph.setAlignment(ParagraphAlignment.CENTER);

			table = document.insertNewTbl(paragraphOriginal.getCTP().newCursor());
			table.setStyleID("TableTSRiskHeatMap");
			// set header
			row = table.getRow(rowIndex++);
			for (int i = 1; i < chart.getLabels().size() + 2; i++)
				row.addNewTableCell();
			setCellText((Tc) row.getContent().get(0), getMessage("report.risk_heat_map.title.impact", null, "Impact", locale));
			for (int i = 0; i < chart.getDatasets().size(); i++) {
				Dataset<List<String>> dataset = (Dataset<List<String>>) chart.getDatasets().get(i);
				if (i > 0) {
					row = table.getRow(rowIndex++);
					if (row == null)
						row = table.createRow();
				}
				XWPFTableCell cell = (Tc) row.getContent().get(1);
				cell.setVerticalAlignment(XWPFVertAlign.CENTER);
				addCellParagraph(cell, dataset.getLabel()).setAlignment(ParagraphAlignment.CENTER);
				cell.setColor(LIGHT_CELL_COLOR);
				for (int j = 0; j < dataset.getData().size(); j++) {
					cell = (Tc) row.getContent().get(j + 2);
					cell.setVerticalAlignment(XWPFVertAlign.CENTER);
					Object data = dataset.getData().get(j);
					if (data instanceof Integer)
						addCellParagraph(cell, data.toString()).setAlignment(ParagraphAlignment.CENTER);
					else
						addCellParagraph(cell, "").setAlignment(ParagraphAlignment.CENTER);
					cell.setColor(dataset.getBackgroundColor().get(j).substring(1));
				}
			}
			row = table.getRow(rowIndex++);
			if (row == null)
				row = table.createRow();
			for (int i = 0; i < chart.getLabels().size(); i++) {
				XWPFTableCell cell = (Tc) row.getContent().get(i + 2);
				cell.setVerticalAlignment(XWPFVertAlign.CENTER);
				addCellParagraph(cell, chart.getLabels().get(i)).setAlignment(ParagraphAlignment.CENTER);
				cell.setColor(LIGHT_CELL_COLOR);
			}

			row = table.getRow(rowIndex);
			if (row == null)
				row = table.createRow();
			setCellText((Tc) row.getContent().get(0), getMessage("report.risk_heat_map.title.probability", null, "Probability", locale));
		}

		if (paragraphOriginal != null)
			paragraphsToDelete.add(paragraphOriginal);

	}

	private void generateRiskGraphic(ReportExcelSheet reportExcelSheet, Map<String, List<Assessment>> assessmentByAsset) {
		XSSFSheet xssfSheet = reportExcelSheet.getXssfWorkbook().getSheetAt(0);
		XSSFRow row = getRow(xssfSheet, 1), colors = getRow(xssfSheet, 0);
		for (int i = 0; i < colorBounds.size(); i++) {
			XSSFCell cell = getCell(row, i + 1, CellType.STRING), color = getCell(colors, i + 1, CellType.STRING);
			cell.setCellValue(colorBounds.get(i).getLabel());
			color.setCellValue(colorBounds.get(i).getColor().substring(1));
		}

		int rowIndex = 2;

		for (Entry<String, List<Assessment>> entry : assessmentByAsset.entrySet()) {
			clearColorBoundCount();
			entry.getValue().forEach(assessment -> {
				int importance = valueFactory.findImportance(assessment);
				colorBounds.stream().filter(colorBound -> colorBound.isAccepted(importance)).findAny().ifPresent(colorBound -> colorBound.setCount(colorBound.getCount() + 1));
			});
			if (!colorBounds.parallelStream().anyMatch(colorBound -> colorBound.getCount() > 0))
				continue;
			row = getRow(xssfSheet, rowIndex++);
			getCell(row, 0, CellType.STRING).setCellValue(entry.getKey());
			for (int i = 0; i < colorBounds.size(); i++) {
				if (colorBounds.get(i).getCount() > 0)
					getCell(row, i + 1, CellType.NUMERIC).setCellValue(colorBounds.get(i).getCount());
			}
		}
	}

	private XSSFRow getRow(XSSFSheet xssfSheet, int index) {
		XSSFRow row = xssfSheet.getRow(index);
		if (row == null)
			row = xssfSheet.createRow(index);
		return row;
	}

	private XSSFCell getCell(XSSFRow row, int index, CellType cellType) {
		XSSFCell cell = (Tc) row.getContent().get(index);
		if (cell == null)
			cell = row.createCell(index, cellType);
		return cell;
	}

	private void clearColorBoundCount() {
		colorBounds.parallelStream().forEach(color -> color.setCount(0));
	}

}
