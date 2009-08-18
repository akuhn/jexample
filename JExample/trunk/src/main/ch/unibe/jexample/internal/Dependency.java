package ch.unibe.jexample.internal;


public class Dependency {

    private final ExampleNode producer;
    private final Throwable broken;

    public Dependency(ExampleNode producer, ExampleNode consumer) {
        this.producer = producer;
        this.broken = null;
        producer.__consumersAdd(consumer.value);
        consumer.__producersAdd(this);
    }

    public Dependency(Throwable broken, ExampleNode consumer) {
        this.broken = broken;
        this.producer = null;
        consumer.__producersAdd(this);
    }

    public boolean isBroken() {
        return broken != null;
    }

    public Example dependency() {
        return getProducer().value;
    }
    
    public ExampleNode getProducer() {
        if (isBroken()) throw new IllegalStateException();
        return producer;
    }

    public Throwable getError() {
        return broken;
    }

    @Override
    public String toString() {
        return broken == null ? producer.toString() : broken.toString();
    }
    
}
