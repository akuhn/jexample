package jexample.internal;

import static java.lang.Character.isJavaIdentifierPart;
import static java.lang.Character.isJavaIdentifierStart;
import static java.lang.Character.isWhitespace;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Arrays;

/** Breaks a dependency declaration into method references. The class accepts the same syntax as the &#64;link tag of the Java
 * documentation tool. The references can be either fully qualified or not. If less than fully qualified, the dependency parser
 * searches specified name first in the declaring class and then the declaring package. The following table shows the different
 * forms of references.
 * <ul>
 * <li>#method</li>
 * <li>#method(Type, Type, ...)</li>
 * <li>class#method</li>
 * <li>class#method(Type, Type, ...)</li>
 * <li>package.class#method</li>
 * <li>package.class#method(Type, Type, ...)</li>
 * </ul>
 *</pre>
 *
 *Multiple references are separated by either a comma (,) or a semicolon (;).
 *<p>
 *<b>NB:</b> As listed above, the hash character (#), rather than a dot (.) separates a member from its class. However, this class is generally
 *lenient and will properly parse a dot if there is no ambiguity. This is the same as the Java documentation tool does.
 *
 * @author Adrian Kuhn, 2007-2008
 *
 */
public class DependsScanner {

	public static class Token {
		public final String path;
		public final String simple;
		public final String[] args;
		public Token(String path, String simple, String[] args) {
		    this.path = path;
			this.simple = simple;
			this.args = args;
		}
        @Override
        public String toString() {
            return String.format("%s#%s%s", path, simple,
                    args == null ? "" : Arrays.asList(args));
        }
		
	}
	
	private String scanFullname() {
        buf.mark();
        while (true) {
            if (!buf.hasRemaining()) return null;
            if (!isJavaIdentifierStart(buf.charAt(0))) return null;
            buf.get();
            while (buf.hasRemaining() && isJavaIdentifierPart(buf.charAt(0))) buf.get();
            if (!(buf.hasRemaining() && buf.charAt(0) == '.')) break;
            buf.get();
        }
        return yank();
	}

    private String scanName() {
        buf.mark();
        if (!buf.hasRemaining()) return null;
        if (!isJavaIdentifierStart(buf.charAt(0))) return null;
        buf.get();
        while (buf.hasRemaining() && isJavaIdentifierPart(buf.charAt(0))) buf.get();
        return yank();
    }
	
	private String[] scanParamlist() {
	    ArrayList<String> $ = new ArrayList();
	    while (true) {
	        String param = scanFullname();
	        if (param == null) return null;
	        $.add(param);
            skipWhitespace();
	        if (!(buf.hasRemaining() && buf.charAt(0) == ',')) break;
	        buf.get();
	        skipWhitespace();
	    }
	    return $.toArray(new String[$.size()]);
	}
	
	private Token scanMethodReference() {
	    String path, simple = null;
        String[] args = null;
        path = scanFullname();
        if (buf.hasRemaining() && buf.charAt(0) == '#') {
            buf.get();
            simple = scanName();
            if (simple == null) throw error();
        }
        else { // be lenient, Javadoc does the same!
            if (path == null) return null;
            int n = path.lastIndexOf('.');
            simple = n < 0 ? path : path.substring(n + 1);
            path = n < 0 ? null : path.substring(0, n);
        }
        skipWhitespace();
        if (buf.hasRemaining() && buf.charAt(0) == '(') {
            buf.get();
            skipWhitespace();
            args = scanParamlist();
            if (!(buf.hasRemaining() && buf.charAt(0) == ')')) throw error();
            buf.get();
            if (args == null) args = new String[0];
        }
        return new Token(path, simple, args);
	}
	
	private InvalidDeclarationError error() {
	    return new InvalidDeclarationError(buf);
    }

    private Token[] scanDeclaration() {
	    ArrayList<Token> $ = new ArrayList();
	    skipWhitespace();
	    while (true) {
	        Token t = scanMethodReference();
	        if (t == null) break;
	        $.add(t);
	        skipWhitespace();
	        if (!buf.hasRemaining()) break;
	        char ch = buf.charAt(0);
	        if (ch != ';' && ch != ',') throw error();
	        buf.get();
	        skipWhitespace();
	    }
	    if (buf.hasRemaining()) throw error();
	    return $.toArray(new Token[$.size()]);
	}

    private void skipWhitespace() {
        while (buf.hasRemaining() && isWhitespace(buf.charAt(0))) buf.get();
    }

    private String yank() {
        int pos = buf.position();
	    buf.reset();
	    String $ = buf.subSequence(0, pos - buf.position()).toString();
	    buf.position(pos);
	    return $;
    }
	
	private CharBuffer buf;
	
	private DependsScanner(String string) {
		buf = CharBuffer.wrap(string);
	}
	
	public static Token[] scan(String string) {
		DependsScanner $ = new DependsScanner(string);
		return $.scanDeclaration();
	}
	
}
