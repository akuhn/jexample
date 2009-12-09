package ch.unibe.jexample.internal.graph;


/** Incoming dependencies of an example.
 *<P> 
 * @author Adrian Kuhn
 *
 */
public class Producers<E> extends Consumers<E> {

    @Override
    protected E value(Edge<E> edge) {
       return edge.producer.value;
    }
    
}
