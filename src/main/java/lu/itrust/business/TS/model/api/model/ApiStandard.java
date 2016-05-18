/**
 * 
 */
package lu.itrust.business.TS.model.api.model;

import java.util.Collections;
import java.util.List;

/**
 * @author eomar
 *
 */
public class ApiStandard extends ApiNamable {
	
	private List<ApiMeasure> measures = Collections.emptyList();

	/**
	 * @return the measures
	 */
	public List<ApiMeasure> getMeasures() {
		return measures;
	}

	/**
	 * @param measures the measures to set
	 */
	public void setMeasures(List<ApiMeasure> measures) {
		this.measures = measures;
	}

}
