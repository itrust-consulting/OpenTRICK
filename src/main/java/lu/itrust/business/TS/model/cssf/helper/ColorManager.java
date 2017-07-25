package lu.itrust.business.TS.model.cssf.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lu.itrust.business.TS.component.chartJS.helper.ColorBound;
import lu.itrust.business.TS.model.parameter.impl.RiskAcceptanceParameter;

public class ColorManager {

	private List<ColorBound> colorBounds = Collections.emptyList();

	public ColorManager(List<RiskAcceptanceParameter> parameters) {
		initialise(parameters);
	}

	public void initialise(List<RiskAcceptanceParameter> parameters) {
		this.colorBounds = new ArrayList<>(parameters.size());
		for (int i = 0; i < parameters.size(); i++) {
			RiskAcceptanceParameter parameter = parameters.get(i);
			if (colorBounds.isEmpty())
				colorBounds.add(new ColorBound(parameter.getColor(), parameter.getLabel(), 0, parameter.getValue().intValue()));
			else if (parameters.size() == (i + 1))
				colorBounds.add(new ColorBound(parameter.getColor(), parameter.getLabel(), parameters.get(i - 1).getValue().intValue(), Integer.MAX_VALUE));
			else
				colorBounds.add(new ColorBound(parameter.getColor(), parameter.getLabel(), parameters.get(i - 1).getValue().intValue(), parameter.getValue().intValue()));
		}
	}

	public String getColor(int importance) {
		return colorBounds.parallelStream().filter(c -> c.isAccepted(importance)).map(ColorBound::getColor).findAny().orElse("#ffffff");
	}

}
