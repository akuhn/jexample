package ch.unibe.jexample;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.unibe.jexample.deepclone.DeepCloneTest;
import ch.unibe.jexample.demo.ListTest;
import ch.unibe.jexample.demo.StackTest;
import ch.unibe.jexample.internal.BrokenDependencyTest;
import ch.unibe.jexample.internal.tests.*;


@RunWith(Suite.class)
@SuiteClasses( {
    BrokenDependencyTest.class,
    ForceCloneSuperclass.class,
    DeepCloneSerializableObjects.class,
    DeepCloneTest.class,
    BeforeClassTest.class, 
    CanRunPrivateInnerClassTest.class,
    DependenciesTest.class,
    DependsParserTest.class,
    DependsScannerTest.class,
    ExampleValidateTest.class,
    ExampleGraphAddTest.class,
    ExampleGraphTest.class,
    ExceptionExpected.class,
    ForExampleTest.class,
    InheritanceTest.class,
    JExampleOptionsDefault.class,
    JExampleOptionsDontCloneReturnValues.class,
    JExampleTest.class,
    JExampleRunnerTest.class,
    ReturnValueTest.class,
    TestClassValidationTest.class,
    StackTest.class,
    ListTest.class })
public class AllTests {

}
