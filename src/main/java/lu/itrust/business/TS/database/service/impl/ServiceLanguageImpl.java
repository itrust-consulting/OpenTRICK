package lu.itrust.business.TS.database.service.impl;

import java.util.List;

import lu.itrust.business.TS.data.basic.Language;
import lu.itrust.business.TS.database.dao.DAOLanguage;
import lu.itrust.business.TS.database.service.ServiceLanguage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ServiceLanguageImpl.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
@Service
public class ServiceLanguageImpl implements ServiceLanguage {

	@Autowired
	private DAOLanguage daoLanguage;

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceLanguage#get(int)
	 */
	@Override
	public Language get(Integer id) throws Exception {
		return daoLanguage.get(id);
	}

	/**
	 * languageExistsByAlpha3: <br>
	 * Description
	 * 
	 * @param alpha3
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceLanguage#languageExistsByAlpha3(java.lang.String)
	 */
	@Override
	public boolean existsByAlpha3(String alpha3) throws Exception {
		return daoLanguage.existsByAlpha3(alpha3);
	}

	/**
	 * languageExistsByName: <br>
	 * Description
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceLanguage#languageExistsByName(java.lang.String)
	 */
	@Override
	public boolean existsByName(String name) throws Exception {
		return daoLanguage.existsByName(name);
	}

	/**
	 * languageExistsByAltName: <br>
	 * Description
	 * 
	 * @param altName
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceLanguage#languageExistsByAltName(java.lang.String)
	 */
	@Override
	public boolean existsByAltName(String altName) throws Exception {
		return daoLanguage.existsByAltName(altName);
	}

	/**
	 * getLanguageOfAnalysis: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceLanguage#getLanguageOfAnalysis(java.lang.Integer)
	 */
	@Override
	public Language getFromAnalysis(Integer idAnalysis) throws Exception {
		return this.daoLanguage.getFromAnalysis(idAnalysis);
	}

	/**
	 * getLanguageByAlpha3: <br>
	 * Description
	 * 
	 * @param alpha3
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceLanguage#getLanguageByAlpha3(java.lang.String)
	 */
	@Override
	public Language getByAlpha3(String alpha3) throws Exception {
		return daoLanguage.getByAlpha3(alpha3);
	}

	/**
	 * getLanguageByName: <br>
	 * Description
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceLanguage#getLanguageByName(java.lang.String)
	 */
	@Override
	public Language getByName(String name) throws Exception {
		return daoLanguage.getByName(name);
	}

	/**
	 * getLanguageByAltName: <br>
	 * Description
	 * 
	 * @param alternativeName
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceLanguage#getLanguageByAltName(java.lang.String)
	 */
	@Override
	public Language getByAltName(String alternativeName) throws Exception {
		return daoLanguage.getByAltName(alternativeName);
	}

	/**
	 * getAllLanguages: <br>
	 * Description
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceLanguage#getAllLanguages()
	 */
	@Override
	public List<Language> getAll() throws Exception {
		return daoLanguage.getAll();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param language
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceLanguage#save(lu.itrust.business.TS.data.basic.Language)
	 */
	@Transactional
	@Override
	public void save(Language language) throws Exception {
		daoLanguage.save(language);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param language
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceLanguage#saveOrUpdate(lu.itrust.business.TS.data.basic.Language)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(Language language) throws Exception {
		daoLanguage.saveOrUpdate(language);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param languageId
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceLanguage#delete(java.lang.Integer)
	 */
	@Transactional
	@Override
	public void delete(Integer languageId) throws Exception {
		daoLanguage.delete(languageId);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param language
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceLanguage#delete(lu.itrust.business.TS.data.basic.Language)
	 */
	@Transactional
	@Override
	public void delete(Language language) throws Exception {
		daoLanguage.delete(language);
	}
}