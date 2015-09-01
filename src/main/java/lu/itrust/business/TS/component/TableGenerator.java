package lu.itrust.business.TS.component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOExternalNotification;
import lu.itrust.business.TS.database.dao.DAOUserAnalysisRight;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.parameter.Parameter;
import lu.itrust.business.TS.model.scenario.Scenario;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.usermanagement.RoleType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Provides functionality for generating tables.
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @since Sep 1, 2015
 */
@Component
public class TableGenerator {
	@Autowired
	private DAOAnalysis daoAnalysis;

	@Autowired
	private DAOUserAnalysisRight daoUserAnalysisRight;

	@Autowired
	private DAOExternalNotification daoExternalNotification; 

	@Autowired
	private DynamicRiskComputer dynamicRiskComputer;

	public String findInterestingAleEvolutionPoints(int idAnalysis, String assetType, Locale locale) throws Exception {
		final Analysis analysis = daoAnalysis.get(idAnalysis);
		final List<Assessment> assessments = analysis.getAssessments().stream().filter(a -> a.getAsset().getAssetType().getType().equals(assetType)).collect(Collectors.toList());
		final List<AnalysisStandard> standards = analysis.getAnalysisStandards();
		final List<Parameter> allParameters = analysis.getParameters();
		final List<String> result = new ArrayList<>();

		// Find the user names of all sources involved
		final List<String> sourceUserNames = daoUserAnalysisRight
			.getAllFromAnalysis(analysis.getId()).stream()
			.map(userRight -> userRight.getUser())
			.filter(user -> user.hasRole(RoleType.ROLE_IDS))
			.map(user -> user.getLogin())
			.collect(Collectors.toList());

		// Determine time-related stuff
		final long timeUpperBound = Instant.now().getEpochSecond();
		final long timeLowerBound = timeUpperBound - Constant.CHART_DYNAMIC_PARAMETER_EVOLUTION_HISTORY_IN_SECONDS;
		long nextTimeIntervalSize = 60; // in seconds

		Map<Scenario, Double> nextTotalAleByScenario = null;
		Map<String, Double> nextExpressionParameters = null;

		for (long timeEnd = timeUpperBound - nextTimeIntervalSize; timeEnd - nextTimeIntervalSize >= timeLowerBound; timeEnd -= nextTimeIntervalSize) {
			final Map<Assessment, Set<String>> involvedVariables = new HashMap<>();
			final Map<String, Double> expressionParameters = new HashMap<>();
			final Map<Assessment, Double> aleByAssessment = dynamicRiskComputer.computeAleOfAssessments(assessments, standards, timeEnd - nextTimeIntervalSize, timeEnd, sourceUserNames, allParameters, 0., involvedVariables, expressionParameters);
			
			// Aggregate by scenario
			final Map<Scenario, Double> totalAleByScenario = new HashMap<>();
			for (Assessment assessment : aleByAssessment.keySet()) {
				final Scenario scenario = assessment.getScenario();
				totalAleByScenario.put(scenario, totalAleByScenario.getOrDefault(scenario, 0.) + aleByAssessment.get(assessment));
			}

			if (nextTotalAleByScenario != null) {
				// Check if the ALE in any scenario changes by any considerable amount
				for (Scenario scenario : totalAleByScenario.keySet()) {
					final double currentAle = totalAleByScenario.get(scenario);
					final double nextAle = nextTotalAleByScenario.getOrDefault(scenario, 0.);
					if (Math.abs(nextAle - currentAle) > Constant.EVOLUTION_MIN_ALE_ABSOLUTE_DIFFERENCE &&
						Math.abs((nextAle - currentAle) / currentAle) >= Constant.EVOLUTION_MIN_ALE_RELATIVE_DIFFERENCE) {

						// Check if any parameter changes considerably
						for (Assessment assessment : involvedVariables.keySet()) {
							if (assessment.getScenario() != scenario) continue;
							for (String dynamicParameterName : involvedVariables.get(assessment)) {
								final double currentValue = expressionParameters.getOrDefault(dynamicParameterName, 0.);
								final double nextValue = nextExpressionParameters.getOrDefault(dynamicParameterName, 0.);
								
								if (Math.abs((nextValue - currentValue) / currentValue) >= Constant.EVOLUTION_MIN_ALE_RELATIVE_DIFFERENCE)
									result.add(ChartGenerator.deltaTimeToString(timeUpperBound - timeEnd) + ": " + dynamicParameterName + ": " + (int)(100.*(nextValue - currentValue) / currentValue) + "%");
							}
						}
					}
				}
			}

			nextTotalAleByScenario = totalAleByScenario;
			nextExpressionParameters = expressionParameters;

			// Modify interval size
			if (nextTimeIntervalSize < Constant.CHART_DYNAMIC_PARAMETER_MAX_SIZE_OF_LOGARITHMIC_SCALE)
				nextTimeIntervalSize = (int)(nextTimeIntervalSize * Constant.CHART_DYNAMIC_PARAMETER_LOGARITHMIC_FACTOR);
		}
		
		return String.join("\n", result);
	}
}
