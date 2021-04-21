package artemislite;

import java.util.Comparator;

/**
 * Comparator class to sort winners at the end of the game
 */
public class CompareWinners implements Comparator<Player> {
    /**
     * Comparator class to sort winners at the end of the game
     * @param p1 the first player
     * @param p2 the second player
     * @return integer representing how to sort the players
     */
    @Override
    public int compare(Player p1, Player p2) {
        int p1worth = Game.calculateNetWorth(p1);
        int p2worth = Game.calculateNetWorth(p2);
        // sort by combined cash and property value
        if (p1worth - p2worth != 0) {
            return Game.calculateNetWorth(p1) - Game.calculateNetWorth(p2);
        } else {
            // if tied, sort by property value
            return (p1worth - p1.getPlayerResources()) - (p2worth - p2.getPlayerResources());
        }
    }
}
