/**
 * 
 */
package uk.ac.hutton.netlogo.list;

import org.nlogo.api.DefaultClassManager;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.PrimitiveManager;

/**
 * @author gary
 *
 */
public class MutableListExtension extends DefaultClassManager {
	public static final String EXTENSION_NAME = "ls";

	@Override
	public void load(PrimitiveManager primManager) throws ExtensionException {
		primManager.addPrimitive("cursor", new ListReporter(ListReporter.Name.CURSOR));
		primManager.addPrimitive("foreach", new ListCommand(ListCommand.Name.FOREACH));
		primManager.addPrimitive("make", new ListReporter(ListReporter.Name.MAKE));
		primManager.addPrimitive("reverse", new ListCommand(ListCommand.Name.REVERSE));
	}

}
