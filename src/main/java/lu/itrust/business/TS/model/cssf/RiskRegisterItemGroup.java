/**
 * 
 */
package lu.itrust.business.TS.model.cssf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author oensuifudine
 * 
 */
public class RiskRegisterItemGroup {

	private int idScenario = -1;

	private int position = 0;

	private double netImportance = 0;

	private double expectedImportance = 0;

	private double rawImportance = 0;

	private List<RiskRegisterItem> registers = new ArrayList<>();

	/**
	 * @param idScenario
	 */
	public RiskRegisterItemGroup(int idScenario) {
		this.idScenario = idScenario;
	}

	/**
	 * @param e
	 * @return
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean add(RiskRegisterItem registerItem) {
		if (registerItem == null)
			return false;
		else if (contains(registerItem))
			return true;
		addItem(registerItem);
		return registers.add(registerItem);

	}

	private void addItem(RiskRegisterItem registerItem) {
		setNetImportance(getNetImportance() + registerItem.getNetEvaluation().getImportance());
		setExpectedImportance(getExpectedImportance() + registerItem.getExpectedEvaluation().getImportance());
		setRawImportance(getRawImportance() + registerItem.getRawEvaluation().getImportance());
	}

	/**
	 * 
	 * @see java.util.List#clear()
	 */
	public void clear() {
		setNetImportance(0);
		setExpectedImportance(0);
		setRawImportance(0);
		registers.clear();
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.List#contains(java.lang.Object)
	 */
	public boolean contains(Object o) {
		return registers.contains(o);
	}

	/**
	 * @param index
	 * @return
	 * @see java.util.List#get(int)
	 */
	public RiskRegisterItem get(int index) {
		return registers.get(index);
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	public int indexOf(Object o) {
		return registers.indexOf(o);
	}

	/**
	 * @return
	 * @see java.util.List#isEmpty()
	 */
	public boolean isEmpty() {
		return registers.isEmpty();
	}

	/**
	 * @return
	 * @see java.util.List#iterator()
	 */
	public Iterator<RiskRegisterItem> iterator() {
		return registers.iterator();
	}

	/**
	 * @return
	 * @see java.util.List#listIterator()
	 */
	public ListIterator<RiskRegisterItem> listIterator() {
		return registers.listIterator();
	}

	/**
	 * @param index
	 * @return
	 * @see java.util.List#listIterator(int)
	 */
	public ListIterator<RiskRegisterItem> listIterator(int index) {
		return registers.listIterator(index);
	}

	/**
	 * @param index
	 * @return
	 * @see java.util.List#remove(int)
	 */
	public RiskRegisterItem remove(int index) {
		return registers.remove(index);
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.List#remove(java.lang.Object)
	 */
	public boolean remove(Object o) {
		if (registers.remove(o)) {
			RiskRegisterItem registerItem = (RiskRegisterItem) o;
			removeItem(registerItem);
			return true;
		}
		return false;
	}

	/**
	 * @param index
	 * @param element
	 * @return
	 * @see java.util.List#set(int, java.lang.Object)
	 */
	public RiskRegisterItem set(int index, RiskRegisterItem element) {
		RiskRegisterItem registerItem = registers.set(index, element);
		if (registerItem != null)
			removeItem(registerItem);
		addItem(element);
		return registerItem;
	}

	/**
	 * @return
	 * @see java.util.List#size()
	 */
	public int size() {
		return registers.size();
	}

	public int getIdScenario() {
		return idScenario;
	}

	public void setIdScenario(int idScenario) {
		this.idScenario = idScenario;
	}

	public List<RiskRegisterItem> getRegisters() {
		return registers;
	}

	public void setRegisters(List<RiskRegisterItem> registers) {
		this.registers = registers;
	}

	public double getNetImportance() {
		return netImportance;
	}

	public void setNetImportance(double netImportance) {
		this.netImportance = netImportance;
	}

	public void setPosition(int i) {
		for (RiskRegisterItem registerItem : registers)
			registerItem.setPosition(i);
		this.position = i;

	}

	public int getPosition() {
		return position;
	}

	/**
	 * @return the expectedImportance
	 */
	public double getExpectedImportance() {
		return expectedImportance;
	}

	/**
	 * @param expectedImportance
	 *            the expectedImportance to set
	 */
	public void setExpectedImportance(double expectedImportance) {
		this.expectedImportance = expectedImportance;
	}

	/**
	 * @return the rawImportance
	 */
	public double getRawImportance() {
		return rawImportance;
	}

	/**
	 * @param rawImportance
	 *            the rawImportance to set
	 */
	public void setRawImportance(double rawImportance) {
		this.rawImportance = rawImportance;
	}
	
	/**
	 * @param registerItem
	 */
	private void removeItem(RiskRegisterItem registerItem) {
		setNetImportance(getNetImportance() - registerItem.getNetEvaluation().getImportance());
		setExpectedImportance(getExpectedImportance() - registerItem.getExpectedEvaluation().getImportance());
		setRawImportance(getRawImportance() - registerItem.getRawEvaluation().getImportance());
	}

}
