package org.ncgazetteer;

import org.apache.jena.sparql.expr.ExprEvalException;
import org.junit.Test;

public class JSONPointerTest extends FunctionTest {

    @Test
    public void testIntegerValue() {
        assertEval(integerNode(1),
                "<java:org.ncgazetteer.jsonPointer>('{\"a\":1}', '/a')");
    }

    @Test
    public void testDoubleValue() {
        assertEval(doubleNode(1.1),
                "<java:org.ncgazetteer.jsonPointer>('{\"a\":1.1}', '/a')");
    }

    @Test
    public void testStringValue() {
        assertEval(stringNode("b"),
                "<java:org.ncgazetteer.jsonPointer>('{\"a\":\"b\"}', '/a')");
    }

    @Test
    public void testTrueValue() {
        assertEval(booleanNode(true),
                "<java:org.ncgazetteer.jsonPointer>('{\"a\":true}', '/a')");
    }

    @Test
    public void testFalseValue() {
        assertEval(booleanNode(false),
                "<java:org.ncgazetteer.jsonPointer>('{\"a\":false}', '/a')");
    }

    @Test
    public void testNullValue() {
        assertEval(jsonNode("null"),
                "<java:org.ncgazetteer.jsonPointer>('{\"a\":null}', '/a')");
    }

    @Test
    public void testArrayValue() {
        assertEval(jsonNode("[1]"),
                "<java:org.ncgazetteer.jsonPointer>('{\"a\":[1]}', '/a')");
    }

    @Test
    public void testObjectValue() {
        assertEval(jsonNode("{\"b\":1}"),
                "<java:org.ncgazetteer.jsonPointer>('{\"a\":{\"b\":1}}', '/a')");
    }

    @Test
    public void testNonExistentValue() {
        try {
            eval("<java:org.ncgazetteer.jsonPointer>('{\"a\":1}', '/b')");
        } catch (ExprEvalException e) {
        }
    }

    @Test
    public void testInvalidJSON() {
        try {
            eval("<java:org.ncgazetteer.jsonPointer>('{', '/a')");
        } catch (ExprEvalException e) {
        }
    }

    @Test
    public void testInvalidJSONPointer() {
        try {
            eval("<java:org.ncgazetteer.jsonPointer>('{\"a\":1}', '\')");
        } catch (ExprEvalException e) {
        }
    }
}
