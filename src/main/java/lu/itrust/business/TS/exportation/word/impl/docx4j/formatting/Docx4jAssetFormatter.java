/**
 * 
 */
package lu.itrust.business.TS.exportation.word.impl.docx4j.formatting;

import java.math.BigInteger;

import org.docx4j.jaxb.Context;
import org.docx4j.wml.STTblLayoutType;
import org.docx4j.wml.Tbl;

import lu.itrust.business.TS.exportation.word.impl.docx4j.helper.ColorSet;
import lu.itrust.business.TS.model.analysis.AnalysisType;

/**
 * @author eomar
 *
 */
public class Docx4jAssetFormatter extends Docx4jFormatter {

	/**
	 * 
	 */
	public Docx4jAssetFormatter() {
		this(null);
	}

	/**
	 * @param next
	 * @param support
	 */
	public Docx4jAssetFormatter(Docx4jFormatter next) {
		super(next, "TableTSAsset");
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
		int[] cols = (type.isQualitative() ? new int[] { 421, 1842, 993, 709, 709 + 4954 }
				: new int[] { 421, 1842, 993, 709, 709, 4954 });
		for (int i = 0; i < cols.length; i++)
			table.getTblGrid().getGridCol().get(i).setW(BigInteger.valueOf(cols[i]));

		if (table.getTblPr() == null)
			table.setTblPr(Context.getWmlObjectFactory().createTblPr());

		if (table.getTblPr().getTblLayout() == null)
			table.getTblPr().setTblLayout(Context.getWmlObjectFactory().createCTTblLayoutType());

		table.getTblPr().getTblLayout().setType(STTblLayoutType.FIXED);

		getTrs(table).forEach(tr -> updateRow(tr, cols, "dxa"));

		return true;
	}

}
