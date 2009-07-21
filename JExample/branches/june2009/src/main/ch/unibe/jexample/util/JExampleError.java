package ch.unibe.jexample.util;

import java.util.ArrayList;

import org.junit.internal.runners.InitializationError;

@SuppressWarnings("serial")
public class JExampleError extends InitializationError {

	public JExampleError() {
		super(new ArrayList<Throwable>());
	}

	public void add(Kind kind, String message, Object... args) {
		Exception ex = new Entry(kind, String.format(message, args), null);
		ex.fillInStackTrace();
		getCauses().add(ex);
	}

	public void add(Kind kind, Throwable cause) {
		Exception ex = new Entry(kind, cause.getMessage(), cause);
		ex.fillInStackTrace();
		getCauses().add(ex);
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		return this; 
	}

	private Entry first() {
		return (Entry) getCauses().get(0);
	}

	@Override
	public Throwable getCause() {
		return isEmpty() ? null : first().getCause();
	}

	public Kind getKind() {
		assert size() == 1;
		return first().kind;
	}

	public boolean isEmpty() {
		return getCauses().isEmpty();
	}

	public int size() {
		return getCauses().size();
	}

	
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("JExample, could not initialize example.\n");
		for (Throwable entry : getCauses()) {
			buf.append(entry).append('\n');
		}
		return buf.toString();
	}

	public class Entry extends RuntimeException {
		public final Kind kind;

		public Entry(Kind kind, String message, Throwable cause) {
			super(message, cause);
			this.kind = kind;
		}

		@Override
		public String toString() {
			return String.format("%s: %s", kind, getMessage());
		}
		
		
	}

	public enum Kind {
		INVALID_DEPENDS_DECLARATION, 
		MISSING_CONSTRUCTOR, 
		MISSING_PROVIDERS, 
		MISSING_RUNWITH_ANNOTATION, 
		MISSING_TEST_ANNOTATION, 
		NO_EXAMPLES_FOUND, 
		PARAMETER_NOT_ASSIGNABLE, 
		PROVIDER_EXPECTS_EXCEPTION, 
		NO_SUCH_PROVIDER,
		RECURSIVE_DEPENDENCIES,
	}



}
