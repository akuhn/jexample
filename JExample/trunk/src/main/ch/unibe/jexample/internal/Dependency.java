package ch.unibe.jexample.internal;

public class Dependency {

	private Example consumer;
    private Example producer;
    private Throwable broken;
    
    public Dependency(Example dependency, Example consumer) {
        this.producer = dependency;
        this.consumer = consumer;
        producer.addConsumer(consumer);
    }
    
    public Dependency(Throwable broken) {
        this.broken = broken;
    }
    
    public boolean isBroken() {
        return broken != null;
    }
    
    public Example dependency() {
        if (isBroken()) throw new RuntimeException(broken);
        return producer;
    }

    public Throwable getError() {
        return broken;
    }
    
    public String toString() {
    	return broken == null ? producer.toString() : broken.toString();
    }

    
}
