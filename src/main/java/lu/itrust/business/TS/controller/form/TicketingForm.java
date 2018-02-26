/**
 * 
 */
package lu.itrust.business.TS.controller.form;

import java.util.LinkedList;
import java.util.List;

/**
 * @author eomar
 *
 */
public class TicketingForm {

	private List<Integer> news = new LinkedList<>();

	private List<Integer> updates = new LinkedList<>();

	/**
	 * 
	 */
	public TicketingForm() {
	}

	/**
	 * @return the news
	 */
	public List<Integer> getNews() {
		return news;
	}

	/**
	 * @param news
	 *            the news to set
	 */
	public void setNews(List<Integer> news) {
		this.news = news;
	}

	/**
	 * @return the updates
	 */
	public List<Integer> getUpdates() {
		return updates;
	}

	/**
	 * @param updates
	 *            the updates to set
	 */
	public void setUpdates(List<Integer> updates) {
		this.updates = updates;
	}

	public int size() {
		return (news == null ? 0 : news.size()) + (updates == null ? 0 : updates.size());
	}

	public void clear() {
		if (news != null)
			news.clear();
		if (updates != null)
			updates.clear();

	}
}
