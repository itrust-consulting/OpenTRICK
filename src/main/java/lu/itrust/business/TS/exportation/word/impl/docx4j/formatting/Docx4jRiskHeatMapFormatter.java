/**
 * 
 */
package lu.itrust.business.TS.exportation.word.impl.docx4j.formatting;

import java.math.BigInteger;

import org.docx4j.XmlUtils;
import org.docx4j.jaxb.Context;
import org.docx4j.wml.CTVerticalJc;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.P;
import org.docx4j.wml.STBorder;
import org.docx4j.wml.STVerticalJc;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;

import lu.itrust.business.TS.exportation.word.impl.docx4j.Docx4jWordExporter;
import lu.itrust.business.TS.model.analysis.AnalysisType;

/**
 * @author eomar
 *
 */
public class Docx4jRiskHeatMapFormatter extends Docx4jFormatter {

	/**
	 * 
	 */
	public Docx4jRiskHeatMapFormatter() {
		this(null);
	}

	/**
	 * @param next
	 * @param support
	 */
	public Docx4jRiskHeatMapFormatter(Docx4jFormatter next) {
		super(next, "TableTSRiskHeatMap");
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
		table.getTblPr().getTblW().setType("pct");
		table.getTblPr().getTblW().setW(BigInteger.valueOf(5000));
		int size = table.getContent().size();
		for (int i = 0; i < size; i++)
			table.getTblGrid().getGridCol().get(i).setW(BigInteger.valueOf(120));

		getTcs(table).forEach(tc -> {
			if (tc.getTcPr() == null)
				tc.setTcPr(Context.getWmlObjectFactory().createTcPr());
			if (tc.getTcPr().getTcW() == null)
				tc.getTcPr().setTcW(Context.getWmlObjectFactory().createTblWidth());
			tc.getTcPr().getTcW().setType("auto");
			tc.getTcPr().getTcW().setW(BigInteger.valueOf(0));
			setBorder(tc, STBorder.SINGLE, STBorder.SINGLE, STBorder.SINGLE, STBorder.SINGLE, "ffffff");
		});

		CTVerticalJc ctVerticalJc = Context.getWmlObjectFactory().createCTVerticalJc();
		ctVerticalJc.setVal(STVerticalJc.CENTER);
		Tr lastRow = (Tr) table.getContent().get(size - 1);
		Tr firstRow = (Tr) table.getContent().get(0);

		table.getContent().parallelStream().map(t -> XmlUtils.unwrap(t)).filter(t -> t instanceof Tr).map(tr -> (Tc) ((Tr) tr).getContent().get(0)).forEach(tc -> {
			tc.getTcPr().getTcBorders().getBottom().setColor(Docx4jWordExporter.HEADER_COLOR);
			tc.getTcPr().getTcBorders().getRight().setColor(Docx4jWordExporter.HEADER_COLOR);
			tc.getTcPr().getTcW().setType("pct");
			tc.getTcPr().getTcW().setW(BigInteger.valueOf(0));
		});

		Docx4jWordExporter.MergeCell(lastRow, 0, lastRow.getContent().size(), Docx4jWordExporter.HEADER_COLOR);
		Docx4jWordExporter.VerticalMergeCell(table.getContent(), 0, 0, size - 1, Docx4jWordExporter.HEADER_COLOR);
		Tc column = (Tc) firstRow.getContent().get(0);
		if (column.getTcPr().getTextDirection() == null)
			column.getTcPr().setTextDirection(Context.getWmlObjectFactory().createTextDirection());
		column.getTcPr().getTextDirection().setVal("btLr");
		column.getTcPr().setVAlign(ctVerticalJc);
		column.getContent().parallelStream().map(p -> (P) p).forEach(p -> {
			if (p.getPPr() == null)
				p.setPPr(Context.getWmlObjectFactory().createPPr());
			if (p.getPPr().getJc() == null)
				p.getPPr().setJc(Context.getWmlObjectFactory().createJc());
			p.getPPr().getJc().setVal(JcEnumeration.CENTER);
		});

		column = (Tc) lastRow.getContent().get(0);

		column.getTcPr().getTcBorders().getTop().setColor(Docx4jWordExporter.HEADER_COLOR);

		column = (Tc) ((Tr) table.getContent().get(size - 2)).getContent().get(1);

		column.getTcPr().getTcBorders().getBottom().setColor(Docx4jWordExporter.LIGHT_CELL_COLOR);
		column.getTcPr().getTcBorders().getLeft().setColor(Docx4jWordExporter.LIGHT_CELL_COLOR);

		Docx4jWordExporter.setColor(column, Docx4jWordExporter.LIGHT_CELL_COLOR);

		return true;
	}

	private void setBorder(Tc tc, STBorder top, STBorder bottom, STBorder left, STBorder right, String color) {
		if (tc.getTcPr().getTcBorders() == null)
			tc.getTcPr().setTcBorders(Context.getWmlObjectFactory().createTcPrInnerTcBorders());
		if (top == null)
			tc.getTcPr().getTcBorders().setTop(null);
		else {
			if (tc.getTcPr().getTcBorders().getTop() == null)
				tc.getTcPr().getTcBorders().setTop(Context.getWmlObjectFactory().createCTBorder());
			tc.getTcPr().getTcBorders().getTop().setVal(top);
			tc.getTcPr().getTcBorders().getTop().setColor(color);
		}

		if (bottom == null)
			tc.getTcPr().getTcBorders().setBottom(null);
		else {
			if (tc.getTcPr().getTcBorders().getBottom() == null)
				tc.getTcPr().getTcBorders().setBottom(Context.getWmlObjectFactory().createCTBorder());
			tc.getTcPr().getTcBorders().getBottom().setVal(top);
			tc.getTcPr().getTcBorders().getBottom().setColor(color);
		}

		if (left == null)
			tc.getTcPr().getTcBorders().setLeft(null);
		else {
			if (tc.getTcPr().getTcBorders().getLeft() == null)
				tc.getTcPr().getTcBorders().setLeft(Context.getWmlObjectFactory().createCTBorder());
			tc.getTcPr().getTcBorders().getLeft().setVal(top);
			tc.getTcPr().getTcBorders().getLeft().setColor(color);
		}

		if (right == null)
			tc.getTcPr().getTcBorders().setRight(null);
		else {
			if (tc.getTcPr().getTcBorders().getRight() == null)
				tc.getTcPr().getTcBorders().setRight(Context.getWmlObjectFactory().createCTBorder());
			tc.getTcPr().getTcBorders().getRight().setVal(top);
			tc.getTcPr().getTcBorders().getRight().setColor(color);
		}

	}

}
