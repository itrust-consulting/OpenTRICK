/**
 * 
 */
package lu.itrust.business.TS.exportation.word;

import lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ColorSet;
import lu.itrust.business.TS.model.analysis.AnalysisType;

/**
 * @author eomar
 *
 */
public interface DocxFormatter {
	
	/**
	 * get next formatter
	 * @return next
	 */
	DocxFormatter getNext();
	
	/**
	 * Look for class can format data
	 * @param data
	 * @param style 
	 * @param type analysis type
	 * @param colors TODO
	 * @return true or false
	 */
	boolean format(Object data, Object style, AnalysisType type, ColorSet colors);
}
