package extension.tests;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

// These test files need to be cleaned.  See
// https://sourceforge.net/pm/task.php?func=detailtask&project_task_id=136507&group_id=15278&group_project_id=51407

@RunWith(Suite.class)
@SuiteClasses({
	ComposedTestRunnerTest.class,
	CycleDetectorTest.class,
	DependencyParserTest.class,
	DependencyValidatorTest.class,
	GraphTest.class,
})
public class AllTests {
	public static Test suite() {
		return new JUnit4TestAdapter(AllTests.class);
	}
}
