package lu.itrust.business.TS.actionplan;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lu.itrust.business.TS.AnalysisStandard;


/** SummaryStandardConformance.java: <br>
 * Detailed description...
 *
 * @author smenghi itrust consulting s.a.rl.:
 * @version 
 * @since Aug 26, 2014
 */
@Entity
@Table(name="ActionPlanSummaryStandardConformance")
public class SummaryStandardConformance {


	@Id @GeneratedValue 
	@Column(name="idActionPlanSummaryStandardConformance")
	private int id = -1;
	
	@ManyToOne 
	@JoinColumn(name="fiAnalysisStandard", nullable=false)
	private AnalysisStandard analysisStandard = null;
	
	@Column(name="dtConformance", nullable=false)
	private double conformance = 0;

	/**
	 * Constructor: <br>
	 *
	 */
	public SummaryStandardConformance() {	
	}
	
	/**
	 * Constructor: <br>
	 * @param norm
	 * @param conformance
	 */
	public SummaryStandardConformance(AnalysisStandard analysisStandard, double conformance) {
		this.analysisStandard = analysisStandard;
		this.conformance = conformance;
	}
	
	/** getId: <br>
	 * Returns the id field value.
	 * 
	 * @return The value of the id field
	 */
	public int getId() {
		return id;
	}

	/** setId: <br>
	 * Sets the Field "id" with a value.
	 * 
	 * @param id 
	 * 			The Value to set the id field
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** getAnalysisNorm: <br>
	 * Returns the analysisNorm field value.
	 * 
	 * @return The value of the analysisNorm field
	 */
	public AnalysisStandard getAnalysisStandard() {
		return analysisStandard;
	}

	/** setAnalysisNorm: <br>
	 * Sets the Field "analysisNorm" with a value.
	 * 
	 * @param analysisNorm 
	 * 			The Value to set the analysisNorm field
	 */
	public void setAnalysisStandard(AnalysisStandard analysisStandard) {
		this.analysisStandard = analysisStandard;
	}

	/** getConformance: <br>
	 * Returns the conformance field value.
	 * 
	 * @return The value of the conformance field
	 */
	public double getConformance() {
		return conformance;
	}

	/** setConformance: <br>
	 * Sets the Field "conformance" with a value.
	 * 
	 * @param conformance 
	 * 			The Value to set the conformance field
	 */
	public void setConformance(double conformance) {
		this.conformance = conformance;
	}
	
}
