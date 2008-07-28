package demo;

import static org.junit.Assert.*;
import jexample.JExampleRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JExampleRunner.class)
public class Fail {

    @Test
    public void fail() {
        assertTrue(false);
    }
    
}
