/**
 * 
 */
package lu.itrust.business.TS.exportation.word.impl.docx4j.formatting;

import org.docx4j.wml.Tbl;

import lu.itrust.business.TS.model.analysis.AnalysisType;

/**
 * @author eomar
 *
 */
public class Docx4jMeasureFormatter extends Docx4jFormatter {
	
	

	/**
	 * 
	 */
	public Docx4jMeasureFormatter() {
		this(null);
	}

	/**
	 * @param next
	 * @param support
	 */
	public Docx4jMeasureFormatter(Docx4jFormatter next) {
		super(next, "TableTSMeasure");

	}

	@Override
	protected boolean formatMe(Tbl table, AnalysisType type) {
		if (!isSupported(table))
			return false;
		return true;
	}

}
