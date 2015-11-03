/**
 * 
 */
package lu.itrust.business.TS.component;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOCustomer;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.general.Customer;
import lu.itrust.business.TS.model.general.LogAction;
import lu.itrust.business.TS.model.general.LogLevel;
import lu.itrust.business.TS.model.general.LogType;

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
	public void switchCustomer(String identifier, int idCustomer, String username) throws Exception {
		List<Analysis> analyses = daoAnalysis.getAllByIdentifier(identifier);
		Customer customer = daoCustomer.get(idCustomer);
		/**
		 * Log
		 */
		analyses.stream()
				.filter(analysis -> analysis.getCustomer() != customer)
				.findAny()
				.ifPresent(
						analysis -> TrickLogManager.Persist(LogLevel.WARNING, LogType.ANALYSIS, "log.user.switch.analysis.customer",
								String.format("Analysis: %s, old: %s, new: %s", analysis.getIdentifier(), analysis.getCustomer().getOrganisation(), customer.getOrganisation()),
								username, LogAction.SWITCH_CUSTOMER, analysis.getIdentifier(), analysis.getCustomer().getOrganisation(), customer.getOrganisation()));
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
