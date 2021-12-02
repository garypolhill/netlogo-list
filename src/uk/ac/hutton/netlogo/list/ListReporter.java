/**
 * 
 */
package uk.ac.hutton.netlogo.list;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.nlogo.api.Agent;
import org.nlogo.api.AgentSet;
import org.nlogo.api.AnonymousReporter;
import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.Reporter;
import org.nlogo.core.LogoList;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;

/**
 * Class for all the NetLogo commands on a NetLogoMutableList object that return
 * a result. The implementation implements several commands using an
 * <code>enum</code> of all the commands and a local variable for the object
 * that contains which of these commands this object is implementing.
 * 
 * @author gary
 */
public class ListReporter implements Reporter {
	public enum Name {
		AS_LIST, AS_LIST_DEEPLY, COUNTS, COPY, CURSOR, DEEP_MEMBER, FROM_AGENTSET, FROM_LIST, HISTOGRAM, INTERSECTS,
		IS_EMPTY, IS_LIST, LENGTH, MAKE, MAP, MAX, MEAN, MEDIAN, MEMBER, MEMBER_ANY, MIN, MODES, POP, QUARTILES, RANGE,
		REDUCE, SHIFT, SUM, FIRST, SECOND, THIRD, FOURTH, FIFTH, SIXTH, SEVENTH, EIGHTH, NINTH, TENTH
	};

	private final Name cmd;

	/**
	 * Constructor, passing in the command that this instance is implementing
	 */
	public ListReporter(Name cmd) {
		this.cmd = cmd;
	}

