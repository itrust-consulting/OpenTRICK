/**
 * 
 */
package lu.itrust.business.TS.exportation.word.impl.docx4j.formatting;

import java.math.BigInteger;
import java.util.Arrays;

import org.docx4j.wml.Tbl;

import lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ColorSet;
import lu.itrust.business.TS.model.analysis.AnalysisType;

/**
 * @author eomar
 *
 */
public class Docx4jImpactProbaFormatter extends Docx4jFormatter {

	/**
	 * 
	 */
	public Docx4jImpactProbaFormatter() {
		this(null);
	}

	/**
	 * @param next
	 * @param support
	 */
	public Docx4jImpactProbaFormatter(Docx4jFormatter next) {
		super(next, Arrays.asList("TableTSProba", "TableTSImpact"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.exportation.word.impl.docx4j.formatting.Docx4jFormatter
	 * #formatMe(org.docx4j.wml.Tbl)
	 */
	@Override
	protected boolean formatMe(Tbl table, AnalysisType type, ColorSet colors) {
		if (!isSupported(table))
			return false;
		table.getTblPr().getTblW().setType("auto");
		table.getTblPr().getTblW().setW(BigInteger.valueOf(0));
		int[] cols = (table.getTblGrid().getGridCol().size() == 3 ? new int[] { 626, 812, 6737 } : new int[] { 493, 784, 6737, 746, 488, 380 });
		for (int i = 0; i < cols.length; i++)
			table.getTblGrid().getGridCol().get(i).setW(BigInteger.valueOf(cols[i]));
		getTrs(table).forEach(tr -> updateRow(tr, null, "auto"));
		return true;
	}

}
