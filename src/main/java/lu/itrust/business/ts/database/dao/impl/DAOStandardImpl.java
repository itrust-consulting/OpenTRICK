package lu.itrust.business.ts.database.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAOStandard;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.standard.Standard;
import lu.itrust.business.ts.model.standard.StandardType;

/**
 * DAOStandardImpl.java: <br>
 * Detailed description...
 * 
 * @author itrust consulting s.à.rl.
 * @version
 * @since Feb 12, 2013
 */
@Repository
public class DAOStandardImpl extends DAOHibernate implements DAOStandard {

	/**
	 * Constructor: <br>
	 */
	public DAOStandardImpl() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOStandardImpl(Session session) {
		super(session);
	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOStandard#get(int)
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
	 * @see lu.itrust.business.ts.database.dao.DAOStandard#findByLabel(java.lang.String)
	 */
	@Override
	public List<Standard> findByLabel(String label) {
		return createQueryWithCache("from Standard where label = :label", Standard.class).setParameter("label", label).list();
	}

	/**
	 * getStandardNotCustomByName: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.ts.database.dao.DAOStandard#findByLabelAndAnalysisOnlyFalse(java.lang.String)
	 */
	@Override
	public List<Standard> findByLabelAndAnalysisOnlyFalse(String label) {
		return createQueryWithCache("from Standard where label = :label and analysisOnly=false", Standard.class).setParameter("label", label).list();
	}

	/**
	 * getStandardByNameAndVersion: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.ts.database.dao.DAOStandard#getStandardByLabelAndVersion(java.lang.String,
	 *      java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Standard getStandardByLabelAndVersion(String label, Integer version) {
		return (Standard) createQueryWithCache("from Standard where label = :label and version = :version").setParameter("label", label).setParameter("version", version)
				.uniqueResultOptional().orElse(null);
	}

	/**
	 * existsByNameAndVersion: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOStandard#existsByLabelAndVersion(java.lang.String,
	 *      int)
	 */
	@Override
	public boolean existsByLabelAndVersion(String label, Integer version) {
		return (boolean) createQueryWithCache("select count(*)>0 from Standard where label = :label and version = :version").setParameter("label", label)
				.setParameter("version", version).getSingleResult();
	}

	/**
	 * getAll: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.ts.database.dao.DAOStandard#getAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Standard> getAll() {
		return (List<Standard>) createQueryWithCache("From Standard").getResultList();
	}

	/**
	 * getAllFromAnalysisId: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOStandard#getAllFromAnalysisId(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Standard> getAllFromAnalysis(Integer analysisId) {
		return createQueryWithCache(
				"Select analysisStandard.standard From Analysis analysis join analysis.analysisStandards analysisStandard where analysis.id = :analysisId order by analysisStandard.standard.label")
				.setParameter("analysisId", analysisId).getResultList();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOStandard#getAllFromAnalysis(lu.itrust.business.ts.model.analysis.Analysis)
	 */
	@Override
	public List<Standard> getAllFromAnalysis(Analysis analysis) {
		return analysis.findStandards();
	}

	/**
	 * getAllNotInAnalysis: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.ts.database.dao.DAOStandard#getAllNotInAnalysis(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Standard> getAllNotInAnalysis(Integer idAnalysis) {
		final String query = "Select std From Standard as std where std.analysisOnly=false and std.id not in "
				+ "(Select std1.id From Standard as std1 join Analysis as analysis on analysis.id = :analysisId "
				+ "inner join analysis.analysisStandards as analysisStandard left join analysisStandard.standard as std2 "
				+ "where std1.name = std2.name or std1.label = std2.label ) order by std.name, std.label";
		return createQueryWithCache(query).setParameter("analysisId", idAnalysis).getResultList();
	}

	/**
	 * save: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.ts.database.dao.DAOStandard#save(lu.itrust.business.ts.model.standard.Standard)
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
	 * @see lu.itrust.business.ts.database.dao.DAOStandard#saveOrUpdate(lu.itrust.business.ts.model.standard.Standard)
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
	 * @see lu.itrust.business.ts.database.dao.DAOStandard#delete(lu.itrust.business.ts.model.standard.Standard)
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
	 * @see lu.itrust.business.ts.database.dao.DAOStandard#getAllNotBoundToAnalysis()
	 **/
	@Override
	public List<Standard> getAllNotBoundToAnalysis() {
		return createQueryWithCache("SELECT standard From Standard standard where standard.analysisOnly=false order by standard.label", Standard.class).getResultList();
	}

	@Override
	public List<Standard> getAllAnalysisOnlyStandardsFromAnalysis(Integer analsisID) {
		return createQueryWithCache(
				"Select analysisStandard.standard From Analysis analysis join analysis.analysisStandards analysisStandard where analysis.id = :analysisId and analysisStandard.standard.analysisOnly=true order by analysisStandard.standard.label",
				Standard.class).setParameter("analysisId", analsisID).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Standard> getAllFromAnalysisNotBound(Integer analysisId) {
		return createQueryWithCache(
				"Select analysisStandard.standard From Analysis analysis join analysis.analysisStandards analysisStandard where analysisStandard.standard.analysisOnly=false and analysis.id = :analysisId order by analysisStandard.standard.label")
				.setParameter("analysisId", analysisId).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Integer getNextVersionByLabelAndType(String label, StandardType standardType) {
		return (Integer) createQueryWithCache("select max(standard.version)+1 from Standard standard where standard.label = :label and standard.type = :type")
				.setParameter("label", label).setParameter("type", standardType).uniqueResultOptional().orElse(1);
	}

	@Override
	public boolean existsByLabelVersionType(String label, Integer version, StandardType type) {
		return (boolean) createQueryWithCache("select count(*)>0 from Standard where label = :label and version = :version and type = :type").setParameter("label", label)
				.setParameter("version", version).setParameter("type", type).getSingleResult();
	}

	@Override
	public boolean belongsToAnalysis(int idAnalysis, Integer idStandard) {
		return (boolean) createQueryWithCache(
				"select count(*)>0 from Analysis analysis inner join analysis.analysisStandards as analysisStandard where analysis.id = :idAnalysis and analysisStandard.standard.id = :idStandard")
				.setParameter("idAnalysis", idAnalysis).setParameter("idStandard", idStandard).getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public int getNextVersion(String label) {
		return (int) createQueryWithCache("Select max(version) + 1 From Standard where label = :label").setParameter("label", label).uniqueResultOptional().orElse(1);
	}

	@Override
	public boolean isUsed(Standard standard) {
		return (boolean) createQueryWithCache("select count(*)>0 from AnalysisStandard where standard = :standard").setParameter("standard", standard).getSingleResult();
	}

	@Override
	public List<Standard> getAllNotInAnalysisAndNotMaturity(Integer idAnalysis) {
		final String query = "Select std From Standard as std where std.type <> :type and std.analysisOnly=false and std.id not in "
				+ "(Select std1.id From Standard as std1 join Analysis as analysis on analysis.id = :analysisId "
				+ "inner join analysis.analysisStandards as analysisStandard left join analysisStandard.standard as std2 "
				+ "where std1.name = std2.name or std1.label = std2.label ) order by std.name, std.label";
		return createQueryWithCache(query, Standard.class).setParameter("type", StandardType.MATURITY).setParameter("analysisId", idAnalysis).getResultList();
	}

	@Override
	public boolean existsByLabel(String name) {
		return createQueryWithCache("select count(*) > 0 from Standard where label = :name", Boolean.class).setParameter("name", name).uniqueResult();
	}

	@Override
	public boolean isLabelConflicted(String newName, String oldName) {
		return createQueryWithCache(
				"SELECT count(*)> 0 FROM Analysis als1 INNER JOIN als1.analysisStandards as alsStd1 where alsStd1.standard.label = :newName and (Select count(*)> 0 from Analysis als2 inner join als2.analysisStandards as alsStd2 where als1 = als2 and alsStd2.standard.label = :oldName) = true",
				Boolean.class).setParameter("newName", newName).setParameter("oldName", oldName).uniqueResult();
	}

	@Override
	public boolean isNameConflicted(String newName, String oldName) {
		return createQueryWithCache(
				"SELECT count(*)> 0 FROM Analysis als1 INNER JOIN als1.analysisStandards as alsStd1 where alsStd1.standard.name = :newName and (Select count(*)> 0 from Analysis als2 inner join als2.analysisStandards as alsStd2 where als1 = als2 and alsStd2.standard.name = :oldName) = true",
				Boolean.class).setParameter("newName", newName).setParameter("oldName", oldName).uniqueResult();
	}

}