package lu.itrust.business.TS.actionplan;

import java.util.ArrayList;
import java.util.List;
import lu.itrust.business.TS.MeasureNorm;
import lu.itrust.business.TS.NormMeasure;

/**
 * SummaryValues: <br>
 * This class has all data to represent the action plan summary. It is used to store values between
 * stages.
 * 
 * @author itrust consulting s.à r.l. - SME,BJA
 * @version 0.1
 * @since 2012-10-17
 */
public class SummaryValues {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	/** The 27001 AnalysisNorm Object with all 27001 Measures */
	public MeasureNorm norm27001 = null;

	/** The 27002 AnalysisNorm Object with all 27002 Measures */
	public MeasureNorm norm27002 = null;

	/** Measures that are to use as 100% Implemented 27001 AnalysisNorm Measures */
	public List<NormMeasure> conformance27001measures = new ArrayList<NormMeasure>();

	/** Measures that are to use as 100% Implemented 27002 AnalysisNorm Measures */
	public List<NormMeasure> conformance27002measures = new ArrayList<NormMeasure>();

	/** Number of Measures per Stage */
	public int measureCount = 0;

	/** Number of Measures in AnalysisNorm 27001 */
	public int measureCount27001 = 0;

	/** Number of Measures in AnalysisNorm 27002 */
	public int measureCount27002 = 0;

	/** Number of implemented measures (100%) */
	public int implementedCount = 0;

	/** Last ALE at the End of the Stage */
	public double totalALE = 0;

	/** Calculation Variable for AnalysisNorm 27001 Conformance */
	public double conf27001 = 0;

	/** Calculation Variable for AnalysisNorm 27002 Conformance */
	public double conf27002 = 0;

	/** Risk Reduction at End of Stage */
	public double deltaALE = 0;

	/** Cost of Measures to the End of Stage (SUM) */
	public double measureCost = 0;

	/** ROSI at the Last Stage Entry */
	public double ROSI = 0;

	/** Relatvive ROSI at the End of the Stage */
	public double relativeROSI = 0;

	/** Sum of Internal Workloads to the Last Stage Entry */
	public double internalWorkload = 0;

	/** Sum of External Workloads to the Last Stage Entry */
	public double externalWorkload = 0;

	/** Sum of Investments to the Last Stage Entry */
	public double investment = 0;

	/** Sum of Internal Maintenance to the Last Stage Entry */
	public double internalMaintenance = 0;

	/** Sum of External Maintenance to the Last Stage Entry */
	public double externalMaintenance = 0;

	/** Reccurent Cost of Stage */
	public double recurrentCost = 0;

	/** Total Cost of Stage */
	public double totalCost = 0;
}