/**
 * 
 */
package lu.itrust.business.TS.exportation.word.impl.docx4j.builder.chain;

import static lu.itrust.business.TS.exportation.word.ExportReport.TS_TAB_TEXT_2;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.Docx4jReportImpl.setColor;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.PPrBase.TextAlignment;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;
import org.springframework.util.StringUtils;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.exportation.word.IDocxBuilder;
import lu.itrust.business.TS.exportation.word.impl.docx4j.Docx4jReportImpl;
import lu.itrust.business.TS.exportation.word.impl.docx4j.DocxChainFactory;
import lu.itrust.business.TS.exportation.word.impl.docx4j.builder.Docx4jBuilder;
import lu.itrust.business.TS.exportation.word.impl.docx4j.builder.Docx4jData;
import lu.itrust.business.TS.helper.NaturalOrderComparator;
import lu.itrust.business.TS.model.analysis.AnalysisSetting;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.parameter.IBoundedParameter;
import lu.itrust.business.TS.model.parameter.impl.ImpactParameter;
import lu.itrust.business.TS.model.scale.ScaleType;

/**
 * @author eomar
 *
 */
public class Docx4jProbaImpactBuilder extends Docx4jBuilder {

	private static final String TS_QT_IMPACTLIST = "ts_qt_impactlist";
	private static final String TS_QL_IMPACTLIST = "ts_ql_impactlist";
	private static final String TS_QL_IMPACT = "ts_ql_impact";
	private static final String TS_QT_IMPACT = "ts_qt_impact";
	private static final String TS_QL_PROBA = "ts_ql_proba";
	private static final String TS_QT_PROBA = "ts_qt_proba";
	private static final String TYPE_PROBA = "Proba";
	private static final String TYPE_IMPACT = "Impact";

	/**
	 * @param next
	 * @param supports
	 */
	public Docx4jProbaImpactBuilder(IDocxBuilder next) {
		super(next, TS_QT_PROBA, TS_QL_PROBA, TS_QT_IMPACT, TS_QL_IMPACT, TS_QL_IMPACTLIST, TS_QT_IMPACTLIST);
	}

	protected void buildQuantitative(final String type, final List<IBoundedParameter> parameters, final Docx4jReportImpl exporter, final P paragraph,
			final DecimalFormat decimalFormat) {
		final Tbl table = exporter.createTable("TableTS" + type, parameters.size() + 1, 6);
		final TextAlignment alignmentCenter = exporter.createAlignment("center");
		final List<Object> contents = new LinkedList<>();
		final Tr header = (Tr) table.getContent().get(0);
		final int length = parameters.size() - 1;

		exporter.setCurrentParagraphId(TS_TAB_TEXT_2);
		exporter.setCellText((Tc) header.getContent().get(0), exporter.getMessage("report.parameter.level", null, "Level"));
		exporter.setCellText((Tc) header.getContent().get(1), exporter.getMessage("report.parameter.acronym", null, "Acro"));
		exporter.setCellText((Tc) header.getContent().get(2), exporter.getMessage("report.parameter.qualification", null, "Qualification"));
		if (type.equalsIgnoreCase(TYPE_PROBA))
			exporter.setCellText((Tc) header.getContent().get(3), exporter.getMessage("report.parameter.proba.value", null, "Value (/y)"));
		else
			exporter.setCellText((Tc) header.getContent().get(3), exporter.getMessage("report.parameter.value", null, "Value (k€/y)"));
		exporter.setCellText((Tc) header.getContent().get(4), exporter.getMessage("report.parameter.value.from", null, "Value From"));
		exporter.setCellText((Tc) header.getContent().get(5), exporter.getMessage("report.parameter.value.to", null, "Value To"));
		exporter.setRepeatHeader(header);

		int countrow = 0;
		// set data
		for (IBoundedParameter parameter : parameters) {
			final Tr row = (Tr) table.getContent().get(countrow + 1);
			exporter.setCellText((Tc) row.getContent().get(0), "" + parameter.getLevel(), alignmentCenter);
			exporter.setCellText((Tc) row.getContent().get(1), parameter.getAcronym(), alignmentCenter);
			exporter.setCellText((Tc) row.getContent().get(2), parameter.getDescription());
			setColor((Tc) row.getContent().get(2), exporter.getDefaultColor());
			if (type.equalsIgnoreCase(TYPE_IMPACT))
				exporter.addCellNumber((Tc) row.getContent().get(3), decimalFormat.format(parameter.getValue() * 0.001));
			else
				exporter.addCellNumber((Tc) row.getContent().get(3),
						parameter.getValue() >= 1 ? exporter.getKiloNumberFormat().format(parameter.getValue()) : decimalFormat.format(parameter.getValue()));

			if (countrow % 2 == 0)
				setColor((Tc) row.getContent().get(3), exporter.getDefaultColor());

			if (type.equals(TYPE_IMPACT))
				exporter.addCellNumber((Tc) row.getContent().get(4), decimalFormat.format(parameter.getBounds().getFrom() * 0.001));
			else
				exporter.addCellNumber((Tc) row.getContent().get(4), parameter.getBounds().getFrom() >= 1 ? exporter.getKiloNumberFormat().format(parameter.getBounds().getFrom())
						: decimalFormat.format(parameter.getBounds().getFrom()));

			if (parameter.getLevel() == length)
				exporter.addCellNumber((Tc) row.getContent().get(5), "+∞");
			else {
				if (type.equals(TYPE_IMPACT))
					exporter.addCellNumber((Tc) row.getContent().get(5), decimalFormat.format(parameter.getBounds().getTo() * 0.001));
				else
					exporter.addCellNumber((Tc) row.getContent().get(5), parameter.getBounds().getTo() >= 1 ? exporter.getKiloNumberFormat().format(parameter.getBounds().getTo())
							: decimalFormat.format(parameter.getBounds().getTo()));
			}
			countrow++;
		}
		contents.add(
				exporter.setText(exporter.setStyle(exporter.getFactory().createP(), "TSEstimationTitle"), exporter.getMessage("report.parameter.title." + type.toLowerCase())));
		contents.add(table);
		if (exporter.insertAllBefore(paragraph, contents))
			DocxChainFactory.format(table, exporter.getDefaultTableStyle(), AnalysisType.QUANTITATIVE);
	}

