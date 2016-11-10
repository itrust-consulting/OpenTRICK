package lu.itrust.business.TS.database.dao.hbm;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.dao.DAOStandard;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.standard.Standard;
import lu.itrust.business.TS.model.standard.StandardType;

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
	 * @see lu.itrust.business.TS.database.dao.DAOStandard#get(int)
	 */
	@Override
	public Standard get(Integer id) {
		return (Standard) getSession().get(Standard.class, id);
	}

	/**
	 * getStandardByName: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.dao.DAOStandard#getStandardByName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Standard getStandardByName(String label) {
		return (Standard) getSession().createQuery("from Standard where label = :label").setParameter("label", label).uniqueResultOptional().orElse(null);
	}

	/**
	 * getStandardNotCustomByName: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.dao.DAOStandard#getStandardNotCustomByName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Standard getStandardNotCustomByName(String label) {
		return (Standard) getSession().createQuery("from Standard where label = :label and label != :custom").setParameter("label", label)
				.setParameter("custom", Constant.STANDARD_CUSTOM).uniqueResultOptional().orElse(null);
	}

	/**
	 * getStandardByNameAndVersion: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.dao.DAOStandard#getStandardByNameAndVersion(java.lang.String,
	 *      java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Standard getStandardByNameAndVersion(String label, Integer version) {
		return (Standard) getSession().createQuery("from Standard where label = :label and version = :version").setParameter("label", label).setParameter("version", version)
				.uniqueResultOptional().orElse(null);
	}

	/**
	 * existsByNameAndVersion: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOStandard#existsByNameAndVersion(java.lang.String,
	 *      int)
	 */
	@Override
	public boolean existsByNameAndVersion(String label, Integer version) {
		return  (boolean) getSession().createQuery("select count(*)>0 from Standard where label = :label and version = :version").setParameter("label", label)
				.setParameter("version", version).getSingleResult();
	}

	/**
	 * getAll: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.dao.DAOStandard#getAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Standard> getAll() {
		return (List<Standard>) getSession().createQuery("From Standard").getResultList();
	}

	/**
	 * getAllFromAnalysisId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOStandard#getAllFromAnalysisId(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Standard> getAllFromAnalysis(Integer analysisId) {
		return getSession()
				.createQuery(
						"Select analysisStandard.standard From Analysis analysis join analysis.analysisStandards analysisStandard where analysis.id = :analysisId order by analysisStandard.standard.label")
				.setParameter("analysisId", analysisId).getResultList();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOStandard#getAllFromAnalysis(lu.itrust.business.TS.model.analysis.Analysis)
	 */
	@Override
	public List<Standard> getAllFromAnalysis(Analysis analysis) {
		return analysis.getStandards();
	}

	/**
	 * getAllNotInAnalysis: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.dao.DAOStandard#getAllNotInAnalysis(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Standard> getAllNotInAnalysis(Integer idAnalysis) {
		String query = "Select standard From Standard standard where standard.analysisOnly=false and standard.label NOT IN (Select analysisStandard.standard.label From Analysis analysis join analysis.analysisStandards analysisStandard where analysis.id = :analysisId) order by standard.label";
		return getSession().createQuery(query).setParameter("analysisId", idAnalysis).getResultList();
	}

	/**
	 * save: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.dao.DAOStandard#save(lu.itrust.business.TS.model.standard.Standard)
	 */
	@Override
	public void save(Standard standard) {
		getSession().save(standard);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.dao.DAOStandard#saveOrUpdate(lu.itrust.business.TS.model.standard.Standard)
	 */
	@Override
	public void saveOrUpdate(Standard standard) {
		getSession().saveOrUpdate(standard);
	}

	/**
	 * delete: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.dao.DAOStandard#delete(lu.itrust.business.TS.model.standard.Standard)
	 */
	@Override
	public void delete(Standard standard) {
		getSession().delete(standard);
	}

	/**
	 * getAllNotBoundToAnalysis: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.dao.DAOStandard#getAllNotBoundToAnalysis()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Standard> getAllNotBoundToAnalysis() {
		return getSession().createQuery("SELECT standard From Standard standard where standard.analysisOnly=false order by standard.label").getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Standard> getAllAnalysisOnlyStandardsFromAnalysis(Integer analsisID) {
		return (List<Standard>) getSession()
				.createQuery(
						"Select analysisStandard.standard From Analysis analysis join analysis.analysisStandards analysisStandard where analysis.id = :analysisId and analysisStandard.standard.analysisOnly=true order by analysisStandard.standard.label")
				.setParameter("analysisId", analsisID).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Standard> getAllFromAnalysisNotBound(Integer analysisId) {
		return getSession()
				.createQuery(
						"Select analysisStandard.standard From Analysis analysis join analysis.analysisStandards analysisStandard where analysisStandard.standard.analysisOnly=false and analysis.id = :analysisId order by analysisStandard.standard.label")
				.setParameter("analysisId", analysisId).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Integer getNextVersionByNameAndType(String label, StandardType standardType) {
		return (Integer) getSession().createQuery("select max(standard.version)+1 from Standard standard where standard.label = :label and standard.type = :type")
				.setParameter("label", label).setParameter("type", standardType).uniqueResultOptional().orElse(1);
	}

	@Override
	public boolean existsByNameVersionType(String label, Integer version, StandardType type) {
		return (boolean) getSession().createQuery("select count(*)>0 from Standard where label = :label and version = :version and type = :type").setParameter("label", label)
				.setParameter("version", version).setParameter("type", type).getSingleResult();
	}

	@Override
	public boolean belongToAnalysis(Integer idStandard, int idAnalysis) {
		return (boolean) getSession()
				.createQuery(
						"select count(*)>0 from Analysis analysis inner join analysis.analysisStandards as analysisStandard where analysis.id = :idAnalysis and analysisStandard.standard.id = :idStandard")
				.setParameter("idAnalysis", idAnalysis).setParameter("idStandard", idStandard).getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public int getNextVersion(String label) {
		return (int) getSession().createQuery("Select max(version) + 1 From Standard where label = :label").setParameter("label", label).uniqueResultOptional().orElse(1);
	}

	@Override
	public boolean isUsed(Standard standard) {
		return (boolean) getSession().createQuery("select count(*)>0 from AnalysisStandard where standard = :standard").setParameter("standard", standard).getSingleResult();
	}

}