package lu.itrust.business.TS;

/**
 * MaturityMeasure: <br>
 * This class represents the MaturityMeasure and its data
 * 
 * @author itrust consulting s.à r.l. - BJA,SME
 * @version 0.1
 * @since 2012-08-21
 */
public class MaturityMeasure extends Measure {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	/** The Reached Security Maturity Level */
	private int reachedLevel = 0;

	/** The Cost to get to the Security Maturity Level 1 */
	private double SML1Cost = 0;

	/** The Cost to get to the Security Maturity Level 2 */
	private double SML2Cost = 0;

	/** The Cost to get to the Security Maturity Level 3 */
	private double SML3Cost = 0;

	/** The Cost to get to the Security Maturity Level 4 */
	private double SML4Cost = 0;

	/** The Cost to get to the Security Maturity Level 5 */
	private double SML5Cost = 0;

	/***********************************************************************************************
	 * Getters and Setters
	 **********************************************************************************************/

	/**
	 * getReachedLevel: <br>
	 * Returns the "reachedLevel" field value
	 * 
	 * @return The reached SML
	 */
	public int getReachedLevel() {
		return reachedLevel;
	}

	/**
	 * setReachedLevel: <br>
	 * Sets the "reachedLevel" field with a value
	 * 
	 * @param reachedLevel
	 *            The value to set the Reached Value
	 */
	public void setReachedLevel(int reachedLevel) {
		if ((reachedLevel < 0) || (reachedLevel > 5)) {
			throw new IllegalArgumentException(
					"Maturity Measure ReachedLevel should be between 0 and 5");
		}
		this.reachedLevel = reachedLevel;
	}

	/**
	 * getSML1Cost: <br>
	 * Returns the "SML1Cost" field value
	 * 
	 * @return The Cost to reach the SML 1
	 */
	public double getSML1Cost() {
		return SML1Cost;
	}

	/**
	 * setSML1Cost: <br>
	 * Sets the "SML1Cost" field with a value
	 * 
	 * @param SML1Cost
	 *            The value to set the cost to reach SML 1
	 */
	public void setSML1Cost(double SML1Cost) {
		if (SML1Cost < 0) {
			throw new IllegalArgumentException(
					"Maturity Measure SML1Cost should be greater than or equal 0");
		}
		this.SML1Cost = SML1Cost;
	}

	/**
	 * getSML2Cost: <br>
	 * Returns the "SML2Cost" field value
	 * 
	 * @return The Cost to reach the SML 2
	 */
	public double getSML2Cost() {
		return SML2Cost;
	}

	/**
	 * setSML2Cost: <br>
	 * Sets the "SML2Cost" field with a value
	 * 
	 * @param SML2Cost
	 *            The value to set the cost to reach SML 2
	 */
	public void setSML2Cost(double SML2Cost) {
		if (SML2Cost < 0) {
			throw new IllegalArgumentException(
					"Maturity Measure SML2Cost should be greater than or equal 0");
		}
		this.SML2Cost = SML2Cost;
	}

	/**
	 * getSML3Cost: <br>
	 * Returns the "SML3Cost" field value
	 * 
	 * @return The Cost to reach the SML 3
	 */
	public double getSML3Cost() {
		return SML3Cost;
	}

	/**
	 * setSML3Cost: <br>
	 * Sets the "SML3Cost" field with a value
	 * 
	 * @param SML3Cost
	 *            The value to set the cost to reach SML 3
	 */
	public void setSML3Cost(double SML3Cost) {
		if (SML3Cost < 0) {
			throw new IllegalArgumentException(
					"Maturity Measure SML3Cost should be greater than or equal 0");
		}
		this.SML3Cost = SML3Cost;
	}

	/**
	 * getSML4Cost: <br>
	 * Returns the "SML4Cost" field value
	 * 
	 * @return The Cost to reach the SML 4
	 */
	public double getSML4Cost() {
		return SML4Cost;
	}

	/**
	 * setSML4Cost: <br>
	 * Sets the "SML4Cost" field with a value
	 * 
	 * @param SML4Cost
	 *            The value to set the cost to reach SML 4
	 */
	public void setSML4Cost(double SML4Cost) {
		if (SML4Cost < 0) {
			throw new IllegalArgumentException(
					"Maturity Measure SML4Cost should be greater than or equal 0");
		}
		this.SML4Cost = SML4Cost;
	}

	/**
	 * getSML5Cost: <br>
	 * Returns the "SML5Cost" field value
	 * 
	 * @return The Cost to reach the SML 5
	 */
	public double getSML5Cost() {
		return SML5Cost;
	}

	/**
	 * setSML5Cost: <br>
	 * Sets the "SML5Cost" field with a value
	 * 
	 * @param SML5Cost
	 *            The value to set the cost to reach SML 5
	 */
	public void setSML5Cost(double SML5Cost) {
		if (SML5Cost < 0) {
			throw new IllegalArgumentException(
					"Maturity Measure SML5Cost should be greater than or equal 0");
		}
		this.SML5Cost = SML5Cost;
	}

	/**
	 * getImplementationRate: <br>
	 * Returns the implementationRate field value (Parameter Object).
	 * 
	 * @return The Object of Parameter representing the implementation rate
	 * @see lu.itrust.business.TS.Measure#getImplementationRate()
	 */
	@Override
	public Parameter getImplementationRate() {
		return (Parameter) super.getImplementationRate();
	}

	/**
	 * setImplementationRate: <br>
	 * Sets the Field "implementationRate" with a Parameter object.
	 * 
	 * @param implementationRate
	 *            The Value to set the implementationRate field PArameter Object
	 * @see lu.itrust.business.TS.Measure#setImplementationRate(Object)
	 */
	@Override
	public void setImplementationRate(Object implementationRate) {
		if (!(implementationRate instanceof Parameter)) {
			System.out.println(implementationRate.getClass().toString());
			throw new IllegalArgumentException("Object needs to be of Type Parameter!");
		}
		super.setImplementationRate((Parameter) implementationRate);
	}

	/**
	 * getImplementationRateValue: <br>
	 * returns the Real Implementation Rate Value
	 * 
	 * @see lu.itrust.business.TS.Measure#getImplementationRateValue()
	 */
	@Override
	public double getImplementationRateValue() {
		return getImplementationRate().getValue();
	}
}