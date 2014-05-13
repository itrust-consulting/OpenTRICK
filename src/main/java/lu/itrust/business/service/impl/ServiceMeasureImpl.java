package lu.itrust.business.service.impl;

import java.util.List;

import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.Norm;
import lu.itrust.business.TS.NormMeasure;
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
	 * getAllMeasuresFromAnalysisIdAndNormId: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param idNorm
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceMeasure#getAllMeasuresFromAnalysisIdAndNormId(int,
	 *      int)
	 */
	@Override
	public List<Measure> getAllFromAnalysisAndNorm(Integer idAnalysis, Integer idNorm) throws Exception {
		return daoMeasure.getAllFromAnalysisAndNorm(idAnalysis, idNorm);
	}

	/**
	 * getAllMeasuresFromAnalysisIdAndNormLabel: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param norm
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceMeasure#getAllMeasuresFromAnalysisIdAndNormLabel(int,
	 *      java.lang.String)
	 */
	@Override
	public List<Measure> getAllFromAnalysisAndNorm(Integer idAnalysis, String norm) throws Exception {
		return daoMeasure.getAllFromAnalysisAndNorm(idAnalysis, norm);
	}

	/**
	 * getAllMeasuresFromAnalysisIdAndNorm: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param norm
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceMeasure#getAllMeasuresFromAnalysisIdAndNorm(int,
	 *      lu.itrust.business.TS.Norm)
	 */
	@Override
	public List<Measure> getAllFromAnalysisAndNorm(Integer idAnalysis, Norm norm) throws Exception {
		return daoMeasure.getAllFromAnalysisAndNorm(idAnalysis, norm);
	}

	/**
	 * getAllNormMeasuresFromAnalysisId: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceMeasure#getAllNormMeasuresFromAnalysisId(int)
	 */
	@Override
	public List<NormMeasure> getAllNormMeasuresFromAnalysis(Integer idAnalysis) throws Exception {
		return daoMeasure.getAllNormMeasuresFromAnalysis(idAnalysis);
	}

	/**
	 * getAllNormMeasuresFromAnalysisIdAndComputable: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceMeasure#getAllNormMeasuresFromAnalysisIdAndComputable(int)
	 */
	@Override
	public List<NormMeasure> getAllNormMeasuresFromAnalysisAndComputable(Integer idAnalysis) throws Exception {
		return daoMeasure.getAllNormMeasuresFromAnalysisAndComputable(idAnalysis);
	}

	/**
	 * getAllAnalysisNormsFromAnalysisByMeasureIdList: <br>
	 * Description
	 * 
	 * @param idAnalysis
	 * @param measures
	 * @return
	 * @throws Exception
	 * 
	 * @see lu.itrust.business.service.ServiceMeasure#getAllAnalysisNormsFromAnalysisByMeasureIdList(int,
	 *      java.util.List)
	 */
	@Override
	public List<NormMeasure> getAllNormMeasuresFromAnalysisByMeasureIdList(Integer idAnalysis, List<Integer> measures) throws Exception {
		return daoMeasure.getAllNormMeasuresFromAnalysisByMeasureIdList(idAnalysis, measures);
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
}