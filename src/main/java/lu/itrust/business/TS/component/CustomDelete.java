/**
 * 
 */
package lu.itrust.business.TS.component;

import java.security.Principal;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import lu.itrust.business.TS.constants.Constant;
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
import lu.itrust.business.TS.database.dao.DAOUserSqLite;
import lu.itrust.business.TS.database.dao.DAOWordReport;
import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.helper.AnalysisComparator;
import lu.itrust.business.TS.model.analysis.rights.UserAnalysisRight;
import lu.itrust.business.TS.model.assessment.Assessment;
import lu.itrust.business.TS.model.asset.Asset;
import lu.itrust.business.TS.model.general.AssetTypeValue;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;
import lu.itrust.business.TS.model.scenario.Scenario;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.Standard;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measure.NormalMeasure;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescription;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescriptionText;
import lu.itrust.business.TS.usermanagement.ResetPassword;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.TS.usermanagement.helper.UserDeleteHelper;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
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

	@Autowired
	private DAOWordReport daoWordReport;

	@Autowired
	private DAOUserSqLite daoUserSqLite;

	@Transactional
	public void deleteAsset(int idAsset, int idAnalysis) throws Exception {
		Asset asset = daoAsset.getFromAnalysisById(idAnalysis, idAsset);
		Analysis analysis = daoAnalysis.get(idAnalysis);
		if (analysis == null)
			throw new TrickException("error.analysis.not_found", "Analysis cannot be found");
		else if (asset == null)
			throw new TrickException("error.asset.not_found", "Asset cannot be found");

		deleteActionPlanRiskRegisterAssessment(analysis, analysis.removeAssessment(asset));

		daoAsset.delete(asset);
	}

	@Transactional
	public void deleteScenario(int idScenario, int idAnalysis) throws Exception {
		Scenario scenario = daoScenario.getFromAnalysisById(idAnalysis, idScenario);
		Analysis analysis = daoAnalysis.get(idAnalysis);

		if (analysis == null)
			throw new TrickException("error.analysis.not_found", "Analysis cannot be found");
		else if (scenario == null)
			throw new TrickException("error.scenario.not_found", "Scenario cannot be found");

		deleteActionPlanRiskRegisterAssessment(analysis, analysis.removeAssessment(scenario));

		daoScenario.delete(scenario);
	}

	private void deleteActionPlanRiskRegisterAssessment(Analysis analysis, List<Assessment> assessments) throws Exception {
		while (!analysis.getActionPlans().isEmpty())
			daoActionPlan.delete(analysis.getActionPlans().remove(0));
		while (!analysis.getSummaries().isEmpty())
			daoActionPlanSummary.delete(analysis.getSummaries().remove(0));
		deleteRiskRegisterAssessment(analysis, assessments);
	}

	private void deleteRiskRegisterAssessment(Analysis analysis, List<Assessment> assessments) throws Exception {
		while (!analysis.getRiskRegisters().isEmpty())
			daoRiskRegister.delete(analysis.getRiskRegisters().remove(0));
		for (Assessment assessment : assessments)
			daoAssessment.delete(assessment);
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
	public void forceDeleteMeasureDescription(int idMeasureDescription, Principal principal) throws Exception {
		MeasureDescription measureDescription = daoMeasureDescription.get(idMeasureDescription);
		List<Analysis> analysis = daoAnalysis.getAllContains(measureDescription);
		deleteActionPlanAndMeasure(analysis, measureDescription, principal);
		delete(measureDescription);
	}

	private void deleteActionPlanAndMeasure(List<Analysis> analyses, MeasureDescription measureDescription, Principal principal) throws Exception {
		for (Analysis analysis : analyses) {
			while (!analysis.getSummaries().isEmpty())
				daoActionPlanSummary.delete(analysis.getSummaries().remove(analysis.getSummaries().size() - 1));
			while (!analysis.getActionPlans().isEmpty())
				daoActionPlan.delete(analysis.getActionPlans().remove(analysis.getActionPlans().size() - 1));
			analysis.getAnalysisStandards().stream().filter(standard -> standard.getStandard().getLabel().equals(Constant.STANDARD_27002)).map(standard -> standard.getMeasures())
					.findFirst().ifPresent(measures -> measures.forEach(measure -> ((NormalMeasure) measure).getMeasurePropertyList().setSoaRisk("")));
			Optional<AnalysisStandard> standard = analysis.getAnalysisStandards().stream()
					.filter(analysisStandard -> analysisStandard.getStandard().equals(measureDescription.getStandard())).findAny();
			if (standard.isPresent()) {
				Iterator<Measure> iterator = standard.get().getMeasures().iterator();
				while (iterator.hasNext()) {
					Measure measure = iterator.next();
					if (measure.getMeasureDescription().equals(measureDescription)) {
						iterator.remove();
						daoMeasure.delete(measure);
						/**
						 * Log
						 */
						TrickLogManager.Persist(
								LogLevel.WARNING,
								LogType.ANALYSIS,
								"log.delete.measure",
								String.format("Analysis: %s, version: %s, target: Measure (%s) from: %s", analysis.getIdentifier(), analysis.getVersion(),
										measureDescription.getReference(), measureDescription.getStandard().getLabel()), principal.getName(), LogAction.DELETE,
								analysis.getIdentifier(), analysis.getVersion(), measureDescription.getReference(), measureDescription.getStandard().getLabel());
						break;
					}
				}
			}
		}
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

		daoAnalysis.delete(analysis);
		/**
		 * Log
		 */
		TrickLogManager.Persist(LogLevel.WARNING, LogType.ANALYSIS, "log.delete.analysis",
				String.format("Analysis: %s, version: %s", analysis.getIdentifier(), analysis.getVersion()), username, LogAction.DELETE, analysis.getIdentifier(),
				analysis.getVersion());

		if (!daoAnalysis.hasData(analysis.getIdentifier()))
			customDeleteEmptyAnalysis(analysis.getIdentifier(), username);
	}

	@Transactional
	public void deleteAnalysis(int idAnalysis, String username) throws Exception {
		deleteAnalysis(daoAnalysis.get(idAnalysis), username);
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

	@Transactional
	public void deleteUser(UserDeleteHelper deleteHelper, Map<Object, String> errors, Principal principal, MessageSource messageSource, Locale locale) throws Exception {
		User user = daoUser.get(deleteHelper.getIdUser());
		if (user == null)
			errors.put("user", messageSource.getMessage("error.action.not_authorise", null, "Action does not authorised", locale));
		else {
			if (deleteHelper.hasAnalysesToSwitch()) {
				SwitchAnalysisOwnerHelper switchAnalysisOwnerHelper = new SwitchAnalysisOwnerHelper(daoAnalysis);
				for (Entry<Integer, Integer> entry : deleteHelper.getSwitchOwners().entrySet()) {
					try {
						Analysis analysis = daoAnalysis.get(entry.getKey());
						User owner = daoUser.get(entry.getValue());
						if (owner == null || analysis == null)
							throw new TrickException("error.action.not_authorise", "Action does not authorised");
						switchAnalysisOwnerHelper.switchOwner(principal, analysis, owner);
						UserAnalysisRight userAnalysisRight = analysis.getRightsforUser(user);
						if (userAnalysisRight != null && analysis.getUserRights().remove(userAnalysisRight))
							daoUserAnalysisRight.delete(userAnalysisRight);
					} catch (TrickException e) {
						e.printStackTrace();
						errors.put(entry.getKey(), messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
						throw new DataIntegrityViolationException(e.getMessage(), e);
					} catch (Exception e) {
						e.printStackTrace();
						errors.put(entry.getKey(), messageSource.getMessage("error.unknown.occurred", null, "An unknown error occurred", locale));
						throw e;
					}
				}
			}
			if (deleteHelper.hasAnalysesToDelete()) {
				List<Analysis> analyses = daoAnalysis.getAll(deleteHelper.getDeleteAnalysis());
				Collections.sort(analyses, new AnalysisComparator().reversed());
				deleteHelper.getDeleteAnalysis().stream().filter(idAnalysis -> !analyses.stream().anyMatch(analysis -> analysis.getId() == idAnalysis))
						.forEach(idAnalysis -> errors.put(idAnalysis, messageSource.getMessage("error.action.not_authorise", null, "Action does not authorised", locale)));
				if (!errors.isEmpty())
					throw new DataIntegrityViolationException("Action does not authorised");

				for (Analysis analysis : analyses) {
					try {
						if (!daoAnalysis.exists(analysis.getId()))
							continue;
						if (!analysis.getOwner().equals(user))
							throw new TrickException("error.action.not_authorise", "Action does not authorised");
						deleteAnalysis(analysis, principal.getName());
					} catch (TrickException e) {
						errors.put(analysis.getId(), messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
						throw new DataIntegrityViolationException(e.getMessage(), e);
					} catch (ConstraintViolationException | org.hibernate.ObjectDeletedException e) {
						errors.put(analysis.getId(), messageSource.getMessage("error.delete.analysis.in_use", null, "There is at least an analysis based on this one.", locale));
						throw e;
					} catch (Exception e) {
						errors.put(analysis.getId(), messageSource.getMessage("error.unknown.occurred", null, "An unknown error occurred", locale));
						throw e;
					}
				}
			}
			try {
				deleteUser(user, principal.getName());
			} catch (Exception e) {
				errors.put("user", messageSource.getMessage("error.user.delete", null, "User cannot be deleted", locale));
				throw e;
			}
		}
	}

	protected void deleteUser(User user, String username) throws Exception {
		user.disable();
		ResetPassword resetPassword = daoResetPassword.get(user);
		if (resetPassword != null)
			daoResetPassword.delete(resetPassword);
		daoWordReport.deeleteByUser(user);
		daoUserSqLite.deeleteByUser(user);
		daoUserAnalysisRight.deleteByUser(user);
		user.getCustomers().clear();
		daoUser.delete(user);
		TrickLogManager.Persist(LogLevel.WARNING, LogType.ADMINISTRATION, "log.user.delete",
				String.format("User: %s %s, username: %s, email: %s", user.getFirstName(), user.getLastName(), user.getLogin(), user.getEmail()), username, LogAction.DELETE,
				user.getFirstName(), user.getLastName(), user.getLogin(), user.getEmail());
	}
}
