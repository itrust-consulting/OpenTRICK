/**
 * 
 */
package lu.itrust.business.TS.exportation.word.impl.docx4j.helper;

import lu.itrust.business.TS.model.analysis.AnalysisReportSetting;

/**
 * @author eomar
 *
 */
public class ColorSet {

	private String dark;

	private String normal;

	private String light;

	private String zeroCost;

	private String cell;

	/**
	 * 
	 */
	public ColorSet() {
		this(AnalysisReportSetting.DARK_COLOR.getValue(), AnalysisReportSetting.DEFAULT_COLOR.getValue(), AnalysisReportSetting.LIGHT_COLOR.getValue(),
				AnalysisReportSetting.ZERO_COST_COLOR.getValue(), AnalysisReportSetting.CEEL_COLOR.getValue());
	}

	public ColorSet(String dark, String normal, String light) {
		this(dark, normal, light, AnalysisReportSetting.ZERO_COST_COLOR.getValue(), AnalysisReportSetting.CEEL_COLOR.getValue());
	}

	public ColorSet(String dark, String normal, String light, String zeroCost, String cell) {
		this.dark = dark;
		this.normal = normal;
		this.light = light;
		this.zeroCost = zeroCost;
		this.cell = cell;
	}

	public String getDark() {
		return dark;
	}

	public void setDark(String dark) {
		this.dark = dark;
	}

	public String getNormal() {
		return normal;
	}

	public void setNormal(String normal) {
		this.normal = normal;
	}

	public String getLight() {
		return light;
	}

	public void setLight(String light) {
		this.light = light;
	}

	public String getZeroCost() {
		return zeroCost;
	}

	public void setZeroCost(String zeroCost) {
		this.zeroCost = zeroCost;
	}

	public String getCell() {
		return cell;
	}

	public void setCell(String cell) {
		this.cell = cell;
	}

}
