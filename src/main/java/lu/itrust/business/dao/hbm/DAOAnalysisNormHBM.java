package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.AnalysisNorm;
import lu.itrust.business.TS.Norm;
import lu.itrust.business.dao.DAOAnalysisNorm;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
@Repository
public class DAOAnalysisNormHBM extends DAOHibernate implements DAOAnalysisNorm {
	
	/**
	 * 
	 */
	public DAOAnalysisNormHBM() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param sessionFactory
	 */
	public DAOAnalysisNormHBM(Session session) {
		super(session);
	}

	@Override
	public AnalysisNorm get(int id) throws Exception {
		return (AnalysisNorm) getSession().get(AnalysisNorm.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AnalysisNorm> loadAll() throws Exception {
		Query query = getSession().createQuery("From AnalysisNorm");
		return (List<AnalysisNorm>) query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AnalysisNorm> loadAllFromAnalysis(Analysis analysis)
			throws Exception {
		Query query = getSession().createQuery(
				"From AnalysisNorm where analysis = :analysis");
		query.setParameter("analysis", analysis);
		return (List<AnalysisNorm>) query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AnalysisNorm> loadAllFromNorm(Norm norm) throws Exception {
		Query query = getSession().createQuery(
				"From AnalysisNorm where norm = :norm");
		query.setParameter("norm", norm);
		return (List<AnalysisNorm>) query.list();

	}

	@Override
	public void save(AnalysisNorm analysisNorm) throws Exception {
		getSession().save(analysisNorm);

	}

	@Override
	public void saveOrUpdate(AnalysisNorm analysisNorm) throws Exception {
		getSession().saveOrUpdate(analysisNorm);
	}

	@Override
	public void remove(AnalysisNorm analysisNorm) throws Exception {
		getSession().delete(analysisNorm);
	}
}
