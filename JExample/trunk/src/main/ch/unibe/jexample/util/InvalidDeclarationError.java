package ch.unibe.jexample.util;

import java.nio.CharBuffer;

@SuppressWarnings("serial")
public class InvalidDeclarationError extends RuntimeException {

    private static String message(CharBuffer buf) {
        String message = buf.hasRemaining() ? "Illegal charater at %d." : "Unexpected end of declaration.";
        return String.format(message, buf.position());
    }
    public final int position;

    public final String string;

    public InvalidDeclarationError(CharBuffer buf) {
        super(message(buf));
        this.position = buf.position();
        this.string = buf.rewind().toString();
    }

}
