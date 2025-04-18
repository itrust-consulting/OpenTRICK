package lu.itrust.business.ts.model.cssf.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.helper.chartJS.item.ColorBound;
import lu.itrust.business.ts.model.parameter.impl.RiskAcceptanceParameter;

/**
 * The ColorManager class is responsible for managing color bounds and providing color values based on importance levels.
 */
public class ColorManager {

	private List<ColorBound> colorBounds = Collections.emptyList();

	public ColorManager(List<RiskAcceptanceParameter> parameters) {
		initialise(parameters);
	}

	/**
	 * Initializes the color bounds based on the given list of risk acceptance parameters.
	 * 
	 * @param parameters the list of risk acceptance parameters
	 */
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

	/**
	 * Returns the color based on the importance level.
	 *
	 * @param importance the importance level
	 * @return the color corresponding to the importance level
	 */
	public String getColor(int importance) {
		return colorBounds.isEmpty() ? Constant.HEAT_MAP_DEFAULT_COLOR : findColor(importance, 0, colorBounds.size());
	}

	/**
	 * Finds the color based on the given importance level within the specified range.
	 *
	 * @param importance the importance level of the color to find
	 * @param begin the beginning index of the range
	 * @param end the ending index of the range
	 * @return the color that matches the given importance level
	 */
	private String findColor(int importance, int begin, int end) {
		int mid = (end + begin) / 2;
		ColorBound bound = colorBounds.get(mid);
		if (bound.isAccepted(importance))
			return bound.getColor();
		else if (importance > bound.getMax())
			return ++mid < end ? findColor(importance, mid, end) : bound.getColor();
		else
			return findColor(importance, begin, mid - 1);
	}

}
