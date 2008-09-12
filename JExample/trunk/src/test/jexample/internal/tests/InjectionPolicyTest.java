package jexample.internal.tests;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import jexample.Depends;
import jexample.InjectionPolicy;
import jexample.JExample;
import jexample.internal.ExampleGraph;
import jexample.internal.JExampleError;

import org.junit.Test;
import org.junit.runner.Result;
import org.junit.runner.RunWith;

public class InjectionPolicyTest {

    static class A {
        
    }
    
    @RunWith( JExample.class )
    @InjectionPolicy( keep = true )
    static class B {
        
        @Test
        public A create() {
            return new A();
        }
        
        @Test
        @Depends("create")
        public A left(A a) {
            return a;
        }
        
        @Test
        @Depends("create")
        public A right(A a) {
            return a;
        }
        
        @Test
        @Depends("left;right")
        public void test(A left, A right) {
            assertSame( left, right );
        }
    }
    
    @Test
    public void runCloneDetection() throws JExampleError {
        Result $ = new ExampleGraph().runJExample( B.class );
        assertTrue( $.wasSuccessful() );
    }
    
}
