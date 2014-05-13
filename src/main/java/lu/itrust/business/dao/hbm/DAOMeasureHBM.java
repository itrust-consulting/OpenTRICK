package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.Norm;
import lu.itrust.business.TS.NormMeasure;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.dao.DAOMeasure;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 * DAOMeasureHBM.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.à.rl.
 * @version
 * @since Feb 12, 2013
 */
@Repository
public class DAOMeasureHBM extends DAOHibernate implements DAOMeasure {

	/**
	 * Constructor: <br>
	 */
	public DAOMeasureHBM() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOMeasureHBM(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasure#get(int)
	 */
	@Override
	public Measure get(Integer id) throws Exception {
		return (Measure) getSession().get(Measure.class, id);
	}

	/**
	 * getMeasureFromAnalysisIdById: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasure#getMeasureFromAnalysisIdById(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@Override
	public Measure getFromAnalysisById(Integer idAnalysis, Integer id) throws Exception {
		String query = "Select measure From AnalysisNorm as analysisNorm inner join analysisNorm.measures as measure where analysisNorm.analysis.id = :analysis and measure.id = :id";
		return (Measure) getSession().createQuery(query).setParameter("analysis", idAnalysis).setParameter("id", id).uniqueResult();
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasure#belongsToAnalysis(int, int)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer measureId) throws Exception {
		String query = "Select count(measure) From AnalysisNorm as analysisNorm inner join analysisNorm.measures as measure where analysisNorm.analysis.id = :analysis and ";
		query += "measure.id = : measureId";
		return ((Long) getSession().createQuery(query).setParameter("analysisid", analysisId).setParameter("measureId", measureId).uniqueResult()).intValue() > 0;
	}

	/**
	 * getAllMeasures: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasure#getAllMeasures()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Measure> getAll() throws Exception {
		return (List<Measure>) getSession().createQuery("From Measure").list();
	}

	/**
	 * getAllFromAnalysisId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasure#getAllFromAnalysisId(Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Measure> getAllFromAnalysis(Integer idAnalysis) throws Exception {
		String query = "Select measure From AnalysisNorm as analysisNorm inner join analysisNorm.measures as measure where analysisNorm.analysis.id = :analysis order by measure.id";
		return getSession().createQuery(query).setParameter("analysis", idAnalysis).list();
	}

	/**
	 * getSOAMeasuresFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasure#getSOAMeasuresFromAnalysis(Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Measure> getSOAMeasuresFromAnalysis(Integer idAnalysis) throws Exception {
		String query = "Select measure From AnalysisNorm as analysisNorm inner join analysisNorm.measures as measure where analysisNorm.norm.label = :norm and analysisNorm.analysis.id";
		query += "= :analysis order by analysisNorm.norm.label ASC, measure.id ASC";
		return getSession().createQuery(query).setParameter("analysis", idAnalysis).setParameter("norm", Constant.NORM_27002).list();
	}

	/**
	 * getAllMeasuresFromAnalysisIdAndComputable: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasure#getAllMeasuresFromAnalysisIdAndComputable(Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Measure> getAllComputableFromAnalysis(Integer idAnalysis) throws Exception {
		String query = "Select measure From AnalysisNorm as analysisNorm inner join analysisNorm.measures as measure where analysisNorm.analysis.id = :idAnalysis and ";
		query += "measure.measureDescription.computable = true order by measure.id";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).list();
	}

	/**
	 * getAllMeasuresFromAnalysisIdAndNormId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasure#getAllMeasuresFromAnalysisIdAndNormId(Integer,
	 *      Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Measure> getAllFromAnalysisAndNorm(Integer idAnalysis, Integer idNorm) throws Exception {
		String query = "Select measure From AnalysisNorm as analysisNorm inner join analysisNorm.measures as measure where analysisNorm.norm.id = :norm and analysisNorm.analysis.id = ";
		query += ":analysis order by measure.id";
		return getSession().createQuery(query).setParameter("analysis", idAnalysis).setParameter("norm", idNorm).list();
	}

	/**
	 * getAllMeasuresFromAnalysisIdAndNormLabel: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasure#getAllMeasuresFromAnalysisIdAndNormLabel(Integer,
	 *      java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Measure> getAllFromAnalysisAndNorm(Integer idAnalysis, String norm) throws Exception {
		String query = "Select measure From AnalysisNorm as analysisNorm inner join analysisNorm.measures as measure where analysisNorm.norm.label = :norm and analysisNorm.analysis.id = ";
		query += ":analysis order by measure.id";
		return getSession().createQuery(query).setParameter("analysis", idAnalysis).setParameter("norm", norm).list();
	}

	/**
	 * getAllMeasuresFromAnalysisIdAndNorm: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasure#getAllMeasuresFromAnalysisIdAndNorm(Integer,
	 *      lu.itrust.business.TS.Norm)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Measure> getAllFromAnalysisAndNorm(Integer idAnalysis, Norm norm) throws Exception {
		String query = "Select measure From AnalysisNorm as analysisNorm inner join analysisNorm.measures as measure where analysisNorm.norm = :norm and analysisNorm.analysis.id = ";
		query += ":analysis order by measure.id";
		return getSession().createQuery(query).setParameter("analysis", idAnalysis).setParameter("norm", norm).list();
	}

	/**
	 * getAllNormMeasuresFromAnalysisId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasure#getAllNormMeasuresFromAnalysisId(Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<NormMeasure> getAllNormMeasuresFromAnalysis(Integer idAnalysis) throws Exception {
		String query = "Select measure From AnalysisNorm as analysisNorm inner join analysisNorm.measures as measure where analysisNorm.analysis.id = :idAnalysis and ";
		query += "exists(From NormMeasure measure2 where measure2 = measure) order by measure.id";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).list();
	}

	/**
	 * getAllNormMeasuresFromAnalysisIdAndComputable: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasure#getAllNormMeasuresFromAnalysisIdAndComputable(Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<NormMeasure> getAllNormMeasuresFromAnalysisAndComputable(Integer idAnalysis) throws Exception {
		String query = "Select measure From AnalysisNorm as analysisNorm inner join analysisNorm.measures as measure where analysisNorm.analysis.id = :idAnalysis and ";
		query += "measure.measureDescription.computable = true and measure.status='AP' and exists(From NormMeasure measure2 where measure2 = measure) order by measure.id ";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).list();
	}

	/**
	 * getAllAnalysisNormsFromAnalysisByMeasureIdList: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasure#getAllAnalysisNormsFromAnalysisByMeasureIdList(Integer,
	 *      java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<NormMeasure> getAllNormMeasuresFromAnalysisByMeasureIdList(Integer idAnalysis, List<Integer> measures) throws Exception {
		String query = "Select measure From AnalysisNorm as analysisNorm inner join analysisNorm.measures as measure where analysisNorm.analysis.id = :analysis and measure.id in ";
		query += ":measures order by measure.id";
		return getSession().createQuery(query).setParameter("analysis", idAnalysis).setParameterList("measures", measures).list();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasure#save(lu.itrust.business.TS.Measure)
	 */
	@Override
	public Measure save(Measure measure) throws Exception {
		return (Measure) getSession().save(measure);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasure#saveOrUpdate(lu.itrust.business.TS.Measure)
	 */
	@Override
	public void saveOrUpdate(Measure measure) throws Exception {
		getSession().saveOrUpdate(measure);
	}

	/**
	 * merge: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasure#merge(lu.itrust.business.TS.Measure)
	 */
	@Override
	public Measure merge(Measure measure) throws Exception {
		return (Measure) getSession().merge(measure);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasure#delete(lu.itrust.business.TS.Measure)
	 */
	@Override
	public void delete(Measure measure) throws Exception {
		getSession().delete(measure);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOMeasure#delete(Integer)
	 */
	@Override
	public void delete(Integer id) throws Exception {
		delete(get(id));
	}
}