package jexample.internal;

@SuppressWarnings("serial")
public class InvalidExampleError extends RuntimeException {

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

    public final Kind kind;
    
    public InvalidExampleError(Kind kind, Throwable cause, String format, Object... args) {
        super(String.format(format, args), cause);
        this.kind = kind;
    }
    
    public InvalidExampleError(Kind kind, String format, Object... args) {
        this(kind, null, format, args);
    }

}
