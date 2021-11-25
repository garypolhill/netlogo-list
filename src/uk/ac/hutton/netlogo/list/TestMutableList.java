/**
 * 
 */
package uk.ac.hutton.netlogo.list;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.junit.Test;

/**
 * @author gary
 *
 */
public class TestMutableList {

	/**
	 * Test method for {@link uk.ac.hutton.netlogo.list.MutableList#MutableList()}.
	 */
	@Test
	public void testMutableList() {
		MutableList list = new MutableList();
		assertTrue("check empty", list.isEmpty());
		assertEquals("size is " + list.size(), 0, list.size());
		assertTrue(list.check(true, "MutableList()"));
	}

	/**
	 * Test method for
	 * {@link uk.ac.hutton.netlogo.list.MutableList#MutableList(java.util.Collection)}.
	 */
	@Test
	public void testMutableListCollectionOfQextendsObject() {
		Integer array[] = new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		MutableList list = new MutableList(Arrays.asList(array));
		assertTrue(list.check(true, "MutableList(Collection)"));
		assertFalse("check not empty", list.isEmpty());
		assertEquals("size is " + list.size(), array.length, list.size());
		for (int i = 0; i < array.length; i++) {
			assertEquals("array[" + i + "] = " + array[i], array[i], list.get(i));
		}
	}

	/**
	 * Test method for {@link uk.ac.hutton.netlogo.list.MutableList#size()}.
	 */
	@Test
	public void testSize() {
		// size is tested elsewhere
	}

	/**
	 * Test method for {@link uk.ac.hutton.netlogo.list.MutableList#isEmpty()}.
	 */
	@Test
	public void testIsEmpty() {
		// isEmpty is tested elsewhere
	}

	/**
	 * Test method for {@link uk.ac.hutton.netlogo.list.MutableList#get(int)}.
	 */
	@Test
	public void testGet() {
		MutableList list = new MutableList(randomIntList(100));
		try {
			list.get(-1);
			fail("Access negative index without Exception");
		} catch (IndexOutOfBoundsException e) {
			// Pass
		} catch (Exception e) {
			fail("Unexpected " + e.getClass().getSimpleName() + " message: " + e.getMessage());
		}
		try {
			list.get(list.size());
			fail("Access index that's too high without Exception");
		} catch (IndexOutOfBoundsException e) {
			// Pass
		} catch (Exception e) {
			fail("Unexpected " + e.getClass().getSimpleName() + " message: " + e.getMessage());
		}
	}

	/**
	 * Test method for
	 * {@link uk.ac.hutton.netlogo.list.MutableList#subList(int, int)}.
	 */
	@SuppressWarnings("unused")
	@Test
	public void testSubList() {
		Double array[] = new Double[] { -0.0, -1.0, -2.0, -3.0, -4.0, -5.0, -6.0, -7.0, -8.0, -9.0 };
		MutableList list = new MutableList(Arrays.asList(array));
		try {
			List<Object> subList = list.subList(3, 7);
			fail("subList() should throw an exception");
		} catch (UnsupportedOperationException e) {
			// OK
		} catch (Exception e) {
			fail("Unexpected " + e.getClass().getSimpleName() + " message : " + e.getMessage());
		}
	}

	/**
	 * Test method for
	 * {@link uk.ac.hutton.netlogo.list.MutableList#subListCopy(int, int)}.
	 */
	@Test
	public void testSubListCopy() {
		String array[] = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j" };
		MutableList list = new MutableList(Arrays.asList(array));
		final int f1 = 3;
		final int f2 = 8;
		final int t1 = 7;
		final int t2 = 1;
		MutableList subList1 = list.subListCopy(f1, t1);
		assertTrue(subList1.check(true, "subListCopy()"));
		assertEquals(t1 - f1, subList1.size());
		for (int i = 0; i < subList1.size(); i++) {
			subList1.set(i, subList1.get(i).toString().toUpperCase());
		}
		for (int i = 0; i < array.length; i++) {
			assertEquals(array[i], list.get(i));
		}
		int n_checked = 0;
		for (Object o : subList1) {
			assertTrue(Character.isUpperCase(o.toString().charAt(0)));
			n_checked++;
		}
		assertEquals(t1 - f1, n_checked);

		MutableList subList2 = list.subListCopy(f2, t2);
		assertTrue(subList2.check(true, "subListCopy()"));
		assertEquals(f2 - t2, subList2.size());

		n_checked = 0;
		int j = f2;
		for (Object o : subList2) {
			j--;
			assertEquals(array[j], o.toString());
		}
		assertEquals(t2, j);

		for (int i = 0; i < array.length; i++) {
			if (i >= t2 && i < f2) {
				assertTrue("array[" + i + "] = " + array[i], subList2.contains(array[i]));
			} else {
				assertFalse("array[" + i + "] = " + array[i], subList2.contains(array[i]));
			}
		}
	}

