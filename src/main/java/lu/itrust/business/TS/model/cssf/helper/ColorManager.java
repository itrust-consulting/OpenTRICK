package lu.itrust.business.TS.model.cssf.helper;

import java.util.Collections;
import java.util.List;

import lu.itrust.business.TS.model.parameter.impl.RiskAcceptanceParameter;

public class ColorManager {

	private static final String DEFAULT_COLOR = "#ffffff";
	private List<RiskAcceptanceParameter> parameters = Collections.emptyList();

	public ColorManager(List<RiskAcceptanceParameter> parameters) {
		this.parameters = parameters;
	}

	public String getColor(int importance) {
		return parameters.isEmpty() ? DEFAULT_COLOR : find(importance, parameters, 0, parameters.size());
	}

	private String find(int importance, List<RiskAcceptanceParameter> parameters, int begin, int end) {
		int mild = (end + begin) / 2;
		if (end == mild || begin == mild)
			return parameters.get(mild).getColor();
		RiskAcceptanceParameter parameter = parameters.get(mild);
		int value = parameter.getValue().intValue();
		if (value == importance)
			return parameter.getColor();
		else if (importance > value)
			return find(importance, parameters, mild, end);
		return find(importance, parameters, begin, mild);
	}

}
