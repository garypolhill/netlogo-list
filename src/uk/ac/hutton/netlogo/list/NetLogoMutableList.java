/**
 * 
 */
package uk.ac.hutton.netlogo.list;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.nlogo.api.AgentSet;
import org.nlogo.api.Argument;
import org.nlogo.api.ExtensionException;
import org.nlogo.core.ExtensionObject;
import org.nlogo.core.LogoList;

/**
 * Wrapper class around MutableList providing convenience functions for NetLogo,
 * and implementing NetLogo's ExtensionObject interface so it can be treated by
 * NetLogo as an object.
 * 
 * @author gary
 */
public class NetLogoMutableList implements ExtensionObject, Iterable<Object> {
	public static final String DATA_TYPE_NAME = "list";
	private MutableList list;
	private static long next_id = 0;
	private final long id;
	private int size;

	/**
	 * Constructor that creates an empty list and assigns it an ID
	 */
	public NetLogoMutableList() {
		list = new MutableList();
		id = next_id;
		next_id++;
		size = 0;
	}

	/**
	 * Convenience constructor initializing from a NetLogo list
	 * 
	 * @param list
	 */
	public NetLogoMutableList(LogoList list) {
		this();
		pushAll(list);
	}

	/**
	 * Convenience constructor initializing from an AgentSet
	 * 
	 * @param set
	 */
	public NetLogoMutableList(AgentSet set) {
		this();
		pushAll(set);
	}

	/**
	 * Convenience constructor initializing from a Java Collection
	 * 
	 * @param c
	 */
	protected NetLogoMutableList(Collection<? extends Object> c) {
		this();
		list.addAll(c);
	}

	/**
	 * @return the ID of this NetLogoMutableList
	 */
	public long getID() {
		return id;
	}

	/**
	 * @return a NetLogoMutableListIndex wrapping an iterator; you probably don't
	 *         want this unless implementing the 'cursor' command!
	 */
	public NetLogoMutableListIndex getIterator() {
		return new NetLogoMutableListIndex(this);
	}

	/**
	 * @return the size of the list
	 */
	public int size() {
		return size;
	}

	/**
	 * Increment the size of the list
	 */
	protected void incSize() {
		size++;
	}

	/**
	 * Decrement the size of the list
	 */
	protected void decSize() {
		size--;
	}

	/**
	 * @return the MutableList wrapped by this object
	 */
	protected MutableList getList() {
		return list;
	}

	/**
	 * Wrapper around MutableList's push() that maintains size
	 * 
	 * @param obj
	 */
	public void push(Object obj) {
		list.add(obj);
		size++;
	}

	/**
	 * Wrapper around MutableList's pushAll() that maintains size
	 * 
	 * @param obj
	 */
	public void pushAll(NetLogoMutableList obj) {
		list.addAll(obj.getList());
		size += obj.size();
	}

	/**
	 * Convenience wrapper around MutableList's pushAll() handling a LogoList
	 * 
	 * @param obj
	 */
	public void pushAll(LogoList obj) {
		list.addAll(obj.toJava());
		size += obj.size();
	}

	public void pushAll(List<Object> obj) {
		list.addAll(obj);
		size += obj.size();
	}

	/**
	 * Wrapper around MutableList's pushAll() that empties the Collection to add
	 * 
	 * @param obj
	 */
	public void pushAllClear(List<Object> obj) {
		pushAll(obj);
		obj.clear();
	}

	/**
	 * Wrapper around MutableList's pushAll() handling an AgentSet
	 * 
	 * @param obj
	 */
	public void pushAll(AgentSet obj) {
		HashSet<Object> agents = new HashSet<Object>();
		for (Object agent : obj.agents()) {
			agents.add(agent);
		}
		list.addAll(agents);
		size += agents.size();
		agents.clear();
	}

	/**
	 * Wrapper around MutableList's pop() that maintains size
	 * 
	 * @return the object popped
	 */
	public Object pop() {
		size--;
		return list.pop();
	}

	/**
	 * Wrapper around MutableList's unshift() that maintains size
	 * 
	 * @param obj the object to unshift
	 */
	public void unshift(Object obj) {
		list.unshift(obj);
		size++;
	}

	/**
	 * Wrapper around MutableList's unshiftAll() that maintains size
	 * 
	 * @param obj
	 */
	public void unshiftAll(NetLogoMutableList obj) {
		list.addAll(0, obj.getList());
		size += obj.size();
	}

