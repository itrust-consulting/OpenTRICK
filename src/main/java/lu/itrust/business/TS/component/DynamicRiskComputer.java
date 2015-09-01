package lu.itrust.business.TS.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.dao.DAOMeasure;
import lu.itrust.business.TS.database.dao.DAOUserAnalysisRight;
import lu.itrust.business.TS.database.service.ServiceExternalNotification;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.parameter.AcronymParameter;
import lu.itrust.business.TS.model.parameter.DynamicParameter;
import lu.itrust.business.TS.model.parameter.Parameter;
import lu.itrust.business.TS.model.rrf.RRF;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.expressions.StringExpressionParser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Component which allows to compute the current (real-time) risk of assessments.
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @since Aug 17, 2015
 */
@Component
public class DynamicRiskComputer {
	public DynamicRiskComputer() {
	}
	
	@Autowired
	private ServiceExternalNotification serviceExternalNotification;
	
	@Autowired
	private DAOUserAnalysisRight daoUserAnalysisRight;
	
	@Autowired
	private DAOMeasure daoMeasure;
	
	/**
	 * Computes the real-time ALE of all given assessments at the given timestamp.
	 * The value is recomputed from scratch, not relying on any cached ALE values.
	 * The computed value is NOT cached within the analysis.
	 * @return Returns the computed value.
	 * @throws Exception 
	 * @throws IllegalArgumentException 
	 */
	public Map<Assessment, Double> computeAleOfAssessments(List<Assessment> assessments, List<AnalysisStandard> standards, long timestampBegin, long timestampEnd, List<String> cache_sourceUserNames, List<Parameter> allParameters, double minimumProbability) throws Exception {
		final Map<Assessment, Set<String>> out_involvedVariables = new HashMap<>();
		final Map<String, Double> out_expressionParameters = new HashMap<>();
		return computeAleOfAssessments(assessments, standards, timestampBegin, timestampEnd, cache_sourceUserNames, allParameters, minimumProbability, out_involvedVariables, out_expressionParameters);
	}

	/**
	 * Computes the real-time ALE of all given assessments at the given timestamp.
	 * The value is recomputed from scratch, not relying on any cached ALE values.
	 * The computed value is NOT cached within the analysis.
	 * @return Returns the computed value.
	 * @throws Exception 
	 * @throws IllegalArgumentException 
	 */
	public Map<Assessment, Double> computeAleOfAssessments(List<Assessment> assessments, List<AnalysisStandard> standards, long timestampBegin, long timestampEnd, List<String> cache_sourceUserNames, List<Parameter> allParameters, double minimumProbability, Map<Assessment, Set<String>> out_involvedVariables, Map<String, Double> out_expressionParameters) throws Exception {
		// Find all measures
		final List<Measure> measures = new ArrayList<>();
		for (AnalysisStandard standard : standards)
			measures.addAll(standard.getMeasures());

		// Find all static expression parameters ("p0" etc.)
		Parameter tuningParameter = null;
		for (Parameter p : allParameters) {
			if (p instanceof AcronymParameter && !(p instanceof DynamicParameter))
				out_expressionParameters.put(((AcronymParameter)p).getAcronym(), p.getValue());
			else if (p.getType().getLabel().equals(Constant.PARAMETERTYPE_TYPE_SINGLE_NAME) && p.getDescription().equals(Constant.PARAMETER_MAX_RRF))
				tuningParameter = p;
		}

		// Find all dynamic parameters and the respective values back then
		for (String sourceUserName : cache_sourceUserNames)
			//expressionParameters.putAll(serviceExternalNotification.computeProbabilitiesAtTime(timestamp, sourceUserName, minimumProbability));
			out_expressionParameters.putAll(serviceExternalNotification.computeProbabilitiesInInterval(timestampBegin, timestampEnd, sourceUserName, minimumProbability));

		Map<Assessment, Double> totalAleGrouped = new HashMap<>(); 
		for (Assessment assessment : assessments) {
			if (!assessment.isSelected()) continue;

			// Determine the likelihood and ALE of the current risk assessment
			final StringExpressionParser likelihoodExprParser = new StringExpressionParser(assessment.getLikelihood());
			final double likelihood = likelihoodExprParser.evaluate(out_expressionParameters);
			final double ale = assessment.getImpactReal() * likelihood;
			
			//out_involvedVariables = likelihoodExprParser.getInvolvedVariables().collect(Collectors.toList());
			out_involvedVariables.putIfAbsent(assessment, new HashSet<>());
			out_involvedVariables.get(assessment).addAll(likelihoodExprParser.getInvolvedVariables());

			// Determine the total ALE of this assessment, considering risk reduction
			// For the mathematical background, see
			// \RD\SGL Cockpit\WP1-RiskAnalysis\DynamicRiskAnalysis\REP_R110_Import_Performance_Measurement_Data_Into_TS_v0.2.docx
 
			double aleFactor = 1.;
			for (Measure measure : measures) {
				final List<String> involvedVariables = measure.getVariablesInvolvedInImplementationRateValue();
				if (involvedVariables.size() > 0) {
					out_involvedVariables.get(assessment).addAll(involvedVariables);
					final double implementationRate = measure.getImplementationRateValue(out_expressionParameters) / 100.0;
					final double rrf = RRF.calculateRRF(assessment, tuningParameter, measure);
	
					// TODO: consider maturity standards
	
					// TODO: HACK: there is no such parameter as the initial implementation rate yet (not implemented yet). \
					// For now, we just assume the initial rate was 0.
					// The motivation of the formula below is given by the above-mentioned document in Section 4.2.2.
					aleFactor *= 1 - rrf * implementationRate;
				}
			}
			final double previousTotalAle = totalAleGrouped.getOrDefault(assessment, 0.0);
			// The formula below for computing the ALE of the current implementation rate is mathematically founded (and not trivial!).
			// See the appropriate documentation for derivation details.
			totalAleGrouped.put(assessment, previousTotalAle + ale * aleFactor);
		}
		return totalAleGrouped;
	}
}
