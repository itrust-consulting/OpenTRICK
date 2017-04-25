package lu.itrust.business.TS.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lu.itrust.business.TS.database.dao.DAOUserAnalysisRight;
import lu.itrust.business.TS.database.service.ServiceUserAnalysisRight;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.analysis.rights.UserAnalysisRight;
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

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @
	 * 
	 * 	@see
	 *   lu.itrust.business.TS.database.service.ServiceUserAnalysisRight#get(long)
	 */
	@Override
	public UserAnalysisRight get(Integer id) {
		return daoUserAnalysisRight.get(id);
	}

	/**
	 * getUserAnalysisRight: <br>
	 * Description
	 * 
	 * @param analysis
	 * @param user
	 * @return
	 * @
	 * 
	 * 	@see
	 *   lu.itrust.business.TS.database.service.ServiceUserAnalysisRight#getUserAnalysisRight(lu.itrust.business.TS.model.analysis.Analysis,
	 *   lu.itrust.business.TS.usermanagement.User)
	 */
	@Override
	public UserAnalysisRight getFromAnalysisAndUser(Analysis analysis, User user) {
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
	 * @
	 * 
	 * 	@see
	 *   lu.itrust.business.TS.database.service.ServiceUserAnalysisRight#isUserAuthorized(java.lang.Integer,
	 *   java.lang.Integer,
	 *   lu.itrust.business.TS.model.analysis.rights.AnalysisRight)
	 */
	@Override
	public boolean isUserAuthorized(Integer analysisId, Integer userId, AnalysisRight right) {
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
	 * @
	 * 
	 * 	@see
	 *   lu.itrust.business.TS.database.service.ServiceUserAnalysisRight#isUserAuthorized(java.lang.Integer,
	 *   java.lang.String,
	 *   lu.itrust.business.TS.model.analysis.rights.AnalysisRight)
	 */
	@Override
	public boolean isUserAuthorized(Integer idAnalysis, String username, AnalysisRight right) {
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
	 * @
	 * 
	 * 	@see
	 *   lu.itrust.business.TS.database.service.ServiceUserAnalysisRight#isUserAuthorized(lu.itrust.business.TS.model.analysis.Analysis,
	 *   lu.itrust.business.TS.usermanagement.User,
	 *   lu.itrust.business.TS.model.analysis.rights.AnalysisRight)
	 */
	@Override
	public boolean isUserAuthorized(Analysis analysis, User user, AnalysisRight right) {
		return daoUserAnalysisRight.isUserAuthorized(analysis, user, right);
	}

	/**
	 * getAnalysisRightOfUser: <br>
	 * Description
	 * 
	 * @param analysis
	 * @param user
	 * @return
	 * @
	 * 
	 * 	@see
	 *   lu.itrust.business.TS.database.service.ServiceUserAnalysisRight#getAnalysisRightOfUser(lu.itrust.business.TS.model.analysis.Analysis,
	 *   lu.itrust.business.TS.usermanagement.User)
	 */
	@Override
	public AnalysisRight getAnalysisRightOfUser(Analysis analysis, User user) {
		return daoUserAnalysisRight.getAnalysisRightOfUser(analysis, user);
	}

	/**
	 * getAllFromAnalysisId: <br>
	 * Description
	 * 
	 * @param analysisid
	 * @return
	 * @
	 * 
	 * 	@see
	 *   lu.itrust.business.TS.database.service.ServiceUserAnalysisRight#getAllFromAnalysisId(int)
	 */
	@Override
	public List<UserAnalysisRight> getAllFromAnalysis(Integer analysisid) {
		return daoUserAnalysisRight.getAllFromAnalysis(analysisid);
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @param analysis
	 * @return
	 * @
	 * 
	 * 	@see
	 *   lu.itrust.business.TS.database.service.ServiceUserAnalysisRight#getAllFromAnalysis(lu.itrust.business.TS.model.analysis.Analysis)
	 */
	@Override
	public List<UserAnalysisRight> getAllFromAnalysis(Analysis analysis) {
		return daoUserAnalysisRight.getAllFromAnalysis(analysis);
	}

	/**
	 * getAllFromUserLogin: <br>
	 * Description
	 * 
	 * @param login
	 * @return
	 * @
	 * 
	 * 	@see
	 *   lu.itrust.business.TS.database.service.ServiceUserAnalysisRight#getAllFromUserLogin(java.lang.String)
	 */
	@Override
	public List<UserAnalysisRight> getAllFromUser(String login) {
		return daoUserAnalysisRight.getAllFromUser(login);
	}

	/**
	 * getAllFromUser: <br>
	 * Description
	 * 
	 * @param user
	 * @return
	 * @
	 * 
	 * 	@see
	 *   lu.itrust.business.TS.database.service.ServiceUserAnalysisRight#getAllFromUser(lu.itrust.business.TS.usermanagement.User)
	 */
	@Override
	public List<UserAnalysisRight> getAllFromUser(User user) {
		return daoUserAnalysisRight.getAllFromUser(user);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param userAnalysisRight
	 * @
	 * 
	 * 	@see
	 *   lu.itrust.business.TS.database.service.ServiceUserAnalysisRight#save(lu.itrust.business.TS.model.analysis.rights.UserAnalysisRight)
	 */
	@Override
	public void save(UserAnalysisRight userAnalysisRight) {
		this.daoUserAnalysisRight.save(userAnalysisRight);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param userAnalysisRight
	 * @
	 * 
	 * 	@see
	 *   lu.itrust.business.TS.database.service.ServiceUserAnalysisRight#saveOrUpdate(lu.itrust.business.TS.model.analysis.rights.UserAnalysisRight)
	 */
	@Override
	public void saveOrUpdate(UserAnalysisRight userAnalysisRight) {
		this.daoUserAnalysisRight.saveOrUpdate(userAnalysisRight);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param userAnalysisRight
	 * @
	 * 
	 * 	@see
	 *   lu.itrust.business.TS.database.service.ServiceUserAnalysisRight#delete(lu.itrust.business.TS.model.analysis.rights.UserAnalysisRight)
	 */
	@Override
	public void delete(UserAnalysisRight userAnalysisRight) {
		this.daoUserAnalysisRight.delete(userAnalysisRight);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param id
	 * @
	 * 
	 * 	@see
	 *   lu.itrust.business.TS.database.service.ServiceUserAnalysisRight#delete(long)
	 */
	@Override
	public void delete(Integer id) {
		this.daoUserAnalysisRight.delete(get(id));
	}

	@Override
	public List<UserAnalysisRight> getAllFromIdenfierExceptAnalysisIdAndRightNotRead(String identifier, int AnalysisId) {
		return this.daoUserAnalysisRight.getAllFromIdenfierExceptAnalysisIdAndRightNotRead(identifier, AnalysisId);
	}

	@Override
	public boolean hasRightOrOwner(int idAnalysis, String username, AnalysisRight right) {
		return daoUserAnalysisRight.hasRightOrOwner(idAnalysis, username, right);
	}

	@Override
	public boolean isUserAuthorized(int idAnalysis, String username, List<AnalysisRight> rights) {
		return daoUserAnalysisRight.isUserAuthorized(idAnalysis, username, rights);
	}

	@Override
	public boolean hasDeletePermission(Integer idAnalysis, String username,Boolean isProfile) {
		return daoUserAnalysisRight.hasDeletePermission(idAnalysis,username, isProfile);
	}

	@Override
	public boolean hasManagementPermission(Integer idAnalysis, String username) {
		return daoUserAnalysisRight.hasManagementPermission(idAnalysis,username);
	}

}