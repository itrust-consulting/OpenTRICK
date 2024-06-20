package lu.itrust.business.ts.database.template;

import java.util.List;

import lu.itrust.business.ts.database.TemplateDAOService;
import lu.itrust.business.ts.model.analysis.AnalysisType;
import lu.itrust.business.ts.model.general.document.impl.TrickTemplate;

public interface TemplateTrickTemplate extends TemplateDAOService<TrickTemplate, Long> {
	
	TrickTemplate findByIdAndCustomer(long id, int customerId);
	
	List<TrickTemplate> findByCustomer(int customerId);
	
	List<TrickTemplate> findByCustomerAndType(int customerId, AnalysisType type);
	
	TrickTemplate findByIdAndCustomerOrDefault(Long id, Integer customerId);
	
	boolean isUseAuthorised(Long id, Integer customerId);
	
	List<TrickTemplate> findDefault();
	
	AnalysisType findTypeById(Long id);
}
