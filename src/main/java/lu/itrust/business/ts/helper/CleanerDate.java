package lu.itrust.business.ts.helper;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * 
 * @author eomar
 *
 */
public class CleanerDate {

	private int year;

	private int month;

	private int day;

	private int hour;

	private int minute;

	private int second;

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		this.second = second;
	}

	public boolean isEmpty() {
		return year < 1 && month < 1 && day < 1 && hour < 1 && minute < 1 && second < 1;
	}

	public Date getDate() {
		Calendar calendar = Calendar.getInstance();
		if (!isEmpty()) {
			calendar.add(Calendar.YEAR, getYear() * -1);
			calendar.add(Calendar.MONTH, getMonth() * -1);
			calendar.add(Calendar.DATE, getDay() * -1);
			calendar.add(Calendar.HOUR, getHour() * -1);
			calendar.add(Calendar.MINUTE, getMinute() * -1);
			calendar.add(Calendar.SECOND, getSecond() * -1);
		}
		return calendar.getTime();
	}

	@Override
	public String toString() {
		return "CleanerDate [year=" + year + ", month=" + month + ", day=" + day + ", hour=" + hour + ", minute=" + minute + ", second=" + second + "]";
	}

	public static final  CleanerDate parse(String data) {
		CleanerDate date = new CleanerDate();
		if (data == null)
			return date;
		final AtomicInteger pointer = new AtomicInteger(0);
		while (pointer.get() < data.length()) {
			Character c = data.charAt(pointer.get());
			if (Character.isDigit(c))
				readNumber(date, data, pointer);
		}
		return date;

	}

	private static void readNumber(CleanerDate date, String data, AtomicInteger pointer) {
		int index = pointer.get(), value = 0, c = 0;
		while (Character.isDigit(c = data.charAt(index++)) && data.length() > index)
			value = value * 10 + Character.digit(c, 10);
		switch (c) {
		case 'y':
		case 'Y':
			date.setYear(value);
			break;
		case 'm':
			date.setMinute(value);
			break;
		case 'M':
			date.setMonth(value);
			break;
		case 'd':
		case 'D':
			date.setDay(value);
			break;
		case 'h':
		case 'H':
			date.setHour(value);
			break;
		case 's':
		case 'S':
			date.setSecond(value);
			break;
		}
		pointer.set(index);
	}

}
