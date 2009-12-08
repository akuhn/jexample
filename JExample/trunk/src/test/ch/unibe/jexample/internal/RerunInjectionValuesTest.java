package ch.unibe.jexample.internal;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.unibe.jexample.Injection;
import ch.unibe.jexample.InjectionPolicy;
import ch.unibe.jexample.JExample;

@RunWith(JExample.class)
@Injection(InjectionPolicy.RERUN)
public class RerunInjectionValuesTest {

    private static List<String> list = new ArrayList<String>();

    @Test
    public void sink() {
        
    }
    
    // TODO test for rerun policy
    
}
