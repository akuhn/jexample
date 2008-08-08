package demo;

import static org.junit.Assert.*;
import jexample.JExample;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith( JExample.class )
public class YouFail {

    @Test
    public void fail() {
        assertEquals( true, false );
    }
    
}
