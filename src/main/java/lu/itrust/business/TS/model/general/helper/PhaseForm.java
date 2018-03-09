package lu.itrust.business.TS.model.general.helper;

import java.util.Date;

public class PhaseForm {
	
	private int id;
	
	private Date begin;
	
	private Date end;
	
	private boolean beginEnabled;
	
	private boolean endEnabled;
	
	public PhaseForm() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getBegin() {
		return begin;
	}

	public void setBegin(Date begin) {
		this.begin = begin;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public boolean isBeginEnabled() {
		return beginEnabled;
	}

	public void setBeginEnabled(boolean beginEnabled) {
		this.beginEnabled = beginEnabled;
	}

	public boolean isEndEnabled() {
		return endEnabled;
	}

	public void setEndEnabled(boolean endEnabled) {
		this.endEnabled = endEnabled;
	}
	
}
