package lu.itrust.business.TS.component;

import java.util.Map;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOIDS;
import lu.itrust.business.TS.database.dao.hbm.DAOAnalysisHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOAssessmentHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOAssetHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOIDSHBM;
import lu.itrust.business.TS.database.dao.hbm.DAORiskProfileHBM;
import lu.itrust.business.TS.database.dao.hbm.DAOScenarioHBM;
import lu.itrust.business.TS.database.service.ServiceExternalNotification;
import lu.itrust.business.TS.database.service.impl.ServiceExternalNotificationImpl;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.model.parameter.impl.DynamicParameter;

/**
 * Component which allows to compute the values of dynamic parameters.
 * 
 * @author Steve Muller (SMU), itrust consulting s.à r.l.
 * @since Jun 2015
 */
@Component
@Transactional
public class DynamicParameterComputer {

	@Autowired
	private DAOAnalysis daoAnalysis;

	@Autowired
	private ServiceExternalNotification serviceExternalNotification;

	@Autowired
	private AssessmentAndRiskProfileManager assessmentManager;

	@Autowired
	private DAOIDS daoIDS;

	public DynamicParameterComputer() {
	}

	public DynamicParameterComputer(Session session, AssessmentAndRiskProfileManager assessmentAndRiskProfileManager) {
		this(session, new DAOAnalysisHBM(session), assessmentAndRiskProfileManager);
	}

	public DynamicParameterComputer(Session session, DAOAnalysis daoAnalysis, AssessmentAndRiskProfileManager assessmentAndRiskProfileManager) {
		this.assessmentManager = assessmentAndRiskProfileManager.initialise(daoAnalysis, new DAOAssetHBM(session), new DAOAssessmentHBM(session), new DAORiskProfileHBM(session),
				new DAOScenarioHBM(session));
		this.daoAnalysis = daoAnalysis;
		this.serviceExternalNotification = new ServiceExternalNotificationImpl(session);
		this.daoIDS = new DAOIDSHBM(session);
	}

	/**
	 * Computes all dynamic parameters for all analyses for the given user.
	 * 
	 * @param userName
	 *            The name of the user to compute the dynamic parameters for.
	 */
	public void computeForAllAnalysesOfUser(String userName) throws Exception {
		// Fetch all analyses which the user can access
		daoIDS.get(userName).getSubscribers().parallelStream().forEach(analysis -> computeForAnalysisAndSource(userName, analysis));
	}

	/**
	 * Computes all dynamic parameters for the given analysis.
	 * 
	 * @param analysis
	 *            The analysis for which parameters shall be recomputed.
	 */
	public void computeForAnalysis(Analysis analysis) throws Exception {
		// Find all external sources (i.e. users) which provide dynamic
		// parameters
		// Compute dynamic parameters
		if (!analysis.isArchived())
			daoIDS.getPrefixesByAnalysisId(analysis.getId()).stream().forEach(prefix -> this.computeForAnalysisAndSource(prefix, analysis));
	}

	/**
	 * Computes all dynamic parameters for the given analysis and the given
	 * user.
	 * 
	 * @param userName
	 *            The name of the user to compute the dynamic parameters for.
	 * @param analysis
	 *            The analysis for which parameters shall be recomputed.
	 */
	private void computeForAnalysisAndSource(String userName, Analysis analysis) {
		if (analysis.isArchived())
			return;
		// Log
		TrickLogManager.Persist(LogType.ANALYSIS, "log.analysis.compute.dynamicparameters",
				String.format("Updating dynamic parameters for analysis: %s, version: %s", analysis.getIdentifier(), analysis.getVersion()), userName, LogAction.UPDATE,
				analysis.getIdentifier(), analysis.getVersion());

		// Get parameters
		/*
		final double minimumProbability = Math.max(0.0, analysis.findParameterValueByTypeAndAcronym(Constant.PARAMETERTYPE_TYPE_PROPABILITY_NAME, "p0"));
		*/
		final double minimumProbability = 0.0;

		/**
		 * The maximum timestamp for all notifications to consider. Points to
		 * NOW.
		 */
		final long now = java.time.Instant.now().getEpochSecond();

		// Compute likelihoods
		Map<String, Double> likelihoods = serviceExternalNotification.computeProbabilitiesAtTime(now, userName, minimumProbability);

		// Fetch instances of all (existing) dynamic parameters
		// and map them by their acronym
		Map<String, DynamicParameter> dynamicParameters = analysis.findDynamicParametersByAnalysisAsMap();

		// Now every parameter has an associated likelihood value.
		// For each computed frequency:
		// - update existing dynamic parameters with the respective value in the
		// frequencies collection; or
		// - create parameter if it does not exist.
		for (String parameterName : likelihoods.keySet()) {
			DynamicParameter parameter = dynamicParameters.get(parameterName);
			if (parameter == null) {
				// The description/label of dynamic parameters is never used
				// within TRICK service,
				// we will set a value nevertheless to ease the work for a
				// database maintainer. :-)
				analysis.add(parameter = new DynamicParameter(parameterName, String.format("dynamic:%s", parameterName)));
			}

			// Remove entry from parameter map so that we know it has been
			// handled
			dynamicParameters.remove(parameterName);

			// Set new parameter value to computed likelihood.
			// If the latter is not set, it exactly means that the likelihood is
			// zero.
			// NB: the user is free to enforce a minimum value by using the
			// max() function in his formula
			parameter.setValue(likelihoods.getOrDefault(parameterName, 0.0));
		}

		/*
		 * This is problematic if there are two reporting sources: one deletes
		 * the dynamic parameters of the other.
		 * 
		 * // Remove all parameters which are no longer needed // (these are all
		 * parameters which have not been removed from 'dynamicParameters') for
		 * (DynamicParameter dynamicParameter : dynamicParameters.values())
		 * daoParameter.delete(dynamicParameter);
		 */

		// Update assessment to reflect the new values of the dynamic parameters
		assessmentManager.updateAssessment(analysis, null);

		// Save everything
		daoAnalysis.saveOrUpdate(analysis);
	}
}
