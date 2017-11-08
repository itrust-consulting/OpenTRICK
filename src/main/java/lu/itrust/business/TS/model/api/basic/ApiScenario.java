package lu.itrust.business.TS.model.api.basic;

/**
 * @author smuller
 * @since 2017-11-08
 */
public class ApiScenario extends ApiNamable {

	private Integer scenarioTypeId;
	private String scenarioTypeName;

	public ApiScenario(Integer id, String name, Integer scenarioTypeId, String scenarioTypeName) {
		super(id, name);
		this.scenarioTypeId = scenarioTypeId;
		this.scenarioTypeName = scenarioTypeName;
	}

	public Integer getScenarioTypeId() {
		return scenarioTypeId;
	}

	public void setScenarioTypeId(Integer scenarioTypeId) {
		this.scenarioTypeId = scenarioTypeId;
	}

	public String getScenarioTypeName() {
		return scenarioTypeName;
	}

	public void setScenarioTypeName(String scenarioTypeName) {
		this.scenarioTypeName = scenarioTypeName;
	}
}
