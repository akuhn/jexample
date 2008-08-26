package jexample.internal;

import java.util.ArrayList;

import org.junit.internal.runners.InitializationError;

@SuppressWarnings("serial")
public class JExampleError extends InitializationError {

    public JExampleError() {
        super(new ArrayList());
    }
    
    public class Entry extends RuntimeException {
        public final Kind kind;
        public Entry(Kind kind, String message, Throwable cause) {
            super(message, cause);
            this.kind = kind;
        }
    }
    
    public void add(Kind kind, Throwable cause) {
        Exception $ = new Entry(kind, cause.getMessage(), cause);
        $.fillInStackTrace();
        getCauses().add($);
    }
    
    public void add(Kind kind, String message, Object... args) {
        Exception $ = new Entry(kind, String.format(message, args), null);
        $.fillInStackTrace();
        getCauses().add($);
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
        return ((Entry) getCauses().get(0)).kind;
    }

    public boolean isEmpty() {
        return getCauses().isEmpty();
    }
    
    @Override
    public String toString() {
        return "JExample error: " + getCauses();
    }
    
}
