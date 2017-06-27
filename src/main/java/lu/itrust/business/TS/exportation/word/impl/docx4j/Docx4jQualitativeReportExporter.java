/**
 * 
 */
package lu.itrust.business.TS.exportation.word.impl.docx4j;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.docx4j.jaxb.XPathBinderAssociationIsPartialException;
import org.docx4j.wml.CTVerticalJc;
import org.docx4j.wml.P;
import org.docx4j.wml.PPrBase.TextAlignment;
import org.docx4j.wml.R;
import org.docx4j.wml.STVerticalJc;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;
import org.springframework.context.MessageSource;

import lu.itrust.business.TS.component.ChartGenerator;
import lu.itrust.business.TS.component.NaturalOrderComparator;
import lu.itrust.business.TS.component.chartJS.Chart;
import lu.itrust.business.TS.component.chartJS.Dataset;
import lu.itrust.business.TS.component.chartJS.helper.ColorBound;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceTaskFeedback;
import lu.itrust.business.TS.messagehandler.MessageHandler;
import lu.itrust.business.TS.model.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.model.actionplan.ActionPlanMode;
import lu.itrust.business.TS.model.actionplan.summary.SummaryStage;
import lu.itrust.business.TS.model.analysis.AnalysisType;
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
			TextAlignment alignment = createAlignment("left");
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
				setAlignment((Tc) row.getContent().get(3), alignment);
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
			insertBofore(paragraph, table);
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
		P paragraph = findTableAnchor("Summary");
		if (paragraph == null)
			return;
		List<SummaryStage> summary = getSummaryStage();
		// initialise table with 1 row and 1 column after the paragraph
		// cursor
		Tbl table = createTable("TableTSSummary", 24, summary.size() + 1);
		setCurrentParagraphId(TS_TAB_TEXT_2);
		// set header
		int rownumber = 0;
		while (rownumber < 24) {

			Tr row = (Tr) table.getContent().get(rownumber);

			switch (rownumber) {
			case 0: {
				int cellnumber = 0;
				setCellText((Tc) row.getContent().get(cellnumber), getMessage("report.summary_stage.phase.characteristics", null, "Phase characteristics", locale));
				for (SummaryStage stage : summary)
					setCellText((Tc) row.getContent().get(++cellnumber),
							stage.getStage().equalsIgnoreCase("Start(P0)") ? getMessage("report.summary_stage.phase.start", null, stage.getStage(), locale)
									: getMessage("report.summary_stage.phase", stage.getStage().split(" "), stage.getStage(), locale));
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
				break;
			}

			case 13: {
				MergeCell(row, 0, summary.size() + 1, null);
				setCellText((Tc) row.getContent().get(0), "5.1	" + getMessage("report.summary_stage.implementation.cost", null, "Implementation costs", locale));
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
		insertBofore(paragraph, table);

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

			List<Object> contents = new LinkedList<>();

			scaleTypes.removeIf(scale -> scale.getName().equals(Constant.DEFAULT_IMPACT_NAME));
			int colLength = 4 + scaleTypes.size(), colIndex = 0, rawLength = 0;
			TextAlignment alignmentLeft = createAlignment("left"), alignmentCenter = createAlignment("center");
			for (Asset asset : assessementsByAsset.keySet()) {
				P paragraph = factory.createP();
				setText(paragraph, asset.getName());
				setStyle(paragraph, "TSEstimationTitle");

				contents.add(paragraph);

				List<Assessment> assessments = assessementsByAsset.get(asset);

				Tbl table = createTable("TableTSAssessment", assessments.size() + 1, colLength);

				Tr row = (Tr) table.getContent().get(rawLength = 0);

				setRepeatHeader(row);

				setCellText((Tc) row.getContent().get(colIndex++), getMessage("report.assessment.scenarios", null, "Scenarios", locale), alignmentLeft);

				for (ScaleType scaleType : scaleTypes)
					setCellText((Tc) row.getContent().get(colIndex++), scaleType.getShortName(languageAlpha2), alignmentCenter);

				setCellText((Tc) row.getContent().get(colIndex++), getMessage("report.assessment.probability", null, "P.", locale), alignmentCenter);
				setCellText((Tc) row.getContent().get(colIndex++), getMessage("report.assessment.owner", null, "Owner", locale));
				setCellText((Tc) row.getContent().get(colIndex++), getMessage("report.assessment.comment", null, "Comment", locale));

				for (Assessment assessment : assessments) {
					row = (Tr) table.getContent().get(++rawLength);
					colIndex = 0;
					setCellText((Tc) row.getContent().get(colIndex++), assessment.getScenario().getName(), alignmentLeft);
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
				contents.add(table);
				contents.add(addTableCaption(getMessage("report.assessment.table.caption", new Object[] { asset.getName() },
						String.format("Risk estimation for the asset %s", asset.getName()), locale)));
				colIndex = 0;
			}
			insertAllAfter(paragraphOrigin, contents);
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
			insertBofore(paragraph, table);
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
		String parmetertype = "", languuage = locale.getLanguage().toUpperCase();
		if (type.equals(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME))
			parmetertype = "Impact";
		else if (type.equals(Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME))
			parmetertype = "Proba";

		P paragraph = findTableAnchor(parmetertype);
		if (paragraph != null) {
			setCurrentParagraphId(TS_TAB_TEXT_2);
			List<Object> contents = new LinkedList<>();
			if (parmetertype == "Proba")
				buildImpactProbabilityTable(contents, getMessage("report.parameter.title." + type.toLowerCase(), null, type, locale), parmetertype,
						analysis.getLikelihoodParameters());
			else {
				Map<ScaleType, List<ImpactParameter>> impacts = analysis.getImpactParameters().stream().filter(parameter -> !parameter.isMatch(Constant.DEFAULT_IMPACT_NAME))
						.collect(Collectors.groupingBy(ImpactParameter::getType));
				generateImpactList(impacts.keySet());
				for (ScaleType scaleType : impacts.keySet()) {
					Translation title = scaleType.get(languuage);
					buildImpactProbabilityTable(contents, title == null ? scaleType.getDisplayName() : title.getName(), parmetertype, impacts.get(scaleType));
				}
			}
			insertAllBefore(paragraph, contents);
		}

	}

	private void generateImpactList(Set<ScaleType> impacts) throws XPathBinderAssociationIsPartialException, JAXBException {
		P paragraph = findTableAnchor("ImpactList");
		if (paragraph == null || impacts.isEmpty())
			return;
		boolean isFirst = true;
		String style = "BulletL1";
		paragraph.getContent().removeIf(value -> value instanceof R);
		setStyle(paragraph, style);
		List<Object> contents = new ArrayList<>(impacts.size() - 1);
		for (ScaleType scaleType : impacts) {
			if (isFirst) {
				isFirst = false;
				setText(paragraph, scaleType.getTranslate(languageAlpha2));
			} else
				contents.add(setText(setStyle(factory.createP(), style), scaleType.getTranslate(languageAlpha2)));
		}
		insertAllBefore(paragraph, contents);
	}

	private void buildImpactProbabilityTable(List<Object> contents, String title, String type, List<? extends IBoundedParameter> parameters) {
		contents.add(setText(setStyle(factory.createP(), "TSEstimationTitle"), title));
		Tbl table = createTable("TableTS" + type, parameters.size() + 1, 3);
		setCurrentParagraphId(TS_TAB_TEXT_2);
		Tr row = (Tr) table.getContent().get(0);
		for (int i = 1; i < 3; i++)
			setColor((Tc) row.getContent().get(i), HEADER_COLOR);
		setCellText((Tc) row.getContent().get(0), getMessage("report.parameter.level", null, "Level", locale));
		setCellText((Tc) row.getContent().get(1), getMessage("report.parameter.label", null, "Label", locale));
		setCellText((Tc) row.getContent().get(2), getMessage("report.parameter.qualification", null, "Qualification", locale));
		for (IBoundedParameter parameter : parameters) {
			if (parameter.getLevel() == 0)
				continue;
			row = (Tr) table.getContent().get(parameter.getLevel());
			setCellText((Tc) row.getContent().get(0), "" + parameter.getLevel());
			setCellText((Tc) row.getContent().get(1), parameter.getLabel());
			setCellText((Tc) row.getContent().get(2), parameter.getDescription());
		}
		contents.add(table);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.exportation.AbstractWordExporter#writeChart(lu.
	 * itrust.business.TS.exportation.helper.PODocx4jExcelSheet)
	 */
	@Override
	protected void writeChart(Docx4jExcelSheet reportExcelSheet) throws Exception {
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

	private void generateRiskByAssetGraphic(Docx4jExcelSheet reportExcelSheet) {
		Map<String, List<Assessment>> assessmentByAsset = analysis.getAssessments().parallelStream().filter(Assessment::isSelected).sorted((a1, a2) -> {
			return NaturalOrderComparator.compareTo(a1.getAsset().getName(), a2.getAsset().getName());
		}).collect(Collectors.groupingBy(assessment -> assessment.getAsset().getName()));
		generateRiskGraphic(reportExcelSheet, assessmentByAsset);
	}

	private void generateRiskByAssetTypeGraphic(Docx4jExcelSheet reportExcelSheet) {
		Map<String, List<Assessment>> assessments = analysis.getAssessments().parallelStream().filter(Assessment::isSelected).sorted((a1, a2) -> {
			return NaturalOrderComparator.compareTo(getDisplayName(a1.getAsset().getAssetType()), getDisplayName(a2.getAsset().getAssetType()));
		}).collect(Collectors.groupingBy(assessment -> getDisplayName(assessment.getAsset().getAssetType())));
		generateRiskGraphic(reportExcelSheet, assessments);
	}

	private void generateRiskByScenarioGraphic(Docx4jExcelSheet reportExcelSheet) {
		Map<String, List<Assessment>> assessments = analysis.getAssessments().parallelStream().filter(Assessment::isSelected).sorted((a1, a2) -> {
			return NaturalOrderComparator.compareTo(a1.getScenario().getName(), a2.getScenario().getName());
		}).collect(Collectors.groupingBy(assessment -> assessment.getScenario().getName()));

		generateRiskGraphic(reportExcelSheet, assessments);
	}

	private void generateRiskByScenarioTypeGraphic(Docx4jExcelSheet reportExcelSheet) {
		Map<String, List<Assessment>> assessments = analysis.getAssessments().parallelStream().filter(Assessment::isSelected).sorted((a1, a2) -> {
			return NaturalOrderComparator.compareTo(getDisplayName(a1.getScenario().getType()), getDisplayName(a2.getScenario().getType()));
		}).collect(Collectors.groupingBy(assessment -> getDisplayName(assessment.getScenario().getType())));
		generateRiskGraphic(reportExcelSheet, assessments);
	}

	@Override
	protected void generateOtherData() throws XPathBinderAssociationIsPartialException, JAXBException {
		serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.table.risk_heat.map", "Printing risk heat map", increase(3)));
		generateRiskHeatMap();

		serviceTaskFeedback.send(idTask, new MessageHandler("info.printing.table.risk_acceptance", "Printing risk acceptance table", increase(2)));
		generateRiskAcceptance();
	}

	private void generateRiskAcceptance() throws XPathBinderAssociationIsPartialException, JAXBException {
		P paragraph = findTableAnchor("RiskAcceptance");
		if (paragraph != null) {
			Tbl table = createTable("TableTSRiskAcceptance", analysis.getRiskAcceptanceParameters().size() + 1, 2);
			setCurrentParagraphId(TS_TAB_TEXT_2);
			// set header
			Tr row = (Tr) table.getContent().get(0);
			TextAlignment alignmentCenter = createAlignment("center");
			// set header
			setCellText((Tc) row.getContent().get(0), getMessage("report.risk_acceptance.title.level", null, "Risk level", locale));
			setCellText((Tc) row.getContent().get(1), getMessage("report.risk_acceptance.title.acceptance.criteria", null, "Risk acceptance criteria", locale));
			int index = 1;
			for (RiskAcceptanceParameter parameter : analysis.getRiskAcceptanceParameters()) {
				row = (Tr) table.getContent().get(index++);
				Tc cell = (Tc) row.getContent().get(0);
				addCellParagraph(cell, parameter.getLabel());
				setAlignment(cell, alignmentCenter);
				addCellParagraph(cell, getMessage("report.risk_acceptance.importance_threshold.value", new Object[] { parameter.getValue().intValue() },
						"Importance threshold: " + parameter.getValue().intValue(), locale), true);
				addCellParagraph((Tc) row.getContent().get(1), parameter.getDescription());
				if (!parameter.getColor().isEmpty())
					setColor(cell, parameter.getColor().substring(1));
			}
			insertBofore(paragraph, table);
		}
	}

	private void generateRiskHeatMap() throws XPathBinderAssociationIsPartialException, JAXBException {
		Chart chart = ChartGenerator.generateRiskHeatMap(analysis, valueFactory);
		generateRiskHeatMap(chart, "RiskHeatMapSummary");
		generateRiskHeatMap(chart, "RiskHeatMap");
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
	private void generateRiskHeatMap(Chart chart, String anchor) throws XPathBinderAssociationIsPartialException, JAXBException {
		P paragraphOriginal = findTableAnchor(anchor);
		if (paragraphOriginal != null) {
			setCurrentParagraphId(TS_TAB_TEXT_2);
			Tbl legends = createTable("TableTSHeatMapLegend", 1, chart.getLegends().size());
			TextAlignment alignmentCenter = createAlignment("center");
			Tr legendRow = (Tr) legends.getContent().get(0);
			int index[] = { 0 };
			chart.getLegends().forEach(legend -> {
				Tc column = (Tc) legendRow.getContent().get(index[0]++);
				setCellText(column, legend.getLabel(), alignmentCenter);
				setColor(column, legend.getColor().substring(1));
			});

			CTVerticalJc verticalJc = createVerticalAlignment(STVerticalJc.CENTER);
			insertBofore(paragraphOriginal, legends);
			insertBofore(paragraphOriginal, setStyle(factory.createP(), "Endlist"));
			Tbl table = createTable("TableTSRiskHeatMap", chart.getLabels().size() + 2, chart.getLabels().size() + 2);
			// set header
			Tr row = (Tr) table.getContent().get(0);
			setCellText((Tc) row.getContent().get(0), getMessage("report.risk_heat_map.title.impact", null, "Impact", locale));
			int rowIndex = 1;
			for (int i = 0; i < chart.getDatasets().size(); i++) {
				Dataset<List<String>> dataset = (Dataset<List<String>>) chart.getDatasets().get(i);
				if (i > 0)
					row = (Tr) table.getContent().get(rowIndex++);
				Tc cell = (Tc) row.getContent().get(1);
				setVerticalAlignment(cell, verticalJc);
				setAlignment(addCellParagraph(cell, dataset.getLabel()), alignmentCenter);
				setColor(cell, LIGHT_CELL_COLOR);
				for (int j = 0; j < dataset.getData().size(); j++) {
					cell = (Tc) row.getContent().get(j + 2);
					setVerticalAlignment(cell, verticalJc);
					Object data = dataset.getData().get(j);
					if (data instanceof Integer)
						setAlignment(addCellParagraph(cell, data.toString()), alignmentCenter);
					else
						setAlignment(addCellParagraph(cell, ""), alignmentCenter);
					setColor(cell, dataset.getBackgroundColor().get(j).substring(1));
				}
			}
			row = (Tr) table.getContent().get(rowIndex++);
			for (int i = 0; i < chart.getLabels().size(); i++) {
				Tc cell = (Tc) row.getContent().get(i + 2);
				setVerticalAlignment(cell, verticalJc);
				setAlignment(addCellParagraph(cell, chart.getLabels().get(i)), alignmentCenter);
				setColor(cell, LIGHT_CELL_COLOR);
			}
			row = (Tr) table.getContent().get(rowIndex);
			setCellText((Tc) row.getContent().get(0), getMessage("report.risk_heat_map.title.probability", null, "Probability", locale), alignmentCenter);
			insertBofore(paragraphOriginal, table);
		}

	}

	private void generateRiskGraphic(Docx4jExcelSheet reportExcelSheet, Map<String, List<Assessment>> assessmentByAsset) {
		XSSFSheet xssfSheet = reportExcelSheet.getWorkbook().getSheetAt(0);
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

	private void clearColorBoundCount() {
		colorBounds.parallelStream().forEach(color -> color.setCount(0));
	}

	@Override
	protected AnalysisType getType() {
		return AnalysisType.QUALITATIVE;
	}

	@Override
	protected ActionPlanMode getActionPlanType() {
		return ActionPlanMode.APQ;
	}

}
