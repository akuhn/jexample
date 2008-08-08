package jexample.internal;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jexample.JExampleRunner;
import jexample.internal.InvalidExampleError.Kind;
import jexample.internal.tests.StackTest;

import org.junit.internal.runners.CompositeRunner;
import org.junit.internal.runners.InitializationError;
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
public class ExampleGraph {

	private static ExampleGraph GRAPH;
	private Map<Method,Example> examples;
	private List<Throwable> initializationErrors;
	private boolean anyHasBeenRun = false;


	public ExampleGraph() {
		this.examples = new HashMap();
		this.initializationErrors = new ArrayList();
	}

	public static ExampleGraph instance() {
		return GRAPH == null ? GRAPH = new ExampleGraph() : GRAPH;
	}

	/** All methods are collected, checked for cycles, validated and
	 * then added to the list of examples.
	 */
	public TestClass add(Class<?> javaClass) throws InitializationError {
	    if (anyHasBeenRun) throw new InitializationError("Cannot add test to running system.");
	    TestClass $ = new TestClass(javaClass, this).validate();
		Collection<Example> novel = new ExampleCollector($)
		    .collect()
		    .validate()
		    .result();
		List<Example> cycle = this.detectCycles(novel, examples.values());
		if (cycle == null) 
	        for (Example e : novel) examples.put(e.jmethod, e);
		else
		    this.throwNewError(Kind.RECURSIVE_DEPENDENCIES,
		            "Recursive dependency in cycle %s", cycle); 
       if (!initializationErrors.isEmpty())
            throw new InitializationError(initializationErrors);
        initializationErrors.clear();
        return $;
	}

	public void run(TestClass testClass, RunNotifier notifier) {
	    anyHasBeenRun = true;
		for (Example e : this.getExamples()) {
			if (testClass.contains(e)) {
				e.run(notifier);
			}
		}
	}

	private List<Example> detectCycles(Collection<Example>... es) {
		CycleDetector<Example> detector = new CycleDetector<Example>(es) {
            @Override
            public Collection<Example> getChildren(Example e) {
                return e.providers;
            }
		};
		return detector.getCycle();
	}

	public Collection<Method> getMethods() {
		return this.examples.keySet();
	}
	
	public Collection<Example> getExamples() {
	    return this.examples.values();
	}

    private class ExampleCollector {
        
        private Map<Method, Example> found;
        private Collection<Method> todo;
        
        public ExampleCollector(TestClass testClass) {
            found = new HashMap();
            todo = new HashSet(testClass.collectTestMethods());
        }
        
        public ExampleCollector validate() {
            for (Example e : found.values())
                e.validate();
            return this;
        }

        public ExampleCollector collect() {
            while (!todo.isEmpty()) {
                Method $ = todo.iterator().next();
                process($);
                todo.remove($);
            }
            return this;
        }
        
        private void process(Method m) {
            Example $ = makeExample(m);
            for (Method d : $.collectDependencies()) {
                $.providers.add(makeExample(d));
            }
        }

        private Example makeExample(Method m) {
            Example e = examples.get(m);
            if (e != null) return e;
            e = found.get(m);
            if (e != null) return e;
            Example $ = new Example(m, ExampleGraph.this);
            found.put(m, $);
            todo.add(m);
            return $;
        }

        public Collection<Example> result() {
            return found.values(); 
        }
        
    }

    public Example getExample(Method m) {
        return examples.get(m);
    }

    public void throwNewError(Kind kind, String message, Object... args) {
        Exception $ = new InvalidExampleError(kind, message, args);
        $.fillInStackTrace();
        initializationErrors.add($);
    }

    public void throwNewError(Kind kind, Throwable cause) {
        Exception $ = new InvalidExampleError(kind, cause, cause.getMessage());
        $.fillInStackTrace();
        initializationErrors.add($);
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

    public Example findExample(Class<?> c, String name) {
        Example found = null;
        for (Example e : getExamples()) {
            if (e.jmethod.getDeclaringClass() == c && e.jmethod.getName().equals(name)) {
                if (found != null) throw new RuntimeException();
                found = e;
            }
        }
        return found;
    }

    public void filter(Filter filter) {
        Iterator<Example> it = examples.values().iterator();
        while (it.hasNext()) {
            if (!filter.shouldRun(it.next().description)) {
                it.remove();
            }
        }
        // copy list of values to avoid concurrent modification
        Collection<Example> copy = new ArrayList(examples.values()); 
        for (Example e : copy) {
            for (Example dependency : e.providers.transitiveClosure()) {
                examples.put(dependency.jmethod, dependency);
            }
        }
    }

}
