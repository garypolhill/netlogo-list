/**
 * 
 */
package uk.ac.hutton.netlogo.list;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;

import java.lang.reflect.Array;

/**
 * A plain old common-or-garden list data structure, with no generics,
 * containing Objects. It is there for when you don't care about the size of the
 * list, and are only interested in the head or the tail. It is mutable and
 * iterable, and random access is slow. If you want fast random access, use a
 * different data structure!
 * 
 * This implementation has a 'front' element with a null 'head', so that the
 * first element of the list can be removed without changing the object
 * referenced for the list as a whole. The implementation is intended to disable
 * access to the objects containing other elements in the list.
 * 
 * N.B. the {@link #add(Object)} method is slow because it is assumed that you
 * will want to add something to the end of the list rather than the beginning.
 * Use {@link #unshift(Object)} if you don't mind adding things in reverse order
 * of what you want. You can {@link reverse()} the list afterwards.
 * 
 * @author Gary Polhill
 * @version 1.0, 5-Nov-2021
 */
public class MutableList implements List<Object>, Cloneable {
	private Object head; // If null, then the list is empty or this is the front
	private MutableList tail; // If null, then the list is empty or this is the last element
	private MutableList prev; // If null, then this is the front or the list is empty

	/**
	 * Constructor returning an empty 'front' of a list
	 */
	public MutableList() {
		this(null, null);
	}

	/**
	 * Convenience constructor allowing a list to be initialized with a collection
	 * 
	 * @param c the collection
	 */
	public MutableList(Collection<? extends Object> c) {
		this(null, null);
		this.addAll(c);
	}

	/**
	 * Convenience internal constructor for a non-front list member
	 * 
	 * @param head the object to store at this member of the list
	 * @param prev the previous member of the list
	 */
	private MutableList(Object head, MutableList prev) {
		this(head, prev, null);
	}

	/**
	 * Main constructor -- all constructors should end up here
	 * 
	 * @param head the object to store at this member of the list
	 * @param prev the previous member of the list
	 * @param tail the tail of this member of the list
	 */
	private MutableList(Object head, MutableList prev, MutableList tail) {
		if (head == null && tail == null) {
			if (prev != null) {
				throw new RuntimeException("BUG! ('prev' is not null when 'head' and 'tail' are)");
			}
		} else {
			if (prev == null) {
				throw new RuntimeException("BUG! ('prev' is null for non-'front' element)");
			}
			if (head == null) {
				throw new NullPointerException("Cannot put a null object in a MutableList");
			}
		}
		this.head = head;
		this.tail = tail;
		this.prev = prev;
		if (prev != null) {
			prev.tail = this;
		}
		if (tail != null) {
			tail.prev = this;
		}
	}

	/**
	 * Warning: this is an O(N) function where N is the length of the list. If
	 * you're using this class, you shouldn't really care about the size, just about
	 * the head and the tail. Use {@link #isEmpty()} instead of
	 * <code>size() == 0</code>.
	 * 
	 * @return the size of the list
	 */
	@Override
	public int size() {
		if (tail == null) {
			return 0;
		}
		int z = 1;
		MutableList i = tail;

		while (i.tail != null) {
			z++;
			if (z == Integer.MAX_VALUE) {
				break;
			}
			i = i.tail;
		}
		return z;
	}

	/**
	 * @return <code>true</code> if there are no elements in the list
	 */
	@Override
	public boolean isEmpty() {
		return (head == null && tail == null);
	}

	/**
	 * Gets the index<sup>th</sup> item in the list, making a fuss if the index is
	 * not valid.
	 * 
	 * @param index the element wanted (in the range [0, size() - 1] if you don't
	 *              want a fuss
	 * @returns the object stored at the element
	 * @throws IndexOutOfBoundsException if you decided you did want a fuss after
	 *                                   all.
	 */
	@Override
	public Object get(int index) {
		if (!isFront()) {
			throw new RuntimeException("BUG! (Should not be able to get at index other than front)");
		}
		if (isEmpty()) {
			throw new IndexOutOfBoundsException("Attempt to get index " + index + " of an empty list");
		}
		if (index < 0) {
			throw new IndexOutOfBoundsException("Attempt to get negative index " + index);
		}
		MutableList element = item(index);
		if (element == null) {
			throw new IndexOutOfBoundsException("Mutable list has fewer than " + index + " elements");
		}
		return element.head;
	}

