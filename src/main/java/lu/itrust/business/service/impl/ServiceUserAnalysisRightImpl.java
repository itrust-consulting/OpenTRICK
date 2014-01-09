package lu.itrust.business.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.UserAnalysisRight;
import lu.itrust.business.TS.usermanagment.User;
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
public class ServiceUserAnalysisRightImpl implements ServiceUserAnalysisRight {

	@Autowired
	private DAOUserAnalysisRight userAnalysisRight;
	
	public void setDAOUserAnalysisRight(DAOUserAnalysisRight daoUAR) {
		this.userAnalysisRight=daoUAR;
	}
	
	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceUserAnalysisRight#get(long)
	 */
	@Override
	public UserAnalysisRight get(long id) throws Exception {
		return userAnalysisRight.get(id);
	}

	/**
	 * getAllByUser: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceUserAnalysisRight#getAllByUser(java.lang.String)
	 */
	@Override
	public List<UserAnalysisRight> getAllByUser(String login) throws Exception {
		return userAnalysisRight.getAllByUser(login);
	}

	/**
	 * getAllByUser: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceUserAnalysisRight#getAllByUser(lu.itrust.business.TS.usermanagment.User)
	 */
	@Override
	public List<UserAnalysisRight> getAllByUser(User user) throws Exception {
		return userAnalysisRight.getAllByUser(user);
	}

	/**
	 * getAllByUniqueAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceUserAnalysisRight#getAllByUniqueAnalysis(lu.itrust.business.TS.Analysis)
	 */
	@Override
	public List<UserAnalysisRight> getAllByUniqueAnalysis(Analysis analysis) throws Exception {
		return userAnalysisRight.getAllByUniqueAnalysis(analysis);

	}

	/**
	 * getAllByAnalysisIdentifier: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceUserAnalysisRight#getAllByAnalysisIdentifier(java.lang.String)
	 */
	@Override
	public List<UserAnalysisRight> getAllByAnalysisIdentifier(String identifier) throws Exception {
		return userAnalysisRight.getAllByAnalysisIdentifier(identifier);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceUserAnalysisRight#save(lu.itrust.business.TS.UserAnalysisRight)
	 */
	@Override
	public void save(UserAnalysisRight userAnalysisRight) throws Exception {
		this.userAnalysisRight.save(userAnalysisRight);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceUserAnalysisRight#saveOrUpdate(lu.itrust.business.TS.UserAnalysisRight)
	 */
	@Override
	public void saveOrUpdate(UserAnalysisRight userAnalysisRight) throws Exception {
		this.userAnalysisRight.saveOrUpdate(userAnalysisRight);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceUserAnalysisRight#delete(lu.itrust.business.TS.UserAnalysisRight)
	 */
	@Override
	public void delete(UserAnalysisRight userAnalysisRight) throws Exception {
		this.userAnalysisRight.delete(userAnalysisRight);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.service.ServiceUserAnalysisRight#delete(long)
	 */
	@Override
	public void delete(long id) throws Exception {
		this.userAnalysisRight.delete(get(id));
	}
}
