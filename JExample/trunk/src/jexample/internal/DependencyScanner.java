package jexample.internal;

import java.nio.CharBuffer;
import java.util.Collection;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Breaks a dependency expression into tokens. The grammar of dependency expressions is
 * <pre>
 *root = tokenlist
 *tokenlist = ( tokenlist ";" )? token
 *token = ( class "." )? method ( "(" paramlist ")" )?
 *paramlist = ( paramlist "," )? param
 *param = FULLNAME
 *class = FULLNAME
 *method = NAME
 *
 *NAME = [$A-Za-z][$A-Za-z0-9]*
 *FULLNAME = ( FULLNAME "." ) ? NAME
 *</pre>
 *
 * @author Adrian Kuhn (akuhn at iam.unibe.ch)
 *
 */
public class DependencyScanner {

	public static class Token {
		public final String className;
		public final String methodName;
		public final String[] parameterNames;
		public Token(String className, String methodName, String[] parameterNames) {
			this.className = className;
			this.methodName = methodName;
			this.parameterNames = parameterNames;
		}
		
	}
	
	private static final Pattern CLASS_NAME =
			Pattern.compile("([$A-Za-z][$A-Za-z0-9]*\\.)+");
	private static final Pattern METHOD_NAME =
			Pattern.compile("[$A-Za-z][$A-Za-z0-9]*");
	private static final Pattern PARAMETER =
			Pattern.compile("[$A-Za-z][$A-Za-z0-9]*(\\.[$A-Za-z][$A-Za-z0-9]*)*");

	private CharBuffer buf;
	private LinkedList<Token> tokens;
	
	private DependencyScanner(String string) {
		buf = CharBuffer.wrap(string);
		tokens = new LinkedList<Token>();
	}
	
	public static Token[] scan(String string) {
		DependencyScanner scanner = new DependencyScanner(string);
		scanner.scanRoot();
		return scanner.tokens.toArray(new Token[0]);
	}
	
	private void scanRoot() {
		while (buf.hasRemaining()) {
			tokens.add(scanDepedency());
		}
	}

	private Token scanDepedency() {
		String className = scanClassName();
		String methodName = scanMethodName();
		String[] parameters = scanParameters();
		if (buf.hasRemaining() && buf.charAt(0) == ';') buf.get();
		return new Token(className, methodName, parameters);
	}

	private String[] scanParameters() {
		if (buf.hasRemaining() && buf.charAt(0) == '(') {
			Collection<String> params = new LinkedList<String>();
			buf.get(); // consume '('
			while (buf.charAt(0) != ')') {
				Matcher m = PARAMETER.matcher(buf);
				if (!m.lookingAt()) 
					throw new IllegalArgumentException();
				params.add(m.group());
				buf.position(buf.position() + m.end());
				if (buf.charAt(0) == ',') buf.get();
			}
			buf.get(); // consume ')'
			return params.toArray(new String[0]);
		}
		return null;
	}

	private String scanMethodName() {
		Matcher m = METHOD_NAME.matcher(buf);
		if (!m.lookingAt())
			throw new IllegalArgumentException(buf.toString());
		String name = m.group();
		buf.position(buf.position() + m.end());
		return name; 
	}

	private String scanClassName() {
		Matcher m = CLASS_NAME.matcher(buf);
		String name = null;
		if (m.lookingAt()) {
			name = buf.subSequence(m.start(), m.end() - 1).toString();
			buf.position(buf.position() + m.end());
		}
		return name;
	}
	
}
