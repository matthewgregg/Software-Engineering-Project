package artemislite;

public class Triplet<A, B, C> {
    private final A first;
    private final B second;
    private final C third;

    /**
     * Custom tuple to allow 3 different objects to be returned
     * @param first the first object
     * @param second the second object
     * @param third the third object
     */
    public Triplet(A first, B second, C third){
        this.first = first;
        this.second = second;
        this.third = third;
    }

    /**
     * @return the first
     */
    public A getFirst() {
        return first;
    }

    /**
     * @return the second
     */
    public B getSecond() {
        return second;
    }

    /**
     * @return the third
     */
    public C getThird() {
        return third;
    }
}
