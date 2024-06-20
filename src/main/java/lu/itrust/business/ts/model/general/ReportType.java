package lu.itrust.business.ts.model.general;

import org.apache.commons.io.FilenameUtils;
import org.springframework.util.StringUtils;

/**
 * Enum representing different types of reports.
 */
public enum ReportType {
	STA, SOA, RISK_SHEET, RISK_SHEET_RAW, RISK_REGISTER;

	/**
	 * Returns the file extension associated with the given report type.
	 *
	 * @param type The report type.
	 * @return The file extension.
	 */
	public static String getExtension(ReportType type) {
		switch (type) {
			case STA:
			case SOA:
			case RISK_SHEET:
				return "docm";
			case RISK_REGISTER:
				return "docx";
			case RISK_SHEET_RAW:
				return "xlsx";
		}
		return null;
	}

	/**
	 * Returns the code name associated with the given report type.
	 *
	 * @param type The report type.
	 * @return The code name.
	 */
	public static String getCodeName(ReportType type) {
		switch (type) {
			case STA:
				return "report";
			case SOA:
			case RISK_SHEET:
			case RISK_REGISTER:
			case RISK_SHEET_RAW:
				return type.name().toLowerCase();
		}
		return null;
	}

	/**
	 * Returns the display name associated with the given report type.
	 *
	 * @param type The report type.
	 * @return The display name.
	 */
	public static String getDisplayName(ReportType type) {
		switch (type) {
			case STA:
				return "Report";
			case SOA:
				return "SOA";
			case RISK_SHEET:
				return "Risk sheets";
			case RISK_SHEET_RAW:
				return "Risk Sheets (raw)";
			case RISK_REGISTER:
				return "Risk Register";
		}
		return null;
	}

	/**
	 * Returns the file extension associated with the given report type and filename.
	 * If the filename has an extension, it is returned. Otherwise, the default extension
	 * for the report type is returned.
	 *
	 * @param type     The report type.
	 * @param filename The filename.
	 * @return The file extension.
	 */
	public static String getExtension(ReportType type, String filename) {
		String extension = FilenameUtils.getExtension(filename);
		return StringUtils.hasText(extension) ? extension : getExtension(type);
	}
}
