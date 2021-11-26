/**
 * 
 */
package uk.ac.hutton.netlogo.list;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.nlogo.api.AgentSet;
import org.nlogo.api.ExtensionException;
import org.nlogo.core.ExtensionObject;
import org.nlogo.core.LogoList;

/**
 * @author gary
 *
 */
public class NetLogoMutableList implements ExtensionObject, Iterable<Object> {
	private MutableList list;
	private static long next_id = 0;
	private final long id;
	private int size;

	/**
	 * 
	 */
	public NetLogoMutableList() {
		list = new MutableList();
		id = next_id;
		next_id++;
		size = 0;
	}
	
	public long getID() {
		return id;
	}
	
	public NetLogoMutableListIndex getIterator() {
		return new NetLogoMutableListIndex(this);
	}
	
	public int size() {
		return size;
	}
	
	protected void incSize() {
		size++;
	}
	
	protected void decSize() {
		size--;
	}
	
	protected MutableList getList() {
		return list;
	}
	
	public void push(Object obj) {
		list.add(obj);
		size++;
	}
		
	public void pushAll(NetLogoMutableList obj) {
		list.addAll(obj.getList());
		size += obj.size();
	}
	
	public void pushAll(LogoList obj) {
		list.addAll(obj.toJava());
		size += obj.size();
	}
	
	public void pushAll(AgentSet obj) {
		HashSet<Object> agents = new HashSet<Object>();
		for(Object agent: obj.agents()) {
			agents.add(agent);
		}
		list.addAll(agents);
		size += agents.size();
		agents.clear();
	}
	
	public Object pop() {
		size--;
		return list.pop();
	}
	
	public void unshift(Object obj) {
		list.unshift(obj);
		size++;
	}
	
	public void unshiftAll(NetLogoMutableList obj) {
		list.addAll(0, obj.getList());
		size += obj.size();
	}
	
	public void unshiftAll(LogoList obj) {
		list.addAll(0, obj.toJava());
		size += obj.size();
	}
	
	public void unshiftAll(AgentSet obj) {
		HashSet<Object> agents = new HashSet<Object>();
		for(Object agent: obj.agents()) {
			agents.add(agent);
		}
		list.addAll(0, agents);
		size += agents.size();
		agents.clear();
	}
	
	public Object shift() {
		size--;
		return list.shift();
	}
	
	public boolean member(Object obj) {
		return list.contains(obj);
	}
	
	public Object first() throws ExtensionException {
		try {
			return list.first();
		}
		catch(NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}
	
	public Object second() throws ExtensionException {
		try {
			return list.second();
		}
		catch(NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}
	
	public Object third() throws ExtensionException {
		try {
			return list.third();
		}
		catch(NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}
	
	public Object fourth() throws ExtensionException {
		try {
			return list.fourth();
		}
		catch(NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}
	
	public Object fifth() throws ExtensionException {
		try {
			return list.fifth();
		}
		catch(NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}
	
	public Object sixth() throws ExtensionException {
		try {
			return list.sixth();
		}
		catch(NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}
	
	public Object seventh() throws ExtensionException {
		try {
			return list.seventh();
		}
		catch(NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}
	
	public Object eighth() throws ExtensionException {
		try {
			return list.eighth();
		}
		catch(NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}
	
	public Object ninth() throws ExtensionException {
		try {
			return list.ninth();
		}
		catch(NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}
	
	public Object tenth() throws ExtensionException {
		try {
			return list.tenth();
		}
		catch(NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}
	
	public void first(Object obj) throws ExtensionException {
		try {
			list.first(obj);
		}
		catch(NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}
	
	public void second(Object obj) throws ExtensionException {
		try {
			list.second(obj);
		}
		catch(NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}
	
	public void third(Object obj) throws ExtensionException {
		try {
			list.third(obj);
		}
		catch(NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}
	
	public void fourth(Object obj) throws ExtensionException {
		try {
			list.fourth(obj);
		}
		catch(NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}
	
	public void fifth(Object obj) throws ExtensionException {
		try {
			list.fifth(obj);
		}
		catch(NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}
	
	public void sixth(Object obj) throws ExtensionException {
		try {
			list.sixth(obj);
		}
		catch(NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}
	
	public void seventh(Object obj) throws ExtensionException {
		try {
			list.seventh(obj);
		}
		catch(NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}
	
	public void eighth(Object obj) throws ExtensionException {
		try {
			list.eighth(obj);
		}
		catch(NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}
	
	public void ninth(Object obj) throws ExtensionException {
		try {
			list.ninth(obj);
		}
		catch(NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}
	
	public void tenth(Object obj) throws ExtensionException {
		try {
			list.tenth(obj);
		}
		catch(NoSuchElementException e) {
			throw new ExtensionException(e.getMessage());
		}
	}
	
	public LogoList asLogoList() {
		return LogoList.fromJava(list);
	}
	
	public LogoList asLogoListDeeply() {
		LogoList logo = LogoList.Empty();
		for(Object item: list) {
			if (item instanceof NetLogoMutableList) {
				logo.lput(((NetLogoMutableList)item).asLogoListDeeply());
			}
			else {
				logo.lput(item);
			}
		}
		return logo;
	}
	
	public void reverse() {
		list.reverse();
	}
	
	public void sort(Comparator<Object> comparator) {
		Collections.sort(list, comparator);
	}

	@Override
	public String dump(boolean readable, boolean exporting, boolean reference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getExtensionName() {
		return MutableListExtension.EXTENSION_NAME;
	}

	@Override
	public String getNLTypeName() {
		return "ls";
	}

	@Override
	public boolean recursivelyEqual(Object obj) {
		return list.equals(obj);
	}

	@Override
	public Iterator<Object> iterator() {
		return list.iterator();
	}

}
