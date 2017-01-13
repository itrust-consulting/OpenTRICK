package lu.itrust.business.TS.component.chartJS;

import java.util.LinkedList;
import java.util.List;

public class Chart {
	
	private List<String> labels = new LinkedList<String>();
	
	private List<Dataset> datasets = new LinkedList<>();
	
	private List<Legend> legends = new LinkedList<>();
	/**
	 * @return the labels
	 */
	public List<String> getLabels() {
		return labels;
	}
	/**
	 * @param labels the labels to set
	 */
	public void setLabels(List<String> labels) {
		this.labels = labels;
	}
	/**
	 * @return the datasets
	 */
	public List<Dataset> getDatasets() {
		return datasets;
	}
	/**
	 * @param datasets the datasets to set
	 */
	public void setDatasets(List<Dataset> datasets) {
		this.datasets = datasets;
	}
	/**
	 * @return the legends
	 */
	public List<Legend> getLegends() {
		return legends;
	}
	/**
	 * @param legends the legends to set
	 */
	public void setLegends(List<Legend> legends) {
		this.legends = legends;
	}
}
