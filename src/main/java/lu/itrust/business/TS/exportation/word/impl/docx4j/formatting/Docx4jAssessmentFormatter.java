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
public class Docx4jAssessmentFormatter extends Docx4jFormatter {

	/**
	 * 
	 */
	public Docx4jAssessmentFormatter() {
		this(null);
	}

	/**
	 * @param next
	 * @param support
	 */
	public Docx4jAssessmentFormatter(Docx4jFormatter next) {
		super(next, "TableTSAssessment");
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
		int[] cols = (type.isQualitative() ? createWith(table.getTblGrid().getGridCol().size()) : new int[] { 2500, 370, 267, 370, 400, 5669 });
		for (int i = 0; i < cols.length; i++)
			table.getTblGrid().getGridCol().get(i).setW(BigInteger.valueOf(cols[i]));
		getTrs(table).forEach(tr -> updateRow(tr, cols, "dxa"));
		return true;
	}

	private int[] createWith(int size) {
		int cols [] = new int [size];
		cols[0] = 1947;
		cols[size-3] = 370;
		cols[size-2] = 400;
		cols[size-1] = 4851;
		for (int i = 1; i < (cols.length-3); i++)
			cols[i] = 300;
		return cols;
	}

}
