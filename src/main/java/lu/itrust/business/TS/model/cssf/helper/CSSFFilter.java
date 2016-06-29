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
	
	/**
	 * -1: exclude, 0: all
	 */
	private int cia = -1;

	private double impact;

	private double probability;
	

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
	public CSSFFilter(int direct, int indirect, int cia, double impact, double probability) {
		this.direct = direct;
		this.indirect = indirect;
		this.cia = cia;
		this.impact = impact;
		this.probability = probability;
	}

	public CSSFFilter(double impactValue, double probabiltyValue) {
		this.impact = impactValue;
		this.probability = probabiltyValue;
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
	 * -1 : exclude 0 : all
	 * 
	 * @return the cia
	 */
	public int getCia() {
		return cia;
	}

	/**
	 * -1 : exclude, 0 : all
	 * 
	 * @return the cia
	 */
	public int getCia(int defaultValue) {
		return cia <= -1 ? 0 : cia == 0 ? defaultValue : cia;
	}

	/**
	 * -1 : exclude, 0 : all
	 * 
	 * @param cia
	 *            the cia to set
	 */
	public void setCia(int cia) {
		this.cia = cia;
	}

	public int getDirect(int size) {
		return direct < 0 ? size : direct;
	}

	public int getIndirect(int size) {
		return indirect < 0 ? size : indirect;
	}

	/**
	 * @return the impact
	 */
	public double getImpact() {
		return impact;
	}

	/**
	 * @param impact
	 *            the impact to set
	 */
	public void setImpact(double impact) {
		this.impact = impact;
	}

	/**
	 * @return the probability
	 */
	public double getProbability() {
		return probability;
	}

	/**
	 * @param probability
	 *            the probability to set
	 */
	public void setProbability(double probability) {
		this.probability = probability;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CSSFFilter [direct=" + direct + ", indirect=" + indirect + ", cia=" + cia + ", impact=" + impact + ", probability=" + probability;
	}

}
