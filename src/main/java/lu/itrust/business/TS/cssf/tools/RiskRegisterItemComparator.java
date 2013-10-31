package lu.itrust.business.TS.cssf.tools;

import java.util.Comparator;

import lu.itrust.business.TS.cssf.RiskRegisterItem;

/** 
 * RiskRegisterItemComparator: <br>
 * Abstract class to compare two RiskRegisterItems on thier Importance value.
 *
 * @author itrust consulting s.à.rl. : BJA, EOM, SME
 * @version 0.1
 * @since 27 déc. 2012
 * 
 * @see {@link lu.itrust.business.TS.cssf.tools.NetImportanceComparatorDescending NetImportanceComparatorDescending}
 * @see {@link lu.itrust.business.TS.cssf.tools.NetImportanceComparatorAscending NetImportanceComparatorAscending}
 */
public abstract class RiskRegisterItemComparator implements Comparator<RiskRegisterItem> {}