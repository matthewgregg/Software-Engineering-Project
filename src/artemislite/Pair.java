package artemislite;

public class Pair<K, V> {
    private final K first;
    private final V second;

    public Pair(K first, V second){
        this.first = first;
        this.second = second;
    }

    /**
     * @return the first
     */
    public K getFirst() {
        return first;
    }

    /**
     * @return the second
     */
    public V getSecond() {
        return second;
    }
}
