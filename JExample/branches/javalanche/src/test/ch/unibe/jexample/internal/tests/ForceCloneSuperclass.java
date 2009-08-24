package ch.unibe.jexample.internal.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.Result;
import org.junit.runner.RunWith;

import ch.unibe.jexample.Given;
import ch.unibe.jexample.Injection;
import ch.unibe.jexample.InjectionPolicy;
import ch.unibe.jexample.JExample;


public class ForceCloneSuperclass {

    @RunWith(JExample.class)
    @Injection(InjectionPolicy.DEEPCOPY)
    public abstract static class AbstractClonedTestCase {
        protected String name;

        protected void initName(){
            this.name = "MyName";
        }

        protected void nameNotNull(){
            assertNotNull(this.name);
        }
    }


    @RunWith(JExample.class)
    @Injection(InjectionPolicy.DEEPCOPY)
    public static class ClonedTestCase extends AbstractClonedTestCase {

        @Test
        public void setUp(){
            initName();
        }

        @Given("#setUp")
        public void testNameNotNull(){
            assertNotNull(this.name);
        }

        @Given("#setUp")
        public void testNameInSuperclass(){
            nameNotNull();
        }
    }

    @Test
    public void superClassIsCloned(){
        Result result = Util.runAllExamples(ClonedTestCase.class);
        assertEquals(0, result.getFailureCount());
    }

}
