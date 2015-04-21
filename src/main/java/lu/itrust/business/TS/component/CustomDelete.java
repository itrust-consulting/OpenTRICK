/**
 * 
 */
package lu.itrust.business.TS.component;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import lu.itrust.business.TS.database.dao.DAOActionPlan;
import lu.itrust.business.TS.database.dao.DAOActionPlanSummary;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOAnalysisStandard;
import lu.itrust.business.TS.database.dao.DAOAssessment;
import lu.itrust.business.TS.database.dao.DAOAsset;
import lu.itrust.business.TS.database.dao.DAOAssetTypeValue;
import lu.itrust.business.TS.database.dao.DAOCustomer;
import lu.itrust.business.TS.database.dao.DAOMeasure;
import lu.itrust.business.TS.database.dao.DAOMeasureDescription;
import lu.itrust.business.TS.database.dao.DAOMeasureDescriptionText;
import lu.itrust.business.TS.database.dao.DAOResetPassword;
import lu.itrust.business.TS.database.dao.DAORiskRegister;
import lu.itrust.business.TS.database.dao.DAOScenario;
import lu.itrust.business.TS.database.dao.DAOStandard;
import lu.itrust.business.TS.database.dao.DAOUser;
import lu.itrust.business.TS.database.dao.DAOUserAnalysisRight;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.model.actionplan.summary.SummaryStage;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.helper.AnalysisComparator;
import lu.itrust.business.TS.model.analysis.rights.UserAnalysisRight;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.general.AssetTypeValue;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.model.general.helper.LogAction;
import lu.itrust.business.TS.model.scenario.Scenario;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.Standard;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescription;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescriptionText;
import lu.itrust.business.TS.usermanagement.ResetPassword;
import lu.itrust.business.TS.usermanagement.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author eom
 * 
 */
@Component
public class CustomDelete {

	@Autowired
	private DAOAssessment daoAssessment;

	@Autowired
	private DAOAsset daoAsset;

	@Autowired
	private DAOStandard daoStandard;

	@Autowired
	private DAOMeasure daoMeasure;

	@Autowired
	private DAOMeasureDescription daoMeasureDescription;

	@Autowired
	private DAOMeasureDescriptionText daoMeasureDescriptionText;

	@Autowired
	private DAOUserAnalysisRight daoUserAnalysisRight;

	@Autowired
	private DAOScenario daoScenario;

	@Autowired
	private DAOCustomer daoCustomer;

	@Autowired
	private DAOAnalysis daoAnalysis;

	@Autowired
	private DAOAnalysisStandard daoAnalysisStandard;

	@Autowired
	private DAOUser daoUser;

	@Autowired
	private DAOResetPassword daoResetPassword;

	@Autowired
	private DAOActionPlan daoActionPlan;

	@Autowired
	private DAOActionPlanSummary daoActionPlanSummary;

	@Autowired
	private DAOAssetTypeValue daoAssetTypeValue;

	@Autowired
	private DAORiskRegister daoRiskRegister;

	@Transactional
	public void deleteAsset(Asset asset) throws Exception {

		if (asset.isSelected()) {

			Analysis analysis = daoAnalysis.get(daoAsset.getAnalysisIdFromAsset(asset.getId()));

			if (analysis == null)
				throw new Exception("Could not retrieve analysis!");

			for (ActionPlanEntry actionplanentry : analysis.getActionPlans())
				daoActionPlan.delete(actionplanentry);

			for (SummaryStage stage : analysis.getSummaries())
				daoActionPlanSummary.delete(stage);

		}

		for (Assessment assessment : daoAssessment.getAllFromAsset(asset))
			daoAssessment.delete(assessment);

		daoAsset.delete(asset);
	}

	@Transactional
	public void deleteScenario(Scenario scenario) throws Exception {

		if (scenario.isSelected()) {

			Analysis analysis = daoAnalysis.get(daoScenario.getAnalysisIdFromScenario(scenario.getId()));

			if (analysis == null)
				throw new Exception("Could not retrieve analysis!");

			for (ActionPlanEntry actionplanentry : analysis.getActionPlans())
				daoActionPlan.delete(actionplanentry);

			for (SummaryStage stage : analysis.getSummaries())
				daoActionPlanSummary.delete(stage);

		}

		for (Assessment assessment : daoAssessment.getAllFromScenario(scenario))
			daoAssessment.delete(assessment);

		daoScenario.delete(scenario);
	}

