/**
 * 
 */
package lu.itrust.business.TS.helper.chartJS.model;

/**
 * @author eomar
 *
 */
public class Point {
	
	private Object x;
	
	private Object y;
	
	private boolean end;

	public Point() {
	}

	public Point(Object x, Object y) {
		this.x = x;
		this.y = y;
	}
	
	public Point(Object x, Object y, boolean end) {
		this(x, y);
		this.end = end;
	}

	public boolean isEnd() {
		return end;
	}

	public void setEnd(boolean end) {
		this.end = end;
	}

	public Object getX() {
		return x;
	}

	public void setX(Object x) {
		this.x = x;
	}

	public Object getY() {
		return y;
	}

	public void setY(Object y) {
		this.y = y;
	}

}
