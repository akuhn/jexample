package jexample.internal;

import static java.lang.Character.isJavaIdentifierPart;
import static java.lang.Character.isJavaIdentifierStart;
import static java.lang.Character.isWhitespace;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Arrays;

/** Breaks a dependency declaration into tokens. The grammar of dependency declarations is
 * <pre>
 *Declaration = Tokenlist
 *Tokenlist = ( Tokenlist ( ";" | "," ) ) ? Token
 *Token = ( Class "." ) ? Method ( "(" Paramlist ")" ) ?
 *Paramlist = ( Paramlist "," ) ? Param
 *Param = FULLNAME
 *Class = FULLNAME
 *Method = NAME
 *
 *NAME = [{@linkplain Character#isJavaIdentifierStart(char)}] [{@linkplain Character#isJavaIdentifierPart(char)}] *
 *FULLNAME = ( FULLNAME "." ) ? NAME
 *</pre>
 *
 * @author Adrian Kuhn
 *
 */
public class DependsScanner {

	public static class Token {
		public final String path;
		public final String simple;
		public final String[] args;
		public Token(String full, String[] args) {
		    int n = full.lastIndexOf('.');
			this.path = n < 0 ? null : full.substring(0, n);
			this.simple = n < 0 ? full : full.substring(n + 1);
			this.args = args;
		}
        @Override
        public String toString() {
            return String.format("[%s,%s,%s]", path, simple,
                    args == null ? null : Arrays.asList(args));
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
	
	private Token scanMethodHandle() {
        String[] names = null;
	    String fullname = scanFullname();
	    if (fullname == null) return null;
        skipWhitespace();
        if (buf.hasRemaining() && buf.charAt(0) == '(') {
            buf.get();
            skipWhitespace();
            names = scanParamlist();
            if (!(buf.hasRemaining() && buf.charAt(0) == ')')) throw error();
            buf.get();
            if (names == null) names = new String[0];
        }
        return new Token(fullname, names);
	}
	
	private InvalidDeclarationError error() {
	    return new InvalidDeclarationError(buf);
    }

    private Token[] scanDeclaration() {
	    ArrayList<Token> $ = new ArrayList();
	    skipWhitespace();
	    while (true) {
	        Token t = scanMethodHandle();
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
