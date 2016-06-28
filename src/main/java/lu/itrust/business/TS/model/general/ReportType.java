package lu.itrust.business.TS.model.general;

public enum ReportType {
	STA, RISK_SHEET, RISK_SHEET_RAW, RISK_REGISTER;

	public static String getExtension(ReportType type) {
		switch (type) {
		case STA:
		case RISK_SHEET:
			return "docm";
		case RISK_REGISTER:
			return "docx";
		case RISK_SHEET_RAW:
			return "xlsx";
		}
		return null;
	}
}
