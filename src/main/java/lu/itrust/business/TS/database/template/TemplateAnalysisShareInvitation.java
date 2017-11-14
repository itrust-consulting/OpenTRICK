package lu.itrust.business.TS.database.template;

import java.util.List;

import lu.itrust.business.TS.database.TemplateDAOService;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.AnalysisShareInvitation;
import lu.itrust.business.TS.usermanagement.User;

public interface TemplateAnalysisShareInvitation extends TemplateDAOService<AnalysisShareInvitation, Long> {

	List<AnalysisShareInvitation> findByAnalysisId(Integer idAnalysis);
	
	AnalysisShareInvitation findByEmailAndAnalysisId(String email, int analysisId);
	
	AnalysisShareInvitation findByToken(String token);
	
	boolean exists(String token);
	
	void deleteByUser(User user);
	
	void deleteByAnalysis(Analysis analysis);
}
