package org.ncgazetteer;

import java.io.StringReader;
import org.apache.jena.atlas.lib.Lib;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.expr.NodeValue;
import org.apache.jena.sparql.function.FunctionBase2;
import org.apache.jena.vocabulary.RDF;
import jakarta.json.Json;
import jakarta.json.JsonException;
import jakarta.json.JsonNumber;
import jakarta.json.JsonPointer;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;

/**
 * Function that takes a string and a JSON pointer expression, parses the string as JSON,
 * and returns the result of running the JSON pointer expression on the JSON
 */
public class jsonPointer extends FunctionBase2 {

    @Override
    public NodeValue exec(NodeValue arg1, NodeValue arg2) {
        if (!arg1.isString()) {
            throw new ExprEvalException(Lib.className(this) + ": not a string: " + arg1);
        }
        if (!arg2.isString()) {
            throw new ExprEvalException(Lib.className(this) + ": not a string: " + arg2);
        }
        String jsonString = arg1.asString();
        String pointerString = arg2.asString();

        try (JsonReader reader = Json.createReader(new StringReader(jsonString))) {
            JsonStructure json = reader.read();
            JsonPointer pointer = Json.createPointer(pointerString);
            JsonValue value = pointer.getValue(json);
            switch (value.getValueType()) {
                case OBJECT:
                case ARRAY:
                case NULL:
                    return NodeValue.makeNode(value.toString(), RDF.dtRDFJSON);
                case TRUE:
                    return NodeValue.TRUE;
                case FALSE:
                    return NodeValue.FALSE;
                case NUMBER:
                    JsonNumber number = (JsonNumber) value;
                    return NodeValue.makeNode(
                       value.toString(),
                       number.isIntegral() ? XSDDatatype.XSDinteger : XSDDatatype.XSDdouble);
                case STRING:
                    JsonString string = (JsonString) value;
                    return NodeValue.makeString(string.getString());
                default:
                    throw new ExprEvalException(
                            Lib.className(this) + ": unknown JSON value type: " + value);
            }
        } catch (JsonException e) {
            throw new ExprEvalException(
                    Lib.className(this) + ": failed to apply '"
                    + pointerString + "' to '" + jsonString + "'");
        }
    }

}
