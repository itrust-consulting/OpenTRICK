package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.AnalysisRight;
import lu.itrust.business.TS.UserAnalysisRight;
import lu.itrust.business.TS.usermanagement.RoleType;
import lu.itrust.business.TS.usermanagement.User;
import lu.itrust.business.dao.DAOUserAnalysisRight;

import org.hibernate.Query;
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
	public UserAnalysisRight get(long id) throws Exception {
		return (UserAnalysisRight) getSession().get(DAOUserAnalysisRightHBM.class, id);
	}

	/**
	 * getAllByUser: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOUserAnalysisRight#getAllByUser(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UserAnalysisRight> getAllByUser(String login) throws Exception {
		Query query = getSession().createQuery("From UserAnalysisRight WHERE user.login = :user").setParameter("user", login);
		return (List<UserAnalysisRight>) query.list();
	}

	/**
	 * getAllByUser: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOUserAnalysisRight#getAllByUser(lu.itrust.business.TS.usermanagement.User)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UserAnalysisRight> getAllByUser(User user) throws Exception {
		Query query = getSession().createQuery("From UserAnalysisRight WHERE user = :user").setParameter("user", user);
		return (List<UserAnalysisRight>) query.list();
	}

	/**
	 * getAllByUniqueAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOUserAnalysisRight#getAllByUniqueAnalysis(lu.itrust.business.TS.Analysis)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UserAnalysisRight> getAllByUniqueAnalysis(int analysisid) throws Exception {
		Query query = getSession().createQuery("From UserAnalysisRight WHERE analysis.id = :analysis").setParameter("analysis", analysisid);
		return (List<UserAnalysisRight>) query.list();
	}

	/**
	 * getAllByUniqueAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOUserAnalysisRight#getAllByUniqueAnalysis(lu.itrust.business.TS.Analysis)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UserAnalysisRight> getAllByUniqueAnalysis(Analysis analysis) throws Exception {
		Query query = getSession().createQuery("From UserAnalysisRight WHERE analysis = :analysis").setParameter("analysis", analysis);
		return (List<UserAnalysisRight>) query.list();
	}

	/**
	 * getAllByAnalysisIdentifier: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOUserAnalysisRight#getAllByAnalysisIdentifier(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UserAnalysisRight> getAllByAnalysisIdentifier(String identifier) throws Exception {
		Query query = getSession().createQuery("From UserAnalysisRight WHERE analysis.identifier = :identifier").setParameter("identifier", identifier);
		return (List<UserAnalysisRight>) query.list();
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
	 * @see lu.itrust.business.dao.DAOUserAnalysisRight#delete(lu.itrust.business.TS.UserAnalysisRight)
	 */
	@Override
	public void delete(UserAnalysisRight userAnalysisRight) throws Exception {
		getSession().delete(userAnalysisRight);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOUserAnalysisRight#delete(long)
	 */
	@Override
	public void delete(long id) throws Exception {
		getSession().delete(get(id));
	}

	@Override
	public boolean isUserAuthorized(Analysis analysis, User user, AnalysisRight right) throws Exception {
		return analysis.isUserAuthorized(user, right);
	}

	@Override
	public UserAnalysisRight getUserAnalysisRight(Analysis analysis, User user) throws Exception {
		return analysis.getRightsforUser(user);
	}

	@Override
	public AnalysisRight getAnalysisRightOfUser(Analysis analysis, User user) throws Exception {
		return analysis.getRightsforUser(user).getRight();
	}

	@Override
	public boolean isUserAuthorized(Integer idAnalysis, String username, AnalysisRight right) {
		AnalysisRight analysisRight =
			(AnalysisRight) getSession().createQuery(
					"Select userAnalysisRight.right From UserAnalysisRight as userAnalysisRight where userAnalysisRight.analysis.id = :idAnalysis and userAnalysisRight.user.login = :login")
					.setParameter("idAnalysis", idAnalysis).setParameter("login", username).uniqueResult();
		
		User user = (User) getSession().createQuery("FROM User as user where user.login = :username").setParameter("username", username).uniqueResult();
		
		Boolean isProfile = (Boolean) getSession().createQuery("Select analysis.profile From Analysis as analysis where analysis.id = :id").setParameter("id", idAnalysis).uniqueResult();
		
		if (isProfile == null)
			isProfile = false;
		
		if(user.hasRole(RoleType.ROLE_CONSULTANT) && isProfile)
			analysisRight = AnalysisRight.ALL;
		
		return analysisRight == null ? false : analysisRight.ordinal() <= right.ordinal();
	}

	@Override
	public boolean isUserAuthorized(Integer analysisId, Integer userId, AnalysisRight right) {
		AnalysisRight analysisRight =
			(AnalysisRight) getSession().createQuery(
					"Select userAnalysisRight.right From UserAnalysisRight as userAnalysisRight where userAnalysisRight.analysis.id = :idAnalysis and userAnalysisRight.user.id = :idUser")
					.setParameter("idAnalysis", analysisId).setParameter("idUser", userId).uniqueResult();
		return analysisRight == null ? false : analysisRight.ordinal() <= right.ordinal();
	}

}