/**
 *
 */
package lu.itrust.business.ts.component;

import java.security.Principal;
import java.util.ArrayList;
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
import lu.itrust.business.ts.database.dao.DAOMeasureAssetValue;
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
import lu.itrust.business.ts.model.standard.AssetStandard;
import lu.itrust.business.ts.model.standard.NormalStandard;
import lu.itrust.business.ts.model.standard.Standard;
import lu.itrust.business.ts.model.standard.StandardType;
import lu.itrust.business.ts.model.standard.measure.AbstractNormalMeasure;
import lu.itrust.business.ts.model.standard.measure.Measure;
import lu.itrust.business.ts.model.standard.measure.impl.AssetMeasure;
import lu.itrust.business.ts.model.standard.measure.impl.MeasureAssetValue;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescription;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescriptionText;
import lu.itrust.business.ts.usermanagement.ResetPassword;
import lu.itrust.business.ts.usermanagement.User;
import lu.itrust.business.ts.usermanagement.helper.UserDeleteHelper;

/**
 * This class represents a component responsible for custom deletion operations.
 * It provides methods to delete various entities such as assessments, assets,
 * measures, etc.
 * The deletion operations are performed in a transactional manner.
 */
@Component
public class CustomDelete {

	private final AnalysisImpactManager analysisImpactManager;

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
	private DAOMeasureAssetValue daoMeasureAssetValue;

	@Autowired
	private DAOIDS daoIDS;

	CustomDelete(AnalysisImpactManager analysisImpactManager) {
		this.analysisImpactManager = analysisImpactManager;
	}

	/**
	 * Deletes all empty analyses associated with the given identifier.
	 * An analysis is considered empty if it does not have any data.
	 *
	 * @param identifier the identifier of the analyses to be deleted
	 * @param username   the username of the user performing the deletion
	 * @throws Exception if an error occurs during the deletion process
	 */
	@Transactional
	public void customDeleteEmptyAnalysis(String identifier, String username) {
		final List<Analysis> analyses = daoAnalysis.getAllByIdentifier(identifier);
		if (analyses.stream().anyMatch(Analysis::hasData))
			return;
		Collections.sort(analyses, Collections.reverseOrder(new AnalysisComparator()));
		for (Analysis analysis : analyses)
			deleteAnalysisProcess(analysis, username);
	}

	/**
	 * Deletes a measure description and breaks all links to analyses that contain
	 * this measure description.
	 * 
	 * This method can only be used for MeasureDescription from Knowledge base.
	 *
	 * @param analyses           The list of analyses containing the measure
	 *                           description.
	 * @param measureDescription The measure description to be deleted.
	 * @param principal          The principal performing the deletion.
	 * @throws TrickException if the measure description is not a knowledge base
	 *                        measure description.
	 */
	private void breakMeasureDescriptionLinks(List<Analysis> analyses, MeasureDescription measureDescription,
			Principal principal) {
		for (Analysis analysis : analyses) {
			final AnalysisStandard analysisStandard = analysis.getAnalysisStandards().values().stream()
					.filter(a -> a.getStandard().equals(measureDescription.getStandard()))
					.findAny().orElse(null);
			if (analysisStandard != null) {

				if (analysisStandard.isAnalysisOnly())
					throw new TrickException("error.measure.manage_analysis_measure",
							"This measure cannot be managed from the knowledge base");

				final Measure measure = removeMeasureByDescription(measureDescription, analysisStandard);
				if (measure == null)
					continue;

				breakMeasureDependencies(measure, analysis);
				daoMeasure.delete(measure);

				TrickLogManager.persist(LogLevel.WARNING, LogType.ANALYSIS, "log.delete.measure",
						String.format("Analysis: %s, version: %s, target: Measure (%s) from: %s",
								analysis.getIdentifier(), analysis.getVersion(),
								measureDescription.getReference(), measureDescription.getStandard().getName()),
						principal.getName(), LogAction.DELETE, analysis.getIdentifier(), analysis.getVersion(),
						measureDescription.getReference(),
						measureDescription.getStandard().getName());

				daoAnalysis.saveOrUpdate(analysis);
			}
		}
	}

