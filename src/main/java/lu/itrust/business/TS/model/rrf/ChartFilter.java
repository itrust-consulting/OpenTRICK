/**
 * 
 */
package lu.itrust.business.TS.model.rrf;

import java.util.ArrayList;
import java.util.List;

/**
 * @author eomar
 *
 */
public class ChartFilter {
	
	private int scrollPosition = 0;
	
	private List<String> series = new ArrayList<String>();

	/**
	 * @return the series
	 */
	public List<String> getSeries() {
		return series;
	}

	/**
	 * @param series the series to set
	 */
	public void setSeries(List<String> series) {
		this.series = series;
	}

	/**
	 * @return the scrollPosition
	 */
	public int getScrollPosition() {
		return scrollPosition;
	}

	/**
	 * @param scrollPosition the scrollPosition to set
	 */
	public void setScrollPosition(int scrollPosition) {
		this.scrollPosition = scrollPosition;
	}
}
