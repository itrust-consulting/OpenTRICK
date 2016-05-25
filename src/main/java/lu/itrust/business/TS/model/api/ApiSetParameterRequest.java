package lu.itrust.business.TS.model.api;

import java.util.List;

/**
 * Represents an API request which sets the value of a dynamic parameter.
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @since Aug 21, 2015
 */
public class ApiSetParameterRequest {
	/**
	 * Represents the actual data of the request, namely the collection of parameter setters.
	 */
	private List<ApiParameterSetter> data;

	/**
	 * Gets the list of parameter setters contained in this request.
	 */
	public List<ApiParameterSetter> getData() {
		return data;
	}

	/**
	 * Sets the list of parameter setters contained in this request.
	 */
	public void setData(List<ApiParameterSetter> data) {
		this.data = data;
	}

}
