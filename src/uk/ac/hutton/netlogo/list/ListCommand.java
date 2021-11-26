/**
 * 
 */
package uk.ac.hutton.netlogo.list;

import java.util.Comparator;

import org.nlogo.api.AnonymousCommand;
import org.nlogo.api.AnonymousReporter;
import org.nlogo.api.Argument;
import org.nlogo.api.Command;
import org.nlogo.api.Context;
import org.nlogo.api.ExtensionException;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;

/**
 * @author gary
 *
 */
public class ListCommand implements Command {
	public enum Name {
		FOREACH, REVERSE, SORT
	};

	private Name cmd;

	/**
	 * 
	 */
	public ListCommand(Name cmd) {
		this.cmd = cmd;
	}

	@Override
	public Syntax getSyntax() {
		switch (cmd) {
		case FOREACH:
			return SyntaxJ.commandSyntax(new int[] { Syntax.WildcardType(), Syntax.CommandBlockType() });
		case REVERSE:
			return SyntaxJ.commandSyntax(new int[] { Syntax.WildcardType() });
		case SORT:
			return SyntaxJ.commandSyntax(new int[] { Syntax.WildcardType(), Syntax.NumberBlockType() });
		default:
			throw new RuntimeException("PANIC!");
		}
	}

	@Override
	public void perform(Argument[] args, Context context) throws ExtensionException {
		NetLogoMutableList list = (NetLogoMutableList) args[0].get();
		switch (cmd) {
		case FOREACH:
			foreach(list, args[1].getCommand(), context);
			break;
		case REVERSE:
			list.reverse();
			break;
		case SORT:
			list.sort(getComparator(args[1].getReporter(), context));
			break;
		default:
			throw new RuntimeException("PANIC!");
		}
	}

	private void foreach(NetLogoMutableList list, AnonymousCommand cmd, Context context) {
		for (Object item : list) {
			cmd.perform(context, new Object[] { item });
		}
	}

	private Comparator<Object> getComparator(AnonymousReporter cmd, Context context) {
		return new ObjectComparator(cmd, context);
	}

	protected class ObjectComparator implements Comparator<Object> {
		private final AnonymousReporter cmd;
		private final Context context;

		protected ObjectComparator(AnonymousReporter cmd, Context context) {
			this.cmd = cmd;
			this.context = context;
		}

		@Override
		public int compare(Object o1, Object o2) {
			Object result = cmd.report(context, new Object[] { o1, o2 });
			if (result instanceof Double) {
				double ans = (double) result;
				return (ans < 0.0 ? -1 : (ans > 0.0 ? 1 : 0));
			} else {
				throw new RuntimeException("Comparison function \"" + cmd + "\" does not return a number with \"" + o1
						+ "\" and \"" + o2 + "\" as arguments");
			}
		}

	}
}
