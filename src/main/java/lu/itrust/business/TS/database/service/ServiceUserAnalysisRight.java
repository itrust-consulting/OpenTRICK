package lu.itrust.business.TS.database.service;

import java.util.List;

import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.analysis.rights.UserAnalysisRight;
import lu.itrust.business.TS.usermanagement.User;

/**
 * ServiceUserAnalysisRight.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.a.rl.
 * @version
 * @since Jan 9, 2013
 */
public interface ServiceUserAnalysisRight {
	
	public UserAnalysisRight get(Integer id);

	public UserAnalysisRight getFromAnalysisAndUser(Analysis analysis, User user);

	public AnalysisRight getAnalysisRightOfUser(Analysis analysis, User user);

	public boolean isUserAuthorized(Integer analysisId, Integer userId, AnalysisRight right);

	public boolean isUserAuthorized(Integer idAnalysis, String username, AnalysisRight right);

	public boolean isUserAuthorized(Analysis analysis, User user, AnalysisRight right);

	public List<UserAnalysisRight> getAllFromAnalysis(Integer analysisid);

	public List<UserAnalysisRight> getAllFromAnalysis(Analysis analysis);

	public List<UserAnalysisRight> getAllFromUser(String login);

	public List<UserAnalysisRight> getAllFromUser(User user);

	public void save(UserAnalysisRight userAnalysisRight);

	public void saveOrUpdate(UserAnalysisRight userAnalysisRight);

	public void delete(Integer id);

	public void delete(UserAnalysisRight userAnalysisRight);

	public List<UserAnalysisRight> getAllFromIdenfierExceptAnalysisIdAndRightNotRead(String identifier, int AnalysisId);

	public boolean hasRightOrOwner(int idAnalysis, String username, AnalysisRight right);

	public boolean isUserAuthorized(int idAnalysis, String username, List<AnalysisRight> rights);

}