	/**
	 * As specified in the <a href=
	 * "https://docs.oracle.com/javase/8/docs/api/java/util/List.html#subList-int-int-">
	 * java documentation</a>, MutableList cannot support subList() and the idioms
	 * indicated. Particularly problematic is the example
	 * <code>list.subList(from, to).clear()</code>, which is supposed to remove
	 * those elements from the list.
	 * 
	 * The reason is firstly that the sublist would need a 'front' object, and
	 * secondly that the last element of the sublist would have a <code>null</code>
	 * tail. Mucking around with indexes of lists strikes me as generally not
	 * treating them like lists, and while this class supports what it can, this
	 * subList() nonsense is a step too far. {@link subListCopy(int, int)} is
	 * provided if you want.
	 * 
	 * So if you call this you will just get an exception thrown.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public List<Object> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException("Mutable list does not support subList()");
	}

	/**
	 * Provide an alternative implementation of {@link subList(int, int)} that is
	 * compatible with the way this data structure works. This returns a <i>copy</i>
	 * of the relevant sublist, operations on which will not affect this list. Slow
	 * (O(N)).
	 * 
	 * Note: unlike {@link subList(int, int)} this method reverses the sublist if
	 * <code>fromIndex</code> is greater than <code>toIndex</code>; in that case the
	 * range to reverse starts at <code>toIndex</code> and finishes at
	 * <code>fromIndex - 1</code>, rather than the default, which is to start at
	 * <code>fromIndex</code> and finish at <code>toIndex - 1</code>.
	 * 
	 * @param fromIndex if less than <code>toIndex</code> the inclusive start of the
	 *                  sublist (minimum 0); if greater, the non-inclusive endpoint.
	 * @param toIndex   if greater than <code>fromIndex</code> the non-inclusive
	 *                  endpoint of the sublist; if less, the inclusive start-point
	 * @return a copy of the (possibly reversed) sub-list as specified by
	 *         <code>fromIndex</code> and <code>toIndex</code>
	 * @throws IndexOutOfBoundsException if <code>fromIndex</code> or
	 *                                   <code>toIndex</code> is negative or is set
	 *                                   such that an element of the list to be
	 *                                   selected would be after the last element
	 * @throws IllegalArgumentException  if <code>fromIndex == toIndex</code> (an
	 *                                   index cannot be exclusive and inclusive at
	 *                                   the same time)
	 */
	public MutableList subListCopy(int fromIndex, int toIndex) {
		if (isEmpty()) {
			throw new IndexOutOfBoundsException("Attempt to get [" + fromIndex + ", " + toIndex + "[ of empty list");
		}
		if (fromIndex < 0) {
			throw new IndexOutOfBoundsException("Negative fromIndex " + fromIndex + " in subListCopy() call");
		}
		if (toIndex < 0) {
			throw new IndexOutOfBoundsException("Negative toIndex " + toIndex + " in subListCopy() call");
		}

		MutableList sub = new MutableList();

		if (fromIndex == toIndex) {
			throw new IllegalArgumentException(
					"Sublist range [" + fromIndex + ", " + toIndex + "[ has the same index inclusive and exclusive");
		}

		MutableList from = fromIndex < toIndex ? item(fromIndex) : item(fromIndex - 1);
		MutableList to = fromIndex < toIndex ? item(toIndex - 1) : item(toIndex);

		if (from == null) {
			throw new IndexOutOfBoundsException(
					"List does not have index " + (fromIndex < toIndex ? fromIndex : toIndex));
		}
		if (to == null) {
			throw new IndexOutOfBoundsException(
					"List does not have end index " + (fromIndex < toIndex ? toIndex : fromIndex));
		}

		// Now we have checked that from and to are correct, we can advance to
		// so that the loop terminates correctly when next == to
		to = fromIndex < toIndex ? to.tail : to.prev;

		MutableList next = from;
		MutableList sub_next = sub;

		do {
			sub_next = new MutableList(next.head, sub_next);
			next = fromIndex < toIndex ? next.tail : next.prev;
		} while (next != to);

		return sub;
	}

	/**
	 * Replace the element at the indicated index with that specified. Slow (O(N)).
	 * 
	 * @param index   the index at which to replace the item currently stored
	 * @param element the object to store there (must not be <code>null</code>
	 * @returns the previous element stored at the index
	 * @throws NullPointerException      if <code>element == null</code>
	 * @throws IndexOutOfPointsException if <code>index &lt; 0</code> or
	 *                                   <code>&gt;= size()</code>
	 */
	@Override
	public Object set(int index, Object element) {
		if (element == null) {
			throw new NullPointerException("Cannot store null in MutableList");
		}
		if (index < 0) {
			throw new IndexOutOfBoundsException("Negative index " + index + " out of bounds");
		}
		MutableList loc = item(index);
		if (loc == null) {
			throw new IndexOutOfBoundsException("MutableList does not have index " + index);
		}

		Object old = loc.head;
		loc.head = element;
		return old;
	}