	/**
	 * Test method for
	 * {@link uk.ac.hutton.netlogo.list.MutableList#set(int, java.lang.Object)}.
	 */
	@Test
	public void testSet() {
		MutableList list = new MutableList();
		list.add("Something");
		assertEquals(1, list.size());
		list.set(0, "Something Else");
		assertEquals(1, list.size());
		assertEquals("Something Else", list.get(0));
		try {
			list.set(0, null);
			fail("Cannot set list item to null");
		} catch (Throwable e) {
			assertEquals(NullPointerException.class, e.getClass());
		}
		try {
			list.set(-1, "Another Thing");
			fail("Cannot set negative list entry");
		} catch (Throwable e) {
			assertEquals(IndexOutOfBoundsException.class, e.getClass());
		}
		try {
			list.set(1, "And One More Thing");
			fail("Cannot set list item = size()");
		} catch (Throwable e) {
			assertEquals(IndexOutOfBoundsException.class, e.getClass());
		}
	}

	/**
	 * Test method for
	 * {@link uk.ac.hutton.netlogo.list.MutableList#contains(java.lang.Object)}.
	 */
	@Test
	public void testContains() {
		Object[] array = new Object[] { new Integer(3), new Double(Math.PI), new String("Hello World"),
				new Byte[] { 0, 1, 2 }, new Character('a') };
		MutableList list = new MutableList(Arrays.asList(array));
		for (Object o : array) {
			assertTrue(o.toString(), list.contains(o));
		}
		assertTrue("new 3", list.contains(new Integer(3)));
		assertTrue("new PI", list.contains(new Double(Math.PI)));
		assertTrue("new Hello World", list.contains(new String("Hello World")));
		// This fails presumably because array equality isn't deep
		// assertTrue("new Byte array", list.contains(new Byte[] { 0, 1, 2 }));
		assertTrue("const 3", list.contains(3));
		assertTrue("const PI", list.contains(Math.PI));
		assertTrue("const Hello World", list.contains("Hello World"));
		assertFalse("-3456L", list.contains(new Long(-3456L)));
		assertFalse("Hello There!", list.contains("Hello There!"));
		assertFalse("Integer array", list.contains(new Integer[] { 1, 2, 3 }));
	}

	/**
	 * @param maxLength maximum length of the array
	 * @return a list of length at least 1 and no longer than maxLength containing
	 *         random integers none of which are 0
	 */
	public List<Integer> randomIntList(int maxLength) {
		maxLength--;
		int len = 1 + (int) Math.floor(Math.random() * maxLength);
		double range = (double) ((long) Integer.MAX_VALUE - (long) Integer.MIN_VALUE);
		Integer[] array = new Integer[len];
		for (int i = 0; i < array.length; i++) {
			do {
				array[i] = (int) ((double) Integer.MIN_VALUE + Math.rint(Math.random() * range));
			} while (array[i] == 0);
		}
		return Arrays.asList(array);
	}

	/**
	 * @param len desired length of the array
	 * @return a list of length len containing random numbers in the range [0.0,
	 *         1.0[
	 */
	public List<Double> randomDoubleList(int len) {
		Double[] array = new Double[len];
		for (int i = 0; i < array.length; i++) {
			array[i] = Math.random();
		}
		return Arrays.asList(array);
	}

	/**
	 * Test method for
	 * {@link uk.ac.hutton.netlogo.list.MutableList#containsAll(java.util.Collection)}.
	 */
	@Test
	public void testContainsAll() {
		for (int rpt = 0; rpt < 100; rpt++) {
			List<Integer> rand = randomIntList(1000);
			MutableList list = new MutableList(rand);
			assertTrue("list contains rand", list.containsAll(rand));
			rand.set(0, 0);
			assertFalse("list does not contain 0", list.containsAll(rand));
			list.clear();
		}
	}

