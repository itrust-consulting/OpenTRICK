package lu.itrust.business.dao.hbm;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.Standard;
import lu.itrust.business.TS.NormalMeasure;
import lu.itrust.business.TS.actionplan.ActionPlanMode;
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
		String query = "Select measure From AnalysisStandard as analysisStandard inner join analysisStandard.measures as measure where analysisStandard.analysis.id = :analysis and measure.id = :id";
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
		String query = "Select count(measure) From AnalysisStandard as analysisStandard inner join analysisStandard.measures as measure where analysisStandard.analysis.id = :analysisId and ";
		query += "measure.id = :measureId";
		return ((Long) getSession().createQuery(query).setParameter("analysisId", analysisId).setParameter("measureId", measureId).uniqueResult()).intValue() > 0;
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
		String query = "Select measure From AnalysisStandard as analysisStandard inner join analysisStandard.measures as measure where analysisStandard.analysis.id = :analysis order by measure.id";
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
		String query = "Select measure From AnalysisStandard as analysisStandard inner join analysisStandard.measures as measure where analysisStandard.standard.label = :standard and analysisStandard.analysis.id";
		query += "= :analysis order by analysisStandard.standard.label ASC, measure.id ASC";
		return getSession().createQuery(query).setParameter("analysis", idAnalysis).setParameter("standard", Constant.STANDARD_27002).list();
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
		String query = "Select measure From AnalysisStandard as analysisStandard inner join analysisStandard.measures as measure where analysisStandard.analysis.id = :idAnalysis and ";
		query += "measure.measureDescription.computable = true order by measure.id";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).list();
	}

	/**
	 * getAllFromAnalysisAndStandard: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.dao.DAOMeasure#getAllFromAnalysisAndStandard(java.lang.Integer, java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Measure> getAllFromAnalysisAndStandard(Integer idAnalysis, Integer idStandard) throws Exception {
		String query = "Select measure From AnalysisStandard as analysisStandard inner join analysisStandard.measures as measure where analysisStandard.standard.id = :idStandard and analysisStandard.analysis.id = ";
		query += ":analysis order by measure.id";
		return getSession().createQuery(query).setParameter("analysis", idAnalysis).setParameter("idStandard", idStandard).list();
	}

	/**
	 * getAllFromAnalysisAndStandard: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.dao.DAOMeasure#getAllFromAnalysisAndStandard(java.lang.Integer, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Measure> getAllFromAnalysisAndStandard(Integer idAnalysis, String standard) throws Exception {
		String query = "Select measure From AnalysisStandard as analysisStandard inner join analysisStandard.measures as measure where analysisStandard.standard.label = :standard and analysisStandard.analysis.id = ";
		query += ":analysis order by measure.id";
		return getSession().createQuery(query).setParameter("analysis", idAnalysis).setParameter("standard", standard).list();
	}

	/**
	 * getAllFromAnalysisAndStandard: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.dao.DAOMeasure#getAllFromAnalysisAndStandard(java.lang.Integer, lu.itrust.business.TS.Standard)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Measure> getAllFromAnalysisAndStandard(Integer idAnalysis, Standard standard) throws Exception {
		String query = "Select measure From AnalysisStandard as analysisStandard inner join analysisStandard.measures as measure where analysisStandard.standard = :standard and analysisStandard.analysis.id = ";
		query += ":analysis order by measure.id";
		return getSession().createQuery(query).setParameter("analysis", idAnalysis).setParameter("standard", standard).list();
	}

	/**
	 * getAllNormalMeasuresFromAnalysis: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.dao.DAOMeasure#getAllNormalMeasuresFromAnalysis(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<NormalMeasure> getAllNormalMeasuresFromAnalysis(Integer idAnalysis) throws Exception {
		String query = "Select measure From AnalysisStandard as analysisStandard inner join analysisStandard.measures as measure where analysisStandard.analysis.id = :idAnalysis and ";
		query += "exists(From NormalMeasure measure2 where measure2 = measure) order by measure.id";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).list();
	}

	/**
	 * getAllNormalMeasuresFromAnalysisAndComputable: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.dao.DAOMeasure#getAllNormalMeasuresFromAnalysisAndComputable(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<NormalMeasure> getAllNormalMeasuresFromAnalysisAndComputable(Integer idAnalysis) throws Exception {
		String query = "Select measure From AnalysisStandard as analysisStandard inner join analysisStandard.measures as measure where analysisStandard.analysis.id = :idAnalysis and ";
		query += "measure.measureDescription.computable = true and measure.status='AP' and exists(From NormalMeasure measure2 where measure2 = measure) order by measure.id ";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).list();
	}

	/**
	 * getAllNormalMeasuresFromAnalysisByMeasureIdList: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.dao.DAOMeasure#getAllNormalMeasuresFromAnalysisByMeasureIdList(java.lang.Integer, java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<NormalMeasure> getAllNormalMeasuresFromAnalysisByMeasureIdList(Integer idAnalysis, List<Integer> measures) throws Exception {
		String query = "Select measure From AnalysisStandard as analysisStandard inner join analysisStandard.measures as measure where analysisStandard.analysis.id = :analysis and measure.id in ";
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

	/**
	 * mappingAllFromAnalysisAndStandard: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.dao.DAOMeasure#mappingAllFromAnalysisAndStandard(java.lang.Integer, java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Measure> mappingAllFromAnalysisAndStandard(Integer idAnalysis, Integer idStandard) {
		Iterator<Measure> iterator = getSession()
				.createQuery(
						"Select  measure From AnalysisStandard analysisStandard inner join analysisStandard.measures as measure where analysisStandard.standard.id = :idStandard and  analysisStandard.analysis.id= :idAnalysis")
				.setParameter("idAnalysis", idAnalysis).setParameter("idStandard", idStandard).iterate();
		Map<String, Measure> result = new LinkedHashMap<String, Measure>();
		while (iterator.hasNext()) {
			Measure measure = iterator.next();
			result.put(measure.getMeasureDescription().getReference(), measure);
		}
		return result;
	}

	/**
	 * countNormalMeasure: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.dao.DAOMeasure#countNormalMeasure()
	 */
	@Override
	public int countNormalMeasure() {
		return ((Long) getSession().createQuery("Select count(*) From NormalMeasure").uniqueResult()).intValue();
	}

	/**
	 * getAllNormalMeasure: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.dao.DAOMeasure#getAllNormalMeasure(int, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<NormalMeasure> getAllNormalMeasure(int pageIndex, int pageSize) {
		return getSession().createQuery("From NormalMeasure").setFirstResult((pageIndex - 1) * pageSize).setMaxResults(pageSize).list();
	}

	/**
	 * getIdMeasuresImplementedByActionPlanTypeFromIdAnalysisAndStandard: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.dao.DAOMeasure#getIdMeasuresImplementedByActionPlanTypeFromIdAnalysisAndStandard(int, java.lang.String, lu.itrust.business.TS.actionplan.ActionPlanMode)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getIdMeasuresImplementedByActionPlanTypeFromIdAnalysisAndStandard(int idAnalysis, String standard, ActionPlanMode actionPlanMode) {
		return getSession()
				.createQuery(
						"Select measure.id From AnalysisStandard as analysisStandard inner join analysisStandard.measures as measure where analysisStandard.analysis.id = :idAnalysis and analysisStandard.standard.label = :standard and measure.id in (Select actionplan.measure.id From Analysis a inner join a.actionPlans actionplan where a.id = :idAnalysis and actionplan.actionPlanType.name = :actionPlanType)")
				.setParameter("idAnalysis", idAnalysis).setString("standard", standard).setParameter("actionPlanType", actionPlanMode).list();
	}
}