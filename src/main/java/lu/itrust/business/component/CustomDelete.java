/**
 * 
 */
package lu.itrust.business.component;

import java.util.Iterator;
import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.Assessment;
import lu.itrust.business.TS.Asset;
import lu.itrust.business.TS.AssetTypeValue;
import lu.itrust.business.TS.Customer;
import lu.itrust.business.TS.MeasureDescription;
import lu.itrust.business.TS.MeasureDescriptionText;
import lu.itrust.business.TS.Norm;
import lu.itrust.business.TS.Scenario;
import lu.itrust.business.TS.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.actionplan.SummaryStage;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.dao.DAOActionPlan;
import lu.itrust.business.dao.DAOActionPlanSummary;
import lu.itrust.business.dao.DAOAnalysis;
import lu.itrust.business.dao.DAOAnalysisNorm;
import lu.itrust.business.dao.DAOAssessment;
import lu.itrust.business.dao.DAOAsset;
import lu.itrust.business.dao.DAOAssetTypeValue;
import lu.itrust.business.dao.DAOCustomer;
import lu.itrust.business.dao.DAOMeasureDescription;
import lu.itrust.business.dao.DAOMeasureDescriptionText;
import lu.itrust.business.dao.DAONorm;
import lu.itrust.business.dao.DAOScenario;
import lu.itrust.business.dao.DAOUser;
import lu.itrust.business.exception.TrickException;

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
	private DAONorm daoNorm;

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
	private DAOAnalysisNorm daoAnalysisNorm;

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

		List<ActionPlanEntry> actionplans = daoActionPlan.getAllFromAsset(asset);

		if (!actionplans.isEmpty()) {

			Integer analysisid = actionplans.get(0).getMeasure().getAnalysisNorm().getAnalysis().getId();

			String type = actionplans.get(0).getActionPlanType().getName();

			for (ActionPlanEntry actionplanentry : actionplans) {

				daoActionPlan.delete(actionplanentry);
			}

			if (analysisid != null) {

				List<SummaryStage> summary = daoActionPlanSummary.getAllFromAnalysisAndActionPlanType(analysisid, type);

				for (SummaryStage stage : summary) {
					daoActionPlanSummary.delete(stage);
				}
			} else
				throw new Exception("Could not get analysis id!");
		}
		List<Assessment> assessments = daoAssessment.getAllFromAsset(asset);
		for (Assessment assessment : assessments)
			daoAssessment.delete(assessment);
		daoAsset.delete(asset);
	}

	@Transactional
	public void deleteScenario(Scenario scenario) throws Exception {
		List<Assessment> assessments = daoAssessment.getAllFromScenario(scenario);
		for (Assessment assessment : assessments)
			daoAssessment.delete(assessment);
		daoScenario.delete(scenario);
	}

	@Transactional
	public void deleteNorm(Norm norm) throws Exception {
		if (daoAnalysisNorm.getAllFromNorm(norm).size() > 0)
			throw new TrickException("error.delete.norm.analyses_with_norm","Standard could not be deleted: it is used in analyses!");

		List<MeasureDescription> measureDescriptions = daoMeasureDescription.getAllByNorm(norm);
		for (MeasureDescription measureDescription : measureDescriptions) {
			List<MeasureDescriptionText> measureDescriptionTexts = daoMeasureDescriptionText.getAllFromMeasureDescription(measureDescription.getId());
			for (MeasureDescriptionText measureDescriptiontext : measureDescriptionTexts) {
				daoMeasureDescriptionText.delete(measureDescriptiontext);
			}
			daoMeasureDescription.delete(measureDescription);
		}
		daoNorm.delete(norm);

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
			throw new TrickException("error.delete.customer.has_analyses","Customer could not be deleted: there are still analyses of this customer!");

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
	public void delete(MeasureDescription measureDescription) throws Exception{
		Iterator<MeasureDescriptionText> iterator = measureDescription.getMeasureDescriptionTexts().iterator();
		while(iterator.hasNext()){
			MeasureDescriptionText descriptionText = iterator.next();
			iterator.remove();
			descriptionText.setMeasureDescription(null);
			daoMeasureDescriptionText.delete(descriptionText);
		}
		daoMeasureDescription.delete(measureDescription);
	}

}
