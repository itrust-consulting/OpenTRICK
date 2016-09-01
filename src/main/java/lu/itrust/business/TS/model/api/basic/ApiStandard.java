/**
 * 
 */
package lu.itrust.business.TS.model.api.basic;

import java.util.LinkedList;
import java.util.List;

/**
 * @author eomar
 *
 */
public class ApiStandard extends ApiNamable {
	
	private List<ApiMeasure> measures = new LinkedList<>();

	/**
	 * 
	 */
	public ApiStandard() {
	}

	/**
	 * @param id
	 * @param name
	 */
	public ApiStandard(Integer id, String name) {
		super(id, name);
	}

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
