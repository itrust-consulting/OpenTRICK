/**
 * 
 */
package lu.itrust.business.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.User;
import lu.itrust.business.dao.DAOUser;
import lu.itrust.business.service.ServiceUser;

/**
 * @author oensuifudine
 *
 */
@Transactional
@Service
public class ServiceUserImpl implements ServiceUser {

	@Autowired
	private DAOUser daoUser;
	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceUser#get(long)
	 */
	@Override
	public User get(long id) throws Exception {
		// TODO Auto-generated method stub
		return daoUser.get(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceUser#get(java.lang.String)
	 */
	@Override
	public User get(String login) throws Exception {
		// TODO Auto-generated method stub
		return daoUser.get(login);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceUser#get(java.lang.String, java.lang.String)
	 */
	@Override
	public User get(String login, String password) throws Exception {
		// TODO Auto-generated method stub
		return daoUser.get(login, password);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceUser#loadAll()
	 */
	@Override
	public List<User> loadAll() throws Exception {
		// TODO Auto-generated method stub
		return daoUser.loadAll();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceUser#loadByName(java.lang.String)
	 */
	@Override
	public List<User> loadByName(String name) throws Exception {
		// TODO Auto-generated method stub
		return daoUser.loadByName(name);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceUser#loadByCountry(java.lang.String)
	 */
	@Override
	public List<User> loadByCountry(String name) throws Exception {
		// TODO Auto-generated method stub
		return daoUser.loadByCountry(name);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceUser#save(lu.itrust.business.TS.User)
	 */
	@Transactional
	@Override
	public void save(User user) throws Exception {
		daoUser.save(user);

	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceUser#saveOrUpdate(lu.itrust.business.TS.User)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(User user) throws Exception {
		daoUser.saveOrUpdate(user);

	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceUser#delete(lu.itrust.business.TS.User)
	 */
	@Transactional
	@Override
	public void delete(User user) throws Exception {
		daoUser.delete(user);

	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceUser#delete(long)
	 */
	@Transactional
	@Override
	public void delete(long id) throws Exception {
		daoUser.delete(id);

	}

	public DAOUser getDaoUser() {
		return daoUser;
	}

	public void setDaoUser(DAOUser daoUser) {
		this.daoUser = daoUser;
	}

	@Override
	public boolean isEmpty() throws Exception {
		// TODO Auto-generated method stub
		return daoUser.isEmpty();
	}

}
