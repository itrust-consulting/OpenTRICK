/**
 * 
 */
package lu.itrust.business.TS.exportation.word.impl.docx4j.formatting;

import java.math.BigInteger;

import org.docx4j.jaxb.Context;
import org.docx4j.wml.Tbl;

import lu.itrust.business.TS.model.analysis.AnalysisType;

/**
 * @author eomar
 *
 */
public class Docx4jScopeFormatter extends Docx4jFormatter {

	/**
	 * 
	 */
	public Docx4jScopeFormatter() {
		this(null);
	}

	/**
	 * @param next
	 */
	public Docx4jScopeFormatter(Docx4jFormatter next) {
		super(next, "TableTSScope");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.exportation.word.impl.docx4j.formatting.
	 * Docx4jFormatter#formatMe(org.docx4j.wml.Tbl,
	 * lu.itrust.business.TS.model.analysis.AnalysisType)
	 */
	@Override
	protected boolean formatMe(Tbl table, AnalysisType type) {
		if (!isSupported(table))
			return false;
		int[] col = { 2607, 7021 };
		for (int i = 0; i < col.length; i++)
			table.getTblGrid().getGridCol().get(i).setW(BigInteger.valueOf(col[i]));
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
