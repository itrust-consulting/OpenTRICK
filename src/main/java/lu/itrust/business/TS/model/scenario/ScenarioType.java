package lu.itrust.business.TS.model.scenario;

import java.util.ArrayList;
import java.util.List;

import lu.itrust.business.TS.exception.TrickException;

/**
 * ScenarioType: <br>
 * Represents the Scenario Type with a Name.
 * 
 * @author itrust consulting s.à .r.l. : EOM, BJA, SME
 * @version 0.1
 * @since 23 janv. 2013
 */
public enum ScenarioType {

	Confidentiality(1), Integrity(2), Availability(3), Exploitability(4), Reliability(5), ILR(6), Direct1(7), Direct2(8), Direct3(9), Direct4(10), Direct5(11), Direct6(12), Direct6_1(
			13), Direct6_2(14), Direct6_3(15), Direct6_4(16), Direct7(17), Indirect1(18), Indirect2(19), Indirect3(20), Indirect4(21), Indirect5(
					22), Indirect6(23), Indirect7(24), Indirect8(25), Indirect8_1(26), Indirect8_2(27), Indirect8_3(28), Indirect8_4(29), Indirect9(30), Indirect10(31);

	public static final String[] NAMES = new String[] { "Confidentiality", "Integrity", "Availability", "Exploitability", "Reliability", "ILR", "D1-Strat", "D2-RH", "D3-Processus",
			"D4-BCM", "D5-Soustrait", "D6-SI", "D6.1-Secu", "D6.2-Dev", "D6.3-Expl", "D6.4-Support", "D7-Aut", "I1-Strat", "I2-Fin", "I3-Leg", "I4-RH", "I5-Processus", "I6-BCM",
			"I7-Soustrait", "I8-SI", "I8.1-Secu", "I8.2-Dev", "I8.3-Expl", "I8.4-Support", "I9-Prest", "I10-Aut" };

	public static final String[] JAVAKEYS = new String[] { "Confidentiality", "Integrity", "Availability", "Exploitability", "Reliability", "ILR", "Direct1", "Direct2", "Direct3",
			"Direct4", "Direct5", "Direct6", "Direct6.1", "Direct6.2", "Direct6.3", "Direct6.4", "Direct7", "Indirect1", "Indirect2", "Indirect3", "Indirect4", "Indirect5",
			"Indirect6", "Indirect7", "Indirect8", "Indirect8.1", "Indirect8.2", "Indirect8.3", "Indirect8.4", "Indirect9", "Indirect10" };

	public static final String[] CSSF_KEYS = new String[] { "Direct1", "Direct2", "Direct3", "Direct4", "Direct5", "Direct6", "Direct6.1", "Direct6.2", "Direct6.3", "Direct6.4",
			"Direct7", "Indirect1", "Indirect2", "Indirect3", "Indirect4", "Indirect5", "Indirect6", "Indirect7", "Indirect8", "Indirect8.1", "Indirect8.2", "Indirect8.3",
			"Indirect8.4", "Indirect9", "Indirect10" };

	public static final String[] CIA_KEYS = new String[] { "Confidentiality", "Integrity", "Availability", "Exploitability", "Reliability", "ILR" };

	/** value */
	private int value;

	/**
	 * Constructor:<br>
	 * 
	 * @param value
	 *            The value to set the ActionPlanMode
	 */
	private ScenarioType(int value) {
		this.value = value;
	}

	/**
	 * getValue: <br>
	 * Returns the Value of the ActionPlanMode
	 * 
	 * @return The Value of the ActionPlanMode
	 */
	public int getValue() {
		return value;
	}

	public boolean isDirect() {
		return value >= 7 && value <= 17;
	}

	public boolean isIndirect() {
		return value >= 18 && value <= 31;
	}

	/**
	 * Confidentiality : 0, Integrity: 1, Availability: 2, Exploitability: 3,Reliability:4, IRL: 5, Direct: 6, Indirect: 7  
	 * @return main group id
	 */
	public int getGroup() {
		if (isIndirect())
			return 7;
		else if (isDirect())
			return 6;
		else
			return value - 1;
	}

	/**
	 * valueOf: <br>
	 * Description
	 * 
	 * @param value
	 * @return
	 * @throws TrickException
	 */
	public static ScenarioType valueOf(int value) throws TrickException {
		ScenarioType[] values = values();
		if (value < 1 || value > values.length)
			throw new TrickException("error.standard_type.invalid", "Scenario type not valid");
		return values[value - 1];
	}

	/**
	 * getAllCIA: <br>
	 * Description
	 * 
	 * @return
	 */
	public static List<ScenarioType> getAllCIA() {
		List<ScenarioType> result = new ArrayList<>();
		ScenarioType[] values = values();
		for (int i = 0; i < CIA_KEYS.length; i++)
			result.add(values[i]);
		return result;
	}

	/**
	 * getAllCssf: <br>
	 * Description
	 * 
	 * @return
	 */
	public static List<ScenarioType> getAllCSSF() {
		List<ScenarioType> result = new ArrayList<>();
		ScenarioType[] values = values();
		for (int i = CIA_KEYS.length; i < values.length; i++)
			result.add(values[i]);
		return result;
	}

	/**
	 * getAll: <br>
	 * Description
	 * 
	 * @return
	 */
	public static List<ScenarioType> getAll() {
		List<ScenarioType> result = new ArrayList<>();
		ScenarioType[] values = values();
		for (int i = 0; i < values.length; i++)
			result.add(values[i]);
		return result;
	}

	public String getName() {
		return NAMES[this.value - 1];
	}
	
	public String getCategory() {
		return JAVAKEYS[this.value - 1];
	}

	public static ScenarioType getByName(String name) {
		ScenarioType[] values = values();
		for (int i = 0; i < values.length; i++)
			if (values[i].name().equalsIgnoreCase(name.trim()) || NAMES[i].equalsIgnoreCase(name))
				return values[i];
		return null;
	}
}
