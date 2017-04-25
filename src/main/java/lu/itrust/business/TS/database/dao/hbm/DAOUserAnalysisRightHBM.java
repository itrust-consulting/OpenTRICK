package lu.itrust.business.TS.database.dao.hbm;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.database.dao.DAOUserAnalysisRight;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.analysis.rights.AnalysisRight;
import lu.itrust.business.TS.model.analysis.rights.UserAnalysisRight;
import lu.itrust.business.TS.usermanagement.RoleType;
import lu.itrust.business.TS.usermanagement.User;

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
	 * @see lu.itrust.business.TS.database.dao.DAOUserAnalysisRight#get(long)
	 */
	@Override
	public UserAnalysisRight get(Integer id) {
		return (UserAnalysisRight) getSession().get(UserAnalysisRight.class, id);
	}

	/**
	 * getUserAnalysisRight: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUserAnalysisRight#getUserAnalysisRight(lu.itrust.business.TS.model.analysis.Analysis,
	 *      lu.itrust.business.TS.usermanagement.User)
	 */
	@Override
	public UserAnalysisRight getFromAnalysisAndUser(Analysis analysis, User user) {
		return analysis.getRightsforUser(user);
	}

	/**
	 * isUserAuthorized: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUserAnalysisRight#isUserAuthorized(java.lang.Integer,
	 *      java.lang.Integer,
	 *      lu.itrust.business.TS.model.analysis.rights.AnalysisRight)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean isUserAuthorized(Integer analysisId, Integer userId, AnalysisRight right) {
		String query = "Select userRight.right From Analysis analysis inner join analysis.userRights userRight where analysis.id = :idAnalysis and userRight.user.id = :idUser";
		AnalysisRight analysisRight = (AnalysisRight) getSession().createQuery(query).setParameter("idAnalysis", analysisId).setParameter("idUser", userId).uniqueResultOptional()
				.orElse(null);
		return analysisRight == null ? false : analysisRight.ordinal() <= right.ordinal();
	}

	/**
	 * isUserAuthorized: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUserAnalysisRight#isUserAuthorized(java.lang.Integer,
	 *      java.lang.String,
	 *      lu.itrust.business.TS.model.analysis.rights.AnalysisRight)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean isUserAuthorized(Integer idAnalysis, String username, AnalysisRight right) {
		String query = "Select userRight.right, analysis.archived From Analysis analysis inner join analysis.userRights userRight where analysis.id = :idAnalysis and userRight.user.login = :login";
		Object[] data = (Object[]) getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).setParameter("login", username).uniqueResultOptional().orElse(null);
		User user = getSession().createQuery("FROM User as user where user.login = :username", User.class).setParameter("username", username).uniqueResultOptional().orElse(null);
		Boolean isProfile = getSession().createQuery("Select analysis.profile From Analysis as analysis where analysis.id = :id", Boolean.class).setParameter("id", idAnalysis)
				.uniqueResultOptional().orElse(false);
		if (data == null && isProfile) {
			if (user.isAutorised(RoleType.ROLE_CONSULTANT))
				data = new Object[] { AnalysisRight.ALL, false };
			else
				data = new Object[] { AnalysisRight.READ, false };
		}
		return isAuthorised((AnalysisRight) data[0], right, (Boolean) data[1]);
	}

	/**
	 * isUserAuthorized: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUserAnalysisRight#isUserAuthorized(lu.itrust.business.TS.model.analysis.Analysis,
	 *      lu.itrust.business.TS.usermanagement.User,
	 *      lu.itrust.business.TS.model.analysis.rights.AnalysisRight)
	 */
	@Override
	public boolean isUserAuthorized(Analysis analysis, User user, AnalysisRight right) {
		return analysis.isUserAuthorized(user, right);
	}

	/**
	 * getAnalysisRightOfUser: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUserAnalysisRight#getAnalysisRightOfUser(lu.itrust.business.TS.model.analysis.Analysis,
	 *      lu.itrust.business.TS.usermanagement.User)
	 */
	@Override
	public AnalysisRight getAnalysisRightOfUser(Analysis analysis, User user) {
		return analysis.getRightsforUser(user).getRight();
	}

	/**
	 * getAllFromAnalysisId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUserAnalysisRight#getAllFromAnalysisId(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UserAnalysisRight> getAllFromAnalysis(Integer analysisid) {
		return (List<UserAnalysisRight>) getSession().createQuery("select userRight From Analysis analysis inner join analysis.userRights userRight WHERE analysis.id = :analysis")
				.setParameter("analysis", analysisid).getResultList();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUserAnalysisRight#getAllFromAnalysis(lu.itrust.business.TS.model.analysis.Analysis)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UserAnalysisRight> getAllFromAnalysis(Analysis analysis) {
		return (List<UserAnalysisRight>) getSession().createQuery("select userRight From Analysis analysis inner join analysis.userRights userRight WHERE analysis = :analysis")
				.setParameter("analysis", analysis).getResultList();
	}

	/**
	 * getAllFromUserLogin: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUserAnalysisRight#getAllFromUserLogin(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UserAnalysisRight> getAllFromUser(String login) {
		return (List<UserAnalysisRight>) getSession()
				.createQuery("select userRight From Analysis analysis inner join analysis.userRights userRight where userRight.user.login = :user").setParameter("user", login)
				.getResultList();
	}

	/**
	 * getAllFromUser: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUserAnalysisRight#getAllFromUser(lu.itrust.business.TS.usermanagement.User)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UserAnalysisRight> getAllFromUser(User user) {
		return (List<UserAnalysisRight>) getSession().createQuery("select userRight From Analysis analysis inner join analysis.userRights userRight WHERE userRight.user = :user")
				.setParameter("user", user).getResultList();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUserAnalysisRight#save(lu.itrust.business.TS.model.analysis.rights.UserAnalysisRight)
	 */
	@Override
	public void save(UserAnalysisRight userAnalysisRight) {
		getSession().save(userAnalysisRight);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUserAnalysisRight#saveOrUpdate(lu.itrust.business.TS.model.analysis.rights.UserAnalysisRight)
	 */
	@Override
	public void saveOrUpdate(UserAnalysisRight userAnalysisRight) {
		getSession().saveOrUpdate(userAnalysisRight);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUserAnalysisRight#delete(long)
	 */
	@Override
	public void delete(Integer id) {
		getSession().delete(get(id));
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOUserAnalysisRight#delete(lu.itrust.business.TS.model.analysis.rights.UserAnalysisRight)
	 */
	@Override
	public void delete(UserAnalysisRight userAnalysisRight) {
		getSession().delete(userAnalysisRight);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserAnalysisRight> getAllFromIdenfierExceptAnalysisIdAndRightNotRead(String identifier, int analysisId) {
		return getSession()
				.createQuery(
						"select userRight From Analysis as analysis inner join analysis.userRights as userRight where analysis.identifier = :identifier and analysis.archived = :archived and analysis.id <> :idAnalysis and userRight.right <>'READ'")
				.setParameter("identifier", identifier).setParameter("idAnalysis", analysisId).getResultList();
	}

	@Override
	public boolean isUserAuthorizedOrOwner(String identifier, String version, User owner, AnalysisRight right) {
		AnalysisRight analysisRight = getSession()
				.createQuery(
						"Select userRight.right From Analysis analysis inner join analysis.userRights userRight where analysis.identifier = :identifier and analysis.version = :version and userRight.user = :owner",
						AnalysisRight.class)
				.setParameter("identifier", identifier).setParameter("version", version).setParameter("owner", owner).uniqueResultOptional().orElse(null);
		if (!(analysisRight == null || right == null) && analysisRight.ordinal() <= right.ordinal())
			return true;
		else
			return (boolean) getSession()
					.createQuery("Select count(*)>0 From Analysis analysis where analysis.identifier = :identifier and  analysis.version = :version and analysis.owner = :owner")
					.setParameter("identifier", identifier).setParameter("version", version).setParameter("owner", owner).getSingleResult();
	}

	@Override
	public boolean hasRightOrOwner(int idAnalysis, String username, AnalysisRight right) {
		return (boolean) getSession()
				.createQuery(
						"select count(analysis) > 0 From Analysis analysis inner join analysis.userRights userRight WHERE analysis.id = :idAnalysis and (analysis.owner.login = :username or userRight.user.login = :username and userRight.right in (:rights))")
				.setParameter("idAnalysis", idAnalysis).setParameter("username", username).setParameterList("rights", AnalysisRight.highRightFrom(right)).getSingleResult();
	}

	@Override
	public void deleteByUser(User user) {
		getSession().createQuery("Delete from UserAnalysisRight where user = :user").setParameter("user", user).executeUpdate();
	}

	@Override
	public boolean isUserAuthorized(int idAnalysis, String username, List<AnalysisRight> rights) {
		return (boolean) getSession()
				.createQuery(
						"select count(analysis) > 0 From Analysis analysis inner join analysis.userRights userRight WHERE analysis.id = :idAnalysis and userRight.user.login = :username and userRight.right in (:rights)")
				.setParameter("idAnalysis", idAnalysis).setParameter("username", username).setParameterList("rights", rights).getSingleResult();
	}

	private boolean isAuthorised(AnalysisRight accessRight, AnalysisRight current, boolean archived) {
		return archived ? current == AnalysisRight.READ && accessRight.ordinal() <= AnalysisRight.READ.ordinal() : accessRight.ordinal() <= current.ordinal();
	}

	@Override
	public boolean hasDeletePermission(Integer idAnalysis, String username, Boolean isProfile) {
		return isProfile ? canDeleteProfile(idAnalysis, username) : canDeleteAnalysis(idAnalysis, username);
	}

	private Boolean canDeleteAnalysis(Integer idAnalysis, String username) {
		return getSession()
				.createQuery(
						"select count(analysis) > 0 From Analysis analysis inner join analysis.userRights userRight WHERE analysis.id = :idAnalysis and analysis.profile = false and (analysis.owner.login = :username or analysis.archived = false and userRight.user.login = :username and userRight.right in (:rights))",
						Boolean.class)
				.setParameter("idAnalysis", idAnalysis).setParameter("username", username).setParameterList("rights", AnalysisRight.highRightFrom(AnalysisRight.ALL))
				.getSingleResult();
	}

	private Boolean canDeleteProfile(Integer idAnalysis, String username) {
		return getSession().createQuery("select count(*) > 0 From User user inner join user.roles role where user.login = :username and role.type in (:roles)", Boolean.class)
				.setParameter("username", username).setParameterList("roles", RoleType.InheritedRoles(RoleType.ROLE_CONSULTANT)).getSingleResult()
				&& getSession().createQuery(
						"select count(analysis) > 0 From Analysis analysis where analysis.id = :idAnalysis and analysis.profile = true and  analysis.defaultProfile = false",
						Boolean.class).setParameter("idAnalysis", idAnalysis).getSingleResult();
	}

	@Override
	public boolean hasManagementPermission(Integer idAnalysis, String username) {
		return getSession()
				.createQuery(
						"select count(analysis) > 0 From Analysis analysis inner join analysis.userRights userRight WHERE analysis.id = :idAnalysis and analysis.archived = false and (analysis.owner.login = :username or userRight.user.login = :username and userRight.right in (:rights))",
						Boolean.class)
				.setParameter("idAnalysis", idAnalysis).setParameter("username", username).setParameterList("rights", AnalysisRight.highRightFrom(AnalysisRight.ALL))
				.getSingleResult();
	}
}