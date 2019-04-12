package lu.itrust.business.TS.model.analysis;

public enum AnalysisReportSetting {
	
	DARK_COLOR("CCC0D9"), DEFAULT_COLOR("E5DFEC"), LIGHT_COLOR("e7e2ed"), ZERO_COST_COLOR("e6b8b7"), CEEL_COLOR("FFFFF");

	private String value;

	private AnalysisReportSetting(String value) {
		setValue(value);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
