/**
 * 
 */
package lu.itrust.business.TS.model.general.helper;

import java.util.LinkedList;
import java.util.List;

import lu.itrust.business.TS.model.scenario.Scenario;

/**
 * @author eomar
 *
 */
public class ProxyScenario implements ProxyAssetScenario {

	private Scenario scenario;

	@Override
	public int getId() {
		return scenario.getId();
	}

	@Override
	public String getName() {
		return scenario.getName();
	}

	@Override
	public List<Object[]> getAllFields() {
		List<Object[]> fields = new LinkedList<>();
		fields.add(getField("name"));
		fields.add(getField("description"));
		return fields;
	}

	@Override
	public Object[] getField(String fieldName) {
		switch (fieldName) {
		case "name":
			return new Object[] { fieldName, getName(), "label.scenario.name" };
		case "description":
			return new Object[] { fieldName, get().getDescription(), "label.scenario.description" };
		}
		return null;
	}

	@Override
	public Scenario get() {
		return this.scenario;
	}

}
