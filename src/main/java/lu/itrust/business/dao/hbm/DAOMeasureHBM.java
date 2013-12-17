/**
 * 
 */
package lu.itrust.business.dao.hbm;

import java.util.List;

import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.Measure;
import lu.itrust.business.TS.Norm;
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
								+ "where analysisNorm.analysis.id = :analysis")
				.setParameter("analysis", idAnalysis).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Measure> findByAnalysisAndNorm(int idAnalysis, int idNorm) {
		return getSession()
				.createQuery(
						"Select measure "
								+ "From AnalysisNorm as analysisNorm "
								+ "inner join analysisNorm.measures as measure "
								+ "where analysisNorm.norm.id = :norm and analysisNorm.analysis.id = :analysis")
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
								+ "where analysisNorm.norm.label = :norm and analysisNorm.analysis.id = :analysis")
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
								+ "where analysisNorm.norm = :norm and analysisNorm.analysis.id = :analysis")
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

}
