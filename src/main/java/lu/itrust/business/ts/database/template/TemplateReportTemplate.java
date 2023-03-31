package lu.itrust.business.ts.database.template;

import java.util.List;

import lu.itrust.business.ts.database.TemplateDAOService;
import lu.itrust.business.ts.model.analysis.AnalysisType;
import lu.itrust.business.ts.model.general.document.impl.ReportTemplate;

public interface TemplateReportTemplate extends TemplateDAOService<ReportTemplate, Long> {
	
	ReportTemplate findByIdAndCustomer(long id, int customerId);
	
	List<ReportTemplate> findByCustomer(int customerId);
	
	List<ReportTemplate> findByCustomerAndType(int customerId, AnalysisType type);
	
	ReportTemplate findByIdAndCustomerOrDefault(Long id, Integer customerId);
	
	Boolean isUseAuthorised(Long id, Integer customerId);
	
	List<ReportTemplate> findDefault();
	
	AnalysisType findTypeById(Long id);
}
