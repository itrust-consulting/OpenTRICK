package lu.itrust.business.service;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.AnalysisRight;
import lu.itrust.business.TS.UserAnalysisRight;
import lu.itrust.business.TS.usermanagement.User;

public interface ServiceUserAnalysisRight {
	UserAnalysisRight get(long id) throws Exception;

	List<UserAnalysisRight> getAllByUser(String login) throws Exception;

	List<UserAnalysisRight> getAllByUser(User user) throws Exception;

	List<UserAnalysisRight> getAllByUniqueAnalysis(Analysis analysis) throws Exception;

	List<UserAnalysisRight> getAllByAnalysisIdentifier(String identifier) throws Exception;
	
	boolean isUserAuthorized(Integer selected, String name, AnalysisRight read);
	
	public boolean isUserAuthorized(Analysis analysis, User user, AnalysisRight right) throws Exception;

	public boolean isUserAuthorized(Integer analysisId, Integer userId, AnalysisRight right) throws Exception;
	
	public UserAnalysisRight getUserAnalysisRight(Analysis analysis, User user) throws Exception;
	
	public AnalysisRight getAnalysisRightOfUser(Analysis analysis, User user) throws Exception;
	
	void save(UserAnalysisRight userAnalysisRight) throws Exception;

	void saveOrUpdate(UserAnalysisRight userAnalysisRight) throws Exception;

	void delete(UserAnalysisRight userAnalysisRight) throws Exception;

	void delete(long id) throws Exception;

	
}