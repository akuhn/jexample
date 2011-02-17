package ch.unibe.jexample;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

public class ExampleGraph extends Runner {

	private Collection<Example> examples = new ArrayList<Example>();
	
	@Override
	public void run(RunNotifier notifier) {
		for (Example each: examples) each.run(notifier);
	}

	@Override
	public Description getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public Example add(Example example) {
		examples.add(example);
		return example;
	}

}
