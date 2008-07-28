package jexample.internal.tests;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;

import jexample.Depends;
import jexample.InjectionPolicy;
import jexample.JExampleRunner;
import jexample.internal.TestGraph;

public class NoClonePolicy {

    public static class A {
        
    }
    
    @RunWith( JExampleRunner.class )
    @InjectionPolicy( keep = true )
    public static class B {
        
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
        Result $ = new JUnitCore().run(new TestGraph().newJExampleRunner( B.class ));
        assertTrue( $.wasSuccessful() );
    }
    
}
