/**
 * 
 */
package lu.itrust.business.ts.exportation.word.impl.docx4j.formatting;

import java.math.BigInteger;

import org.docx4j.jaxb.Context;
import org.docx4j.wml.Tbl;

import lu.itrust.business.ts.exportation.word.impl.docx4j.helper.ColorSet;
import lu.itrust.business.ts.model.analysis.AnalysisType;

/**
 * @author eomar
 *
 */
public class Docx4jHeatMapLegendFormatter extends Docx4jFormatter {

	/**
	 * 
	 */
	public Docx4jHeatMapLegendFormatter() {
		this(null);
	}

	/**
	 * @param next
	 * @param support
	 */
	public Docx4jHeatMapLegendFormatter(Docx4jFormatter next) {
		super(next, "TableTSHeatMapLegend");
		// TODO Auto-generated constructor stub
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
		int size = table.getTblGrid().getGridCol().size();
		for (int i = 0; i < size; i++)
			table.getTblGrid().getGridCol().get(i).setW(BigInteger.valueOf(120));
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
