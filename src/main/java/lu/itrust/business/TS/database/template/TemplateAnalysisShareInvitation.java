package lu.itrust.business.TS.database.template;

import java.util.List;

import lu.itrust.business.TS.database.TemplateDAOService;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.AnalysisShareInvitation;
import lu.itrust.business.TS.model.general.helper.InvitationFilter;
import lu.itrust.business.TS.usermanagement.User;

public interface TemplateAnalysisShareInvitation extends TemplateDAOService<AnalysisShareInvitation, Long> {

	List<AnalysisShareInvitation> findByAnalysisId(Integer idAnalysis);
	
	List<AnalysisShareInvitation> findByEmail(String email);
	
	AnalysisShareInvitation findByEmailAndAnalysisId(String email, int analysisId);
	
	AnalysisShareInvitation findByIdAndUsername(Long id, String username);
	
	List<AnalysisShareInvitation> findAllByUsernameAndFilterControl(String username, Integer page, InvitationFilter filter);
	
	AnalysisShareInvitation findByToken(String token);
	
	String findTokenByIdAndUsername(Long id, String username);
	
	boolean exists(String token);
	
	void deleteByUser(User user);
	
	void deleteByAnalysis(Analysis analysis);
	
	long countByEmail(String email);
	
	long countByUsername(String username);
	
	
	
	
}
