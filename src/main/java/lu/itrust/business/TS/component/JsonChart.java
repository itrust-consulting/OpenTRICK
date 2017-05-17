/**
 * 
 */
package lu.itrust.business.TS.component;

/**
 * @author eomar
 *
 */
@Deprecated
public class JsonChart {

	private String chart;

	private String title;
	
	private String legend;

	private String pane;

	private String plotOptions;

	private String tooltip;

	private String xAxis;
	
	private String yAxis;

	private String series;

	private String exporting;

	/**
	 * 
	 */
	public JsonChart() {
	}

	/**
	 * @param chart
	 * @param title
	 * @param pane
	 * @param legend
	 */
	public JsonChart(String chart, String title, String pane, String legend) {
		this.chart = chart;
		this.title = title;
		this.pane = pane;
		this.legend = legend;
	}

	/**
	 * @return the chart
	 */
	public String getChart() {
		return chart;
	}

	/**
	 * @param chart
	 *            the chart to set
	 */
	public void setChart(String chart) {
		this.chart = chart;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the pane
	 */
	public String getPane() {
		return pane;
	}

	/**
	 * @param pane
	 *            the pane to set
	 */
	public void setPane(String pane) {
		this.pane = pane;
	}

	/**
	 * @return the plotOptions
	 */
	public String getPlotOptions() {
		return plotOptions;
	}

	/**
	 * @param plotOptions
	 *            the plotOptions to set
	 */
	public void setPlotOptions(String plotOptions) {
		this.plotOptions = plotOptions;
	}

	/**
	 * @return the tooltip
	 */
	public String getTooltip() {
		return tooltip;
	}

	/**
	 * @param tooltip
	 *            the tooltip to set
	 */
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	/**
	 * @return the xAxis
	 */
	public String getxAxis() {
		return xAxis;
	}

	/**
	 * @param xAxis
	 *            the xAxis to set
	 */
	public void setxAxis(String xAxis) {
		this.xAxis = xAxis;
	}

	/**
	 * @return the series
	 */
	public String getSeries() {
		return series;
	}

	/**
	 * @param series
	 *            the series to set
	 */
	public void setSeries(String series) {
		this.series = series;
	}

	
	/**
	 * @return the exporting
	 */
	public String getExporting() {
		return exporting;
	}

	/**
	 * @param exporting
	 *            the exporting to set
	 */
	public void setExporting(String exporting) {
		this.exporting = exporting;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (title != null)
			chart += "," + title;
		
		if(legend !=null)
			chart += "," + legend;
		
		if (pane != null)
			chart += "," + pane;
		
		if (plotOptions != null)
			chart += "," + plotOptions;
		
		if (tooltip != null)
			chart += "," + tooltip;
		
		if (xAxis != null)
			chart += "," + xAxis;
		
		if (yAxis != null)
			chart += "," + yAxis;
		
		if (series != null)
			chart += "," + series;
		
		if (exporting != null)
			chart += "," + exporting;
		
		return ("{" + chart + "}").replaceAll("\r|\n", " ");

	}

	/**
	 * @return the yAxis
	 */
	public String getyAxis() {
		return yAxis;
	}

	/**
	 * @param yAxis the yAxis to set
	 */
	public void setyAxis(String yAxis) {
		this.yAxis = yAxis;
	}

}
