package ch.unibe.jexample.demo;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;

public class Main {

    public static void main(String[] args) {

        JUnitCore junit = new JUnitCore();
        Request q = Request.method(ListTest.class, "withValue");
        junit.run(q);

    }

}
