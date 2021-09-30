/**
 * 
 */
package lu.itrust.business.TS.exportation.word.impl.docx4j.builder.chain;

import static lu.itrust.business.TS.exportation.word.ExportReport.TS_TAB_TEXT_2;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.docx4j.wml.P;
import org.docx4j.wml.PPrBase.TextAlignment;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;

import lu.itrust.business.TS.component.AssessmentAndRiskProfileManager;
import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.exportation.word.IDocxBuilder;
import lu.itrust.business.TS.exportation.word.impl.docx4j.Docx4jReportImpl;
import lu.itrust.business.TS.exportation.word.impl.docx4j.DocxChainFactory;
import lu.itrust.business.TS.exportation.word.impl.docx4j.builder.Docx4jBuilder;
import lu.itrust.business.TS.exportation.word.impl.docx4j.builder.Docx4jData;
import lu.itrust.business.TS.helper.NaturalOrderComparator;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.AnalysisSetting;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.assessment.helper.ALE;
import lu.itrust.business.TS.model.assessment.helper.AssetComparatorByALE;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.parameter.impl.ImpactParameter;
import lu.itrust.business.TS.model.parameter.value.IValue;
import lu.itrust.business.TS.model.parameter.value.impl.FormulaValue;
import lu.itrust.business.TS.model.scale.ScaleType;

/**
 * @author eomar
 *
 */
public class Docx4jAssessmentBuilder extends Docx4jBuilder {

	private static final String TS_QL_ASSESSMENT = "ts_ql_assessment";
	private static final String TS_QT_ASSESSMENT = "ts_qt_assessment";

	/**
	 * @param next
	 * @param supports
	 */
	public Docx4jAssessmentBuilder(IDocxBuilder next) {
		super(next, TS_QT_ASSESSMENT, TS_QL_ASSESSMENT);
	}

	@Override
	protected boolean internalBuild(Docx4jData data) {
		switch (data.getAnchor()) {
			case TS_QL_ASSESSMENT:
				return buildQualitative(data);
			case TS_QT_ASSESSMENT:
				return buildQuantitative(data);
			default:
				return true;
		}

	}

	private boolean buildQualitative(Docx4jData data) {
		final Docx4jReportImpl exporter = (Docx4jReportImpl) data.getExportor();
		final P paragraphOrigin = exporter.findP(data.getSource());

		if (paragraphOrigin != null) {
			final Analysis analysis = exporter.getAnalysis();
			final List<Object> contents = new LinkedList<>();
			final String language = exporter.getLocale().getLanguage();
			final TextAlignment alignmentLeft = exporter.createAlignment("left");
			final TextAlignment alignmentCenter = exporter.createAlignment("center");
			final Map<Asset, List<Assessment>> assessementsByAsset = analysis.findSelectedAssessmentByAsset();
			final List<ScaleType> scaleTypes = analysis.getImpactParameters().stream().map(p -> p.getType()).distinct()
					.filter(p -> !p.getName().equals(Constant.DEFAULT_IMPACT_NAME))
					.sorted((s1, s2) -> NaturalOrderComparator.compareTo(s1.getShortName(language),
							s2.getShortName(language)))
					.collect(Collectors.toList());

			final int colLength = 4 + scaleTypes.size();

			assessementsByAsset.keySet().forEach(asset -> {
				final List<Assessment> assessments = assessementsByAsset.get(asset);
				final Tbl table = exporter.createTable("TableTSAssessment", assessments.size() + 1, colLength);
				int hColIndex = 0, rawLength = 0;
				final Tr header = (Tr) table.getContent().get(hColIndex);
				exporter.setRepeatHeader(header);
				exporter.setCellText((Tc) header.getContent().get(hColIndex++),
						exporter.getMessage("report.assessment.scenarios", null, "Scenarios"), alignmentLeft);
				for (ScaleType scaleType : scaleTypes)
					exporter.setCellText((Tc) header.getContent().get(hColIndex++), scaleType.getShortName(language),
							alignmentCenter);

				exporter.setCellText((Tc) header.getContent().get(hColIndex++),
						exporter.getMessage("report.assessment.probability", null, "P."), alignmentCenter);
				exporter.setCellText((Tc) header.getContent().get(hColIndex++),
						exporter.getMessage("report.assessment.owner", null, "Owner"));
				exporter.setCellText((Tc) header.getContent().get(hColIndex++),
						exporter.getMessage("report.assessment.comment", null, "Comment"));

				for (Assessment assessment : assessments) {
					final Tr row = (Tr) table.getContent().get(++rawLength);
					int colIndex = 0;
					exporter.setCellText((Tc) row.getContent().get(colIndex++), assessment.getScenario().getName(),
							alignmentLeft);
					for (ScaleType scaleType : scaleTypes) {
						final IValue impact = assessment.getImpact(scaleType.getName());
						exporter.setCellText((Tc) row.getContent().get(colIndex++),
								impact == null || impact.getLevel() == 0
										? exporter.getMessage("label.status.na", null, "na")
										: impact.getLevel() + "",
								alignmentCenter);
					}
					final int probaLevel = assessment.getLikelihood() == null ? 0
							: assessment.getLikelihood().getLevel();
					exporter.setCellText((Tc) row.getContent().get(colIndex++),
							probaLevel == 0 ? exporter.getMessage("label.status.na", null, "na") : probaLevel + "",
							alignmentCenter);
					exporter.addCellParagraph((Tc) row.getContent().get(colIndex++), assessment.getOwner());
					exporter.addCellParagraph((Tc) row.getContent().get(colIndex++), assessment.getComment());
				}
				final P paragraph = exporter.getFactory().createP();
				exporter.setText(paragraph, asset.getName());
				exporter.setStyle(paragraph, "TSEstimationTitle");
				contents.add(paragraph);
				contents.add(table);
				contents.add(exporter.addTableCaption(
						exporter.getMessage("report.assessment.table.caption", new Object[] { asset.getName() },
								String.format("Risk estimation for the asset %s", asset.getName()))));
			});
			if (exporter.insertAllAfter(paragraphOrigin, contents))
				contents.parallelStream().filter(t -> (t instanceof Tbl)).forEach(t -> DocxChainFactory.format(t,
						exporter.getDefaultTableStyle(), AnalysisType.QUALITATIVE, exporter.getColors()));
			assessementsByAsset.clear();
		}
		return true;
	}

