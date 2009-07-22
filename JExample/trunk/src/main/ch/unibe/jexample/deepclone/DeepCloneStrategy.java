package ch.unibe.jexample.deepclone;

public interface DeepCloneStrategy {

    public static final DeepCloneStrategy IMMUTABLE = new DeepCloneStrategy() {
        @Override
        public Object makeClone(Object original, CloneFactory delegate) throws Exception {
            return original;
        }
        @Override
        public String toString() {
        	return "IMMUTABLE";
        }
    };

    public Object makeClone(Object original, CloneFactory delegate) throws Exception;
    
}
