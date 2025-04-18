package lu.itrust.business.ts.database.service;

import java.util.List;

import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.analysis.rights.AnalysisRight;
import lu.itrust.business.ts.model.analysis.rights.UserAnalysisRight;
import lu.itrust.business.ts.usermanagement.User;

/**
 * ServiceUserAnalysisRight.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.a.rl.
 * @version
 * @since Jan 9, 2013
 */
public interface ServiceUserAnalysisRight {

	public void delete(Integer id);

	public void delete(UserAnalysisRight userAnalysisRight);

	public UserAnalysisRight get(Integer id);

	public List<UserAnalysisRight> getAllFromAnalysis(Analysis analysis);

	public List<UserAnalysisRight> getAllFromAnalysis(Integer analysisid);

	public List<UserAnalysisRight> getAllFromIdenfierExceptAnalysisIdAndRightNotRead(String identifier, int AnalysisId);

	public List<UserAnalysisRight> getAllFromUser(String login);

	public List<UserAnalysisRight> getAllFromUser(User user);

	public AnalysisRight getAnalysisRightOfUser(Analysis analysis, User user);

	public UserAnalysisRight getFromAnalysisAndUser(Analysis analysis, User user);

	public boolean hasDeletePermission(Integer idAnalysis, String username, Boolean isProfile);

	public boolean hasManagementPermission(Integer idAnalysis, String username);

	public boolean hasRightOrOwner(int idAnalysis, String username, AnalysisRight right);

	public boolean isUserAuthorized(Analysis analysis, User user, AnalysisRight right);

	public boolean isUserAuthorized(int idAnalysis, String username, List<AnalysisRight> rights);

	public boolean isUserAuthorized(Integer analysisId, Integer userId, AnalysisRight right);

	/**
	 * Check if user has access, if analysis has been archived: READ ONLY is
	 * authorised.
	 * 
	 * @param analysisId
	 * @param name
	 * @param right
	 * @return true/ false
	 */
	public boolean isUserAuthorized(Integer idAnalysis, String username, AnalysisRight right);

	public void save(UserAnalysisRight userAnalysisRight);

	public void saveOrUpdate(UserAnalysisRight userAnalysisRight);

}