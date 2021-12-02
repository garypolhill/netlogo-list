/**
 * 
 */
package uk.ac.hutton.netlogo.list;

import java.util.ListIterator;

import org.nlogo.api.Argument;
import org.nlogo.api.ExtensionException;
import org.nlogo.core.ExtensionObject;

/**
 * NetLogo extension object for wrapping ListIterator functionality, except
 * commands to do with providing random access to a list. If you want random
 * access to a list, you don't want a list!
 * 
 * @author gary
 */
public class NetLogoMutableListIndex implements ExtensionObject {
	private ListIterator<Object> ix;
	private final NetLogoMutableList list;
	public static final String DATA_TYPE_NAME = "index";
	private static long next_id = 0L;
	private final long id;

	/**
	 * Constructor
	 */
	public NetLogoMutableListIndex(NetLogoMutableList list) {
		ix = list.getList().listIterator();
		this.list = list;
		id = next_id;
		next_id++;
	}

	/**
	 * @return the next Object in the list, advancing the cursor one to the right
	 */
	public Object next() {
		return ix.next();
	}

	/**
	 * @return the previous Object in the list, advancing the cursor one to the
	 *         right
	 */
	public Object previous() {
		return ix.previous();
	}

	/**
	 * @return <code>true</code> iff the next call to next() will not fail
	 */
	public boolean hasNext() {
		return ix.hasNext();
	}

	/**
	 * @return <code>true</code> iff the next call to previous() will not fail
	 */
	public boolean hasPrevious() {
		return ix.hasPrevious();
	}

	/**
	 * Insert something in to the list at the cursor
	 * 
	 * @param obj the object to insert
	 */
	public void add(Object obj) {
		list.incSize();
		ix.add(obj);
	}

	/**
	 * Remove the object at the cursor
	 */
	public void remove() {
		list.decSize();
		ix.remove();
	}

	/**
	 * Change the object at the cursor to that of the argument
	 * 
	 * @param obj the new object to store here
	 */
	public void set(Object obj) {
		ix.set(obj);
	}

	/**
	 * Utility method to convert a NetLogo API argument to a command into a
	 * NetLogoMutableListIndex
	 * 
	 * @param arg
	 * @return
	 * @throws ExtensionException
	 */
	public static NetLogoMutableListIndex asNetLogoMutableListIndex(Argument arg) throws ExtensionException {
		return asNetLogoMutableListIndex(arg.get());
	}

	public static NetLogoMutableListIndex asNetLogoMutableListIndex(Object obj) throws ExtensionException {
		if (obj instanceof NetLogoMutableListIndex) {
			return (NetLogoMutableListIndex) obj;
		} else {
			throw new ExtensionException("A " + obj.getClass().getSimpleName() + " (value \"" + obj.toString()
					+ "\") has been given as an argument where a " + MutableListExtension.EXTENSION_NAME + ":"
					+ DATA_TYPE_NAME + " was expected");
		}
	}

	/**
	 * Provide a string dump of the iterator -- the lack of documentation for what
	 * is expected of this method means I've just mirrored the table extension's
	 * implementation.
	 */
	@Override
	public String dump(boolean readable, boolean exporting, boolean reference) {
		if (exporting && reference) {
			return Long.toString(id);
		} else {
			return (exporting ? (id + ": [!") : "[!") + list.getID() + "!]<" + ix.previousIndex() + ", "
					+ ix.nextIndex() + ">";
		}
	}

	/**
	 * Get the name of the extension
	 */
	@Override
	public String getExtensionName() {
		return MutableListExtension.EXTENSION_NAME;
	}

	/**
	 * Get the name of the data type
 	 */
	@Override
	public String getNLTypeName() {
		return DATA_TYPE_NAME;
	}

	/**
	 * Check equality
	 */
	@Override
	public boolean recursivelyEqual(Object obj) {
		if(obj instanceof NetLogoMutableListIndex) {
			NetLogoMutableListIndex other = (NetLogoMutableListIndex)obj;
			if (list == other.list && ix.equals(other.ix)) {
				// It doesn't seem very likely
				return true;
			}
		}
		return false;
	}

}
