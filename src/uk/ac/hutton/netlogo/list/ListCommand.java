/**
 * 
 */
package uk.ac.hutton.netlogo.list;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.nlogo.api.AgentSet;
import org.nlogo.api.AnonymousCommand;
import org.nlogo.api.AnonymousReporter;
import org.nlogo.api.Argument;
import org.nlogo.api.Command;
import org.nlogo.api.Context;
import org.nlogo.api.ExtensionException;
import org.nlogo.core.LogoList;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;

/**
 * Class implementing the commands that operate on a list. Each command is an
 * object, and an <code>enum</code> is used to store the particular command this
 * instance is implementing.
 * 
 * @author gary
 */
public class ListCommand implements Command {
	public enum Name {
		CAT, CLEAR, FILTER, FOREACH, FOREACH_DEPTH_FIRST, KEEP, PUSH, PUSH_ALL, REMOVE, REMOVE_DUPLICATES, REMOVE_ONCE,
		REVERSE, SORT, UNSHIFT, UNSHIFT_ALL, FIRST, SECOND, THIRD, FOURTH, FIFTH, SIXTH, SEVENTH, EIGHTH, NINTH, TENTH
	};

	private final Name cmd;

	/**
	 * Constructor initializing the command this instance is implementing
	 */
	public ListCommand(Name cmd) {
		this.cmd = cmd;
	}

	/**
	 * Return the syntax of this command. All the commands take a list as the first
	 * argument, except for FILTER, which, to mirror the NetLogo syntax, puts the
	 * Boolean reporter block first. PUSH and UNSHIFT are variadic implementations,
	 * so unlike lput and fput, the list comes first. With that being the case,
	 * although PUSH_ALL and UNSHIFT_ALL are not variadic, they adopt the same
	 * convention of putting the list first.
	 */
	@Override
	public Syntax getSyntax() {
		switch (cmd) {
		case CAT:
			return SyntaxJ.commandSyntax(
					new int[] { Syntax.WildcardType(), Syntax.WildcardType() | Syntax.RepeatableType() });
		case CLEAR:
			return SyntaxJ.commandSyntax(new int[] { Syntax.WildcardType() });
		case FILTER:
			return SyntaxJ.commandSyntax(new int[] { Syntax.BooleanBlockType(), Syntax.WildcardType() });
		case FOREACH:
			return SyntaxJ.commandSyntax(new int[] { Syntax.WildcardType(), Syntax.CommandBlockType() });
		case FOREACH_DEPTH_FIRST:
			return SyntaxJ.commandSyntax(new int[] { Syntax.WildcardType(), Syntax.CommandBlockType() });
		case KEEP:
			return SyntaxJ.commandSyntax(
					new int[] { Syntax.WildcardType(), Syntax.WildcardType() | Syntax.RepeatableType() });
		case PUSH:
			return SyntaxJ.commandSyntax(
					new int[] { Syntax.WildcardType(), Syntax.WildcardType() | Syntax.RepeatableType() });
		case PUSH_ALL:
			return SyntaxJ.commandSyntax(new int[] { Syntax.WildcardType(), Syntax.WildcardType() });
		case REMOVE:
			return SyntaxJ.commandSyntax(
					new int[] { Syntax.WildcardType(), Syntax.WildcardType() | Syntax.RepeatableType() });
		case REMOVE_DUPLICATES:
			return SyntaxJ.commandSyntax(new int[] { Syntax.WildcardType() });
		case REMOVE_ONCE:
			return SyntaxJ.commandSyntax(new int[] { Syntax.WildcardType(), Syntax.WildcardType() });
		case REVERSE:
			return SyntaxJ.commandSyntax(new int[] { Syntax.WildcardType() });
		case SORT:
			return SyntaxJ.commandSyntax(new int[] { Syntax.WildcardType(), Syntax.NumberBlockType() });
		case UNSHIFT:
			return SyntaxJ.commandSyntax(
					new int[] { Syntax.WildcardType(), Syntax.WildcardType() | Syntax.RepeatableType() });
		case UNSHIFT_ALL:
			return SyntaxJ.commandSyntax(new int[] { Syntax.WildcardType(), Syntax.WildcardType() });
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
			return SyntaxJ.commandSyntax(new int[] { Syntax.WildcardType(), Syntax.WildcardType() });
		default:
			throw new RuntimeException("PANIC!");
		}
	}

