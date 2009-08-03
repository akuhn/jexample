package ch.unibe.jexample.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.manipulation.Sortable;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.RunNotifier;

import ch.unibe.jexample.internal.ExampleGraph;

/** Copypasta from JUnit 4.4 sources, since missing in JUnit 4.5 and beyond.
 *
 */
public class CompositeRunner extends Runner implements Filterable, Sortable {
        private final List<Runner> fRunners= new ArrayList<Runner>();
        
        @Override
        public void run(RunNotifier notifier) {
                runChildren(notifier);
        }

        protected void runChildren(RunNotifier notifier) {
                for (Runner each : fRunners)
                        each.run(notifier);
        }

        @Override
        public Description getDescription() {
                Description spec= Description.createSuiteDescription(ExampleGraph.class);
                for (Runner runner : fRunners)
                        spec.addChild(runner.getDescription());
                return spec;
        }

        public List<Runner> getRunners() {
                return fRunners;
        }

        public void addAll(List<? extends Runner> runners) {
                fRunners.addAll(runners);
        }

        public void add(Runner runner) {
                fRunners.add(runner);
        }
        
        public void filter(Filter filter) throws NoTestsRemainException {
                for (Iterator<Runner> iter= fRunners.iterator(); iter.hasNext();) {
                        Runner runner= iter.next();
                        if (filter.shouldRun(runner.getDescription()))
                                filter.apply(runner);
                        else
                                iter.remove();
                }
        }

        public void sort(final Sorter sorter) {
                Collections.sort(fRunners, new Comparator<Runner>() {
                        public int compare(Runner o1, Runner o2) {
                                return sorter.compare(o1.getDescription(), o2.getDescription());
                        }
                });
                for (Runner each : fRunners)
                        sorter.apply(each);
        }
}

