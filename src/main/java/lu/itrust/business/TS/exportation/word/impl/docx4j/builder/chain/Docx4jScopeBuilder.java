/**
 * 
 */
package lu.itrust.business.TS.exportation.word.impl.docx4j.builder.chain;

import java.util.Collections;
import java.util.List;

import org.docx4j.wml.P;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;
import org.docx4j.wml.PPrBase.TextAlignment;

import lu.itrust.business.TS.exportation.word.ExportReport;
import lu.itrust.business.TS.exportation.word.IDocxBuilder;
import lu.itrust.business.TS.exportation.word.impl.docx4j.Docx4jReportImpl;
import lu.itrust.business.TS.exportation.word.impl.docx4j.DocxChainFactory;
import lu.itrust.business.TS.exportation.word.impl.docx4j.builder.Docx4jBuilder;
import lu.itrust.business.TS.exportation.word.impl.docx4j.builder.Docx4jData;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.iteminformation.ItemInformation;
import lu.itrust.business.TS.model.iteminformation.helper.ComparatorItemInformation;

/**
 * @author eomar
 *
 */
public class Docx4jScopeBuilder extends Docx4jBuilder {

	/**
	 * @param next
	 * @param supports
	 */
	public Docx4jScopeBuilder(IDocxBuilder next) {
		super(next, "ts_scope");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * lu.itrust.business.TS.exportation.word.impl.docx4j.builder.Docx4jBuilder#
	 * internalBuild(lu.itrust.business.TS.exportation.word.impl.docx4j.builder.
	 * Docx4jData)
	 */
	@Override
	protected boolean internalBuild(Docx4jData data) {
		final Docx4jReportImpl exporter = (Docx4jReportImpl) data.getExportor();
		final P paragraph = (P) exporter.findP(data.getSource());
		if (paragraph != null) {
			final List<ItemInformation> iteminformations = exporter.getAnalysis().getItemInformations();
			final Tbl table = exporter.createTable("TableTSScope", iteminformations.size() + 1, 2);
			final TextAlignment alignment = exporter.createAlignment("left");
			Collections.sort(iteminformations, new ComparatorItemInformation());
			exporter.setCurrentParagraphId(ExportReport.TS_TAB_TEXT_2);
			Tr row = (Tr) table.getContent().get(0);
			exporter.setCellText((Tc) row.getContent().get(0), exporter.getMessage("report.scope.title.description", null, "Description"));
			exporter.setCellText((Tc) row.getContent().get(1), exporter.getMessage("report.scope.title.value", null, "Value"));
			exporter.setRepeatHeader(row);
			int rowIndex = 1;
			// set data
			for (ItemInformation iteminfo : iteminformations) {
				row = (Tr) table.getContent().get(rowIndex++);
				exporter.setCellText((Tc) row.getContent().get(0),
						exporter.getMessage("report.scope.name." + iteminfo.getDescription().toLowerCase(), null, iteminfo.getDescription()), alignment);
				exporter.addCellParagraph((Tc) row.getContent().get(1), iteminfo.getValue());
			}
			if (exporter.insertBefore(paragraph, table))
				DocxChainFactory.format(table, exporter.getDefaultTableStyle(), AnalysisType.HYBRID, exporter.getColors());
		}
		return true;
	}

}
