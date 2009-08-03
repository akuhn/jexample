package ch.unibe.jexample.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class ExampleNode {

    // TODO 1) move all graph navigation to this class
    // TODO 2) move all graph validation to this class
    // TODO 3) rewrite isolated tests for ExampleNode & Dependency 
    
    public List<Dependency> producers;
    public Collection<Example> consumers; // TODO Collection<Dependency>
    public Example value;
    
    public ExampleNode(Example example) {
        this.producers = new ArrayList<Dependency>();
        this.consumers = new HashSet<Example>();
        this.value = example;
    }
    
}