	/**
	 * Test method for
	 * {@link uk.ac.hutton.netlogo.list.MutableList#indexOf(java.lang.Object)}.
	 */
	@Test
	public void testIndexOf() {
		String[] array = new String[] { "zero", "one", "two", "one", "zero", "one", "minus one", "two" };
		MutableList list = new MutableList(Arrays.asList(array));
		assertEquals(0, list.indexOf(array[0]));
		assertEquals(0, list.indexOf(array[4]));
		assertEquals(0, list.indexOf("zero"));
		assertEquals(0, list.indexOf(new String("zero")));
		assertEquals(1, list.indexOf(array[1]));
		assertEquals(1, list.indexOf(array[5]));
		assertEquals(1, list.indexOf("one"));
		assertEquals(1, list.indexOf(new String("one")));
		assertEquals(2, list.indexOf(array[2]));
		assertEquals(2, list.indexOf(array[7]));
		assertEquals(2, list.indexOf("two"));
		assertEquals(2, list.indexOf(new String("two")));
		assertEquals(-1, list.indexOf("not there"));

		assertEquals(4, list.lastIndexOf(array[0]));
		assertEquals(4, list.lastIndexOf(array[4]));
		assertEquals(4, list.lastIndexOf("zero"));
		assertEquals(4, list.lastIndexOf(new String("zero")));
		assertEquals(5, list.lastIndexOf(array[1]));
		assertEquals(5, list.lastIndexOf(array[5]));
		assertEquals(5, list.lastIndexOf("one"));
		assertEquals(5, list.lastIndexOf(new String("one")));
		assertEquals(7, list.lastIndexOf(array[2]));
		assertEquals(7, list.lastIndexOf(array[7]));
		assertEquals(7, list.lastIndexOf("two"));
		assertEquals(7, list.lastIndexOf(new String("two")));
	}

	/**
	 * Test method for
	 * {@link uk.ac.hutton.netlogo.list.MutableList#lastIndexOf(java.lang.Object)}.
	 */
	@Test
	public void testLastIndexOf() {
		// done in testIndexOf()
	}

	/**
	 * Test method for {@link uk.ac.hutton.netlogo.list.MutableList#iterator()}.
	 */
	@Test
	public void testIterator() {
		List<Double> rand = randomDoubleList(10000);
		MutableList list = new MutableList(rand);
		Iterator<Object> lix = list.iterator();
		Iterator<Double> rix = rand.iterator();

		while (lix.hasNext()) {
			Object lo = lix.next();
			Double ro = rix.next();
			assertTrue(lo.equals(ro));
		}
		assertFalse(lix.hasNext());
		assertFalse(rix.hasNext());
	}

	/**
	 * Test method for {@link uk.ac.hutton.netlogo.list.MutableList#listIterator()}.
	 */
	@Test
	public void testListIterator() {
		String[] array = new String[] { "a", "b", "c", "d", "e" };
		MutableList list = new MutableList(Arrays.asList(array));
		ListIterator<Object> ix = list.listIterator();
		ix.add("z");
		assertEquals(array.length + 1, list.size());
		assertEquals("z", ix.previous());
		int j = 0;
		while (ix.hasNext()) {
			assertEquals(array[j], ix.next());
			j++;
		}
		ix.add("z");
		assertEquals(array.length + 2, list.size());
		assertEquals("zabcdze", list.asPrintableString("", "", ""));
		try {
			ix.next();
		} catch (Throwable e) {
			assertEquals(NoSuchElementException.class, e.getClass());
		}
		assertEquals("z", ix.previous());
		ix.remove();

		assertEquals(array.length + 1, list.size());
		while (ix.hasPrevious() && j > 0) {
			j--;
			assertEquals(array[j], j == (array.length - 1) ? ix.next() : ix.previous());
		}
		assertEquals("z", ix.previous());
		try {
			ix.previous();
		} catch (Throwable e) {
			assertEquals(NoSuchElementException.class, e.getClass());
		}
		ix.remove();
		assertEquals(array.length, list.size());
		while (ix.hasNext()) {
			assertEquals(array[j], ix.next());
			j++;
		}
	}

