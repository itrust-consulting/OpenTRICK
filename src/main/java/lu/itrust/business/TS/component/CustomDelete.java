/**
 * 
 */
package lu.itrust.business.TS.component;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import lu.itrust.business.TS.data.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.data.actionplan.summary.SummaryStage;
import lu.itrust.business.TS.data.analysis.Analysis;
import lu.itrust.business.TS.data.assessment.Assessment;
import lu.itrust.business.TS.data.asset.Asset;
import lu.itrust.business.TS.data.general.AssetTypeValue;
import lu.itrust.business.TS.data.general.Customer;
import lu.itrust.business.TS.data.scenario.Scenario;
import lu.itrust.business.TS.data.standard.AnalysisStandard;
import lu.itrust.business.TS.data.standard.Standard;
import lu.itrust.business.TS.data.standard.measure.Measure;
import lu.itrust.business.TS.data.standard.measuredescription.MeasureDescription;
import lu.itrust.business.TS.data.standard.measuredescription.MeasureDescriptionText;
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
import lu.itrust.business.TS.database.dao.DAOScenario;
import lu.itrust.business.TS.database.dao.DAOStandard;
import lu.itrust.business.TS.database.dao.DAOUser;
import lu.itrust.business.TS.exception.TrickException;
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
	private DAOActionPlan daoActionPlan;

	@Autowired
	private DAOActionPlanSummary daoActionPlanSummary;

	@Autowired
	private DAOAssetTypeValue daoAssetTypeValue;

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
	public void deleteCustomer(Customer customer) throws Exception {
		if (!customer.isCanBeUsed())
			return;
		List<Analysis> analyses = daoAnalysis.getAllFromCustomer(customer);
		if (analyses.size() > 0)
			throw new TrickException("error.delete.customer.has_analyses", "Customer could not be deleted: there are still analyses of this customer!");

		for (Analysis analysis : analyses)
			daoAnalysis.delete(analysis);
		List<User> users = daoUser.getAllFromCustomer(customer);
		for (User user : users) {
			user.getCustomers().remove(customer);
			daoUser.saveOrUpdate(user);
		}
		daoCustomer.delete(customer);
	}

	@Transactional
	public void removeCustomerByUser(Customer customer, String userName) throws Exception {

		if (!customer.isCanBeUsed())
			return;

		List<Analysis> analyses = daoAnalysis.getAllFromUserAndCustomer(userName, customer.getId());

		User user = daoUser.get(userName);
		for (Analysis analysis : analyses) {
			analysis.removeRights(user);
			daoAnalysis.saveOrUpdate(analysis);
		}

		user.getCustomers().remove(customer);
		if (!user.containsCustomer(customer))
			daoUser.saveOrUpdate(user);
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
			if(contains)
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
	public boolean deleteAnalysis(List<Integer> ids) {
		try {
			Collections.sort(ids, Collections.reverseOrder());
			for (Integer id : ids)
				daoAnalysis.delete(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
