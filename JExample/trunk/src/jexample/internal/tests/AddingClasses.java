package jexample.internal.tests;

import static org.junit.Assert.*;
import jexample.Depends;
import jexample.JExampleRunner;
import jexample.internal.TestGraph;
import jexample.internal.TestMethod;

import org.junit.Test;
import org.junit.internal.runners.InitializationError;
import org.junit.runner.RunWith;

public class AddingClasses {

    @RunWith( JExampleRunner.class )
    public static class A {
        @Test
        public void t() { }
    }
    
    @Test
    public void simpleClass() throws Throwable {
        TestGraph g = new TestGraph();
        g.addTestCase(A.class);
        
        assertEquals(1, g.getClasses().size());
        assertEquals(1, g.getTestMethods().size());
        assertEquals(A.class,
                g.getClasses().iterator().next().getJavaClass());
        assertEquals(A.class.getMethod("t"), 
                g.getTestMethods().iterator().next().getJavaMethod());
    }

    @RunWith( JExampleRunner.class )
    public static class B extends A {
        @Override
        @Test
        public void t() { }
    }
    
    @Test
    public void simpleOverride() throws Throwable {
        TestGraph g = new TestGraph();
        g.addTestCase(B.class);
        
        assertEquals(1, g.getClasses().size());
        assertEquals(1, g.getTestMethods().size());
        assertEquals(B.class,
                g.getClasses().iterator().next().getJavaClass());
        assertEquals(B.class.getMethod("t"), 
                g.getTestMethods().iterator().next().getJavaMethod());
    }
    
    @RunWith( JExampleRunner.class )
    public static class C {
        @Test
        public Object provider() { return 42; }
        @Test 
        @Depends("provider")
        public void consumer(Object o) { }
    }

    @Test
    public void simpleDepedency() throws Throwable {
        TestGraph g = new TestGraph();
        g.addTestCase(C.class);
        
        assertEquals(1, g.getClasses().size());
        assertEquals(2, g.getTestMethods().size());
        assertEquals(C.class,
                g.getClasses().iterator().next().getJavaClass());
        
        TestMethod p = g.getTestMethod(C.class.getMethod("provider"));
        TestMethod c = g.getTestMethod(C.class.getMethod("consumer", Object.class));
        assertEquals(0, p.getDependencies().size());
        assertEquals(1, c.getDependencies().size());
        assertEquals(p, c.getDependencies().iterator().next());
    }
    
    
}
