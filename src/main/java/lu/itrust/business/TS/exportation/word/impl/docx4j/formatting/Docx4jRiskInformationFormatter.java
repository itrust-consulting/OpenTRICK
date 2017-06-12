/**
 * 
 */
package lu.itrust.business.TS.exportation.word.impl.docx4j.formatting;

import java.util.Arrays;

import org.docx4j.wml.Tbl;

import lu.itrust.business.TS.model.analysis.AnalysisType;

/**
 * @author eomar
 *
 */
public class Docx4jRiskInformationFormatter extends Docx4jFormatter {
	/**
	 * 
	 */
	public Docx4jRiskInformationFormatter() {
		this(null);
	}

	/**
	 * @param next
	 * @param support
	 */
	public Docx4jRiskInformationFormatter(Docx4jFormatter next) {
		super(next, Arrays.asList("TableTSThreat","TableTSRisk","TableTSVul"));
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.exportation.word.impl.docx4j.formatting.Docx4jFormatter#formatMe(org.docx4j.wml.Tbl, lu.itrust.business.TS.model.analysis.AnalysisType)
	 */
	@Override
	protected boolean formatMe(Tbl table, AnalysisType type) {
		if (!isSupported(table))
			return false;
		return true;
	}

}
