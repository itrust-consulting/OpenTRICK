/**
 * 
 */
package lu.itrust.business.ts.exportation.word.impl.docx4j.formatting;

import java.math.BigInteger;
import java.util.Arrays;

import org.docx4j.wml.Tbl;

import lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ColorSet;
import lu.itrust.business.ts.model.analysis.AnalysisType;

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
		super(next, Arrays.asList("TableTSThreat", "TableTSRisk", "TableTSVul"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.ts.exportation.word.impl.docx4j.formatting.
	 * Docx4jFormatter#formatMe(org.docx4j.wml.Tbl,
	 * lu.itrust.business.ts.model.analysis.AnalysisType)
	 */
	@Override
	protected boolean formatMe(Tbl table, AnalysisType type, ColorSet colors) {
		if (!isSupported(table))
			return false;
		final String tableName = findId(table);
		final int[] cols = tableName.equals("TableTSThreat") ? new int[] { 467, 1835, 542, 526, 501, 5757 }
				: tableName.equals("TableTSRisk") ? new int[] { 467, 1835, 542, 526, 6258 }
						: new int[] { 467, 4046, 542, 526, 4047 };
		for (int i = 0; i < cols.length; i++)
			table.getTblGrid().getGridCol().get(i).setW(BigInteger.valueOf(cols[i]));
		getTrs(table).forEach(tr -> updateRow(tr, cols, "dxa"));
		return true;
	}

}
