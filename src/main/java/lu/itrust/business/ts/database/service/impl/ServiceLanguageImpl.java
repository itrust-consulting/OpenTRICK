package lu.itrust.business.ts.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.ts.database.dao.DAOLanguage;
import lu.itrust.business.ts.database.service.ServiceLanguage;
import lu.itrust.business.ts.model.general.Language;

/**
 * ServiceLanguageImpl.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
@Transactional(readOnly = true)
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
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceLanguage#get(int)
	 */
	@Override
	public Language get(Integer id)  {
		return daoLanguage.get(id);
	}

	/**
	 * languageExistsByAlpha3: <br>
	 * Description
	 * 
	 * @param alpha3
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceLanguage#languageExistsByAlpha3(java.lang.String)
	 */
	@Override
	public boolean existsByAlpha3(String alpha3)  {
		return daoLanguage.existsByAlpha3(alpha3);
	}

	/**
	 * languageExistsByName: <br>
	 * Description
	 * 
	 * @param name
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceLanguage#languageExistsByName(java.lang.String)
	 */
	@Override
	public boolean existsByName(String name)  {
		return daoLanguage.existsByName(name);
	}

	/**
	 * languageExistsByAltName: <br>
	 * Description
	 * 
	 * @param altName
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceLanguage#languageExistsByAltName(java.lang.String)
	 */
	@Override
	public boolean existsByAltName(String altName)  {
		return daoLanguage.existsByAltName(altName);
	}

	/**
	 * getLanguageOfAnalysis: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceLanguage#getLanguageOfAnalysis(java.lang.Integer)
	 */
	@Override
	public Language getFromAnalysis(Integer idAnalysis)  {
		return this.daoLanguage.getFromAnalysis(idAnalysis);
	}

	/**
	 * getLanguageByAlpha3: <br>
	 * Description
	 * 
	 * @param alpha3
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceLanguage#getLanguageByAlpha3(java.lang.String)
	 */
	@Override
	public Language getByAlpha3(String alpha3)  {
		return daoLanguage.getByAlpha3(alpha3);
	}

	/**
	 * getLanguageByName: <br>
	 * Description
	 * 
	 * @param name
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceLanguage#getLanguageByName(java.lang.String)
	 */
	@Override
	public Language getByName(String name)  {
		return daoLanguage.getByName(name);
	}

	/**
	 * getLanguageByAltName: <br>
	 * Description
	 * 
	 * @param alternativeName
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceLanguage#getLanguageByAltName(java.lang.String)
	 */
	@Override
	public Language getByAltName(String alternativeName)  {
		return daoLanguage.getByAltName(alternativeName);
	}

	/**
	 * getAllLanguages: <br>
	 * Description
	 * 
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceLanguage#getAllLanguages()
	 */
	@Override
	public List<Language> getAll()  {
		return daoLanguage.getAll();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param language
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceLanguage#save(lu.itrust.business.ts.model.general.Language)
	 */
	@Transactional
	@Override
	public void save(Language language)  {
		daoLanguage.save(language);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param language
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceLanguage#saveOrUpdate(lu.itrust.business.ts.model.general.Language)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(Language language)  {
		daoLanguage.saveOrUpdate(language);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param languageId
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceLanguage#delete(java.lang.Integer)
	 */
	@Transactional
	@Override
	public void delete(Integer languageId)  {
		daoLanguage.delete(languageId);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param language
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceLanguage#delete(lu.itrust.business.ts.model.general.Language)
	 */
	@Transactional
	@Override
	public void delete(Language language)  {
		daoLanguage.delete(language);
	}

	@Override
	public boolean isUsed(Language language) {
		return daoLanguage.isInUse(language);
	}

	@Override
	public boolean existsByIdAndAlpha3(int id, String alpha3) {
		return daoLanguage.existsByIdAndAlpha3(id, alpha3);
	}

	@Override
	public boolean existsByIdAndName(int id, String name) {
		return daoLanguage.existsByIdAndName(id, name);
	}

	@Override
	public boolean existsByIdAndAltName(int id, String altName) {
		return daoLanguage.existsByIdAndAltName(id, altName);
	}

	@Override
	public List<Language> getByAlpha3(String... alpha3s) {
		return daoLanguage.getByAlpha3(alpha3s);
	}

	@Override
	public boolean existsByAlpha3(String... alpha3s) {
		return daoLanguage.existsByAlpha3(alpha3s);
	}
}