	/**
	 * @return the syntax of the command
	 */
	@Override
	public Syntax getSyntax() {
		switch (cmd) {
		case AS_LIST:
			// Same as AS_LIST_DEEPLY
		case AS_LIST_DEEPLY:
			return SyntaxJ.reporterSyntax(new int[] { Syntax.WildcardType() }, Syntax.ListType());
		case COUNTS:
			return SyntaxJ.reporterSyntax(new int[] { Syntax.WildcardType() }, Syntax.ListType());
		case COPY:
			return SyntaxJ.reporterSyntax(new int[] { Syntax.WildcardType() }, Syntax.WildcardType());
		case CURSOR:
			return SyntaxJ.reporterSyntax(new int[] { Syntax.WildcardType() }, Syntax.WildcardType());
		case DEEP_MEMBER:
			return SyntaxJ.reporterSyntax(new int[] { Syntax.WildcardType(), Syntax.WildcardType() },
					Syntax.BooleanType());
		case FROM_AGENTSET:
			return SyntaxJ.reporterSyntax(new int[] { Syntax.AgentsetType() }, Syntax.WildcardType());
		case FROM_LIST:
			return SyntaxJ.reporterSyntax(new int[] { Syntax.ListType() }, Syntax.WildcardType());
		case HISTOGRAM:
			return SyntaxJ.reporterSyntax(
					new int[] { Syntax.WildcardType(), Syntax.NumberType(), Syntax.NumberType(), Syntax.NumberType() },
					Syntax.WildcardType());
		case INTERSECTS:
			return SyntaxJ.reporterSyntax(new int[] { Syntax.WildcardType(), Syntax.WildcardType() },
					Syntax.BooleanType());
		case IS_EMPTY:
			return SyntaxJ.reporterSyntax(new int[] { Syntax.WildcardType() }, Syntax.BooleanType());
		case IS_LIST:
			return SyntaxJ.reporterSyntax(new int[] { Syntax.WildcardType() }, Syntax.BooleanType());
		case LENGTH:
			return SyntaxJ.reporterSyntax(new int[] { Syntax.WildcardType() }, Syntax.NumberType());
		case MAKE:
			return SyntaxJ.reporterSyntax(Syntax.WildcardType());
		case MAP:
			return SyntaxJ.reporterSyntax(
					new int[] { Syntax.ReporterType(), Syntax.WildcardType() | Syntax.RepeatableType() },
					Syntax.WildcardType());
		case MAX:
			return SyntaxJ.reporterSyntax(new int[] { Syntax.WildcardType() }, Syntax.NumberType());
		case MEAN:
			return SyntaxJ.reporterSyntax(new int[] { Syntax.WildcardType() }, Syntax.NumberType());
		case MEDIAN:
			return SyntaxJ.reporterSyntax(new int[] { Syntax.WildcardType() }, Syntax.NumberType());
		case MEMBER:
			// Same as MEMBER_ANY
		case MEMBER_ANY:
			return SyntaxJ.reporterSyntax(
					new int[] { Syntax.WildcardType(), Syntax.WildcardType() | Syntax.RepeatableType() },
					Syntax.BooleanType());
		case MIN:
			return SyntaxJ.reporterSyntax(new int[] { Syntax.WildcardType() }, Syntax.NumberType());
		case MODES:
			return SyntaxJ.reporterSyntax(new int[] { Syntax.WildcardType() }, Syntax.ListType());
		case POP:
			return SyntaxJ.reporterSyntax(new int[] { Syntax.WildcardType() }, Syntax.WildcardType());
		case QUARTILES:
			return SyntaxJ.reporterSyntax(new int[] { Syntax.WildcardType() }, Syntax.ListType());
		case RANGE:
			return SyntaxJ.reporterSyntax(
					new int[] { Syntax.NumberType(), Syntax.NumberType(), Syntax.NumberType() | Syntax.OptionalType() },
					Syntax.WildcardType());
		case REDUCE:
			return SyntaxJ.reporterSyntax(new int[] { Syntax.ReporterType(), Syntax.WildcardType() },
					Syntax.WildcardType());
		case SHIFT:
			return SyntaxJ.reporterSyntax(new int[] { Syntax.WildcardType() }, Syntax.WildcardType());
		case SUM:
			return SyntaxJ.reporterSyntax(new int[] { Syntax.WildcardType() }, Syntax.NumberType());
		case FIRST:
		case SECOND:
		case THIRD:
		case FOURTH:
		case FIFTH:
		case SIXTH:
		case SEVENTH:
		case EIGHTH:
		case NINTH:
		case TENTH:
			return SyntaxJ.reporterSyntax(new int[] { Syntax.WildcardType() }, Syntax.WildcardType());
		default:
			throw new RuntimeException("PANIC!");
		}
	}

