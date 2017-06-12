/**
 * 
 */
package lu.itrust.business.TS.exportation.word.impl.docx4j.formatting;

import java.util.List;

import org.docx4j.wml.Tbl;

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
	 * @param support
	 *            TODO
	 */
	public Docx4jFormatter(Docx4jFormatter next, Object support) {
		this.next = next;
		if (support != null)
			setSupport(support);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.exportation.word.DocxFormatter#format(java.lang.
	 * Object)
	 */
	@Override
	public boolean format(Object table, AnalysisType type) {
		if (table instanceof Tbl) {
			if (formatMe((Tbl) table, type))
				return true;
			if (getNext() != null)
				return getNext().format(table, type);
		}
		return false;
	}

	/**
	 * Specifically formating.
	 * 
	 * @param table
	 * @param type
	 *            TODO
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
	 * @param supported
	 *            the supported to set
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

}
