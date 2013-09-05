package lu.itrust.business.TS.cssf.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lu.itrust.business.TS.cssf.RiskRegisterItem;
import lu.itrust.business.TS.cssf.RiskRegisterItemGroup;

/**
 * CSSFSort: <br>
 * This class is used to sort RiskRegisterItems.<br>
 * Features:
 * <ul>
 * <li>Sorting by category [direct, indirect, other]</li>
 * <li>Sorting each group [direct, indirect] by net importance</li>
 * <li>Sorting group[other] by impact and probability net</li>
 * </ul>
 * 
 * @author itrust consulting s.�.rl. : BJA, EOM, SME
 * @version 0.1
 * @since 27 d�c. 2012
 */
public class CSSFSort {

	/***********************************************************************************************
	 * Field declarations
	 **********************************************************************************************/

	/**
	 * Indirect category regular expression: <br />
	 * <b>(i|I|InDirect|Indirect|indirect|inDirect)([1-9]|8\\.[1-4]|10)</b>
	 */
	public static final String INDIRECT_REGEX = "(i|I|InDirect|Indirect|indirect|inDirect)([1-9]|8\\.[1-4]|10)";

	/**
	 * Direct category regular expression: <br />
	 * <b>(d|D|Direct|direct)([1-7]|6\\.[1-4])</b>
	 */
	public static final String DIRECT_REGEX = "(d|D|Direct|direct)([1-7]|6\\.[1-4])";

	/** acceptable net Impact to verify on risk register list item adding */
	public static final int ACCEPTABLE_NET_IMPACT = 6;

	/** acceptable net probability to verify on risk register list item adding */
	public static final int ACCEPTABLE_NET_PROBABILITY = 5;

	/** Group name: <b>other</b> */
	public static final String OTHER = "other";

	/** Group name: <b>indirect</b> */
	public static final String INDIRECT = "indirect";

	/** Group name: <b>direct</b> */
	public static final String DIRECT = "direct";

	/***********************************************************************************************
	 * Methods
	 **********************************************************************************************/

	/**
	 * sortByGroup: <br>
	 * Initialises groups with empty RiskRegisterItem Lists (if group name was
	 * not yet found), or with existing groups Sort risk register in three
	 * groups [direct, indirect, other]
	 * 
	 * @param registers
	 *            CSSF computation result
	 * 
	 * @return initialised groups with empty RiskkRegisterItem Lists
	 * @see #findGroup(String)
	 */
	public static Map<String, List<RiskRegisterItemGroup>> sortByGroup(
			Map<String, RiskRegisterItem> registers) {

		// initialises the group list which contains 3 entries: direct, indirect
		// and others as keys
		// and for each entry the corresponding riskregister item list
		Map<String, List<RiskRegisterItemGroup>> groups = new HashMap<String, List<RiskRegisterItemGroup>>(
				3);

		// parse all riskregister items
		for (RiskRegisterItem registerItem : registers.values()) {

			// retrieve group name from scenario type (direct, indirect or
			// other)
			String groupName = findGroup(registerItem.getScenario().getType()
					.getTypeName());

			// retrieve group from name (the risk register item list)
			List<RiskRegisterItemGroup> registerItemGroups = groups
					.get(groupName);

			// check if the list is not yet initialised -> YES
			if (registerItemGroups == null) {

				// create a new risk register item list
				registerItemGroups = new ArrayList<RiskRegisterItemGroup>();

				// add this empty list to the groups
				groups.put(groupName, registerItemGroups);
			}

			RiskRegisterItemGroup registerItemGroup = null;

			for (int i = 0; i < registerItemGroups.size(); i++) {

				if (registerItemGroups.get(i).getIdScenario() == registerItem
						.getScenario().getId()) {
					registerItemGroup = registerItemGroups.get(i);
					break;
				}
			}

			if (registerItemGroup == null) {
				registerItemGroup = new RiskRegisterItemGroup(registerItem
						.getScenario().getId());
				registerItemGroups.add(registerItemGroup);
			}

			// add the risk register item to the list inside this group
			registerItemGroup.add(registerItem);
		}

		// return all risk register items grouped by their scenario type into
		// direct, indeirect and
		// other
		return groups;
	}

