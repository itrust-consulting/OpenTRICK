package lu.itrust.business.dao.hbm;

import java.util.ArrayList;
import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.AnalysisNorm;
import lu.itrust.business.TS.Norm;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.dao.DAONorm;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 * DAONormHBM.java: <br>
 * Detailed description...
 *
 * @author smenghi, itrust consulting s.à.rl.
 * @version 
 * @since Feb 12, 2013
 */
@Repository
public class DAONormHBM extends DAOHibernate implements DAONorm {

	/**
	 * Constructor: <br>
	 */
	public DAONormHBM() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAONormHBM(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAONorm#get(int)
	 */
	@Override
	public Norm get(int id) throws Exception {
		return (Norm) getSession().get(Norm.class, id);
	}

	/**
	 * getNormByName: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAONorm#getNormByName(java.lang.String)
	 */
	@Override
	public Norm getNormByName(String label) throws Exception {
		return (Norm) getSession().createQuery("from Norm where label = :label").setParameter("label", label).uniqueResult();
	}

	/**
	 * getNormNotCustomByName: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAONorm#getNormNotCustomByName(java.lang.String)
	 */
	@Override
	public Norm getNormNotCustomByName(String label) throws Exception {
		return (Norm) getSession().createQuery("from Norm where label = :label and label != :custom").setParameter("label", label).setParameter("custom", Constant.NORM_CUSTOM)
				.uniqueResult();
	}

	/**
	 * getNormByNameAndVersion: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAONorm#getNormByNameAndVersion(java.lang.String, int)
	 */
	@Override
	public Norm getNormByNameAndVersion(String label, int version) throws Exception {
		return (Norm) getSession().createQuery("from Norm where label = :label and version = :version").setParameter("label", label).setParameter("version", version).uniqueResult();
	}

	/**
	 * existsByNameAndVersion: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAONorm#existsByNameAndVersion(java.lang.String, int)
	 */
	@Override
	public boolean existsByNameAndVersion(String label, int version) throws Exception {
		return ((Long) getSession().createQuery("select count(*) from Norm where label = :label and version = :version").setParameter("label", label).setParameter("version", version)
				.uniqueResult()).intValue() != 0;
	}

	/**
	 * getAllNorms: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAONorm#getAllNorms()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Norm> getAllNorms() throws Exception {
		return (List<Norm>) getSession().createQuery("From Norm").list();
	}

	/**
	 * getAllFromAnalysisId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAONorm#getAllFromAnalysisId(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Norm> getAllFromAnalysisId(int analysisId) throws Exception {
		return getSession().createQuery("Select analysisNorm.norm From AnalysisNorm as analysisNorm where analysisNorm.analysis.id = :analysisId").setParameter("analysisId", analysisId)
				.list();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAONorm#getAllFromAnalysis(lu.itrust.business.TS.Analysis)
	 */
	@Override
	public List<Norm> getAllFromAnalysis(Analysis analysis) throws Exception {
		List<Norm> norms = new ArrayList<Norm>();
		for (AnalysisNorm anorm : analysis.getAnalysisNorms())
			norms.add(anorm.getNorm());
		return norms;
	}

	/**
	 * getAllNormsNotInAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAONorm#getAllNormsNotInAnalysis(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Norm> getAllNormsNotInAnalysis(int idAnalysis) throws Exception {
		String query = "Select norm From Norm norm where norm.label not in (Select analysisNorm.norm.label From AnalysisNorm as analysisNorm where analysisNorm.analysis.id = :analysisId)";
		return getSession().createQuery(query).setParameter("analysisId", idAnalysis).list();
	}

	/**
	 * save: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAONorm#save(lu.itrust.business.TS.Norm)
	 */
	@Override
	public void save(Norm norm) throws Exception {
		getSession().save(norm);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAONorm#saveOrUpdate(lu.itrust.business.TS.Norm)
	 */
	@Override
	public void saveOrUpdate(Norm norm) throws Exception {
		getSession().saveOrUpdate(norm);
	}

	/**
	 * delete: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAONorm#delete(lu.itrust.business.TS.Norm)
	 */
	@Override
	public void delete(Norm norm) throws Exception {
		getSession().delete(norm);
	}
}