	/**
	 * Convenience wrapper around MutableList's unshiftAll() for LogoLists
	 * 
	 * @param obj
	 */
	public void unshiftAll(LogoList obj) {
		list.addAll(0, obj.toJava());
		size += obj.size();
	}

	/**
	 * Convenience wrapper around MutableList's unshiftAll() for AgentSets
	 * 
	 * @param obj
	 */
	public void unshiftAll(AgentSet obj) {
		HashSet<Object> agents = new HashSet<Object>();
		for (Object agent : obj.agents()) {
			agents.add(agent);
		}
		list.addAll(0, agents);
		size += agents.size();
		agents.clear();
	}

	/**
	 * Wrapper around MutableList's shift() that maintains size
	 * 
	 * @return the object shifted
	 */
	public Object shift() {
		size--;
		return list.shift();
	}

	/**
	 * Wrapper around MutableList's containsAll()
	 * 
	 * @param objs
	 * @return <code>true</code> if all the objs are in the list
	 */
	public boolean memberAll(List<Object> objs) {
		return list.containsAll(objs);
	}

	/**
	 * Wrapper around MutableList's containsAny()
	 * 
	 * @param objs
	 * @return <code>true</code> if at least one of the objs are in the list
	 */
	public boolean memberAny(List<Object> objs) {
		return list.containsAny(objs);
	}

	/**
	 * Wrapper around MutableList's contains()
	 * 
	 * @param obj
	 * @return <code>true</code> if obj is in the list
	 */
	public boolean member(Object obj) {
		return list.contains(obj);
	}

	/**
	 * Wrapper around MutableList's containsDeeply()
	 * 
	 * @param obj
	 * @return <code>true</code> if obj is in the list or (recursively) any list
	 *         members
	 */
	public boolean deepMember(Object obj) {
		return list.containsDeeply(obj);
	}

	/**
	 * Wrapper around MutableList's remove() that maintains size
	 * 
	 * @param obj
	 */
	public void remove(Object obj) {
		if (list.remove(obj)) {
			size--;
		}
	}

	/**
	 * Wrapper around MutableList's removeAll() that maintains size
	 * 
	 * @param objs
	 */
	public void removeAll(List<Object> objs) {
		if (list.removeAll(objs)) {
			size = list.size();
		}
	}

	/**
	 * Wrapper around MutableList's retainAll() that maintains size
	 * 
	 * @param objs
	 */
	public void retainAll(List<Object> objs) {
		if (list.retainAll(objs)) {
			size = list.size();
		}
	}

