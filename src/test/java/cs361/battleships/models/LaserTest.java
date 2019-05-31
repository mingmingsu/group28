package cs361.battleships.models;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;


public class LaserTest {
    private Board board;
    @Before
    public void setUp() {
        board = new Board();
    }
    @Test
    public void testPlacement() {
        Weapon laser = new LaserWeapon();
        Result result = board.attack(1, 'A');
        board.setWeapon(laser);
        assertEquals(AtackStatus.MISS, result.getResult());
    }
}
