package lu.itrust.business.TS.helper;

public class Distribution {

	private int divisor;

	protected Distribution(int divisor) {
		setDivisor(divisor);
	}

	public static Distribution Distribut(int size, int contentSize, int maxContentSize) {
		return new Distribution((size <= maxContentSize ? 1 : (int) Math.ceil((double) size / (double) contentSize)));
	}

	/**
	 * @return the divisor
	 */
	public int getDivisor() {
		return divisor;
	}

	/**
	 * @param divisor
	 *            the divisor to set
	 */
	public void setDivisor(int divisor) {
		this.divisor = divisor;
	}

}
