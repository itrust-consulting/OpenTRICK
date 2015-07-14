package lu.itrust.business.TS.component;

public class Distribution {

	private int divisor;

	private int remainder;

	protected Distribution() {
	}

	public static Distribution Distribut(int size, int contentSize, int maxContentSize) {
		Distribution distribution = new Distribution();
		if (size % contentSize == 0)
			distribution.setDivisor(contentSize);
		else if (size % maxContentSize == 0)
			distribution.setDivisor(maxContentSize);
		else
			distribution.bestDivisor(maxContentSize, size, maxContentSize);
		return distribution;
	}

	private void bestDivisor(int condidate, int size, int maxContentSize) {
		do {
			if (condidate == 1)
				break;
			remainder = size % --condidate;
		} while ((condidate + remainder) > maxContentSize);
		divisor = condidate;
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

	/**
	 * @return the remainder
	 */
	public int getRemainder() {
		return remainder;
	}

	/**
	 * @param remainder
	 *            the remainder to set
	 */
	public void setRemainder(int remainder) {
		this.remainder = remainder;
	}

}
