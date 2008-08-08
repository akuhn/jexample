package jexample.internal;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jexample.JExample;

import org.junit.internal.runners.CompositeRunner;
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
	private Map<Class,ExampleClass> classes;
	private boolean anyHasBeenRun = false;



	public ExampleGraph() {
		this.examples = new HashMap();
		this.classes = new HashMap();
	}


	public static ExampleGraph instance() {
		return GRAPH == null ? GRAPH = new ExampleGraph() : GRAPH;
	}


    protected ExampleClass newExampleClass(Class jclass) {
        ExampleClass $ = classes.get(jclass);
        if ($ == null) { 
            $ = new ExampleClass(jclass, this);
            classes.put(jclass, $);
        }
        return $;
    }

    protected Example newExample(Method jmethod) {
        Example e = examples.get(jmethod);
        if (e != null) return e;
        e = new Example(jmethod);
        examples.put(jmethod, e);
        for (Method m : e.collectDependencies()) {
            Example d = newExample(m);
            e.providers.add(d);
            e.providers.invalidateCycle(e);
        }
        e.validate();
        return e;
    }

	public ExampleClass add(Class<?> jclass) throws JExampleError {
	    if (anyHasBeenRun) throw new RuntimeException("Cannot add test to running system.");
	    ExampleClass $ = newExampleClass(jclass);
	    $.validate();
	    $.initializeExamples();
        return $;
	}

    public void run(ExampleClass testClass, RunNotifier notifier) {
	    anyHasBeenRun = true;
		for (Example e : this.getExamples()) {
			if (testClass.contains(e)) {
				e.run(notifier);
			}
		}
	}

	public Collection<Method> getMethods() {
		return this.examples.keySet();
	}
	
	public Collection<Example> getExamples() {
	    return this.examples.values();
	}

    public Example getExample(Method m) {
        return examples.get(m);
    }

    public Runner newJExampleRunner(Class<?>... all) {
        CompositeRunner $ = new CompositeRunner("All");
        for (Class<?> c : all) {
            $.add(newJExampleRunner(c));
        }
        return $;
    }    
        
    public Runner newJExampleRunner(Class<?> c) {
        try {
            ExampleClass test = this.add(c);
            return new JExample(test);
        } 
        catch (JExampleError err) { 
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
