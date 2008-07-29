package demo;

import static org.junit.Assert.*;
import jexample.Depends;
import jexample.JExampleRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JExampleRunner.class)
public class Fail {

    @Test
    @Depends("StackTest.withManyValues")
    public void fail() {
        assertTrue(false);
    }
    
}
