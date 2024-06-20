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
 * The ActionPlanManager class provides utility methods for managing action plans.
 */
public class ActionPlanManager {

	/**
	 * Splits the given list of action plan entries by their type.
	 *
	 * @param entries The list of action plan entries to be split.
	 * @return A map where the keys are the action plan types and the values are the corresponding action plan entries.
	 */
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

	/**
	 * Retrieves the assets associated with the given action plan entries.
	 *
	 * @param entries The list of action plan entries.
	 * @return A list of assets associated with the action plan entries.
	 * @throws TrickException If the action plan is empty.
	 */
	public static List<Asset> getAssetsByActionPlanType(List<ActionPlanEntry> entries) throws TrickException {
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

	/**
	 * Orders the action plan assets based on the given asset list.
	 *
	 * @param entry  The action plan entry.
	 * @param assets The list of assets to be used for ordering.
	 * @return A list of action plan assets ordered based on the asset list.
	 */
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