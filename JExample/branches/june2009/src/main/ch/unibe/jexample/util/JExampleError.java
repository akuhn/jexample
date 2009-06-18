package ch.unibe.jexample.util;

import java.util.ArrayList;

import org.junit.internal.runners.InitializationError;

@SuppressWarnings("serial")
public class JExampleError extends InitializationError {

    public JExampleError() {
        super(new ArrayList<Throwable>());
    }

    public class Entry extends RuntimeException {
        public final Kind kind;

        public Entry(Kind kind, String message, Throwable cause) {
            super(message, cause);
            this.kind = kind;
        }
    }

    public void add(Kind kind, Throwable cause) {
        Exception ex = new Entry(kind, cause.getMessage(), cause);
        ex.fillInStackTrace();
        getCauses().add(ex);
    }

    public void add(Kind kind, String message, Object... args) {
        Exception ex = new Entry(kind, String.format(message, args), null);
        ex.fillInStackTrace();
        getCauses().add(ex);
    }

    public enum Kind {
        MISSING_PROVIDERS, 
        MISSING_TEST_ANNOTATION, 
        PARAMETER_NOT_ASSIGNABLE, 
        PROVIDER_EXPECTS_EXCEPTION, 
        MISSING_RUNWITH_ANNOTATION, 
        NO_EXAMPLES_FOUND, 
        MISSING_CONSTRUCTOR, 
        INVALID_DEPENDS_DECLARATION, 
        PROVIDER_NOT_FOUND,
        RECURSIVE_DEPENDENCIES,
    }

    public int size() {
        return getCauses().size();
    }

    public Kind kind() {
        assert size() == 1;
        return first().kind;
    }

    private Entry first() {
        return (Entry) getCauses().get(0);
    }

    public boolean isEmpty() {
        return getCauses().isEmpty();
    }

    @Override
    public String toString() {
        return "JExample error: " + getCauses();
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this; 
    }

    @Override
    public Throwable getCause() {
        return isEmpty() ? null : first().getCause();
    }

    
    
}
