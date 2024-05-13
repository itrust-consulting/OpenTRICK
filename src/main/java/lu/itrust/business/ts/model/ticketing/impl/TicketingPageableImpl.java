/**
 * 
 */
package lu.itrust.business.ts.model.ticketing.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import lu.itrust.business.ts.model.ticketing.TicketingPageable;


/**
 * Implementation of the TicketingPageable interface.
 * This class represents a pageable list of elements.
 *
 * @param <E> the type of elements in the list
 */
public class TicketingPageableImpl<E> implements TicketingPageable<E> {

	private List<E> content;
	private int maxSize;
	private int offset;

	/**
	 * Constructs an empty TicketingPageableImpl object.
	 * The content list is initialized as an empty LinkedList.
	 * The maxSize and offset are set to 0.
	 */
	public TicketingPageableImpl() {
		this(0, 0, new LinkedList<>());
	}

	/**
	 * Constructs a TicketingPageableImpl object with the specified maxSize.
	 * The content list is initialized as an empty ArrayList with the specified maxSize.
	 * The offset is set to 0.
	 *
	 * @param maxSize the maximum size of the content list
	 */
	public TicketingPageableImpl(int maxSize) {
		this(0, maxSize, new ArrayList<>(maxSize));
	}

	/**
	 * Constructs a TicketingPageableImpl object with the specified offset and maxSize.
	 * The content list is initialized as an empty ArrayList with the specified maxSize.
	 *
	 * @param offset  the offset of the content list
	 * @param maxSize the maximum size of the content list
	 */
	public TicketingPageableImpl(int offset, int maxSize) {
		this(offset, maxSize, new ArrayList<>(maxSize));
	}

	/**
	 * Constructs a TicketingPageableImpl object with the specified offset, maxSize, and content list.
	 *
	 * @param offset  the offset of the content list
	 * @param maxSize the maximum size of the content list
	 * @param content the list of elements
	 */
	public TicketingPageableImpl(int offset, int maxSize, List<E> content) {
		setContent(content);
		setMaxSize(maxSize);
		setOffset(offset);
	}

	/**
	 * Returns the content list.
	 *
	 * @return the content list
	 */
	@Override
	public List<E> getContent() {
		return content;
	}

	/**
	 * Returns the maximum size of the content list.
	 *
	 * @return the maximum size of the content list
	 */
	@Override
	public int getMaxSize() {
		return maxSize;
	}

	/**
	 * Sets the content list.
	 *
	 * @param content the list of elements
	 */
	public void setContent(List<E> content) {
		this.content = content;
	}

	/**
	 * Sets the maximum size of the content list.
	 * If the specified size is negative, the maxSize is set to 0.
	 *
	 * @param size the maximum size of the content list
	 */
	public void setMaxSize(int size) {
		this.maxSize = size < 0 ? 0 : size;
	}

	/**
	 * Returns the offset of the content list.
	 *
	 * @return the offset of the content list
	 */
	@Override
	public int getOffset() {
		return offset;
	}

	/**
	 * Sets the offset of the content list.
	 * If the specified offset is negative, the offset is set to 0.
	 *
	 * @param offset the offset of the content list
	 */
	public void setOffset(int offset) {
		this.offset = offset < 0 ? 0 : offset;
	}

	/**
	 * Returns the size of the content.
	 *
	 * @return the size of the content
	 */
	@Override
	public int size() {
		return content.size();
	}

	/* Rest of methods */
	@Override
	public boolean isEmpty() {
		return content.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return content.contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		return content.iterator();
	}

	@Override
	public Object[] toArray() {
		return content.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return content.toArray(a);
	}

	@Override
	public boolean add(E e) {
		return content.add(e);
	}

	@Override
	public boolean remove(Object o) {
		return content.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return content.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		return content.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		return content.addAll(index, c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return content.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return content.retainAll(c);
	}

	@Override
	public void clear() {
		content.clear();
	}

	@Override
	public E get(int index) {
		return content.get(index);
	}

	@Override
	public E set(int index, E element) {
		return content.set(index, element);
	}

	@Override
	public void add(int index, E element) {
		content.add(index, element);
	}

	@Override
	public E remove(int index) {
		return content.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return content.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return content.lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator() {
		return content.listIterator();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return content.listIterator(index);
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return content.subList(fromIndex, toIndex);
	}

	@Override
	public int moveNext() {
		return increase(getMaxSize());
	}

	@Override
	public int increase(int offset) {
		setOffset(getOffset() + offset);
		return getOffset();
	}
}