	/**
	 * Get the (non-front) MutableList at the requested index, returning null if
	 * there isn't one. Slow (O(N))
	 * 
	 * @param ix the index to retrieve (starting at zero)
	 * @return the MutableList stored there
	 */
	private MutableList item(int ix) {
		if (tail == null || ix < 0) {
			return null;
		} else {
			MutableList next = tail;

			for (int i = 0; i < ix; i++) {
				if (next == null) {
					return null;
				}
				next = next.tail;
			}
			return next;
		}
	}

	/**
	 * @return <code>true</code> if this is the front of the list
	 */
	protected boolean isFront() {
		return (head == null && prev == null);
	}

	/**
	 * Slow (O(N)) way of getting the last element of the list, iteratively (i.e.
	 * non-recursively without relying on the stack)
	 * 
	 * @return the last element of the list
	 */
	protected MutableList last() {
		MutableList i = this;

		while (i.tail != null) {
			i = i.tail;
		}

		return i;
	}

	/**
	 * Private convenience method turning collection in the argument into a Set.
	 * 
	 * @param c a collection
	 * @return the collection as a Set
	 */
	private Set<Object> makeSet(Collection<?> c) {
		Set<Object> cc = new HashSet<Object>();
		for (Object o : c) {
			cc.add(o);
		}
		return cc;
	}

	/**
	 * Iterates through the list to see if it contains <code>o</code> Does not use
	 * recursion.
	 * 
	 * @param o
	 * @return <code>true</code> if <code>o</code> is in the list
	 */
	@Override
	public boolean contains(Object o) {
		if (tail == null) {
			return false;
		}
		MutableList i = tail;

		while (i != null) {
			if (i.head.equals(o)) {
				return true;
			}
			i = i.tail;
		}
		return false;
	}

	/**
	 * Checks that all the items in a collection are members of the list. If the
	 * collection has duplicates in, they will be treated as though they only occur
	 * once.
	 * 
	 * @param c a collection
	 * @return <code>true</code> if all the items in the collection appear at least
	 *         once in the list
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		Set<Object> cc = makeSet(c);
		boolean answer = containsAllSet(cc);
		cc.clear();
		return answer;
	}

	/**
	 * Private method to implement {@link #containsAll(Collection)}. Iterates
	 * through the list while there are elements in the Set left to find. The set is
	 * modified.
	 * 
	 * @param cc a Set of items to check
	 * @return <code>true</code> if all the items in the Set appear at least once in
	 *         the list
	 */
	private boolean containsAllSet(Set<Object> cc) {
		if (cc.isEmpty()) {
			return true;
		}
		if (head == null && tail == null) {
			return false;
		}
		for (MutableList next = tail; next != null; next = next.tail) {
			if (cc.contains(next.head)) {
				cc.remove(next.head);
			}
		}
		return cc.isEmpty();
	}

	/**
	 * Return the first index at which the object is equal to the element stored, or
	 * -1 if there is no such index. Slow (O(N)). Seriously, why are you using a
	 * list if you want to know the index? If you look up the item at that index,
	 * then that will be slow too (see {@link #get(int)}).
	 * 
	 * @param o the object to search for
	 * @return the first index at which o appears, or -1 if it doesn't
	 */
	@Override
	public int indexOf(Object o) {
		if (o == null) {
			return -1;
		}
		int ix = -1;
		for (MutableList next = tail; next != null; next = next.tail) {
			ix++;
			if (next.head.equals(o)) {
				return ix;
			}
		}
		return -1;
	}

	/**
	 * Return the last index at which the object is equal to the element stored.
	 * Slow (O(N)). See {@link #indexOf(Object)} for the rant.
	 * 
	 * @param o the object to search for
	 * @return the last index at which o appears, or -1 if it doesn't
	 */
	@Override
	public int lastIndexOf(Object o) {
		if (o == null) {
			return -1;
		}
		int ix = -1;
		int i = -1;
		for (MutableList next = tail; next != null; next = next.tail) {
			i++;
			if (next.head.equals(o)) {
				ix = i;
			}
		}
		return ix;
	}

	/**
	 * @return an iterator for the list
	 */
	@Override
	public Iterator<Object> iterator() {
		return new MutableListIterator(this);
	}

	/**
	 * @return a ListIterator for the list
	 */
	@Override
	public ListIterator<Object> listIterator() {
		return new MutableListIterator(this);
	}

