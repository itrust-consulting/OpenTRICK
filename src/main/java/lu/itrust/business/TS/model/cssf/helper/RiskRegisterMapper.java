/**
 * 
 */
package lu.itrust.business.TS.model.cssf.helper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.exception.TrickException;
import lu.itrust.business.TS.model.cssf.RiskRegisterItem;
import lu.itrust.business.TS.model.parameter.helper.ValueFactory;

/**
 * @author eomar
 *
 */
public class RiskRegisterMapper {
	
	public static Map<Integer, RiskRegisterHelper> Generate(List<RiskRegisterItem> riskRegisterItems,ValueFactory factory) throws TrickException{
		Map<Integer, RiskRegisterHelper> mapping = new LinkedHashMap<Integer, RiskRegisterHelper>(riskRegisterItems.size());
		for (RiskRegisterItem registerItem : riskRegisterItems) {
			RiskRegisterHelper registerHelper = new RiskRegisterHelper();
			mapping.put(registerItem.getId(), registerHelper);
			
			registerHelper.getRawEvaluation().setImpact(factory.findImpactLevelByMaxLevel(registerItem.getRawEvaluation().getImpact()));
			registerHelper.getRawEvaluation().setProbability(factory.findProbLevel(registerItem.getRawEvaluation().getProbability()));
		
			registerHelper.getExpectedEvaluation().setImpact(factory.findImpactLevelByMaxLevel(registerItem.getExpectedEvaluation().getImpact()));
			registerHelper.getExpectedEvaluation().setProbability(factory.findProbLevel(registerItem.getExpectedEvaluation().getProbability()));
			
			registerHelper.getNetEvaluation().setImpact(factory.findImpactLevelByMaxLevel(registerItem.getNetEvaluation().getImpact()));
			registerHelper.getNetEvaluation().setProbability(factory.findProbLevel(registerItem.getNetEvaluation().getProbability()));

		}
		return mapping;
	}

}
