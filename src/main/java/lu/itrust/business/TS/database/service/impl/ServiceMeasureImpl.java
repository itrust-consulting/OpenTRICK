package lu.itrust.business.TS.database.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.database.dao.DAOMeasure;
import lu.itrust.business.TS.database.service.ServiceMeasure;
import lu.itrust.business.TS.model.standard.Standard;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measure.NormalMeasure;

/**
 * ServiceMeasureImpl.java: <br>
 * Detailed description...
 * 
 * @author eomar, itrust consulting s.a.rl.
 * @version
 * @since Jan 16, 2013
 */
@Service
public class ServiceMeasureImpl implements ServiceMeasure {

	@Autowired
	private DAOMeasure daoMeasure;

	/**
	 * get: <br>
	 * Description
	 * 
	 * @param id
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceMeasure#get(int)
	 */

	@Override
	public Measure get(Integer id)  {
		return daoMeasure.get(id);
	}

	/**
	 * getMeasureFromAnalysisIdById: <br>
	 * Description
	 * 
	 * @param id
	 * @param idAnalysis
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceMeasure#getMeasureFromAnalysisIdById(java.lang.Integer,
	 *      java.lang.Integer)
	 */

	@Override
	public Measure getFromAnalysisById(Integer idAnalysis, Integer id)  {
		return daoMeasure.getFromAnalysisById(idAnalysis, id);
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @param assetId
	 * @param analysisId
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceMeasure#belongsToAnalysis(int, int)
	 */

	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer assetId)  {
		return daoMeasure.belongsToAnalysis(analysisId, assetId);
	}

	/**
	 * getAllMeasures: <br>
	 * Description
	 * 
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceMeasure#getAllMeasures()
	 */

	@Override
	public List<Measure> getAll()  {
		return daoMeasure.getAll();
	}

	/**
	 * getAllFromAnalysisId: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceMeasure#getAllFromAnalysisId(int)
	 */

	@Override
	public List<Measure> getAllFromAnalysis(Integer idAnalysis)  {
		return daoMeasure.getAllFromAnalysis(idAnalysis);
	}

	/**
	 * getSOAMeasuresFromAnalysis: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceMeasure#getSOAMeasuresFromAnalysis(int)
	 */

	@Override
	public List<Measure> getSOAMeasuresFromAnalysis(Integer idAnalysis)  {
		return daoMeasure.getSOAMeasuresFromAnalysis(idAnalysis);
	}

	/**
	 * getAllMeasuresFromAnalysisIdAndComputable: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceMeasure#getAllMeasuresFromAnalysisIdAndComputable(int)
	 */

	@Override
	public List<Measure> getAllComputableFromAnalysis(Integer idAnalysis)  {
		return daoMeasure.getAllComputableFromAnalysis(idAnalysis);
	}

	/**
	 * getAllFromAnalysisAndStandard: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.service.ServiceMeasure#getAllFromAnalysisAndStandard(java.lang.Integer,
	 *      java.lang.Integer)
	 */

	@Override
	public List<Measure> getAllFromAnalysisAndStandard(Integer idAnalysis, Integer idStandard)  {
		return daoMeasure.getAllFromAnalysisAndStandard(idAnalysis, idStandard);
	}

	/**
	 * getAllFromAnalysisAndStandard: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.service.ServiceMeasure#getAllFromAnalysisAndStandard(java.lang.Integer,
	 *      java.lang.String)
	 */

	@Override
	public List<Measure> getAllFromAnalysisAndStandard(Integer idAnalysis, String standard)  {
		return daoMeasure.getAllFromAnalysisAndStandard(idAnalysis, standard);
	}

	/**
	 * getAllFromAnalysisAndStandard: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.service.ServiceMeasure#getAllFromAnalysisAndStandard(java.lang.Integer,
	 *      lu.itrust.business.TS.model.standard.Standard)
	 */

	@Override
	public List<Measure> getAllFromAnalysisAndStandard(Integer idAnalysis, Standard standard)  {
		return daoMeasure.getAllFromAnalysisAndStandard(idAnalysis, standard);
	}

