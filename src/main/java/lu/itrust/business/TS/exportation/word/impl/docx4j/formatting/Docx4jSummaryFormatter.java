/**
 * 
 */
package lu.itrust.business.TS.exportation.word.impl.docx4j.formatting;

import java.math.BigInteger;

import org.docx4j.jaxb.Context;
import org.docx4j.wml.Tbl;

import lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ColorSet;
import lu.itrust.business.TS.model.analysis.AnalysisType;

/**
 * @author eomar
 *
 */
public class Docx4jSummaryFormatter extends Docx4jFormatter {

	/**
	 * 
	 */
	public Docx4jSummaryFormatter() {
		this(null);
	}

	/**
	 * @param next
	 */
	public Docx4jSummaryFormatter(Docx4jFormatter next) {
		super(next, "TableTSSummary");
	}

	/* (non-Javadoc)
	 * @see lu.itrust.business.TS.exportation.word.impl.docx4j.formatting.Docx4jFormatter#formatMe(org.docx4j.wml.Tbl, lu.itrust.business.TS.model.analysis.AnalysisType)
	 */
	@Override
	protected boolean formatMe(Tbl table, AnalysisType type, ColorSet colors) {
		if (!isSupported(table))
			return false;
		table.getTblGrid().getGridCol().get(0).setW(BigInteger.valueOf(3558));
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
