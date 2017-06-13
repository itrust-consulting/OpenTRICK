/**
 * 
 */
package lu.itrust.business.TS.exportation.word.impl.docx4j.formatting;

import java.math.BigInteger;

import org.docx4j.jaxb.Context;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;

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
		int[] cols = (type.isQualitative() ? new int[] { 3947, 370, 419, 455, 432, 2851 } : new int[] { 1947, 370, 267, 876, 601, 2851 });
		table.getTblPr().getTblW().setType("auto");
		table.getTblPr().getTblW().setW(BigInteger.valueOf(0));
		for (int i = 0; i < cols.length; i++)
			table.getTblGrid().getGridCol().get(i).setW(BigInteger.valueOf(cols[i]));
		table.getContent().parallelStream().map(tr -> (Tr) tr).flatMap(tr -> tr.getContent().parallelStream()).map(tc -> (Tc) tc).forEach(tc -> {
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
