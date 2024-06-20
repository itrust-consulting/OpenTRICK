package lu.itrust.business.ts.form;

import lu.itrust.business.ts.model.analysis.rights.AnalysisRight;

public class RightForm {

	private AnalysisRight oldRight;

	private AnalysisRight newRight;

	/**
	 * 
	 */
	public RightForm() {
	}

	/**
	 * @return the oldRight
	 */
	public AnalysisRight getOldRight() {
		return oldRight;
	}

	/**
	 * @param oldRight
	 *            the oldRight to set
	 */
	public void setOldRight(AnalysisRight oldRight) {
		this.oldRight = oldRight;
	}

	/**
	 * @return the newRight
	 */
	public AnalysisRight getNewRight() {
		return newRight;
	}

	/**
	 * @param newRight
	 *            the newRight to set
	 */
	public void setNewRight(AnalysisRight newRight) {
		this.newRight = newRight;
	}

	public boolean hasChange() {
		return oldRight != newRight;
	}

	/**
	 * @param oldRight
	 * @param newRight
	 */
	public RightForm(AnalysisRight oldRight, AnalysisRight newRight) {
		this.oldRight = oldRight;
		this.newRight = newRight;
	}
}