	/**
	 * Test method for
	 * {@link uk.ac.hutton.netlogo.list.MutableList#listIterator(int)}.
	 */
	@Test
	public void testListIteratorInt() {
		List<Double> rand = randomDoubleList(1000);
		MutableList list = new MutableList(rand);
		for (int i = 0; i <= rand.size(); i++) {
			ListIterator<Double> rix = rand.listIterator(i);
			ListIterator<Object> lix = list.listIterator(i);
			assertEquals(rix.previousIndex(), lix.previousIndex());
			assertEquals(rix.nextIndex(), lix.nextIndex());
			if (i % 2 == 0) {
				while (rix.hasNext() && lix.hasNext()) {
					assertEquals(rix.next(), lix.next());
					assertEquals(rix.nextIndex(), lix.nextIndex());
				}
			} else {
				int j = 0;
				while (rix.hasPrevious() && lix.hasPrevious()) {
					j++;
					assertEquals(rix.previous(), lix.previous());
					assertEquals("i is " + i + "; j is " + j, rix.previousIndex(), lix.previousIndex());
				}
			}
			assertEquals(rix.hasPrevious(), lix.hasPrevious());
		}
	}

	/**
	 * Test method for
	 * {@link uk.ac.hutton.netlogo.list.MutableList#depthFirstIterator()}.
	 */
	@Test
	public void testDepthFirstIterator() {
		String[][] arr = new String[][] { new String[] { "a", "b", "?", "c", "?", "d", "e" },
				new String[] { "f", "g", "?", "?", "h" }, new String[] { "i", "j", "k" },
				new String[] { "l", "m", "?", "?", "?" }, new String[] { "n", "o" }, new String[] { "?", "p", "q" },
				new String[] { "?", "?" }, new String[] { "r" }, new String[] { "s", "t" },
				new String[] { "u", "v", "w" }, new String[] { "x", "y", "z" } };
		List<String> answer = Arrays.asList(new String[] { "a", "b", "f", "g", "l", "m", "u", "v", "w", "x", "y", "z",
				"s", "t", "p", "q", "r", "n", "o", "h", "c", "i", "j", "k", "d", "e" });

		MutableList list = new MutableList();
		MutableList[] leaves = new MutableList[] { new MutableList(Arrays.asList(arr[2])),
				new MutableList(Arrays.asList(arr[4])), new MutableList(Arrays.asList(arr[7])),
				new MutableList(Arrays.asList(arr[8])), new MutableList(Arrays.asList(arr[9])),
				new MutableList(Arrays.asList(arr[10])) };

		MutableList[] branches = new MutableList[] { new MutableList(), new MutableList(), new MutableList(),
				new MutableList() };

		branches[3].add(leaves[4]);
		branches[3].add(leaves[5]);
		branches[2].add(leaves[3]);
		branches[2].add(arr[5][1]);
		branches[2].add(arr[5][2]);
		branches[1].add(arr[3][0]);
		branches[1].add(arr[3][1]);
		branches[1].add(branches[3]);
		branches[1].add(branches[2]);
		branches[1].add(leaves[2]);
		branches[0].add(arr[1][0]);
		branches[0].add(arr[1][1]);
		branches[0].add(branches[1]);
		branches[0].add(leaves[1]);
		branches[0].add(arr[1][4]);
		list.add(arr[0][0]);
		list.add(arr[0][1]);
		list.add(branches[0]);
		list.add(arr[0][3]);
		list.add(leaves[0]);
		list.add(arr[0][5]);
		list.add(arr[0][6]);

		Iterator<Object> dfix = list.depthFirstIterator();
		Iterator<String> aix = answer.iterator();

		while (dfix.hasNext() && aix.hasNext()) {
			assertEquals(aix.next(), dfix.next());
		}
		assertEquals(aix.hasNext(), dfix.hasNext());
	}

	/**
	 * Test method for {@link uk.ac.hutton.netlogo.list.MutableList#toArray()}.
	 */
	@Test
	public void testToArray() {
		List<Integer> rand = randomIntList(1000);
		MutableList list = new MutableList(rand);

		Object[] arr_rnd = rand.toArray();
		Object[] arr_lst = list.toArray();

		assertEquals(arr_rnd.length, arr_lst.length);
		for (int i = 0; i < arr_rnd.length; i++) {
			assertEquals(arr_rnd[i], arr_lst[i]);
		}
	}

	/**
	 * Test method for {@link uk.ac.hutton.netlogo.list.MutableList#toArray(T[])}.
	 */
	@Test
	public void testToArrayTArray() {
		List<Double> rand = randomDoubleList(1000);
		MutableList list = new MutableList(rand);

		Double[] arr_rnd = rand.toArray(new Double[0]);
		Double[] arr_lst = list.toArray(new Double[0]);

		assertEquals(arr_rnd.length, arr_lst.length);
		for (int i = 0; i < arr_rnd.length; i++) {
			assertEquals(arr_rnd[i], arr_lst[i]);
		}
	}

