package lu.itrust.business.ts.model.general.helper;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

public class PhaseForm {
	
	private int id;
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date begin;
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date end;
	
	private int number;
	
	private boolean beginEnabled;
	
	private boolean endEnabled = true;
	
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

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
	
}
