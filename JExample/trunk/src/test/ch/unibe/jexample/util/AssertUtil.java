package ch.unibe.jexample.util;

import junit.framework.Assert;

public class AssertUtil {

    public static void assertToString(String expected, Object actual) {
        Assert.assertEquals(expected, actual.toString());
    }

    public static void assertToStringFormat(String format, Object actual, Object... args) {
        Assert.assertEquals(String.format(format, args), actual.toString());
    }
    
    
}