	private boolean buildQuantitative(Docx4jData data) {
		final Docx4jReportImpl exporter = (Docx4jReportImpl) data.getExportor();
		final P paragraphOrigin = exporter.findP(data.getSource());
		if (paragraphOrigin != null) {
			final List<Object> contents = new LinkedList<>();
			final Analysis analysis = exporter.getAnalysis();
			final Map<String, ALE> alesmap = new LinkedHashMap<String, ALE>();
			final TextAlignment alignmentLeft = exporter.createAlignment("left");
			final TextAlignment alignmentCenter = exporter.createAlignment("center");
			final List<Assessment> assessments = analysis.findSelectedAssessments();
			final double totalale = assessments.stream().mapToDouble(Assessment::getALE).sum();
			final DecimalFormat assessmentFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.FRANCE);
			final Map<String, List<Assessment>> assessementsmap = new LinkedHashMap<String, List<Assessment>>();
			final boolean mixted = analysis.isQualitative()
					&& (boolean) analysis.findSetting(AnalysisSetting.ALLOW_QUALITATIVE_IN_QUANTITATIVE_REPORT);
			final List<ScaleType> scaleTypes = analysis.getImpactParameters().stream()
					.filter(p -> mixted && !p.getTypeName().equals(Constant.DEFAULT_IMPACT_NAME))
					.map(ImpactParameter::getType).distinct().collect(Collectors.toList());
			final P paraph = exporter.setStyle(exporter.getFactory().createP(), "TSAssessmentTotalALE");
			exporter.setCurrentParagraphId(TS_TAB_TEXT_2);
			exporter.setText(paraph,
					exporter.getMessage("report.assessment.total_ale.assets", null, "Total ALE for all assets"));
			exporter.addTab(paraph);
			exporter.addText(paraph, String.format("%s k€", exporter.getKiloNumberFormat().format(totalale * 0.001)));
			contents.add(paraph);
			assessmentFormat.setMinimumFractionDigits(1);
			assessmentFormat.setMaximumFractionDigits(1);

			assessments.sort((a1, a2) -> {
				int result = NaturalOrderComparator.compareTo(a1.getAsset().getName(), a2.getAsset().getName());
				if (result == 0)
					result = NaturalOrderComparator.compareTo(a1.getScenario().getName(), a2.getScenario().getName());
				return result;
			});

			AssessmentAndRiskProfileManager.SplitAssessment(assessments, alesmap, assessementsmap);
			alesmap.values().stream().sorted(new AssetComparatorByALE()).forEach(ale -> {
				final List<Assessment> assessmentsofasset = assessementsmap.get(ale.getAssetName());
				final Tbl table = exporter.createTable("TableTSAssessment", assessmentsofasset.size() + 1,
						6 + scaleTypes.size());
				P paragraph = exporter.getFactory().createP();
				exporter.setText(paragraph, ale.getAssetName());
				exporter.setStyle(paragraph, "TSEstimationTitle");
				contents.add(paragraph);
				paragraph = exporter.getFactory().createP();
				exporter.setText(paragraph,
						exporter.getMessage("report.assessment.total.ale.for.asset", null, "Total ALE of asset"));
				exporter.addTab(paragraph);
				exporter.setText(paragraph,
						String.format("%s k€", exporter.getKiloNumberFormat().format(ale.getValue() * 0.001)), true);
				exporter.setStyle(paragraph, "TSAssessmentTotalALE");
				contents.add(paragraph);
				int rowIndex = 0, colIndex = 0;
				Tr row = (Tr) table.getContent().get(rowIndex++);
				exporter.setCellText((Tc) row.getContent().get(colIndex++),
						exporter.getMessage("report.assessment.scenarios", null, "Scenarios"), alignmentLeft);
				exporter.setCellText((Tc) row.getContent().get(colIndex++),
						exporter.getMessage("report.assessment.impact.financial", null, "Fin.(k€/y)"), alignmentCenter);
				for (ScaleType impact : scaleTypes)
					exporter.setCellText((Tc) row.getContent().get(colIndex++),
							impact.getShortName(exporter.getLocale().getLanguage()), alignmentCenter);
				exporter.setCellText((Tc) row.getContent().get(colIndex++),
						exporter.getMessage("report.assessment.probability", null, "P."), alignmentCenter);
				exporter.setCellText((Tc) row.getContent().get(colIndex++),
						exporter.getMessage("report.assessment.ale", null, "ALE(k€/y)"));
				exporter.setCellText((Tc) row.getContent().get(colIndex++),
						exporter.getMessage("report.assessment.owner", null, "Owner"));
				exporter.setCellText((Tc) row.getContent().get(colIndex++),
						exporter.getMessage("report.assessment.comment", null, "Comment"));

				for (Assessment assessment : assessmentsofasset) {
					colIndex = 0;
					row = (Tr) table.getContent().get(rowIndex++);
					exporter.setCellText((Tc) row.getContent().get(colIndex++), assessment.getScenario().getName(),
							alignmentLeft);
					exporter.addCellNumber((Tc) row.getContent().get(colIndex++), exporter.getKiloNumberFormat()
							.format(assessment.getImpactValue(Constant.DEFAULT_IMPACT_NAME) * 0.001));
					for (ScaleType type : scaleTypes) {
						final IValue value = assessment.getImpact(type.getName());
						exporter.setCellText((Tc) row.getContent().get(colIndex++),
								value == null || value.getLevel() == 0
										? exporter.getMessage("label.status.na", null, "na")
										: value.getLevel().toString(),
								alignmentCenter);
					}

					final Object likelihood = assessment.getLikelihood() == null ? null
							: assessment.getLikelihood() instanceof FormulaValue
									? String.format("%s (p%d)",
											exporter.getKiloNumberFormat().format(assessment.getLikelihood().getReal()),
											assessment.getLikelihood().getLevel())
									: assessment.getLikelihood().getRaw();

					exporter.setCellText((Tc) row.getContent().get(colIndex++),
							exporter.formatLikelihood(assessment.getLikelihood() == null
									? exporter.getMessage("label.status.na", null, "na")
									: likelihood),
							alignmentCenter);
					exporter.addCellNumber((Tc) row.getContent().get(colIndex++),
							assessment.getALE() == 0
									? exporter.getKiloNumberFormat().format(assessment.getALE() * 0.001)
									: assessmentFormat.format(assessment.getALE() * 0.001));
					exporter.addCellParagraph((Tc) row.getContent().get(colIndex++), assessment.getOwner());
					exporter.addCellParagraph((Tc) row.getContent().get(colIndex++), assessment.getComment());
				}
				contents.add(table);
				contents.add(exporter.addTableCaption(
						exporter.getMessage("report.assessment.table.caption", new Object[] { ale.getAssetName() },
								String.format("Risk estimation for the asset %s", ale.getAssetName()))));
			});
			if (exporter.insertAllAfter(paragraphOrigin, contents))
				contents.parallelStream().filter(t -> (t instanceof Tbl)).forEach(t -> DocxChainFactory.format(t,
						exporter.getDefaultTableStyle(), AnalysisType.QUANTITATIVE, exporter.getColors()));
			assessementsmap.clear();
			contents.clear();
		}
		return true;
	}

}
