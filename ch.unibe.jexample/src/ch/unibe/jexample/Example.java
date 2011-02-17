package ch.unibe.jexample;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

public class Example extends Runner {

	static class Result {
		Throwable failure;
		static Result PENDING = new Result();
		static Result SUCCESS = new Result();
		static Result IGNORE = new Result();
	}
	
	private boolean whenRunThenFail = false;
	private Example[] producers;
	private Result result = Result.PENDING;

	public Example(Example... producers) {
		this.producers = producers;
	}
	
	@Override
	public Description getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void run(RunNotifier notifier) {
		notifier.fireTestStarted(getDescription());
		Result result = innerRun();
		if (Result.IGNORE == result) notifier.fireTestIgnored(getDescription());
		if (result.failure != null) notifier.fireTestFailure(null);
		notifier.fireTestFinished(getDescription());
	}
	
	private Result innerRun() {
		if (Result.PENDING != result) return result;
		for (Example each: producers) {
			if (Result.SUCCESS != each.innerRun()) return result = Result.IGNORE;
		}
		try {
			innerMostRun();
		}
		catch (Throwable e) {
			result = new Result();
			result.failure = e;
			return result;
		}
		return result = Result.SUCCESS;
	}
	
	private void innerMostRun() {
		if (whenRunThenFail) throw new AssertionError();
	}

	public void whenRunThenFail() {
		whenRunThenFail  = true;
	}

	public boolean hasBeenRun() {
		return Result.SUCCESS == result || result.failure != null;
	}

}
