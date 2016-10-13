package lu.itrust.business.TS.model.cssf.tools;

import java.util.ArrayList;
import java.util.List;

import lu.itrust.business.TS.model.cssf.RiskRegisterItem;
import lu.itrust.business.TS.model.cssf.helper.CSSFFilter;
import lu.itrust.business.TS.model.cssf.helper.ComputationHelper;

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

	/**
	 * acceptable net probability to verify on risk register list item adding
	 */
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
	 * selectedByProbabilityAndImpactAndIndexing: <br>
	 * Indexing RiskRegisterItem and remove all item with (net impact <
	 * impactMin or net pro < probaMin).
	 * 
	 * @param registerItems
	 *            The Items List to check
	 * @param impactMin
	 *            The minimum Impact value to be valid
	 * @param probaMin
	 *            The minimum Probability value to be valid
	 * @param startIndex
	 *            The start index inside the List
	 * 
	 */
	public static void removeImproper(List<RiskRegisterItem> registerItems, int limit, double impactMin, double probaMin) {
		// parse all risk register items given as parameter
		for (int i = 0; i < registerItems.size();) {
			// retrieve probability value
			double pro = registerItems.get(i).getNetEvaluation().getProbability();
			// retrieve impact value
			double impact = registerItems.get(i).getNetEvaluation().getImpact();
			// check if probability and impact are not acceptable
			if (i < limit || (impact >= impactMin && pro >= probaMin))
				i++;
			else
				registerItems.remove(i);
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

	public static List<RiskRegisterItem> sortAndConcatenate(ComputationHelper helper, CSSFFilter cssfFilter) {
		int cia = cssfFilter.getCia(), direct = cssfFilter.getDirect(), inderect = cssfFilter.getIndirect();
		List<RiskRegisterItem> riskRegisterItems = new ArrayList<>();
		helper.getRiskRegisters().values().stream().sorted(new NetImportanceComparator().reversed()).forEach(riskRegister -> {
			switch (findGroup(riskRegister.getScenario().getType().getName())) {
			case DIRECT:
				if (direct == -1 || direct > -1 && (cssfFilter.getDirect() > 0 || riskRegister.isCompliant(cssfFilter.getImpact(), cssfFilter.getProbability()))) {
					riskRegisterItems.add(riskRegister);
					if (direct > 0)
						cssfFilter.setDirect(cssfFilter.getDirect() - 1);
				}
				break;
			case INDIRECT:
				if (inderect == -1 || inderect > -1 && (cssfFilter.getIndirect() > 0 || riskRegister.isCompliant(cssfFilter.getImpact(), cssfFilter.getProbability()))) {
					riskRegisterItems.add(riskRegister);
					if (inderect > 0)
						cssfFilter.setIndirect(cssfFilter.getIndirect() - 1);
				}
				break;
			default:
				if (cia == -1 || cia > -1 && (cssfFilter.getCia() > 0 || riskRegister.isCompliant(cssfFilter.getImpact(), cssfFilter.getProbability()))) {
					riskRegisterItems.add(riskRegister);
					if (cia > 0)
						cssfFilter.setCia(cssfFilter.getCia() - 1);
				}
				break;
			}
		});
		return riskRegisterItems;
	}
}