/**
 * 
 */
package lu.itrust.business.TS.data.cssf.helper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.data.cssf.RiskRegisterItem;
import lu.itrust.business.TS.data.parameter.Parameter;
import lu.itrust.business.TS.exception.TrickException;

/**
 * @author eomar
 *
 */
public class RiskRegisterMapper {
	
	public static Map<Integer, RiskRegisterHelper> Generate(List<RiskRegisterItem> riskRegisterItems,List<Parameter> parameters) throws TrickException{
		Map<Integer, RiskRegisterHelper> mapping = new LinkedHashMap<Integer, RiskRegisterHelper>(riskRegisterItems.size());
		ParameterConvertor convertor = new ParameterConvertor(parameters);
		for (RiskRegisterItem registerItem : riskRegisterItems) {
			RiskRegisterHelper registerHelper = new RiskRegisterHelper();
			mapping.put(registerItem.getId(), registerHelper);
			
			registerHelper.getRawEvaluation().setImpact(convertor.getImpactLevel(registerItem.getRawEvaluation().getImpact()));
			registerHelper.getRawEvaluation().setProbability(convertor.getProbabiltyLevel(registerItem.getRawEvaluation().getProbability()));
			
			
			registerHelper.getExpectedImportance().setImpact(convertor.getImpactLevel(registerItem.getExpectedImportance().getImpact()));
			registerHelper.getExpectedImportance().setProbability(convertor.getProbabiltyLevel(registerItem.getExpectedImportance().getProbability()));
			
			registerHelper.getNetEvaluation().setImpact(convertor.getImpactLevel(registerItem.getNetEvaluation().getImpact()));
			registerHelper.getNetEvaluation().setProbability(convertor.getProbabiltyLevel(registerItem.getNetEvaluation().getProbability()));

		}
		return mapping;
	}

}
