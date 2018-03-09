package lu.itrust.business.TS.model.general.helper;

import static lu.itrust.business.TS.constants.Constant.PARAMETERTYPE_TYPE_SINGLE_NAME;
import static lu.itrust.business.TS.constants.Constant.SOA_THRESHOLD;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.general.Phase;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.measure.Measure;

public final class PhaseManager {

	public static void updateStatistics(Analysis analysis) {
		final ValueFactory factory = new ValueFactory(analysis.getParameters());
		final double soaThreshold = analysis.getParameter(PARAMETERTYPE_TYPE_SINGLE_NAME, SOA_THRESHOLD, 100.0);
		updateStatistics(analysis.getAnalysisStandards().stream().map(AnalysisStandard::getMeasures).collect(Collectors.toList()), factory, soaThreshold);
	}

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

			});

		});

	}

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
			total.setInternalWorkload(total.getInternalWorkload() + phase.getExternalWorkload());
			if (phase.getNumber() > total.getNumber())
				total.setNumber(phase.getNumber());
		}
		return total;
	}

}
