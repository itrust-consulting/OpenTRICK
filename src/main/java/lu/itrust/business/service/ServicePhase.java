package lu.itrust.business.service;

import java.sql.Date;
import java.util.List;

import lu.itrust.business.TS.Phase;

public interface ServicePhase {

	public Phase get(int id) throws Exception;

	public Phase loadFromPhaseNumberAnalysis(int number, int idAnalysis)
			throws Exception;

	public List<Phase> loadByBeginDate(Date beginDate, int idAnalysis)
			throws Exception;

	public List<Phase> loadByEndDate(Date beginDate, int idAnalysis)
			throws Exception;
	
	public boolean canBeDeleted(int idPhase);

	public List<Phase> loadAllFromAnalysis(int idAnalysis) throws Exception;
	
	public Phase loadByIdAndIdAnalysis(int idPhase, Integer idAnalysis);

	public List<Phase> loadAll() throws Exception;

	public void save(Phase phase) throws Exception;

	public void saveOrUpdate(Phase phase) throws Exception;

	public void remove(Phase phase) throws Exception;

	

	

}
