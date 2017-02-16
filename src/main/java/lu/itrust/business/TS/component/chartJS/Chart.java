package lu.itrust.business.TS.component.chartJS;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(value = Include.NON_EMPTY)
public class Chart {

	private String title = null;
	
	private Object trickId = null;
	
	private List<String> labels = new ArrayList<>();

	private List<String> xLabels = new ArrayList<>();

	private List<String> yLabels = new ArrayList<>();

	private List<Dataset<?>> datasets = new ArrayList<>();

	private List<Legend> legends = new ArrayList<>();

	/**
	 * 
	 */
	public Chart() {
	}

	/**
	 * @param title
	 */
	public Chart(String title) {
		this.title = title;
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
	 * @return the labels
	 */
	public List<String> getLabels() {
		return labels;
	}

	/**
	 * @param labels
	 *            the labels to set
	 */
	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	/**
	 * @return the datasets
	 */
	public List<Dataset<?>> getDatasets() {
		return datasets;
	}

	/**
	 * @param datasets
	 *            the datasets to set
	 */
	public void setDatasets(List<Dataset<?>> datasets) {
		this.datasets = datasets;
	}

	/**
	 * @return the legends
	 */
	public List<Legend> getLegends() {
		return legends;
	}

	/**
	 * @param legends
	 *            the legends to set
	 */
	public void setLegends(List<Legend> legends) {
		this.legends = legends;
	}

	/**
	 * @return the xlabels
	 */
	@JsonProperty("xLabels")
	public List<String> getXLabels() {
		return xLabels;
	}

	/**
	 * @param xLabels
	 *            the xLabels to set
	 */
	public void setXLabels(List<String> xLabels) {
		this.xLabels = xLabels;
	}

	/**
	 * @return the ylabels
	 */
	@JsonProperty("yLabels")
	public List<String> getYLabels() {
		return yLabels;
	}

	/**
	 * @param yLabels
	 *            the yLabels to set
	 */
	public void setYLabels(List<String> yLabels) {
		this.yLabels = yLabels;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the trickId
	 */
	public Object getTrickId() {
		return trickId;
	}

	/**
	 * @param trickId the trickId to set
	 */
	public void setTrickId(Object trickId) {
		this.trickId = trickId;
	}
}
