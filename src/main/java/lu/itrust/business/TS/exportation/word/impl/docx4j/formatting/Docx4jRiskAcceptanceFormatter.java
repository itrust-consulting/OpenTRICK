/**
 * 
 */
package lu.itrust.business.TS.exportation.word.impl.docx4j.formatting;

import java.math.BigInteger;

import org.docx4j.jaxb.Context;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;

import lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ColorSet;
import lu.itrust.business.TS.model.analysis.AnalysisType;

/**
 * @author eomar
 *
 */
public class Docx4jRiskAcceptanceFormatter extends Docx4jFormatter {

	/**
	 * 
	 */
	public Docx4jRiskAcceptanceFormatter() {
		this(null);
	}

	/**
	 * @param next
	 * @param support
	 */
	public Docx4jRiskAcceptanceFormatter(Docx4jFormatter next) {
		super(next, "TableTSRiskAcceptance");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.exportation.word.impl.docx4j.formatting.
	 * Docx4jFormatter#formatMe(org.docx4j.wml.Tbl,
	 * lu.itrust.business.TS.model.analysis.AnalysisType)
	 */
	@Override
	protected boolean formatMe(Tbl table, AnalysisType type, ColorSet colors) {
		if (!isSupported(table))
			return false;
		table.getTblPr().getTblW().setType("pct");
		table.getTblPr().getTblW().setW(BigInteger.valueOf(5000));
		int[] cols = { 2122, 7506 }, colCells = { 1102, 3898 };
		for (int i = 0; i < cols.length; i++)
			table.getTblGrid().getGridCol().get(i).setW(BigInteger.valueOf(cols[i]));
		getTrs(table).forEach(tr -> {
			for (int i = 0; i < colCells.length; i++) {
				Tc tc = (Tc) tr.getContent().get(i);
				if (tc.getTcPr() == null)
					tc.setTcPr(Context.getWmlObjectFactory().createTcPr());
				if (tc.getTcPr().getTcW() == null)
					tc.getTcPr().setTcW(Context.getWmlObjectFactory().createTblWidth());
				tc.getTcPr().getTcW().setType("pct");
				tc.getTcPr().getTcW().setW(BigInteger.valueOf(colCells[i]));
			}

		});
		return true;
	}

}
