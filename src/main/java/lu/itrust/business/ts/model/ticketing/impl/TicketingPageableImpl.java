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
 * @author eomar
 * @param <T>
 *
 */
public class TicketingPageableImpl<E> implements TicketingPageable<E> {

	private List<E> content;

	private int maxSize;

	private int offset;

	/**
	 * 
	 */
	public TicketingPageableImpl() {
		this(0, 0, new LinkedList<>());
	}

	public TicketingPageableImpl(int maxSize) {
		this(0, maxSize, new ArrayList<>(maxSize));

	}

	public TicketingPageableImpl(int offSet, int maxSize) {
		this(offSet, maxSize, new ArrayList<>(maxSize));

	}

	public TicketingPageableImpl(int offSet, int maxSize, List<E> content) {
		setContent(content);
		setMaxSize(maxSize);
		setOffset(offSet);
	}

	@Override
	public List<E> getContent() {
		return content;
	}

	@Override
	public int getMaxSize() {
		return maxSize;
	}

	public void setContent(List<E> content) {
		this.content = content;
	}

	public void setMaxSize(int size) {
		this.maxSize = size < 0 ? 0 : size;
	}

	@Override
	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset < 0 ? 0 : offset;
	}

	@Override
	public int size() {
		return content.size();
	}

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
