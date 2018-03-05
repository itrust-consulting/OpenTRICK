/**
 * 
 */
package lu.itrust.business.TS.exportation.word.impl.docx4j.formatting;

import java.math.BigInteger;
import java.util.Arrays;

import org.docx4j.jaxb.Context;
import org.docx4j.wml.Tbl;

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
		super(next, Arrays.asList("TableTSProba","TableTSImpact"));
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.exportation.word.impl.docx4j.formatting.Docx4jFormatter#formatMe(org.docx4j.wml.Tbl)
	 */
	@Override
	protected boolean formatMe(Tbl table, AnalysisType type) {
		if (!isSupported(table))
			return false;
		int[] cols = (type.isQualitative() ? new int[] {626, 812, 6737} : new int[] {493, 784, 6737, 746, 488, 380});
		for (int i = 0; i < cols.length; i++)
			table.getTblGrid().getGridCol().get(i).setW(BigInteger.valueOf(cols[i]));
		getTcs(table).forEach(tc -> {
			if (tc.getTcPr() == null)
				tc.setTcPr(Context.getWmlObjectFactory().createTcPr());
			if (tc.getTcPr().getTcW() == null)
				tc.getTcPr().setTcW(Context.getWmlObjectFactory().createTblWidth());
			tc.getTcPr().getTcW().setType("auto");
			tc.getTcPr().getTcW().setW(BigInteger.valueOf(0));
		});
		return true;
	}

}
