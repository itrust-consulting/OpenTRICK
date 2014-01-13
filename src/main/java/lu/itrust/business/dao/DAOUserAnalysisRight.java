package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.AnalysisRight;
import lu.itrust.business.TS.UserAnalysisRight;
import lu.itrust.business.TS.usermanagment.User;

/**
 * DAOUserAnalysisRight.java: <br>
 * Detailed description...
 *
 * @author smenghi, itrust consulting s.à.rl. :
 * @version 
 * @since Jan 9, 2014
 */
public interface DAOUserAnalysisRight {
		
		UserAnalysisRight get(long id) throws Exception;
		
		List<UserAnalysisRight> getAllByUser(String login) throws Exception;
		
		List<UserAnalysisRight> getAllByUser(User user) throws Exception;
		
		List<UserAnalysisRight> getAllByUniqueAnalysis(Analysis analysis) throws Exception;
		
		List<UserAnalysisRight> getAllByAnalysisIdentifier(String identifier) throws Exception;
		
		void save(UserAnalysisRight userAnalysisRight)throws Exception;
		
		void saveOrUpdate(UserAnalysisRight userAnalysisRight)throws Exception;
		
		void delete(UserAnalysisRight userAnalysisRight)throws Exception;
		
		void delete(long id)throws Exception;

		public UserAnalysisRight getUserAnalysisRight(Analysis analysis, User user) throws Exception;

		public AnalysisRight getAnalysisRightOfUser(Analysis analysis, User user) throws Exception;
		
		public boolean isUserAuthorized(Analysis analysis, User user, AnalysisRight right) throws Exception;
		
}