	/**
	 * @return a ListIterator starting at element index
	 * @throws IndexOutOfBoundsException if the index is outside the range [0,
	 *                                   size()]
	 */
	@Override
	public ListIterator<Object> listIterator(int index) {
		if (index < 0) {
			throw new IndexOutOfBoundsException("MutableList has no negative index");
		}
		MutableList loc = index == 0 ? this : item(index - 1);
		if (loc == null) {
			throw new IndexOutOfBoundsException("MutableList has no element " + index);
		}
		return new MutableListIterator(loc, index);
	}

	/**
	 * @return an iterator that will iterate (recursively) through elements that are
	 *         MutableLists
	 */
	public Iterator<Object> depthFirstIterator() {
		return new DepthFirstMutableListIterator(this);
	}

	/**
	 * Converts the list into an array of Objects. O(2N) because we have to get the
	 * size before populating the array.
	 * 
	 * @return the list as an array
	 */
	@Override
	public Object[] toArray() {
		if (!isFront()) {
			throw new RuntimeException("BUG! (Should not be able to toArray() anythong other than the 'front'");
		}
		int length = size();
		Object[] array = new Object[length];

		MutableList i = tail;
		for (int j = 0; j < length; j++) {
			array[j] = i.head;
			i = i.tail;
		}

		return array;
	}

	/**
	 * Converts the list into an array of the specified generic type. Returns
	 * <code>a</code> if the list will fit there. Assignability of list members to
	 * the generic type is checked at runtime.
	 * 
	 * @param a an array to populate with members of the list. Typically empty as
	 *          bog-standard awkward java workaround to let you pretend you can work
	 *          with generics.
	 * @return an array populated with members of the list. This will be
	 *         <code>a</code> if it's big enough, or a new array if not.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		if (!isFront()) {
			throw new RuntimeException("BUG! (Should not be able to toArray() anythong other than the 'front'");
		}
		if (isEmpty()) {
			for (int j = 0; j < a.length; j++) {
				a[j] = null;
			}
			return a;
		}

		Class<?> cls = a.getClass().getComponentType();

		MutableList i = tail;

		// First try to fill a[] -- that will be quicker as we don't have to check the
		// size of this list

		for (int j = 0; j < a.length; j++) {
			if (i == null) {
				a[j] = null;
			} else {
				if (cls.isAssignableFrom(i.head.getClass())) {
					a[j] = (T) (i.head);
				} else {
					throw new ClassCastException("Cannot assign to \"" + cls + "\" from \"" + i.head.getClass() + "\"");
				}
				i = i.tail;
			}
		}

		if (i == null) {
			return a;
		}

		// Finally build a new array of T, which requires reflection, sigh and thanks
		// stackoverflow.com

		int length = size();
		T array[] = (T[]) Array.newInstance(cls, length);
		i = tail;
		for (int j = 0; j < length; j++) {
			if (cls.isAssignableFrom(i.head.getClass())) {
				array[j] = (T) (i.head);
			} else {
				throw new ClassCastException("Cannot assign to \"" + cls + "\" from \"" + i.head.getClass() + "\"");
			}
			i = i.tail;
		}

		return array;
	}

	/**
	 * Adds an item to the end of the list -- so it will be slow (O(N) where N is
	 * the length of the list) If you're more concerned with speed than ordering,
	 * use {@link #unshift(Object)}.
	 * 
	 * Repeated adding of several items will be O(N^2) where N is the number of
	 * items to add. That's because each successive add() will iterate through the
	 * whole list so far. Use {@link #addAll(Collection)} instead.
	 * 
	 * @param o the object to add; must not be <code>null</code>
	 * @return <code>true</code> -- this will always succeed
	 * @throws IllegalArgumentException if <code>o == null</code>
	 */
	@Override
	public boolean add(Object o) {
		if (o == null) {
			throw new NullPointerException("Cannot add() a null to a MutableList");
		}
		MutableList end = last();
		end.tail = new MutableList(o, end, null);
		return true;
	}

	/**
	 * Adds all the elements in the collection to the end of the list in order. O(N
	 * + M) where N is the length of this list, and M is the size of the collection.
	 * 
	 * @param c the collection to add
	 * @return <code>true</code>
	 */
	@Override
	public boolean addAll(Collection<? extends Object> c) {
		if (c == null) {
			throw new NullPointerException("Attempt to add from null collection");
		}
		MutableList next = last();
		for (Object o : c) {
			if (o == null) {
				throw new NullPointerException("MutableList does not except null elements");
			}
			next = new MutableList(o, next);
		}
		return true;
	}

