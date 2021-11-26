/**
 * 
 */
package uk.ac.hutton.netlogo.list;

import java.util.ListIterator;

import org.nlogo.core.ExtensionObject;

/**
 * @author gary
 *
 */
public class NetLogoMutableListIndex implements ExtensionObject {
	private ListIterator<Object> ix;
	private final NetLogoMutableList list;

	/**
	 * 
	 */
	public NetLogoMutableListIndex(NetLogoMutableList list) {
		ix = list.getList().listIterator();
		this.list = list;
	}
	
	public Object next() {
		return ix.next();
	}
	
	public Object previous() {
		return ix.previous();
	}
	
	public boolean hasNext() {
		return ix.hasNext();
	}
	
	public boolean hasPrevious() {
		return ix.hasPrevious();
	}
	
	public void add(Object obj) {
		list.incSize();
		ix.add(obj);
	}
	
	public void remove() {
		list.decSize();
		ix.remove();
	}
	
	public void set(Object obj) {
		ix.set(obj);
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
		return "ix";
	}

	@Override
	public boolean recursivelyEqual(Object obj) {
		return ix.equals(obj);
	}



}
