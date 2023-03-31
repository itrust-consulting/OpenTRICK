package lu.itrust.business.ts.database.template;

import java.util.List;

import lu.itrust.business.ts.model.parameter.impl.SimpleParameter;
import lu.itrust.business.ts.model.parameter.type.impl.ParameterType;

public interface TemplateSimpleParameter extends TemplateAnalysisMember<SimpleParameter, Integer> {
	
	List<SimpleParameter> findByTypeAndAnalysisId(String type, Integer idAnalysis);

	List<SimpleParameter> findByTypeAndAnalysisId(ParameterType type, Integer idAnalysis);

	SimpleParameter findByAnalysisIdAndDescription(Integer idAnalysis, String description);
	
	SimpleParameter findByAnalysisIdAndTypeAndDescription(Integer idAnalysis, String parametertypeTypeSingleName, String description);
}
