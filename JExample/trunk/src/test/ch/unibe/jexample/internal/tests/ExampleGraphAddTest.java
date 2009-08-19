package ch.unibe.jexample.internal.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.unibe.jexample.Given;
import ch.unibe.jexample.JExample;
import ch.unibe.jexample.internal.Example;
import ch.unibe.jexample.internal.ExampleGraph;

public class ExampleGraphAddTest {

    private ExampleGraph g;

    @Before
    public void fixture() {
        g = new ExampleGraph();
    }

    @RunWith(JExample.class)
    static class A {
        @Test
        public void t() {
        }
    }

    @Test
    public void simpleClass() throws Throwable {
        g.add(A.class);

        assertEquals(1, g.getExamples().size());
        assertEquals(A.class, g.getExamples().iterator().next().method.getActualClass());
        assertEquals("t", g.getExamples().iterator().next().method.getName());
    }

    @RunWith(JExample.class)
    static class B extends A {
        @Override
        @Test
        public void t() {
        }
    }

    @Test
    public void simpleOverride() throws Throwable {
        g.add(B.class);

        assertEquals(1, g.getExamples().size());
        assertEquals(B.class, g.getExamples().iterator().next().method.getActualClass());
        assertEquals("t", g.getExamples().iterator().next().method.getName());
    }

    @RunWith(JExample.class)
    static class C {
        @Test
        public Object provider() {
            return 42;
        }

        @Test
        @Given("provider")
        public void consumer(Object o) {
        }
    }

    @Test
    public void simpleDepedency() throws Throwable {
        g.add(C.class);

        // assertEquals(1, g.getClasses().size());
        assertEquals(2, g.getExamples().size());
        // assertEquals(C.class,
        // g.getClasses().iterator().next().getJavaClass());

        Example p = g.findExample(C.class, "provider");
        Example c = g.findExample(C.class, "consumer");
        assertEquals(0, p.node.dependencies().size());
        assertEquals(1, c.node.dependencies().size());
        assertEquals(p, c.node.dependencies().iterator().next().getProducer());
    }

}
