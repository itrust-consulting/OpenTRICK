package lu.itrust.business.TS.database.dao;

import java.util.List;

import lu.itrust.business.TS.data.analysis.Analysis;
import lu.itrust.business.TS.data.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.data.analysis.rights.UserAnalysisRight;
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
	public UserAnalysisRight get(Integer id) throws Exception;

	public UserAnalysisRight getFromAnalysisAndUser(Analysis analysis, User user) throws Exception;

	public AnalysisRight getAnalysisRightOfUser(Analysis analysis, User user) throws Exception;

	public boolean isUserAuthorized(Integer analysisId, Integer userId, AnalysisRight right) throws Exception;

	public boolean isUserAuthorized(Integer idAnalysis, String username, AnalysisRight right) throws Exception;

	public boolean isUserAuthorized(Analysis analysis, User user, AnalysisRight right) throws Exception;

	public List<UserAnalysisRight> getAllFromAnalysis(Integer analysisid) throws Exception;

	public List<UserAnalysisRight> getAllFromAnalysis(Analysis analysis) throws Exception;

	public List<UserAnalysisRight> getAllFromUser(String login) throws Exception;

	public List<UserAnalysisRight> getAllFromUser(User user) throws Exception;

	public void save(UserAnalysisRight userAnalysisRight) throws Exception;

	public void saveOrUpdate(UserAnalysisRight userAnalysisRight) throws Exception;

	public void delete(Integer id) throws Exception;

	public void delete(UserAnalysisRight userAnalysisRight) throws Exception;
	
}