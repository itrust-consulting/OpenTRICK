package lu.itrust.business.TS.component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lu.itrust.business.TS.database.dao.DAOUserAnalysisRight;
import lu.itrust.business.TS.database.service.ServiceExternalNotification;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.parameter.Parameter;
import lu.itrust.business.TS.model.rrf.RRF;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.usermanagement.RoleType;
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

	/**
	 * Computes the real-time ALE of an asset at the given timestamp.
	 * The value is recomputed from scratch, not relying on any cached ALE value.
	 * The computed value is NOT cached within the analysis.
	 * @param assetId The id of the asset to compute the ALE for.
	 * @return Returns the computed value.
	 * @throws Exception 
	 * @throws IllegalArgumentException 
	 */
	public double computeALEOfAsset(Analysis parentAnalysis, AnalysisStandard standard, int assetId, long timestamp) throws Exception {
		double totalALE = 0.0;
		final List<Measure> measures = standard.getMeasures();
		final List<Parameter> allParameters = parentAnalysis.getParameters();
		final double minimumProbability = Math.max(0.0, parentAnalysis.getParameter("p0")); // getParameter() returns -1 if no such parameter exists

		// Find the user names of all sources involved
		List<String> sourceUserNames = daoUserAnalysisRight
				.getAllFromAnalysis(parentAnalysis.getId()).stream()
				.map(userRight -> userRight.getUser())
				.filter(user -> user.hasRole(RoleType.ROLE_IDS))
				.map(user -> user.getLogin())
				.collect(Collectors.toList());
		
		// Find all dynamic parameters and the respective values back then
		final Map<String, Double> expressionParameters = new HashMap<>();
		for (String sourceUserName : sourceUserNames)
			expressionParameters.putAll(serviceExternalNotification.computeProbabilitiesAtTime(timestamp, sourceUserName, minimumProbability));

		for (Assessment assessment : parentAnalysis.getAssessments()) {
			if (assessment.getId() != assetId) continue;

			// Determine the total ΔALE resulting from risk reduction 
			double deltaALEFactor = 0.0;
			for (Measure measure : measures) {
				final double implementationRate = measure.getImplementationRateValue(expressionParameters) / 100.0;
				final double rrf = RRF.calculateRRF(assessment, allParameters, measure);
				deltaALEFactor += rrf * (1 - implementationRate) / (1 - rrf * implementationRate);  // TODO: is this formula correct?
			}

			// Determine the likelihood of the risk scenario
			final String likelihoodExpression = assessment.getLikelihood();
			final double likelihood = new StringExpressionParser(likelihoodExpression).evaluate(expressionParameters);

			// Get ALE for this assessment
			totalALE += (1 - deltaALEFactor) * assessment.getImpactReal() * likelihood;
		}
		return totalALE;
	}
}
