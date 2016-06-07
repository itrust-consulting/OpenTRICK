package lu.itrust.business.TS.database.dao.hbm;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import lu.itrust.business.TS.database.dao.DAOAnalysisStandard;
import lu.itrust.business.TS.model.analysis.Analysis;
import lu.itrust.business.TS.model.standard.AnalysisStandard;
import lu.itrust.business.TS.model.standard.Standard;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescription;
import lu.itrust.business.TS.model.standard.measuredescription.MeasureDescriptionText;

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

	/**
	 * get: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysisStandard#get(int)
	 */
	@Override
	public AnalysisStandard get(Integer id) {
		return (AnalysisStandard) getSession().get(AnalysisStandard.class, id);
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysisStandard#getAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AnalysisStandard> getAll() {
		return (List<AnalysisStandard>) getSession().createQuery("From AnalysisStandard").list();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysisStandard#getAllFromAnalysis(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AnalysisStandard> getAllFromAnalysis(Integer analysisID) {
		return (List<AnalysisStandard>) getSession()
				.createQuery(
						"SELECT analysisStandard From Analysis analysis inner join analysis.analysisStandards analysisStandard where analysis.id = :analysis ORDER BY analysisStandard.standard.label  ASC")
				.setParameter("analysis", analysisID).list();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysisStandard#getAllFromAnalysis(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AnalysisStandard> getAllComputableFromAnalysis(Integer analysisID) {
		return (List<AnalysisStandard>) getSession()
				.createQuery(
						"SELECT analysisStandard From Analysis analysis inner join analysis.analysisStandards analysisStandard where analysis.id = :analysis and analysisStandard.standard.computable = true ORDER BY analysisStandard.standard.label ASC")
				.setParameter("analysis", analysisID).list();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysisStandard#getAllFromAnalysis(lu.itrust.business.TS.model.analysis.Analysis)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AnalysisStandard> getAllFromAnalysis(Analysis analysis) {
		return (List<AnalysisStandard>) getSession()
				.createQuery("SELECT analysisStandard From Analysis analysis inner join analysis.analysisStandards analysisStandard where analysis = :analysis")
				.setParameter("analysis", analysis).list();
	}

	/**
	 * getAllFromStandard: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysisStandard#getAllFromStandard(lu.itrust.business.TS.model.standard.Standard)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AnalysisStandard> getAllFromStandard(Standard standard) {
		return (List<AnalysisStandard>) getSession().createQuery("From AnalysisStandard where standard = :standard").setParameter("standard", standard).list();
	}

	/**
	 * save: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysisStandard#save(lu.itrust.business.TS.model.standard.AnalysisStandard)
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
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysisStandard#saveOrUpdate(lu.itrust.business.TS.model.standard.AnalysisStandard)
	 */
	@Override
	public void saveOrUpdate(AnalysisStandard analysisStandard) {
		getSession().saveOrUpdate(analysisStandard);
	}

	/**
	 * delete: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysisStandard#delete(lu.itrust.business.TS.model.standard.AnalysisStandard)
	 */
	@Override
	public void delete(AnalysisStandard analysisStandard) {
		getSession().delete(analysisStandard);
	}

	/**
	 * getFromAnalysisIdAndStandardId: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.TS.database.dao.DAOAnalysisStandard#getFromAnalysisIdAndStandardId(java.lang.Integer,
	 *      int)
	 */
	@Override
	public AnalysisStandard getFromAnalysisIdAndStandardId(Integer idAnalysis, int idStandard) {
		return (AnalysisStandard) getSession()
				.createQuery(
						"select analysisStandard From Analysis analysis inner join analysis.analysisStandards analysisStandard where analysis.id = :idAnalysis and analysisStandard.standard.id = :idStandard")
				.setParameter("idAnalysis", idAnalysis).setParameter("idStandard", idStandard).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deleteAllFromAnalysis(Integer analysisId) {
		Analysis analysis = (Analysis) getSession().createQuery("select analysis from Analysis analysis where analysis.id = :analysis").setParameter("analysis", analysisId)
				.uniqueResult();

		List<AnalysisStandard> standards = new ArrayList<AnalysisStandard>();

		for (AnalysisStandard standard : analysis.getAnalysisStandards()) {

			getSession().delete(standard);

			if (standard.getStandard().isAnalysisOnly())
				standards.add(standard);

		}

		analysis.getAnalysisStandards().clear();

		getSession().saveOrUpdate(analysis);

		for (AnalysisStandard standard : standards) {

			Standard tmpstandard = standard.getStandard();

			List<MeasureDescription> mesDescs = (List<MeasureDescription>) getSession()
					.createQuery("SELECT mesDesc from MeasureDescription mesDesc where mesDesc.standard= :standard").setParameter("standard", tmpstandard).list();

			for (MeasureDescription mesDesc : mesDescs) {
				for (MeasureDescriptionText mesDescText : mesDesc.getMeasureDescriptionTexts())
					getSession().delete(mesDescText);
				getSession().delete(mesDesc);
			}
			getSession().delete(tmpstandard);
		}

	}

	@Override
	public Integer getAnalysisIDFromAnalysisStandard(Integer analysisStandard) {
		return (Integer) getSession()
				.createQuery("SELECT analysis.id From Analysis analysis inner join analysis.analysisStandards analysisStandard where analysisStandard.id = :analysisstandard")
				.setParameter("analysisstandard", analysisStandard).uniqueResult();
	}

	@Override
	public boolean belongsToAnalysis(Integer idAnalysis, int id) {
		return (Boolean) getSession()
				.createQuery(
						"select count(analysisStandard) > 0 From Analysis analysis inner join analysis.analysisStandards analysisStandard where analysis.id = :idAnalysis and analysisStandard.id = :id")
				.setParameter("idAnalysis", idAnalysis).setParameter("id", id).uniqueResult();
	}

	@Override
	public Standard getStandardById(int idAnalysisStandard) {
		return (Standard) getSession().createQuery("Select analysisStandard.standard From AnalysisStandard analysisStandard where analysisStandard.id = :id")
				.setParameter("id", idAnalysisStandard).uniqueResult();
	}

	@Override
	public String getStandardNameById(int idAnalysisStandard) {
		return (String) getSession().createQuery("Select analysisStandard.standard.label From AnalysisStandard analysisStandard where analysisStandard.id = :id")
				.setParameter("id", idAnalysisStandard).uniqueResult();
	}

	@Override
	public AnalysisStandard getFromAnalysisIdAndStandardName(Integer idAnalysis, String name) {
		return (AnalysisStandard) getSession()
				.createQuery(
						"select analysisStandard From Analysis analysis inner join analysis.analysisStandards analysisStandard where analysis.id = :idAnalysis and analysisStandard.standard.label = :standardName")
				.setParameter("idAnalysis", idAnalysis).setParameter("standardName", name).uniqueResult();
	}

	@Override
	public Boolean hasStandard(Integer idAnalysis, String standard) {
		return (Boolean) getSession()
				.createQuery(
						"select count(analysisStandard)>0 From Analysis analysis inner join analysis.analysisStandards analysisStandard where analysis.id = :idAnalysis and analysisStandard.standard.label = :standardName")
				.setParameter("idAnalysis", idAnalysis).setParameter("standardName", standard).uniqueResult();
	}
}