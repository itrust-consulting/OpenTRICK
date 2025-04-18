/**
 * 
 */
package lu.itrust.business.ts.model.rrf;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a filter for a chart.
 * The filter includes the scroll position and the list of series.
 */
public class ChartFilter {
	
	private int scrollPosition = 0;
	
	private List<String> series = new ArrayList<String>();

	/**
	 * Gets the list of series.
	 * 
	 * @return the series
	 */
	public List<String> getSeries() {
		return series;
	}

	/**
	 * Sets the list of series.
	 * 
	 * @param series the series to set
	 */
	public void setSeries(List<String> series) {
		this.series = series;
	}

	/**
	 * Gets the scroll position.
	 * 
	 * @return the scrollPosition
	 */
	public int getScrollPosition() {
		return scrollPosition;
	}

	/**
	 * Sets the scroll position.
	 * 
	 * @param scrollPosition the scrollPosition to set
	 */
	public void setScrollPosition(int scrollPosition) {
		this.scrollPosition = scrollPosition;
	}
	

}
