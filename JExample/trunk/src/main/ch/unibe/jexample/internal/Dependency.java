package ch.unibe.jexample.internal;

public class Dependency {

    private Example dependency;
    private Throwable broken;
    
    public Dependency(Example dependency) {
        this.dependency = dependency;
    }
    
    public Dependency(Throwable broken) {
        this.broken = broken;
    }
    
    public boolean isBroken() {
        return broken != null;
    }
    
    public Example dependency() {
        if (isBroken()) throw new RuntimeException(broken);
        return dependency;
    }

    public Throwable getError() {
        return broken;
    }

    
}
