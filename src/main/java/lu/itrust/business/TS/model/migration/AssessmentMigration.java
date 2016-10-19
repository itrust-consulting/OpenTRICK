/**
 * 
 */
package lu.itrust.business.TS.model.migration;

import java.util.Map;

/**
 * @author eomar
 *
 */
public class AssessmentMigration {

	private int id;

	private String impactFin;

	private String impactRep;

	private String impactLeg;

	private String impactOp;

	/**
	 * 
	 */
	public AssessmentMigration() {
	}

	/**
	 * @param id
	 * @param impactFin
	 * @param impactRep
	 * @param impactLeg
	 * @param impactOp
	 */
	public AssessmentMigration(int id, String impactFin, String impactRep, String impactLeg, String impactOp) {
		this.setId(id);
		this.setImpactFin(impactFin);
		this.setImpactRep(impactRep);
		this.setImpactLeg(impactLeg);
		this.setImpactOp(impactOp);
	}

	public AssessmentMigration(Map<String, Object> data) {
		this((int) data.get("idAssessment"), (String) data.get("dtImpactFin"), (String) data.get("dtImpactRep"), (String) data.get("dtImpactLeg"), (String) data.get("dtImpactOp"));
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the impactFin
	 */
	public String getImpactFin() {
		return impactFin;
	}

	/**
	 * @param impactFin
	 *            the impactFin to set
	 */
	public void setImpactFin(String impactFin) {
		this.impactFin = impactFin;
	}

	/**
	 * @return the impactRep
	 */
	public String getImpactRep() {
		return impactRep;
	}

	/**
	 * @param impactRep
	 *            the impactRep to set
	 */
	public void setImpactRep(String impactRep) {
		this.impactRep = impactRep;
	}

	/**
	 * @return the impactLeg
	 */
	public String getImpactLeg() {
		return impactLeg;
	}

	/**
	 * @param impactLeg
	 *            the impactLeg to set
	 */
	public void setImpactLeg(String impactLeg) {
		this.impactLeg = impactLeg;
	}

	/**
	 * @return the impactOp
	 */
	public String getImpactOp() {
		return impactOp;
	}

	/**
	 * @param impactOp
	 *            the impactOp to set
	 */
	public void setImpactOp(String impactOp) {
		this.impactOp = impactOp;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AssessmentMigration [id=" + id + ", impactFin=" + impactFin + ", impactRep=" + impactRep + ", impactLeg=" + impactLeg + ", impactOp=" + impactOp + "]";
	}
	
	
}
