package jexample.internal.tests;

import static org.junit.Assert.*;
import jexample.Depends;
import jexample.JExampleRunner;
import jexample.internal.TestGraph;
import jexample.internal.TestMethod;

import org.junit.Test;
import org.junit.internal.runners.InitializationError;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;

public class TestDependencies {

    public static class A {
        
    }
    
    public static class B extends A {
        
    }
    
    @RunWith( JExampleRunner.class )
    public static class C {
        @Test
        public B empty() {
            return new B();
        }
        @Test
        @Depends("empty")
        public A test(A a) {
            return a;
        }
    }
    
    @Test
    public void testPolymorphicDepedency() throws Exception {
        TestGraph $ = new TestGraph();
        $.add( C.class );
        
        assertEquals( 1, $.getClasses().size() );
        assertEquals( 2, $.getMethods().size() );
        
        TestMethod t = $.getTestMethod( C.class, "test" );
        TestMethod e = $.getTestMethod( C.class, "empty" );
        
        assertNotNull(t);
        assertNotNull(e);
        assertEquals( 1, t.getDependencies().size() );
        assertEquals( e, t.getDependencies().iterator().next() );
    }
    
    @Test
    public void runPolymorphicDepedency() throws Exception {
        TestGraph $ = new TestGraph();
        Result result = new JUnitCore().run($.newJExampleRunner(C.class));
        assertTrue( result.wasSuccessful() );
        assertEquals( 2, result.getRunCount() );
        
        TestMethod t = $.getTestMethod( C.class, "test" );
        TestMethod e = $.getTestMethod( C.class, "empty" );
 
        assertSame( e.getReturnValue(), t.getReturnValue() );
    }

    @RunWith( JExampleRunner.class )
    public static class D {
        @Test
        public B empty() {
            return new B();
        }
        @Test
        @Depends("empty")
        public B b(B b) {
            return b;
        }
        @Test
        @Depends("b")
        public A a(A a) {
            return a;
        }
    }

    @Test
    public void testPolymorphicDepedency2() throws Exception {
        TestGraph $ = new TestGraph();
        $.add( D.class );
        
        assertEquals( 1, $.getClasses().size() );
        assertEquals( 3, $.getMethods().size() );
        
        TestMethod a = $.getTestMethod( D.class, "a" );
        TestMethod b = $.getTestMethod( D.class, "b" );
        TestMethod e = $.getTestMethod( D.class, "empty" );
        
        assertNotNull(a);
        assertNotNull(b);
        assertNotNull(e);
        assertEquals( 1, b.getDependencies().size() );
        assertEquals( e, b.getDependencies().iterator().next() );
        assertEquals( 1, a.getDependencies().size() );
        assertEquals( b, a.getDependencies().iterator().next() );
    }

    @RunWith( JExampleRunner.class )
    public static class E_fail {
        @Test
        public A empty() {
            return new A();
        }
        @Test
        @Depends("empty")
        public B b(B b) {
            return b;
        }
    }
    
    @Test
    public void testFailPolymorphicDepedency() throws Exception {
        try {
            TestGraph $ = new TestGraph();
            $.add( E_fail.class );
        }
        catch (InitializationError err) {
            assertEquals( 1, err.getCauses().size() );
            return;
        }
        fail("InitializationError expected!");
    }
    
}
