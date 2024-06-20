/**
 * 
 */
package lu.itrust.business.ts.helper;

import java.util.LinkedList;
import java.util.List;

/**
 * @author eomar
 *
 */
public class Column {

	private String name;

	private List<Column> childs = new LinkedList<>();

	/**
	 * 
	 */
	public Column() {
	}

	/**
	 * @param name
	 */
	public Column(String name) {
		this.name = name;
	}

	/**
	 * @param name
	 * @param childs
	 */
	public Column(String name, List<Column> childs) {
		this.name = name;
		this.childs = childs;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Column> getChilds() {
		return childs;
	}

	public void setChilds(List<Column> childs) {
		this.childs = childs;
	}

	public int getCount() {
		return Math.max(childs.size(), childs.stream().mapToInt(e -> e.getCount()).max().orElse(1));
	}

	public int getDeep() {
		return computeDeep(1, childs);
	}

	private int computeDeep(int deep, List<Column> childs) {
		return childs.stream().mapToInt(c -> c.computeDeep(deep + 1, c.childs)).max().orElse(deep);
	}

}
