package lu.itrust.business.TS.model.cssf.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.Destroyable;

import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.cssf.RiskRegisterItem;
import lu.itrust.business.TS.model.parameter.ExtendedParameter;
import lu.itrust.business.TS.model.parameter.helper.value.ValueFactory;

public class ComputationHelper implements Destroyable {

	private boolean destroyed = false;

	// initialise ALE Array (this array will contain all net ALE's of each
	// Risk)
	// Integer: Scenario ID, Double: ALE of the Scenario, calculated by
	// Inet*Pnet)
	private Map<String, Double> netALEs = new HashMap<String, Double>();

	// initialise a empty map that contains Impact objects
	private Map<String, Impact> impacts = new HashMap<String, Impact>();

	// initialise the risk register as an empty list of risk register items
	private Map<String, RiskRegisterItem> riskRegisters = new HashMap<String, RiskRegisterItem>();

	// create an empty list for probability relative impacts
	private Map<String, double[]> probabilityRelativeImpacts = new HashMap<String, double[]>();

	// initialise a map for the rawALEs
	private Map<String, Double> rawALEs = new HashMap<String, Double>();

	// initialise an empty map for delta ALEs
	private Map<String, Double> deltaALEs = new HashMap<String, Double>();
	
	private ValueFactory factory;
	
	public ComputationHelper(List<ExtendedParameter> parameters) {
		setFactory(new ValueFactory(parameters));
	}

	/**
	 * @return the netALEs
	 */
	public Map<String, Double> getNetALEs() {
		if (destroyed)
			throw new TrickException("error.use.destroyed.object", "Data cannot be used");
		return netALEs;
	}

	/**
	 * @param netALEs
	 *            the netALEs to set
	 */
	public void setNetALEs(Map<String, Double> netALEs) {
		if (destroyed)
			throw new TrickException("error.use.destroyed.object", "Data cannot be used");
		this.netALEs = netALEs;
	}

	/**
	 * @return the impacts
	 */
	public Map<String, Impact> getImpacts() {
		if (destroyed)
			throw new TrickException("error.use.destroyed.object", "Data cannot be used");
		return impacts;
	}

	/**
	 * @param impacts
	 *            the impacts to set
	 */
	public void setImpacts(Map<String, Impact> impacts) {
		if (destroyed)
			throw new TrickException("error.use.destroyed.object", "Data cannot be used");
		this.impacts = impacts;
	}

	/**
	 * @return the riskRegisters
	 */
	public Map<String, RiskRegisterItem> getRiskRegisters() {
		if (destroyed)
			throw new TrickException("error.use.destroyed.object", "Data cannot be used");
		return riskRegisters;
	}

	/**
	 * @param riskRegisters
	 *            the riskRegisters to set
	 */
	public void setRiskRegisters(Map<String, RiskRegisterItem> riskRegisters) {
		if (destroyed)
			throw new TrickException("error.use.destroyed.object", "Data cannot be used");
		this.riskRegisters = riskRegisters;
	}

	/**
	 * @return the probabilityRelativeImpacts
	 */
	public Map<String, double[]> getProbabilityRelativeImpacts() {
		if (destroyed)
			throw new TrickException("error.use.destroyed.object", "Data cannot be used");
		return probabilityRelativeImpacts;
	}

	/**
	 * @param probabilityRelativeImpacts
	 *            the probabilityRelativeImpacts to set
	 */
	public void setProbabilityRelativeImpacts(Map<String, double[]> probabilityRelativeImpacts) {
		if (destroyed)
			throw new TrickException("error.use.destroyed.object", "Data cannot be used");
		this.probabilityRelativeImpacts = probabilityRelativeImpacts;
	}

	/**
	 * @return the rawALEs
	 */
	public Map<String, Double> getRawALEs() {
		if (destroyed)
			throw new TrickException("error.use.destroyed.object", "Data cannot be used");
		return rawALEs;
	}

	/**
	 * @param rawALEs
	 *            the rawALEs to set
	 */
	public void setRawALEs(Map<String, Double> rawALEs) {
		if (destroyed)
			throw new TrickException("error.use.destroyed.object", "Data cannot be used");
		this.rawALEs = rawALEs;
	}

	/**
	 * @return the deltaALEs
	 */
	public Map<String, Double> getDeltaALEs() {
		if (destroyed)
			throw new TrickException("error.use.destroyed.object", "Data cannot be used");
		return deltaALEs;
	}

	/**
	 * @param deltaALEs
	 *            the deltaALEs to set
	 */
	public void setDeltaALEs(Map<String, Double> deltaALEs) {
		if (destroyed)
			throw new TrickException("error.use.destroyed.object", "Data cannot be used");
		this.deltaALEs = deltaALEs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.security.auth.Destroyable#destroy()
	 */
	@Override
	public void destroy(){
		if (isDestroyed())
			return;
		synchronized (this) {
			if (isDestroyed())
				return;
			destroyed = true;
			deltaALEs.clear();
			impacts.clear();
			riskRegisters.clear();
			probabilityRelativeImpacts.clear();
			rawALEs.clear();
			deltaALEs.clear();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.security.auth.Destroyable#isDestroyed()
	 */
	@Override
	public boolean isDestroyed() {
		return destroyed;
	}

	/**
	 * @return the factory
	 */
	public ValueFactory getFactory() {
		return factory;
	}

	/**
	 * @param factory the factory to set
	 */
	public void setFactory(ValueFactory factory) {
		this.factory = factory;
	}

	

}