	/**
	 * getAllNormalMeasuresFromAnalysis: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.service.ServiceMeasure#getAllNormalMeasuresFromAnalysis(java.lang.Integer)
	 */

	@Override
	public List<NormalMeasure> getAllNormalMeasuresFromAnalysis(Integer idAnalysis)  {
		return daoMeasure.getAllNormalMeasuresFromAnalysis(idAnalysis);
	}

	/**
	 * getAllNormalMeasuresFromAnalysisAndComputable: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.service.ServiceMeasure#getAllNormalMeasuresFromAnalysisAndComputable(java.lang.Integer)
	 */

	@Override
	public List<NormalMeasure> getAllNormalMeasuresFromAnalysisAndComputable(Integer idAnalysis)  {
		return daoMeasure.getAllNormalMeasuresFromAnalysisAndComputable(idAnalysis);
	}

	/**
	 * getAllNotMaturityMeasuresFromAnalysisAndComputable: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.service.ServiceMeasure#getAllNotMaturityMeasuresFromAnalysisAndComputable(java.lang.Integer)
	 */
	@Override
	public List<Measure> getAllNotMaturityMeasuresFromAnalysisAndComputable(Integer idAnalysis)  {
		return daoMeasure.getAllNotMaturityMeasuresFromAnalysisAndComputable(idAnalysis);
	}

	/**
	 * getAllNormalMeasuresFromAnalysisByMeasureIdList: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.service.ServiceMeasure#getAllNormalMeasuresFromAnalysisByMeasureIdList(java.lang.Integer,
	 *      java.util.List)
	 */

	@Override
	public List<NormalMeasure> getAllNormalMeasuresFromAnalysisByMeasureIdList(Integer idAnalysis, List<Integer> measures)  {
		return daoMeasure.getAllNormalMeasuresFromAnalysisByMeasureIdList(idAnalysis, measures);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param measure
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceMeasure#save(lu.itrust.business.TS.model.standard.measure.Measure)
	 */
	@Transactional
	@Override
	public Measure save(Measure measure)  {
		return daoMeasure.save(measure);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param measure
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceMeasure#saveOrUpdate(lu.itrust.business.TS.model.standard.measure.Measure)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(Measure measure)  {
		daoMeasure.saveOrUpdate(measure);

	}

	/**
	 * merge: <br>
	 * Description
	 * 
	 * @param measure
	 * @return
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceMeasure#merge(lu.itrust.business.TS.model.standard.measure.Measure)
	 */
	@Transactional
	@Override
	public Measure merge(Measure measure)  {
		return daoMeasure.merge(measure);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param measure
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceMeasure#delete(lu.itrust.business.TS.model.standard.measure.Measure)
	 */
	@Transactional
	@Override
	public void delete(Measure measure)  {
		daoMeasure.delete(measure);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param id
	 * @
	 * 
	 * @see lu.itrust.business.TS.database.service.ServiceMeasure#delete(int)
	 */
	@Transactional
	@Override
	public void delete(Integer id)  {
		daoMeasure.delete(id);
	}

	/**
	 * getFromAnalysisAndStandardAndReference: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.service.ServiceMeasure#getFromAnalysisAndStandardAndReference(java.lang.Integer,
	 *      java.lang.Integer, java.lang.String)
	 */

	@Override
	public Measure getFromAnalysisAndStandardAndReference(Integer idAnalysis, Integer idStandard, String reference)  {
		return daoMeasure.getFromAnalysisAndStandardAndReference(idAnalysis, idStandard, reference);
	}

	@Override
	public List<Measure> getByIdAnalysisAndIds(Integer idAnalysis, List<Integer> ids) {
		return daoMeasure.getByIdAnalysisAndIds(idAnalysis, ids);
	}

	@Override
	public Measure getByAnalysisAndStandardAndReference(Integer idAnalysis, String standard, String reference) {
		return daoMeasure.getByAnalysisAndStandardAndReference(idAnalysis,standard,reference);
	}

	@Override
	public List<Measure> getByAnalysisAndStandardAndReferences(Integer idAnalysis, String standard, List<String> references) {
		return daoMeasure.getByAnalysisAndStandardAndReferences(idAnalysis,standard,references);
	}
}