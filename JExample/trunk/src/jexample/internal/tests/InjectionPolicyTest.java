package jexample.internal.tests;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import jexample.Depends;
import jexample.InjectionPolicy;
import jexample.JExampleRunner;
import jexample.internal.ExampleGraph;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;

public class InjectionPolicyTest {

    private static class A {
        
    }
    
    @RunWith( JExampleRunner.class )
    @InjectionPolicy( keep = true )
    private static class B {
        
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
    public void runCloneDetection() {
        Result $ = new JUnitCore().run(new ExampleGraph().newJExampleRunner( B.class ));
        assertTrue( $.wasSuccessful() );
    }
    
}
