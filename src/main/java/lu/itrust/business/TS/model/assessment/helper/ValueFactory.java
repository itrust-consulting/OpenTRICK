/**
 * 
 */
package lu.itrust.business.TS.model.assessment.helper;

import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.model.parameter.DynamicParameter;
import lu.itrust.business.TS.model.parameter.ExtendedParameter;

/**
 * @author eomar
 *
 */
public class ValueFactory {

	private List<ExtendedParameter> operationalImpacts;

	private List<ExtendedParameter> reputationImpacts;

	private List<ExtendedParameter> financialImpacts;

	private List<ExtendedParameter> legalImpacts;

	private List<DynamicParameter> dynamicParameters;

	private List<ExtendedParameter> probabilities;
	
	private Map<String, ExtendedParameter> operationalMapper;
	
	private Map<String, ExtendedParameter> reputationMapper;
	
	private Map<String, ExtendedParameter> financialMapper;
	
	private Map<String, ExtendedParameter> LegalMapper;
	
	private Map<String, ExtendedParameter> probabilityMapper;
	
	private Map<String, DynamicParameter> dynamicMapper;

	
	/**
	 * @return the operationalImpacts
	 */
	public List<ExtendedParameter> getOperationalImpacts() {
		return operationalImpacts;
	}

	/**
	 * @param operationalImpacts
	 *            the operationalImpacts to set
	 */
	public void setOperationalImpacts(List<ExtendedParameter> operationalImpacts) {
		this.operationalImpacts = operationalImpacts;
	}

	/**
	 * @return the reputationImpacts
	 */
	public List<ExtendedParameter> getReputationImpacts() {
		return reputationImpacts;
	}

	/**
	 * @param reputationImpacts
	 *            the reputationImpacts to set
	 */
	public void setReputationImpacts(List<ExtendedParameter> reputationImpacts) {
		this.reputationImpacts = reputationImpacts;
	}

	/**
	 * @return the financialImpacts
	 */
	public List<ExtendedParameter> getFinancialImpacts() {
		return financialImpacts;
	}

	/**
	 * @param financialImpacts
	 *            the financialImpacts to set
	 */
	public void setFinancialImpacts(List<ExtendedParameter> financialImpacts) {
		this.financialImpacts = financialImpacts;
	}

	/**
	 * @return the legalImpacts
	 */
	public List<ExtendedParameter> getLegalImpacts() {
		return legalImpacts;
	}

	/**
	 * @param legalImpacts
	 *            the legalImpacts to set
	 */
	public void setLegalImpacts(List<ExtendedParameter> legalImpacts) {
		this.legalImpacts = legalImpacts;
	}

	/**
	 * @return the dynamicParameters
	 */
	public List<DynamicParameter> getDynamicParameters() {
		return dynamicParameters;
	}

	/**
	 * @param dynamicParameters
	 *            the dynamicParameters to set
	 */
	public void setDynamicParameters(List<DynamicParameter> dynamicParameters) {
		this.dynamicParameters = dynamicParameters;
	}

	/**
	 * @return the probabilities
	 */
	public List<ExtendedParameter> getProbabilities() {
		return probabilities;
	}

	/**
	 * @param probabilities
	 *            the probabilities to set
	 */
	public void setProbabilities(List<ExtendedParameter> probabilities) {
		this.probabilities = probabilities;
	}

	/**
	 * @return the operationalMapper
	 */
	public Map<String, ExtendedParameter> getOperationalMapper() {
		return operationalMapper;
	}

	/**
	 * @param operationalMapper the operationalMapper to set
	 */
	public void setOperationalMapper(Map<String, ExtendedParameter> operationalMapper) {
		this.operationalMapper = operationalMapper;
	}

	/**
	 * @return the reputationMapper
	 */
	public Map<String, ExtendedParameter> getReputationMapper() {
		return reputationMapper;
	}

	/**
	 * @param reputationMapper the reputationMapper to set
	 */
	public void setReputationMapper(Map<String, ExtendedParameter> reputationMapper) {
		this.reputationMapper = reputationMapper;
	}

	/**
	 * @return the financialMapper
	 */
	public Map<String, ExtendedParameter> getFinancialMapper() {
		return financialMapper;
	}

	/**
	 * @param financialMapper the financialMapper to set
	 */
	public void setFinancialMapper(Map<String, ExtendedParameter> financialMapper) {
		this.financialMapper = financialMapper;
	}

	/**
	 * @return the legalMapper
	 */
	public Map<String, ExtendedParameter> getLegalMapper() {
		return LegalMapper;
	}

	/**
	 * @param legalMapper the legalMapper to set
	 */
	public void setLegalMapper(Map<String, ExtendedParameter> legalMapper) {
		LegalMapper = legalMapper;
	}

	/**
	 * @return the probabilityMapper
	 */
	public Map<String, ExtendedParameter> getProbabilityMapper() {
		return probabilityMapper;
	}

	/**
	 * @param probabilityMapper the probabilityMapper to set
	 */
	public void setProbabilityMapper(Map<String, ExtendedParameter> probabilityMapper) {
		this.probabilityMapper = probabilityMapper;
	}

	/**
	 * @return the dynamicMapper
	 */
	public Map<String, DynamicParameter> getDynamicMapper() {
		return dynamicMapper;
	}

	/**
	 * @param dynamicMapper the dynamicMapper to set
	 */
	public void setDynamicMapper(Map<String, DynamicParameter> dynamicMapper) {
		this.dynamicMapper = dynamicMapper;
	}

}
