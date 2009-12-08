package ch.unibe.jexample;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.unibe.jexample.demo.ListTest;
import ch.unibe.jexample.demo.StackTest;
import ch.unibe.jexample.internal.BrokenDependencyTest;
import ch.unibe.jexample.internal.CloneInjectionStrategyTest;
import ch.unibe.jexample.internal.CloneInjectionValuesTest;
import ch.unibe.jexample.internal.GivenLinearDependenciesTest;
import ch.unibe.jexample.internal.InvalidDependencyTest;
import ch.unibe.jexample.internal.RerunInjectionStrategyTest;
import ch.unibe.jexample.internal.RerunInjectionValuesTest;
import ch.unibe.jexample.internal.WhenReturnValueMissing;
import ch.unibe.jexample.internal.deepclone.DeepCloneHashMapTest;
import ch.unibe.jexample.internal.deepclone.DeepCloneReferenceTest;
import ch.unibe.jexample.internal.deepclone.DeepCloneTest;
import ch.unibe.jexample.internal.deepclone.ImmutableClassesTest;
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
@SuiteClasses({
    BeforeClassTest.class, 
    BrokenDependencyTest.class,
    CanRunPrivateInnerClassTest.class,
    CloneInjectionStrategyTest.class,
    CloneInjectionValuesTest.class,
    CycleDetectionTest.class,
    ForceCloneSuperclass.class,
    DeepCloneSerializableObjects.class,
    DeepCloneTest.class,
    DeepCloneHashMapTest.class,
    DeepCloneReferenceTest.class,
    DependenciesTest.class,
    DependsParserTest.class,
    DependsScannerTest.class,
    ExampleValidateTest.class,
    ExampleGraphAddTest.class,
    ExampleGraphTest.class,
    ExceptionExpected.class,
    ForExampleTest.class,
    GivenLinearDependenciesTest.class,
    InheritanceTest.class,
    InvalidDependencyTest.class,
    ImmutableClassesTest.class,
    JExampleOptionsDefault.class,
    JExampleOptionsDontCloneReturnValues.class,
    JExampleTest.class,
    JExampleRunnerTest.class,
    RerunInjectionStrategyTest.class,
    RerunInjectionValuesTest.class,
    ReturnValueTest.class,
    TestClassValidationTest.class,
    SmallGraph.class,
    StackTest.class,
    ListTest.class,
    WhenReturnValueMissing.class
})
public class AllTests {

}
