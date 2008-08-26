package jexample.internal.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import jexample.Depends;
import jexample.JExample;
import jexample.internal.Example;
import jexample.internal.ExampleGraph;
import jexample.internal.JExampleError;
import jexample.internal.JExampleError.Kind;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;

public class DependenciesTest {

    static class A {
        
    }
    
    static class B extends A {
        
    }
    
    @RunWith( JExample.class )
    static class C {
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
        ExampleGraph $ = new ExampleGraph();
        $.add( C.class );
        
        //assertEquals( 1, $.getClasses().size() );
        assertEquals( 2, $.getMethods().size() );
        
        Example t = $.findExample( C.class, "test" );
        Example e = $.findExample( C.class, "empty" );
        
        assertNotNull(t);
        assertNotNull(e);
        assertEquals( 1, t.providers.size() );
        assertEquals( e, t.providers.iterator().next() );
    }
    
    @Test
    public void runPolymorphicDepedency() throws Exception {
        ExampleGraph $ = new ExampleGraph();
        Result result = new JUnitCore().run($.newJExampleRunner(C.class));
        assertTrue( result.wasSuccessful() );
        assertEquals( 2, result.getRunCount() );
        
        Example t = $.findExample( C.class, "test" );
        Example e = $.findExample( C.class, "empty" );
 
        assertNotSame( e.returnValue, t.returnValue );
    }

    @RunWith( JExample.class )
    static class D {
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
        ExampleGraph $ = new ExampleGraph();
        $.add( D.class );
        
        //assertEquals( 1, $.getClasses().size() );
        assertEquals( 3, $.getMethods().size() );
        
        Example a = $.findExample( D.class, "a" );
        Example b = $.findExample( D.class, "b" );
        Example e = $.findExample( D.class, "empty" );
        
        assertNotNull(a);
        assertNotNull(b);
        assertNotNull(e);
        assertEquals( 1, b.providers.size() );
        assertEquals( e, b.providers.iterator().next() );
        assertEquals( 1, a.providers.size() );
        assertEquals( b, a.providers.iterator().next() );
    }

    @RunWith( JExample.class )
    static class E_fail {
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
    
    @RunWith( JExample.class )
    static class F extends D {
        @Test
        public B another() {
            return null;
        }
        @Override
        @Test
        @Depends("another")
        public B b(B b) {
            return b;
        }
    }
    
    @Test
    public void testParameterNotAssignableFromProvider() {
        Result r = JExample.run( E_fail.class );
        assertEquals( false, r.wasSuccessful() );
        assertEquals( 2, r.getRunCount() );
        assertEquals( 1, r.getFailureCount() );
        JExampleError err = (JExampleError) r.getFailures().get(0).getException();
        assertEquals( 1, err.size() );
        assertEquals( Kind.PARAMETER_NOT_ASSIGNABLE, err.kind() );
    }
    
    @Test
    @Ignore("JExample does not (yet) support inheritence and overrides.")
    public void testPolymorphicDepedency3() throws Exception {
        ExampleGraph $ = new ExampleGraph();
        $.add( F.class );
        
        //assertEquals( 1, $.getClasses().size() );
        assertEquals( 5, $.getMethods().size() );
        
        Example a = $.findExample( F.class, "a" );
        Example b = $.findExample( F.class, "b" );
        Example x = $.findExample( F.class, "another" );
        
        assertNotNull(a);
        assertNotNull(b);
        assertNotNull(x);
        assertEquals( 1, b.providers.size() );
        assertEquals( x, b.providers.iterator().next() );
        assertEquals( 1, a.providers.size() );
        assertEquals( b, a.providers.iterator().next() );
    }
    
    @RunWith( JExample.class )
    static class G {
        @Test( expected = Exception.class )
        public Object provider() throws Exception {
            throw new Exception();
        }
        @Test
        @Depends("provider")
        public void consumer(Object o) {
            // do nothing
        }
    }
    
    @Test
    public void providerMustNotExpectException() {
        Result r = JExample.run( G.class );
        assertEquals( false, r.wasSuccessful() );
        assertEquals( 2, r.getRunCount() );
        assertEquals( 1, r.getFailureCount() );
        JExampleError err = (JExampleError) r.getFailures().get(0).getException();
        assertEquals( 1, err.size() );
        assertEquals( Kind.PROVIDER_EXPECTS_EXCEPTION, err.kind() );
    }
    
    @RunWith( JExample.class )
    static class H {
        @Test
        public Object provider() {
            return new Object();
        }
        @Test
        @Depends("provider")
        public void consumer(Object a, Object b) {
            // do nothing
        }
    }    

    @Test
    public void lesProvidersThanParameters() {
        Result r = JExample.run( H.class );
        assertEquals( false, r.wasSuccessful() );
        assertEquals( 2, r.getRunCount() );
        assertEquals( 1, r.getFailureCount() );
        JExampleError err = (JExampleError) r.getFailures().get(0).getException();
        assertEquals( 1, err.size() );
        assertEquals( Kind.MISSING_PROVIDERS, err.kind() );
    }
    
}
