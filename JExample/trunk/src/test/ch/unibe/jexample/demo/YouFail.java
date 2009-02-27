package ch.unibe.jexample.demo;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.unibe.jexample.JExample;

@RunWith( JExample.class )
public class YouFail {

    @Test
    public void fail() {
        assertEquals( true, false );
    }
    
}