	/**
	 * Run the reporter. Since not all reporters take a NetLogoMutableList as the
	 * first argument, not all commands are contained in a <code>switch</code>
	 * statement, but are <code>if-else if</code>ed first.
	 * 
	 * @param args    the arguments to the reporter as passed by the NetLogo API
	 * @param context the context of the command's execution as passed by the
	 *                NetLogo API
	 * @return the result of executing the command implemented by this instance
	 */
	@Override
	public Object report(Argument[] args, Context context) throws ExtensionException {
		if (cmd == Name.MAKE) {
			return new NetLogoMutableList();
		} else if (cmd == Name.FROM_LIST) {
			return new NetLogoMutableList(args[0].getList());
		} else if (cmd == Name.FROM_AGENTSET) {
			return new NetLogoMutableList(args[0].getAgentSet());
		} else if (cmd == Name.IS_LIST) {
			return (args[0].get() instanceof NetLogoMutableList);
		} else if (cmd == Name.MAP) {
			return map(args, context);
		} else if (cmd == Name.RANGE) {
			double start = args[0].getDoubleValue();
			double stop = args[1].getDoubleValue();
			double inc = args.length < 3 ? (stop < start ? -1 : 1) : args[2].getDoubleValue();
			if (inc == 0.0) {
				throw new ExtensionException("range command called with zero increment");
			} else if ((inc > 0.0 && start > stop) || (inc < 0.0 && start < stop)) {
				throw new ExtensionException("range command has increment \"" + inc
						+ "\" with wrong sign given start \"" + start + "\" and stop \"" + stop + "\"");
			} else if ((Math.ulp(start) >= Math.abs(inc)) || (Math.ulp(stop) >= Math.abs(inc))) {
				throw new ExtensionException("range command has increment \"" + inc
						+ "\" that is too small given values of start \"" + start + "\" and stop \"" + stop + "\"");
			} else if (!Double.isFinite(start) || !Double.isFinite(stop) || !Double.isFinite(inc)) {
				throw new ExtensionException("range command called with non-finite arguments");
			}
			List<Object> lst = new LinkedList<Object>();
			for (double i = start; (inc < 0.0 && i >= stop) || (inc > 0.0 && i <= stop); i += inc) {
				if (!Double.isFinite(i)) {
					throw new ExtensionException("range command start \"" + start + "\", stop \"" + stop
							+ ", increment \"" + inc + "\" leads to non-finite list element");
				}
				lst.add(new Double(i));
			}
			return new NetLogoMutableList(lst);
		} else if (cmd == Name.REDUCE) {
			return reduce(args, context);
		} else {
			// All of these commands take a list as the first argument
			NetLogoMutableList list = NetLogoMutableList.asNetLogoMutableList(args[0]);
			switch (cmd) {
			// N.B. 'FROM_LIST', 'FROM_AGENTSET', 'IS_LIST' and 'MAKE' handled above
			case AS_LIST:
				return list.asLogoList();
			case AS_LIST_DEEPLY:
				return list.asLogoListDeeply();
			case COPY:
				return list.copy();
			case COUNTS:
				return counts(list);
			case CURSOR:
				return list.getIterator();
			case DEEP_MEMBER:
				return list.deepMember(args[1].get());
			case HISTOGRAM:
				return histogram(list, args[1].getDoubleValue(), args[2].getDoubleValue(), args[3].getDoubleValue());
			case INTERSECTS:
				Object obj = args[1].get();
				Set<Object> elements = new HashSet<Object>();
				if (obj instanceof AgentSet) {
					AgentSet set = (AgentSet) obj;
					for (Agent a : set.agents()) {
						elements.add(a);
					}
				} else if (obj instanceof LogoList) {
					List<Object> lst = ((LogoList) obj).toJava();
					for (Object o : lst) {
						elements.add(o);
					}
				} else if (obj instanceof NetLogoMutableList) {
					for (Object o : ((NetLogoMutableList) obj).getList()) {
						elements.add(o);
					}
				}
				List<Object> unique = new LinkedList<Object>();
				for (Object o : elements) {
					unique.add(o);
				}
				elements.clear();
				boolean result = list.memberAny(unique);
				unique.clear();
				return result;
			case IS_EMPTY:
				return list.size() == 0;
			case LENGTH:
				return new Double((double) list.size());
			case MAX:
				return new Double(getStats(list)[1]);
			case MEAN:
				return new Double(getStats(list)[3]);
			case MEDIAN:
				return new Double(quartiles(list)[1]);
			case MEMBER:
				if (args.length == 2) {
					return new Boolean(list.member(args[1]));
				} else {
					return new Boolean(list.memberAll(ListCommand.getArguments(args)));
				}
			case MEMBER_ANY:
				return new Boolean(list.memberAny(ListCommand.getArguments(args)));
			case MIN:
				return new Double(getStats(list)[0]);
			case MODES:
				return modes(list);
			case POP:
				return list.pop();
			case QUARTILES:
				return LogoList.fromJava(Arrays.asList(quartiles(list)));
			case SHIFT:
				return list.shift();
			case SUM:
				return new Double(getStats(list)[2]);
			case FIRST:
				return list.first();
			case SECOND:
				return list.second();
			case THIRD:
				return list.third();
			case FOURTH:
				return list.fourth();
			case FIFTH:
				return list.fifth();
			case SIXTH:
				return list.sixth();
			case SEVENTH:
				return list.seventh();
			case EIGHTH:
				return list.eighth();
			case NINTH:
				return list.ninth();
			case TENTH:
				return list.tenth();
			default:
				throw new RuntimeException("PANIC!");
			}
		}
	}

