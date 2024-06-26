package lu.itrust.business.ts.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.ts.database.dao.DAOAnalysisStandard;
import lu.itrust.business.ts.database.service.ServiceAnalysisStandard;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.standard.AnalysisStandard;
import lu.itrust.business.ts.model.standard.Standard;
import lu.itrust.business.ts.model.standard.StandardType;

/**
 * ServiceAnalysisStandardImpl.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.a.rl.
 * @version
 * @since Jan 24, 2013
 */
@Transactional(readOnly = true)
@Service
public class ServiceAnalysisStandardImpl implements ServiceAnalysisStandard {

	@Autowired
	private DAOAnalysisStandard daoAnalysisStandard;

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceAnalysisStandard#get(int)
	 */
	
	@Override
	public AnalysisStandard get(Integer id)  {
		return daoAnalysisStandard.get(id);
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceAnalysisStandard#getAll()
	 */
	
	@Override
	public List<AnalysisStandard> getAll()  {
		return daoAnalysisStandard.getAll();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @param analysisID
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceAnalysisStandard#getAllFromAnalysis(java.lang.Integer)
	 */
	
	@Override
	public List<AnalysisStandard> getAllFromAnalysis(Integer analysisID)  {
		return this.daoAnalysisStandard.getAllFromAnalysis(analysisID);
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @param analysisID
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceAnalysisStandard#getAllFromAnalysis(java.lang.Integer)
	 */
	
	@Override
	public List<AnalysisStandard> getAllComputableFromAnalysis(Integer analysisID)  {
		return this.daoAnalysisStandard.getAllComputableFromAnalysis(analysisID);
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @param analysis
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceAnalysisStandard#getAllFromAnalysis(lu.itrust.business.ts.model.analysis.Analysis)
	 */
	
	@Override
	public List<AnalysisStandard> getAllFromAnalysis(Analysis analysis)  {
		return daoAnalysisStandard.getAllFromAnalysis(analysis);
	}

	/**
	 * getAllFromStandard: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.ts.database.service.ServiceAnalysisStandard#getAllFromStandard(lu.itrust.business.ts.model.standard.Standard)
	 */
	
	@Override
	public List<AnalysisStandard> getAllFromStandard(Standard standard)  {
		return daoAnalysisStandard.getAllFromStandard(standard);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param analysisStandard
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceAnalysisStandard#save(lu.itrust.business.ts.model.standard.AnalysisStandard)
	 */
	@Transactional
	@Override
	public void save(AnalysisStandard analysisStandard)  {
		daoAnalysisStandard.save(analysisStandard);

	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param analysisStandard
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceAnalysisStandard#saveOrUpdate(lu.itrust.business.ts.model.standard.AnalysisStandard)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(AnalysisStandard analysisStandard)  {
		daoAnalysisStandard.saveOrUpdate(analysisStandard);

	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param analysisStandard
	 * @
	 * 
	 * @see lu.itrust.business.ts.database.service.ServiceAnalysisStandard#delete(lu.itrust.business.ts.model.standard.AnalysisStandard)
	 */
	@Transactional
	@Override
	public void delete(AnalysisStandard analysisStandard)  {
		daoAnalysisStandard.delete(analysisStandard);

	}

	@Transactional
	@Override
	public void deleteAllFromAnalysis(Integer analysisId)  {
		daoAnalysisStandard.deleteAllFromAnalysis(analysisId);
	}

	@Override
	public AnalysisStandard getFromAnalysisIdAndStandardId(Integer analysisId, int standardId) {
		return daoAnalysisStandard.getFromAnalysisIdAndStandardId(analysisId,standardId );
	}

	@Override
	public boolean belongsToAnalysis(Integer idAnalysis, int id) {
		return daoAnalysisStandard.belongsToAnalysis(idAnalysis, id);
	}

	@Override
	public Standard getStandardById(int idAnalysisStandard) {
		return daoAnalysisStandard.getStandardById(idAnalysisStandard);
	}

	@Override
	public String getStandardNameById(int idAnalysisStandard) {
		return daoAnalysisStandard.getStandardNameById(idAnalysisStandard);
	}

	@Override
	public AnalysisStandard getFromAnalysisIdAndStandardName(Integer idAnalysis, String name) {
		return daoAnalysisStandard.getFromAnalysisIdAndStandardName(idAnalysis,name);
	}

	@Override
	public Boolean hasStandard(Integer idAnalysis, String standard) {
		return daoAnalysisStandard.hasStandard(idAnalysis,standard);
	}

	@Override
	public List<AnalysisStandard> findBySOAEnabledAndAnalysisId(boolean state, Integer idAnalysis) {
		return daoAnalysisStandard.findBySOAEnabledAndAnalysisId(state,idAnalysis);
	}

	@Override
	public List<AnalysisStandard> findByAndAnalysisIdAndTypeIn(Integer analysisId, Class<?>... classes) {
		return daoAnalysisStandard.findByAndAnalysisIdAndTypeIn(analysisId, classes);
	}

	@Override
	public AnalysisStandard findOne(int id, int analysisId) {
		return daoAnalysisStandard.findOne(id, analysisId);
	}

	@Override
	public List<AnalysisStandard> findByComputableAndAnalysisIdAndTypeIn(boolean computable, Integer idAnalysis, Class<?>... classes) {
		return daoAnalysisStandard.findByComputableAndAnalysisIdAndTypeIn(computable,idAnalysis,classes);
	}

	@Override
	public List<Standard> findStandardByComputableAndAnalysisIdAndTypeIn(boolean computable, Integer idAnalysis, Class<?>... classes) {
		return  daoAnalysisStandard.findStandardByComputableAndAnalysisIdAndTypeIn(computable,idAnalysis,classes);
	}

	@Override
	public List<Standard> findStandardByAnalysisIdAndTypeIn(Integer idAnalysis, Class<?>... classes) {
		return daoAnalysisStandard.findStandardByAnalysisIdAndTypeIn(idAnalysis,classes);
	}

	@Override
	public List<String> findByAnalysisAndNameLikeAndTypeAndCustom(Integer idAnalysis, String name, StandardType type,
			boolean custom) {
		return daoAnalysisStandard.findByAnalysisAndNameLikeAndTypeAndCustom(idAnalysis,name,type, custom);
	}
}