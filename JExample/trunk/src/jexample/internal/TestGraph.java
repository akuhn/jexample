package jexample.internal;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jexample.JExampleRunner;

import org.junit.internal.runners.CompositeRunner;
import org.junit.internal.runners.InitializationError;
import org.junit.runner.Description;
import org.junit.runner.Request;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.notification.RunNotifier;

/** Validates, describes and runs all JExample tests. Implemented as a singleton
 * in order to persist results between runs of many classes. JUnit's eclipse
 * plug-in runs all classes of a package in one bunch, but restarts JUnit for
 * each new launch. Thus, this singleton should work for Eclipse.
 * 
 * @author Lea Haensenberger
 * @author Adrian Kuhn
 * 
 */
public class TestGraph {

	private static TestGraph GRAPH;
	private Map<Method,TestMethod> testMethods;
	private List<Throwable> initializationErrors;
	private boolean anyHasBeenRun = false;


	public TestGraph() {
		this.testMethods = new HashMap<Method,TestMethod>();
		this.initializationErrors = new ArrayList<Throwable>();
	}

	public static TestGraph instance() {
		return GRAPH == null ? GRAPH = new TestGraph() : GRAPH;
	}

	/**
	 * All {@link Method}'s are collected, checked for cycles, validated and
	 * then added to {@link Map} of all the {@link Method}'s to be run.
	 * 
	 * @param testClass
	 *            the {@link TestClass} to be added
	 * @throws InitializationError
	 */
	public TestClass add(Class<?> javaClass) throws InitializationError {
	    if (anyHasBeenRun) throw new InitializationError();
	    TestClass $ = new TestClass(javaClass, this).validate();
		Collection<TestMethod> news = new MethodCollector($)
		    .collect()
		    .validate()
		    .result();
		this.detectCycles(news, testMethods.values());
		for (TestMethod m : news)
		    testMethods.put(m.getJavaMethod(), m);
        if (!initializationErrors.isEmpty())
            throw new InitializationError(initializationErrors);
        initializationErrors.clear();
        return $;
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
	public void run(TestClass testClass, RunNotifier notifier) {
	    anyHasBeenRun = true;
		for (TestMethod method : this.getTestMethods()) {
			if ( testClass.contains(method)) {
				method.run(notifier);
			}
		}
	}

	private void detectCycles( Collection<TestMethod>... methods ) throws InitializationError {
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

	public Collection<Method> getMethods() {
		return this.testMethods.keySet();
	}
	
	public Collection<TestMethod> getTestMethods() {
	    return this.testMethods.values();
	}

    private class MethodCollector {
        
        private Map<Method, TestMethod> found;
        private Collection<Method> todo;
        
        public MethodCollector(TestClass testClass) {
            found = new HashMap();
            todo = new HashSet(testClass.collectTestMethods());
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
            for (Method d : $.collectDependencies()) {
                $.providers.add(testMethod(d));
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

    public void throwNewError(String message, Object... args) {
        Exception $ = new Exception(String.format(message, args));
        $.fillInStackTrace();
        this.addInitializationError($);
    }
    
    public void addInitializationError(Throwable th) {
        initializationErrors.add(th);
    }

    public Runner newJExampleRunner(Class<?>... classes) {
        CompositeRunner $ = new CompositeRunner("All");
        for (Class<?> c : classes) {
            $.add(newJExampleRunner(c));
        }
        return $;
    }    
        
    public Runner newJExampleRunner(Class<?> c) {
        try {
            TestClass test = this.add(c);
            return new JExampleRunner(test);
        } 
        catch (InitializationError err) { 
            return Request.errorReport(c, err).getRunner();
        }
    }

    public TestMethod getTestMethod(Class<?> c, String name) {
        TestMethod found = null;
        for (TestMethod tm : getTestMethods()) {
            if (tm.javaMethod.getDeclaringClass() == c && tm.getJavaMethod().getName().equals(name)) {
                if (found != null) throw new RuntimeException();
                found = tm;
            }
        }
        return found;
    }

    public void filter(Filter filter) {
        Iterator<TestMethod> it = testMethods.values().iterator();
        while (it.hasNext())
            if (!filter.shouldRun(it.next().description))
                it.remove();
        Collection<TestMethod> copy = new ArrayList(testMethods.values());
        for (TestMethod tm : copy)
            for (TestMethod dep : tm.providers.transitiveClosure())
                testMethods.put(dep.getJavaMethod(), dep);
    }

}