	/**
	 * sortAndConcatGroup: <br>
	 * Identify:
	 * <ul>
	 * <li>20 most important direct risk(referring to the net importance value)</li>
	 * <li>5 most important indirect risk(referring to the net importance value)
	 * </li>
	 * <li>
	 * Risks that have a net impact >=6 and net impact probability >= 5 (inside
	 * direct and indirect)</li>
	 * </ul>
	 * Afterwards, the 2 lists will be concatenated<br>
	 * <ul>
	 * <li>
	 * RiskRegister with id between 1 and 20, are the 20 most important direct
	 * risk(referring to the net importance value) after 20 come those where
	 * impact >= 6 and probability >= 5</li>
	 * <li>
	 * RiskRegister with id after the 20 and x items with impact >= 6 and
	 * probability >= 5 come the 5 most important indirect risk(referring to the
	 * net importance value) after these 5 items come all indirect risk that
	 * have impact >= 6 and probability >= 5</li>
	 * </ul>
	 * 
	 * @param groups
	 *            The 3 lists of indirect, direct and other risk register items
	 * 
	 * @return A concatenated list of all direct and indirect items
	 */
	public static List<RiskRegisterItem> sortAndConcatenateGroup(
			Map<String, List<RiskRegisterItemGroup>> groups,
			double acceptableImportace) {

		// initialise or use existing group direct
		List<RiskRegisterItemGroup> direct20 = groups.containsKey(DIRECT) ? groups
				.get(DIRECT) : new ArrayList<RiskRegisterItemGroup>();

		// initialise or use existing group indirect
		List<RiskRegisterItemGroup> indirect5 = groups.containsKey(INDIRECT) ? groups
				.get(INDIRECT) : new ArrayList<RiskRegisterItemGroup>();

		List<RiskRegisterItemGroup> cia = groups.containsKey(OTHER) ? groups
				.get(OTHER) : new ArrayList<RiskRegisterItemGroup>();

		// sort direct risk register items descending
		sortByNetImportance(direct20);

		// sort indirect risk register items descending
		sortByNetImportance(indirect5);

		sortByNetImportance(cia);

		// identify 20 most important direct risks, start at index 1 and add all
		// those where
		// impact >= 5 and probability >= 6
		indexRiskRegisterItem(direct20, 1, 20, acceptableImportace);

		// identify 5 most important indirect risks, start at last index of the
		// direct risks + 1 and
		// add all those where impact >= 5 and probability >= 6
		indexRiskRegisterItem(indirect5, direct20.size() + 1, 5,
				acceptableImportace);

		direct20.addAll(indirect5);

		indexRiskRegisterItem(cia, direct20.size() + 1, cia.size(),
				acceptableImportace);

		direct20.addAll(cia);

		List<RiskRegisterItem> registerItems = new ArrayList<>();

		for (int i = 0; i < direct20.size(); i++) {
			sortRiskRegisterItemGroupByNetImportance(direct20.get(i)
					.getRegisters());
			registerItems.addAll(direct20.get(i).getRegisters());
		}

		// concatenate all indirect items to the direct items list (direct start
		// from index 1,
		// indirect come after the last direct item)

		// return the whole risk register
		return registerItems;
	}

	/**
	 * selectedByProbabilityAndImpactAndIndexing: <br>
	 * Indexing RiskRegisterItem and remove all item with (net impact <
	 * impactMin or net pro < probaMin).
	 * 
	 * @param registerItems
	 *            The Items List to check
	 * @param startIndex
	 *            The start index inside the List
	 * @param impactMin
	 *            The minimum Impact value to be valid
	 * @param probaMin
	 *            The minimum Probability value to be valid
	 * 
	 * @deprecated by {@link #checkImpactAndProbability}
	 */
	public static void selectedByProbabilityAndImpactAndIndexing(
			List<RiskRegisterItem> registerItems, int startIndex,
			int impactMin, int probaMin) {

		// initialise index
		int i = 0;

		// parse all risk register items given as parameter
		while (i < registerItems.size()) {

			// retrieve probability value
			double pro = registerItems.get(i).getNetEvaluation()
					.getProbability();

			// retrieve impact value
			double impact = registerItems.get(i).getNetEvaluation().getImpact();

			// check if probability and impact are not acceptable
			if (impact < impactMin || pro < probaMin)

				// remove item
				registerItems.remove(i);
			else {

				// set position inside the list
				registerItems.get(i).setPosition(i + startIndex);

				// move to next item
				i++;
			}
		}
	}