	@Override
	protected boolean internalBuild(Docx4jData data) {
		switch (data.getAnchor()) {
		case TS_QL_IMPACT:
			return buildQualitative(data, TYPE_IMPACT);
		case TS_QT_IMPACT:
			return buildQuantitative(data, TYPE_IMPACT);
		case TS_QL_IMPACTLIST:
			return buildQualitativeImpactList(data);
		case TS_QT_IMPACTLIST:
			return buildQuantitativeImpactList(data);
		case TS_QL_PROBA:
			return buildQualitative(data, TYPE_PROBA);
		case TS_QT_PROBA:
			return buildQuantitative(data, TYPE_PROBA);
		default:
			return true;
		}
	}

	private boolean buildQualitative(Docx4jData data, String type) {
		final Docx4jReportImpl exporter = (Docx4jReportImpl) data.getExportor();
		final P paragraph = exporter.findP(data.getSource());
		if (paragraph != null) {
			final List<Object> contents = new LinkedList<>();
			exporter.setCurrentParagraphId(TS_TAB_TEXT_2);
			if (type.equalsIgnoreCase(TYPE_PROBA))
				buildQualitative(exporter, type, exporter.getMessage("report.parameter.title." + type.toLowerCase(), null, type), contents,
						exporter.getAnalysis().getLikelihoodParameters());
			else {
				final String language = exporter.getLocale().getLanguage();
				final Map<ScaleType, List<ImpactParameter>> impacts = exporter.getAnalysis().getImpactParameters().stream()
						.filter(p -> !p.getTypeName().equalsIgnoreCase(Constant.DEFAULT_IMPACT_NAME)).sorted(impactComparator(language))
						.collect(Collectors.groupingBy(ImpactParameter::getType, LinkedHashMap::new, Collectors.toList()));
				impacts.forEach((scale, parameters) -> buildQualitative(exporter, type, scale.getTranslate(language), contents, parameters));
			}
			if (exporter.insertAllBefore(paragraph, contents))
				contents.parallelStream().filter(t -> (t instanceof Tbl)).forEach(t -> DocxChainFactory.format(t, exporter.getDefaultTableStyle(), AnalysisType.QUALITATIVE));
		}
		return true;
	}

	private void buildQualitative(final Docx4jReportImpl exporter, final String type, final String title, List<Object> contents,
			final List<? extends IBoundedParameter> parameters) {
		final Tbl table = exporter.createTable("TableTS" + type, parameters.size(), 3);
		final TextAlignment alignment = exporter.createAlignment("center");
		final Tr header = (Tr) table.getContent().get(0);
		exporter.setCurrentParagraphId(TS_TAB_TEXT_2);
		exporter.setCellText((Tc) header.getContent().get(0), exporter.getMessage("report.parameter.level", null, "Level"));
		exporter.setCellText((Tc) header.getContent().get(1), exporter.getMessage("report.parameter.label", null, "Label"));
		exporter.setCellText((Tc) header.getContent().get(2), exporter.getMessage("report.parameter.qualification", null, "Qualification"));
		for (IBoundedParameter parameter : parameters) {
			if (parameter.getLevel() == 0)
				continue;
			final Tr row = (Tr) table.getContent().get(parameter.getLevel());
			exporter.setCellText((Tc) row.getContent().get(0), "" + parameter.getLevel(), alignment);
			exporter.setCellText((Tc) row.getContent().get(1), parameter.getLabel());
			exporter.setCellText((Tc) row.getContent().get(2), parameter.getDescription());
		}
		contents.add(exporter.setText(exporter.setStyle(exporter.getFactory().createP(), "TSEstimationTitle"), title));
		contents.add(table);
	}

