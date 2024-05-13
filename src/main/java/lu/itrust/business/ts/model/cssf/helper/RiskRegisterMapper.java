/**
 * 
 */
package lu.itrust.business.ts.model.cssf.helper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.model.cssf.RiskRegisterItem;
import lu.itrust.business.ts.model.parameter.helper.ValueFactory;


/**
 * The RiskRegisterMapper class is responsible for mapping a list of RiskRegisterItems to a map of RiskRegisterHelpers.
 * It provides a static method to generate the mapping.
 */
public class RiskRegisterMapper {
	
	/**
	 * Generates a mapping of RiskRegisterItems to RiskRegisterHelpers.
	 * 
	 * @param riskRegisterItems The list of RiskRegisterItems to be mapped.
	 * @param factory The ValueFactory object used for finding impact and probability levels.
	 * @return A map of RiskRegisterHelpers, where the key is the ID of the RiskRegisterItem and the value is the corresponding RiskRegisterHelper.
	 * @throws TrickException If an error occurs during the mapping process.
	 */
	public static Map<Integer, RiskRegisterHelper> Generate(List<RiskRegisterItem> riskRegisterItems, ValueFactory factory) throws TrickException {
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
