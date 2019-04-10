/**
 * 
 */
package lu.itrust.business.TS.exportation.word.impl.docx4j.builder.chain;

import static lu.itrust.business.TS.exportation.word.ExportReportData.TS_TAB_TEXT_2;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.Docx4jReportImpl.setColor;

import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import org.docx4j.jaxb.XPathBinderAssociationIsPartialException;
import org.docx4j.wml.P;
import org.docx4j.wml.PPrBase.TextAlignment;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;

import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.exportation.word.IDocxBuilder;
import lu.itrust.business.TS.exportation.word.impl.docx4j.Docx4jReportImpl;
import lu.itrust.business.TS.exportation.word.impl.docx4j.DocxChainFactory;
import lu.itrust.business.TS.exportation.word.impl.docx4j.builder.Docx4jBuilder;
import lu.itrust.business.TS.exportation.word.impl.docx4j.builder.Docx4jData;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.asset.Asset;

/**
 * @author eomar
 *
 */
public class Docx4jAssetBuilder extends Docx4jBuilder {

	private static final String TS_QL_ASSETNOTSELECTED = "ts_ql_assetnotselected";
	private static final String TS_QT_ASSETNOTSELECTED = "ts_qt_assetnotselected";
	private static final String TS_QL_ASSET = "ts_ql_asset";
	private static final String TS_QT_ASSET = "ts_qt_asset";

	public Docx4jAssetBuilder(IDocxBuilder next) {
		super(next, TS_QT_ASSET, TS_QT_ASSETNOTSELECTED, TS_QL_ASSET, TS_QL_ASSETNOTSELECTED);
	}

	@Override
	protected boolean internalBuild(Docx4jData data) {
		try {
			switch (data.getAnchor()) {
			case TS_QL_ASSET:
				return qualitativeBuild(data, data.getExportor().getAnalysis().getAssets().stream().filter(Asset::isSelected).collect(Collectors.toList()));
			case TS_QL_ASSETNOTSELECTED:
				return qualitativeBuild(data, data.getExportor().getAnalysis().getAssets().stream().filter(a -> !a.isSelected()).collect(Collectors.toList()));
			case TS_QT_ASSET:
				return quantitativeBuild(data, data.getExportor().getAnalysis().getAssets().stream().filter(Asset::isSelected).collect(Collectors.toList()));
			case TS_QT_ASSETNOTSELECTED:
				return quantitativeBuild(data, data.getExportor().getAnalysis().getAssets().stream().filter(a -> !a.isSelected()).collect(Collectors.toList()));
			default:
				return false;
			}
		} catch (XPathBinderAssociationIsPartialException | JAXBException e) {
			throw new TrickException("error.internal.report", null, e);
		}
	}

	private boolean quantitativeBuild(final Docx4jData data, final List<Asset> assets) {
		final Docx4jReportImpl exporter = (Docx4jReportImpl) data.getExportor();
		final P paragraph = exporter.findP(data.getSource());
		if (paragraph != null) {
			
			final Tbl table = exporter.createTable("TableTSAsset", assets.size() + 1, 6);
			final TextAlignment alignment = exporter.createAlignment("left");
			Tr row = (Tr) table.getContent().get(0);
			// set header
			exporter.setCurrentParagraphId(TS_TAB_TEXT_2);
			exporter.setCellText((Tc) row.getContent().get(0), exporter.getMessage("report.asset.title.number.row", null, "Nr"));
			exporter.setCellText((Tc) row.getContent().get(1), exporter.getMessage("report.asset.title.name", null, "Name"));
			exporter.setCellText((Tc) row.getContent().get(2), exporter.getMessage("report.asset.title.type", null, "Type"));
			exporter.setCellText((Tc) row.getContent().get(3), exporter.getMessage("report.asset.title.value", null, "Value(k€)"));
			exporter.setCellText((Tc) row.getContent().get(4), exporter.getMessage("report.asset.title.ale", null, "ALE(k€)"));
			exporter.setCellText((Tc) row.getContent().get(5), exporter.getMessage("report.asset.title.comment", null, "Comment"));
			exporter.setRepeatHeader(row);
			int number = 1;
			// set data
			for (Asset asset : assets) {
				row = (Tr) table.getContent().get(number);
				exporter.setCellText((Tc) row.getContent().get(0), "" + (number++));
				exporter.setCellText((Tc) row.getContent().get(1), asset.getName(), alignment);
				exporter.setCellText((Tc) row.getContent().get(2), exporter.getDisplayName(asset.getAssetType()));
				exporter.addCellNumber((Tc) row.getContent().get(3), exporter.getKiloNumberFormat().format(asset.getValue() * 0.001));
				setColor(((Tc) row.getContent().get(4)), exporter.getLightColor());
				exporter.addCellNumber((Tc) row.getContent().get(4), exporter.getKiloNumberFormat().format(asset.getALE() * 0.001));
				exporter.addCellParagraph((Tc) row.getContent().get(5), asset.getComment());
			}
			if (exporter.insertBefore(paragraph, table))
				DocxChainFactory.format(table, exporter.getDefaultTableStyle(), AnalysisType.QUANTITATIVE);
		}
		return true;
	}

	private boolean qualitativeBuild(final Docx4jData data, final List<Asset> assets) throws XPathBinderAssociationIsPartialException, JAXBException {
		final Docx4jReportImpl exporter = (Docx4jReportImpl) data.getExportor();
		final P paragraph = (P) exporter.findP(data.getSource());
		if (paragraph != null) {
			final TextAlignment alignmentLeft = exporter.createAlignment("left");
			final TextAlignment alignmentCenter = exporter.createAlignment("center");
			final Tbl table = exporter.createTable("TableTSAsset", assets.size() + 1, 5);
			Tr row = (Tr) table.getContent().get(0);
			// set header
			exporter.setCurrentParagraphId(TS_TAB_TEXT_2);
			exporter.setCellText((Tc) row.getContent().get(0), exporter.getMessage("report.asset.title.number.row", null, "Nr"));
			exporter.setCellText((Tc) row.getContent().get(1), exporter.getMessage("report.asset.title.name", null, "Name"));
			exporter.setCellText((Tc) row.getContent().get(2), exporter.getMessage("report.asset.title.type", null, "Type"));
			exporter.setCellText((Tc) row.getContent().get(3), exporter.getMessage("report.asset.title.value", null, "Value(k€)"));
			exporter.setCellText((Tc) row.getContent().get(4), exporter.getMessage("report.asset.title.comment", null, "Comment"));
			exporter.setRepeatHeader(row);
			int number = 1;
			// set data
			for (Asset asset : assets) {
				row = (Tr) table.getContent().get(number);
				exporter.setCellText((Tc) row.getContent().get(0), "" + (number++), alignmentCenter);
				exporter.setCellText((Tc) row.getContent().get(1), asset.getName(), alignmentLeft);
				exporter.setCellText((Tc) row.getContent().get(2), exporter.getDisplayName(asset.getAssetType()));
				exporter.addCellNumber((Tc) row.getContent().get(3), exporter.getKiloNumberFormat().format(asset.getValue() * 0.001));
				exporter.addCellParagraph((Tc) row.getContent().get(4), asset.getComment());
			}
			if (exporter.insertBefore(paragraph, table))
				DocxChainFactory.format(table, exporter.getDefaultTableStyle(), AnalysisType.QUALITATIVE);
		}
		return true;
	}
}