	/**
	 * Extract the numbers from a list, returning them as an array of
	 * <code>Double</code>
	 * 
	 * @param list a NetLogoMutableList
	 * @return an array of numbers contained in the list
	 */
	private Double[] getNumbers(NetLogoMutableList list) {
		List<Double> numbers = new LinkedList<Double>();
		for (Object o : list.getList()) {
			if (o instanceof Double) {
				numbers.add((Double) o);
			}
		}
		return numbers.toArray(new Double[0]);
	}

	/**
	 * Extract numerical summaries of the numbers in the list, returning them as an
	 * array of <code>double</code> thus:
	 * 
	 * <ol start=0>
	 * <li>Minimum number found or <code>Double.NaN</code> if the list is empty</li>
	 * <li>Maximum number found or <code>Double.NaN</code> if the list is empty</li>
	 * <li>Sum of numbers found or <code>Double.NaN</code> if the list is empty</li>
	 * <li>Mean of numbers found or <code>Double.NaN</code> if the list is
	 * empty</li>
	 * </ol>
	 * 
	 * @param list a NetLogoMutableList
	 * @return an array containing the summary statistics of the numbers in the list
	 */
	private double[] getStats(NetLogoMutableList list) {
		double min = Double.NaN;
		double max = Double.NaN;
		double sum = Double.NaN;
		double n = 0.0;
		boolean first = true;

		for (Object o : list.getList()) {
			if (o instanceof Double) {
				if (first) {
					min = (Double) o;
					max = (Double) o;
					sum = (Double) o;
					first = false;
				} else {
					sum += (Double) o;
					n += 1.0;
					min = ((Double) o < min) ? (Double) o : min;
					max = ((Double) o > max) ? (Double) o : max;
				}
			}
		}
		return new double[] { min, max, sum, sum / n };
	}

	/**
	 * According to <a href="https://en.wikipedia.org/wiki/Quartile">Wikipedia</a>
	 * there is no standard way of computing the quartiles. This method implements
	 * that referred to in the article as 'Tukey's hinges'. The lower and upper
	 * quartiles are medians of the bottom half of the list and the top half of the
	 * list, respectively; including the median value if the list has odd length.
	 * 
	 * @param list
	 * @return first, second, and third quartiles as an array; the median will be
	 *         <code>Double.NaN</code> if the list is empty; the upper and lower
	 *         quartiles will be <code>Double.NaN</code> if the list has fewer than
	 *         two elements
	 */
	private double[] quartiles(NetLogoMutableList list) {
		Double[] numbers = getNumbers(list);

		// Handle quick cases first with no sorted list
		if (numbers.length == 0) {
			return new double[] { Double.NaN, Double.NaN, Double.NaN };
		} else if (numbers.length == 1) {
			return new double[] { Double.NaN, numbers[0], Double.NaN };
		} else if (numbers.length == 2) {
			return new double[] { numbers[0], (numbers[0] + numbers[1]) / 2.0, numbers[1] };
		}

		Arrays.sort(numbers);

		// Handle quick cases with a sorted list
		if (numbers.length == 3) {
			return new double[] { (numbers[0] + numbers[1]) / 2.0, numbers[1], (numbers[1] + numbers[2]) / 2.0 };
		} else if (numbers.length == 4) {
			return new double[] { (numbers[0] + numbers[1]) / 2.0, (numbers[1] + numbers[2]) / 2.0,
					(numbers[2] + numbers[3]) / 2.0 };
		}

		int q1hi;
		int q1lo;
		int q2hi;
		int q2lo;
		int q3hi;
		int q3lo;
		int odd;
		if (numbers.length % 2 == 0) {
			q2hi = numbers.length / 2;
			q2lo = q2hi - 1;
			odd = 0;
		} else {
			q2hi = (numbers.length - 1) / 2;
			q2lo = q2hi;
			odd = 1;
		}
		if (q2hi % 2 == 0) {
			q1hi = q2hi / 2;
			q1lo = q1hi - 1;
			q3hi = (3 * q1hi) + odd;
			q3lo = q3hi - 1;
		} else {
			q1hi = (q2hi - 1) / 2;
			q1lo = q1hi;
			q3hi = (q1hi + q2hi) + odd;
			q3lo = q3hi;
		}

		return new double[] { (numbers[q1lo] + numbers[q1hi]) / 2.0, (numbers[q2lo] + numbers[q2hi]) / 2.0,
				(numbers[q3lo] + numbers[q3hi]) / 2.0 };
	}