	private boolean buildQualitativeImpactList(Docx4jData data) {
		final Docx4jReportImpl exporter = (Docx4jReportImpl) data.getExportor();
		final P paragraph = exporter.findP(data.getSource());
		if (paragraph != null)
			buildImpactList(exporter, exporter.getAnalysis().getImpactParameters().stream().sorted(impactComparator(exporter.getLocale().getLanguage()))
					.map(ImpactParameter::getType).distinct().filter(e -> !e.getName().equalsIgnoreCase(Constant.DEFAULT_IMPACT_NAME)).collect(Collectors.toList()), paragraph);
		return true;
	}

	@SuppressWarnings("unchecked")
	private boolean buildQuantitative(Docx4jData data, String type) {
		final Docx4jReportImpl exporter = (Docx4jReportImpl) data.getExportor();
		final P paragraph = exporter.findP(data.getSource());
		if (paragraph != null) {
			buildQuantitative(type, (List<IBoundedParameter>) exporter.getAnalysis().findParametersByType(type), exporter, paragraph, createByType(exporter, type));
			if (type.equalsIgnoreCase(TYPE_IMPACT) && (boolean) exporter.getAnalysis().findSetting(AnalysisSetting.ALLOW_QUALITATIVE_IN_QUANTITATIVE_REPORT)) {
				final List<Object> contents = new LinkedList<Object>();
				final String language = exporter.getLocale().getLanguage();
				final Map<ScaleType, List<ImpactParameter>> impacts = exporter.getAnalysis().getImpactParameters().stream()
						.filter(p -> !p.getTypeName().equalsIgnoreCase(Constant.DEFAULT_IMPACT_NAME)).sorted(impactComparator(language))
						.collect(Collectors.groupingBy(ImpactParameter::getType, LinkedHashMap::new, Collectors.toList()));

				impacts.forEach((scale, parameters) -> {
					final String name = scale.getTranslate(language);
					final String captionName = exporter.getMessage("report.impact_scale.table.caption", new Object[] { name }, String.format("%s impact scale", name));
					buildQualitative(exporter, type, name, contents, parameters);
					contents.add(exporter.addTableCaption(StringUtils.capitalize(captionName.toLowerCase())));
				});

				if (exporter.insertAllAfter(paragraph, contents))
					contents.parallelStream().filter(t -> (t instanceof Tbl)).forEach(t -> DocxChainFactory.format(t, exporter.getDefaultTableStyle(), AnalysisType.QUALITATIVE));
			}

		}
		return true;
	}

	private boolean buildQuantitativeImpactList(Docx4jData data) {
		final Docx4jReportImpl exporter = (Docx4jReportImpl) data.getExportor();
		final P paragraph = exporter.findP(data.getSource());
		if (paragraph != null)
			buildImpactList(exporter, exporter.getAnalysis().getImpactParameters().stream().sorted(impactComparator(exporter.getLocale().getLanguage()))
					.map(ImpactParameter::getType).distinct().collect(Collectors.toList()), paragraph);
		return true;
	}

	private void buildImpactList(final Docx4jReportImpl exporter, List<ScaleType> impacts, final P paragraph) {
		final String style = "BulletL1";
		final String language = exporter.getLocale().getLanguage();
		final List<Object> contents = new ArrayList<>(impacts.size() - 1);
		final AtomicBoolean isFirst = new AtomicBoolean(true);
		paragraph.getContent().removeIf(value -> value instanceof R);
		exporter.setStyle(paragraph, style);
		impacts.forEach(type -> {
			String value = type.getTranslate(language);
			if (isFirst.getAndSet(false)) {
				exporter.setText(paragraph, exporter.getMessage("report.format.bullet.list.iteam", new Object[] { value }, value));
			} else
				contents.add(exporter.setText(exporter.setStyle(exporter.getFactory().createP(), style),
						exporter.getMessage("report.format.bullet.list.iteam", new Object[] { value }, value)));
		});
		exporter.insertAllBefore(paragraph, contents);
	}

	private DecimalFormat createByType(Docx4jReportImpl exporter, String type) {
		if (type.equalsIgnoreCase(TYPE_IMPACT))
			return exporter.getKiloNumberFormat();
		final DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.FRANCE);
		decimalFormat.setMaximumFractionDigits(2);
		return decimalFormat;
	}

	private Comparator<? super ImpactParameter> impactComparator(final String language) {
		return (p1, p2) -> {
			final int comp = NaturalOrderComparator.compareTo(p1.getType().getTranslate(language), p2.getType().getTranslate(language));
			return comp == 0 ? Integer.compare(p1.getLevel(), p2.getLevel()) : comp;
		};
	}
}
