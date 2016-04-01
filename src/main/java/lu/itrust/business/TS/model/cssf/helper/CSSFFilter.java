/**
 * 
 */
package lu.itrust.business.TS.model.cssf.helper;

/**
 * @author eomar
 *
 */
public class CSSFFilter {

	private int direct = 20;

	private int indirect = 5;

	private int cia = -1;

	private double importanceThreshold = 0D;

	/**
	 * 
	 */
	public CSSFFilter() {
	}

	/**
	 * @param direct
	 * @param indirect
	 * @param cia
	 */
	public CSSFFilter(int direct, int indirect, int cia, double threshold) {
		this.direct = direct;
		this.indirect = indirect;
		this.cia = cia;
		this.importanceThreshold = threshold;
	}

	/**
	 * @return the direct
	 */
	public int getDirect() {
		return direct;
	}

	/**
	 * @param direct
	 *            the direct to set
	 */
	public void setDirect(int direct) {
		this.direct = direct;
	}

	/**
	 * @return the indirect
	 */
	public int getIndirect() {
		return indirect;
	}

	/**
	 * @param indirect
	 *            the indirect to set
	 */
	public void setIndirect(int indirect) {
		this.indirect = indirect;
	}

	/**
	 * @return the cia
	 */
	public int getCia() {
		return cia;
	}

	/**
	 * @param cia
	 *            the cia to set
	 */
	public void setCia(int cia) {
		this.cia = cia;
	}

	/**
	 * @return the importanceThreshold
	 */
	public double getImportanceThreshold() {
		return importanceThreshold;
	}

	/**
	 * @param importanceThreshold
	 *            the importanceThreshold to set
	 */
	public void setImportanceThreshold(double importanceThreshold) {
		this.importanceThreshold = importanceThreshold;
	}

	public int getDirect(int size) {
		return direct < 0 ? size : direct;
	}

	public int getIndirect(int size) {
		return indirect < 0 ? size : indirect;
	}

	public int getCia(int size) {
		return cia < 0 ? size : cia;
	}

}
