package jexample.internal.tests;

import java.lang.reflect.Method;
import java.util.*;

public class Trace {

    public static class Event {
        public final String impl;
        public final String method;
        public final Object[] args;
        public final Object arg;
        public Event(String impl, String method, Object... args) {
            this.impl = impl;
            this.method = method;
            this.args = args;
            this.arg = args.length == 0 ? null : args[0];
        }
    }
    
    public static List<Event> events;
    
    public static void reset() {
        events = new ArrayList<Event>();
    }
    
    public static void record(Object... args) {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        String $ = stack[2].getClassName();
        $ = $.substring($.lastIndexOf(".") + 1);
        events.add(new Event($, stack[2].getMethodName(), args));
    }
    
}
