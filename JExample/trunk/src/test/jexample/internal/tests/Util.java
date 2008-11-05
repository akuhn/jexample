package jexample.internal.tests;

public abstract class Util {

    public static class NotCloneable {
        public final  String name;
        public NotCloneable(String name) {
            this.name = name;
        }
    }    
    
    public static class IsCloneable implements Cloneable {
        public final  String name;
        public IsCloneable(String name) {
            this.name = name;
        }
        @Override
        public Object clone() throws CloneNotSupportedException {
            return new IsCloneable("clone of " + name);
        }
    }
    
    
}
