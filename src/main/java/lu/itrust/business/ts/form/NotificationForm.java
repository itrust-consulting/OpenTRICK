package lu.itrust.business.ts.form;

import java.util.ArrayList;
import java.util.List;

import lu.itrust.business.ts.model.general.helper.Notification;

public class NotificationForm {
	
	private List<Integer> targets = new ArrayList<>();
	
	private Notification data;
	
	
	
	/**
	 * 
	 */
	public NotificationForm() {
		this(new Notification());
	}

	public NotificationForm(Notification notification) {
		this.data = notification;
	}

	public List<Integer> getTargets() {
		return targets;
	}

	public void setTargets(List<Integer> targets) {
		this.targets = targets;
	}

	public Notification getData() {
		return data;
	}

	public void setData(Notification data) {
		this.data = data;
	}

	public boolean isAll() {
		return targets == null || targets.isEmpty();
	}

}
