package lu.itrust.business.TS.component;

import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOParameter;
import lu.itrust.business.TS.database.dao.DAOParameterType;
import lu.itrust.business.TS.database.service.ServiceExternalNotification;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.parameter.DynamicParameter;
import lu.itrust.business.TS.model.parameter.ParameterType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class DynamicParameterComputer {
	@Autowired
	private DAOAnalysis daoAnalysis;

	@Autowired
	private DAOParameter daoParameter;

	@Autowired
	private DAOParameterType daoParameterType;
	
	@Autowired
	private ServiceExternalNotification serviceExternalNotification;
	
	/**
	 * Computes all dynamic parameters for all analyses for the given user.
	 * @param userName The name of the user to compute the dynamic parameters for.
	 * @param timespan The time span over which all notifications shall be considered in the computation of the dynamic parameter.
	 */
	public void computeForAllAnalysesOfUser(String userName, long timespan) throws Exception {
		// Fetch all analyses which the user can access
		List<Analysis> analyses = daoAnalysis.getFromUserNameAndNotEmpty("admin", AnalysisRight.highRightFrom(AnalysisRight.MODIFY));

		// Fetch the 'DYNAMIC' parameter type or create it, if if does not exist yet/anymore
		ParameterType dynamicParameterType = daoParameterType.getByName(Constant.PARAMETERTYPE_TYPE_DYNAMIC_NAME);
		if (dynamicParameterType == null) {
			dynamicParameterType = new ParameterType(Constant.PARAMETERTYPE_TYPE_DYNAMIC_NAME);
			dynamicParameterType.setId(Constant.PARAMETERTYPE_TYPE_DYNAMIC);
		}
		
		final long maxTimestamp = java.time.Instant.now().getEpochSecond(); // now
		final long minTimestamp = maxTimestamp - timespan;
		// All frequencies within TRICK service are to be understood with respect to 1 year.
		// 1 year is defined here to be 365 days, which is not entirely correct,
		// but the value does not have to be that precise anyway.
		final double unitDuration = 86400 * 365;
		
		// Determine frequency of each notification category in the database
		Map<String, Double> frequencies = serviceExternalNotification.getAllFrequencies(minTimestamp, maxTimestamp, unitDuration);

		// Deduce parameter values for each analysis
		for (Analysis analysis : analyses) {
			// Fetch instance of all (existing) dynamic parameters
			// and map them by their acronym
			Map<String, DynamicParameter> dynamicParameters = analysis.findDynamicParametersByAnalysisAsMap();
			
			// Make sure that there is a frequency value for each parameter.
			// If there is none, it exactly means that the frequency is zero.
			for (String acronym : dynamicParameters.keySet())
				frequencies.putIfAbsent(acronym, 0.0);

			// Now every parameter has an associated frequency value.
			// For each computed frequency:
			// - update existing dynamic parameters with the respective value in the frequencies collection; or
			// - create parameter if none exists.
			for (String acronym : frequencies.keySet()) {
				DynamicParameter newParameter = dynamicParameters.get(acronym);
				if (newParameter == null) {
					newParameter = new DynamicParameter();
					newParameter.setAcronym(acronym);
					newParameter.setDescription("dynamic:" + acronym);
					newParameter.setType(dynamicParameterType);
					analysis.getParameters().add(newParameter);
				}
				newParameter.setValue(frequencies.get(acronym));
			}

			// Save everything
			daoAnalysis.saveOrUpdate(analysis);
		}
		
	}
}
