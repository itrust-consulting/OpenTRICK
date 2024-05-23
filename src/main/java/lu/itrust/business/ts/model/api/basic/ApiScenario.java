package lu.itrust.business.ts.model.api.basic;

import lu.itrust.business.ts.model.scenario.Scenario;

/**
 * Represents an API scenario.
 * Extends the {@link ApiNamable} class.
 */
public class ApiScenario extends ApiNamable {

	private Integer scenarioTypeId;
	private String scenarioTypeName;

	/**
	 * Constructs a new {@code ApiScenario} object with the specified parameters.
	 *
	 * @param id               the ID of the scenario
	 * @param name             the name of the scenario
	 * @param scenarioTypeId   the ID of the scenario type
	 * @param scenarioTypeName the name of the scenario type
	 */
	public ApiScenario(Integer id, String name, Integer scenarioTypeId, String scenarioTypeName) {
		super(id, name);
		this.scenarioTypeId = scenarioTypeId;
		this.scenarioTypeName = scenarioTypeName;
	}

	/**
	 * Creates a new {@code ApiScenario} object based on the given {@code Scenario} object.
	 *
	 * @param scenario the scenario object to create from
	 * @return the created {@code ApiScenario} object
	 */
	public static ApiScenario create(Scenario scenario) {
		return new ApiScenario(scenario.getId(), scenario.getName(), scenario.getType().getValue(), scenario.getType().getName());
	}

	/**
	 * Gets the ID of the scenario type.
	 *
	 * @return the scenario type ID
	 */
	public Integer getScenarioTypeId() {
		return scenarioTypeId;
	}

	/**
	 * Sets the ID of the scenario type.
	 *
	 * @param scenarioTypeId the scenario type ID to set
	 */
	public void setScenarioTypeId(Integer scenarioTypeId) {
		this.scenarioTypeId = scenarioTypeId;
	}

	/**
	 * Gets the name of the scenario type.
	 *
	 * @return the scenario type name
	 */
	public String getScenarioTypeName() {
		return scenarioTypeName;
	}

	/**
	 * Sets the name of the scenario type.
	 *
	 * @param scenarioTypeName the scenario type name to set
	 */
	public void setScenarioTypeName(String scenarioTypeName) {
		this.scenarioTypeName = scenarioTypeName;
	}
}
