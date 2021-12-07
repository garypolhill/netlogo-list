/**
 * 
 */
package uk.ac.hutton.netlogo.list;

import java.util.NoSuchElementException;

import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.Reporter;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;

/**
 * Class implementing reporter functions for iterators on lists (which I call
 * indexes for reasons best known to myself, and which will be referred to as
 * 'cursors' in the commands as implemented).
 * 
 * @author gary
 */
public class IndexReporter implements Reporter {
	public enum Name {
		HASLEFT, HASRIGHT, IS_INDEX, LEFT, RIGHT
	};

	private final Name cmd;

	/**
	 * An enumeration is used to store all the commands implemented by this class.
	 * Instances will implement one of them. The constructor passes in the command
	 * to be implemented by this instance.
	 */
	public IndexReporter(Name cmd) {
		this.cmd = cmd;
	}

	/**
	 * Return the syntaxes of the reporters. All just take a list as an argument
	 */
	@Override
	public Syntax getSyntax() {
		switch (cmd) {
		case HASLEFT:
			return SyntaxJ.reporterSyntax(new int[] { Syntax.WildcardType() }, Syntax.BooleanType());
		case HASRIGHT:
			return SyntaxJ.reporterSyntax(new int[] { Syntax.WildcardType() }, Syntax.BooleanType());
		case IS_INDEX:
			return SyntaxJ.reporterSyntax(new int[] { Syntax.WildcardType() }, Syntax.BooleanType());
		case LEFT:
			return SyntaxJ.reporterSyntax(new int[] { Syntax.WildcardType() }, Syntax.WildcardType());
		case RIGHT:
			return SyntaxJ.reporterSyntax(new int[] { Syntax.WildcardType() }, Syntax.WildcardType());
		default:
			throw new RuntimeException("PANIC!");
		}
	}

	/**
	 * Implement the reporters
	 */
	@Override
	public Object report(Argument[] args, Context context) throws ExtensionException {
		if (cmd == Name.IS_INDEX) {
			return (args[0].get() instanceof NetLogoMutableListIndex);
		} else {
			NetLogoMutableListIndex ix = NetLogoMutableListIndex.asNetLogoMutableListIndex(args[0]);
			switch (cmd) {
			case HASLEFT:
				return new Boolean(ix.hasPrevious());
			case HASRIGHT:
				return new Boolean(ix.hasNext());
			case LEFT:
				try {
					return ix.previous();
				} catch (NoSuchElementException e) {
					throw new ExtensionException("Cursor has reached the beginning of the list");
				}
			case RIGHT:
				try {
					return ix.next();
				} catch (NoSuchElementException e) {
					throw new ExtensionException("Cursor has reached the end of the list");
				}
			default:
				throw new RuntimeException("PANIC!");
			}
		}
	}

}