	/**
	 * indexRiskRegisterItem: <br>
	 * RiskRegisterItem Indexing, on item out of bounds "limit" check with
	 * {@link #checkImpactAndProbability(RiskRegisterItem)} to have usable
	 * Items. Unusable items will be removed.
	 * 
	 * @param registerItems
	 *            The List of Risk Register Items to Index
	 * @param startIndex
	 *            The Index to start
	 * @param limit
	 *            the Limit of Items to add (default: 20 for direct; 5 for
	 *            indirect)
	 * @param parameters
	 */
	public static void indexRiskRegisterItem(
			List<RiskRegisterItemGroup> registerItems, int startIndex,
			int limit, double acceptableNetImportace) {

		// parse all risk register items
		for (int i = 0; i < registerItems.size();) {

			// check if current index has reached the limit and if the impact
			// and probability are
			// acceptable
			if (i < limit
					|| registerItems.get(i).getNetImportance() >= acceptableNetImportace) {

				// index this register item as next index
				registerItems.get(i).setPosition(i++ + startIndex);
			} else {

				// remove this registeritem (wont be used)
				registerItems.remove(i);
			}
		}
	}

	/**
	 * findGroup:<br>
	 * Tries to identify the group name [direct, indirect, other]<br>
	 * Parameter name should meet this convention: Category - Description
	 * (example: D1-Strat). Check if the parameter si direct or indirect.
	 * 
	 * @param name
	 *            The Category to check
	 * 
	 * @return [direct, indirect, other]
	 * 
	 * @see #isDirect(String)
	 * @see #isIndirect(String)
	 */
	public static String findGroup(String name) {

		// initialise the index to the character "-"
		int index = name.indexOf("-");

		// set the name to everything before the index "-" and removes spaces
		// (example: "D1 - Strat" -> "D1")

		name = index != -1 ? name.substring(0, index).trim() : "CIA";

		// check if the formated parameter is of type direct -> YES
		if (isDirect(name))

			// return direct answer
			return DIRECT;

		// check if the formated parameter is of type indirect -> YES
		else if (isIndirect(name))

			// return indirect answer
			return INDIRECT;
		else

			// all other categories will be categorised under other

			// return other answer
			return OTHER;
	}

	/**
	 * isDirect: <br>
	 * Check if the given Category meets {@link #DIRECT_REGEX this regular
	 * expression}. In other words it checks if the given Category is of Type
	 * Direct
	 * 
	 * @param name
	 *            The Category name to check
	 * 
	 * @return True if the Category is Direct; False if the Category is not
	 *         Direct
	 * 
	 */
	public static boolean isDirect(String name) {
		return name == null ? false : name.matches(DIRECT_REGEX);
	}

	/**
	 * isIndirect: <br>
	 * Check if the given Category meets {@link #INDIRECT_REGEX this regular
	 * expression}
	 * 
	 * @param name
	 *            The Category Type to check
	 * 
	 * @return True if the Category Type is Indirect; False if Category is not
	 *         Indirect
	 */
	public static boolean isIndirect(String name) {
		return name == null ? false : name.matches(INDIRECT_REGEX);
	}

	public static void sortByNetImportance(
			List<RiskRegisterItemGroup> registerItems) {
		Collections.sort(registerItems,
				new NetImportanceRegisterGroupComparatorDescending());
	}

	/**
	 * sortByNetImportance: <br>
	 * Sort a given Group of Risk Register Items by NET Importance. The
	 * Algorithme to compare is located inside the
	 * NetImportanceComparatorDescending class (it checks one item with another
	 * on a criteria inside NetImportanceComparatorDescending to sort)
	 * 
	 * @param registerItems
	 *            The List of Risk Register Items to sort
	 * 
	 * @see NetImportanceComparatorDescending
	 */
	public static void sortRiskRegisterItemGroupByNetImportance(
			List<RiskRegisterItem> registerItems) {

		// Sort the given list of risk register items using the Collections java
		// class and using the
		// NetImportanceComparatorDescending check
		Collections
				.sort(registerItems, new NetImportanceComparatorDescending());
	}
}