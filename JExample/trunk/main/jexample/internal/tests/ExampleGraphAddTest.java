package jexample.internal.tests;

import static org.junit.Assert.assertEquals;
import jexample.Depends;
import jexample.JExample;
import jexample.internal.Example;
import jexample.internal.ExampleGraph;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

public class ExampleGraphAddTest {

    private ExampleGraph g;

    @Before
    public void fixture() {
        g = new ExampleGraph();
    }
    
    @RunWith( JExample.class )
    static class A {
        @Test
        public void t() { }
    }
    
    @Test
    public void simpleClass() throws Throwable {
        g.add(A.class);
        
        //assertEquals(1, g.getClasses().size());
        assertEquals(1, g.getExamples().size());
        //assertEquals(A.class,
        //        g.getClasses().iterator().next().getJavaClass());
        assertEquals(A.class.getMethod("t"), 
                g.getExamples().iterator().next().jmethod);
    }

    @RunWith( JExample.class )
    static class B extends A {
        @Override
        @Test
        public void t() { }
    }
    
    @Test
    public void simpleOverride() throws Throwable {
        g.add(B.class);
        
        //assertEquals(1, g.getClasses().size());
        assertEquals(1, g.getExamples().size());
        //assertEquals(B.class,
        //        g.getClasses().iterator().next().getJavaClass());
        assertEquals(B.class.getMethod("t"), 
                g.getExamples().iterator().next().jmethod);
    }
    
    @RunWith( JExample.class )
    static class C {
        @Test
        public Object provider() { return 42; }
        @Test 
        @Depends("provider")
        public void consumer(Object o) { }
    }

    @Test
    public void simpleDepedency() throws Throwable {
        g.add(C.class);
        
        //assertEquals(1, g.getClasses().size());
        assertEquals(2, g.getExamples().size());
        //assertEquals(C.class,
        //        g.getClasses().iterator().next().getJavaClass());
        
        Example p = g.getExample(C.class.getMethod("provider"));
        Example c = g.getExample(C.class.getMethod("consumer", Object.class));
        assertEquals(0, p.providers.size());
        assertEquals(1, c.providers.size());
        assertEquals(p, c.providers.iterator().next());
    }
    
}   
    

