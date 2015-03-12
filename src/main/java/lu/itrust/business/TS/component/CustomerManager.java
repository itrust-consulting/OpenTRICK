/**
 * 
 */
package lu.itrust.business.TS.component;

import java.util.List;

import javax.transaction.Transactional;

import lu.itrust.business.TS.data.analysis.Analysis;
import lu.itrust.business.TS.data.general.Customer;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOCustomer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author eomar
 *
 */
@Component
public class CustomerManager {

	@Autowired
	private DAOCustomer daoCustomer;

	@Autowired
	private DAOAnalysis daoAnalysis;

	@Transactional
	public void switchCustomer(String identifier, int idCustomer) throws Exception {
		List<Analysis> analyses = daoAnalysis.getAllByIdentifier(identifier);
		Customer customer = daoCustomer.get(idCustomer);
		for (Analysis analysis : analyses) {
			analysis.setCustomer(customer);
			analysis.getUserRights().stream().forEach(userAnalysisRight -> {
				if (!userAnalysisRight.getUser().containsCustomer(customer))
					userAnalysisRight.getUser().addCustomer(customer);
			});
			daoAnalysis.saveOrUpdate(analysis);
		}
	}

}
