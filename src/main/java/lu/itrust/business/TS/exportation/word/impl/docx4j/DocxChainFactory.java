package lu.itrust.business.TS.exportation.word.impl.docx4j;

import lu.itrust.business.TS.exportation.word.DocxFormatter;
import lu.itrust.business.TS.exportation.word.IBuildData;
import lu.itrust.business.TS.exportation.word.IDocxBuilder;
import lu.itrust.business.TS.exportation.word.impl.docx4j.builder.chain.Docx4jALEChartBuilder;
import lu.itrust.business.TS.exportation.word.impl.docx4j.builder.chain.Docx4jActionPlanBuilder;
import lu.itrust.business.TS.exportation.word.impl.docx4j.builder.chain.Docx4jAssessmentBuilder;
import lu.itrust.business.TS.exportation.word.impl.docx4j.builder.chain.Docx4jAssetBuilder;
import lu.itrust.business.TS.exportation.word.impl.docx4j.builder.chain.Docx4jHeatMapRiskAcceptanceBuilder;
import lu.itrust.business.TS.exportation.word.impl.docx4j.builder.chain.Docx4jPhaseBuilder;
import lu.itrust.business.TS.exportation.word.impl.docx4j.builder.chain.Docx4jProbaImpactBuilder;
import lu.itrust.business.TS.exportation.word.impl.docx4j.builder.chain.Docx4jRentabilityChartBuilder;
import lu.itrust.business.TS.exportation.word.impl.docx4j.builder.chain.Docx4jRiskChartBuilder;
import lu.itrust.business.TS.exportation.word.impl.docx4j.builder.chain.Docx4jRiskInformationBuilder;
import lu.itrust.business.TS.exportation.word.impl.docx4j.builder.chain.Docx4jScenarioBuilder;
import lu.itrust.business.TS.exportation.word.impl.docx4j.builder.chain.Docx4jScopeBuilder;
import lu.itrust.business.TS.exportation.word.impl.docx4j.builder.chain.Docx4jStandardBuilder;
import lu.itrust.business.TS.exportation.word.impl.docx4j.builder.chain.Docx4jSummaryBuilder;
import lu.itrust.business.TS.exportation.word.impl.docx4j.formatting.Docx4jActionPlanFormatter;
import lu.itrust.business.TS.exportation.word.impl.docx4j.formatting.Docx4jAssessmentFormatter;
import lu.itrust.business.TS.exportation.word.impl.docx4j.formatting.Docx4jAssetFormatter;
import lu.itrust.business.TS.exportation.word.impl.docx4j.formatting.Docx4jFormatter;
import lu.itrust.business.TS.exportation.word.impl.docx4j.formatting.Docx4jHeatMapLegendFormatter;
import lu.itrust.business.TS.exportation.word.impl.docx4j.formatting.Docx4jImpactProbaFormatter;
import lu.itrust.business.TS.exportation.word.impl.docx4j.formatting.Docx4jMeasureFormatter;
import lu.itrust.business.TS.exportation.word.impl.docx4j.formatting.Docx4jRiskAcceptanceFormatter;
import lu.itrust.business.TS.exportation.word.impl.docx4j.formatting.Docx4jRiskHeatMapFormatter;
import lu.itrust.business.TS.exportation.word.impl.docx4j.formatting.Docx4jRiskInformationFormatter;
import lu.itrust.business.TS.exportation.word.impl.docx4j.formatting.Docx4jScenarioFormatter;
import lu.itrust.business.TS.exportation.word.impl.docx4j.formatting.Docx4jScopeFormatter;
import lu.itrust.business.TS.exportation.word.impl.docx4j.formatting.Docx4jSummaryFormatter;
import lu.itrust.business.TS.model.analysis.AnalysisType;

public class DocxChainFactory {

	private IDocxBuilder builderChain;

	private DocxFormatter formatterChain;

	private volatile static DocxChainFactory instance;

	private DocxChainFactory() {
		buildFormatterChain();
		buildChainBuilder();
	}

	private void buildFormatterChain() {
		Docx4jFormatter docx4jFormatter = new Docx4jRiskAcceptanceFormatter();
		docx4jFormatter = new Docx4jHeatMapLegendFormatter(docx4jFormatter);
		docx4jFormatter = new Docx4jRiskHeatMapFormatter(docx4jFormatter);
		docx4jFormatter = new Docx4jSummaryFormatter(docx4jFormatter);
		docx4jFormatter = new Docx4jScopeFormatter(docx4jFormatter);
		docx4jFormatter = new Docx4jScenarioFormatter(docx4jFormatter);
		docx4jFormatter = new Docx4jRiskInformationFormatter(docx4jFormatter);
		docx4jFormatter = new Docx4jMeasureFormatter(docx4jFormatter);
		docx4jFormatter = new Docx4jImpactProbaFormatter(docx4jFormatter);
		docx4jFormatter = new Docx4jAssetFormatter(docx4jFormatter);
		docx4jFormatter = new Docx4jAssessmentFormatter(docx4jFormatter);
		docx4jFormatter = new Docx4jActionPlanFormatter(docx4jFormatter);
		setFormatterChain(docx4jFormatter);
	}

	private void buildChainBuilder() {
		IDocxBuilder builder = new Docx4jScenarioBuilder(null);
		builder = new Docx4jRentabilityChartBuilder(builder);
		builder = new Docx4jScopeBuilder(builder);
		builder = new Docx4jProbaImpactBuilder(builder);
		builder = new Docx4jSummaryBuilder(builder);
		builder = new Docx4jPhaseBuilder(builder);
		builder = new Docx4jHeatMapRiskAcceptanceBuilder(builder);
		builder = new Docx4jActionPlanBuilder(builder);
		builder = new Docx4jAssessmentBuilder(builder);
		builder = new Docx4jRiskChartBuilder(builder);
		builder = new Docx4jRiskInformationBuilder(builder);
		builder = new Docx4jALEChartBuilder(builder);
		builder = new Docx4jAssetBuilder(builder);
		builder = new Docx4jStandardBuilder(builder);
		
		setBuilderChain(builder);
	}

	public static DocxChainFactory getInstance() {
		DocxChainFactory factory = instance;
		if (factory == null) {
			synchronized (DocxChainFactory.class) {
				factory = instance;
				if (factory == null)
					instance = factory = new DocxChainFactory();
			}
		}
		return factory;
	}

	public static boolean build(IBuildData data) {
		return getInstance().getBuilderChain().build(data);
	}

	public static boolean format(Object table, Object style, AnalysisType type) {
		return getInstance().getFormatterChain().format(table, style, type);
	}

	public DocxFormatter getFormatterChain() {
		return formatterChain;
	}

	public IDocxBuilder getBuilderChain() {
		return builderChain;
	}

	/**
	 * @param builderChain the builderChain to set
	 */
	protected void setBuilderChain(IDocxBuilder builderChain) {
		this.builderChain = builderChain;
	}

	/**
	 * @param formatterChain the formatterChain to set
	 */
	protected void setFormatterChain(DocxFormatter formatterChain) {
		this.formatterChain = formatterChain;
	}

}
