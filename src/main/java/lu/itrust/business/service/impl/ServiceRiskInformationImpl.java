/**
 * 
 */
package lu.itrust.business.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.RiskInformation;
import lu.itrust.business.dao.DAORiskInformation;
import lu.itrust.business.service.ServiceRiskInformation;

/**
 * @author eomar
 *
 */
@Service
public class ServiceRiskInformationImpl implements ServiceRiskInformation {

	@Autowired
	private DAORiskInformation daoRiskInformation;
	
	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceRiskInformation#get(int)
	 */
	@Override
	public RiskInformation get(int id) throws Exception {
		return daoRiskInformation.get(id);
	}


	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceRiskInformation#loadFromChapter(java.lang.String)
	 */
	@Override
	public List<RiskInformation> loadFromChapter(String chapter) throws Exception {
		return daoRiskInformation.loadFromChapter(chapter);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceRiskInformation#loadFromCategory(java.lang.String)
	 */
	@Override
	public List<RiskInformation> loadFromCategory(String category) throws Exception {
		return daoRiskInformation.loadFromCategory(category);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceRiskInformation#loadAllFromAnalysis(lu.itrust.business.TS.Analysis)
	 */
	@Override
	public List<RiskInformation> loadAllFromAnalysis(Analysis analysis) throws Exception {
		return daoRiskInformation.loadAllFromAnalysis(analysis);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceRiskInformation#loadAllFromAnalysisID(int)
	 */
	@Override
	public List<RiskInformation> loadAllFromAnalysisID(int analysisID) throws Exception {
		return daoRiskInformation.loadAllFromAnalysisID(analysisID);
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceRiskInformation#loadAll()
	 */
	@Override
	public List<RiskInformation> loadAll() throws Exception {
		return daoRiskInformation.loadAll();
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceRiskInformation#save(lu.itrust.business.TS.RiskInformation)
	 */
	@Transactional
	@Override
	public void save(RiskInformation riskInformation) throws Exception {
		daoRiskInformation.save(riskInformation);

	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceRiskInformation#saveOrUpdate(lu.itrust.business.TS.RiskInformation)
	 */
	@Transactional
	@Override
	public void saveOrUpdate(RiskInformation riskInformation) throws Exception {
		daoRiskInformation.saveOrUpdate(riskInformation);

	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.service.ServiceRiskInformation#remove(lu.itrust.business.TS.RiskInformation)
	 */
	@Transactional
	@Override
	public void remove(RiskInformation riskInformation) throws Exception {
		daoRiskInformation.remove(riskInformation);
	}

	@Override
	public RiskInformation findByIdAndAnalysis(int id, int idAnalysis) throws Exception {
		return daoRiskInformation.findbyIdAndAnalysis(id, idAnalysis);
	}

}
