package lu.itrust.business.TS.model.cssf.tools;

import java.util.Comparator;

import lu.itrust.business.TS.model.cssf.RiskRegisterItem;

/** 
 * RiskRegisterItemComparator: <br>
 * Abstract class to compare two RiskRegisterItems on thier Importance value.
 *
 * @author itrust consulting s.�.rl. : BJA, EOM, SME
 * @version 0.1
 * @since 27 d�c. 2012
 * 
 * @see {@link lu.itrust.business.TS.model.cssf.tools.NetImportanceComparatorDescending NetImportanceComparatorDescending}
 * @see {@link lu.itrust.business.TS.model.cssf.tools.NetImportanceComparatorAscending NetImportanceComparatorAscending}
 */
public abstract class RiskRegisterItemComparator implements Comparator<RiskRegisterItem> {}