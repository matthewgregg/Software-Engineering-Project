package artemislite;

import java.util.Comparator;

/**
 * Comparator class to sort squares by their position
 */
public class ComparePosition implements Comparator<Square> {
    /**
     * Comparator class to sort squares by their position
     * @param s1 the first square
     * @param s2 the second square
     * @return integer representing how to sort the squares
     */
    @Override
    public int compare(Square s1, Square s2) {
        return s1.getPosition() - s2.getPosition();
    }
}
