/**
 * 
 */
package lu.itrust.business.TS.helper;

/**
 * @author eomar
 *
 */
public class Task {

	private String id;

	private int progress;

	private int minProgress;

	private int maxProgress;

	public Task() {
	}

	public Task(String id, int minProgress, int maxProgress) {
		this.id = id;
		this.maxProgress = maxProgress;
		this.progress = this.minProgress = minProgress;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public int getMinProgress() {
		return minProgress;
	}

	public void setMinProgress(int minProgress) {
		this.minProgress = minProgress;
	}

	public int getMaxProgress() {
		return maxProgress;
	}

	public void setMaxProgress(int maxProgress) {
		this.maxProgress = maxProgress;
	}

	public int update(int index, int size) {
		return progress = (minProgress + (index * (maxProgress - minProgress) / size));
	}

}