	@Transactional
	public void deleteStandard(Standard standard) throws Exception {
		if (daoAnalysisStandard.getAllFromStandard(standard).size() > 0)
			throw new TrickException("error.delete.norm.analyses_with_norm", "Standard could not be deleted: it is used in analyses!");

		List<MeasureDescription> measureDescriptions = daoMeasureDescription.getAllByStandard(standard);
		for (MeasureDescription measureDescription : measureDescriptions) {
			List<MeasureDescriptionText> measureDescriptionTexts = daoMeasureDescriptionText.getAllFromMeasureDescription(measureDescription.getId());
			for (MeasureDescriptionText measureDescriptiontext : measureDescriptionTexts) {
				daoMeasureDescriptionText.delete(measureDescriptiontext);
			}
			daoMeasureDescription.delete(measureDescription);
		}
		daoStandard.delete(standard);

	}

	@Transactional
	public void deleteDuplicationAssetTypeValue(List<Scenario> scenarios) throws Exception {
		for (Scenario scenario : scenarios) {
			List<AssetTypeValue> assetTypeValues = scenario.deleteAssetTypeDuplication();
			daoScenario.saveOrUpdate(scenario);
			daoAssetTypeValue.delete(assetTypeValues);
		}
	}

	@Transactional
	public void deleteCustomer(int idcustomer, String username) throws Exception {
		Customer customer = daoCustomer.get(idcustomer);
		if (customer == null)
			throw new TrickException("error.customer.not_found", "Customer cannot be found");
		if (!customer.isCanBeUsed())
			throw new TrickException("error.customer.delete.profile", "Customer Profile cannot be deleted");
		if (daoCustomer.isInUsed(customer))
			throw new TrickException("error.delete.customer.has_analyses", "Customer could not be deleted: there are still analyses of this customer!");
		List<User> users = daoUser.getAllFromCustomer(customer);
		for (User user : users) {
			user.getCustomers().remove(customer);
			daoUser.saveOrUpdate(user);
		}
		daoCustomer.delete(customer);
		TrickLogManager.Persist(LogLevel.WARNING, LogType.ANALYSIS, "log.delete.customer", String.format("Customer: %s", customer.getOrganisation()), username, LogAction.DELETE,
				customer.getOrganisation());

	}

	@Transactional
	public void removeCustomerByUser(int customerId, String userName, String adminUsername) throws Exception {
		Customer customer = daoCustomer.get(customerId);
		if (customer == null || !customer.isCanBeUsed())
			return;
		User user = daoUser.get(userName);
		if (user == null)
			return;
		List<Analysis> analyses = daoAnalysis.getAllFromUserAndCustomer(userName, customer.getId());

		for (Analysis analysis : analyses) {
			analysis.removeRights(user);
			daoAnalysis.saveOrUpdate(analysis);
		}

		if (!(user.containsCustomer(customer) && user.getCustomers().remove(customer)))
			return;
		daoUser.saveOrUpdate(user);
		/**
		 * Log
		 */
		TrickLogManager.Persist(LogLevel.WARNING, LogType.ANALYSIS, "log.remove.access.to.customer",
				String.format("Customer: %s, target: %s", customer.getOrganisation(), user.getLogin()), adminUsername, LogAction.REMOVE_ACCESS, customer.getOrganisation(),
				user.getLogin());
	}

	@Transactional
	public void delete(MeasureDescription measureDescription) throws Exception {
		Iterator<MeasureDescriptionText> iterator = measureDescription.getMeasureDescriptionTexts().iterator();
		while (iterator.hasNext()) {
			MeasureDescriptionText descriptionText = iterator.next();
			iterator.remove();
			descriptionText.setMeasureDescription(null);
			daoMeasureDescriptionText.delete(descriptionText);
		}
		daoMeasureDescription.delete(measureDescription);
	}

