package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.AnalysisNorm;
import lu.itrust.business.TS.MaturityMeasure;
import lu.itrust.business.TS.MaturityNorm;
import lu.itrust.business.TS.MeasureNorm;
import lu.itrust.business.TS.NormMeasure;

/** 
 * DAOMaturityMeasure.java: <br>
 * Detailed description...
 *
 * @author itrust consulting s.à.rl. :
 * @version 
 * @since 16 janv. 2013
 */
public interface DAOMaturityMeasure {

	public MaturityMeasure get(int id) throws Exception;
	
	public MaturityMeasure getFromReferenceNorm(String reference, MaturityNorm norm) throws Exception;
	
	public MaturityMeasure getFromReferenceNormName(String reference, String norm) throws Exception;
	
	public List<NormMeasure> loadFromNormNameAnalysis(MeasureNorm norm, Analysis analysis) throws Exception;
	
	public List<NormMeasure> loadFromNorm(MaturityNorm norm) throws Exception;
	
	public List<MaturityMeasure> loadFromNorm(AnalysisNorm norm) throws Exception;
	
	public List<MaturityMeasure> loadFromNormNameAnalysis(String norm, Analysis analysis) throws Exception;
	
	public List<MaturityMeasure> loadAllFromAnalysis(Analysis analysis) throws Exception;
	
	public List<MaturityMeasure> loadAllFromAnalysisIdentifierVersionCreationDate(int identifier, int version, String creationDate) throws Exception;
	
	public List<MaturityMeasure> loadAll() throws Exception;
	
	public void save(MaturityMeasure maturityMeasure) throws Exception;
	
	public void saveOrUpdate(MaturityMeasure maturityMeasure) throws Exception;
	
	public void remove(MaturityMeasure maturityMeasure)throws Exception;
	
}
