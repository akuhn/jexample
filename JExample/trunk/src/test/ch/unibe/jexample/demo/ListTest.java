package ch.unibe.jexample.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;


import org.junit.Test;
import org.junit.runner.RunWith;

import ch.unibe.jexample.Given;
import ch.unibe.jexample.JExample;

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
    @Given("#empty")
    public List withValue(List $) {
        $.add("Lorem");
        assertFalse($.isEmpty());
        assertEquals(1, $.size());
        return $;
    }

    @Test
    @Given("#withValue")
    public List withMoreValues(List $) {
        $.add("Ipsum");
        $.add("Dolor");
        assertFalse($.isEmpty());
        assertEquals(3, $.size());
        return $;
    }
    
}
