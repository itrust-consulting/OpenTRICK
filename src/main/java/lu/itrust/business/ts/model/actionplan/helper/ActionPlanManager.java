/**
 * 
 */
package lu.itrust.business.ts.model.actionplan.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lu.itrust.business.ts.component.TrickLogManager;
import lu.itrust.business.ts.exception.TrickException;
import lu.itrust.business.ts.model.actionplan.ActionPlanAsset;
import lu.itrust.business.ts.model.actionplan.ActionPlanEntry;
import lu.itrust.business.ts.model.actionplan.ActionPlanType;
import lu.itrust.business.ts.model.asset.Asset;

/**
 * @author eomar
 * 
 */
public class ActionPlanManager {

	public static Map<String, List<ActionPlanEntry>> splitByType(List<ActionPlanEntry> entries) {
		Map<String, List<ActionPlanEntry>> mappingActionPlans = new LinkedHashMap<>();
		for (ActionPlanEntry entry : entries) {
			ActionPlanType apt = entry.getActionPlanType();
			List<ActionPlanEntry> templist = mappingActionPlans.get(apt.getName());
			if (templist == null)
				mappingActionPlans.put(apt.getName(), templist = new LinkedList<>());
			templist.add(entry);
		}
		return mappingActionPlans;
	}

	public static List<Asset> getAssetsByActionPlanType(List<ActionPlanEntry> entries) {
		try {
			ActionPlanEntry ape = null;
			List<Asset> assets = new ArrayList<>();
			if (!(entries == null || entries.isEmpty()))
				ape = entries.get(0);
			else
				throw new TrickException("error.action.plan.empty", "Action plan is empty!");

			for (ActionPlanAsset apa : ape.getActionPlanAssets())
				assets.add(apa.getAsset());
			return assets;
		} catch (TrickException e) {
			TrickLogManager.Persist(e);
		}
		return Collections.emptyList();
	}

	public static List<ActionPlanAsset> orderActionPlanAssetsByAssetList(ActionPlanEntry entry, List<Asset> assets) {
		try {
			List<ActionPlanAsset> apassets = new ArrayList<>();
			Map<Asset, ActionPlanAsset> mapofassets = new LinkedHashMap<>();
			for (ActionPlanAsset apasset : entry.getActionPlanAssets())
				mapofassets.put(apasset.getAsset(), apasset);
			for (Asset asset : assets)
				apassets.add(mapofassets.get(asset));
			return apassets;
		} catch (Exception e) {
			TrickLogManager.Persist(e);
			return Collections.emptyList();
		}

	}
}