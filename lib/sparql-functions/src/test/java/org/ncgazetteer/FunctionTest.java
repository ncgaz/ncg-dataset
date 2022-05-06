package org.ncgazetteer;

import static org.junit.Assert.assertEquals;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.engine.binding.BindingFactory;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.function.FunctionEnv;
import org.apache.jena.sparql.function.FunctionEnvBase;
import org.apache.jena.sparql.util.ExprUtils;
import org.junit.Before;


public abstract class FunctionTest {
    protected FunctionEnv env;

    @Before
    public void setUp() {
        env = new FunctionEnvBase();
    }


    protected void assertEval(Node expected, String expression) {
        assertEquals(expected, eval(expression));
    }

    protected Node eval(String expression) {
        Expr expr = ExprUtils.parse(expression);
        return expr.eval(BindingFactory.root(), env).asNode();
    }

    protected Node stringNode(String s) {
        return NodeFactory.createLiteral(s);
    }

    protected Node integerNode(long l) {
        return NodeFactory.createLiteral(String.valueOf(l), XSDDatatype.XSDinteger);
    }

    protected Node doubleNode(double d) {
        return NodeFactory.createLiteral(String.valueOf(d), XSDDatatype.XSDdouble);
    }

    protected Node booleanNode(boolean b) {
        return NodeFactory.createLiteral(String.valueOf(b), XSDDatatype.XSDboolean);
    }

    protected Node jsonNode(String s) {
        // Older versions of Jena (such as that used by TARQL) do not include
        // the rdf:json datatype, so we cannot use it.
        // return NodeFactory.createLiteral(s, RDF.dtRDFJSON);
        return NodeFactory.createLiteral(s);
    }
}
