package lu.itrust.business.TS.database.dao.hbm;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.database.dao.DAOMeasure;
import lu.itrust.business.TS.model.actionplan.ActionPlanMode;
import lu.itrust.business.TS.model.standard.Standard;
import lu.itrust.business.TS.model.standard.measure.MaturityMeasure;
import lu.itrust.business.TS.model.standard.measure.Measure;
import lu.itrust.business.TS.model.standard.measure.NormalMeasure;

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
	 * @see lu.itrust.business.TS.database.dao.DAOMeasure#get(int)
	 */
	@Override
	public Measure get(Integer id) {
		return (Measure) getSession().get(Measure.class, id);
	}

	/**
	 * getMeasureFromAnalysisIdById: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOMeasure#getMeasureFromAnalysisIdById(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Measure getFromAnalysisById(Integer idAnalysis, Integer id) {
		String query = "Select measure From Analysis analysis join analysis.analysisStandards analysisStandard inner join analysisStandard.measures measure where analysis.id = :analysis and measure.id = :id";
		return (Measure) getSession().createQuery(query).setParameter("analysis", idAnalysis).setParameter("id", id).uniqueResultOptional().orElse(null);
	}

	/**
	 * belongsToAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOMeasure#belongsToAnalysis(int,
	 *      int)
	 */
	@Override
	public boolean belongsToAnalysis(Integer analysisId, Integer measureId) {
		String query = "Select count(measure)>0 From Analysis analysis join analysis.analysisStandards analysisStandard inner join analysisStandard.measures measure where analysis.id = :analysisId and ";
		query += "measure.id = :measureId";
		return (boolean) getSession().createQuery(query).setParameter("analysisId", analysisId).setParameter("measureId", measureId).getSingleResult();
	}

	/**
	 * getAllMeasures: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOMeasure#getAllMeasures()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Measure> getAll() {
		return (List<Measure>) getSession().createQuery("From Measure").getResultList();
	}

	/**
	 * getAllFromAnalysisId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOMeasure#getAllFromAnalysisId(Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Measure> getAllFromAnalysis(Integer idAnalysis) {
		String query = "Select measure From Analysis analysis join analysis.analysisStandards analysisStandard inner join analysisStandard.measures measure where analysis.id = :analysis order by measure.id";
		return getSession().createQuery(query).setParameter("analysis", idAnalysis).getResultList();
	}

	/**
	 * getAllMeasuresFromAnalysisIdAndComputable: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOMeasure#getAllMeasuresFromAnalysisIdAndComputable(Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Measure> getAllComputableFromAnalysis(Integer idAnalysis) {
		String query = "Select measure From Analysis analysis join analysis.analysisStandards analysisStandard inner join analysisStandard.measures measure where analysis.id = :idAnalysis and ";
		query += "measure.measureDescription.computable = true order by measure.id";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).getResultList();
	}

	/**
	 * getAllFromAnalysisAndStandard: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.dao.DAOMeasure#getAllFromAnalysisAndStandard(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Measure> getAllFromAnalysisAndStandard(Integer idAnalysis, Integer idStandard) {
		String query = "Select measure From Analysis analysis join analysis.analysisStandards analysisStandard inner join analysisStandard.measures measure where analysisStandard.standard.id = :idStandard and analysis.id = ";
		query += ":analysis order by measure.id";
		return getSession().createQuery(query).setParameter("analysis", idAnalysis).setParameter("idStandard", idStandard).getResultList();
	}

	/**
	 * getAllFromAnalysisAndStandard: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.dao.DAOMeasure#getAllFromAnalysisAndStandard(java.lang.Integer,
	 *      java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Measure> getAllFromAnalysisAndStandard(Integer idAnalysis, String standard) {
		String query = "Select measure From Analysis analysis join analysis.analysisStandards analysisStandard inner join analysisStandard.measures measure where analysisStandard.standard.label = :standard and analysis.id = ";
		query += ":analysis order by measure.id";
		return getSession().createQuery(query).setParameter("analysis", idAnalysis).setParameter("standard", standard).getResultList();
	}

	/**
	 * getAllFromAnalysisAndStandard: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.dao.DAOMeasure#getAllFromAnalysisAndStandard(java.lang.Integer,
	 *      lu.itrust.business.TS.model.standard.Standard)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Measure> getAllFromAnalysisAndStandard(Integer idAnalysis, Standard standard) {
		String query = "Select measure From Analysis analysis join analysis.analysisStandards analysisStandard inner join analysisStandard.measures measure where analysisStandard.standard = :standard and analysis.id = ";
		query += ":analysis order by measure.id";
		return getSession().createQuery(query).setParameter("analysis", idAnalysis).setParameter("standard", standard).getResultList();
	}

	/**
	 * getAllNormalMeasuresFromAnalysis: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.dao.DAOMeasure#getAllNormalMeasuresFromAnalysis(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<NormalMeasure> getAllNormalMeasuresFromAnalysis(Integer idAnalysis) {
		String query = "Select measure From Analysis analysis join analysis.analysisStandards analysisStandard inner join analysisStandard.measures measure where analysis.id = :idAnalysis and ";
		query += "exists(From NormalMeasure measure2 where measure2 = measure) order by measure.id";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).getResultList();
	}

	/**
	 * getAllNormalMeasuresFromAnalysisAndComputable: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.dao.DAOMeasure#getAllNormalMeasuresFromAnalysisAndComputable(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<NormalMeasure> getAllNormalMeasuresFromAnalysisAndComputable(Integer idAnalysis) {
		String query = "Select measure From Analysis analysis join analysis.analysisStandards analysisStandard inner join analysisStandard.measures measure where analysis.id = :idAnalysis and ";
		query += "measure.measureDescription.computable = true and measure.status='AP' and exists(From NormalMeasure measure2 where measure2 = measure) order by measure.id ";
		return getSession().createQuery(query).setParameter("idAnalysis", idAnalysis).getResultList();
	}

	/**
	 * getAllNormalMeasuresFromAnalysisAndComputable: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.dao.DAOMeasure#getAllNormalMeasuresFromAnalysisAndComputable(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Measure> getAllNotMaturityMeasuresFromAnalysisAndComputable(Integer idAnalysis) {
		return getSession()
				.createQuery(
						"Select measure From Analysis analysis join analysis.analysisStandards analysisStandard inner join analysisStandard.measures measure where analysis.id = :idAnalysis and analysisStandard.standard.type != 'MATURITY' and measure.status <> 'NA'")
				.setParameter("idAnalysis", idAnalysis).getResultList();
	}

	/**
	 * getAllNormalMeasuresFromAnalysisByMeasureIdList: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.dao.DAOMeasure#getAllNormalMeasuresFromAnalysisByMeasureIdList(java.lang.Integer,
	 *      java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Measure> getAllNotMaturityMeasuresFromAnalysisByMeasureIdList(Integer idAnalysis, List<Integer> measures) {
		String query = "Select measure From Analysis analysis join analysis.analysisStandards analysisStandard inner join analysisStandard.measures as measure where analysis.id = :analysis and measure.id in ";
		query += ":measures order by measure.id";
		return getSession().createQuery(query).setParameter("analysis", idAnalysis).setParameterList("measures", measures).getResultList();
	}

	/**
	 * getAllNormalMeasuresFromAnalysisByMeasureIdList: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.dao.DAOMeasure#getAllNormalMeasuresFromAnalysisByMeasureIdList(java.lang.Integer,
	 *      java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<NormalMeasure> getAllNormalMeasuresFromAnalysisByMeasureIdList(Integer idAnalysis, List<Integer> measures) {
		String query = "Select measure From Analysis analysis join analysis.analysisStandards analysisStandard inner join analysisStandard.measures as measure where analysis.id = :analysis and measure.id in ";
		query += ":measures order by measure.id";
		return getSession().createQuery(query).setParameter("analysis", idAnalysis).setParameterList("measures", measures).getResultList();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOMeasure#save(lu.itrust.business.TS.model.standard.measure.Measure)
	 */
	@Override
	public Measure save(Measure measure) {
		return (Measure) getSession().save(measure);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOMeasure#saveOrUpdate(lu.itrust.business.TS.model.standard.measure.Measure)
	 */
	@Override
	public void saveOrUpdate(Measure measure) {
		getSession().saveOrUpdate(measure);
	}

	/**
	 * merge: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOMeasure#merge(lu.itrust.business.TS.model.standard.measure.Measure)
	 */
	@Override
	public Measure merge(Measure measure) {
		return (Measure) getSession().merge(measure);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOMeasure#delete(lu.itrust.business.TS.model.standard.measure.Measure)
	 */
	@Override
	public void delete(Measure measure) {
		getSession().delete(measure);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOMeasure#delete(Integer)
	 */
	@Override
	public void delete(Integer id) {
		delete(get(id));
	}

	/**
	 * mappingAllFromAnalysisAndStandard: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.dao.DAOMeasure#mappingAllFromAnalysisAndStandard(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Measure> mappingAllFromAnalysisAndStandard(Integer idAnalysis, Integer idStandard) {
		return (Map<String, Measure>) getSession()
				.createQuery(
						"Select  measure From Analysis analysis join analysis.analysisStandards analysisStandard inner join analysisStandard.measures measure where analysisStandard.standard.id = :idStandard and  analysis.id= :idAnalysis")
				.setParameter("idAnalysis", idAnalysis).setParameter("idStandard", idStandard).getResultList().stream()
				.collect(Collectors.toMap(measure -> ((Measure) measure).getMeasureDescription().getReference(), Function.identity()));

	}

	/**
	 * countNormalMeasure: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.dao.DAOMeasure#countNormalMeasure()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public int countNormalMeasure() {
		return  (int) getSession().createQuery("Select count(*) From NormalMeasure").uniqueResultOptional().orElse(0);
	}

	/**
	 * getAllNormalMeasure: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.dao.DAOMeasure#getAllNormalMeasure(int,
	 *      int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<NormalMeasure> getAllNormalMeasure(int pageIndex, int pageSize) {
		return getSession().createQuery("From NormalMeasure").setFirstResult((pageIndex - 1) * pageSize).setMaxResults(pageSize).getResultList();
	}

	/**
	 * getIdMeasuresImplementedByActionPlanTypeFromIdAnalysisAndStandard: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.dao.DAOMeasure#getIdMeasuresImplementedByActionPlanTypeFromIdAnalysisAndStandard(int,
	 *      java.lang.String,
	 *      lu.itrust.business.TS.model.actionplan.ActionPlanMode)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getIdMeasuresImplementedByActionPlanTypeFromIdAnalysisAndStandard(int idAnalysis, String standard, ActionPlanMode actionPlanMode) {
		return getSession()
				.createQuery(
						"Select measure.id From Analysis analysis join analysis.analysisStandards analysisStandard inner join analysisStandard.measures measure where analysis.id = :idAnalysis and analysisStandard.standard.label = :standard and measure.id in (Select actionplan.measure.id From Analysis a inner join a.actionPlans actionplan where a.id = :idAnalysis and actionplan.actionPlanType.name = :actionPlanType)")
				.setParameter("idAnalysis", idAnalysis).setParameter("standard", standard).setParameter("actionPlanType", actionPlanMode).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Measure getFromAnalysisAndStandardAndReference(Integer idAnalysis, Integer idStandard, String reference) {
		String query = "Select measure From Analysis analysis join analysis.analysisStandards analysisStandard inner join analysisStandard.measures measure where analysisStandard.standard.id = :idStandard and analysis.id = ";
		query += ":analysis and measure.measureDescription.reference = :reference order by measure.id";
		return (Measure) getSession().createQuery(query).setParameter("analysis", idAnalysis).setParameter("idStandard", idStandard).setParameter("reference", reference)
				.uniqueResultOptional().orElse(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Measure> getByIdAnalysisAndIds(Integer idAnalysis, List<Integer> ids) {
		return getSession()
				.createQuery(
						"Select measure From Analysis analysis join analysis.analysisStandards analysisStandard inner join analysisStandard.measures as measure where analysis.id = :idAnalysis and measure.id in :ids order by measure.id")
				.setParameter("idAnalysis", idAnalysis).setParameterList("ids", ids).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Measure getByAnalysisAndStandardAndReference(Integer idAnalysis, String standard, String reference) {
		return (Measure) getSession()
				.createQuery(
						"Select measure From Analysis analysis join analysis.analysisStandards analysisStandard inner join analysisStandard.measures as measure where analysis.id = :idAnalysis and analysisStandard.standard.label = :standard and measure.measureDescription.reference = :reference")
				.setParameter("idAnalysis", idAnalysis).setParameter("standard", standard).setParameter("reference", reference).uniqueResultOptional().orElse(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Measure> getByAnalysisAndStandardAndReferences(Integer idAnalysis, String standard, List<String> references) {
		return getSession()
				.createQuery(
						"Select measure From Analysis analysis join analysis.analysisStandards analysisStandard inner join analysisStandard.measures as measure where analysis.id = :idAnalysis and analysisStandard.standard.label = :standard and measure.measureDescription.reference in :references")
				.setParameter("idAnalysis", idAnalysis).setParameter("standard", standard).setParameterList("references", references).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Measure> getReferenceStartWith(Integer idAnalysis, String standard, String reference) {
		return getSession()
				.createQuery(
						"Select measure From Analysis analysis join analysis.analysisStandards analysisStandard inner join analysisStandard.measures as measure where analysis.id = :idAnalysis and analysisStandard.standard.label = :standard and measure.measureDescription.reference like :reference")
				.setParameter("idAnalysis", idAnalysis).setParameter("standard", standard).setParameter("reference", reference + "%").getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Measure> getByAnalysisIdStandardAndChapters(Integer idAnalysis, String standard, List<String> chapters) {
		String request = "Select measure From Analysis analysis join analysis.analysisStandards analysisStandard inner join analysisStandard.measures as measure where analysis.id = :idAnalysis and analysisStandard.standard.label = :standard",
				subSql = "";
		for (int i = 0; i < chapters.size(); i++)
			subSql += (subSql.isEmpty() ? "" : " or ") + "measure.measureDescription.reference like :reference" + i;
		if (!subSql.isEmpty())
			request += " and (" + subSql + ")";
		Query<Measure> query = getSession().createQuery(request).setParameter("idAnalysis", idAnalysis).setParameter("standard", standard);
		for (int i = 0; i < chapters.size(); i++)
			query.setParameter("reference" + i, chapters.get(i) + "%");
		return query.getResultList();
	}

	@Override
	public MaturityMeasure getMaturityMeasure(Integer id) throws Exception {
		return (MaturityMeasure) getSession().get(MaturityMeasure.class, id);
	}
}