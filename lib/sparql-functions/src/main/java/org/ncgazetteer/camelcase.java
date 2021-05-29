package org.ncgazetteer;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.jena.atlas.lib.Lib;
import org.apache.jena.query.QueryBuildException;
import org.apache.jena.sparql.ARQInternalErrorException;
import org.apache.jena.sparql.expr.ExprList;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase;

/**
 * Function that concatenates strings using a separator. This is not
 * fn:string-join because (1) that takes a sequence as argument (2) the
 * arguments are in a different order
 */

public class camelcase extends FunctionBase {
    @Override
    public final NodeValue exec(List<NodeValue> args) {
        if (args == null)
            // The contract on the function interface is that this should not happen.
            throw new ARQInternalErrorException(Lib.className(this) + ": Null args list");

        Iterator<NodeValue> iter = args.iterator();
        String s = iter.next().asString();

        return NodeValue.makeString(
                Stream.of(s.split("\\s+"))
                        .map(camelcase::capitalize)
                        .collect(Collectors.joining()));
    }

    private static String capitalize(String s) {
        if (s.length() == 0) {
            return s;
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    @Override
    public void checkBuild(String uri, ExprList args) {
        if (args.size() != 1)
            throw new QueryBuildException(
                "Function '" + Lib.className(this) + "' requires one argument");
    }
}
