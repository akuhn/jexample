package ch.unibe.jexample.util;

public class MissingReference implements Reference {

    private final String text;
    
    public MissingReference(String text) {
        this.text = text;
    }

    @Override
    public boolean exists() {
        return false;
    }
    
    @Override
    public String toString() {
        return text;
    }
    
}
