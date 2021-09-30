/**
 * 
 */
package lu.itrust.business.TS.exportation.word.impl.docx4j.builder.chain;

import static lu.itrust.business.TS.constants.Constant.RI_TYPE_RISK;
import static lu.itrust.business.TS.constants.Constant.RI_TYPE_THREAT;
import static lu.itrust.business.TS.constants.Constant.RI_TYPE_VUL;
import static lu.itrust.business.TS.exportation.word.impl.docx4j.Docx4jReportImpl.setColor;

import java.util.List;
import java.util.stream.Collectors;

import org.docx4j.wml.P;
import org.docx4j.wml.PPrBase.TextAlignment;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;

import lu.itrust.business.TS.exportation.word.IDocxBuilder;
import lu.itrust.business.TS.exportation.word.impl.docx4j.Docx4jReportImpl;
import lu.itrust.business.TS.exportation.word.impl.docx4j.DocxChainFactory;
import lu.itrust.business.TS.exportation.word.impl.docx4j.builder.Docx4jBuilder;
import lu.itrust.business.TS.exportation.word.impl.docx4j.builder.Docx4jData;
import lu.itrust.business.TS.model.analysis.AnalysisType;
import lu.itrust.business.TS.model.riskinformation.RiskInformation;
import lu.itrust.business.TS.model.riskinformation.helper.RiskInformationComparator;

/**
 * @author eomar
 *
 */
public class Docx4jRiskInformationBuilder extends Docx4jBuilder {

	private static final String TS_RISK = "ts_risk";

	private static final String TS_THREAT = "ts_threat";

	private static final String TS_VULNERABILITY = "ts_vulnerability";

	/**
	 * @param next
	 * @param supports
	 */
	public Docx4jRiskInformationBuilder(IDocxBuilder next) {
		super(next, TS_VULNERABILITY, TS_RISK, TS_THREAT);
	}

	@Override
	protected boolean internalBuild(Docx4jData data) {
		switch (data.getAnchor()) {
		case TS_RISK:
			return buildRiskInformation(data, RI_TYPE_RISK);
		case TS_THREAT:
			return buildRiskInformation(data, RI_TYPE_THREAT);
		case TS_VULNERABILITY:
			return buildRiskInformation(data, RI_TYPE_VUL);
		default:
			return true;
		}
	}

	private boolean buildRiskInformation(Docx4jData data, String type) {
		final Docx4jReportImpl exporter = (Docx4jReportImpl) data.getExportor();
		final P paragraph = (P) exporter.findP(data.getSource());
		if (paragraph != null) {
			final int index[] = { 0 /* row counter */, 0 /* column counter */ };
			final int coloredColumn = RI_TYPE_THREAT.equals(type) ? 3 : 2;
			final TextAlignment alignmentLeft = exporter.createAlignment("left");
			final TextAlignment alignmentCenter = exporter.createAlignment("center");
			final List<RiskInformation> riskInformations = exporter.getAnalysis().getRiskInformations().stream().filter(rf -> rf.isMatch(type))
					.sorted(new RiskInformationComparator()).collect(Collectors.toList());
			final Tbl table = exporter.createTable("TableTS" + type, riskInformations.size() + 1, RI_TYPE_THREAT.equals(type) ? 6 : 5);
			final Tr header = (Tr) table.getContent().get(index[0]++);

			exporter.setCellText((Tc) header.getContent().get(index[1]++), exporter.getMessage(String.format("report.risk_information.title.%s", "id"), null, "Id"));
			exporter.setCellText((Tc) header.getContent().get(index[1]++),
					exporter.getMessage(String.format("report.risk_information.title.%s", type.toLowerCase()), null, type.toLowerCase()));
			if (RI_TYPE_THREAT.equals(type))
				exporter.setCellText((Tc) header.getContent().get(index[1]++), exporter.getMessage(String.format("report.risk_information.title.%s", "acro"), null, "Acro"));
			exporter.setCellText((Tc) header.getContent().get(index[1]++), exporter.getMessage(String.format("report.risk_information.title.%s", "expo"), null, "Expo."),
					alignmentLeft);
			exporter.setCellText((Tc) header.getContent().get(index[1]++), exporter.getMessage(String.format("report.risk_information.title.%s", "owner"), null, "Owner"));
			exporter.setCellText((Tc) header.getContent().get(index[1]++), exporter.getMessage(String.format("report.risk_information.title.%s", "comment"), null, "Comment"));
			exporter.setRepeatHeader(header);
			riskInformations.forEach(riskinfo -> {
				final Tr row = (Tr) table.getContent().get(index[0]++);
				for (int i = 0; i < coloredColumn; i++)
					setColor(((Tc) row.getContent().get(i)), exporter.getDefaultColor());
				exporter.setCellText((Tc) row.getContent().get(index[1] = 0), riskinfo.getChapter());// reset column counter
				exporter.setCellText((Tc) row.getContent().get(++index[1]),
						exporter.getMessage(String.format("label.risk_information.%s.%s", riskinfo.getCategory().toLowerCase(), riskinfo.getChapter().replace(".", "_")), null,
								riskinfo.getLabel()),
						alignmentLeft);
				if (coloredColumn == 3)
					exporter.setCellText((Tc) row.getContent().get(++index[1]), riskinfo.getAcronym());
				exporter.setCellText((Tc) row.getContent().get(++index[1]), riskinfo.getExposed(), alignmentCenter);
				exporter.setCellText((Tc) row.getContent().get(++index[1]), exporter.getValueOrEmpty(riskinfo.getOwner()));
				exporter.addCellParagraph((Tc) row.getContent().get(++index[1]), riskinfo.getComment());
			});
			if (exporter.insertBefore(paragraph, table))
				DocxChainFactory.format(table, exporter.getDefaultTableStyle(), AnalysisType.HYBRID, exporter.getColors());
		}
		return true;
	}

}