	/**
	 * Count the number of times each unique member of the list occurs and return
	 * the result as a list of lists
	 * 
	 * @param list a NetLogoMutableList
	 * @return a NetLogo list-of-lists containing the counts of each unique item in
	 *         the argument
	 */
	private LogoList counts(NetLogoMutableList list) {
		Map<Object, Double> cts = accumulations(list);

		List<LogoList> result = new LinkedList<LogoList>();
		for (Object key : cts.keySet()) {
			List<Object> keyValue = Arrays.asList(new Object[] { key, cts.get(key) });
			result.add(LogoList.fromJava(keyValue));
		}
		return LogoList.fromJava(result);
	}

	/**
	 * @param list a NetLogoMutableList
	 * @return a NetLogo list of the members of the argument that appear most
	 *         frequently
	 */
	private LogoList modes(NetLogoMutableList list) {
		Map<Object, Double> cts = accumulations(list);

		List<Object> result = new LinkedList<Object>();
		double max = Double.NaN;
		for (Object key : cts.keySet()) {
			double n = cts.get(key);
			if (!Double.isFinite(max)) {
				max = n;
				result.add(key);
			} else if (max == n) {
				result.add(key);
			} else if (n > max) {
				result.clear();
				max = n;
				result.add(key);
			}
		}
		return LogoList.fromJava(result);
	}

	/**
	 * Provide an implementation of NetLogo's list <code>reduce</code> command that
	 * can work on NetLogoMutableLists.
	 * 
	 * @param args    the arguments passed in to the command (a reporter and the
	 *                list)
	 * @param context the NetLogo context passed in from the API
	 * @return the result
	 * @throws ExtensionException
	 */
	private Object reduce(Argument[] args, Context context) throws ExtensionException {
		AnonymousReporter cmd = args[0].getReporter();
		NetLogoMutableList lst = NetLogoMutableList.asNetLogoMutableList(args[1]);
		Iterator<Object> ix = lst.iterator();
		if (ix.hasNext()) {
			Object result = ix.next();

			while (ix.hasNext()) {
				result = cmd.report(context, new Object[] { result, ix.next() });
			}

			return result;
		} else {
			throw new ExtensionException(MutableListExtension.EXTENSION_NAME + ":reduce called on empty list");
		}
	}

	/**
	 * Used as input to modes() and counts(), this function creates a Map of unique
	 * entries in the list to the number of times they appear
	 * 
	 * @param list
	 * @return a Map of elements in the list to the number of times they appear
	 */
	private Map<Object, Double> accumulations(NetLogoMutableList list) {
		Map<Object, Double> cts = new HashMap<Object, Double>();
		ListIterator<Object> ix = list.getListIterator();
		while (ix.hasNext()) {
			Object obj = ix.next();
			if (cts.containsKey(obj)) {
				cts.put(obj, cts.get(obj) + 1.0);
			} else {
				cts.put(obj, 1.0);
			}
		}
		return cts;
	}

