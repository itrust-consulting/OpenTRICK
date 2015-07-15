package lu.itrust.business.TS.model.actionplan.summary.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.model.actionplan.summary.SummaryStage;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.NormalStandard;
import lu.itrust.business.TS.model.standard.measure.NormalMeasure;

/**
 * SummaryValues: <br>
 * This class has all data to represent the action plan summary. It is used to store values between
 * stages.
 * 
 * @author itrust consulting s.a r.l. - SME,BJA
 * @version 0.1
 * @since 2012-10-17
 */
public class SummaryValues {

	/***********************************************************************************************
	 * Fields declaration
	 **********************************************************************************************/

	public Map<String, SummaryStandardHelper> conformanceHelper = new HashMap<String, SummaryStandardHelper>();
	
	/** The 27001 AnalysisStandard Object with all 27001 Measures */
	public NormalStandard standard27001 = null;

	/** The 27002 AnalysisStandard Object with all 27002 Measures */
	public NormalStandard standard27002 = null;

	/** The 27002 AnalysisStandard Object with all 27002 Measures */
	public NormalStandard standardCustom = null;
	
	/** Measures that are to use as 100% Implemented 27001 AnalysisStandard Measures */
	public List<NormalMeasure> conformance27001measures = new ArrayList<NormalMeasure>();

	/** Measures that are to use as 100% Implemented 27002 AnalysisStandard Measures */
	public List<NormalMeasure> conformance27002measures = new ArrayList<NormalMeasure>();
	
	/** Measures that are to use as 100% Implemented Custom AnalysisStandard Measures */
	public List<NormalMeasure> conformanceCustommeasures = new ArrayList<NormalMeasure>();
	
	public SummaryStage previousStage = null;
	
	/** Number of Measures per Stage */
	public int measureCount = 0;

	/** Number of implemented measures (100%) */
	public int implementedCount = 0;

	/** Last ALE at the End of the Stage */
	public double totalALE = 0;
	
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

	/** Sum of Internal MaintenanceRecurrentInvestment to the Last Stage Entry */
	public double internalMaintenance = 0;
	
	/** Sum of External MaintenanceRecurrentInvestment to the Last Stage Entry */
	public double externalMaintenance = 0;
	
	/** Sum of recurrent investment to the Last Stage Entry */
	public double recurrentInvestment = 0;
	
	/** Reccurent Cost of Stage */
	public double recurrentCost = 0;

	/** Total Cost of Stage */
	public double totalCost = 0;

	/** Total implement cost of phase */
	public double implementCostOfPhase = 0;
	
	public SummaryValues(List<AnalysisStandard> standards) {
		conformanceHelper.clear();
		for(AnalysisStandard an : standards)
			conformanceHelper.put(an.getStandard().getLabel(), new SummaryStandardHelper(an));
	}
}