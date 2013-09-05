package lu.itrust.business.dao;

import java.util.List;

import lu.itrust.business.TS.Analysis;
import lu.itrust.business.TS.AnalysisNorm;
import lu.itrust.business.TS.MeasureNorm;
import lu.itrust.business.TS.NormMeasure;

/** 
 * DAONormMeasure.java: <br>
 * Detailed description...
 *
 * @author itrust consulting s.à.rl. :
 * @version 
 * @since 16 janv. 2013
 */
public interface DAONormMeasure {
	public NormMeasure get(int id) throws Exception;
	
	public NormMeasure getFromReferenceNorm(String reference, MeasureNorm norm) throws Exception;
	
	public NormMeasure getFromReferenceNormName(String reference, String norm) throws Exception;
	
	public List<NormMeasure> loadFromNorm(AnalysisNorm norm) throws Exception;
	
	public List<NormMeasure> loadFromNorm(MeasureNorm norm) throws Exception;
	
	public List<NormMeasure> loadFromNormNameAnalysis(MeasureNorm norm, Analysis analysis) throws Exception;
	
	public List<NormMeasure> loadFromNormNameAnalysis(String norm, Analysis analysis) throws Exception;
	
	public List<NormMeasure> loadAllFromAnalysis(Analysis analysis) throws Exception;
	
	public List<NormMeasure> loadAllFromAnalysisIdentifierVersionCreationDate(int identifier, int version, String creationDate) throws Exception;
	
	public List<NormMeasure> loadAll() throws Exception;
	
	public void save(NormMeasure normMeasure) throws Exception;
	
	public void saveOrUpdate(NormMeasure normMeasure) throws Exception;
	
	public void remove(NormMeasure normMeasure)throws Exception;
}