	/**
	 * Implement the <code>histogram</code> command (sort-of) for MutableLists.
	 * Provide the minimum value, the maximum value, and the width of each bar, and
	 * receive a LogoList in return.
	 * 
	 * @param list
	 * @param min   the minimum (inclusive) value in the list to include in the
	 *              histogram
	 * @param max   the maximum (exclusive) value in the list
	 * @param width the width of each bar
	 * @return a NetLogo list of the results
	 * @throws ExtensionException
	 */
	private LogoList histogram(NetLogoMutableList list, double min, double max, double width)
			throws ExtensionException {
		if (min > max) {
			throw new ExtensionException("histogram called with minimum \"" + min + "\" > maximum \"" + max + "\"");
		} else if (width <= 0.0) {
			throw new ExtensionException("histogram called with non-positive width \"" + width + "\"");
		} else if (!Double.isFinite(min) || !Double.isFinite(max) || !Double.isFinite(width)) {
			throw new ExtensionException("histogram called with non-finite minimum, maximum or width");
		}
		double dn_bins = Math.ceil((max - min) / width);
		if (dn_bins > (double) Integer.MAX_VALUE) {
			throw new ExtensionException("histogram with minimum \"" + min + "\", maximum \"" + max + "\" and width \""
					+ width + "\" leads to too many bins \"" + dn_bins + "\"");
		}
		int n_bins = (int) dn_bins;
		Double[] result = new Double[n_bins];
		for (int i = 0; i < result.length; i++) {
			result[i] = 0.0;
		}
		Iterator<Object> ix = list.iterator();
		while (ix.hasNext()) {
			Object obj = ix.next();
			if (obj instanceof Double) {
				double n = (Double) obj;
				if (Double.isFinite(n) && n >= min && n < max) {
					int i = (int) Math.floor((n - min) / width);
					result[i] += 1.0;
				}
			}
		}
		return LogoList.fromJava(Arrays.asList(result));
	}

	/**
	 * Provide an implementation of NetLogo's <code>map</code>
	 * 
	 * @param args    the arguments passed in to the command from the NetLogo API
	 * @param context the context passed in to the command from the NetLogo API
	 * @return a NetLogoMutableList containing the result
	 * @throws ExtensionException
	 */
	private NetLogoMutableList map(Argument args[], Context context) throws ExtensionException {
		AnonymousReporter cmd = args[1].getReporter();
		NetLogoMutableList[] listArgs = new NetLogoMutableList[args.length - 2];
		for (int i = 2; i < args.length; i++) {
			listArgs[i - 2] = NetLogoMutableList.asNetLogoMutableList(args[i]);
		}
		@SuppressWarnings("unchecked")
		Iterator<Object>[] ixes = new Iterator[listArgs.length];
		for (int i = 0; i < listArgs.length; i++) {
			ixes[i] = listArgs[i].iterator();
		}
		List<Object> result = new LinkedList<Object>();
		while (hasNext(ixes)) {
			result.add(cmd.report(context, next(ixes)));
		}

		return new NetLogoMutableList(result);
	}

	/**
	 * Implementation of hasNext() for lots of iterators at once
	 * 
	 * @param ixes
	 * @return <code>true</code> if all the iterators have a next item
	 */
	private boolean hasNext(Iterator<Object>[] ixes) {
		for (int i = 0; i < ixes.length; i++) {
			if (!ixes[i].hasNext()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Implementation of next() for lots of iterators at once
	 * 
	 * @param ixes
	 * @return an array of the same size as <code>ixes</code> containing the result
	 *         of <code>next()</code> on each
	 */
	private Object[] next(Iterator<Object>[] ixes) {
		Object[] nexts = new Object[ixes.length];

		for (int i = 0; i < ixes.length; i++) {
			nexts[i] = ixes[i].next();
		}

		return nexts;
	}
}
