/**
 * 
 */
package uk.ac.hutton.netlogo.list;

import org.nlogo.api.Argument;
import org.nlogo.api.Command;
import org.nlogo.api.Context;
import org.nlogo.api.ExtensionException;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;

/**
 * Class implementing the commands provided for iterators on NetLogoMutableLists
 * 
 * @author gary
 */
public class IndexCommand implements Command {
	public enum Name {
		INSERT, DELETE, OVERWRITE
	};

	private final Name cmd;

	/**
	 * Constructor passing in which of the enumerations this instance is providing
	 * an implementation for
	 */
	public IndexCommand(Name cmd) {
		this.cmd = cmd;
	}

	/**
	 * Return the syntaxes for the commands.
	 */
	@Override
	public Syntax getSyntax() {
		switch (cmd) {
		case DELETE:
			return SyntaxJ.commandSyntax(new int[] { Syntax.WildcardType() });
		case INSERT:
			return SyntaxJ.commandSyntax(new int[] { Syntax.WildcardType(), Syntax.WildcardType() });
		case OVERWRITE:
			return SyntaxJ.commandSyntax(new int[] { Syntax.WildcardType(), Syntax.WildcardType() });
		default:
			throw new RuntimeException("PANIC!");
		}
	}

	/**
	 * Implement the commands.
	 */
	@Override
	public void perform(Argument[] args, Context context) throws ExtensionException {
		NetLogoMutableListIndex ix = NetLogoMutableListIndex.asNetLogoMutableListIndex(args[0]);

		switch (cmd) {
		case DELETE:
			try {
				ix.remove();
			} catch (IllegalStateException e) {
				throw new ExtensionException(
						"You can only use delete once between calls to left and right, and not on an empty list");
			}
			break;
		case INSERT:
			ix.add(args[1].get());
			break;
		case OVERWRITE:
			try {
				ix.set(args[1].get());
			} catch (IllegalStateException e) {
				throw new ExtensionException(
						"You can only use overwrite if you've already called left and right, and not if you've "
								+ "already used insert or delete");
			}
			break;
		default:
			throw new RuntimeException("PANIC!");
		}
	}

}