	/**
	 * Adds an item to the front of the list.
	 * 
	 * @param o the object to add; must not be <code>null</code>
	 * @throws IllegalArgumentException if <code>o == null</code>
	 */
	public void unshift(Object o) {
		if (o == null) {
			throw new IllegalArgumentException("Cannot unshift() a null to a MutableList");
		}
		if (isFront()) {
			tail = new MutableList(o, this, tail);
		} else {
			throw new RuntimeException("BUG! (Should not be able to unshift() to anything other than the 'front')");
		}
	}

	/**
	 * Alias for {@link #add(Object)} to complete the Perl nomenclature
	 * 
	 * @param o object to add to the end of the list
	 */
	public void push(Object o) {
		add(o);
	}

	/**
	 * Add an element at the specified index in the list. Slow (O(N)).
	 * 
	 * @param index   the index at which to add the element
	 * @param element the element to add at the index
	 * @throws NullPointerException      if <code>element == null</code>
	 * @throws IndexOutOfBoundsException if index is outwith [0,
	 *                                   <code>size()</code>]
	 */
	@Override
	public void add(int index, Object element) {
		if (element == null) {
			throw new NullPointerException("MutableList cannot have null entries");
		}
		MutableList loc = item(index - 1);
		if (loc == null) {
			throw new IndexOutOfBoundsException("MutableList has no index " + index);
		}
		new MutableList(element, loc, loc.tail);
	}

	/**
	 * Adds all the elements in the collection at the specified point. This will be
	 * faster than calling @{link #add(int)} repeatedly, but is still O(N).
	 * 
	 * @param index the point at which to start adding elements from the collection
	 * @param c     the collection
	 * @return <code>true</code> -- always successful
	 * @throws IndexOutOfBoundsException if index outwith [0, <code>size()</code>]
	 * @throws NullPointerException      if any of the elements in <code>c</code>
	 *                                   are <code>null</code>
	 */
	@Override
	public boolean addAll(int index, Collection<? extends Object> c) {
		if (!isFront()) {
			throw new RuntimeException("BUG! (Should not be able to addAll at index to other than front)");
		}

		// Ask for the element _before_ the required index in case index is at the end
		// of the list (i.e. index == size())
		MutableList element = index == 0 ? this : item(index - 1);
		if (element == null) {
			throw new IndexOutOfBoundsException("Mutable list has too few elements to add at position " + index);
		}

		MutableList rest = element.tail;
		MutableList next = element;

		for (Object o : c) {
			if (o == null) {
				throw new NullPointerException("MutableList cannot have null entries");
			}
			next = new MutableList(o, next);
		}
		next.tail = rest;
		if (rest != null) {
			rest.prev = next;
		}

		return true;
	}

	/**
	 * Removes the specified item from the list once, the first time it occurs.
	 * O(N). If you want to make sure that the list does not contain the object at
	 * all, {@link #removeAll(Collection)} will do that.
	 * 
	 * @return <code>true</code> if one item was removed from the list
	 */
	@Override
	public boolean remove(Object o) {
		for (MutableList i = this; i.tail != null; i = i.tail) {
			if (i.tail.head.equals(o)) {
				MutableList j = i.tail;
				i.tail = i.tail.tail;
				i.tail.prev = i;
				j.shallowClear();
				return true;
			}
		}
		return false;
	}

