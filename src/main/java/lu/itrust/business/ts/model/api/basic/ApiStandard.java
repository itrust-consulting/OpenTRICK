package lu.itrust.business.ts.model.api.basic;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents an API standard which contains list of all the measures collections.
 */
public class ApiStandard extends ApiNamable {
	
	private List<ApiMeasure> measures = new LinkedList<>();

	/**
	 * Default constructor.
	 */
	public ApiStandard() {
	}

	/**
	 * Constructor with id and name parameters.
	 *
	 * @param id   the ID of the API standard
	 * @param name the name of the API standard
	 */
	public ApiStandard(Integer id, String name) {
		super(id, name);
	}

	/**
	 * Returns the list of API measures associated with this standard.
	 *
	 * @return the list of API measures
	 */
	public List<ApiMeasure> getMeasures() {
		return measures;
	}

	/**
	 * Sets the list of API measures associated with this standard.
	 *
	 * @param measures the list of API measures to set
	 */
	public void setMeasures(List<ApiMeasure> measures) {
		this.measures = measures;
	}

}
