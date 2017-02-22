package lu.itrust.business.TS.component.chartJS;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(value = Include.NON_EMPTY)
public class Chart {

	private List<Dataset<?>> datasets = new ArrayList<>();
	
	private List<String> labels = new ArrayList<>();
	
	private List<Legend> legends = new ArrayList<>();

	private Object settings = null;

	private String title = null;

	private Object trickId = null;

	private List<String> xLabels = new ArrayList<>();
	
	private String xTitle;
	
	private List<String> yLabels = new ArrayList<>();
	
	private String yTitle;

	/**
	 * 
	 */
	public Chart() {
	}

	/**
	 * @param trickId
	 * @param title
	 */
	public Chart(Object trickId, String title) {
		this.trickId = trickId;
		this.title = title;
	}
	
	/**
	 * @param title
	 */
	public Chart(String title) {
		this.title = title;
	}

	/**
	 * @return the datasets
	 */
	public List<Dataset<?>> getDatasets() {
		return datasets;
	}

	/**
	 * @return the labels
	 */
	public List<String> getLabels() {
		return labels;
	}

	/**
	 * @return the legends
	 */
	public List<Legend> getLegends() {
		return legends;
	}

	/**
	 * @return the settings
	 */
	public Object getSettings() {
		return settings;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the trickId
	 */
	public Object getTrickId() {
		return trickId;
	}

	/**
	 * @return the xlabels
	 */
	@JsonProperty("xLabels")
	public List<String> getXLabels() {
		return xLabels;
	}

	/**
	 * @return the xTitle
	 */
	@JsonProperty("xTitle")
	public String getXTitle() {
		return xTitle;
	}

	/**
	 * @return the ylabels
	 */
	@JsonProperty("yLabels")
	public List<String> getYLabels() {
		return yLabels;
	}

	/**
	 * @return the yTitle
	 */
	@JsonProperty("yTitle")
	public String getYTitle() {
		return yTitle;
	}

	/**
	 * @param datasets
	 *            the datasets to set
	 */
	public void setDatasets(List<Dataset<?>> datasets) {
		this.datasets = datasets;
	}

	/**
	 * @param labels
	 *            the labels to set
	 */
	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	/**
	 * @param legends
	 *            the legends to set
	 */
	public void setLegends(List<Legend> legends) {
		this.legends = legends;
	}

	/**
	 * @param settings the settings to set
	 */
	public void setSettings(Object settings) {
		this.settings = settings;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @param trickId the trickId to set
	 */
	public void setTrickId(Object trickId) {
		this.trickId = trickId;
	}

	/**
	 * @param xLabels
	 *            the xLabels to set
	 */
	public void setXLabels(List<String> xLabels) {
		this.xLabels = xLabels;
	}

	/**
	 * @param xTitle the xTitle to set
	 */
	public void setXTitle(String xTitle) {
		this.xTitle = xTitle;
	}

	/**
	 * @param yLabels
	 *            the yLabels to set
	 */
	public void setYLabels(List<String> yLabels) {
		this.yLabels = yLabels;
	}

	/**
	 * @param yTitle the yTitle to set
	 */
	public void setYTitle(String yTitle) {
		this.yTitle = yTitle;
	}
}
