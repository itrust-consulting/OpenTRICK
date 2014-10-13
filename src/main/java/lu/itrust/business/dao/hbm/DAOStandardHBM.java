package lu.itrust.business.dao.hbm;

import java.util.ArrayList;
import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.AnalysisStandard;
import lu.itrust.business.TS.Standard;
import lu.itrust.business.TS.tsconstant.Constant;
import lu.itrust.business.dao.DAOStandard;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

/**
 * DAOStandardHBM.java: <br>
 * Detailed description...
 * 
 * @author smenghi, itrust consulting s.à.rl.
 * @version
 * @since Feb 12, 2013
 */
@Repository
public class DAOStandardHBM extends DAOHibernate implements DAOStandard {

	/**
	 * Constructor: <br>
	 */
	public DAOStandardHBM() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOStandardHBM(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOStandard#get(int)
	 */
	@Override
	public Standard get(Integer id) throws Exception {
		return (Standard) getSession().get(Standard.class, id);
	}

	/**
	 * getStandardByName: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.dao.DAOStandard#getStandardByName(java.lang.String)
	 */
	@Override
	public Standard getStandardByName(String label) throws Exception {
		return (Standard) getSession().createQuery("from Standard where label = :label").setParameter("label", label).uniqueResult();
	}

	/**
	 * getStandardNotCustomByName: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.dao.DAOStandard#getStandardNotCustomByName(java.lang.String)
	 */
	@Override
	public Standard getStandardNotCustomByName(String label) throws Exception {
		return (Standard) getSession().createQuery("from Standard where label = :label and label != :custom").setParameter("label", label).setParameter("custom", Constant.STANDARD_CUSTOM)
				.uniqueResult();
	}

	/**
	 * getStandardByNameAndVersion: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.dao.DAOStandard#getStandardByNameAndVersion(java.lang.String,
	 *      java.lang.Integer)
	 */
	@Override
	public Standard getStandardByNameAndVersion(String label, Integer version) throws Exception {
		return (Standard) getSession().createQuery("from Standard where label = :label and version = :version").setParameter("label", label).setParameter("version", version).uniqueResult();
	}

	/**
	 * existsByNameAndVersion: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOStandard#existsByNameAndVersion(java.lang.String, int)
	 */
	@Override
	public boolean existsByNameAndVersion(String label, Integer version) throws Exception {
		return ((Long) getSession().createQuery("select count(*) from Standard where label = :label and version = :version").setParameter("label", label).setParameter("version", version)
				.uniqueResult()).intValue() != 0;
	}

	/**
	 * getAll: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.dao.DAOStandard#getAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Standard> getAll() throws Exception {
		return (List<Standard>) getSession().createQuery("From Standard").list();
	}

	/**
	 * getAllFromAnalysisId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOStandard#getAllFromAnalysisId(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Standard> getAllFromAnalysis(Integer analysisId) throws Exception {
		return getSession().createQuery("Select analysisStandard.standard From AnalysisStandard as analysisStandard where analysisStandard.analysis.id = :analysisId").setParameter("analysisId",
				analysisId).list();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOStandard#getAllFromAnalysis(lu.itrust.business.TS.Analysis)
	 */
	@Override
	public List<Standard> getAllFromAnalysis(Analysis analysis) throws Exception {
		List<Standard> standards = new ArrayList<Standard>();
		for (AnalysisStandard analysisStandard : analysis.getAnalysisStandards())
			standards.add(analysisStandard.getStandard());
		return standards;
	}

	/**
	 * getAllNotInAnalysis: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.dao.DAOStandard#getAllNotInAnalysis(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Standard> getAllNotInAnalysis(Integer idAnalysis) throws Exception {
		String query =
			"Select standard From Standard as standard where standard.label NOT IN (Select analysisStandard.standard.label From AnalysisStandard as analysisStandard where analysisStandard.analysis.id = :analysisId)";
		return getSession().createQuery(query).setParameter("analysisId", idAnalysis).list();
	}

	/**
	 * save: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.dao.DAOStandard#save(lu.itrust.business.TS.Standard)
	 */
	@Override
	public void save(Standard standard) throws Exception {
		getSession().save(standard);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.dao.DAOStandard#saveOrUpdate(lu.itrust.business.TS.Standard)
	 */
	@Override
	public void saveOrUpdate(Standard standard) throws Exception {
		getSession().saveOrUpdate(standard);
	}

	/**
	 * delete: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.dao.DAOStandard#delete(lu.itrust.business.TS.Standard)
	 */
	@Override
	public void delete(Standard standard) throws Exception {
		getSession().delete(standard);
	}

	/**
	 * getAllNotBoundToAnalysis: <br>
	 * Description
	 *
	 * @{tags}
	 *
	 * @see lu.itrust.business.dao.DAOStandard#getAllNotBoundToAnalysis()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Standard> getAllNotBoundToAnalysis() throws Exception {
		return (List<Standard>) getSession().createQuery("From Standard where analysisOnly!=true").list();
	}

}