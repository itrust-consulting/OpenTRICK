/**
 * 
 */
package lu.itrust.business.component;

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
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.dao.DAOAnalysis;
import lu.itrust.business.dao.DAOAssessment;
import lu.itrust.business.dao.DAOAsset;
import lu.itrust.business.dao.DAOAssetTypeValue;
import lu.itrust.business.dao.DAOCustomer;
import lu.itrust.business.dao.DAOMeasureDescription;
import lu.itrust.business.dao.DAOMeasureDescriptionText;
import lu.itrust.business.dao.DAONorm;
import lu.itrust.business.dao.DAOScenario;
import lu.itrust.business.dao.DAOUser;

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
	private DAOUser daoUser;
	
	@Autowired
	private DAOAssetTypeValue daoAssetTypeValue;

	@Transactional
	// TODO check if actionplan needs to be cleared
	public void deleteAsset(Asset asset) throws Exception {
		List<Assessment> assessments = daoAssessment.loadAllFromAsset(asset);
		for (Assessment assessment : assessments)
			daoAssessment.remove(assessment);
		daoAsset.delete(asset);
	}

	@Transactional
	public void deleteScenario(Scenario scenario) throws Exception {
		List<Assessment> assessments = daoAssessment.loadAllFromScenario(scenario);
		for (Assessment assessment : assessments)
			daoAssessment.remove(assessment);
		daoScenario.remove(scenario);
	}

	@Transactional
	public void deleteNorm(Norm norm) throws Exception {
		List<MeasureDescription> measureDescriptions = daoMeasureDescription.getAllByNorm(norm);
		for (MeasureDescription measureDescription : measureDescriptions) {
			List<MeasureDescriptionText> measureDescriptionTexts = daoMeasureDescriptionText.getByMeasureDescription(measureDescription.getId());
			for (MeasureDescriptionText measureDescriptiontext : measureDescriptionTexts) {
				daoMeasureDescriptionText.remove(measureDescriptiontext);
			}
			daoMeasureDescription.remove(measureDescription);
		}
		daoNorm.remove(norm);
	}
	
	@Transactional
	public void deleteDuplicationAssetTypeValue(List<Scenario> scenarios) throws Exception{
		for (Scenario scenario : scenarios) {
			List<AssetTypeValue> assetTypeValues = scenario.deleteAssetTypeDuplication();
			daoScenario.saveOrUpdate(scenario);
			daoAssetTypeValue.delete(assetTypeValues);
		}
	}

	@Transactional
	public void deleteCustomer(Customer customer) throws Exception {
		if(!customer.isCanBeUsed())
			return;
		List<Analysis> analyses = daoAnalysis.loadAllFromCustomer(customer);
		for (Analysis analysis : analyses)
			daoAnalysis.remove(analysis);
		List<User> users = daoUser.loadByCustomer(customer);
		for (User user : users) {
			user.getCustomers().remove(customer);
			daoUser.saveOrUpdate(user);
		}
		daoCustomer.remove(customer);
	}

	@Transactional
	public void deleteCustomerByUser(Customer customer, String userName) throws Exception {
		
		if (!customer.isCanBeUsed())
			return;
		
		List<Analysis> analyses = daoAnalysis.loadByUserAndCustomer(userName, customer.getOrganisation());
		User user = daoUser.get(userName);
		for (Analysis analysis : analyses) {
			analysis.removeRights(user);
			daoAnalysis.saveOrUpdate(analysis);
		}

		user.getCustomers().remove(customer);
		if (!user.containsCustomer(customer))
			daoUser.saveOrUpdate(user);

		if (!daoCustomer.hasUser(customer.getId()) && analyses.isEmpty())
			daoCustomer.remove(customer);
	}

}
