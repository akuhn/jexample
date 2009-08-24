package ch.unibe.jexample.internal.tests;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Test;

import ch.unibe.jexample.internal.util.InvalidDeclarationError;
import ch.unibe.jexample.internal.util.MethodLocator;

public class DependsScannerTest {

    @Test
    public void simpleName() {
        MethodLocator[] tokens = scan("#method");
        assertEquals(1, tokens.length);
        assertNull(tokens[0].path);
        assertEquals("method", tokens[0].simple);
        assertNull(tokens[0].args);
    }

    @SuppressWarnings("unchecked")
    private MethodLocator[] scan(String string) {
        ArrayList list = new ArrayList();
        for (MethodLocator each: MethodLocator.parseAll(string)) {
            list.add(each);
        }
        return (MethodLocator[]) list.toArray(new MethodLocator[list.size()]);
    }

    @Test
    public void empty() {
        MethodLocator[] tokens = scan("");
        assertEquals(0, tokens.length);
    }

    @Test(expected = InvalidDeclarationError.class)
    public void hash() {
        scan("#");
    }

    @Test
    public void whitespace() {
        MethodLocator[] tokens = scan("       ");
        assertEquals(0, tokens.length);
    }

    @Test
    public void fullName() {
        MethodLocator[] tokens = scan("ch.akuhn.package.Class#method");
        assertEquals(1, tokens.length);
        assertEquals("ch.akuhn.package.Class", tokens[0].path);
        assertEquals("method", tokens[0].simple);
        assertNull(tokens[0].args);
    }

    @Test
    public void emptyParams() {
        MethodLocator[] tokens = scan("#method()");
        assertEquals(1, tokens.length);
        assertNull(tokens[0].path);
        assertEquals("method", tokens[0].simple);
        assertNotNull(tokens[0].args);
        assertEquals(0, tokens[0].args.length);
    }

    @Test
    public void oneParam() {
        MethodLocator[] tokens = scan("#method(int)");
        assertEquals(1, tokens.length);
        assertNull(tokens[0].path);
        assertEquals("method", tokens[0].simple);
        assertArrayEquals("int".split(","), tokens[0].args);
    }

    @Test
    public void manyParam() {
        MethodLocator[] tokens = scan("#method(int,String,List)");
        assertEquals(1, tokens.length);
        assertNull(tokens[0].path);
        assertEquals("method", tokens[0].simple);
        assertArrayEquals("int,String,List".split(","), tokens[0].args);
    }

    @Test
    public void fullParam() {
        MethodLocator[] tokens = scan("#method(int,java.lang.String,java.util.List)");
        assertEquals(1, tokens.length);
        assertNull(tokens[0].path);
        assertEquals("method", tokens[0].simple);
        assertArrayEquals("int,java.lang.String,java.util.List".split(","), tokens[0].args);
    }

    @Test
    public void tokenlist() {
        MethodLocator[] tokens = scan("#aa, #bb, #cc, #dd");
        assertEquals(4, tokens.length);
        assertEquals("dd", tokens[3].simple);
    }

    @Test
    public void tokenlistWithSemicolons() {
        MethodLocator[] tokens = scan("#aa; #bb; #cc; #dd");
        assertEquals(4, tokens.length);
        assertEquals("dd", tokens[3].simple);
    }

    @Test
    public void dollarRulesTheWorld() {
        MethodLocator[] tokens = scan("#$$$, #$$$, #$$$");
        assertEquals(3, tokens.length);
        assertEquals("$$$", tokens[2].simple);
    }

    @Test
    public void tokenlistWithParams() {
        MethodLocator[] tokens = scan("#aa(int, int), #bb(int, int), #cc(int, int), #dd(int, int, long)");
        assertEquals(4, tokens.length);
        assertEquals("dd", tokens[3].simple);
        assertEquals(3, tokens[3].args.length);
        assertEquals("long", tokens[3].args[2]);
    }

    @Test
    public void fancyLongDeclaration() {
        MethodLocator[] tokens = scan("demo.StackTest#empty, #withValues(Stack), #withManyValues(java.util.Stack), #cc, #dd(int, int, long)");
        assertEquals(5, tokens.length);
        assertEquals("dd", tokens[4].simple);
        assertEquals(3, tokens[4].args.length);
        assertEquals("long", tokens[4].args[2]);
    }

    @Test
    public void errorsInfancyLongDeclaration() {
        String $ = "demo.StackTest#empty, #withValues(Stack), #withManyValues(java.util.Stack), #cc, #dd(int, int, long)";
        for (int n = 0; n < $.length(); n++) {
            String fail = $.substring(0, n) + "'" + $.substring(n);
            try {
                scan(fail);
                fail();
            } catch (InvalidDeclarationError ex) {
                assertEquals(fail, n, ex.position);
            }
        }
    }

    @Test
    public void lenientFancyLongDeclaration() {
        MethodLocator[] tokens = scan("demo.StackTest.empty, withValues(Stack), withManyValues(java.util.Stack), cc, dd(int, int, long)");
        assertEquals(5, tokens.length);
        assertEquals("dd", tokens[4].simple);
        assertEquals(3, tokens[4].args.length);
        assertEquals("long", tokens[4].args[2]);
    }

    @Test
    public void tokenToString() {
        // Make code coverage tools happy
        MethodLocator[] tokens = scan("#foo");
        assertNotNull(tokens[0].toString());
    }

}
