package lu.itrust.business.ts.model.actionplan.summary;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.ColumnDefault;

import freemarker.template.utility.NullArgumentException;
import lu.itrust.business.ts.model.standard.AnalysisStandard;


/**
 * Represents a summary of standard conformance for an action plan.
 * This class is used to store information about the conformance level and the number of not compliant measures for a specific analysis standard.
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "ActionPlanSummaryStandardConformance")
public class SummaryStandardConformance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idActionPlanSummaryStandardConformance")
	private int id = 0;

	@ManyToOne
	@JoinColumn(name = "fiAnalysisStandard", nullable = false)
	private AnalysisStandard analysisStandard = null;

	@Column(name = "dtConformance", nullable = false)
	private double conformance = 0;

	@ColumnDefault(value = "0")
	@Column(name = "dtNotCompliantMeasureCount", nullable = false)
	private int notCompliantMeasureCount = 0;

	/**
	 * Constructor: <br>
	 *
	 */
	public SummaryStandardConformance() {
	}

	/**
	 * Constructor: <br>
	 *
	 * @param analysisStandard
	 * @param conformance
	 */
	public SummaryStandardConformance(AnalysisStandard analysisStandard, double conformance, int notCompliantMeasureCount) {
		if (analysisStandard == null)
			throw new NullArgumentException("analysisStandard");
		this.conformance = conformance;
		this.analysisStandard = analysisStandard;
		this.notCompliantMeasureCount = notCompliantMeasureCount;
	}

	/**
	 * getId: <br>
	 * Returns the id field value.
	 *
	 * @return The value of the id field
	 */
	public int getId() {
		return id;
	}

	/**
	 * setId: <br>
	 * Sets the Field "id" with a value.
	 *
	 * @param id The Value to set the id field
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * getAnalysisStandard: <br>
	 * Description
	 *
	 * @return
	 */
	public AnalysisStandard getAnalysisStandard() {
		return analysisStandard;
	}

	/**
	 * setAnalysisStandard: <br>
	 * Description
	 *
	 * @param analysisStandard
	 */
	public void setAnalysisStandard(AnalysisStandard analysisStandard) {
		if (analysisStandard == null)
			throw new NullArgumentException("analysisStandard");
		this.analysisStandard = analysisStandard;
	}

	/**
	 * getConformance: <br>
	 * Returns the conformance field value.
	 *
	 * @return The value of the conformance field
	 */
	public double getConformance() {
		return conformance;
	}

	/**
	 * setConformance: <br>
	 * Sets the Field "conformance" with a value.
	 *
	 * @param conformance The Value to set the conformance field
	 */
	public void setConformance(double conformance) {
		this.conformance = conformance;
	}

	/**
	 * @return the notCompliantMeasureCount
	 */
	public int getNotCompliantMeasureCount() {
		return notCompliantMeasureCount;
	}

	/**
	 * @param notCompliantMeasureCount the notCompliantMeasureCount to set
	 */
	public void setNotCompliantMeasureCount(int notCompliantMeasureCount) {
		this.notCompliantMeasureCount = notCompliantMeasureCount;
	}

}
