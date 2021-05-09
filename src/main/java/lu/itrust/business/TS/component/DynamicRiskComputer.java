package lu.itrust.business.TS.component;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.service.ServiceExternalNotification;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.parameter.IImpactParameter;
import lu.itrust.business.TS.model.parameter.IParameter;
import lu.itrust.business.TS.model.parameter.impl.LikelihoodParameter;
import lu.itrust.business.TS.model.parameter.impl.SimpleParameter;
import lu.itrust.business.TS.model.parameter.value.IValue;
import lu.itrust.business.TS.model.parameter.value.impl.FormulaValue;
import lu.itrust.business.TS.model.rrf.RRF;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.expressions.StringExpressionParser;

/**
 * Component which allows to compute the current (real-time) risk of
 * assessments.
 * 
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @since Aug 17, 2015
 */
@Component
public class DynamicRiskComputer {
	public DynamicRiskComputer() {
	}

	@Autowired
	private ServiceExternalNotification serviceExternalNotification;

	/**
	 * Computes the real-time ALE of all given assessments at the given timestamp.
	 * The value is recomputed from scratch, not relying on any cached ALE values.
	 * The computed value is NOT cached within the analysis.
	 * 
	 * @param assessments           The list of assessments that are taken into
	 *                              consideration when computing ALE.
	 * @param standards             The collection of all standards containing the
	 *                              security measures taken into consideration.
	 * @param timestampBegin        The beginning of the time interval over which
	 *                              the ALE shall be computed.
	 * @param timestampEnd          The end of the time interval over which the ALE
	 *                              shall be computed.
	 * @param cache_sourceUserNames A collection of all user names of external
	 *                              utilities reporting the dynamic parameters that
	 *                              are evaluated in the given time interval.
	 * @param allParameters         A collection of (at least) all static
	 *                              parameters.
	 * @param minimumProbability    The minimum probability level throughout time.
	 * @param out_involvedVariables An empty set per assessment, to which this
	 *                              method will add all involved variables.
	 * @return Returns the computed value for each assessment in the given list.
	 * @throws Exception
	 * @throws IllegalArgumentException
	 */
	public Map<Assessment, Double> computeAleOfAssessments(List<Assessment> assessments, List<AnalysisStandard> standards, long timestampBegin, long timestampEnd,
			List<String> cache_sourceUserNames, List<IParameter> allParameters, double minimumProbability) throws Exception {
		final Map<Assessment, Set<String>> out_involvedVariables = new HashMap<>();
		final Map<String, Double> out_expressionParameters = new HashMap<>();
		return computeAleOfAssessments(assessments, standards, timestampBegin, timestampEnd, cache_sourceUserNames, allParameters, minimumProbability, out_involvedVariables,
				out_expressionParameters);
	}

