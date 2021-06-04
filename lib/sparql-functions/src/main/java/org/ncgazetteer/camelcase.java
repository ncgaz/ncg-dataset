package org.ncgazetteer;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.jena.atlas.lib.Lib;
import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase1;

/**
 * Function that takes a string, splits it on whitespace, and returns a camel-cased string.
 * e.g. "foo bar biz" -> "FooBarBiz"
 */
public class camelcase extends FunctionBase1 {

    @Override
    public final NodeValue exec(NodeValue arg) {
        if (!arg.isString()) {
            throw new ExprEvalException(Lib.className(this) + ": not a string: " + arg);
        }
        String s = arg.asString();

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
}
