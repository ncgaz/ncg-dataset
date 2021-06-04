package org.ncgazetteer;

import static org.junit.Assert.fail;
import org.apache.jena.query.QueryBuildException;
import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.expr.VariableNotBoundException;
import org.junit.Test;

public class CamelCaseTest extends FunctionTest {

    @Test
    public void testOneWord() {
        assertEval(stringNode("Foo"), "<java:org.ncgazetteer.camelcase>('foo')");
    }

    @Test
    public void testTwoWords() {
        assertEval(stringNode("FooBar"), "<java:org.ncgazetteer.camelcase>('foo bar')");
    }

    @Test
    public void testThreeWords() {
        assertEval(stringNode("FooBarBiz"), "<java:org.ncgazetteer.camelcase>('foo bar biz')");
    }

    @Test
    public void testEmptyString() {
        assertEval(stringNode(""), "<java:org.ncgazetteer.camelcase>('')");
    }

    @Test
    public void testUnboundArg() {
        try {
            eval("<java:org.ncgazetteer.camelcase>(?unbound)");
            fail();
        } catch (VariableNotBoundException ex) {
        }
    }

    @Test
    public void testNonStringArg() {
        try {
            eval("<java:org.ncgazetteer.camelcase>(true)");
            fail();
        } catch (ExprEvalException ex) {
        }
    }

    @Test
    public void testNoArgs() {
        try {
            eval("<java:org.ncgazetteer.camelcase>()");
            fail();
        } catch (QueryBuildException ex) {
        }
    }

    @Test
    public void testTooManyArgs() {
        try {
            eval("<java:org.ncgazetteer.camelcase>('foo', 'bar')");
            fail();
        } catch (QueryBuildException ex) {}
    }
}
