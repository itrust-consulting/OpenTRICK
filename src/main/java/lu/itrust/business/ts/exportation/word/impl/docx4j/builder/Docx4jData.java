/**
 * 
 */
package lu.itrust.business.ts.exportation.word.impl.docx4j.builder;

import org.docx4j.wml.CTBookmark;

import lu.itrust.business.ts.exportation.word.Docx4jReport;
import lu.itrust.business.ts.exportation.word.IBuildData;

/**
 * @author eomar
 *
 */
public class Docx4jData implements IBuildData {
	
	private String anchor;
	
	private CTBookmark source;
	
	private Docx4jReport exportor;

	/**
	 * 
	 */
	public Docx4jData() {
	}

	public Docx4jData(String anchor, CTBookmark source, Docx4jReport exportor) {
		this.anchor = anchor;
		this.source = source;
		this.exportor = exportor;
	}

	@Override
	public Docx4jReport getExportor() {
		return exportor;
	}

	@Override
	public String getAnchor() {
		return anchor;
	}

	@Override
	public CTBookmark getSource() {
		return source;
	}

	public void setAnchor(String anchor) {
		this.anchor = anchor;
	}

	public void setSource(CTBookmark source) {
		this.source = source;
	}

	public void setExportor(Docx4jReport exportor) {
		this.exportor = exportor;
	}

}
