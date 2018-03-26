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
public class Docx4jActionPlanFormatter extends Docx4jFormatter {

	/**
	 * 
	 */
	public Docx4jActionPlanFormatter() {
		this(null);
	}

	/**
	 * @param next
	 * @param support
	 */
	public Docx4jActionPlanFormatter(Docx4jFormatter next) {
		super(next, "TableTSActionPlan");
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
		int[] cols = (type.isQualitative() ? new int[] { 187, 534, 734, 4061, 187, 500, 500, 500, 500, 187, 508 }
				: new int[] { 187, 534, 734, 4061, 500, 653, 480, 500, 500, 500, 500, 187, 508 });
		for (int i = 0; i < cols.length; i++)
			table.getTblGrid().getGridCol().get(i).setW(BigInteger.valueOf(cols[i]));
		getTrs(table).forEach(tr -> updateRow(tr, cols, "dxa"));
		return true;
	}

}