	/**
	 * Computes the real-time ALE of all given assessments at the given timestamp.
	 * The value is recomputed from scratch, not relying on any cached ALE values.
	 * The computed value is NOT cached within the analysis.
	 * 
	 * @param assessments              The list of assessments that are taken into
	 *                                 consideration when computing ALE.
	 * @param standards                The collection of all standards containing
	 *                                 the security measures taken into
	 *                                 consideration.
	 * @param timestampBegin           The beginning of the time interval over which
	 *                                 the ALE shall be computed.
	 * @param timestampEnd             The end of the time interval over which the
	 *                                 ALE shall be computed.
	 * @param cache_sourceUserNames    A collection of all user names of external
	 *                                 utilities reporting the dynamic parameters
	 *                                 that are evaluated in the given time
	 *                                 interval.
	 * @param allParameters            A collection of (at least) all static
	 *                                 parameters.
	 * @param minimumProbability       The minimum probability level throughout
	 *                                 time.
	 * @param out_involvedVariables    An empty set per assessment, to which this
	 *                                 method will add all involved variables.
	 * @param out_expressionParameters An empty map, to which this method will add
	 *                                 all used dynamic parameters and their values
	 *                                 in the time interval.
	 * @return Returns the computed value for each assessment in the given list.
	 * @throws Exception
	 * @throws IllegalArgumentException
	 */
	public Map<Assessment, Double> computeAleOfAssessments(List<Assessment> assessments, Collection<AnalysisStandard> standards, long timestampBegin, long timestampEnd,
			List<String> cache_sourceUserNames, List<IParameter> allParameters, double minimumProbability, final Map<Assessment, Set<String>> out_involvedVariables,
			final Map<String, Double> out_expressionParameters) throws Exception {
		// Find all measures
		final List<Measure> measures = new LinkedList<>();
		for (AnalysisStandard standard : standards)
			measures.addAll(standard.getMeasures());

		// Find all static expression parameters ("p0" etc.)
		final Map<String, Double> parameters = new HashMap<>();
		IParameter tuningParameter = null;
		for (IParameter p : allParameters) {
			if (p instanceof LikelihoodParameter) {
				out_expressionParameters.put(((LikelihoodParameter) p).getAcronym(), p.getValue().doubleValue());
				parameters.put(((LikelihoodParameter) p).getAcronym(), p.getValue().doubleValue());
			} else if ((p instanceof IImpactParameter) && p.getTypeName().equals(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME))
				parameters.put(((IImpactParameter) p).getAcronym(), p.getValue().doubleValue());
			else if ((p instanceof SimpleParameter) && p.isMatch(Constant.PARAMETERTYPE_TYPE_SINGLE_NAME, Constant.PARAMETER_MAX_RRF))
				tuningParameter = p;
		}

		// Find all dynamic parameters and the respective values back then
		for (String sourceUserName : cache_sourceUserNames)
			// expressionParameters.putAll(serviceExternalNotification.computeProbabilitiesAtTime(timestamp,
			// sourceUserName, minimumProbability));
			out_expressionParameters.putAll(serviceExternalNotification.computeProbabilitiesInInterval(timestampBegin, timestampEnd, sourceUserName, minimumProbability));

		final Map<Assessment, Double> totalAleGrouped = new HashMap<>();
		for (Assessment assessment : assessments) {
			final IValue impact = assessment.getImpact(Constant.PARAMETERTYPE_TYPE_IMPACT_NAME);
			final double realImact;
			if (impact == null)
				continue;
			else if (impact instanceof FormulaValue) {
				realImact = new StringExpressionParser(impact.getVariable(), StringExpressionParser.IMPACT).evaluate(parameters);
			} else
				realImact = impact.getReal();
			// Determine the likelihood and ALE of the current risk assessment
			final StringExpressionParser likelihoodExprParser = new StringExpressionParser(assessment.getLikelihood() == null ? "0" : assessment.getLikelihood().getVariable(),
					StringExpressionParser.PROBABILITY);
			final double likelihood = likelihoodExprParser.evaluate(out_expressionParameters);
			final double ale = realImact * likelihood;
			if (!assessment.isSelected())
				continue;
			// out_involvedVariables =
			// likelihoodExprParser.getInvolvedVariables().collect(Collectors.toList());
			out_involvedVariables.putIfAbsent(assessment, new HashSet<>());
			out_involvedVariables.get(assessment).addAll(likelihoodExprParser.getInvolvedVariables());

			// Determine the total ALE of this assessment, considering risk
			// reduction
			// For the mathematical background, see
			// \RD\SGL
			// Cockpit\WP1-RiskAnalysis\DynamicRiskAnalysis\REP_R110_Import_Performance_Measurement_Data_Into_TS_v0.2.docx

			double aleFactor = 1.;
			for (Measure measure : measures) {
				final List<String> involvedVariables = measure.getVariablesInvolvedInImplementationRateValue();
				if (involvedVariables.size() > 0) {
					out_involvedVariables.get(assessment).addAll(involvedVariables);
					final double implementationRate = measure.getImplementationRateValue(out_expressionParameters) / 100.0;
					final double rrf = RRF.calculateRRF(assessment, tuningParameter, measure);

					// TODO: consider maturity standards

					// TODO: HACK: there is no such parameter as the initial
					// implementation rate yet (not implemented yet). \
					// For now, we just assume the initial rate was 0.
					// The motivation of the formula below is given by the
					// above-mentioned document in Section 4.2.2.
					aleFactor *= 1 - rrf * implementationRate;
				}
			}
			final double previousTotalAle = totalAleGrouped.getOrDefault(assessment, 0.0);
			// The formula below for computing the ALE of the current
			// implementation rate is mathematically founded (and not trivial!).
			// See the appropriate documentation for derivation details.
			totalAleGrouped.put(assessment, previousTotalAle + ale * aleFactor);
		}
		return totalAleGrouped;
	}

