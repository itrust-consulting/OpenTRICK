package lu.itrust.business.TS.database.dao;

import java.util.List;

import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.analysis.rights.UserAnalysisRight;
import lu.itrust.business.TS.usermanagement.User;

/**
 * DAOUserAnalysisRight.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.à.rl. :
 * @version
 * @since Jan 9, 2014
 */
public interface DAOUserAnalysisRight {
	public void delete(Integer id);

	public void delete(UserAnalysisRight userAnalysisRight);

	public void deleteByUser(User user);

	public UserAnalysisRight get(Integer id);

	public List<UserAnalysisRight> getAllFromAnalysis(Analysis analysis);

	public List<UserAnalysisRight> getAllFromAnalysis(Integer analysisid);

	public List<UserAnalysisRight> getAllFromIdenfierExceptAnalysisIdAndRightNotRead(String identifier, int analysisId);

	public List<UserAnalysisRight> getAllFromUser(String login);

	public List<UserAnalysisRight> getAllFromUser(User user);

	public AnalysisRight getAnalysisRightOfUser(Analysis analysis, User user);

	public UserAnalysisRight getFromAnalysisAndUser(Analysis analysis, User user);

	public boolean hasRightOrOwner(int idAnalysis, String username, AnalysisRight right);

	public boolean isUserAuthorized(Analysis analysis, User user, AnalysisRight right);

	public boolean isUserAuthorized(int idAnalysis, String username, List<AnalysisRight> rights);

	public boolean isUserAuthorized(Integer analysisId, Integer userId, AnalysisRight right);

	public boolean isUserAuthorized(Integer idAnalysis, String username, AnalysisRight right);

	public boolean isUserAuthorizedOrOwner(String identifier, String version, User owner, AnalysisRight right);

	public void save(UserAnalysisRight userAnalysisRight);

	public void saveOrUpdate(UserAnalysisRight userAnalysisRight);

	public boolean hasDeletePermission(Integer idAnalysis, String username, Boolean isProfile);

	public boolean hasManagementPermission(Integer idAnalysis, String username);

}