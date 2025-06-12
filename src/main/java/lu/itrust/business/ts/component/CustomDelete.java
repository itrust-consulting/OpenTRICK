/**
 *
 */
package lu.itrust.business.ts.component;

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.ts.constants.Constant;
import lu.itrust.business.ts.database.dao.DAOActionPlan;
import lu.itrust.business.ts.database.dao.DAOActionPlanSummary;
import lu.itrust.business.ts.database.dao.DAOAnalysis;
import lu.itrust.business.ts.database.dao.DAOAnalysisShareInvitation;
import lu.itrust.business.ts.database.dao.DAOAnalysisStandard;
import lu.itrust.business.ts.database.dao.DAOAssessment;
import lu.itrust.business.ts.database.dao.DAOAsset;
import lu.itrust.business.ts.database.dao.DAOAssetEdge;
import lu.itrust.business.ts.database.dao.DAOAssetNode;
import lu.itrust.business.ts.database.dao.DAOAssetTypeValue;
import lu.itrust.business.ts.database.dao.DAOCustomer;
import lu.itrust.business.ts.database.dao.DAOEmailValidatingRequest;
import lu.itrust.business.ts.database.dao.DAOIDS;
import lu.itrust.business.ts.database.dao.DAOMeasure;
import lu.itrust.business.ts.database.dao.DAOMeasureDescription;
import lu.itrust.business.ts.database.dao.DAOMeasureDescriptionText;
import lu.itrust.business.ts.database.dao.DAOResetPassword;
import lu.itrust.business.ts.database.dao.DAORiskProfile;
import lu.itrust.business.ts.database.dao.DAORiskRegister;
import lu.itrust.business.ts.database.dao.DAOScenario;
import lu.itrust.business.ts.database.dao.DAOStandard;
import lu.itrust.business.ts.database.dao.DAOUser;
import lu.itrust.business.ts.database.dao.DAOUserAnalysisRight;
import lu.itrust.business.ts.database.dao.DAOUserSqLite;
import lu.itrust.business.ts.database.dao.DAOWordReport;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.helper.SwitchAnalysisOwnerHelper;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.analysis.helper.AnalysisComparator;
import lu.itrust.business.ts.model.analysis.rights.UserAnalysisRight;
import lu.itrust.business.ts.model.assessment.Assessment;
import lu.itrust.business.ts.model.asset.Asset;
import lu.itrust.business.ts.model.cssf.RiskProfile;
import lu.itrust.business.ts.model.general.Customer;
import lu.itrust.business.ts.model.general.LogAction;
import lu.itrust.business.ts.model.general.LogLevel;
import lu.itrust.business.ts.model.general.LogType;
import lu.itrust.business.ts.model.general.TicketingSystem;
import lu.itrust.business.ts.model.ilr.AssetEdge;
import lu.itrust.business.ts.model.ilr.AssetNode;
import lu.itrust.business.ts.model.scenario.Scenario;
import lu.itrust.business.ts.model.standard.AnalysisStandard;
import lu.itrust.business.ts.model.standard.Standard;
import lu.itrust.business.ts.model.standard.measure.AbstractNormalMeasure;
import lu.itrust.business.ts.model.standard.measure.Measure;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescription;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescriptionText;
import lu.itrust.business.ts.usermanagement.ResetPassword;
import lu.itrust.business.ts.usermanagement.User;
import lu.itrust.business.ts.usermanagement.helper.UserDeleteHelper;


