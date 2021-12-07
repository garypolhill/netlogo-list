/**
 * 
 */
package uk.ac.hutton.netlogo.list;

import org.nlogo.api.DefaultClassManager;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.PrimitiveManager;

/**
 * Implementation of the extension adding all the commands provided by this
 * extension
 * 
 * @author gary
 */
public class MutableListExtension extends DefaultClassManager {
	public static final String EXTENSION_NAME = "ls";

	@Override
	public void load(PrimitiveManager primManager) throws ExtensionException {
		primManager.addPrimitive("as-list", new ListReporter(ListReporter.Name.AS_LIST));
		primManager.addPrimitive("as-list-deeply", new ListReporter(ListReporter.Name.AS_LIST_DEEPLY));
		primManager.addPrimitive("cat", new ListCommand(ListCommand.Name.CAT));
		primManager.addPrimitive("clear", new ListCommand(ListCommand.Name.CLEAR));
		primManager.addPrimitive("copy", new ListReporter(ListReporter.Name.COPY));
		primManager.addPrimitive("counts", new ListReporter(ListReporter.Name.COUNTS));
		primManager.addPrimitive("cursor", new ListReporter(ListReporter.Name.CURSOR));
		primManager.addPrimitive("deep-member?", new ListReporter(ListReporter.Name.DEEP_MEMBER));
		primManager.addPrimitive("delete", new IndexCommand(IndexCommand.Name.DELETE));
		primManager.addPrimitive("empty?", new ListReporter(ListReporter.Name.IS_EMPTY));
		primManager.addPrimitive("filter", new ListCommand(ListCommand.Name.FILTER));
		primManager.addPrimitive("foreach", new ListCommand(ListCommand.Name.FOREACH));
		primManager.addPrimitive("foreach-depth-first", new ListCommand(ListCommand.Name.FOREACH_DEPTH_FIRST));
		primManager.addPrimitive("from-agentset", new ListReporter(ListReporter.Name.FROM_AGENTSET));
		primManager.addPrimitive("from-list", new ListReporter(ListReporter.Name.FROM_LIST));
		primManager.addPrimitive("fpop", new ListReporter(ListReporter.Name.SHIFT));
		primManager.addPrimitive("fpush", new ListCommand(ListCommand.Name.UNSHIFT));
		primManager.addPrimitive("fpush-all", new ListCommand(ListCommand.Name.UNSHIFT_ALL));
		primManager.addPrimitive("has-left?", new IndexReporter(IndexReporter.Name.HASLEFT));
		primManager.addPrimitive("has-right?", new IndexReporter(IndexReporter.Name.HASRIGHT));
		primManager.addPrimitive("histogram", new ListReporter(ListReporter.Name.HISTOGRAM));
		primManager.addPrimitive("insert", new IndexCommand(IndexCommand.Name.INSERT));
		primManager.addPrimitive("intersects?", new ListReporter(ListReporter.Name.INTERSECTS));
		primManager.addPrimitive("is-cursor?", new IndexReporter(IndexReporter.Name.IS_INDEX));
		primManager.addPrimitive("is-list?", new ListReporter(ListReporter.Name.IS_LIST));
		primManager.addPrimitive("keep", new ListCommand(ListCommand.Name.KEEP));
		primManager.addPrimitive("left", new IndexReporter(IndexReporter.Name.LEFT));
		primManager.addPrimitive("length", new ListReporter(ListReporter.Name.LENGTH));
		primManager.addPrimitive("lpop", new ListReporter(ListReporter.Name.POP));
		primManager.addPrimitive("lpush", new ListCommand(ListCommand.Name.PUSH));
		primManager.addPrimitive("lpush-all", new ListCommand(ListCommand.Name.PUSH_ALL));
		primManager.addPrimitive("make", new ListReporter(ListReporter.Name.MAKE));
		primManager.addPrimitive("map", new ListReporter(ListReporter.Name.MAP));
		primManager.addPrimitive("max", new ListReporter(ListReporter.Name.MAX));
		primManager.addPrimitive("mean", new ListReporter(ListReporter.Name.MEAN));
		primManager.addPrimitive("median", new ListReporter(ListReporter.Name.MEDIAN));
		primManager.addPrimitive("member?", new ListReporter(ListReporter.Name.MEMBER));
		primManager.addPrimitive("member-any?", new ListReporter(ListReporter.Name.MEMBER_ANY));
		primManager.addPrimitive("min", new ListReporter(ListReporter.Name.MIN));
		primManager.addPrimitive("modes", new ListReporter(ListReporter.Name.MODES));
		primManager.addPrimitive("overwrite", new IndexCommand(IndexCommand.Name.OVERWRITE));
		primManager.addPrimitive("quartiles", new ListReporter(ListReporter.Name.QUARTILES));
		primManager.addPrimitive("range", new ListReporter(ListReporter.Name.RANGE));
		primManager.addPrimitive("reduce", new ListReporter(ListReporter.Name.REDUCE));
		primManager.addPrimitive("remove", new ListCommand(ListCommand.Name.REMOVE));
		primManager.addPrimitive("remove-duplicates", new ListCommand(ListCommand.Name.REMOVE_DUPLICATES));
		primManager.addPrimitive("remove-once", new ListCommand(ListCommand.Name.REMOVE_ONCE));
		primManager.addPrimitive("reverse", new ListCommand(ListCommand.Name.REVERSE));
		primManager.addPrimitive("right", new IndexReporter(IndexReporter.Name.RIGHT));
		primManager.addPrimitive("shuffle", new ListCommand(ListCommand.Name.SHUFFLE));
		primManager.addPrimitive("sort", new ListCommand(ListCommand.Name.SORT));
		primManager.addPrimitive("sum", new ListReporter(ListReporter.Name.SUM));

		primManager.addPrimitive("first", new ListReporter(ListReporter.Name.FIRST));
		primManager.addPrimitive("second", new ListReporter(ListReporter.Name.SECOND));
		primManager.addPrimitive("third", new ListReporter(ListReporter.Name.THIRD));
		primManager.addPrimitive("fourth", new ListReporter(ListReporter.Name.FOURTH));
		primManager.addPrimitive("fifth", new ListReporter(ListReporter.Name.FIFTH));
		primManager.addPrimitive("sixth", new ListReporter(ListReporter.Name.SIXTH));
		primManager.addPrimitive("seventh", new ListReporter(ListReporter.Name.SEVENTH));
		primManager.addPrimitive("eighth", new ListReporter(ListReporter.Name.EIGHTH));
		primManager.addPrimitive("ninth", new ListReporter(ListReporter.Name.NINTH));
		primManager.addPrimitive("tenth", new ListReporter(ListReporter.Name.TENTH));

		primManager.addPrimitive("set-first", new ListCommand(ListCommand.Name.FIRST));
		primManager.addPrimitive("set-second", new ListCommand(ListCommand.Name.SECOND));
		primManager.addPrimitive("set-third", new ListCommand(ListCommand.Name.THIRD));
		primManager.addPrimitive("set-fourth", new ListCommand(ListCommand.Name.FOURTH));
		primManager.addPrimitive("set-fifth", new ListCommand(ListCommand.Name.FIFTH));
		primManager.addPrimitive("set-sixth", new ListCommand(ListCommand.Name.SIXTH));
		primManager.addPrimitive("set-seventh", new ListCommand(ListCommand.Name.SEVENTH));
		primManager.addPrimitive("set-eighth", new ListCommand(ListCommand.Name.EIGHTH));
		primManager.addPrimitive("set-ninth", new ListCommand(ListCommand.Name.NINTH));
		primManager.addPrimitive("Set-tenth", new ListCommand(ListCommand.Name.TENTH));
	}

}