	@Transactional
	public void deleteAnalysisMeasure(Integer analysisID, MeasureDescription measureDescription) throws Exception {

		List<AnalysisStandard> astandards = daoAnalysisStandard.getAllFromStandard(measureDescription.getStandard());

		for (AnalysisStandard astandard : astandards) {

			Measure mes = null;

			for (Measure measure : astandard.getMeasures()) {
				if (measure.getMeasureDescription().equals(measureDescription)) {
					mes = measure;
					break;
				}
			}

			List<ActionPlanEntry> entries = daoActionPlan.getAllFromAnalysis(analysisID);

			boolean contains = false;

			for (ActionPlanEntry entry : entries)
				if (entry.getMeasure().equals(mes)) {
					contains = true;
					break;
				}
			if (contains)
				daoActionPlan.deleteAllFromAnalysis(analysisID);

			astandard.getMeasures().remove(mes);

			daoMeasure.delete(mes);

			daoAnalysisStandard.saveOrUpdate(astandard);
		}

		Iterator<MeasureDescriptionText> iterator = measureDescription.getMeasureDescriptionTexts().iterator();
		while (iterator.hasNext()) {
			MeasureDescriptionText descriptionText = iterator.next();
			iterator.remove();
			descriptionText.setMeasureDescription(null);
			daoMeasureDescriptionText.delete(descriptionText);
		}
		daoMeasureDescription.delete(measureDescription);
	}

	@Transactional
	public boolean deleteAnalysis(List<Integer> ids, String username) {
		try {
			List<Analysis> analyses = daoAnalysis.getAll(ids);
			Collections.sort(analyses, new AnalysisComparator().reversed());
			for (Analysis analysis : analyses)
				deleteAnalysis(analysis, username);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	protected void deleteAnalysis(Analysis analysis, String username) throws Exception {

		List<String> versions = daoAnalysis.getAllNotEmptyVersion(analysis.getIdentifier());
		Comparator<String> comparator = new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return GeneralComperator.VersionComparator(o1, o2);
			}
		};

		Collections.sort(versions, Collections.reverseOrder(comparator));
		if (!versions.isEmpty())
			versions.remove(0);

		Analysis lastVersion = null;
		if (!versions.isEmpty()) {
			lastVersion = daoAnalysis.getFromIdentifierVersionCustomer(analysis.getIdentifier(), versions.get(0), analysis.getCustomer().getId());
			if (lastVersion != null) {
				for (UserAnalysisRight userAnalysisRight : analysis.getUserRights()) {
					UserAnalysisRight analysisRight = lastVersion.getRightsforUser(userAnalysisRight.getUser());
					if (analysisRight != null)
						analysisRight.setRight(userAnalysisRight.getRight());
				}
			}
		}

		daoAnalysis.delete(analysis);
		/**
		 * Log
		 */
		TrickLogManager.Persist(LogLevel.WARNING, LogType.ANALYSIS, "log.delete.analysis",
				String.format("Analysis: %s, version: %s", analysis.getIdentifier(), analysis.getVersion()), username, LogAction.DELETE, analysis.getIdentifier(),
				analysis.getVersion());
		if (lastVersion != null)
			daoAnalysis.saveOrUpdate(lastVersion);
		else
			customDeleteEmptyAnalysis(analysis.getIdentifier(), username);
	}

	@Transactional
	public void deleteAnalysis(int idAnalysis, String username) throws Exception {
		deleteAnalysis(daoAnalysis.get(idAnalysis), username);
	}

	@Transactional
	public void deleteUser(User user) throws Exception {
		user.disable();
		ResetPassword resetPassword = daoResetPassword.get(user);
		if (resetPassword != null)
			daoResetPassword.delete(resetPassword);
		user.getCustomers().clear();
		daoUser.saveOrUpdate(user);
		daoUser.delete(user.getId());
	}

	@Transactional
	public void customDeleteEmptyAnalysis(String identifier, String username) throws Exception {
		List<Analysis> analyses = daoAnalysis.getAllByIdentifier(identifier);
		if (analyses.stream().anyMatch(analysis -> analysis.hasData()))
			return;
		Collections.sort(analyses, Collections.reverseOrder(new AnalysisComparator()));
		for (Analysis analysis : analyses) {
			daoAnalysis.delete(analysis);
			/**
			 * Log
			 */
			TrickLogManager.Persist(LogLevel.WARNING, LogType.ANALYSIS, "log.delete.analysis",
					String.format("Analysis: %s, version: %s", analysis.getIdentifier(), analysis.getVersion()), username, LogAction.DELETE, analysis.getIdentifier(),
					analysis.getVersion());
		}
	}

}
