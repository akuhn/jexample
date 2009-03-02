package ch.unibe.jexample.internal.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Result;
import org.junit.runner.RunWith;

import ch.unibe.jexample.Given;
import ch.unibe.jexample.JExample;
import ch.unibe.jexample.internal.ExampleGraph;
import ch.unibe.jexample.internal.JExampleError;
import ch.unibe.jexample.internal.JExampleError.Kind;

public class ExampleGraphTest {

    private ExampleGraph graph;

    @Before
    public void setUp() throws Exception {
        graph = new ExampleGraph();
    }

    @Test
    public void testAddOneClass() throws JExampleError {
        graph.add(OneClass.class);
        // assertEquals( 1, graph.getClasses().size() );
        assertEquals(4, graph.getExamples().size());
    }

    @Test
    public void testAddMethodsOfOneClass() throws JExampleError {
        graph.add(OneClass.class);
        assertEquals(4, graph.getExamples().size());
    }

    @Test
    public void testAddDependenciesOfOneClass() throws JExampleError, SecurityException, NoSuchMethodException {
        graph.add(OneClass.class);
        assertEquals(0, graph.findExample(OneClass.class, "testMethod").providers.size());
        assertEquals(1, graph.findExample(OneClass.class, "anotherTestMethod").providers.size());
        assertEquals(1, graph.findExample(OneClass.class, "depOnOtherTest").providers.size());
        assertEquals(0, graph.findExample(DependsParserTest.B.class, "otherTest").providers.size());
    }

    @Test
    public void detectCycles() throws JExampleError {
        Class<?>[] classes = { Cyclic.class };
        ExampleGraph g = new ExampleGraph();
        Result r = g.runJExample(classes);
        assertEquals(false, r.wasSuccessful());
        assertEquals(3, r.getRunCount());
        assertEquals(2, r.getFailureCount());
        JExampleError err;
        err = (JExampleError) r.getFailures().get(0).getException();
        assertEquals(Kind.RECURSIVE_DEPENDENCIES, err.kind());
        err = (JExampleError) r.getFailures().get(1).getException();
        assertEquals(Kind.RECURSIVE_DEPENDENCIES, err.kind());
    }

    @RunWith(JExample.class)
    static private class OneClass {

        public OneClass() {

        }

        @Test
        public void testMethod() {

        }

        @Test
        @Given("testMethod")
        public void anotherTestMethod() {

        }

        @Test
        @Given("DependsParserTest$B.otherTest")
        public void depOnOtherTest() {

        }
    }

    @RunWith(JExample.class)
    private static class Cyclic {
        @Test
        public void provider() {
        }

        @Test
        @Given("provider;aaa")
        public void bbb() {
        }

        @Test
        @Given("bbb")
        public void aaa() {
        }
    }

    @SuppressWarnings("unused")
    private static class CyclicOverClasses {
        public CyclicOverClasses() {

        }

        @Test
        public void testMethod() {

        }

        @Test
        @Given("testMethod")
        public void anotherTestMethod() {

        }

        @Test
        @Given("B.otherTestCyclic")
        public void depOnOtherTest() {

        }
    }
}
