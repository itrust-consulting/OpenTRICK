package lu.itrust.business.ts.database.dao.hbm;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.ts.database.dao.DAOAnalysisStandard;
import lu.itrust.business.ts.model.analysis.Analysis;
import lu.itrust.business.ts.model.standard.AnalysisStandard;
import lu.itrust.business.ts.model.standard.Standard;
import lu.itrust.business.ts.model.standard.StandardType;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescription;
import lu.itrust.business.ts.model.standard.measuredescription.MeasureDescriptionText;

/**
 * DAOAnalysisStandardHBM.java: <br>
 * Detailed description...
 *
 * @author smenghi itrust consulting s.a.rl.:
 * @version
 * @since Feb 12, 2013
 */
@Repository
public class DAOAnalysisStandardHBM extends DAOHibernate implements DAOAnalysisStandard {

	/**
	 * Constructor: <br>
	 */
	public DAOAnalysisStandardHBM() {
	}

	/**
	 * Constructor: <br>
	 * 
	 * @param session
	 */
	public DAOAnalysisStandardHBM(Session session) {
		super(session);
	}

	@Override
	public boolean belongsToAnalysis(Integer idAnalysis, int id) {
		return (Boolean) getSession().createQuery(
				"select count(analysisStandard) > 0 From Analysis analysis inner join analysis.analysisStandards analysisStandard where analysis.id = :idAnalysis and analysisStandard.id = :id")
				.setParameter("idAnalysis", idAnalysis).setParameter("id", id).getSingleResult();
	}