	/**
	 * Generates the ALE evolution data for the given assessments.
	 * 
	 * @param assessments     The list of assessments that are taken into
	 *                        consideration when computing ALE.
	 * @param standards       The collection of all standards containing the
	 *                        security measures taken into consideration.
	 * @param sourceUserNames A collection of all user names of external utilities
	 *                        reporting the dynamic parameters that are evaluated in
	 *                        the given time interval.
	 * @param allParameters   A collection of (at least) all static parameters.
	 * @param aggregator      A function which selects the key by which the data
	 *                        shall be aggregated.
	 * @param out_timePoints  An empty list, to which this method will add all used
	 *                        time points.
	 * @return Returns the ALE by aggregation key and by time.
	 * @throws Exception
	 */
	public <TAggregator> Map<TAggregator, Map<Long, Double>> generateAleEvolutionData(List<Assessment> assessments, List<AnalysisStandard> standards, List<String> sourceUserNames,
			List<IParameter> allParameters, Function<Assessment, TAggregator> aggregator, List<Long> out_timePoints) throws Exception {
		return generateAleEvolutionData(assessments, standards, sourceUserNames, allParameters, aggregator, out_timePoints, null, null);
	}

	/**
	 * Generates the ALE evolution data for the given assessments.
	 * 
	 * @param assessments                      The list of assessments that are
	 *                                         taken into consideration when
	 *                                         computing ALE.
	 * @param standards                        The collection of all standards
	 *                                         containing the security measures
	 *                                         taken into consideration.
	 * @param sourceUserNames                  A collection of all user names of
	 *                                         external utilities reporting the
	 *                                         dynamic parameters that are evaluated
	 *                                         in the given time interval.
	 * @param allParameters                    A collection of (at least) all static
	 *                                         parameters.
	 * @param aggregator                       A function which selects the key by
	 *                                         which the data shall be aggregated.
	 * @param out_timePoints                   An empty list, to which this method
	 *                                         will add all used time points.
	 * @param out_involvedVariables_or_null    An empty set per assessment per time
	 *                                         point, to which this method will add
	 *                                         all involved variables. Or null, in
	 *                                         which case no data is output.
	 * @param out_expressionParameters_or_null An empty map per time point, to which
	 *                                         this method will add all used dynamic
	 *                                         parameters and their values in the
	 *                                         time interval. Or null, in which case
	 *                                         no data is output.
	 * @return Returns the ALE by aggregation key and by time.
	 * @throws Exception
	 */
	public <TAggregator> Map<TAggregator, Map<Long, Double>> generateAleEvolutionData(List<Assessment> assessments, Collection<AnalysisStandard> standards,
			List<String> sourceUserNames, List<IParameter> allParameters, Function<Assessment, TAggregator> aggregator, List<Long> out_timePoints,
			final Map<Long, Map<Assessment, Set<String>>> out_involvedVariables_or_null, final Map<Long, Map<String, Double>> out_expressionParameters_or_null) throws Exception {
		// Determine time-related stuff
		final long timeUpperBound = Instant.now().getEpochSecond();
		final long timeLowerBound = timeUpperBound - Constant.CHART_DYNAMIC_PARAMETER_EVOLUTION_HISTORY_IN_SECONDS;
		long nextTimeIntervalSize = 60; // in seconds

		Map<TAggregator, Map<Long, Double>> data = new HashMap<>();
		for (long timeEnd = timeUpperBound; timeEnd - nextTimeIntervalSize >= timeLowerBound; timeEnd -= nextTimeIntervalSize) {
			// Add x-axis values to a list in reverse order (we use
			// Collections.reverse() later on)
			if (out_timePoints != null)
				out_timePoints.add(timeEnd);

			final Map<Assessment, Set<String>> out1 = new HashMap<>();
			final Map<String, Double> out2 = new HashMap<>();
			if (out_involvedVariables_or_null != null)
				out_involvedVariables_or_null.put(timeEnd, out1);
			if (out_expressionParameters_or_null != null)
				out_expressionParameters_or_null.put(timeEnd, out2);

			// Fetch data
			final Map<Assessment, Double> aleByAssessment = this.computeAleOfAssessments(assessments, standards, timeEnd - nextTimeIntervalSize, timeEnd, sourceUserNames,
					allParameters, 0., out1, out2);
			for (Assessment assessment : aleByAssessment.keySet()) {
				final double partialAle = aleByAssessment.get(assessment);

				// Group by aggregator
				final TAggregator key = aggregator.apply(assessment);
				data.putIfAbsent(key, new HashMap<Long, Double>());

				// Group by time
				final Map<Long, Double> dataByTime = data.get(key);
				dataByTime.put(timeEnd, dataByTime.getOrDefault(timeEnd, 0.) + partialAle);
			}

			// Modify interval size
			if (nextTimeIntervalSize < Constant.CHART_DYNAMIC_PARAMETER_MAX_SIZE_OF_LOGARITHMIC_SCALE)
				nextTimeIntervalSize = (int) (nextTimeIntervalSize * Constant.CHART_DYNAMIC_PARAMETER_LOGARITHMIC_FACTOR);
		}

		if (out_timePoints != null)
			Collections.reverse(out_timePoints);
		return data;
	}
}