	/**
	 * Removes everything in the collection from the list, every time it occurs.
	 * After executing this procedure, none of the elements of <code>c</code> will
	 * be in the list.
	 * 
	 * @param c the collection all of which are to be removed from the list
	 * @return <code>true</code> if at least one element of the list was removed.
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		if (isEmpty()) {
			return false;
		}
		if (!isFront()) {
			throw new RuntimeException("BUG! (Somehow able to removeAll() non-'front' MutableList)");
		}

		Set<Object> cc = makeSet(c);
		int n_removed = 0;

		for (MutableList i = this; i != null && i.tail != null; i = i.tail) {
			MutableList j = i.tail;
			while (j != null && cc.contains(j.head)) {
				MutableList k = j;

				j = k.tail;
				k.shallowClear();
				n_removed++;
			}
			i.tail = j;
			if (j != null) {
				j.prev = i;
			}
		}

		cc.clear();
		return (n_removed > 0);
	}

	/**
	 * Remove all items in the list that aren't in the collection
	 * 
	 * @param c the collection of items from which this list must only have elements
	 * @return <code>true</code> if calling this method has led to at least one
	 *         deletion
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		if (isEmpty()) {
			return false;
		}
		if (!isFront()) {
			throw new RuntimeException("BUG! (Somehow able to retainAll() non-'front' MutableList)");
		}

		Set<Object> cc = makeSet(c);
		int n_removed = 0;

		for (MutableList i = this; i != null && i.tail != null; i = i.tail) {
			MutableList j = i.tail;
			while (j != null && !cc.contains(j.head)) {
				MutableList k = j;

				j = k.tail;
				k.shallowClear();
				n_removed++;
			}
			i.tail = j;
			if (j != null) {
				j.prev = i;
			}
		}

		cc.clear();
		return (n_removed > 0);
	}

	/**
	 * Removes the item at the indicated index, returning what was stored there.
	 * Slow (O(N))
	 * 
	 * @param index the index at which to remove an item
	 * @return the value stored there
	 * @throws IndexOutOfBoundsException if the index is outwith the range [0,
	 *                                   <code>size()</code>[
	 */
	@Override
	public Object remove(int index) {
		MutableList loc = item(index);
		if (loc == null) {
			throw new IndexOutOfBoundsException("MutableList has no index " + index);
		}
		loc.prev.tail = loc.tail;
		loc.tail.prev = loc.prev;
		Object entry = loc.head;
		loc.shallowClear();
		return entry;
	}

	/**
	 * Removes the item from the front of the list and returns it
	 * 
	 * @return the object removed from the front of the list
	 * @throws NoSuchElementException if the list is empty
	 */
	public Object shift() {
		if (isFront()) {
			if (tail == null) {
				throw new NoSuchElementException("Cannot shift from empty MutableList");
			} else {
				MutableList next = tail;
				tail = next.tail;
				if (tail != null) {
					tail.prev = this;
				}
				Object o = next.head;
				next.shallowClear();
				return o;
			}
		} else {
			throw new RuntimeException("BUG! (Should not be able to shift() from anything other than the 'front')");
		}
	}

	/**
	 * Removes the last item on the list and returns it. Will be slow (O(N)).
	 * 
	 * @return the object removed from the end of the list
	 * @throws NoSuchElementException if the list is empty
	 */
	public Object pop() {
		if (isEmpty()) {
			throw new NoSuchElementException("Cannot pop from empty MutableList");
		}
		MutableList end = last();
		Object obj = end.head;
		end.prev.tail = null;
		end.shallowClear();
		return obj;
	}

	/**
	 * Clear the list (make sure all its entries point to <code>null</code>) so that
	 * references are cleaned up. Iteratively clears everything after the tail, too.
	 * Recursively clears elements that are MutableLists
	 */
	@Override
	public void clear() {
		for (MutableList i = tail; i != null; i = i.tail) {
			i.prev.tail = null;
			i.prev = null;
			if (i.head instanceof MutableList) {
				((MutableList) (i.head)).clear();
			}
			i.head = null;
		}
		head = null; // Paranoid; should be null already if this is the front
		prev = null; // Paranoid; should be null already if this is the front
		tail = null; // Paranoid; should be null already if list is empty or loop above worked
	}

	/**
	 * Just set references in this object to null, but recursively clear (not
	 * shallowly) elements that are MutableLists
	 */
	private void shallowClear() {
		if (head instanceof MutableList) {
			((MutableList) (head)).clear();
		}
		head = null;
		tail = null;
		prev = null;
	}

	/**
	 * @return a copy of this list (Objects at elements are not copied, unless they
	 *         are mutableLists)
	 */
	@Override
	public MutableList clone() {
		MutableList copy = new MutableList();
		MutableList next = copy;
		for (MutableList i = tail; i != null; i = i.tail) {
			Object newObj = i.head;
			if (i.head instanceof MutableList) {
				newObj = ((MutableList) (i.head)).clone();
			}
			next = new MutableList(newObj, next);
		}
		return copy;
	}

	/**
	 * @return a copy of the reverse of this list
	 */
	public MutableList reverseCopy() {
		if (!isFront()) {
			throw new RuntimeException("BUG! (Should not be able to reverseCopy starting from other than front)");
		}

		MutableList reversed = new MutableList();

		for (MutableList i = tail; i != null; i = i.tail) {
			reversed.unshift(i.head);
		}

		return reversed;
	}

	/**
	 * Reverse the list in place
	 */
	public void reverse() {
		if (!isFront()) {
			throw new RuntimeException("BUG! (Should not be able to reverse starting from other than front)");
		}

		MutableList last = null;
		// N.B. Deliberately setting i = i.prev in the next line; we're looping forward
		// through the list that will be reversed by the end of the loop, and the first
		// line sets i.prev = i.tail
		for (MutableList i = tail; i != null; i = i.prev) {
			i.prev = i.tail;
			i.tail = last;
			last = i;
		}
		tail = last;
		if (last != null) {
			last.prev = this;
		}
	}

