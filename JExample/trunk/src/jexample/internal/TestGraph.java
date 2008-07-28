package jexample.internal;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.internal.runners.InitializationError;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

/** Validates, describes and runs all JExample tests. Implemented as a singleton
 * in order to persist results between runs of many classes. JUnit's eclipse
 * plug-in runs all classes of a package in one bunch, but restarts JUnit whenever
 * test are rerun. Thus, this singleton does persists for one run only.
 * 
 * @author Lea Haensenberger
 * @author Adrian Kuhn
 * 
 */
public class TestGraph {

	private static TestGraph graph;
	private Set<TestClass> classesUnderTest;
	private Map<Method,TestMethod> testMethods;
	private List<Throwable> initializationErrors;
	private boolean anyHasBeenRun = false;


	public TestGraph() {
		this.classesUnderTest = new HashSet<TestClass>();
		this.testMethods = new HashMap<Method,TestMethod>();
		this.initializationErrors = new ArrayList<Throwable>();
	}

	public static TestGraph instance() {
		return graph == null ? graph = new TestGraph() : graph;
	}

	/**
	 * All {@link Method}'s are collected, checked for cycles, validated and
	 * then added to {@link Map} of all the {@link Method}'s to be run.
	 * 
	 * @param testClass
	 *            the {@link TestClass} to be added
	 * @throws InitializationError
	 */
	public TestClass addTestCase(Class<?> testCase) throws InitializationError {
	    if (anyHasBeenRun) throw new InitializationError();
	    TestClass $ = new TestClass(testCase, this).validate();
		Collection<TestMethod> news = new MethodCollector($)
		    .collect()
		    .validate()
		    .result();
		this.detectCycles(news);
		this.classesUnderTest.add($);
		for (TestMethod m : news)
		    testMethods.put(m.getJavaMethod(), m);
        if (!initializationErrors.isEmpty())
            throw new InitializationError(initializationErrors);
        return $;
	}

	/**
	 * The {@link Description}'s for the {@link Method}'s of this class are
	 * added as children to the class description. If there are dependencies
	 * from {@link TestMethod}'s which are not declared in a {@link TestClass}
	 * that is run in this turn, the {@link Description} of the declaring
	 * {@link Class} is also added as a child.
	 * 
	 * @param testClass
	 *            the {@link TestClass} to get the {@link Description} from
	 * @return the <code>description</code> for <code>testClass</code>;
	 */
	public Description descriptionForClass( TestClass testClass ) {
	    // TODO too long
		Description description = Description.createSuiteDescription( testClass.getJavaClass() );
		Set<Description> subDescriptions = new HashSet<Description>();
		for ( TestMethod method : this.getTestMethods() ) {
			if ( method.belongsToClass( testClass ) ) {
				description.addChild( method.getDescription() );
			} else if ( this.methodBelongsToNoClass( method ) ) {
				subDescriptions = this.addChildDescription( subDescriptions, method );
			}
	
		}
		for ( Description subDescription : subDescriptions ) {
			description.addChild( subDescription );
		}
		return description;
	}

	/**
	 * All {@link TestMethod}'s of <code>testClass</code> are run, inclusive
	 * their dependencies.
	 * 
	 * @param testClass
	 *            the {@link TestClass} to be run
	 * @param notifier
	 *            {@link RunNotifier}
	 */
	public void runClass(TestClass testClass, RunNotifier notifier) {
	     // TODO anyHasBeenRun = true; does not work because of tests nested in tests
		for (TestMethod method : this.getTestMethods()) {
			if (method.belongsToClass(testClass)) {
				method.run(notifier);
			}
		}
	}

	private void detectCycles( Collection<TestMethod> methods ) throws InitializationError {
		CycleDetector<TestMethod> detector = new CycleDetector<TestMethod>(methods) {
            @Override
            public Collection<TestMethod> getChildren(TestMethod tm) {
                return tm.getDependencies();
            }
		};
		if ( detector.hasCycle() ) {
			throw new InitializationError( "The dependencies are cyclic." );
		}
	}


	private Set<Description> addChildDescription( Set<Description> subDescriptions, TestMethod method ) {
		Class<?> declaringClass = method.getDeclaringClass();
		Description description;

		if ( ( description = this.getDescriptionForClass( declaringClass, subDescriptions ) ) == null ) {
			description = Description.createSuiteDescription( declaringClass );
			subDescriptions.add( description );
		}
		description.addChild( method.getDescription() );

		return subDescriptions;
	}

	private Description getDescriptionForClass( Class<?> declaringClass, Set<Description> subDescriptions ) {
		for ( Description description : subDescriptions ) {
			if ( description.getDisplayName().equals( declaringClass.getName() ) ) {
				return description;
			}
		}
		return null;
	}

	private boolean methodBelongsToNoClass( TestMethod method ) {
		for ( TestClass testClass : this.classesUnderTest ) {
			if ( method.belongsToClass( testClass ) ) {
				return false;
			}
		}
		return true;
	}

	public Collection<Method> getMethods() {
		return this.testMethods.keySet();
	}
	
	public Collection<TestMethod> getTestMethods() {
	    return this.testMethods.values();
	}

	public Collection<TestClass> getClasses() {
		return this.classesUnderTest;
	}

    public static TestClass addClass(Class<?> testCase) throws InitializationError {
        return TestGraph.instance().addTestCase(testCase);
    }
    
    private class MethodCollector {
        
        private Map<Method, TestMethod> found;
        private Collection<Method> todo;
        
        public MethodCollector(TestClass testClass) {
            found = new HashMap();
            todo = new HashSet(testClass.getTestMethods());
        }
        
        public MethodCollector validate() throws InitializationError {
            for (TestMethod m : found.values()) m.validate();
            return this;
        }

        public MethodCollector collect() {
            while (!todo.isEmpty()) {
                Method $ = todo.iterator().next();
                process($);
                todo.remove($);
            }
            return this;
        }
        
        private void process(Method m) {
            TestMethod $ = testMethod(m);
            for (Method d : $.dependencies()) {
                $.addDependency(testMethod(d));
            }
        }

        private TestMethod testMethod(Method m) {
            TestMethod $ = testMethods.get(m);
            if ($ != null) return $;
            $ = found.get(m);
            if ($ != null) return $;
            $ = new TestMethod(m, TestGraph.this);
            found.put(m, $);
            todo.add(m);
            return $;
        }

        public Collection<TestMethod> result() {
            return found.values(); 
        }
        
        
        
    }

    public TestMethod getTestMethod(Method m) {
        return testMethods.get(m);
    }

    public void addInitializationError(Throwable ex) {
        initializationErrors.add(ex);
    }
    
}
