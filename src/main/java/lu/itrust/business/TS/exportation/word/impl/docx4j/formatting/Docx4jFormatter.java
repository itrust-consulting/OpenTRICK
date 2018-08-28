/**
 * 
 */
package lu.itrust.business.TS.exportation.word.impl.docx4j.formatting;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Stream;

import org.docx4j.XmlUtils;
import org.docx4j.jaxb.Context;
import org.docx4j.wml.Style;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;

import lu.itrust.business.TS.exportation.word.DocxFormatter;
import lu.itrust.business.TS.model.analysis.AnalysisType;

/**
 * @author eomar
 *
 */
public abstract class Docx4jFormatter implements DocxFormatter {

	private Docx4jFormatter next;

	private Object support;

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.exportation.word.DocxFormatter#getNext()
	 */
	@Override
	public DocxFormatter getNext() {
		return next;
	}

	/**
	 * 
	 */
	public Docx4jFormatter() {
		this(null, null);
	}

	/**
	 * @param next
	 * @param support TODO
	 */
	public Docx4jFormatter(Docx4jFormatter next, Object support) {
		this.next = next;
		if (support != null)
			setSupport(support);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see lu.itrust.business.TS.exportation.word.DocxFormatter#format(java.lang.
	 * Object)
	 */
	@Override
	public boolean format(Object table, Object style, AnalysisType type) {
		if (table instanceof Tbl) {
			if (formatMe((Tbl) table, type)) {
				postTreatment(table, style);
				return true;
			} else if (getNext() != null)
				return getNext().format(table, style, type);
		}
		return false;
	}

	/**
	 * Specifically formating.
	 * 
	 * @param table
	 * @param type  TODO
	 * @return true or false.
	 */
	protected abstract boolean formatMe(Tbl table, AnalysisType type);

	protected String findId(Tbl tbl) {
		return tbl.getTblPr().getTblStyle().getVal();
	}

	/**
	 * @return the supported
	 */
	protected Object getSupport() {
		return support;
	}

	/**
	 * @param supported the supported to set
	 */
	protected void setSupport(Object support) {
		this.support = support;
	}

	protected boolean isSupported(Tbl table) {
		if (support == null)
			return false;
		if (support instanceof String)
			return support.equals(findId(table));
		if (support instanceof List)
			return ((List<?>) support).contains(findId(table));
		return false;
	}

	protected Stream<Tc> getTcs(Tbl table) {
		return getTrs(table).flatMap(tr -> tr.getContent().parallelStream()).map(tc -> (Tc) tc);
	}

	protected Stream<Tr> getTrs(Tbl table) {
		return table.getContent().parallelStream().map(c -> XmlUtils.unwrap(c)).filter(c -> c instanceof Tr).map(tr -> (Tr) tr);
	}

	public static void updateRow(Tr tr, int[] cols, String type) {
		if (cols == null) {
			tr.getContent().parallelStream().map(tc -> (Tc) tc).forEach(tc -> {
				if (tc.getTcPr() == null)
					tc.setTcPr(Context.getWmlObjectFactory().createTcPr());
				if (tc.getTcPr().getTcW() == null)
					tc.getTcPr().setTcW(Context.getWmlObjectFactory().createTblWidth());
				tc.getTcPr().getTcW().setType(type);
				tc.getTcPr().getTcW().setW(BigInteger.valueOf(0));
			});
		} else {
			for (int i = 0; i < cols.length; i++) {
				Tc tc = (Tc) tr.getContent().get(i);
				if (tc.getTcPr() == null)
					tc.setTcPr(Context.getWmlObjectFactory().createTcPr());
				if (tc.getTcPr().getTcW() == null)
					tc.getTcPr().setTcW(Context.getWmlObjectFactory().createTblWidth());
				tc.getTcPr().getTcW().setType(type);
				tc.getTcPr().getTcW().setW(BigInteger.valueOf(cols[i]));
			}
		}
	}

	protected void postTreatment(Object data, Object style) {
		if (style != null && data instanceof Tbl) {
			if (style instanceof String)
				((Tbl) data).getTblPr().getTblStyle().setVal(style.toString());
			else if (style instanceof Style)
				((Tbl) data).getTblPr().getTblStyle().setVal(((Style) style).getName().getVal());
		}
	}

}