	/**
	 * Test method for
	 * {@link uk.ac.hutton.netlogo.list.MutableList#add(java.lang.Object)}.
	 */
	@Test
	public void testAddObject() {
		MutableList list = new MutableList(Arrays.asList(new String[] { "a", "b", "c", "d", "e" }));
		assertTrue(list.add("f"));
		assertTrue(list.check(true, "add(Object)"));
		String[] test = new String[] { "a", "b", "c", "d", "e", "f" };
		assertEquals(test.length, list.size());
		for (int i = 0; i < test.length; i++) {
			assertEquals(test[i], list.get(i));
		}
	}

	/**
	 * Test method for
	 * {@link uk.ac.hutton.netlogo.list.MutableList#addAll(java.util.Collection)}.
	 */
	@Test
	public void testAddAllCollectionOfQextendsObject() {
		List<Integer> rnd1 = randomIntList(1000);
		List<Integer> rnd2 = randomIntList(1000);
		MutableList list = new MutableList();
		assertTrue(list.addAll(rnd1));
		assertTrue(list.check(true, "addAll(Collection 1)"));
		assertEquals(rnd1.size(), list.size());
		assertTrue(list.addAll(rnd2));
		assertTrue(list.check(true, "addAll(Collection 2)"));
		assertEquals(rnd1.size() + rnd2.size(), list.size());
		Iterator<Integer> rix = rnd1.iterator();
		Iterator<Object> lix = list.iterator();

		while (lix.hasNext()) {
			if (!rix.hasNext()) {
				rix = rnd2.iterator();
			}
			assertEquals(rix.next(), lix.next());
		}
	}

	/**
	 * Test method for
	 * {@link uk.ac.hutton.netlogo.list.MutableList#unshift(java.lang.Object)}.
	 */
	@Test
	public void testUnshift() {
		String[] arr = new String[] { "alpha", "bravo", "charlie", "delta", "echo", "fox-trot", "golf" };
		MutableList list = new MutableList();
		for (int i = 0; i < arr.length; i++) {
			list.unshift(arr[i]);
			assertTrue(list.check(true, "unshift(Object)"));
			assertEquals(arr[i], list.get(0));
			assertEquals(i + 1, list.size());
		}
		for (int i = 0; i < arr.length; i++) {
			assertEquals(arr[i], list.get(arr.length - (1 + i)));
		}
	}

	/**
	 * Test method for
	 * {@link uk.ac.hutton.netlogo.list.MutableList#push(java.lang.Object)}.
	 */
	@Test
	public void testPush() {
		// Tested with add()
	}

	/**
	 * Test method for
	 * {@link uk.ac.hutton.netlogo.list.MutableList#add(int, java.lang.Object)}.
	 */
	@Test
	public void testAddIntObject() {
		String[] arr = new String[] { "alpha", "bravo", "charlie", "delta", "echo", "fox-trot", "golf", "hotel",
				"india" };
		MutableList list = new MutableList(Arrays.asList(arr));
		String[] a = new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I" };
		for (int i = 0; i < a.length; i++) {
			list.add(1 + (2 * i), a[i]);
			assertTrue(list.check(true, "add(int, Object)"));
			assertEquals(a[i], list.get(1 + (2 * i)));
			assertEquals(arr[i], list.get(2 * i));
			assertEquals(arr.length + i + 1, list.size());
		}
		for (int i = 0; i < a.length; i++) {
			assertEquals(arr[i], list.get(2 * i));
			assertEquals(a[i], list.get(1 + (2 * i)));
		}
	}

