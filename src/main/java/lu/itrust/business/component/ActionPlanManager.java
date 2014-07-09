/**
 * 
 */
package lu.itrust.business.component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.Asset;
import lu.itrust.business.TS.actionplan.ActionPlanAsset;
import lu.itrust.business.TS.actionplan.ActionPlanEntry;
import lu.itrust.business.TS.actionplan.ActionPlanType;

/**
 * @author eomar
 * 
 */
public class ActionPlanManager {

	public static Map<String, List<ActionPlanEntry>> SplitByType(List<ActionPlanEntry> entries) {
		Map<String, List<ActionPlanEntry>> mappingActionPlans = new LinkedHashMap<>();
		for (ActionPlanEntry entry : entries) {
			ActionPlanType apt = entry.getActionPlanType();
			List<ActionPlanEntry> templist = mappingActionPlans.get(apt.getName());
			if (templist == null) {
				templist = new LinkedList<>();
				mappingActionPlans.put(apt.getName(), templist);
			}
			templist.add(entry);
		}
		return mappingActionPlans;
	}

	public static List<Asset> getAssetsByActionPlanType(List<ActionPlanEntry> entries) {

		try {

			ActionPlanEntry ape = null;
			List<Asset> assets = new ArrayList<Asset>();
			if (entries != null && entries.size() > 0) {
				ape = entries.get(0);
			} else {
				throw new IllegalArgumentException("Action plan is empty!");
			}

			for (ActionPlanAsset apa : ape.getActionPlanAssets()) {
				assets.add(apa.getAsset());
			}
			return assets;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
}