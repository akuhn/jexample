package demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import jexample.Depends;
import jexample.JExample;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JExample.class)
public class ListTest {

    @Test
    public List empty() {
        List $ = new ArrayList();
        assertTrue($.isEmpty());
        assertEquals(0, $.size());
        return $;
    }
    
    @Test
    @Depends("#empty")
    public List withValue(List $) {
        $.add("Lorem");
        assertFalse($.isEmpty());
        assertEquals(1, $.size());
        return $;
    }

    @Test
    @Depends("#withValue")
    public List withMoreValues(List $) {
        $.add("Ipsum");
        $.add("Dolor");
        assertFalse($.isEmpty());
        assertEquals(3, $.size());
        return $;
    }
    
}
