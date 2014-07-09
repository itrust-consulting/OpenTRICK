package lu.itrust.business.component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.Scenario;
import lu.itrust.business.TS.ScenarioType;

public class ScenarioManager {

	public static Map<ScenarioType, List<Scenario>> SplitByType(List<Scenario> scenarios) {
		Map<ScenarioType, List<Scenario>> mappedScenarios = new LinkedHashMap<ScenarioType, List<Scenario>>();
		for (Scenario scenario : scenarios) {
			List<Scenario> scenarios2 = mappedScenarios.get(scenario.getScenarioType());
			if (scenarios2 == null)
				mappedScenarios.put(scenario.getScenarioType(), scenarios2 = new ArrayList<Scenario>());
			scenarios2.add(scenario);
		}
		return mappedScenarios;
	}

}