	/**
	 * delete: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.ts.database.dao.DAOAnalysisStandard#delete(lu.itrust.business.ts.model.standard.AnalysisStandard)
	 */
	@Override
	public void delete(AnalysisStandard analysisStandard) {
		getSession().delete(analysisStandard);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deleteAllFromAnalysis(Integer analysisId) {
		final Analysis analysis = (Analysis) getSession()
				.createQuery("select analysis from Analysis analysis where analysis.id = :analysis")
				.setParameter("analysis", analysisId)
				.uniqueResultOptional().orElse(null);

		final List<AnalysisStandard> standards = new ArrayList<AnalysisStandard>();

		for (AnalysisStandard standard : analysis.getAnalysisStandards().values()) {

			getSession().delete(standard);

			if (standard.getStandard().isAnalysisOnly())
				standards.add(standard);

		}

		analysis.getAnalysisStandards().clear();

		getSession().saveOrUpdate(analysis);

		for (AnalysisStandard standard : standards) {

			final Standard tmpstandard = standard.getStandard();

			List<MeasureDescription> mesDescs = (List<MeasureDescription>) getSession()
					.createQuery("SELECT mesDesc from MeasureDescription mesDesc where mesDesc.standard= :standard")
					.setParameter("standard", tmpstandard).getResultList();

			for (MeasureDescription mesDesc : mesDescs) {
				for (MeasureDescriptionText mesDescText : mesDesc.getMeasureDescriptionTexts())
					getSession().delete(mesDescText);
				getSession().delete(mesDesc);
			}
			getSession().delete(tmpstandard);
		}

	}

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAnalysisStandard#get(int)
	 */
	@Override
	public AnalysisStandard get(Integer id) {
		return (AnalysisStandard) getSession().get(AnalysisStandard.class, id);
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAnalysisStandard#getAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AnalysisStandard> getAll() {
		return (List<AnalysisStandard>) getSession().createQuery("From AnalysisStandard order by standard.label")
				.getResultList();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAnalysisStandard#getAllFromAnalysis(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AnalysisStandard> getAllComputableFromAnalysis(Integer analysisID) {
		return (List<AnalysisStandard>) getSession().createQuery(
				"SELECT analysisStandard From Analysis analysis inner join analysis.analysisStandards analysisStandard where analysis.id = :analysis and analysisStandard.standard.computable = true ORDER BY analysisStandard.standard.label ASC")
				.setParameter("analysis", analysisID).getResultList();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAnalysisStandard#getAllFromAnalysis(lu.itrust.business.ts.model.analysis.Analysis)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AnalysisStandard> getAllFromAnalysis(Analysis analysis) {
		return (List<AnalysisStandard>) getSession().createQuery(
				"SELECT analysisStandard From Analysis analysis inner join analysis.analysisStandards analysisStandard where analysis = :analysis order by analysisStandard.standard.label")
				.setParameter("analysis", analysis).getResultList();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.ts.database.dao.DAOAnalysisStandard#getAllFromAnalysis(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AnalysisStandard> getAllFromAnalysis(Integer analysisID) {
		return (List<AnalysisStandard>) getSession().createQuery(
				"SELECT analysisStandard From Analysis analysis inner join analysis.analysisStandards analysisStandard where analysis.id = :analysis ORDER BY analysisStandard.standard.label ASC")
				.setParameter("analysis", analysisID).getResultList();
	}

	/**
	 * getAllFromStandard: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.ts.database.dao.DAOAnalysisStandard#getAllFromStandard(lu.itrust.business.ts.model.standard.Standard)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AnalysisStandard> getAllFromStandard(Standard standard) {
		return (List<AnalysisStandard>) getSession().createQuery("From AnalysisStandard where standard = :standard")
				.setParameter("standard", standard).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Integer getAnalysisIDFromAnalysisStandard(Integer analysisStandard) {
		return (Integer) getSession()
				.createQuery(
						"SELECT analysis.id From Analysis analysis inner join analysis.analysisStandards analysisStandard where analysisStandard.id = :analysisstandard")
				.setParameter("analysisstandard", analysisStandard).uniqueResultOptional().orElse(-1);
	}

	/**
	 * getFromAnalysisIdAndStandardId: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.ts.database.dao.DAOAnalysisStandard#getFromAnalysisIdAndStandardId(java.lang.Integer,
	 *      int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public AnalysisStandard getFromAnalysisIdAndStandardId(Integer idAnalysis, int idStandard) {
		return (AnalysisStandard) getSession().createQuery(
				"select analysisStandard From Analysis analysis inner join analysis.analysisStandards analysisStandard where analysis.id = :idAnalysis and analysisStandard.standard.id = :idStandard")
				.setParameter("idAnalysis", idAnalysis).setParameter("idStandard", idStandard).uniqueResultOptional()
				.orElse(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public AnalysisStandard getFromAnalysisIdAndStandardName(Integer idAnalysis, String name) {
		return (AnalysisStandard) getSession().createQuery(
				"select analysisStandard From Analysis analysis inner join analysis.analysisStandards analysisStandard where analysis.id = :idAnalysis and analysisStandard.standard.label = :standardName")
				.setParameter("idAnalysis", idAnalysis).setParameter("standardName", name).uniqueResultOptional()
				.orElse(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Standard getStandardById(int idAnalysisStandard) {
		return (Standard) getSession().createQuery(
				"Select analysisStandard.standard From AnalysisStandard analysisStandard where analysisStandard.id = :id")
				.setParameter("id", idAnalysisStandard).uniqueResultOptional().orElse(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getStandardNameById(int idAnalysisStandard) {
		return (String) getSession().createQuery(
				"Select analysisStandard.standard.label From AnalysisStandard analysisStandard where analysisStandard.id = :id")
				.setParameter("id", idAnalysisStandard).uniqueResultOptional().orElse(null);
	}

	@Override
	public Boolean hasStandard(Integer idAnalysis, String standard) {
		return (Boolean) getSession().createQuery(
				"select count(analysisStandard)>0 From Analysis analysis inner join analysis.analysisStandards analysisStandard where analysis.id = :idAnalysis and analysisStandard.standard.label = :standardName")
				.setParameter("idAnalysis", idAnalysis).setParameter("standardName", standard).getSingleResult();
	}

	/**
	 * save: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.ts.database.dao.DAOAnalysisStandard#save(lu.itrust.business.ts.model.standard.AnalysisStandard)
	 */
	@Override
	public void save(AnalysisStandard analysisStandard) {
		getSession().save(analysisStandard);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.ts.database.dao.DAOAnalysisStandard#saveOrUpdate(lu.itrust.business.ts.model.standard.AnalysisStandard)
	 */
	@Override
	public void saveOrUpdate(AnalysisStandard analysisStandard) {
		getSession().saveOrUpdate(analysisStandard);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AnalysisStandard> findBySOAEnabledAndAnalysisId(boolean state, Integer idAnalysis) {
		return getSession().createQuery(
				"Select analysisStandard From Analysis analysis join analysis.analysisStandards analysisStandard where analysis.id = :analysis and analysisStandard.soaEnabled = :state order by analysisStandard.standard.label ASC")
				.setParameter("analysis", idAnalysis).setParameter("state", state).getResultList();
	}

	@Override
	public List<AnalysisStandard> findByAndAnalysisIdAndTypeIn(Integer analysisId, Class<?>... classes) {
		return getSession().createQuery(
				"Select analysisStandard From Analysis analysis join analysis.analysisStandards analysisStandard where analysis.id = :analysis and type(analysisStandard) in :types order by analysisStandard.standard.label ASC",
				AnalysisStandard.class).setParameter("analysis", analysisId).setParameterList("types", classes)
				.getResultList();
	}

	@Override
	public AnalysisStandard findOne(int id, int analysisId) {
		return (AnalysisStandard) getSession().createQuery(
				"Select analysisStandard From Analysis analysis join analysis.analysisStandards analysisStandard where analysis.id = :analysis and analysisStandard.id = :id",
				AnalysisStandard.class).setParameter("analysis", analysisId).setParameter("id", id)
				.uniqueResultOptional().orElse(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AnalysisStandard> findByComputableAndAnalysisIdAndTypeIn(boolean computable, Integer idAnalysis,
			Class<?>... classes) {
		return getSession().createQuery(
				"Select analysisStandard From Analysis analysis join analysis.analysisStandards analysisStandard where analysis.id = :analysis and analysisStandard.standard.computable = :computable and type(analysisStandard) in :types order by analysisStandard.standard.label ASC")
				.setParameter("analysis", idAnalysis).setParameter("computable", computable)
				.setParameterList("types", classes).getResultList();
	}

	@Override
	public List<Standard> findStandardByComputableAndAnalysisIdAndTypeIn(boolean computable, Integer idAnalysis,
			Class<?>... classes) {
		return getSession().createQuery(
				"Select analysisStandard.standard From Analysis analysis join analysis.analysisStandards analysisStandard where analysis.id = :analysis and analysisStandard.standard.computable = :computable and type(analysisStandard) in :types order by analysisStandard.standard.label ASC",
				Standard.class).setParameter("analysis", idAnalysis).setParameter("computable", computable)
				.setParameterList("types", classes).getResultList();
	}

	@Override
	public List<Standard> findStandardByAnalysisIdAndTypeIn(Integer idAnalysis, Class<?>... classes) {
		return getSession().createQuery(
				"Select analysisStandard.standard From Analysis analysis join analysis.analysisStandards analysisStandard where analysis.id = :analysis and type(analysisStandard) in :types order by analysisStandard.standard.label ASC",
				Standard.class).setParameter("analysis", idAnalysis).setParameterList("types", classes).getResultList();
	}

	@Override
	public long countByStandard(Standard standard) {
		return getSession().createQuery("Select count(*) From AnalysisStandard where standard = :standard", Long.class)
				.setParameter("standard", standard).uniqueResult();
	}

	@Override
	public List<AnalysisStandard> findByStandard(int page, int size, Standard standard) {
		return getSession().createQuery("From AnalysisStandard where standard = :standard", AnalysisStandard.class)
				.setParameter("standard", standard).setMaxResults(size)
				.setFirstResult((page - 1) * size).list();
	}

	@Override
	public List<String> findByAnalysisAndNameLikeAndTypeAndCustom(Integer idAnalysis, String name, StandardType type,
			boolean custom) {
		return getSession().createQuery(
				"Select analysisStandard.standard.label From Analysis analysis join analysis.analysisStandards analysisStandard where analysis.id = :analysis and analysisStandard.standard.label like :name and analysisStandard.standard.type = :stdType order by analysisStandard.standard.label ASC",
				String.class).setParameter("analysis", idAnalysis).setParameter("name", name)
				.setParameter("stdType", type).getResultList();
	}

}