	public boolean check(boolean msg, String msgPrefix) {
		if (!isFront()) {
			if (msg) {
				System.err.println(msgPrefix + ": Checking non-front element");
			}
			return false;
		}
		if (head != null) {
			if (msg) {
				System.err.println(msgPrefix + ": Front element has non-null head \"" + head + "\"");
			}
			return false;
		}
		if (prev != null) {
			if (msg) {
				System.err.println(msgPrefix + ": Front element has non-null prev");
			}
			return false;
		}
		MutableList j = this;
		int ix = 0;
		boolean ret = true;
		for (MutableList i = tail; i != null; i = i.tail) {
			if (i.prev == null) {
				System.err.println(msgPrefix + ": Element " + ix + " has null prev");
				ret = false;
			}
			if (i.head == null) {
				System.err.println(msgPrefix + ": Element " + ix + " has null head");
				ret = false;
			}
			if (i.prev != j) {
				System.err.println(msgPrefix + ": Element " + ix
						+ " has non-null prev that does not point to previous element (prev element's head is \""
						+ i.prev.head + "\"; expecting head \"" + j.head + "\")");
				ret = false;
			}
			j = i;
			ix++;
		}
		return ret;
	}

	public String asPrintableString() {
		return asPrintableString(", ");
	}

	public String asPrintableString(String sep) {
		return asPrintableString("[ ", sep, " ]");
	}

	public String asPrintableString(String start, String sep, String end) {
		StringBuffer buf = new StringBuffer();

		buf.append(start);
		for (MutableList i = tail; i != null; i = i.tail) {
			if (i != tail) {
				buf.append(sep);
			}
			if (i.head instanceof MutableList) {
				buf.append(((MutableList) i.head).asPrintableString(start, sep, end));
			} else {
				buf.append(i.head.toString());
			}
		}
		buf.append(end);
		return buf.toString();
	}

