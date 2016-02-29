/**
 * 
 */
package lu.itrust.business.TS.model.cssf;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import lu.itrust.business.TS.model.parameter.ExtendedParameter;

/**
 * @author eomar
 *
 */
@Embeddable
public class RiskProbaImpact {

	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	private ExtendedParameter probabitity;

	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	private ExtendedParameter impactRep;

	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	private ExtendedParameter impactOp;

	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	private ExtendedParameter impactLeg;

	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	private ExtendedParameter impactFin;

	/**
	 * @return the probabitity
	 */
	public ExtendedParameter getProbabitity() {
		return probabitity;
	}

	/**
	 * @param probabitity
	 *            the probabitity to set
	 */
	public void setProbabitity(ExtendedParameter probabitity) {
		this.probabitity = probabitity;
	}

	/**
	 * @return the impactRep
	 */
	public ExtendedParameter getImpactRep() {
		return impactRep;
	}

	/**
	 * @param impactRep
	 *            the impactRep to set
	 */
	public void setImpactRep(ExtendedParameter impactRep) {
		this.impactRep = impactRep;
	}

	/**
	 * @return the impactOp
	 */
	public ExtendedParameter getImpactOp() {
		return impactOp;
	}

	/**
	 * @param impactOp
	 *            the impactOp to set
	 */
	public void setImpactOp(ExtendedParameter impactOp) {
		this.impactOp = impactOp;
	}

	/**
	 * @return the impactLeg
	 */
	public ExtendedParameter getImpactLeg() {
		return impactLeg;
	}

	/**
	 * @param impactLeg
	 *            the impactLeg to set
	 */
	public void setImpactLeg(ExtendedParameter impactLeg) {
		this.impactLeg = impactLeg;
	}

	/**
	 * @return the impactFin
	 */
	public ExtendedParameter getImpactFin() {
		return impactFin;
	}

	/**
	 * @param impactFin
	 *            the impactFin to set
	 */
	public void setImpactFin(ExtendedParameter impactFin) {
		this.impactFin = impactFin;
	}

}
