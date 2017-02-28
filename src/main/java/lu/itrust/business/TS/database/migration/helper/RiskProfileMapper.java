/**
 * 
 */
package lu.itrust.business.TS.database.migration.helper;

/**
 * @author eomar
 *
 */
public class RiskProfileMapper {
	
	private Integer id;
	
	private Integer expFinancial;
	
	private Integer expLegal;
	
	private Integer expOperational;
	
	private Integer expReputational;
	
	private Integer expLikelihood;
	
	private Integer rawFinancial;
	
	private Integer rawLegal;
	
	private Integer rawOperational;
	
	private Integer rawReputational;
	
	private Integer rawLikelihood;

	/**
	 * 
	 */
	public RiskProfileMapper() {
		
	}

	

	/**
	 * @param id
	 * @param expFinancial
	 * @param expLegal
	 * @param expOperational
	 * @param expReputational
	 * @param expLikelihood
	 * @param rawFinancial
	 * @param rawLegal
	 * @param rawOperational
	 * @param rawReputational
	 * @param rawLikelihood
	 */
	public RiskProfileMapper(Integer id, Integer expFinancial, Integer expLegal, Integer expOperational, Integer expReputational, Integer expLikelihood, Integer rawFinancial, Integer rawLegal,
			Integer rawOperational, Integer rawReputational, Integer rawLikelihood) {
		this.setId(id);
		this.setExpFinancial(expFinancial);
		this.setExpLegal(expLegal);
		this.setExpOperational(expOperational);
		this.setExpReputational(expReputational);
		this.setExpLikelihood(expLikelihood);
		this.setRawFinancial(rawFinancial);
		this.setRawLegal(rawLegal);
		this.setRawOperational(rawOperational);
		this.setRawReputational(rawReputational);
		this.setRawLikelihood(rawLikelihood);
	}



	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}



	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}



	/**
	 * @return the expFinancial
	 */
	public Integer getExpFinancial() {
		return expFinancial;
	}



	/**
	 * @param expFinancial the expFinancial to set
	 */
	public void setExpFinancial(Integer expFinancial) {
		this.expFinancial = expFinancial;
	}



	/**
	 * @return the expLegal
	 */
	public Integer getExpLegal() {
		return expLegal;
	}



	/**
	 * @param expLegal the expLegal to set
	 */
	public void setExpLegal(Integer expLegal) {
		this.expLegal = expLegal;
	}



	/**
	 * @return the expOperational
	 */
	public Integer getExpOperational() {
		return expOperational;
	}



	/**
	 * @param expOperational the expOperational to set
	 */
	public void setExpOperational(Integer expOperational) {
		this.expOperational = expOperational;
	}



	/**
	 * @return the expReputational
	 */
	public Integer getExpReputational() {
		return expReputational;
	}



	/**
	 * @param expReputational the expReputational to set
	 */
	public void setExpReputational(Integer expReputational) {
		this.expReputational = expReputational;
	}



	/**
	 * @return the expLikelihood
	 */
	public Integer getExpLikelihood() {
		return expLikelihood;
	}



	/**
	 * @param expLikelihood the expLikelihood to set
	 */
	public void setExpLikelihood(Integer expLikelihood) {
		this.expLikelihood = expLikelihood;
	}



	/**
	 * @return the rawFinancial
	 */
	public Integer getRawFinancial() {
		return rawFinancial;
	}



	/**
	 * @param rawFinancial the rawFinancial to set
	 */
	public void setRawFinancial(Integer rawFinancial) {
		this.rawFinancial = rawFinancial;
	}



	/**
	 * @return the rawLegal
	 */
	public Integer getRawLegal() {
		return rawLegal;
	}



	/**
	 * @param rawLegal the rawLegal to set
	 */
	public void setRawLegal(Integer rawLegal) {
		this.rawLegal = rawLegal;
	}



	/**
	 * @return the rawOperational
	 */
	public Integer getRawOperational() {
		return rawOperational;
	}



	/**
	 * @param rawOperational the rawOperational to set
	 */
	public void setRawOperational(Integer rawOperational) {
		this.rawOperational = rawOperational;
	}



	/**
	 * @return the rawReputational
	 */
	public Integer getRawReputational() {
		return rawReputational;
	}



	/**
	 * @param rawReputational the rawReputational to set
	 */
	public void setRawReputational(Integer rawReputational) {
		this.rawReputational = rawReputational;
	}



	/**
	 * @return the rawLikelihood
	 */
	public Integer getRawLikelihood() {
		return rawLikelihood;
	}



	/**
	 * @param rawLikelihood the rawLikelihood to set
	 */
	public void setRawLikelihood(Integer rawLikelihood) {
		this.rawLikelihood = rawLikelihood;
	}




}
