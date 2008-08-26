package demo;

import org.junit.internal.RealSystem;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;

public class Main {

    public static void main(String[] args) {
        
        JUnitCore junit = new JUnitCore();
        junit.addListener(new TextListener(new RealSystem()));
        Request q = Request.method(ListTest.class, "empty");
        junit.run(q);
        
    }
    
}