/**
 * 
 */
package lu.itrust.business.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.Language;
import lu.itrust.business.dao.DAOLanguage;
import lu.itrust.business.service.ServiceLanguage;

/**
 * @author oensuifudine
 *
 */
@Service
public class ServiceLanguageImpl implements ServiceLanguage {

	@Autowired
	private DAOLanguage daoLanguage;
	
	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceLanguage#get(int)
	 */
	@Override
	public Language get(int id) throws Exception {
		// TODO Auto-generated method stub
		return daoLanguage.get(id);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceLanguage#loadFromAlpha3(java.lang.String)
	 */
	@Override
	public Language loadFromAlpha3(String alpha3) throws Exception {
		// TODO Auto-generated method stub
		return daoLanguage.loadFromAlpha3(alpha3);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceLanguage#loadFromName(java.lang.String)
	 */
	@Override
	public Language loadFromName(String name) throws Exception {
		// TODO Auto-generated method stub
		return daoLanguage.loadFromName(name);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceLanguage#loadFromAlternativeName(java.lang.String)
	 */
	@Override
	public Language loadFromAlternativeName(String alternativeName)
			throws Exception {
		// TODO Auto-generated method stub
		return daoLanguage.loadFromAlternativeName(alternativeName);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceLanguage#loadAll()
	 */
	@Override
	public List<Language> loadAll() throws Exception {
		// TODO Auto-generated method stub
		return daoLanguage.loadAll();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceLanguage#save(lu.itrust.business.TS.Language)
	 */
	@Transactional
	@Override
	public void save(Language language) throws Exception {
		daoLanguage.save(language);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceLanguage#saveOrUpdate(lu.itrust.business.TS.Language)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(Language language) throws Exception {
		daoLanguage.saveOrUpdate(language);

	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceLanguage#remove(lu.itrust.business.TS.Language)
	 */
	@Transactional
	@Override
	public void remove(Language language) throws Exception {
		daoLanguage.remove(language);

	}

	/**
	 * @return the daoLanguage
	 */
	public DAOLanguage getDaoLanguage() {
		return daoLanguage;
	}

	/**
	 * @param daoLanguage the daoLanguage to set
	 */
	public void setDaoLanguage(DAOLanguage daoLanguage) {
		this.daoLanguage = daoLanguage;
	}

}
