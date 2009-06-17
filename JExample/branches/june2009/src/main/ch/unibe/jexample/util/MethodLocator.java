/**
 * 
 */
package ch.unibe.jexample.util;

import static java.lang.Character.isJavaIdentifierPart;
import static java.lang.Character.isJavaIdentifierStart;
import static java.lang.Character.isWhitespace;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Breaks a dependency declaration into method references. The class accepts the
 * same syntax as the &#64;link tag of the Java documentation tool. The
 * references can be either fully qualified or not. If less than fully
 * qualified, the dependency parser searches specified name first in the
 * declaring class and then the declaring package. The following table shows the
 * different forms of references.
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
 * <b>NB:</b> As listed above, the hash character (#), rather than a dot (.)
 * separates a member from its class. However, this class is generally lenient
 * and will properly parse a dot if there is no ambiguity. This is the same as
 * the Java documentation tool does.
 * 
 * @author Adrian Kuhn
 * 
 */
@SuppressWarnings("unchecked")
public class MethodLocator {

    public final String path;

    public final String simple;

    public final String[] args;

    public MethodLocator(String path, String simple, String[] args) {
        this.path = path;
        this.simple = simple;
        this.args = args;
    }

    @Override
    public String toString() {
        return String.format("%s#%s%s", path, simple, args == null ? "" : Arrays.asList(args));
    }

    public static class Resolver {

        private final Class base;

        private Resolver(Class base) {
            this.base = base;
        }

        private MethodReference findMethodReference(MethodLocator token) throws ClassNotFoundException,
                SecurityException, NoSuchMethodException {
            Class providerClass = findClass(token);
            return findMethodReference(providerClass, token);
        }

        private Class findClass(MethodLocator token) throws ClassNotFoundException {
            if (token.path == null) return base;
            String name = token.path;
            Class $ = classForName(name);
            if ($ != null) return $;
            name = base.getName() + "$" + token.path;
            $ = classForName(name);
            if ($ != null) return $;
            name = base.getPackage().getName() + "." + token.path;
            $ = classForName(name);
            if ($ != null) return $;
            throw new ClassNotFoundException(token.path);
        }

        private Class classForName(String fullname) {
            try {
                return Class.forName(fullname);
            } catch (ClassNotFoundException _) {
                return null;
            }
        }

        private MethodReference findMethodReference(Class receiver, MethodLocator token) throws ClassNotFoundException,
                SecurityException, NoSuchMethodException {
            if (token.args == null) {
                MethodReference found = null;
                for (MethodReference m: MethodReference.all(receiver)) {
                    if (m.equals(receiver, token.simple)) {
                        if (found != null) throw new NoSuchMethodException(
                                "Ambigous depedency, please specify parameters: " + receiver.getName() + "."
                                        + m.getName());
                        found = m;
                    }
                }
                if (found == null) throw new NoSuchMethodException(token.toString());
                return found;
            } else {
                return new MethodReference(receiver, receiver.getMethod(token.simple, this
                        .getParameterClasses(token.args)));
            }
        }

        private Class[] getParameterClasses(String[] parameters) throws ClassNotFoundException {
            ArrayList<Class> $ = new ArrayList();
            for (String name: parameters) {
                Class c = classForName(name);
                if (c == null) {
                    c = classForName("java.lang." + name);
                    if (c == null) {
                        if (name.equals("int")) {
                            c = int.class;
                        } else if (name.equals("long")) {
                            c = long.class;
                        } else if (name.equals("double")) {
                            c = double.class;
                        } else if (name.equals("float")) {
                            c = float.class;
                        } else if (name.equals("char")) {
                            c = char.class;
                        } else if (name.equals("boolean")) {
                            c = boolean.class;
                        } else {
                            throw new ClassNotFoundException(name);
                        }
                    }
                }
                $.add(c);
            }
            return $.toArray(new Class[$.size()]);
        }

    }

    public static class Parser {

        private String scanFullname() {
            buf.mark();
            while (true) {
                if (!buf.hasRemaining()) return null;
                if (!isJavaIdentifierStart(buf.charAt(0))) return null;
                buf.get();
                while (buf.hasRemaining() && isJavaIdentifierPart(buf.charAt(0)))
                    buf.get();
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
            while (buf.hasRemaining() && isJavaIdentifierPart(buf.charAt(0)))
                buf.get();
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

        private MethodLocator scanMethodReference() {
            String path, simple = null;
            String[] args = null;
            path = scanFullname();
            if (buf.hasRemaining() && buf.charAt(0) == '#') {
                buf.get();
                simple = scanName();
                if (simple == null) throw error();
            } else { // be lenient, Javadoc does the same!
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
            return new MethodLocator(path, simple, args);
        }

        private InvalidDeclarationError error() {
            return new InvalidDeclarationError(buf);
        }

        private Iterable<MethodLocator> scanDeclaration() {
            List<MethodLocator> $ = new ArrayList();
            skipWhitespace();
            while (true) {
                MethodLocator t = scanMethodReference();
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
            return $;
        }

        private void skipWhitespace() {
            while (buf.hasRemaining() && isWhitespace(buf.charAt(0)))
                buf.get();
        }

        private String yank() {
            int pos = buf.position();
            buf.reset();
            // FIX due to a bug in Java 1.6.0_11 CharBuffer#subSequence is broken.       
            String $ = buf.toString().substring(0, pos - buf.position());
            buf.position(pos);
            return $;
        }

        private CharBuffer buf;

        private Parser(String string) {
            buf = CharBuffer.wrap(string);
        }

    }

    public static MethodLocator parse(String string) {
        Parser p = new Parser(string);
        MethodLocator m = p.scanMethodReference();
        if (p.buf.hasRemaining()) throw p.error();
        return m;
    }

    public static Iterable<MethodLocator> parseAll(String string) {
        Parser p = new Parser(string);
        return p.scanDeclaration();
    }

    public MethodReference resolve() throws SecurityException, ClassNotFoundException, NoSuchMethodException {
        return resolve(Object.class);
    }

    public MethodReference resolve(Class context) throws SecurityException, ClassNotFoundException,
            NoSuchMethodException {
        return new Resolver(context).findMethodReference(this);
    }

}