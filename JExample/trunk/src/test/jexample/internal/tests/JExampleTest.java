package jexample.internal.tests;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;

public class JExampleTest {

    private static class Event {
        public final StackTraceElement activation;
        public final int index;
        public final Object arg;
        private Object self;
        public Event(int index, Object self, StackTraceElement activation, Object arg) {
            this.self = self;
            this.index = index;
            this.activation = activation;
            this.arg = arg;
        }
        @Override
        public String toString() {
            String $ = self.getClass().getName();
            $ = $.substring(Math.max($.lastIndexOf('.'), $.lastIndexOf('$')) +1);
            return $ + "#" + activation.getMethodName();
        }
        public boolean before(String s) {
            return index < find(s).index;
        }
        public boolean after(String s) {
            return index > find(s).index;
        }
    }
    
    private static List<Event> events;
    
    public static void resetTrace() {
        events = new ArrayList<Event>();
    }

    public static void trace(Object self, Object... args) {
        events.add(new Event(events.size(), self, Thread.currentThread().getStackTrace()[2],
                args.length == 0 ? null : args[0]));
    }

    private static Event find(String s) {
        for (Event e : events) {
            if (e.toString().equals(s)) return e;
        }
        return null;
    }
    
    @Test
    public void testTrace() {
        resetTrace();
        assertTraceSize( 0 );
        trace( this, "Foobar" );
        assertTraceSize( 1 );
        assertTrace( this.getClass().getSimpleName() + "#testTrace" );
        assertTraceArgument( this.getClass().getSimpleName() + "#testTrace", "Foobar" );
    }

    public void assertTrace(String... strings) {
        Event prev = null;
        for (String s : strings) {
            Event curr = find(s);
            assertNotNull( s, curr );
            if (prev != null) assertTrue( s, prev.index < curr.index );
            prev = curr;
        }
    }

    public void assertTraceSize(int size) {
        assertEquals( size, events.size() );
    }

    protected void assertTraceArgument(String s, Object arg) {
        assertEquals( arg, find(s).arg);
    }

    
}
