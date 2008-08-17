package jexample.internal.tests;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import jexample.Depends;
import jexample.JExample;

@RunWith( JExample.class )
public class BeforeClassTest {

    static int n = 0;
 
    @BeforeClass
    public static void init() {
        n++;
    }

    @Test
    public void atLeastOnce() {
        assertEquals(1, n);
    }

    @Test
    @Depends("atLeastOnce")
    public void noMoreThanOnce() {
        assertEquals(1, n);
    }
    
}
