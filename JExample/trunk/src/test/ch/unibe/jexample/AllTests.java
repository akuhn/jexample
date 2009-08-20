package ch.unibe.jexample;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.unibe.jexample.deepclone.DeepCloneHashMapTest;
import ch.unibe.jexample.deepclone.DeepCloneReferenceTest;
import ch.unibe.jexample.deepclone.DeepCloneTest;
import ch.unibe.jexample.demo.ListTest;
import ch.unibe.jexample.demo.StackTest;
import ch.unibe.jexample.internal.BrokenDependencyTest;
import ch.unibe.jexample.internal.InvalidDependencyTest;
import ch.unibe.jexample.internal.graph.CycleDetectionTest;
import ch.unibe.jexample.internal.graph.SmallGraph;
import ch.unibe.jexample.internal.tests.BeforeClassTest;
import ch.unibe.jexample.internal.tests.CanRunPrivateInnerClassTest;
import ch.unibe.jexample.internal.tests.DeepCloneSerializableObjects;
import ch.unibe.jexample.internal.tests.DependenciesTest;
import ch.unibe.jexample.internal.tests.DependsParserTest;
import ch.unibe.jexample.internal.tests.DependsScannerTest;
import ch.unibe.jexample.internal.tests.ExampleGraphAddTest;
import ch.unibe.jexample.internal.tests.ExampleGraphTest;
import ch.unibe.jexample.internal.tests.ExampleValidateTest;
import ch.unibe.jexample.internal.tests.ExceptionExpected;
import ch.unibe.jexample.internal.tests.ForExampleTest;
import ch.unibe.jexample.internal.tests.ForceCloneSuperclass;
import ch.unibe.jexample.internal.tests.InheritanceTest;
import ch.unibe.jexample.internal.tests.JExampleOptionsDefault;
import ch.unibe.jexample.internal.tests.JExampleOptionsDontCloneReturnValues;
import ch.unibe.jexample.internal.tests.JExampleRunnerTest;
import ch.unibe.jexample.internal.tests.JExampleTest;
import ch.unibe.jexample.internal.tests.ReturnValueTest;
import ch.unibe.jexample.internal.tests.TestClassValidationTest;


@RunWith(Suite.class)
@SuiteClasses( {
    BrokenDependencyTest.class,
    ForceCloneSuperclass.class,
    CycleDetectionTest.class,
    DeepCloneSerializableObjects.class,
    DeepCloneTest.class,
    DeepCloneHashMapTest.class,
    DeepCloneReferenceTest.class,
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
    InvalidDependencyTest.class,
    JExampleOptionsDefault.class,
    JExampleOptionsDontCloneReturnValues.class,
    JExampleTest.class,
    JExampleRunnerTest.class,
    ReturnValueTest.class,
    TestClassValidationTest.class,
    SmallGraph.class,
    StackTest.class,
    ListTest.class })
public class AllTests {

}
