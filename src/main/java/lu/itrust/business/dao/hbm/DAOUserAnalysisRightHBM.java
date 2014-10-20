package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.AnalysisRight;
import lu.itrust.business.TS.UserAnalysisRight;
import lu.itrust.business.TS.usermanagement.RoleType;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.dao.DAOUserAnalysisRight;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 * DAOUserAnalysisRightHBM.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.à.rl. :
 * @version
 * @since Jan 13, 2014
 */
@Repository
public class DAOUserAnalysisRightHBM extends DAOHibernate implements DAOUserAnalysisRight {

	/**
	 * Constructor
	 */
	public DAOUserAnalysisRightHBM() {
	}

	/**
	 * Constructor
	 * 
	 * @param session
	 */
	public DAOUserAnalysisRightHBM(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOUserAnalysisRight#get(long)
	 */
	@Override
	public UserAnalysisRight get(Integer id) throws Exception {
		return (UserAnalysisRight) getSession().get(DAOUserAnalysisRightHBM.class, id);
	}

	/**
	 * getUserAnalysisRight: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOUserAnalysisRight#getUserAnalysisRight(lu.itrust.business.TS.Analysis,
	 *      lu.itrust.business.TS.usermanagement.User)
	 */
	@Override
	public UserAnalysisRight getFromAnalysisAndUser(Analysis analysis, User user) throws Exception {
		return analysis.getRightsforUser(user);
	}

	/**
	 * isUserAuthorized: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOUserAnalysisRight#isUserAuthorized(java.lang.Integer,
	 *      java.lang.Integer, lu.itrust.business.TS.AnalysisRight)
	 */
	@Override
	public boolean isUserAuthorized(Integer analysisId, Integer userId, AnalysisRight right) throws Exception {
		String query = "Select userRight.right From Analysis analysis join analysis.userRights userRight where analysis.id = :idAnalysis and userRight.user.id = :idUser";
		AnalysisRight analysisRight = (AnalysisRight) getSession().createQuery(query).setParameter("idAnalysis", analysisId).setParameter("idUser", userId).uniqueResult();
		return analysisRight == null ? false : analysisRight.ordinal() <= right.ordinal();
	}

	/**
	 * isUserAuthorized: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOUserAnalysisRight#isUserAuthorized(java.lang.Integer,
	 *      java.lang.String, lu.itrust.business.TS.AnalysisRight)
	 */
	@Override
	public boolean isUserAuthorized(Integer idAnalysis, String username, AnalysisRight right) throws Exception {
		String query = "Select userRight.right From Analysis analysis join analysis.userRights userRight where analysis.id = :idAnalysis and userRight.user.login = :login";
		AnalysisRight analysisRight = (AnalysisRight) getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).setParameter("login", username).uniqueResult();
		User user = (User) getSession().createQuery("FROM User as user where user.login = :username").setParameter("username", username).uniqueResult();
		Boolean isProfile = (Boolean) getSession().createQuery("Select analysis.profile From Analysis as analysis where analysis.id = :id").setParameter("id", idAnalysis).uniqueResult();
		if (isProfile == null)
			isProfile = false;
		if (analysisRight == null && isProfile) {
			if (user.isAutorised(RoleType.ROLE_CONSULTANT))
				analysisRight = AnalysisRight.ALL;
			else
				analysisRight = AnalysisRight.READ;
		}
		return analysisRight == null ? false : analysisRight.ordinal() <= right.ordinal();
	}

	/**
	 * isUserAuthorized: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOUserAnalysisRight#isUserAuthorized(lu.itrust.business.TS.Analysis,
	 *      lu.itrust.business.TS.usermanagement.User, lu.itrust.business.TS.AnalysisRight)
	 */
	@Override
	public boolean isUserAuthorized(Analysis analysis, User user, AnalysisRight right) throws Exception {
		return analysis.isUserAuthorized(user, right);
	}

	/**
	 * getAnalysisRightOfUser: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOUserAnalysisRight#getAnalysisRightOfUser(lu.itrust.business.TS.Analysis,
	 *      lu.itrust.business.TS.usermanagement.User)
	 */
	@Override
	public AnalysisRight getAnalysisRightOfUser(Analysis analysis, User user) throws Exception {
		return analysis.getRightsforUser(user).getRight();
	}

	/**
	 * getAllFromAnalysisId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOUserAnalysisRight#getAllFromAnalysisId(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UserAnalysisRight> getAllFromAnalysis(Integer analysisid) throws Exception {
		return (List<UserAnalysisRight>) getSession().createQuery("select userRight From Analysis analysis join analyis.userRights userRight WHERE analysis.id = :analysis").setParameter("analysis",
				analysisid).list();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOUserAnalysisRight#getAllFromAnalysis(lu.itrust.business.TS.Analysis)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UserAnalysisRight> getAllFromAnalysis(Analysis analysis) throws Exception {
		return (List<UserAnalysisRight>) getSession().createQuery("select userRight From Analysis analysis join analyis.userRights userRight WHERE analysis = :analysis").setParameter("analysis",
				analysis).list();
	}

	/**
	 * getAllFromUserLogin: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOUserAnalysisRight#getAllFromUserLogin(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UserAnalysisRight> getAllFromUser(String login) throws Exception {
		return (List<UserAnalysisRight>) getSession().createQuery("select userRight From Analysis analysis join analyis.userRights userRight where userRight.user.login = :user").setParameter("user",
				login).list();
	}

	/**
	 * getAllFromUser: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOUserAnalysisRight#getAllFromUser(lu.itrust.business.TS.usermanagement.User)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UserAnalysisRight> getAllFromUser(User user) throws Exception {
		return (List<UserAnalysisRight>) getSession().createQuery("select userRight From Analysis analysis join analyis.userRights userRight WHERE userRight.user = :user").setParameter("user", user)
				.list();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOUserAnalysisRight#save(lu.itrust.business.TS.UserAnalysisRight)
	 */
	@Override
	public void save(UserAnalysisRight userAnalysisRight) throws Exception {
		getSession().save(userAnalysisRight);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOUserAnalysisRight#saveOrUpdate(lu.itrust.business.TS.UserAnalysisRight)
	 */
	@Override
	public void saveOrUpdate(UserAnalysisRight userAnalysisRight) throws Exception {
		getSession().saveOrUpdate(userAnalysisRight);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOUserAnalysisRight#delete(long)
	 */
	@Override
	public void delete(Integer id) throws Exception {
		getSession().delete(get(id));
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOUserAnalysisRight#delete(lu.itrust.business.TS.UserAnalysisRight)
	 */
	@Override
	public void delete(UserAnalysisRight userAnalysisRight) throws Exception {
		getSession().delete(userAnalysisRight);
	}
}