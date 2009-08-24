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
    public List<String> emptyList() {
        List<String> list = new ArrayList<String>();
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
        return list;
    }

    @Given("#emptyList")
    public List<String> withValue(List<String> list) {
        list.add("Lorem");
        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
        return list;
    }

    @Given("#withValue")
    public List<String> withMoreValues(List<String> list) {
        list.add("Ipsum");
        list.add("Dolor");
        assertFalse(list.isEmpty());
        assertEquals(3, list.size());
        return list;
    }

}