/**
 * This class represents a component responsible for custom deletion operations.
 * It provides methods to delete various entities such as assessments, assets, measures, etc.
 * The deletion operations are performed in a transactional manner.
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

	@Autowired
	private DAORiskProfile daoRiskProfile;

	@Autowired
	private DAOAnalysisShareInvitation daoAnalysisShareInvitation;

	@Autowired
	private DAOEmailValidatingRequest daoEmailValidatingRequest;

	@Autowired
	private DAOAssetEdge daoAssetEdge;

	@Autowired
	private DAOAssetNode daoAssetNode;

	@Autowired
	private DAOIDS daoIDS;

	/**
	 * Deletes all empty analyses associated with the given identifier.
	 * An analysis is considered empty if it does not have any data.
	 *
	 * @param identifier the identifier of the analyses to be deleted
	 * @param username   the username of the user performing the deletion
	 * @throws Exception if an error occurs during the deletion process
	 */
	@Transactional
	public void customDeleteEmptyAnalysis(String identifier, String username) throws Exception {
		final List<Analysis> analyses = daoAnalysis.getAllByIdentifier(identifier);
		if (analyses.stream().anyMatch(Analysis::hasData))
			return;
		Collections.sort(analyses, Collections.reverseOrder(new AnalysisComparator()));
		for (Analysis analysis : analyses)
			deleteAnalysisProcess(analysis, username);
	}

	/**
	 * Deletes the action plan and measure associated with the given analyses, measure description, and principal.
	 *
	 * @param analyses          the list of analyses
	 * @param measureDescription the measure description
	 * @param principal         the principal
	 * @throws Exception if an error occurs during the deletion process
	 */
	private void deleteActionPlanAndMeasure(List<Analysis> analyses, MeasureDescription measureDescription,
			Principal principal) throws Exception {
		for (Analysis analysis : analyses) {
			final AnalysisStandard analysisStandard = analysis.getAnalysisStandards().values().stream()
					.filter(a -> a.getStandard().equals(measureDescription.getStandard()))
					.findAny().orElse(null);
			if (analysisStandard != null) {

				analysis.getAnalysisStandards().values().stream()
						.filter(standard -> standard.getStandard().is(Constant.STANDARD_27002))
						.map(standard -> standard.getMeasures())
						.findFirst().ifPresent(measures -> measures.forEach(
								measure -> ((AbstractNormalMeasure) measure).getMeasurePropertyList().setSoaRisk("")));

				removeMeasureDependencies(measureDescription, analysis);

				final Iterator<Measure> iterator = analysisStandard.getMeasures().iterator();

				while (iterator.hasNext()) {

					final Measure measure = iterator.next();

					if (measure.getMeasureDescription().equals(measureDescription)) {
						iterator.remove();
						daoMeasure.delete(measure);
						/**
						 * Log
						 */
						TrickLogManager.persist(LogLevel.WARNING, LogType.ANALYSIS, "log.delete.measure",
								String.format("Analysis: %s, version: %s, target: Measure (%s) from: %s",
										analysis.getIdentifier(), analysis.getVersion(),
										measureDescription.getReference(), measureDescription.getStandard().getName()),
								principal.getName(), LogAction.DELETE, analysis.getIdentifier(), analysis.getVersion(),
								measureDescription.getReference(),
								measureDescription.getStandard().getName());
						break;
					}
				}

				daoAnalysis.saveOrUpdate(analysis);
			}
		}
		daoMeasureDescription.delete(measureDescription);
	}

	/**
	 * Deletes the action plan associated with the given analysis and also deletes any dependencies on scenarios or assets.
	 *
	 * @param analysis The analysis for which the action plan and dependencies need to be deleted.
	 * @param assessments The list of assessments associated with the analysis.
	 * @param riskProfiles The list of risk profiles associated with the analysis.
	 * @throws Exception If an error occurs while deleting the action plan or dependencies.
	 */
	private void deleteActionPlanAndScenarioOrAssetDependencies(Analysis analysis, List<Assessment> assessments,
			List<RiskProfile> riskProfiles) throws Exception {
		deleteAnalysisActionPlan(analysis);
		deleteAssetOrScenarioDependencies(analysis, assessments, riskProfiles);
	}

	/**
	 * Deletes the specified analysis and performs additional cleanup if necessary.
	 *
	 * @param analysis the analysis to be deleted
	 * @param username the username of the user performing the deletion
	 * @throws Exception if an error occurs during the deletion process
	 */
	protected void deleteAnalysis(Analysis analysis, String username) throws Exception {
		deleteAnalysisProcess(analysis, username);
		if (!daoAnalysis.hasData(analysis.getIdentifier()))
			customDeleteEmptyAnalysis(analysis.getIdentifier(), username);
	}

	/**
	 * Deletes an analysis with the specified ID and username.
	 *
	 * @param idAnalysis the ID of the analysis to delete
	 * @param username the username of the user performing the deletion
	 * @throws Exception if an error occurs during the deletion process
	 */
	@Transactional
	public void deleteAnalysis(int idAnalysis, String username) throws Exception {
		deleteAnalysis(daoAnalysis.get(idAnalysis), username);
	}

	/**
	 * Deletes the analyses with the specified IDs and updates the username of the user performing the deletion.
	 *
	 * @param ids      the list of analysis IDs to be deleted
	 * @param username the username of the user performing the deletion
	 * @return true if the deletion is successful, false otherwise
	 */
	@Transactional
	public boolean deleteAnalysis(List<Integer> ids, String username) {
		try {
			final List<Analysis> analyses = daoAnalysis.getAll(ids);
			Collections.sort(analyses, new AnalysisComparator().reversed());
			for (Analysis analysis : analyses)
				deleteAnalysis(analysis, username);
			return true;
		} catch (Exception e) {
			TrickLogManager.persist(e);
			return false;
		}
	}

	/**
	 * It must be done in a transaction<br>
	 * Clear Action plan and Action plan summary
	 *
	 * @param analysis
	 */
	public void deleteAnalysisActionPlan(Analysis analysis) {
		while (!analysis.getActionPlans().isEmpty())
			daoActionPlan.delete(analysis.getActionPlans().remove(0));
		while (!analysis.getSummaries().isEmpty())
			daoActionPlanSummary.delete(analysis.getSummaries().remove(0));
	}

	@Transactional
	public void deleteAnalysisMeasure(Integer analysisID, Integer idStandard, Integer idMeasure) {

		final Measure measure = daoMeasure.getFromAnalysisById(analysisID, idMeasure);

		if (measure == null || measure.getMeasureDescription().getStandard().getId() != idStandard)
			throw new TrickException("error.measure.not_found", "Measure cannot be found");

		final Analysis analysis = daoAnalysis.get(analysisID);

		final MeasureDescription measureDescription = measure.getMeasureDescription();

		final AnalysisStandard analysisStandard = analysis.getAnalysisStandards()
				.get(measureDescription.getStandard().getLabel());

		if (!analysisStandard.isAnalysisOnly())
			throw new TrickException("error.measure.manage_knowledgebase_measure",
					"This measure can only be managed from the knowledge base");

		removeMeasureDependencies(measureDescription, analysis);

		analysisStandard.getMeasures().remove(measure);

		daoMeasure.delete(measure);

		daoMeasureDescription.delete(measureDescription);

		daoAnalysis.saveOrUpdate(analysis);
	}

	private void deleteAnalysisProcess(Analysis analysis, String username) {
		daoIDS.getByAnalysis(analysis).forEach(ids -> {
			ids.getSubscribers().remove(analysis);
			daoIDS.saveOrUpdate(ids);
		});

		daoAnalysisShareInvitation.deleteByAnalysis(analysis);

		daoAnalysis.delete(analysis);
		/**
		 * Log
		 */
		TrickLogManager.persist(LogLevel.WARNING, LogType.ANALYSIS, "log.delete.analysis",
				String.format("Analysis: %s, version: %s", analysis.getIdentifier(), analysis.getVersion()), username,
				LogAction.DELETE, analysis.getIdentifier(),
				analysis.getVersion());
	}

	@Transactional
	public void deleteAsset(int idAsset, int idAnalysis) throws Exception {

		final Analysis analysis = daoAnalysis.get(idAnalysis);
		if (analysis == null)
			throw new TrickException("error.analysis.not_found", "Analysis cannot be found");

		final Asset asset = analysis.findAsset(idAsset);

		if (asset == null)
			throw new TrickException("error.asset.not_found", "Asset cannot be found");

		final Set<AssetNode> nodes = new HashSet<>();

		analysis.getAssetNodes().removeIf(e -> asset.equals(e.getAsset()) && nodes.add(e));

		final Set<AssetEdge> edges = analysis.getAssetNodes().stream()
				.flatMap(n -> nodes.stream().map(c -> n.getEdges().remove(c))).filter(Objects::nonNull)
				.collect(Collectors.toSet());

		nodes.forEach(e -> {
			edges.addAll(e.getEdges().values());
			e.getEdges().clear();
		});

		daoAssetEdge.delete(edges);

		daoAssetNode.delete(nodes);

		deleteActionPlanAndScenarioOrAssetDependencies(analysis, analysis.removeAssessment(asset),
				analysis.removeRiskProfile(asset));

		analysis.removeFromScenario(asset).forEach(scenario -> daoScenario.saveOrUpdate(scenario));

		analysis.getAssets().remove(asset);

		daoAsset.delete(asset);

		daoAnalysis.saveOrUpdate(analysis);
	}

	private void deleteAssetOrScenarioDependencies(Analysis analysis, List<Assessment> assessments,
			List<RiskProfile> riskProfiles) throws Exception {
		while (!analysis.getRiskRegisters().isEmpty())
			daoRiskRegister.delete(analysis.getRiskRegisters().remove(0));
		for (Assessment assessment : assessments)
			daoAssessment.delete(assessment);
		riskProfiles.forEach(riskProfile -> daoRiskProfile.delete(riskProfile));
	}

	@Transactional
	public void deleteCustomer(int idcustomer, String username) throws Exception {
		final Customer customer = daoCustomer.get(idcustomer);
		if (customer == null)
			throw new TrickException("error.customer.not_found", "Customer cannot be found");
		if (!customer.isCanBeUsed())
			throw new TrickException("error.customer.delete.profile", "Default customer cannot be deleted");
		if (daoCustomer.isInUsed(customer))
			throw new TrickException("error.delete.customer.has_analyses",
					"Customer could not be deleted: there are still analyses of this customer!");
		final TicketingSystem ticketingSystem = customer.getTicketingSystem();

		daoUser.getAllFromCustomer(customer).forEach(user -> {
			user.getCustomers().remove(customer);
			if (ticketingSystem != null)
				user.getCredentials().remove(ticketingSystem);
			daoUser.saveOrUpdate(user);
		});

		if (ticketingSystem != null) {
			daoUser.findByTicketingSystem(ticketingSystem).forEach(user -> {
				user.getCredentials().remove(ticketingSystem);
				daoUser.saveOrUpdate(user);
			});
		}

		daoCustomer.delete(customer);
		TrickLogManager.persist(LogLevel.WARNING, LogType.ANALYSIS, "log.delete.customer",
				String.format("Customer: %s", customer.getOrganisation()), username, LogAction.DELETE,
				customer.getOrganisation());
	}

	@Transactional
	public void deleteDuplicationAssetTypeValue(Integer analysisId) throws Exception {
		final Analysis analysis = daoAnalysis.get(analysisId);
		for (Scenario scenario : analysis.getScenarios())
			daoAssetTypeValue.delete(scenario.deleteDuplicatedAndUnsed());
		daoAnalysis.saveOrUpdate(analysis);
	}

	@Transactional
	public void deleteScenario(int idScenario, int idAnalysis) throws Exception {
		final Scenario scenario = daoScenario.getFromAnalysisById(idAnalysis, idScenario);
		final Analysis analysis = daoAnalysis.get(idAnalysis);

		if (analysis == null)
			throw new TrickException("error.analysis.not_found", "Analysis cannot be found");
		else if (scenario == null)
			throw new TrickException("error.scenario.not_found", "Scenario cannot be found");

		deleteActionPlanAndScenarioOrAssetDependencies(analysis, analysis.removeAssessment(scenario),
				analysis.removeRiskProfile(scenario));

		analysis.getScenarios().remove(scenario);
		daoScenario.delete(scenario);
		daoAnalysis.saveOrUpdate(analysis);
	}

	@Transactional
	public void deleteStandard(Standard standard) throws Exception {

		if (daoAnalysisStandard.getAllFromStandard(standard).size() > 0)
			throw new TrickException("error.delete.norm.analyses_with_norm",
					"Standard could not be deleted: it is used in analyses!");

		final List<MeasureDescription> measureDescriptions = daoMeasureDescription.getAllByStandard(standard);

		for (MeasureDescription measureDescription : measureDescriptions) {

			final List<MeasureDescriptionText> measureDescriptionTexts = daoMeasureDescriptionText
					.getAllFromMeasureDescription(measureDescription.getId());

			for (MeasureDescriptionText measureDescriptiontext : measureDescriptionTexts)
				daoMeasureDescriptionText.delete(measureDescriptiontext);

			daoMeasureDescription.delete(measureDescription);
		}

		daoStandard.delete(standard);

	}

	protected void deleteUser(User user, String username) throws Exception {

		final ResetPassword resetPassword = daoResetPassword.get(user);

		if (resetPassword != null)
			daoResetPassword.delete(resetPassword);

		user.getCustomers().clear();
		daoWordReport.deleteByUser(user);
		daoUserSqLite.deleteByUser(user);
		daoUserAnalysisRight.deleteByUser(user);
		daoAnalysisShareInvitation.deleteByUser(user);
		daoEmailValidatingRequest.deleteByUser(user);
		daoUser.delete(user);

		TrickLogManager.persist(LogLevel.WARNING, LogType.ADMINISTRATION, "log.user.delete",
				String.format("User: %s %s, username: %s, email: %s", user.getFirstName(), user.getLastName(),
						user.getLogin(), user.getEmail()),
				username, LogAction.DELETE,
				user.getFirstName(), user.getLastName(), user.getLogin(), user.getEmail());
	}

	@Transactional
	public void deleteUser(UserDeleteHelper deleteHelper, Map<Object, String> errors, Principal principal,
			MessageSource messageSource, Locale locale) throws Exception {

		final User user = daoUser.get(deleteHelper.getIdUser());

		if (user == null)
			errors.put("user",
					messageSource.getMessage("error.action.not_authorise", null, "Action does not authorised", locale));
		else {

			if (deleteHelper.hasAnalysesToSwitch()) {

				SwitchAnalysisOwnerHelper switchAnalysisOwnerHelper = new SwitchAnalysisOwnerHelper(daoAnalysis);

				for (Entry<Integer, Integer> entry : deleteHelper.getSwitchOwners().entrySet()) {

					try {

						final Analysis analysis = daoAnalysis.get(entry.getKey());

						final User owner = daoUser.get(entry.getValue());

						if (owner == null || analysis == null)
							throw new TrickException("error.action.not_authorise", "Action does not authorised");

						switchAnalysisOwnerHelper.switchOwner(principal, analysis, owner);

						final UserAnalysisRight userAnalysisRight = analysis.findRightsforUser(user);

						if (userAnalysisRight != null && analysis.getUserRights().remove(userAnalysisRight))
							daoUserAnalysisRight.delete(userAnalysisRight);

					} catch (TrickException e) {
						TrickLogManager.persist(e);
						errors.put(entry.getKey(),
								messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
						throw new DataIntegrityViolationException(e.getMessage(), e);
					} catch (Exception e) {
						TrickLogManager.persist(e);
						errors.put(entry.getKey(), messageSource.getMessage("error.unknown.occurred", null,
								"An unknown error occurred", locale));
						throw e;
					}
				}
			}
			if (deleteHelper.hasAnalysesToDelete()) {

				final List<Analysis> analyses = daoAnalysis.getAll(deleteHelper.getDeleteAnalysis());

				Collections.sort(analyses, new AnalysisComparator().reversed());

				deleteHelper.getDeleteAnalysis().stream()
						.filter(idAnalysis -> !analyses.stream().anyMatch(analysis -> analysis.getId() == idAnalysis))
						.forEach(idAnalysis -> errors.put(idAnalysis, messageSource
								.getMessage("error.action.not_authorise", null, "Action does not authorised", locale)));

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
						errors.put(analysis.getId(),
								messageSource.getMessage(e.getCode(), e.getParameters(), e.getMessage(), locale));
						throw new DataIntegrityViolationException(e.getMessage(), e);
					} catch (ConstraintViolationException | org.hibernate.ObjectDeletedException e) {
						errors.put(analysis.getId(), messageSource.getMessage("error.delete.analysis.in_use", null,
								"There is at least an analysis based on this one.", locale));
						throw e;
					} catch (Exception e) {
						errors.put(analysis.getId(), messageSource.getMessage("error.unknown.occurred", null,
								"An unknown error occurred", locale));
						throw e;
					}
				}
			}
			try {
				deleteUser(user, principal.getName());
			} catch (Exception e) {
				errors.put("user",
						messageSource.getMessage("error.user.delete", null, "User cannot be deleted", locale));
				throw e;
			}
		}
	}

	@Transactional
	public void forceDeleteMeasureDescription(int idMeasureDescription, Principal principal) throws Exception {
		MeasureDescription measureDescription = daoMeasureDescription.get(idMeasureDescription);
		deleteActionPlanAndMeasure(daoAnalysis.getAllContains(measureDescription), measureDescription, principal);
	}

	@Transactional
	public boolean removeCustomerByUser(int customerId, String userName, String adminUsername) {
		final Customer customer = daoCustomer.get(customerId);

		if (customer == null || !customer.isCanBeUsed())
			return false;
		final User user = daoUser.get(userName);

		if (user == null)
			return false;

		final List<Analysis> analyses = daoAnalysis.getAllFromUserAndCustomer(userName, customer.getId());

		if (!(user.containsCustomer(customer) && user.getCustomers().remove(customer)))
			return false;

		for (Analysis analysis : analyses) {
			daoUserAnalysisRight.delete(analysis.removeRights(user));
			daoAnalysis.saveOrUpdate(analysis);
		}

		daoUser.saveOrUpdate(user);
		/**
		 * Log
		 */
		TrickLogManager.persist(LogLevel.WARNING, LogType.ANALYSIS, "log.remove.access.to.customer",
				String.format("Customer: %s, target: %s", customer.getOrganisation(), user.getLogin()), adminUsername,
				LogAction.REMOVE_ACCESS, customer.getOrganisation(),
				user.getLogin());
		return true;
	}

	/**
	 * It must be done in a transaction<br>
	 * Clear Action plan and Action plan summary and remove measure from
	 * {@link RiskProfile#getMeasures()}
	 *
	 * @param measureDescription
	 * @param analysis
	 * @see CustomDelete#deleteAnalysisActionPlan
	 */
	public void removeMeasureDependencies(MeasureDescription measureDescription, Analysis analysis) {
		deleteAnalysisActionPlan(analysis);
		analysis.getRiskProfiles().stream().forEach(riskProfile -> riskProfile.getMeasures()
				.removeIf(measure -> measure.getMeasureDescription().equals(measureDescription)));
	}
}
