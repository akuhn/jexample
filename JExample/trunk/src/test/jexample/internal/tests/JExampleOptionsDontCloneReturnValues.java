package jexample.internal.tests;

import static org.junit.Assert.assertSame;
import jexample.Depends;
import jexample.JExampleOptions;
import jexample.JExample;
import jexample.internal.tests.Util.IsCloneable;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith( JExample.class )
@JExampleOptions( cloneReturnValues = false )
public class JExampleOptionsDontCloneReturnValues {

        @Test
        public IsCloneable create() {
            return new IsCloneable("root");
        }
        
        @Test
        @Depends("create")
        public IsCloneable left(IsCloneable a) {
            return a;
        }
        
        @Test
        @Depends("create")
        public IsCloneable right(IsCloneable a) {
            return a;
        }
        
        @Test
        @Depends("left;right")
        public void test(IsCloneable left, IsCloneable right) {
            assertSame( left, right );
        }
    
}
