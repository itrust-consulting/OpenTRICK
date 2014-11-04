package lu.itrust.business.TS.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lu.itrust.business.TS.data.analysis.Analysis;
import lu.itrust.business.TS.data.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.data.analysis.rights.UserAnalysisRight;
import lu.itrust.business.TS.database.dao.DAOAnalysis;
import lu.itrust.business.TS.database.dao.DAOUser;
import lu.itrust.business.TS.database.dao.DAOUserAnalysisRight;
import lu.itrust.business.TS.database.service.ServiceUserAnalysisRight;
import lu.itrust.business.TS.usermanagement.User;

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
	 * @param id
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceUserAnalysisRight#get(long)
	 */
	@Override
	public UserAnalysisRight get(Integer id) throws Exception {
		return daoUserAnalysisRight.get(id);
	}

	/**
	 * getUserAnalysisRight: <br>
	 * Description
	 * 
	 * @param analysis
	 * @param user
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceUserAnalysisRight#getUserAnalysisRight(lu.itrust.business.TS.data.analysis.Analysis,
	 *      lu.itrust.business.TS.usermanagement.User)
	 */
	@Override
	public UserAnalysisRight getFromAnalysisAndUser(Analysis analysis, User user) throws Exception {
		return daoUserAnalysisRight.getFromAnalysisAndUser(analysis, user);
	}

	/**
	 * isUserAuthorized: <br>
	 * Description
	 * 
	 * @param analysisId
	 * @param userId
	 * @param right
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceUserAnalysisRight#isUserAuthorized(java.lang.Integer,
	 *      java.lang.Integer, lu.itrust.business.TS.data.analysis.rights.AnalysisRight)
	 */
	@Override
	public boolean isUserAuthorized(Integer analysisId, Integer userId, AnalysisRight right) throws Exception {
		return daoUserAnalysisRight.isUserAuthorized(analysisId, userId, right);
	}

	/**
	 * isUserAuthorized: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param username
	 * @param right
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceUserAnalysisRight#isUserAuthorized(java.lang.Integer,
	 *      java.lang.String, lu.itrust.business.TS.data.analysis.rights.AnalysisRight)
	 */
	@Override
	public boolean isUserAuthorized(Integer idAnalysis, String username, AnalysisRight right) throws Exception {
		return this.daoUserAnalysisRight.isUserAuthorized(idAnalysis, username, right);
	}

	/**
	 * isUserAuthorized: <br>
	 * Description
	 * 
	 * @param analysis
	 * @param user
	 * @param right
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceUserAnalysisRight#isUserAuthorized(lu.itrust.business.TS.data.analysis.Analysis,
	 *      lu.itrust.business.TS.usermanagement.User, lu.itrust.business.TS.data.analysis.rights.AnalysisRight)
	 */
	@Override
	public boolean isUserAuthorized(Analysis analysis, User user, AnalysisRight right) throws Exception {
		return daoUserAnalysisRight.isUserAuthorized(analysis, user, right);
	}

	/**
	 * getAnalysisRightOfUser: <br>
	 * Description
	 * 
	 * @param analysis
	 * @param user
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceUserAnalysisRight#getAnalysisRightOfUser(lu.itrust.business.TS.data.analysis.Analysis,
	 *      lu.itrust.business.TS.usermanagement.User)
	 */
	@Override
	public AnalysisRight getAnalysisRightOfUser(Analysis analysis, User user) throws Exception {
		return daoUserAnalysisRight.getAnalysisRightOfUser(analysis, user);
	}

	/**
	 * getAllFromAnalysisId: <br>
	 * Description
	 * 
	 * @param analysisid
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceUserAnalysisRight#getAllFromAnalysisId(int)
	 */
	@Override
	public List<UserAnalysisRight> getAllFromAnalysis(Integer analysisid) throws Exception {
		return daoUserAnalysisRight.getAllFromAnalysis(analysisid);
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @param analysis
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceUserAnalysisRight#getAllFromAnalysis(lu.itrust.business.TS.data.analysis.Analysis)
	 */
	@Override
	public List<UserAnalysisRight> getAllFromAnalysis(Analysis analysis) throws Exception {
		return daoUserAnalysisRight.getAllFromAnalysis(analysis);
	}

	/**
	 * getAllFromUserLogin: <br>
	 * Description
	 * 
	 * @param login
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceUserAnalysisRight#getAllFromUserLogin(java.lang.String)
	 */
	@Override
	public List<UserAnalysisRight> getAllFromUser(String login) throws Exception {
		return daoUserAnalysisRight.getAllFromUser(login);
	}

	/**
	 * getAllFromUser: <br>
	 * Description
	 * 
	 * @param user
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceUserAnalysisRight#getAllFromUser(lu.itrust.business.TS.usermanagement.User)
	 */
	@Override
	public List<UserAnalysisRight> getAllFromUser(User user) throws Exception {
		return daoUserAnalysisRight.getAllFromUser(user);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param userAnalysisRight
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceUserAnalysisRight#save(lu.itrust.business.TS.data.analysis.rights.UserAnalysisRight)
	 */
	@Override
	public void save(UserAnalysisRight userAnalysisRight) throws Exception {
		this.daoUserAnalysisRight.save(userAnalysisRight);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param userAnalysisRight
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceUserAnalysisRight#saveOrUpdate(lu.itrust.business.TS.data.analysis.rights.UserAnalysisRight)
	 */
	@Override
	public void saveOrUpdate(UserAnalysisRight userAnalysisRight) throws Exception {
		this.daoUserAnalysisRight.saveOrUpdate(userAnalysisRight);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param userAnalysisRight
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceUserAnalysisRight#delete(lu.itrust.business.TS.data.analysis.rights.UserAnalysisRight)
	 */
	@Override
	public void delete(UserAnalysisRight userAnalysisRight) throws Exception {
		this.daoUserAnalysisRight.delete(userAnalysisRight);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param id
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceUserAnalysisRight#delete(long)
	 */
	@Override
	public void delete(Integer id) throws Exception {
		this.daoUserAnalysisRight.delete(get(id));
	}
}