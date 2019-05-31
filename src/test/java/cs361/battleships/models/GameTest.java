package cs361.battleships.models;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;
public class GameTest {
    @Test
    public void testPlacement() {
        Ship ship = new Minesweeper();
        Game game = new Game();
        assertTrue(game.placeShip(ship,1,'A',true));
        assertFalse(game.placeShip(ship,1,'W',true));
    }

    @Test
    public void testAttack() {
        Game game = new Game();
        assertTrue(game.attack(3,'D'));
        assertTrue(game.attack(1,'A'));
    }
    @Test
    public void testMove() {
        Ship minesweeper = new Ship("MINESWEEPER");
        assertTrue(minesweeper.move(2));
    }

}


