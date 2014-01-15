package lu.itrust.business.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.AnalysisRight;
import lu.itrust.business.TS.UserAnalysisRight;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.dao.DAOAnalysis;
import lu.itrust.business.dao.DAOUser;
import lu.itrust.business.dao.DAOUserAnalysisRight;
import lu.itrust.business.service.ServiceUserAnalysisRight;

/**
 * serviceUserAnalysisRightImpl.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.à.rl. :
 * @version
 * @since Jan 9, 2014
 */
@Service
public class ServiceUserAnalysisRightImpl implements ServiceUserAnalysisRight {

	@Autowired
	private DAOUserAnalysisRight daoUserAnalysisRight;
	
	@Autowired
	private DAOAnalysis daoAnalysis;
	
	@Autowired
	private DAOUser daoUser;
	
	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceUserAnalysisRight#get(long)
	 */
	@Override
	public UserAnalysisRight get(long id) throws Exception {
		return daoUserAnalysisRight.get(id);
	}

	/**
	 * getAllByUser: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceUserAnalysisRight#getAllByUser(java.lang.String)
	 */
	@Override
	public List<UserAnalysisRight> getAllByUser(String login) throws Exception {
		return daoUserAnalysisRight.getAllByUser(login);
	}

	/**
	 * getAllByUser: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceUserAnalysisRight#getAllByUser(lu.itrust.business.TS.usermanagement.User)
	 */
	@Override
	public List<UserAnalysisRight> getAllByUser(User user) throws Exception {
		return daoUserAnalysisRight.getAllByUser(user);
	}

	/**
	 * getAllByUniqueAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceUserAnalysisRight#getAllByUniqueAnalysis(lu.itrust.business.TS.Analysis)
	 */
	@Override
	public List<UserAnalysisRight> getAllByUniqueAnalysis(Analysis analysis) throws Exception {
		return daoUserAnalysisRight.getAllByUniqueAnalysis(analysis);

	}

	/**
	 * getAllByAnalysisIdentifier: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceUserAnalysisRight#getAllByAnalysisIdentifier(java.lang.String)
	 */
	@Override
	public List<UserAnalysisRight> getAllByAnalysisIdentifier(String identifier) throws Exception {
		return daoUserAnalysisRight.getAllByAnalysisIdentifier(identifier);
	}

	/**
	 * isUserAuthorized: <br>
	 * Description
	 *
	 * @see lu.itrust.business.service.ServiceUserAnalysisRight#isUserAuthorized(lu.itrust.business.TS.Analysis, lu.itrust.business.TS.usermanagement.User, lu.itrust.business.TS.AnalysisRight)
	 */
	@Override
	public boolean isUserAuthorized(Analysis analysis, User user, AnalysisRight right) throws Exception {
		return daoUserAnalysisRight.isUserAuthorized(analysis, user, right);
	}

	/**
	 * isUserAuthorized: <br>
	 * Description
	 *
	 * @see lu.itrust.business.service.ServiceUserAnalysisRight#isUserAuthorized(java.lang.Integer, java.lang.Integer, lu.itrust.business.TS.AnalysisRight)
	 */
	@Override
	public boolean isUserAuthorized(Integer analysisId, Integer userId, AnalysisRight right) throws Exception {
		return daoUserAnalysisRight.isUserAuthorized(analysisId, userId, right);
	}
	
	@Override
	public UserAnalysisRight getUserAnalysisRight(Analysis analysis, User user) throws Exception {
		return daoUserAnalysisRight.getUserAnalysisRight(analysis, user);
	}
	
	@Override
	public AnalysisRight getAnalysisRightOfUser(Analysis analysis, User user) throws Exception {
		return daoUserAnalysisRight.getAnalysisRightOfUser(analysis, user);
	}
	
	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceUserAnalysisRight#save(lu.itrust.business.TS.UserAnalysisRight)
	 */
	@Override
	public void save(UserAnalysisRight userAnalysisRight) throws Exception {
		this.daoUserAnalysisRight.save(userAnalysisRight);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceUserAnalysisRight#saveOrUpdate(lu.itrust.business.TS.UserAnalysisRight)
	 */
	@Override
	public void saveOrUpdate(UserAnalysisRight userAnalysisRight) throws Exception {
		this.daoUserAnalysisRight.saveOrUpdate(userAnalysisRight);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceUserAnalysisRight#delete(lu.itrust.business.TS.UserAnalysisRight)
	 */
	@Override
	public void delete(UserAnalysisRight userAnalysisRight) throws Exception {
		this.daoUserAnalysisRight.delete(userAnalysisRight);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceUserAnalysisRight#delete(long)
	 */
	@Override
	public void delete(long id) throws Exception {
		this.daoUserAnalysisRight.delete(get(id));
	}

	@Override
	public boolean isUserAuthorized(Integer idAnalysis, String username, AnalysisRight right) {
		return this.daoUserAnalysisRight.isUserAuthorized(idAnalysis,username,right );
	}
}
