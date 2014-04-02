/**
 * 
 */
package lu.itrust.business.dao.hbm;

import java.util.List;

import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.Norm;
import lu.itrust.business.TS.NormMeasure;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.dao.DAOMeasure;

/**
 * @author eomar
 * 
 */
@Repository
public class DAOMeasureHBM extends DAOHibernate implements DAOMeasure {

	@Override
	public Measure findOne(int id) {
		return (Measure) getSession().get(Measure.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Measure> findByAnalysis(int idAnalysis) {
		return getSession()
				.createQuery(
						"Select measure "
								+ "From AnalysisNorm as analysisNorm "
								+ "inner join analysisNorm.measures as measure "
								+ "where analysisNorm.analysis.id = :analysis order by measure.id")
				.setParameter("analysis", idAnalysis).list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<NormMeasure> findNormMeasureByAnalysisAndComputable(int idAnalysis) {
		return getSession()
		.createQuery(
				"Select measure "
						+ "From AnalysisNorm as analysisNorm "
						+ "inner join analysisNorm.measures as measure "
						+ "where analysisNorm.analysis.id = :idAnalysis and measure.measureDescription.computable = true and measure.status='AP' and exists(From NormMeasure measure2 where measure2 = measure) order by measure.id ")
		.setParameter("idAnalysis", idAnalysis).list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<NormMeasure> findNormMeasureByAnalysis(int idAnalysis) {
		return getSession()
				.createQuery(
						"Select measure "
								+ "From AnalysisNorm as analysisNorm "
								+ "inner join analysisNorm.measures as measure "
								+ "where analysisNorm.analysis.id = :idAnalysis and exists(From NormMeasure measure2 where measure2 = measure) order by measure.id ")
				.setParameter("idAnalysis", idAnalysis).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Measure> findByAnalysisAndNorm(int idAnalysis, int idNorm) {
		return getSession()
				.createQuery(
						"Select measure "
								+ "From AnalysisNorm as analysisNorm "
								+ "inner join analysisNorm.measures as measure "
								+ "where analysisNorm.norm.id = :norm and analysisNorm.analysis.id = :analysis order by measure.id")
				.setParameter("analysis", idAnalysis)
				.setParameter("norm", idNorm).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Measure> findByAnalysisAndNorm(int idAnalysis, String norm) {
		return getSession()
				.createQuery(
						"Select measure "
								+ "From AnalysisNorm as analysisNorm "
								+ "inner join analysisNorm.measures as measure "
								+ "where analysisNorm.norm.label = :norm and analysisNorm.analysis.id = :analysis order by measure.id")
				.setParameter("analysis", idAnalysis)
				.setParameter("norm", norm).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Measure> findByAnalysisAndNorm(int idAnalysis, Norm norm) {
		return getSession()
				.createQuery(
						"Select measure "
								+ "From AnalysisNorm as analysisNorm "
								+ "inner join analysisNorm.measures as measure "
								+ "where analysisNorm.norm = :norm and analysisNorm.analysis.id = :analysis order by measure.id")
				.setParameter("analysis", idAnalysis)
				.setParameter("norm", norm).list();
	}

	@Override
	public Measure save(Measure measure) {
		return (Measure) getSession().save(measure);
	}

	@Override
	public void saveOrUpdate(Measure measure) {
		getSession().saveOrUpdate(measure);
	}

	@Override
	public Measure merge(Measure measure) {
		return (Measure) getSession().merge(measure);
	}

	@Override
	public void delete(Measure measure) {
		getSession().delete(measure);
	}

	@Override
	public void delete(int id) {
		delete(findOne(id));
	}

	@Override
	public Measure findByIdAndAnalysis(Integer id, Integer idAnalysis) {
		return (Measure) getSession()
				.createQuery(
						"Select measure "
								+ "From AnalysisNorm as analysisNorm "
								+ "inner join analysisNorm.measures as measure "
								+ "where analysisNorm.analysis.id = :analysis and measure.id = :id")
				.setParameter("analysis", idAnalysis)
				.setParameter("id", id).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<NormMeasure> findByAnalysisContains(int idAnalysis, List<Integer> measures) {
		return getSession()
		.createQuery(
				"Select measure "
						+ "From AnalysisNorm as analysisNorm "
						+ "inner join analysisNorm.measures as measure "
						+ "where analysisNorm.analysis.id = :analysis and measure.id in :measures order by measure.id")
		.setParameter("analysis", idAnalysis)
		.setParameterList("measures", measures).list();
	}

	/**
	 * loadSOA: <br>
	 * Loads measures from all 27002 norms to be placed on the SOA table
	 *
	 * @see lu.itrust.business.dao.DAOMeasure#loadSOA(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Measure> loadSOA(int idAnalysis) {
		return getSession()
				.createQuery(
						"Select measure "
								+ "From AnalysisNorm as analysisNorm "
								+ "inner join analysisNorm.measures as measure "
								+ "where analysisNorm.norm.label = :norm and analysisNorm.analysis.id = :analysis order by analysisNorm.norm.label ASC, measure.id ASC")
				.setParameter("analysis", idAnalysis)
				.setParameter("norm", Constant.NORM_27002).list();
	}
}