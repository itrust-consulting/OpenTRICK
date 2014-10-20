package lu.itrust.business.dao.hbm;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.AnalysisStandard;
import lu.itrust.business.TS.MeasureDescription;
import lu.itrust.business.TS.MeasureDescriptionText;
import lu.itrust.business.TS.Standard;
import lu.itrust.business.dao.DAOAnalysisStandard;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

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
	 * @see lu.itrust.business.dao.DAOAnalysisStandard#get(int)
	 */
	@Override
	public AnalysisStandard get(Integer id) throws Exception {
		return (AnalysisStandard) getSession().get(AnalysisStandard.class, id);
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysisStandard#getAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AnalysisStandard> getAll() throws Exception {
		return (List<AnalysisStandard>) getSession().createQuery("From AnalysisStandard").list();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysisStandard#getAllFromAnalysis(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AnalysisStandard> getAllFromAnalysis(Integer analysisID) throws Exception {
		return (List<AnalysisStandard>) getSession().createQuery("From AnalysisStandard where analysis.id = :analysis ORDER BY standard.label ASC").setParameter("analysis", analysisID).list();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysisStandard#getAllFromAnalysis(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AnalysisStandard> getAllComputableFromAnalysis(Integer analysisID) throws Exception {
		return (List<AnalysisStandard>) getSession().createQuery("From AnalysisStandard where analysis.id = :analysis and standard.computable = true ORDER BY standard.label ASC").setParameter(
				"analysis", analysisID).list();
	}

	/**
	 * getAllFromAnalysis: <br>
	 * Description
	 * 
	 * @see lu.itrust.business.dao.DAOAnalysisStandard#getAllFromAnalysis(lu.itrust.business.TS.Analysis)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AnalysisStandard> getAllFromAnalysis(Analysis analysis) throws Exception {
		return (List<AnalysisStandard>) getSession().createQuery("SELECT analysisStandard From Analysis analysis join analysis.analysisStandards analysisStandard where analysis = :analysis")
				.setParameter("analysis", analysis).list();
	}

	/**
	 * getAllFromStandard: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.dao.DAOAnalysisStandard#getAllFromStandard(lu.itrust.business.TS.Standard)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AnalysisStandard> getAllFromStandard(Standard standard) throws Exception {
		return (List<AnalysisStandard>) getSession().createQuery("From AnalysisStandard where standard = :standard").setParameter("standard", standard).list();
	}

	/**
	 * save: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.dao.DAOAnalysisStandard#save(lu.itrust.business.TS.AnalysisStandard)
	 */
	@Override
	public void save(AnalysisStandard analysisStandard) throws Exception {
		getSession().save(analysisStandard);
	}

	/**
	 * saveOrUpdate: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.dao.DAOAnalysisStandard#saveOrUpdate(lu.itrust.business.TS.AnalysisStandard)
	 */
	@Override
	public void saveOrUpdate(AnalysisStandard analysisStandard) throws Exception {
		getSession().saveOrUpdate(analysisStandard);
	}

	/**
	 * delete: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.dao.DAOAnalysisStandard#delete(lu.itrust.business.TS.AnalysisStandard)
	 */
	@Override
	public void delete(AnalysisStandard analysisStandard) throws Exception {
		getSession().delete(analysisStandard);
	}

	/**
	 * getFromAnalysisIdAndStandardId: <br>
	 * Description
	 *
	 * @{tags
	 *
	 * @see lu.itrust.business.dao.DAOAnalysisStandard#getFromAnalysisIdAndStandardId(java.lang.Integer,
	 *      int)
	 */
	@Override
	public AnalysisStandard getFromAnalysisIdAndStandardId(Integer idAnalysis, int idStandard) {
		return (AnalysisStandard) getSession().createQuery(
				"select analysisStandard From Analysis analysis join analysis.analysisStandards analysisStandard where analysis.id = :idAnalysis and analysisStandard.standard.id = :idStandard")
				.setParameter("idAnalysis", idAnalysis).setParameter("idStandard", idStandard).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deleteAllFromAnalysis(Integer analysisId) throws Exception {
		List<AnalysisStandard> standards =
			getSession().createQuery("select analysisStandard From Analysis analysis join analysis.analysisStandards analysisStandard where analysis.id = :analysis").setParameter("analysis",
					analysisId).list();

		for (AnalysisStandard standard : standards) {
			if (standard.getStandard().getAnalysis() == null) {
				getSession().delete(standard);
			} else {
				Standard tmpstandard = standard.getStandard();

				getSession().delete(standard);

				List<MeasureDescription> mesDescs =
					(List<MeasureDescription>) getSession().createQuery("SELECT mesDesc from MeasureDescription mesDesc where mesDesc.standard= :standard").setParameter("standard", tmpstandard).list();

				for (MeasureDescription mesDesc : mesDescs) {
					for (MeasureDescriptionText mesDescText : mesDesc.getMeasureDescriptionTexts())
						getSession().delete(mesDescText);
					getSession().delete(mesDesc);
				}

				getSession().delete(tmpstandard);
			}
		}

	}

	@Override
	public Integer getAnalysisIDFromAnalysisStandard(Integer analysisStandard) throws Exception {
		return (Integer) getSession().createQuery("SELECT analysis.id From Analysis analysis join analysis.analysisStandards analysisStandard where analysisStandard.id = :analysisstandard")
				.setParameter("analysisstandard", analysisStandard).uniqueResult();
	}
}