	/**
	 * Deletes the action plan associated with the given analysis and also deletes
	 * any dependencies on scenarios or assets.
	 *
	 * @param analysis     The analysis for which the action plan and dependencies
	 *                     need to be deleted.
	 * @param assessments  The list of assessments associated with the analysis.
	 * @param riskProfiles The list of risk profiles associated with the analysis.
	 * @throws Exception If an error occurs while deleting the action plan or
	 *                   dependencies.
	 */
	private void deleteActionPlanAndScenarioOrAssetDependencies(Analysis analysis, List<Assessment> assessments,
			List<RiskProfile> riskProfiles) {
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
	protected void deleteAnalysis(Analysis analysis, String username) {
		if (analysis == null)
			throw new TrickException("error.analysis.not_found", "Analysis cannot be found");
		deleteAnalysisProcess(analysis, username);
		if (!daoAnalysis.hasData(analysis.getIdentifier()))
			customDeleteEmptyAnalysis(analysis.getIdentifier(), username);
	}

	/**
	 * Deletes an analysis with the specified ID and username.
	 *
	 * @param idAnalysis the ID of the analysis to delete
	 * @param username   the username of the user performing the deletion
	 * @throws Exception if an error occurs during the deletion process
	 */
	@Transactional
	public void deleteAnalysis(int idAnalysis, String username) {
		deleteAnalysis(daoAnalysis.get(idAnalysis), username);
	}

	/**
	 * Deletes the analyses with the specified IDs and updates the username of the
	 * user performing the deletion.
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

	/**
	 * Deletes a measure from the analysis by its ID, ensuring that it belongs to
	 * the
	 * specified standard.
	 * This method is transactional and ensures that all changes are committed
	 * together.
	 *
	 * @param analysisID The ID of the analysis containing the measure.
	 * @param idStandard The ID of the standard to which the measure belongs.
	 * @param idMeasure  The ID of the measure to be deleted.
	 * @throws TrickException if the measure cannot be found or does not belong to
	 *                        the specified standard.
	 */
	@Transactional
	public void deleteMeasure(Integer analysisID, Integer idStandard, Integer idMeasure) {

		final Analysis analysis = daoAnalysis.get(analysisID);

		if (analysis == null)
			throw new TrickException("error.analysis.not_found", "Analysis cannot be found");

		final AnalysisStandard analysisStandard = analysis.getAnalysisStandards().values()
				.stream().filter(e -> e.getStandard().getId() == idStandard).findAny()
				.orElseThrow(() -> new TrickException("error.standard.not_found", "Standard cannot be found"));

		if (!analysisStandard.isAnalysisOnly())
			throw new TrickException("error.measure.manage_knowledgebase_measure",
					"This measure can only be managed from the knowledge base");

		final Measure measure = analysisStandard.getMeasures().stream()
				.filter(m -> m.getId() == idMeasure).findAny()
				.orElseThrow(() -> new TrickException("error.measure.not_found", "Measure cannot be found"));

		analysisStandard.getMeasures().remove(measure);

		breakMeasureDependencies(measure, analysis);

		daoMeasure.delete(measure);

		daoMeasureDescription.delete(measure.getMeasureDescription());

		daoAnalysis.saveOrUpdate(analysis);
	}

	/**
	 * Deletes a list of measures by their IDs, ensuring that they belong to the
	 * specified standard and analysis.
	 * This method is transactional and ensures that all changes are committed
	 * together.
	 *
	 * @param ids        The list of measure IDs to be deleted.
	 * @param idStandard The ID of the standard to which the measures belong.
	 * @param analysisId The ID of the analysis containing the measures.
	 * @return A list of IDs of the deleted measures.
	 * @throws TrickException if the analysis or standard cannot be found, or if the
	 *                        measures do not belong to the specified standard.
	 */
	@Transactional
	public List<Integer> deleteMeasures(final List<Integer> ids,final int idStandard, final int analysisId ) {
		// If the list of IDs is null or empty, return an empty list
		if (ids == null || ids.isEmpty())
			return Collections.emptyList();
			
		final Analysis analysis = daoAnalysis.get(analysisId);
		if (analysis == null)
			throw new TrickException("error.analysis.not_found", "Analysis cannot be found");
		final AnalysisStandard analysisStandard = analysis.getAnalysisStandards().values().stream()
				.filter(e -> e.getStandard().getId() == idStandard).findAny()
				.orElseThrow(() -> new TrickException("error.standard.not_found", "Standard cannot be found"));
		if (!analysisStandard.isAnalysisOnly())
			throw new TrickException("error.measure.manage_knowledgebase_measure",
					"This measure can only be managed from the knowledge base");

		final List<Integer> deletedIds = ids.stream().filter(Objects::nonNull).map(idMeasure -> {
			final Measure measure = removeMeasureById(idMeasure, analysisStandard);
			if (measure == null)
				return null;
			breakMeasureDependencies(measure, analysis);
			daoMeasure.delete(measure);
			daoMeasureDescription.delete(measure.getMeasureDescription());
			return idMeasure;
		}).filter(Objects::nonNull).toList();

		if (!deletedIds.isEmpty())
			daoAnalysis.saveOrUpdate(analysis);
		return deletedIds;
	}

	/**
	 * Deletes the specified analysis and its associated data, including
	 * subscribers,
	 * invitations, and logs.
	 *
	 * @param analysis the analysis to be deleted
	 * @param username the username of the user performing the deletion
	 */
	private void deleteAnalysisProcess(Analysis analysis, String username) {
		if (analysis == null)
			throw new TrickException("error.analysis.not_found", "Analysis cannot be found");
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

	/**
	 * Deletes a list of assets by their IDs and analysis ID, breaking all links to
	 * measures, nodes, and edges associated with the assets.
	 *
	 * @param ids        The list of asset IDs to be deleted.
	 * @param analysisId The ID of the analysis containing the assets.
	 * @return A list of IDs of the deleted assets.
	 * @throws TrickException if the analysis cannot be found.
	 */
	@Transactional
	public List<Integer> deleteAssets(final List<Integer> ids, final int analysisId) {

		if (ids == null || ids.isEmpty())
			return Collections.emptyList();

		final Analysis analysis = daoAnalysis.get(analysisId);
		if (analysis == null)
			throw new TrickException("error.analysis.not_found", "Analysis cannot be found");

		final List<Integer> deletedIds = ids.stream().filter(Objects::nonNull).map(idAsset -> {
			final Asset asset = analysis.findAsset(idAsset);
			if (asset == null)
				return null;
			breakAssetLinks(asset, analysis);
			analysis.getAssets().remove(asset);
			daoAsset.delete(asset);
			return idAsset;
		}).filter(Objects::nonNull).toList();

		if (!deletedIds.isEmpty())
			daoAnalysis.saveOrUpdate(analysis);
		return deletedIds;
	}

	/**
	 * Deletes an asset by its ID and analysis ID, breaking all links to measures,
	 * nodes, and edges associated with the asset.
	 *
	 * @param idAsset    The ID of the asset to be deleted.
	 * @param idAnalysis The ID of the analysis containing the asset.
	 * @throws TrickException if the analysis or asset cannot be found.
	 */
	@Transactional
	public void deleteAsset(int idAsset, int idAnalysis) {
		final Analysis analysis = daoAnalysis.get(idAnalysis);
		if (analysis == null)
			throw new TrickException("error.analysis.not_found", "Analysis cannot be found");
		final Asset asset = analysis.findAsset(idAsset);
		if (asset == null)
			throw new TrickException("error.asset.not_found", "Asset cannot be found");

		breakAssetLinks(asset, analysis);
		analysis.getAssets().remove(asset);
		daoAsset.delete(asset);
		daoAnalysis.saveOrUpdate(analysis);
	}

	/**
	 * Breaks the links between the specified asset and its associated measures,
	 * nodes, and edges in the given analysis.
	 *
	 * @param asset    The asset to break links for.
	 * @param analysis The analysis containing the asset.
	 */
	private void breakAssetLinks(final Asset asset, final Analysis analysis) {
		final List<AssetMeasure> assetMeasures = analysis.getAnalysisStandards().values().stream()
				.filter(e -> e.isAnalysisOnly() && e.getStandard().getType() == StandardType.ASSET)
				.flatMap(e -> e.getMeasures().stream())
				.filter(m -> (m instanceof AssetMeasure ma) && ma.getMeasureAssetValues().stream()
						.anyMatch(av -> av.getAsset().equals(asset)))
				.map(m -> (AssetMeasure) m).toList();

		final List<MeasureAssetValue> assetValues = new ArrayList<>();

		assetMeasures
				.forEach(measure -> measure.getMeasureAssetValues().removeIf(av -> av.getAsset().equals(asset) &&
						assetValues.add(av)));

		assetValues.forEach(daoMeasureAssetValue::delete);

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
	}

	/**
	 * Deletes the dependencies of assets or scenarios associated with the given
	 * analysis, assessments, and risk profiles.
	 *
	 * @param analysis     The analysis from which to delete dependencies.
	 * @param assessments  The list of assessments to be deleted.
	 * @param riskProfiles The list of risk profiles to be deleted.
	 */
	private void deleteAssetOrScenarioDependencies(Analysis analysis, List<Assessment> assessments,
			List<RiskProfile> riskProfiles) {
		while (!analysis.getRiskRegisters().isEmpty())
			daoRiskRegister.delete(analysis.getRiskRegisters().remove(0));
		for (Assessment assessment : assessments)
			daoAssessment.delete(assessment);
		riskProfiles.forEach(riskProfile -> daoRiskProfile.delete(riskProfile));
	}

	/**
	 * Deletes a customer and all associated users, ensuring that the customer can
	 * be
	 * deleted (not a default customer and not in use).
	 * This method is transactional and ensures that all changes are committed
	 * together.
	 *
	 * @param idcustomer the ID of the customer to be deleted
	 * @param username   the username of the user performing the deletion
	 * @throws TrickException if the customer cannot be found, is a default
	 *                        customer,
	 *                        or has associated analyses
	 */
	@Transactional
	public void deleteCustomer(int idcustomer, String username) {
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

	/**
	 * Deletes duplicated and unused asset type values for a given analysis.
	 * This method is transactional and ensures that all changes are committed
	 * together.
	 *
	 * @param analysisId the ID of the analysis from which to delete asset type
	 *                   values
	 * @throws Exception if an error occurs during the deletion process
	 */
	@Transactional
	public void deleteDuplicationAssetTypeValue(Integer analysisId) {
		final Analysis analysis = daoAnalysis.get(analysisId);
		for (Scenario scenario : analysis.getScenarios())
			daoAssetTypeValue.delete(scenario.deleteDuplicatedAndUnsed());
		daoAnalysis.saveOrUpdate(analysis);
	}

	/**
	 * Deletes a scenario from the analysis by its ID.
	 * This method is transactional and ensures that all changes are committed
	 * together.
	 *
	 * @param idScenario the ID of the scenario to be deleted
	 * @param idAnalysis the ID of the analysis from which the scenario is deleted
	 * @throws TrickException if the analysis or scenario cannot be found
	 */
	@Transactional
	public void deleteScenario(int idScenario, int idAnalysis) {

		final Analysis analysis = daoAnalysis.get(idAnalysis);

		if (analysis == null)
			throw new TrickException("error.analysis.not_found", "Analysis cannot be found");

		final Scenario scenario = analysis.findScenario(idScenario);

		if (scenario == null)
			throw new TrickException("error.scenario.not_found", "Scenario cannot be found");

		deleteActionPlanAndScenarioOrAssetDependencies(analysis, analysis.removeAssessment(scenario),
				analysis.removeRiskProfile(scenario));

		analysis.getScenarios().remove(scenario);
		daoScenario.delete(scenario);
		daoAnalysis.saveOrUpdate(analysis);
	}

	/**
	 * Deletes a list of scenarios from the analysis by their IDs.
	 * This method is transactional and ensures that all changes are committed
	 * together.
	 *
	 * @param ids        the list of scenario IDs to be deleted
	 * @param idAnalysis the ID of the analysis from which the scenarios are deleted
	 * @return a list of IDs of the deleted scenarios
	 * @throws TrickException if the analysis cannot be found
	 */
	@Transactional
	public List<Integer> deleteScenarios(final List<Integer> ids, final int idAnalysis) {

		if (ids == null || ids.isEmpty())
			return Collections.emptyList();

		final Analysis analysis = daoAnalysis.get(idAnalysis);
		if (analysis == null)
			throw new TrickException("error.analysis.not_found", "Analysis cannot be found");

		final List<Integer> deletedIds = ids.stream().filter(Objects::nonNull).map(idScenario -> {
			final Scenario scenario = analysis.findScenario(idScenario);
			if (scenario == null)
				return null;
			deleteActionPlanAndScenarioOrAssetDependencies(analysis, analysis.removeAssessment(scenario),
					analysis.removeRiskProfile(scenario));
			analysis.getScenarios().remove(scenario);
			daoScenario.delete(scenario);
			return idScenario;
		}).filter(Objects::nonNull).toList();

		// Save the analysis after deleting scenarios
		if (!deletedIds.isEmpty())
			daoAnalysis.saveOrUpdate(analysis);

		return deletedIds;
	}

	/**
	 * Deletes a standard and its associated measure descriptions and texts.
	 * This method is transactional and ensures that all changes are committed
	 * together.
	 *
	 * @param standard the standard to be deleted
	 * @throws TrickException if the standard is used in analyses
	 */
	@Transactional
	public void deleteStandard(Standard standard) {

		if (daoAnalysisStandard.countByStandard(standard) > 0)
			throw new TrickException("error.delete.norm.analyses_with_norm",
					"Standard could not be deleted: it is used in analyses!");

		daoMeasureDescription.getAllByStandard(standard)
				.forEach(measureDescription -> daoMeasureDescription.delete(measureDescription));

		daoStandard.delete(standard);

	}

	/**
	 * Deletes a user and their associated data, including reset passwords, word
	 * reports, SQLite data, analysis rights, invitations, and email validation
	 * requests.
	 *
	 * @param user     the user to be deleted
	 * @param username the username of the user performing the deletion
	 */
	protected void deleteUser(User user, String username) {

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

	/**
	 * Deletes a user and their associated analyses, switching ownership of analyses
	 * if necessary.
	 *
	 * @param deleteHelper  the helper containing user deletion information
	 * @param errors        a map to store any errors encountered during deletion
	 * @param principal     the principal performing the deletion
	 * @param messageSource the message source for localization
	 * @param locale        the locale for error messages
	 * @throws Exception if an error occurs during the deletion process
	 */
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
						.filter(idAnalysis -> analyses.stream().noneMatch(analysis -> analysis.getId() == idAnalysis))
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

	/**
	 * Deletes a measure description and its associated action plans and measures.
	 * This method is transactional and ensures that all changes are committed
	 * together.
	 *
	 * @param idMeasureDescription the ID of the measure description to be deleted
	 * @param principal            the principal performing the deletion
	 * @throws Exception if an error occurs during the deletion process
	 */
	@Transactional
	public void forceDeleteMeasureDescription(int idMeasureDescription, Principal principal) {
		final MeasureDescription measureDescription = daoMeasureDescription.get(idMeasureDescription);
		if (measureDescription == null)
			throw new TrickException("error.measure.description.not_found", "Measure description cannot be found");
		breakMeasureDescriptionLinks(daoAnalysis.getAllContains(measureDescription), measureDescription, principal);
		daoMeasureDescription.delete(measureDescription);
		TrickLogManager.persist(LogLevel.WARNING, LogType.ANALYSIS, "log.delete.measure.description",
				String.format("Measure description: %s", measureDescription.getReference()), principal.getName(),
				LogAction.DELETE, measureDescription.getReference());
	}

	/**
	 * Removes a customer from a user and deletes the associated rights in analyses.
	 * This method is transactional and ensures that all changes are committed
	 * together.
	 *
	 * @param customerId    the ID of the customer to be removed
	 * @param userName      the username of the user from whom the customer is being
	 *                      removed
	 * @param adminUsername the username of the admin performing this action
	 * @return true if the customer was successfully removed, false otherwise
	 */
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
	private void breakMeasureDependencies(Measure measure, Analysis analysis) {
		deleteAnalysisActionPlan(analysis);
		analysis.getRiskProfiles().stream().forEach(riskProfile -> riskProfile.getMeasures()
				.removeIf(m -> m.equals(measure)));
	}

	/**
	 * Removes a measure from the analysis standard by its ID.
	 * This method iterates through the measures in the analysis standard and
	 * removes the one that matches the given measure ID.
	 *
	 * @param measureId        The ID of the measure to be removed.
	 * @param analysisStandard The analysis standard from which to remove the
	 *                         measure.
	 * @return The removed measure, or null if no matching measure was found.
	 */
	private Measure removeMeasureById(final int measureId,
			final AnalysisStandard analysisStandard) {

		final Iterator<Measure> iterator = analysisStandard.getMeasures().iterator();

		while (iterator.hasNext()) {

			final Measure measure = iterator.next();

			if (measure.getId() == measureId) {

				iterator.remove();
				return measure;
			}
		}
		return null;
	}

	/**
	 * Removes a measure from the analysis standard by its description.
	 * This method iterates through the measures in the analysis standard and
	 * removes the one that matches the given measure description.
	 *
	 * @param measureDescription The description of the measure to be removed.
	 * @param analysisStandard   The analysis standard from which to remove the
	 *                           measure.
	 * @return The removed measure, or null if no matching measure was found.
	 */
	private Measure removeMeasureByDescription(final MeasureDescription measureDescription,
			final AnalysisStandard analysisStandard) {

		final Iterator<Measure> iterator = analysisStandard.getMeasures().iterator();

		while (iterator.hasNext()) {

			final Measure measure = iterator.next();

			if (measure.getMeasureDescription().equals(measureDescription)) {

				iterator.remove();
				return measure;
			}
		}
		return null;
	}
}
