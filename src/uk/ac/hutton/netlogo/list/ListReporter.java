/**
 * 
 */
package uk.ac.hutton.netlogo.list;

import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.Reporter;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;

/**
 * @author gary
 *
 */
public class ListReporter implements Reporter {
	public enum Name { CURSOR, MAKE };
	private Name cmd;
	/**
	 * 
	 */
	public ListReporter(Name cmd) {
		this.cmd = cmd;
	}

	@Override
	public Syntax getSyntax() {
		switch(cmd) {
		case CURSOR:
			return SyntaxJ.reporterSyntax(new int[] { Syntax.WildcardType() }, Syntax.WildcardType());
		case MAKE:
			return SyntaxJ.reporterSyntax(Syntax.WildcardType());
		default:
			throw new RuntimeException("PANIC!");
		}
	}

	@Override
	public Object report(Argument[] args, Context context) throws ExtensionException {
		if (cmd == Name.MAKE) {
			return new NetLogoMutableList();
		}
		else {
			NetLogoMutableList list = (NetLogoMutableList)args[0].get();
			switch(cmd) {
			case CURSOR:
				return list.getIterator();
			default:
				throw new RuntimeException("PANIC!");
			}
		}
	}

}
