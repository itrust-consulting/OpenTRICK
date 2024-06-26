package lu.itrust.business.ts.model.general.helper;

import static lu.itrust.business.ts.constants.Constant.PARAMETERTYPE_TYPE_SINGLE_NAME;
import static lu.itrust.business.ts.constants.Constant.SOA_THRESHOLD;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.general.Phase;
import lu.itrust.business.ts.model.parameter.helper.ValueFactory;
import lu.itrust.business.ts.model.standard.AnalysisStandard;
import lu.itrust.business.ts.model.standard.measure.Measure;

/**
 * The PhaseManager class provides utility methods for updating statistics and computing totals for phases in an analysis.
 */
public final class PhaseManager {

	/**
	 * Updates the statistics for the given analysis.
	 *
	 * @param analysis The analysis object for which the statistics need to be updated.
	 */
	public static void updateStatistics(Analysis analysis) {
		final ValueFactory factory = new ValueFactory(analysis.getParameters());
		final double soaThreshold = analysis.findParameter(PARAMETERTYPE_TYPE_SINGLE_NAME, SOA_THRESHOLD, 100.0);
		updateStatistics(analysis.getAnalysisStandards().values().stream().map(AnalysisStandard::getMeasures).collect(Collectors.toList()), factory, soaThreshold);
	}

	/**
	 * Updates the statistics for a collection of measures.
	 *
	 * @param measures     the collection of measures
	 * @param valueFactory the value factory used for computing values
	 * @param soaThreshold the threshold for compliance rate
	 */
	public static void updateStatistics(Collection<List<Measure>> measures, ValueFactory valueFactory, final double soaThreshold) {
		measures.stream().flatMap(c -> c.stream()).collect(Collectors.groupingBy(Measure::getPhase)).entrySet().stream().forEach(e -> {
			e.getKey().setRemovable(false);
			e.getValue().stream().filter(m -> !m.getStatus().equals(Constant.MEASURE_STATUS_NOT_APPLICABLE) && m.getMeasureDescription().isComputable()).forEach(m -> {
				double implementationRate = m.getImplementationRateValue(valueFactory);
				e.getKey().setMeasureCount(e.getKey().getMeasureCount() + 1);
				e.getKey().setInvestment(e.getKey().getInvestment() + m.getInvestment());
				e.getKey().setExternalWorkload(e.getKey().getExternalWorkload() + m.getExternalWL());
				e.getKey().setInternalWorkload(e.getKey().getInternalWorkload() + m.getInternalWL());
				if (implementationRate >= soaThreshold)
					e.getKey().setComplianceCount(e.getKey().getComplianceCount() + 1);
				if (implementationRate >= 100) {
					e.getKey().setImplementedMeasureCount(e.getKey().getImplementedMeasureCount() + 1);
					e.getKey().setImplementedExternalWorkload(e.getKey().getImplementedExternalWorkload() + m.getExternalWL());
					e.getKey().setImplementedInternalWorkload(e.getKey().getImplementedInternalWorkload() + m.getInternalWL());
				}
			});

		});

	}

	/**
	 * Represents a phase in a project.
	 */
	public static Phase computeTotal(List<Phase> phases) {
		final Phase total = new Phase(phases.size());
		for (Phase phase : phases) {
			if (total.getBeginDate() == null || total.getBeginDate().after(phase.getBeginDate()))
				total.setBeginDate(phase.getBeginDate());
			if (total.getEndDate() == null || total.getEndDate().before(phase.getEndDate()))
				total.setEndDate(phase.getEndDate());
			total.setInvestment(total.getInvestment() + phase.getInvestment());
			total.setMeasureCount(total.getMeasureCount() + phase.getMeasureCount());
			total.setComplianceCount(total.getComplianceCount() + phase.getComplianceCount());
			total.setExternalWorkload(total.getExternalWorkload() + phase.getExternalWorkload());
			total.setInternalWorkload(total.getInternalWorkload() + phase.getInternalWorkload());
			if (phase.getNumber() > total.getNumber())
				total.setNumber(phase.getNumber());
			total.setImplementedMeasureCount(total.getImplementedMeasureCount() + phase.getImplementedMeasureCount());
			total.setImplementedExternalWorkload(total.getImplementedExternalWorkload() + phase.getImplementedExternalWorkload());
			total.setImplementedInternalWorkload(total.getImplementedInternalWorkload() + phase.getImplementedInternalWorkload());
		}
		return total;
	}

}
