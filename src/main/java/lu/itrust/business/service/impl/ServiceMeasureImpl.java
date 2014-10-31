package lu.itrust.business.service.impl;

import java.util.List;

import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.Standard;
import lu.itrust.business.TS.NormalMeasure;
import lu.itrust.business.dao.DAOMeasure;
import lu.itrust.business.service.ServiceMeasure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceMeasure#get(int)
	 */

	@Override
	public Measure get(Integer id) throws Exception {
		return daoMeasure.get(id);
	}

	/**
	 * getMeasureFromAnalysisIdById: <br>
	 * Description
	 * 
	 * @param id
	 * @param idAnalysis
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceMeasure#getMeasureFromAnalysisIdById(java.lang.Integer,
	 *      java.lang.Integer)
	 */

	@Override
	public Measure getFromAnalysisById(Integer idAnalysis, Integer id) throws Exception {
		return daoMeasure.getFromAnalysisById(idAnalysis, id);
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @param assetId
	 * @param analysisId
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceMeasure#belongsToAnalysis(int, int)
	 */

	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer assetId) throws Exception {
		return daoMeasure.belongsToAnalysis(analysisId, assetId);
	}

	/**
	 * getAllMeasures: <br>
	 * Description
	 * 
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceMeasure#getAllMeasures()
	 */

	@Override
	public List<Measure> getAll() throws Exception {
		return daoMeasure.getAll();
	}

	/**
	 * getAllFromAnalysisId: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceMeasure#getAllFromAnalysisId(int)
	 */

	@Override
	public List<Measure> getAllFromAnalysis(Integer idAnalysis) throws Exception {
		return daoMeasure.getAllFromAnalysis(idAnalysis);
	}

	/**
	 * getSOAMeasuresFromAnalysis: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceMeasure#getSOAMeasuresFromAnalysis(int)
	 */

	@Override
	public List<Measure> getSOAMeasuresFromAnalysis(Integer idAnalysis) throws Exception {
		return daoMeasure.getSOAMeasuresFromAnalysis(idAnalysis);
	}

	/**
	 * getAllMeasuresFromAnalysisIdAndComputable: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceMeasure#getAllMeasuresFromAnalysisIdAndComputable(int)
	 */

	@Override
	public List<Measure> getAllComputableFromAnalysis(Integer idAnalysis) throws Exception {
		return daoMeasure.getAllComputableFromAnalysis(idAnalysis);
	}

	/**
	 * getAllFromAnalysisAndStandard: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.service.ServiceMeasure#getAllFromAnalysisAndStandard(java.lang.Integer,
	 *      java.lang.Integer)
	 */

	@Override
	public List<Measure> getAllFromAnalysisAndStandard(Integer idAnalysis, Integer idStandard) throws Exception {
		return daoMeasure.getAllFromAnalysisAndStandard(idAnalysis, idStandard);
	}

	/**
	 * getAllFromAnalysisAndStandard: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.service.ServiceMeasure#getAllFromAnalysisAndStandard(java.lang.Integer,
	 *      java.lang.String)
	 */

	@Override
	public List<Measure> getAllFromAnalysisAndStandard(Integer idAnalysis, String standard) throws Exception {
		return daoMeasure.getAllFromAnalysisAndStandard(idAnalysis, standard);
	}

	/**
	 * getAllFromAnalysisAndStandard: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.service.ServiceMeasure#getAllFromAnalysisAndStandard(java.lang.Integer,
	 *      lu.itrust.business.TS.Standard)
	 */

	@Override
	public List<Measure> getAllFromAnalysisAndStandard(Integer idAnalysis, Standard standard) throws Exception {
		return daoMeasure.getAllFromAnalysisAndStandard(idAnalysis, standard);
	}

	/**
	 * getAllNormalMeasuresFromAnalysis: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.service.ServiceMeasure#getAllNormalMeasuresFromAnalysis(java.lang.Integer)
	 */

	@Override
	public List<NormalMeasure> getAllNormalMeasuresFromAnalysis(Integer idAnalysis) throws Exception {
		return daoMeasure.getAllNormalMeasuresFromAnalysis(idAnalysis);
	}

	/**
	 * getAllNormalMeasuresFromAnalysisAndComputable: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.service.ServiceMeasure#getAllNormalMeasuresFromAnalysisAndComputable(java.lang.Integer)
	 */

	@Override
	public List<NormalMeasure> getAllNormalMeasuresFromAnalysisAndComputable(Integer idAnalysis) throws Exception {
		return daoMeasure.getAllNormalMeasuresFromAnalysisAndComputable(idAnalysis);
	}

	/**
	 * getAllNotMaturityMeasuresFromAnalysisAndComputable: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.service.ServiceMeasure#getAllNotMaturityMeasuresFromAnalysisAndComputable(java.lang.Integer)
	 */
	@Override
	public List<Measure> getAllNotMaturityMeasuresFromAnalysisAndComputable(Integer idAnalysis) throws Exception {
		return daoMeasure.getAllNotMaturityMeasuresFromAnalysisAndComputable(idAnalysis);
	}

	/**
	 * getAllNormalMeasuresFromAnalysisByMeasureIdList: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.service.ServiceMeasure#getAllNormalMeasuresFromAnalysisByMeasureIdList(java.lang.Integer,
	 *      java.util.List)
	 */

	@Override
	public List<NormalMeasure> getAllNormalMeasuresFromAnalysisByMeasureIdList(Integer idAnalysis, List<Integer> measures) throws Exception {
		return daoMeasure.getAllNormalMeasuresFromAnalysisByMeasureIdList(idAnalysis, measures);
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @param measure
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceMeasure#save(lu.itrust.business.TS.Measure)
	 */
	@Transactional
	@Override
	public Measure save(Measure measure) throws Exception {
		return daoMeasure.save(measure);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @param measure
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceMeasure#saveOrUpdate(lu.itrust.business.TS.Measure)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(Measure measure) throws Exception {
		daoMeasure.saveOrUpdate(measure);

	}

	/**
	 * merge: <br>
	 * Description
	 * 
	 * @param measure
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceMeasure#merge(lu.itrust.business.TS.Measure)
	 */
	@Transactional
	@Override
	public Measure merge(Measure measure) throws Exception {
		return daoMeasure.merge(measure);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param measure
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceMeasure#delete(lu.itrust.business.TS.Measure)
	 */
	@Transactional
	@Override
	public void delete(Measure measure) throws Exception {
		daoMeasure.delete(measure);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @param id
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceMeasure#delete(int)
	 */
	@Transactional
	@Override
	public void delete(Integer id) throws Exception {
		daoMeasure.delete(id);
	}

	/**
	 * getFromAnalysisAndStandardAndReference: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.service.ServiceMeasure#getFromAnalysisAndStandardAndReference(java.lang.Integer,
	 *      java.lang.Integer, java.lang.String)
	 */

	@Override
	public Measure getFromAnalysisAndStandardAndReference(Integer idAnalysis, Integer idStandard, String reference) throws Exception {
		return daoMeasure.getFromAnalysisAndStandardAndReference(idAnalysis, idStandard, reference);
	}
}