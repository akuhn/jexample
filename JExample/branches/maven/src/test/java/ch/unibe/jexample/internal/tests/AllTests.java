package ch.unibe.jexample.internal.tests;

import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.unibe.jexample.demo.ListTest;
import ch.unibe.jexample.demo.StackTest;

@RunWith(Suite.class)
@SuiteClasses( { BeforeClassTest.class, CanRunPrivateInnerClassTest.class, DependenciesTest.class,
        DependsParserTest.class, DependsScannerTest.class, ExampleValidateTest.class, ExampleGraphAddTest.class,
        ExampleGraphTest.class, ExceptionExpected.class, ForExampleTest.class, InheritanceTest.class,
        JExampleOptionsDefault.class, JExampleOptionsDontCloneReturnValues.class, JExampleTest.class,
        JExampleRunnerTest.class, ReturnValueTest.class, TestClassValidationTest.class, StackTest.class, ListTest.class })
public class AllTests {
    public static void main(String[] args) {
        JUnitCore junit = new JUnitCore();
        junit.run(AllTests.class);
    }
}