	public Object first() throws ExtensionException {
		try {
			return list.first();
		} catch (NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}

	public Object second() throws ExtensionException {
		try {
			return list.second();
		} catch (NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}

	public Object third() throws ExtensionException {
		try {
			return list.third();
		} catch (NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}

	public Object fourth() throws ExtensionException {
		try {
			return list.fourth();
		} catch (NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}

	public Object fifth() throws ExtensionException {
		try {
			return list.fifth();
		} catch (NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}

	public Object sixth() throws ExtensionException {
		try {
			return list.sixth();
		} catch (NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}

	public Object seventh() throws ExtensionException {
		try {
			return list.seventh();
		} catch (NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}

	public Object eighth() throws ExtensionException {
		try {
			return list.eighth();
		} catch (NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}

	public Object ninth() throws ExtensionException {
		try {
			return list.ninth();
		} catch (NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}

	public Object tenth() throws ExtensionException {
		try {
			return list.tenth();
		} catch (NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}

	public void first(Object obj) throws ExtensionException {
		try {
			list.first(obj);
		} catch (NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}

	public void second(Object obj) throws ExtensionException {
		try {
			list.second(obj);
		} catch (NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}

	public void third(Object obj) throws ExtensionException {
		try {
			list.third(obj);
		} catch (NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}

	public void fourth(Object obj) throws ExtensionException {
		try {
			list.fourth(obj);
		} catch (NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}

	public void fifth(Object obj) throws ExtensionException {
		try {
			list.fifth(obj);
		} catch (NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}

	public void sixth(Object obj) throws ExtensionException {
		try {
			list.sixth(obj);
		} catch (NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}

	public void seventh(Object obj) throws ExtensionException {
		try {
			list.seventh(obj);
		} catch (NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}

	public void eighth(Object obj) throws ExtensionException {
		try {
			list.eighth(obj);
		} catch (NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}

	public void ninth(Object obj) throws ExtensionException {
		try {
			list.ninth(obj);
		} catch (NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}

	public void tenth(Object obj) throws ExtensionException {
		try {
			list.tenth(obj);
		} catch (NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}

	/**
	 * @return this list as a NetLogo list
	 */
	public LogoList asLogoList() {
		return LogoList.fromJava(list);
	}

	/**
	 * @return this list as a NetLogo list, with any NetLogoMutableList elements
	 *         also converted to NetLogo lists.
	 */
	public LogoList asLogoListDeeply() {
		LogoList logo = LogoList.Empty();
		for (Object item : list) {
			if (item instanceof NetLogoMutableList) {
				logo.lput(((NetLogoMutableList) item).asLogoListDeeply());
			} else {
				logo.lput(item);
			}
		}
		return logo;
	}

	/**
	 * Wrapper around MutableList's reverse()
	 */
	public void reverse() {
		list.reverse();
	}

	/**
	 * Provide a sort implementation using a comparator (e.g. one that wraps a
	 * NetLogo reporter)
	 * 
	 * @param comparator
	 */
	public void sort(Comparator<Object> comparator) {
		Collections.sort(list, comparator);
	}

	/**
	 * Wrapper around MutableList's cat() that maintains sizes
	 * 
	 * @param other
	 */
	public void cat(NetLogoMutableList other) {
		size += other.size();
		list.cat(other.list);
		other.size = 0;
	}

	/**
	 * Ensure each element of the list is unique, and maintain size
	 */
	public void removeDuplicates() {
		ListIterator<Object> ix = list.listIterator();
		Set<Object> c = new HashSet<Object>();
		while (ix.hasNext()) {
			Object obj = ix.next();
			if (c.contains(obj)) {
				ix.remove();
			} else {
				c.add(obj);
			}
		}
		size = c.size();
		c.clear();
	}

	/**
	 * Wrapper around MutableList's depthFirstIterator()
	 * 
	 * @return
	 */
	protected Iterator<Object> getDepthFirstIterator() {
		return list.depthFirstIterator();
	}

	/**
	 * Wrapper around MutableList's listIterator()
	 * 
	 * @return
	 */
	protected ListIterator<Object> getListIterator() {
		return list.listIterator();
	}
	
	/**
	 * Wrapper around MutableList's clear() maintaining size
	 */
	public void clear() {
		list.clear();
		size = 0;
	}

	/**
	 * Convenience function converting a NetLogo API argument expected to be a
	 * NetLogoMutableList into an instance of NetLogoMutableList
	 * 
	 * @param arg
	 * @return the arg as a NetLogoMutableList
	 * @throws ExtensionException
	 */
	public static NetLogoMutableList asNetLogoMutableList(Argument arg) throws ExtensionException {
		return asNetLogoMutableList(arg.get());
	}

	public static NetLogoMutableList asNetLogoMutableList(Object obj) throws ExtensionException {
		if (obj instanceof NetLogoMutableList) {
			return (NetLogoMutableList) obj;
		} else {
			throw new ExtensionException("A " + obj.getClass().getSimpleName() + " (value \"" + obj.toString()
					+ "\") was given as an argument where a " + MutableListExtension.EXTENSION_NAME + ":"
					+ DATA_TYPE_NAME + " was expected");
		}
	}

	/**
	 * Implement ExtensionObject's dump() method. The lack of documentation of this
	 * method means that the table extension's dump() implementation is mirrored
	 * here.
	 */
	@Override
	public String dump(boolean readable, boolean exporting, boolean reference) {
		if (exporting && reference) {
			return Long.toString(id);
		} else {
			return (exporting ? (id + ": ") : "") + list.asPrintableString("[!", " ", "!]");
		}
	}

	/**
	 * @return the name of the extension
	 */
	@Override
	public String getExtensionName() {
		return MutableListExtension.EXTENSION_NAME;
	}

	/**
	 * @return the data type name
	 */
	@Override
	public String getNLTypeName() {
		return DATA_TYPE_NAME;
	}

	/**
	 * Check the equality of the two NetLogoMutableLists
	 */
	@Override
	public boolean recursivelyEqual(Object obj) {
		if (obj instanceof NetLogoMutableList) {
			NetLogoMutableList other = (NetLogoMutableList) obj;
			if (size != other.size) {
				return false;
			}
			return list.equals(other.list);
		}
		return false;
	}

	/**
	 * Get an iterator over the list
	 */
	@Override
	public Iterator<Object> iterator() {
		return list.iterator();
	}

}
