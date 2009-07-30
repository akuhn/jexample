package ch.unibe.jexample.deepclone;

import java.util.Stack;

public class DeepCloneException extends RuntimeException {

	private static final long serialVersionUID = 167984378038815842L;

	public DeepCloneException(Throwable ex) {
		super(ex);
	}
	
	public DeepCloneException(ThreadLocal<Stack<String>> debugTraceStack, Throwable ex) {
		super(toDebugString(debugTraceStack), ex);
	}

	private static String toDebugString(ThreadLocal<Stack<String>> debugTraceStack) {
		String debug = "";
		for (String each: debugTraceStack.get()) debug += each + " in " + debug;
		return debug;
	}

}
