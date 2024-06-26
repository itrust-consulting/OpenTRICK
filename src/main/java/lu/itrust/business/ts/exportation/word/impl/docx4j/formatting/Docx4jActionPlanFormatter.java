/**
 * 
 */
package lu.itrust.business.ts.exportation.word.impl.docx4j.formatting;

import java.math.BigInteger;

import org.docx4j.wml.Tbl;

import lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ColorSet;
import lu.itrust.business.ts.model.analysis.AnalysisType;

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
	 * @see lu.itrust.business.ts.exportation.word.impl.docx4j.formatting.
	 * Docx4jFormatter#formatMe(org.docx4j.wml.Tbl)
	 */
	@Override
	protected boolean formatMe(Tbl table, AnalysisType type, ColorSet colors) {
		if (!isSupported(table))
			return false;
		int[] cols = (type.isQualitative()
				? new int[] { 187, 734, 4595, 187, 500, 500, 500, 500, 187, 187, 508 }
				: new int[] { 187, 734, 4595, 500, 653, 480, 500, 500, 500, 500, 187, 187, 508 });
		for (int i = 0; i < cols.length; i++)
			table.getTblGrid().getGridCol().get(i).setW(BigInteger.valueOf(cols[i]));
		getTrs(table).forEach(tr -> updateRow(tr, cols, "dxa"));
		return true;
	}

}
