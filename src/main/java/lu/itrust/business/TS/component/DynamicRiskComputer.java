package lu.itrust.business.TS.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	 * Computes the real-time ALE of all assets at the given timestamp.
	 * The value is recomputed from scratch, not relying on any cached ALE value.
	 * The computed value is NOT cached within the analysis.
	 * @param assetId The id of the asset to compute the ALE for.
	 * @return Returns the computed value.
	 * @throws Exception 
	 * @throws IllegalArgumentException 
	 */
	public Map<Integer, Double> computeAleOfAllAssets(List<AnalysisStandard> standards, long timestampBegin, long timestampEnd, List<Assessment> cache_assessments, List<String> cache_sourceUserNames, List<Parameter> allParameters, Map<String, Double> allParameterValuesByLabel) throws Exception {
		double minimumProbability = allParameterValuesByLabel.getOrDefault("p0", 0.0);

		// Find all measures
		final List<Measure> measures = new ArrayList<>();
		for (AnalysisStandard standard : standards)
			measures.addAll(standard.getMeasures());

		// Find all static expression parameters ("p0" etc.)
		final Map<String, Double> expressionParameters = new HashMap<>();
		for (Parameter p : allParameters)
			if (p instanceof AcronymParameter && !(p instanceof DynamicParameter))
				expressionParameters.put(((AcronymParameter)p).getAcronym(), p.getValue());

		// Find all dynamic parameters and the respective values back then
		for (String sourceUserName : cache_sourceUserNames)
			//expressionParameters.putAll(serviceExternalNotification.computeProbabilitiesAtTime(timestamp, sourceUserName, minimumProbability));
			expressionParameters.putAll(serviceExternalNotification.computeProbabilitiesInInterval(timestampBegin, timestampEnd, sourceUserName, minimumProbability));

		Map<Integer, Double> totalAleByAsset = new HashMap<>(); 
		for (Assessment assessment : cache_assessments) {
			if (!assessment.isUsable()) continue;

			// Determine the likelihood of the risk scenario
			final String likelihoodExpression = assessment.getLikelihood();
			final double likelihood = new StringExpressionParser(likelihoodExpression).evaluate(expressionParameters);

			// Determine the total ALE of this assessment, considering risk reduction
			// TODO: for maturity standards, the computation is a bit different
			double ale = assessment.getImpactReal() * likelihood;
			for (Measure measure : measures) {
				final double implementationRate = measure.getImplementationRateValue(expressionParameters) / 100.0;
				final double rrf = RRF.calculateRRF(assessment, allParameters, measure);
				
				// TODO: consider maturity standards
				
				// The line
				// ale *= 1 - rrf * (1 - implementationRate) / (1 - rrf * implementationRate);
				// is equivalent to:
				ale *= (1 - rrf) / (1 - rrf * implementationRate);
			}

			double totalAle = totalAleByAsset.getOrDefault(assessment.getAssetId(), 0.0);
			totalAleByAsset.put(assessment.getAssetId(), totalAle + ale);
		}
		return totalAleByAsset;
	}
}