	/**
	 * Implement the command
	 */
	@Override
	public void perform(Argument[] args, Context context) throws ExtensionException {
		if (cmd == Name.FILTER) {
			filter(NetLogoMutableList.asNetLogoMutableList(args[1]), args[0].getReporter(), context);
		} else {
			NetLogoMutableList list = NetLogoMutableList.asNetLogoMutableList(args[0]);
			Object obj;
			switch (cmd) {
			case CAT:
				NetLogoMutableList prev = NetLogoMutableList.asNetLogoMutableList(args[args.length - 1]);
				for (int i = args.length - 2; i >= 1; i++) {
					NetLogoMutableList other = NetLogoMutableList.asNetLogoMutableList(args[i]);
					other.cat(prev);
					prev = other;
				}
				list.cat(prev);
				break;
			case CLEAR:
				list.clear();
			case FOREACH:
				foreach(list, args[1].getCommand(), context);
				break;
			case FOREACH_DEPTH_FIRST:
				foreachDepthFirst(list, args[1].getCommand(), context);
				break;
			case KEEP:
				list.retainAll(getArguments(args));
			case PUSH:
				if (args.length == 2) {
					list.push(args[1].get());
				} else {
					list.pushAllClear(getArguments(args));
				}
				break;
			case PUSH_ALL:
				obj = args[1].get();
				if (obj instanceof LogoList) {
					list.pushAll((LogoList) obj);
				} else if (obj instanceof AgentSet) {
					list.pushAll((AgentSet) obj);
				} else if (obj instanceof NetLogoMutableList) {
					list.pushAll((NetLogoMutableList) obj);
				} else {
					throw new ExtensionException("Cannot push-all a " + obj.getClass().getSimpleName() + "(value \""
							+ obj.toString() + "\" -- I need a list, an agentset or a mutable list");
				}
				break;
			case REMOVE:
				list.removeAll(getArguments(args));
				break;
			case REMOVE_DUPLICATES:
				list.removeDuplicates();
				break;
			case REMOVE_ONCE:
				list.remove(args[1].get());
				break;
			case REVERSE:
				list.reverse();
				break;
			case SORT:
				try {
					list.sort(new ObjectComparator(args[1].getReporter(), context));
				} catch (IllegalArgumentException e) {
					throw new ExtensionException(e.getMessage());
				}
				break;
			case UNSHIFT:
				for (int i = args.length - 1; i >= 1; i--) {
					list.unshift(args[i].get());
				}
			case UNSHIFT_ALL:
				obj = args[1].get();
				if (obj instanceof LogoList) {
					list.unshiftAll((LogoList) obj);
				} else if (obj instanceof AgentSet) {
					list.unshiftAll((AgentSet) obj);
				} else if (obj instanceof NetLogoMutableList) {
					list.unshiftAll((NetLogoMutableList) obj);
				} else {
					throw new ExtensionException("Cannot unshift-all a " + obj.getClass().getSimpleName() + "(value \""
							+ obj.toString() + "\" -- I need a list, an agentset or a mutable list");
				}
				break;
			case FIRST:
				list.first(args[1].get());
				break;
			case SECOND:
				list.second(args[1].get());
				break;
			case THIRD:
				list.third(args[1].get());
				break;
			case FOURTH:
				list.fourth(args[1].get());
				break;
			case FIFTH:
				list.fifth(args[1].get());
				break;
			case SIXTH:
				list.sixth(args[1].get());
				break;
			case SEVENTH:
				list.seventh(args[1].get());
				break;
			case EIGHTH:
				list.eighth(args[1].get());
				break;
			case NINTH:
				list.ninth(args[1].get());
				break;
			case TENTH:
				list.tenth(args[1].get());
				break;
			default:
				throw new RuntimeException("PANIC!");
			}
		}
	}

	/**
	 * Get the arguments to the command as a List, ignoring the first argument,
	 * which will be the NetLogoMutableList on which the command is operating, for
	 * commands using this method.
	 * 
	 * @param args the NetLogo API arguments
	 * @return the arguments to this command (except the first), as a list
	 * @throws ExtensionException
	 */
	protected static List<Object> getArguments(Argument[] args) throws ExtensionException {
		List<Object> argsList = new LinkedList<Object>();
		for (int i = 1; i <= args.length - i; i++) {
			argsList.add(args[i].get());
		}
		return argsList;
	}

	/**
	 * Provide an implementation of the foreach command on NetLogoMutableLists
	 * 
	 * @param list
	 * @param cmd
	 * @param context
	 */
	private void foreach(NetLogoMutableList list, AnonymousCommand cmd, Context context) {
		for (Object item : list) {
			cmd.perform(context, new Object[] { item });
		}
	}

	/**
	 * Provide an implementation of the filter command for NetLogoMutableLists
	 * 
	 * @param list
	 * @param cmd
	 * @param context
	 */
	private void filter(NetLogoMutableList list, AnonymousReporter cmd, Context context) {
		ListIterator<Object> ix = list.getListIterator();

		while (ix.hasNext()) {
			if (!cmd.report(context, new Object[] { ix.next() }).equals(Boolean.TRUE)) {
				ix.remove();
			}
		}
	}

	/**
	 * Provide a command enabling depth first search of nested NetLogoMutableLists
	 * as a single loop
	 * 
	 * @param list
	 * @param cmd
	 * @param context
	 */
	private void foreachDepthFirst(NetLogoMutableList list, AnonymousCommand cmd, Context context) {
		Iterator<Object> dfix = list.getDepthFirstIterator();
		while (dfix.hasNext()) {
			cmd.perform(context, new Object[] { dfix.next() });
		}
	}

	/**
	 * Wrapper class implementing the Comparator interface for a NetLogo reporter
	 * expected to return a number
	 * 
	 * @author gary
	 */
	protected class ObjectComparator implements Comparator<Object> {
		private final AnonymousReporter cmd;
		private final Context context;

		protected ObjectComparator(AnonymousReporter cmd, Context context) {
			this.cmd = cmd;
			this.context = context;
		}

		/**
		 * Implement the compare() method stipulated by the Comparator interface, as a
		 * wrapper around the reporter and command passed from the NetLogo API. For
		 * obvious reasons, the interface doesn't allow throwing an ExtensionException,
		 * so if there's a problem with non-numeric results from the reporter, an
		 * IllegalArgumentException is thrown instead, which can be caught by the caller
		 * and rethrown as an ExtensionException.
		 */
		@Override
		public int compare(Object o1, Object o2) {
			Object result = cmd.report(context, new Object[] { o1, o2 });
			if (result instanceof Double) {
				double ans = (double) result;
				return (ans < 0.0 ? -1 : (ans > 0.0 ? 1 : 0));
			} else {
				throw new IllegalArgumentException("Comparison function \"" + cmd
						+ "\" does not return a number with \"" + o1 + "\" and \"" + o2 + "\" as arguments");
			}
		}

	}
}