	/**
	 * Test method for
	 * {@link uk.ac.hutton.netlogo.list.MutableList#addAll(int, java.util.Collection)}.
	 */
	@Test
	public void testAddAllIntCollectionOfQextendsObject() {
		String[] arr1 = new String[] { "one", "two", "three", "four", "five", "six", "seven" };
		String[] arr2 = new String[] { "is", "a", "number" };
		MutableList list = new MutableList();
		assertTrue(list.addAll(0, Arrays.asList(arr1)));
		assertTrue(list.check(true, "addAll(int, Collection)"));
		assertEquals(arr1.length, list.size());
		for (int i = 0; i < arr1.length; i++) {
			assertEquals(arr1[i], list.get(i));
		}
		for (int i = 0; i <= arr1.length; i++) {
			assertTrue(list.addAll(i * (1 + arr2.length), Arrays.asList(arr2)));
			assertTrue(list.check(true, "addAll(int, Collection " + i + ")"));
			assertEquals(arr1.length + ((i + 1) * arr2.length), list.size());
		}
		for (int i = 0; i <= arr1.length; i++) {
			for (int j = 0; j < arr2.length; j++) {
				assertEquals(arr2[j], list.get((i * (arr2.length + 1)) + j));
			}
			if (i < arr1.length) {
				assertEquals(arr1[i], list.get((arr2.length * (i + 1)) + i));
			}
		}
	}

	/**
	 * Test method for
	 * {@link uk.ac.hutton.netlogo.list.MutableList#remove(java.lang.Object)}.
	 */
	@Test
	public void testRemoveObject() {
		String[] arr = new String[] { "1", "2", "3", "4", "5", "5" };
		MutableList list = new MutableList(Arrays.asList(arr));
		assertEquals(arr.length, list.size());
		assertTrue(list.remove(arr[5]));
		assertTrue(list.check(true, "remove(Object)"));
		assertEquals(arr.length - 1, list.size());
		for (int i = 0; i < arr.length - 1; i++) {
			assertEquals(arr[i], list.get(i));
		}
		assertFalse(list.remove("6"));
	}

	/**
	 * Test method for
	 * {@link uk.ac.hutton.netlogo.list.MutableList#removeAll(java.util.Collection)}.
	 */
	@Test
	public void testRemoveAll() {
		String[] arr = new String[] { "Money", "Money", "Money", "Must", "Be", "Funny", "In", "A", "Rich", "Man's",
				"World", "Money", "Money", "Money", "Always", "Sunny", "In", "A", "Rich", "Man's", "World" };
		String[] del = new String[] { "Money", "Funny", "In", "Rich", "Man's" };
		String[] ans = new String[] { "Must", "Be", "A", "World", "Always", "Sunny", "A", "World" };
		MutableList list = new MutableList(Arrays.asList(arr));
		MutableList ansl = new MutableList(Arrays.asList(ans));
		assertFalse(list.equals(ansl));
		assertTrue(list.removeAll(Arrays.asList(del)));
		assertTrue(list.check(true, "removeAll(Collection)"));
		assertTrue(list.equals(ansl));
		assertFalse(list.removeAll(Arrays.asList(del)));
	}

	/**
	 * Test method for
	 * {@link uk.ac.hutton.netlogo.list.MutableList#retainAll(java.util.Collection)}.
	 */
	@Test
	public void testRetainAll() {
		String[] arr = new String[] { "Money", "Money", "Money", "Must", "Be", "Funny", "In", "A", "Rich", "Man's",
				"World", "Money", "Money", "Money", "Always", "Sunny", "In", "A", "Rich", "Man's", "World" };
		String[] del = new String[] { "Money", "Funny", "In", "Rich", "Man's" };
		String[] ans = new String[] { "Money", "Money", "Money", "Funny", "In", "Rich", "Man's", "Money", "Money",
				"Money", "In", "Rich", "Man's" };
		MutableList list = new MutableList(Arrays.asList(arr));
		MutableList ansl = new MutableList(Arrays.asList(ans));
		assertFalse(list.equals(ansl));
		assertTrue(list.retainAll(Arrays.asList(del)));
		assertTrue(list.check(true, "retainAll(Collection)"));
		assertTrue(list.equals(ansl));
		assertFalse(list.retainAll(Arrays.asList(del)));
	}

	/**
	 * Test method for {@link uk.ac.hutton.netlogo.list.MutableList#remove(int)}.
	 */
	@Test
	public void testRemoveInt() {
		String[] arr = new String[] { "Norb", "is", "not", "a", "bedbiter" };
		MutableList list = new MutableList(Arrays.asList(arr));
		assertEquals(arr.length, list.size());
		assertEquals(arr[2], list.remove(2));
		assertTrue(list.check(true, "remove(int)"));
		assertEquals(arr.length - 1, list.size());
		for(int i = 0; i < arr.length - 1; i++) {
			if(i < 2) {
				assertEquals(arr[i], list.get(i));
			}
			else {
				assertEquals(arr[i + 1], list.get(i));
			}
		}
	}

