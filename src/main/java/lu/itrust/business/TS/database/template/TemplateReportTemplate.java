package lu.itrust.business.TS.database.template;

import java.util.List;

import lu.itrust.business.TS.database.TemplateDAOService;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.general.document.impl.ReportTemplate;

public interface TemplateReportTemplate extends TemplateDAOService<ReportTemplate, Long> {
	
	ReportTemplate findByIdAndCustomer(long id, int customerId);
	
	List<ReportTemplate> findByCustomer(int customerId);
	
	List<ReportTemplate> findByCustomerAndType(int customerId, AnalysisType type);
}
