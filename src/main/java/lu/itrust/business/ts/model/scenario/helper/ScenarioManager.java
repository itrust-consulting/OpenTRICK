package lu.itrust.business.ts.model.scenario.helper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lu.itrust.business.ts.model.scenario.Scenario;
import lu.itrust.business.ts.model.scenario.ScenarioType;

/**
 * The ScenarioManager class is responsible for managing scenarios and splitting them by type.
 */
public class ScenarioManager {

	/**
	 * Splits the given list of scenarios by type and returns a map of scenario types to lists of scenarios.
	 *
	 * @param scenarios the list of scenarios to be split
	 * @return a map of scenario types to lists of scenarios
	 */
	public static Map<ScenarioType, List<Scenario>> SplitByType(List<Scenario> scenarios) {
		Map<ScenarioType, List<Scenario>> mappedScenarios = new LinkedHashMap<ScenarioType, List<Scenario>>();
		for (Scenario scenario : scenarios) {
			List<Scenario> scenarios2 = mappedScenarios.get(scenario.getType());
			if (scenarios2 == null)
				mappedScenarios.put(scenario.getType(), scenarios2 = new ArrayList<Scenario>());
			scenarios2.add(scenario);
		}
		return mappedScenarios;
	}

}
