package lu.itrust.business.TS.database.dao.hbm;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.constants.Constant;
import lu.itrust.business.TS.database.dao.DAOStandard;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
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
	public Standard get(Integer id) throws Exception {
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
	 * @see lu.itrust.business.TS.database.dao.DAOStandard#getStandardNotCustomByName(java.lang.String)
	 */
	@Override
	public Standard getStandardNotCustomByName(String label) throws Exception {
		return (Standard) getSession().createQuery("from Standard where label = :label and label != :custom").setParameter("label", label)
				.setParameter("custom", Constant.STANDARD_CUSTOM).uniqueResult();
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
	@Override
	public Standard getStandardByNameAndVersion(String label, Integer version) throws Exception {
		return (Standard) getSession().createQuery("from Standard where label = :label and version = :version").setParameter("label", label).setParameter("version", version)
				.uniqueResult();
	}

	/**
	 * existsByNameAndVersion: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOStandard#existsByNameAndVersion(java.lang.String,
	 *      int)
	 */
	@Override
	public boolean existsByNameAndVersion(String label, Integer version) throws Exception {
		return ((Long) getSession().createQuery("select count(*) from Standard where label = :label and version = :version").setParameter("label", label)
				.setParameter("version", version).uniqueResult()).intValue() != 0;
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
	public List<Standard> getAll() throws Exception {
		return (List<Standard>) getSession().createQuery("From Standard").list();
	}

	/**
	 * getAllFromAnalysisId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOStandard#getAllFromAnalysisId(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Standard> getAllFromAnalysis(Integer analysisId) throws Exception {
		return getSession().createQuery("Select analysisStandard.standard From Analysis analysis join analysis.analysisStandards analysisStandard where analysis.id = :analysisId")
				.setParameter("analysisId", analysisId).list();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOStandard#getAllFromAnalysis(lu.itrust.business.TS.model.analysis.Analysis)
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
	 * @see lu.itrust.business.TS.database.dao.DAOStandard#getAllNotInAnalysis(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Standard> getAllNotInAnalysis(Integer idAnalysis) throws Exception {
		String query = "Select standard From Standard standard where standard.analysisOnly=false and standard.label NOT IN (Select analysisStandard.standard.label From Analysis analysis join analysis.analysisStandards analysisStandard where analysis.id = :analysisId)";

		return getSession().createQuery(query).setParameter("analysisId", idAnalysis).list();
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
	public void save(Standard standard) throws Exception {
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
	public void saveOrUpdate(Standard standard) throws Exception {
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
	public void delete(Standard standard) throws Exception {
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
	public List<Standard> getAllNotBoundToAnalysis() throws Exception {

		List<Standard> standards = getSession().createQuery("SELECT standard From Standard standard where standard.analysisOnly=false").list();

		return standards;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Standard> getAllAnalysisOnlyStandardsFromAnalysis(Integer analsisID) throws Exception {
		return (List<Standard>) getSession()
				.createQuery(
						"Select analysisStandard.standard From Analysis analysis join analysis.analysisStandards analysisStandard where analysis.id = :analysisId and analysisStandard.standard.analysisOnly=true")
				.setParameter("analysisId", analsisID).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Standard> getAllFromAnalysisNotBound(Integer analysisId) throws Exception {
		return getSession()
				.createQuery(
						"Select analysisStandard.standard From Analysis analysis join analysis.analysisStandards analysisStandard where analysisStandard.standard.analysisOnly=false and analysis.id = :analysisId")
				.setParameter("analysisId", analysisId).list();
	}

	@Override
	public Integer getNextVersionByNameAndType(String label, StandardType standardType) throws Exception {
		Integer version =  (Integer) getSession().createQuery("select max(standard.version)+1 from Standard standard where standard.label = :label and standard.type = :type")
				.setParameter("label", label).setParameter("type", standardType).uniqueResult();
		return version == null? 1 : version;
	}

	@Override
	public boolean existsByNameVersionType(String label, Integer version, StandardType type) throws Exception {
		return (boolean) getSession().createQuery("select count(*)>0 from Standard where label = :label and version = :version and type = :type").setParameter("label", label)
				.setParameter("version", version).setParameter("type", type).uniqueResult();
	}

	@Override
	public boolean belongToAnalysis(Integer idStandard, int idAnalysis) {
		return (boolean) getSession()
				.createQuery(
						"select count(*)>0 from Analysis analysis inner join analysis.analysisStandards as analysisStandard where analysis.id = :idAnalysis and analysisStandard.standard.id = :idStandard")
				.setParameter("idAnalysis", idAnalysis).setParameter("idStandard", idStandard).uniqueResult();
	}

	@Override
	public int getNextVersion(String label) {
		Integer version =  (Integer) getSession().createQuery("Select max(version) + 1 From Standard where label = :label").setString("label", label).uniqueResult();
		return version == null? 1 : version;
	}

	@Override
	public boolean isUsed(Standard standard) {
		return (boolean) getSession()
				.createQuery(
						"select count(*)>0 from AnalysisStandard where standard = :standard")
				.setParameter("standard", standard).uniqueResult();
	}

}