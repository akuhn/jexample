/**
 * 
 */
package ch.unibe.jexample.internal.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;

import ch.unibe.jexample.Given;
import ch.unibe.jexample.JExample;
import ch.unibe.jexample.demo.StackTest;
import ch.unibe.jexample.internal.Example;
import ch.unibe.jexample.internal.ExampleClass;
import ch.unibe.jexample.internal.ExampleGraph;
import ch.unibe.jexample.internal.JExampleError;
import ch.unibe.jexample.internal.JExampleError.Kind;

/**
 * @author Lea Haensenberger (lhaensenberger at students.unibe.ch)
 * 
 */
public class JExampleRunnerTest {

    @RunWith(JExample.class)
    private static class CycleOfThree {
        @Test
        @Given("ccc")
        public void aaa() {
        }

        @Test
        @Given("aaa")
        public void bbb() {
        }

        @Test
        @Given("bbb")
        public void ccc() {
        }
    }

    @Test
    public void cycleMethods() throws JExampleError {
        Class<?>[] classes = { CycleOfThree.class };
        ExampleGraph g = new ExampleGraph();
        Result result = g.runJExample(classes);
        assertEquals(false, result.wasSuccessful());
        assertEquals(3, result.getRunCount());
        assertEquals(3, result.getFailureCount());
        JExampleError err = (JExampleError) result.getFailures().get(0).getException();
        assertEquals(1, err.size());
        assertEquals(Kind.RECURSIVE_DEPENDENCIES, err.kind());
    }

    @RunWith(JExample.class)
    private static class SkipMethods {

        public SkipMethods() {
        }

        @Test
        public void firstMethod() {
            assertTrue(true);
        }

        // test is supposed to fail
        @Test
        @Given("firstMethod")
        public void secondMethod() {
            assertTrue(false);
        }

        // this test is ignored, because secondMethod failed
        @Test
        @Given("secondMethod")
        public void thirdMethod() {
            assertTrue(true);
        }

        // this test is ignored, because secondMethod failed
        @Test
        @Given("secondMethod")
        public void fourthMethod() {
            assertTrue(true);
        }
    }

    @Test
    public void skipMethods() throws JExampleError {
        Class<?>[] classes = { SkipMethods.class };
        ExampleGraph g = new ExampleGraph();
        Result result = g.runJExample(classes);
        assertEquals(1, result.getFailureCount());
        assertEquals(2, result.getIgnoreCount());
        assertEquals(2, result.getRunCount());
    }

    @RunWith(JExample.class)
    private static class GoodTest {
        public GoodTest() {
        }

        @Test
        public void firstMethod() {

        }

        @Test
        @Given("firstMethod")
        public void secondMethod() {
            assertTrue(true);
        }

        @Test
        @Given("secondMethod")
        public void thirdMethod() {
            assertTrue(true);
        }
    }

    @Test
    public void testGoodTest() throws JExampleError {
        Class<?>[] classes = { GoodTest.class };
        ExampleGraph g = new ExampleGraph();
        Result result = g.runJExample(classes);
        assertEquals(0, result.getFailureCount());
        assertEquals(0, result.getIgnoreCount());
        assertEquals(3, result.getRunCount());
    }

    @RunWith(JExample.class)
    private static class FirstGoodTest {
        public FirstGoodTest() {
        }

        @Test
        public void firstMethod() {

        }

        @Test
        @Given("JExampleRunnerTest$SecondGoodTest.secondMethod")
        public void thirdMethod() {
            assertTrue(true);
        }
    }

    @RunWith(JExample.class)
    private static class SecondGoodTest {
        public SecondGoodTest() {
        }

        @Test
        @Given("JExampleRunnerTest$FirstGoodTest.firstMethod")
        public void secondMethod() {
            assertTrue(true);
        }
    }

    @Test
    public void testGoodTests() throws JExampleError {
        Class<?>[] classes = { FirstGoodTest.class, SecondGoodTest.class };
        ExampleGraph g = new ExampleGraph();
        Result result = g.runJExample(classes);
        assertEquals(0, result.getFailureCount());
        assertEquals(0, result.getIgnoreCount());
        assertEquals(3, result.getRunCount());
    }

    @RunWith(JExample.class)
    private static class WithAttributes {
        public WithAttributes() {

        }

        @Test
        public String rootMethod() {
            return "Hello, I'm a string.";
        }

        @Test
        public int returnInteger() {
            return 2;
        }

        @Test
        public void noReturn() {

        }

        @Test
        @Given("rootMethod")
        public String getsString(String aString) {
            assertEquals("Hello, I'm a string.", aString);
            return aString;
        }

