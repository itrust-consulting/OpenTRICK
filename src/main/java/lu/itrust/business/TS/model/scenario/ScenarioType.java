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

	Confidentiality(1), Integrity(2), Availability(3), Direct1(4), Direct2(5), Direct3(6), Direct4(7), Direct5(8), Direct6(9), Direct6_1(10), Direct6_2(11), Direct6_3(12), Direct6_4(
			13), Direct7(14), Indirect1(15), Indirect2(16), Indirect3(17), Indirect4(18), Indirect5(19), Indirect6(20), Indirect7(21), Indirect8(22), Indirect8_1(23), Indirect8_2(
			24), Indirect8_3(25), Indirect8_4(26), Indirect9(27), Indirect10(28);

	public static final String[] NAMES = new String[] { "Confidentiality", "Integrity", "Availability", "D1-Strat", "D2-RH", "D3-Processus", "D4-BCM", "D5-Soustrait", "D6-SI",
			"D6.1-Secu", "D6.2-Dev", "D6.3-Expl", "D6.4-Support", "D7-Aut", "I1-Strat", "I2-Fin", "I3-Leg", "I4-RH", "I5-Processus", "I6-BCM", "I7-Soustrait", "I8-SI",
			"I8.1-Secu", "I8.2-Dev", "I8.3-Expl", "I8.4-Support", "I9-Prest", "I10-Aut" };

	public static final String[] JAVAKEYS = new String[] { "Confidentiality", "Integrity", "Availability", "Direct1", "Direct2", "Direct3", "Direct4", "Direct5", "Direct6",
			"Direct6.1", "Direct6.2", "Direct6.3", "Direct6.4", "Direct7", "Indirect1", "Indirect2", "Indirect3", "Indirect4", "Indirect5", "Indirect6", "Indirect7", "Indirect8",
			"Indirect8.1", "Indirect8.2", "Indirect8.3", "Indirect8.4", "Indirect9", "Indirect10" };

	public static final String[] CSSF_KEYS = new String[] { "Direct1", "Direct2", "Direct3", "Direct4", "Direct5", "Direct6", "Direct6.1", "Direct6.2", "Direct6.3", "Direct6.4",
			"Direct7", "Indirect1", "Indirect2", "Indirect3", "Indirect4", "Indirect5", "Indirect6", "Indirect7", "Indirect8", "Indirect8.1", "Indirect8.2", "Indirect8.3",
			"Indirect8.4", "Indirect9", "Indirect10" };

	public static final String[] CIA_KEYS = new String[] { "Confidentiality", "Integrity", "Availability" };

	/** value */
	private int value = 1;

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
		List<ScenarioType> result = new ArrayList<ScenarioType>();
		ScenarioType[] values = values();
		for (int i = 0; i < 3; i++)
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
		List<ScenarioType> result = new ArrayList<ScenarioType>();
		ScenarioType[] values = values();
		for (int i = 3; i < values.length; i++)
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
		List<ScenarioType> result = new ArrayList<ScenarioType>();
		ScenarioType[] values = values();
		for (int i = 0; i < values.length; i++)
			result.add(values[i]);
		return result;
	}

	public String getName() {
		return NAMES[this.value - 1];
	}

	public static ScenarioType getByName(String name) {
		ScenarioType[] values = values();
		for (int i = 0; i < values.length; i++)
			if (values[i].name().equalsIgnoreCase(name.trim()) || NAMES[i].equalsIgnoreCase(name))
				return values[i];
		return null;
	}
}
