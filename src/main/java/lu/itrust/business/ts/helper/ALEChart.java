/**
 * 
 */
package lu.itrust.business.ts.helper;

import java.util.Collections;
import java.util.List;

import lu.itrust.business.ts.model.assessment.helper.ALE;

/**
 * @author eomar
 *
 */
public class ALEChart {
	
	private String name = "ALE";
	
	private List<ALE>  ales = Collections.emptyList();

	/**
	 * @param ales
	 */
	public ALEChart(List<ALE> ales) {
		setAles(ales);
	}
	
	/**
	 * @param name
	 * @param ales
	 */
	public ALEChart(String name, List<ALE> ales) {
		setName(name);
		setAles(ales);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		if(name == null)
			name = "ALE";
		this.name = name;
	}

	/**
	 * @return the ales
	 */
	public List<ALE> getAles() {
		return ales;
	}

	/**
	 * @param ales the ales to set
	 */
	public void setAles(List<ALE> ales) {
		this.ales = ales;
	}
	
}