	/**
	 * @return <code>true</code> if this list and the other are the same length and
	 *         all their elements are equal
	 */
	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof MutableList)) {
			return false;
		}
		MutableList otherList = (MutableList) other;
		MutableList thisNext = tail;
		MutableList otherNext = otherList.tail;
		while (thisNext != null && otherNext != null) {
			if (!thisNext.head.equals(otherNext.head)) {
				return false;
			}
			thisNext = thisNext.tail;
			otherNext = otherNext.tail;
		}
		return (thisNext == null && otherNext == null);
	}

	/**
	 * Iterator class for MutableList conforming to ListIterator. Some rather clunky
	 * machinery here.
	 * 
	 * @author Gary Polhill
	 * @version 1.0, 10 November 2021
	 */
	private class MutableListIterator implements ListIterator<Object> {
		private MutableList i;
		private int ix;
		private boolean removed;
		private boolean indexed;
		private int added;

		/**
		 * Default constructor to start the iterator at the beginning of a list
		 * 
		 * @param i the 'front' of a MutableList
		 */
		private MutableListIterator(MutableList i) {
			this(i, -1);
		}

		/**
		 * Constructor to start the iterator at any point in the list. If
		 * <code>ix</code> is not <code>-1</code>, then <code>i</code> must be the
		 * MutableList at the location <i>before</i> <code>i</code>.
		 * 
		 * @param i  the MutableList at which the iterator will start
		 * @param ix the corresponding index
		 */
		private MutableListIterator(MutableList i, int ix) {
			if (i == null) {
				throw new IllegalArgumentException("Cannot iterate null MutableList");
			}
			this.i = i;
			this.ix = ix;
			if (ix == -1 && !i.isFront()) {
				throw new RuntimeException("BUG! MutableListIterator at -1 called with non-front element");
			}
			removed = false;
			indexed = (ix >= 0);
			added = 0;
		}

		/**
		 * There are two ways for the ListIterator to be called. If <code>indexed</code>
		 * is <code>true</code> then it was called at a specific index rather than the
		 * beginning of the list. In that case, <code>i</code> is at the location before
		 * <code>ix</code>. There is then a <code>next()</code> if i.tail.tail != null.
		 */
		@Override
		public boolean hasNext() {
			if (indexed) {
				return (i.tail != null && i.tail.tail != null);
			}
			return (i.tail != null);
		}

		@Override
		public Object next() {
			if (indexed) {
				i = i.tail;
				indexed = false;
				return i.head;
			} else if (i.tail == null) {
				throw new NoSuchElementException("Reached end of MutableList (at [" + ix + "] = " + i.head + ")");
			} else {
				MutableList j = i;
				i = i.tail;
				if (removed) {
					j.shallowClear();
				}
				ix++;
				removed = false;
				added = 0;
				return i.head;
			}
		}

		@Override
		public boolean hasPrevious() {
			if (added > 0 || indexed) {
				return true;
			}
			return (i.prev != null && i.prev.head != null);
		}

		@Override
		public Object previous() {
			if (indexed) {
				indexed = false;
				ix--;
				return i.head;
			}
			if (added > 0) {
				// should not be necessary to set removed = false
				added = 0;
				return i.head;
			}
			if (i.prev == null || i.prev.head == null) {
				throw new NoSuchElementException("Reached beginning of MutableList (at [" + ix + "] = " + i.head + ")");
			} else {
				MutableList j = i;
				i = i.prev;
				if (removed) {
					j.shallowClear();
				}
				ix--;
				removed = false;
				added = 0;
				return i.head;
			}
		}

		@Override
		public int nextIndex() {
			return indexed ? ix : ix + 1;
		}

		@Override
		public int previousIndex() {
			if (added > 0 || ix == -1) {
				return ix;
			}
			return ix - 1;
		}

		@Override
		public void remove() {
			if (removed) {
				throw new IllegalStateException("Cannot remove() twice between next() / previous() calls");
			}
			if (added != 0) {
				throw new IllegalStateException("Cannot remove() after add() between next() / previous() calls");
			}
			if (i.head == null) {
				throw new IllegalStateException("Cannot remove() from empty list");
			}
			if (ix == -1 || indexed) {
				throw new IllegalStateException("Cannot remove() before calling next() or previous()");
			}
			if (i.prev == null) {
				throw new RuntimeException("BUG! (Item to remove has no 'prev')");
			}
			i.prev.tail = i.tail;
			if (i.tail != null) {
				i.tail.prev = i.prev;
			}
			removed = true;
		}

		@Override
		public void set(Object o) {
			if (o == null) {
				throw new IllegalArgumentException("Cannot set()  MutableList entry to null");
			}
			if (ix == -1 || indexed) {
				throw new IllegalStateException("Cannot set() before calling next() or previous()");
			}
			if (removed || added != 0) {
				throw new IllegalStateException(
						"Cannot set() MutableList entry when you've already add()ed or remove()d");
			}
			i.head = o;
		}

		@Override
		public void add(Object o) {
			if (o == null) {
				throw new IllegalArgumentException("Cannot add null to MutableList");
			} else if ((i.isFront() && i.tail == null) || (indexed && i.tail == null)) {
				// Adding items to an empty list or to the end of a list puts the 'cursor' at an
				// imaginary endpoint after the last item; so future adds will be _after_ this
				// add rather than before it
				i.tail = new MutableList(o, i);
				i = i.tail;
				added = 1;
			} else {
				if (i.isFront() || indexed) {
					i = i.tail;
					if (!indexed) {
						ix++;
					} else {
						indexed = false;
					}
				}
				if (added <= 0) {
					MutableList j = new MutableList(o, i.prev, i);
					i.prev = j;
					added = -1;
				} else {
					if (i.tail != null) {
						throw new RuntimeException("BUG! (Started adding to empty list, but have a non-null tail)");
					}
					i.tail = new MutableList(o, i);
					i = i.tail;
				}
			}
			ix++;

		}

	}

	private class DepthFirstMutableListIterator implements Iterator<Object> {
		MutableList i;
		Stack<MutableList> backtrack;

		private DepthFirstMutableListIterator(MutableList i) {
			this.i = i;
			backtrack = new Stack<MutableList>();
		}

		@Override
		public boolean hasNext() {
			return !(i.tail == null && backtrack.size() == 0);
		}

		@Override
		public Object next() {
			boolean popped = false;
			if (i.tail == null) {
				if (backtrack.size() == 0) {
					throw new NoSuchElementException("Reached end of depth-first search of MutableList");
				}
				i = backtrack.pop();
				popped = true;
			}
			i = popped ? i : i.tail;
			while (i.head instanceof MutableList) {
				MutableList ihead = (MutableList) (i.head);

				if (ihead.isEmpty()) {
					if (i.tail == null) {
						return null;
					}
					i = i.tail;
				} else {
					if (i.tail != null) {
						backtrack.push(i.tail);
					}
					i = ihead.tail;
				}
			}
			return i.head;
		}

	}
}
