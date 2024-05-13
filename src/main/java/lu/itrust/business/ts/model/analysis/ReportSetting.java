package lu.itrust.business.ts.model.analysis;

import java.io.Serializable;

/**
 * The ReportSetting enum represents different settings for generating reports.
 */
public enum ReportSetting implements Serializable {

	DARK_COLOR("CCC0D9"), 
	DEFAULT_COLOR("E5DFEC"), 
	LIGHT_COLOR("e7e2ed"), 
	ZERO_COST_COLOR("e6b8b7"), 
	CEEL_COLOR("FFFFF");

	private String value;

	/**
	 * Constructs a ReportSetting enum with the specified value.
	 * 
	 * @param value the value associated with the setting
	 */
	private ReportSetting(String value) {
		setValue(value);
	}

	/**
	 * Returns the value associated with the setting.
	 * 
	 * @return the value associated with the setting
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value associated with the setting.
	 * 
	 * @param value the value to be set
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
