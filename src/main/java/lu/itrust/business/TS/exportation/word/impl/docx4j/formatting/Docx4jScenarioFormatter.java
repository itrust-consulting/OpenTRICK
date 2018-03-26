/**
 * 
 */
package lu.itrust.business.TS.exportation.word.impl.docx4j.formatting;

import java.math.BigInteger;

import org.docx4j.wml.Tbl;

import lu.itrust.business.TS.model.analysis.AnalysisType;

/**
 * @author eomar
 *
 */
public class Docx4jScenarioFormatter extends Docx4jFormatter {

	/**
	 * 
	 */
	public Docx4jScenarioFormatter() {
		this(null);
	}

	/**
	 * @param next
	 * @param support
	 */
	public Docx4jScenarioFormatter(Docx4jFormatter next) {
		super(next, "TableTSScenario");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.exportation.word.impl.docx4j.formatting.
	 * Docx4jFormatter#formatMe(org.docx4j.wml.Tbl)
	 */
	@Override
	protected boolean formatMe(Tbl table, AnalysisType type) {
		if (!isSupported(table))
			return false;
		int[] cols = { 272, 3216, 6140 };
		for (int i = 0; i < cols.length; i++)
			table.getTblGrid().getGridCol().get(i).setW(BigInteger.valueOf(cols[i]));
		getTrs(table).forEach(tr -> updateRow(tr, cols, "dxa"));
		return true;
	}

}