        @Test
        @Given("getsString(java.lang.String);returnInteger")
        public boolean getsStringAndInteger(String aString, int aInteger) {
            assertEquals("Hello, I'm a string.", aString);
            assertEquals(2, aInteger);
            return true;
        }

        @Test
        @Given("getsStringAndInteger(java.lang.String,int)")
        public void findsDep(boolean aBool) {
            assertTrue(aBool);
        }
    }

    @Test
    public void testWithAttributes() throws JExampleError {
        Class<?>[] classes = { WithAttributes.class };
        ExampleGraph g = new ExampleGraph();
        Result result = g.runJExample(classes);
        assertEquals(0, result.getFailureCount());
        assertEquals(0, result.getIgnoreCount());
        assertEquals(6, result.getRunCount());
    }

    @RunWith(JExample.class)
    private static class DependsOnBeforeTest {

        public DependsOnBeforeTest() {
        }

        @Test
        public int root() {
            return 2;
        }

        @Test
        @Given("root")
        public String second(int i) {
            assertEquals(2, i);
            return "bla";
        }

        @Test
        @Given("second(int)")
        public void third(String aString) {
            assertEquals("bla", aString);
        }
    }

    @Test
    public void testDependsOnBefore() throws JExampleError {
        Class<?>[] classes = { DependsOnBeforeTest.class };
        ExampleGraph g = new ExampleGraph();
        Result result = g.runJExample(classes);
        assertEquals(0, result.getFailureCount());
        assertEquals(0, result.getIgnoreCount());
        assertEquals(3, result.getRunCount());
    }

    @Test
    public void filter() throws NoTestsRemainException, JExampleError {
        ExampleGraph g = new ExampleGraph();
        Runner runner = new JExample(g.add(StackTest.class));
        Example e = g.findExample(StackTest.class, "withValue");
        ((Filterable) runner).filter(newFilter(e.description));
        Result r = new JUnitCore().run(runner);
        assertEquals(2, r.getRunCount());
        assertEquals(0, r.getIgnoreCount()); // it says filter, not ignore!
        assertEquals(0, r.getFailureCount());
        assertEquals(true, r.wasSuccessful());
    }

    private Filter newFilter(final Description d) {
        return new Filter() {
            @Override
            public String describe() {
                return String.format("Method %s", d);
            }

            @Override
            public boolean shouldRun(Description description) {
                if (d.isTest()) return d.equals(description);
                for (Description each: d.getChildren())
                    if (shouldRun(each)) return true;
                return false;
            }
        };
    }

    @RunWith(JExample.class)
    static class A_fail {
        @Test(expected = Exception.class)
        public void fail() {
            throw new Error();
        }
    }

    @Test
    public void unexpectedException() throws JExampleError {
        Class<?>[] classes = { A_fail.class };
        ExampleGraph g = new ExampleGraph();
        Result r = g.runJExample(classes);
        assertEquals(1, r.getRunCount());
        assertEquals(false, r.wasSuccessful());
        assertEquals(1, r.getFailureCount());
        assertTrue(r.getFailures().get(0).getMessage().startsWith("Unexpected exception, expected"));
    }

    @RunWith(JExample.class)
    static class B_fail {
        public void missingAnnotation() {
        }

        @Test
        @Given("#missingAnnotation")
        public void provider() {
        }

        @Test
        @Given("#provider")
        public void consumer() {
        }
    }

    @Test
    public void dependsOnNonTestMethodFails() throws JExampleError {
        Class<?>[] classes = { B_fail.class };
        ExampleGraph g = new ExampleGraph();
        Result result = g.runJExample(classes);
        assertEquals(false, result.wasSuccessful());
        assertEquals(1, result.getRunCount());
        assertEquals(1, result.getFailureCount());
    }

    @RunWith(JExample.class)
    static class C_fail {
        @Test
        @Given("D#test")
        public void test() {
            assertTrue(true);
        }
    }

    @RunWith(JExample.class)
    static class D_fail {
        @Test
        @Given("C#test")
        public void test() {
        }
    }

    @Test
    public void testBadTests() throws JExampleError {
        Class<?>[] classes = { D_fail.class, C_fail.class };
        ExampleGraph g = new ExampleGraph();
        Result result = g.runJExample(classes);
        assertEquals(2, result.getFailureCount());
        assertEquals(0, result.getIgnoreCount());
        assertEquals(2, result.getRunCount());
    }

    @Test
    public void exampleClassesAreUnique() throws JExampleError {
        ExampleGraph g = new ExampleGraph();
        ExampleClass aaa = g.add(StackTest.class);
        ExampleClass bbb = g.add(StackTest.class);

        assertSame(aaa, bbb);
    }

}
