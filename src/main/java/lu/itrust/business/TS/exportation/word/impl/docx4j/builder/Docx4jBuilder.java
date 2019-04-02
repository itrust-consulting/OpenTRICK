/**
 * 
 */
package lu.itrust.business.TS.exportation.word.impl.docx4j.builder;

import lu.itrust.business.TS.exportation.word.Docx4jReportData;
import lu.itrust.business.TS.exportation.word.ExportReportData;
import lu.itrust.business.TS.exportation.word.IDocxBuilder;

/**
 * @author eomar
 *
 */
public abstract class Docx4jBuilder implements IDocxBuilder {

	@Override
	public boolean build(ExportReportData data, Object target) {
		if ((data instanceof Docx4jReportData) && (target instanceof String)) {
			if (internalBuild((Docx4jReportData) data, (String) target))
				return true;
			else if (getNext() != null)
				return getNext().build(data, target);
		}
		return false;
	}

	protected abstract boolean internalBuild(Docx4jReportData data, String target);
}
