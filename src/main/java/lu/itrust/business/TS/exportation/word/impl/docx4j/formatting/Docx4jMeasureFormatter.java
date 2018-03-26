/**
 * 
 */
package lu.itrust.business.TS.exportation.word.impl.docx4j.formatting;

import java.math.BigInteger;

import org.docx4j.wml.Tbl;

import lu.itrust.business.TS.model.analysis.AnalysisType;

/**
 * @author eomar
 *
 */
public class Docx4jMeasureFormatter extends Docx4jFormatter {

	/**
	 * 
	 */
	public Docx4jMeasureFormatter() {
		this(null);
	}

	/**
	 * @param next
	 * @param support
	 */
	public Docx4jMeasureFormatter(Docx4jFormatter next) {
		super(next, "TableTSMeasure");

	}

	@Override
	protected boolean formatMe(Tbl table, AnalysisType type) {
		if (!isSupported(table))
			return false;
		int[] headers = { 1017, 1975, 779, 636, 878, 910, 988, 675, 878, 878, 898, 758, 493, 779, 2254, 2254 },
				cols = { 732, 2193, 238, 338, 338, 338, 338, 238, 338, 338, 338, 338, 219, 416, 6064, 6064 }, mergeCols = { 732, sum(1, 15, cols), 6064 };
		table.getTblPr().getTblW().setType("dxa");
		table.getTblPr().getTblW().setW(BigInteger.valueOf(16157));
		for (int i = 0; i < headers.length; i++)
			table.getTblGrid().getGridCol().get(i).setW(BigInteger.valueOf(headers[i]));
		getTrs(table).forEach(tr -> updateRow(tr, tr.getContent().size() == mergeCols.length ? mergeCols : cols, "dxa"));
		return true;
	}

	public static int sum(int i, int j, int[] cols) {
		int value = 0;
		for (; i < j; i++)
			value += cols[i];
		return value;
	}

	

}