	/**
	 * Test method for {@link uk.ac.hutton.netlogo.list.MutableList#shift()}.
	 */
	@Test
	public void testShift() {
		String[] arr = new String[] { "alpha", "bravo", "charlie", "delta", "echo", "fox-trot", "golf" };
		MutableList list = new MutableList(Arrays.asList(arr));
		for (int i = 0; i < arr.length; i++) {
			assertEquals(arr[i], list.shift());
			assertTrue(list.check(true, "shift()"));
		}
		assertTrue(list.isEmpty());
		try {
			list.shift();
			fail("Should not be able to shift() from empty list");
		} catch (Throwable e) {
			assertEquals(e.getClass(), NoSuchElementException.class);
		}
	}

	/**
	 * Test method for {@link uk.ac.hutton.netlogo.list.MutableList#pop()}.
	 */
	@Test
	public void testPop() {
		List<Double> test = randomDoubleList(10000);
		MutableList list = new MutableList(test);
		
		Collections.reverse(test);
		
		for(Double d: test) {
			assertEquals(d, list.pop());
			assertTrue(list.check(true, "pop()"));
		}
		
		assertTrue(list.isEmpty());
	}

	/**
	 * Test method for {@link uk.ac.hutton.netlogo.list.MutableList#clear()}.
	 */
	@Test
	public void testClear() {
		MutableList list = new MutableList(randomDoubleList(10000));
		assertEquals(10000, list.size());
		list.clear();
		assertEquals(0, list.size());
	
	}

	/**
	 * Test method for {@link uk.ac.hutton.netlogo.list.MutableList#clone()}.
	 */
	@Test
	public void testClone() {
		MutableList list = new MutableList(randomDoubleList(10000));
		MutableList copy = list.clone();
		assertTrue(list.check(true, "clone()"));
		assertTrue(list.equals(copy));
		list.clear();
		assertEquals(0, list.size());
		assertEquals(10000, copy.size());
	}

	/**
	 * Test method for {@link uk.ac.hutton.netlogo.list.MutableList#reverseCopy()}.
	 */
	@Test
	public void testReverseCopy() {
		String[] arr = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m" };
		MutableList list = new MutableList(Arrays.asList(arr));
		MutableList revc = list.reverseCopy();
		assertTrue(revc.check(true, "reverseCopy()"));
		assertEquals(list.size(), revc.size());
		ListIterator<Object> lix = list.listIterator(0);
		ListIterator<Object> rix = revc.listIterator(arr.length);
		int j = 0;
		while(lix.hasNext()) {
			j++;
			assertEquals("j is " + j, lix.next(), rix.previous());
		}
		assertEquals(arr.length, j);
	}

	/**
	 * Test method for {@link uk.ac.hutton.netlogo.list.MutableList#reverse()}.
	 */
	@Test
	public void testReverse() {
		String[] arr = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m" };
		MutableList list = new MutableList(Arrays.asList(arr));
		list.reverse();
		assertTrue(list.check(true, "reverse()"));
		assertEquals(arr.length, list.size());
		ListIterator<Object> ix = list.listIterator();
		for(int i = arr.length - 1; i >= 0; i--) {
			assertEquals(arr[i], ix.next());
		}
	}

	/**
	 * Test method for
	 * {@link uk.ac.hutton.netlogo.list.MutableList#asPrintableString()}.
	 */
	@Test
	public void testAsPrintableString() {
		MutableList list = new MutableList(Arrays.asList(new String[] { "a", "b" }));
		list.add(new MutableList(Arrays.asList(new String[] { "c", "d", "e" })));
		list.addAll(Arrays.asList(new String[] { "f", "g", "h" }));
		assertEquals("[ a, b, [ c, d, e ], f, g, h ]", list.asPrintableString());
	}

	/**
	 * Test method for
	 * {@link uk.ac.hutton.netlogo.list.MutableList#asPrintableString(java.lang.String)}.
	 */
	@Test
	public void testAsPrintableStringString() {
		// tested by testAsPrintableString()
	}

	/**
	 * Test method for
	 * {@link uk.ac.hutton.netlogo.list.MutableList#asPrintableString(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAsPrintableStringStringStringString() {
		// tested by testAsPrintableString()
	}

	/**
	 * Test method for
	 * {@link uk.ac.hutton.netlogo.list.MutableList#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		// tested by testClone()
	}

}
