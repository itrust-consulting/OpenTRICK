package lu.itrust.business.ts.model.parameter;

/**
 * This interface represents a colored parameter that extends the {@link IParameter} interface.
 * It provides a method to retrieve the color of the parameter.
 */
public interface IColoredParameter extends IParameter {
	
	/**
	 * Gets the color of the parameter.
	 *
	 * @return the color of the parameter
	 */
	String getColor();
}
