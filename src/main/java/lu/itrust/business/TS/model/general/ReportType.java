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

	public static String getCodeName(ReportType type) {
		switch (type) {
		case STA:
			return "report";
		case RISK_SHEET:
		case RISK_REGISTER:
		case RISK_SHEET_RAW:
			return type.name().toLowerCase();
		}
		return null;
	}

	public static String getDisplayName(ReportType type) {
		switch (type) {
		case STA:
			return "Report";
		case RISK_SHEET:
			return "Risk sheets";
		case RISK_SHEET_RAW:
			return "Risk Sheets (raw)";
		case RISK_REGISTER:
			return "Risk Register";
		}
		return null;
		
	}
}
