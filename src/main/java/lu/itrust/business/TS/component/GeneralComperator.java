package lu.itrust.business.TS.component;

/**
 * GeneralComperator.java: <br>
 * Detailed description...
 *
 * @author smenghi, itrust consulting s.à.rl.
 * @version
 * @since May 9, 2014
 */
public class GeneralComperator {
	private static int toInt(String version) {
		try {
			if (version.isEmpty() || version.equals("M"))
				return 0;
			return Integer.parseInt(version);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * VersionComparator: <br>
	 * <ul>
	 * <li>versionA > versionB : 1</li>
	 * <li>versionA < versionB : -1</li>
	 * <li>versionA = versionB : 0</li>
	 * </ul>
	 * 
	 * @param versionA
	 * @param versionB
	 * @return
	 */
	public static final int VersionComparator(String versionA, String versionB) {
		String[] versionsA = versionA.split("\\.", 2);
		String[] versionsB = versionB.split("\\.", 2);
		int valueA = toInt(versionsA[0]);
		int valueB = toInt(versionsB[0]);
		if (valueA > valueB)
			return 1;
		else if (valueA < valueB)
			return -1;
		else if (valueA == valueB && (versionsA.length == 1 && versionsB.length == 1))
			return 0;
		return VersionComparator(versionsA.length > 1 ? versionsA[1] : "", versionsB.length > 1 ? versionsB[1] : "");
	}
}
