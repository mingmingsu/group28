package cs361.battleships.models;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BoardTest {

    private Board board;

    @Before
    public void setUp() {
        board = new Board();
    }

    @Test
    public void testInvalidPlacement() {
        assertFalse(board.placeShip(new Ship("MINESWEEPER"), 11, 'C', true));
    }

    @Test
    public void testPlaceMinesweeper() {
        assertTrue(board.placeShip(new Ship("MINESWEEPER"), 1, 'A', true));
    }

    @Test
    public void testAttackEmptySquare() {
        board.placeShip(new Ship("MINESWEEPER"), 1, 'A', true);
        Result result = board.attack(2, 'E');
        assertEquals(AtackStatus.MISS, result.getResult());
    }

    @Test
    public void testAttackShip() {
        Ship minesweeper = new Ship("MINESWEEPER");
        board.placeShip(minesweeper, 1, 'A', true);
        minesweeper = board.getShips().get(0);
        Result result = board.attack(1, 'A');
        //assertEquals(AtackStatus.HIT, result.getResult());
        assertEquals(AtackStatus.SURRENDER, result.getResult());
        assertEquals(minesweeper, result.getShip());
    }

    @Test
    public void testAttackSameSquareMultipleTimes() {
        Ship minesweeper = new Ship("MINESWEEPER");
        board.placeShip(minesweeper, 1, 'A', true);
        Result result = board.attack(1, 'A');
        assertEquals(AtackStatus.SURRENDER, result.getResult());
        result = board.attack(1, 'A');
        assertEquals(AtackStatus.INVALID, result.getResult());
    }

    @Test
    public void testAttackSameEmptySquareMultipleTimes() {
        Result initialResult = board.attack(1, 'A');
        assertEquals(AtackStatus.MISS, initialResult.getResult());
        Result result = board.attack(1, 'A');
        //assertEquals(AtackStatus.INVALID, result.getResult());
        assertEquals(AtackStatus.MISS, result.getResult());
    }

    @Test
    public void testSurrender() {
        board.placeShip(new Ship("MINESWEEPER"), 1, 'A', true);
        Result result = board.attack(1, 'A');
        assertEquals(AtackStatus.SURRENDER, result.getResult());
        //var result = board.attack(2, 'A');
        result = board.attack(2, 'A');
        assertEquals(AtackStatus.SURRENDER, result.getResult());
    }

    @Test
    public void testPlaceMultipleShipsOfSameType() {
        assertTrue(board.placeShip(new Ship("MINESWEEPER"), 1, 'A', true));
        assertFalse(board.placeShip(new Ship("MINESWEEPER"), 5, 'D', true));

    }

    @Test
    public void testCantPlaceMoreThan3Ships() {
        assertTrue(board.placeShip(new Ship("MINESWEEPER"), 1, 'A', true));
        assertTrue(board.placeShip(new Ship("BATTLESHIP"), 5, 'D', true));
        assertTrue(board.placeShip(new Ship("DESTROYER"), 6, 'A', false));
        assertTrue(board.placeShip(new Submarine(), 8, 'E', false));
        assertFalse(board.placeShip(new Ship(""), 9, 'A', false));

    }

    @Test
    public void testQuaterAttack() {
        Ship destroyer = new Ship("DESTROYER");
        board.placeShip(destroyer, 1, 'A', true);

        assertTrue(board.placeShip(new Ship("MINESWEEPER"), 1, 'B', true));

        Result result = board.attack(1, 'A');
        assertEquals(AtackStatus.HIT, result.getResult());

        result = board.attack(2, 'A');
        assertEquals(AtackStatus.MISS, result.getResult());

        result = board.attack(2, 'A');
        assertEquals(AtackStatus.SUNK, result.getResult());

        result = board.attack(3, 'A');
        assertEquals(AtackStatus.SUNK, result.getResult());
    }

    @Test
    public void testRadarAttack() {
        Ship battleship = new Ship("BATTLESHIP");
        Weapon bomb = new BombWeapon();
        Weapon radar = new RadarWeapon();

        board.placeShip(battleship, 1, 'A', true);

        board.setWeapon(radar);
        Result result = board.attack(1, 'A');
        assertEquals(AtackStatus.INVALID, result.getResult());

        board.setWeapon(bomb);
        result = board.attack(1, 'A');
        result = board.attack(2, 'A');
        result = board.attack(3, 'A');
        result = board.attack(3, 'A');
        result = board.attack(4, 'A');
        assertEquals(AtackStatus.SURRENDER, result.getResult());

        board.setWeapon(radar);
        result = board.attack(4, 'A');
        assertEquals(AtackStatus.RADAR, result.getResult());

        result = board.attack(5, 'A');
        assertEquals(AtackStatus.RADAR, result.getResult());

        result = board.attack(5, 'A');
        assertEquals(AtackStatus.INVALID, result.getResult());
    }

    @Test
    public void testMove() {
        Ship battleship = new Ship("BATTLESHIP");

        assertTrue(board.placeShip(battleship, 2, 'A', true));

        board.move(1);

        List<Square> occupiedSquares = board.getShips().get(0).getOccupiedSquares();

        ArrayList<Object> expected = new ArrayList<>();
        expected.add(new Square(1, 'A'));
        expected.add(new Square(2, 'A'));
        expected.add(new Square(3, 'A'));
        expected.add(new Square(4, 'A'));

        assertEquals(expected, occupiedSquares);
    }

    @Test
    public void testPlaceSubmarine() {

        Ship submarine = new Submarine();
        board.placeShip(submarine, 1, 'A', true);

        List<Square> occupiedSquares1 = board.getShips().get(0).getOccupiedSquares();
        ArrayList<Object> expected = new ArrayList<>();
        expected.add(new Square(1, 'A'));
        expected.add(new Square(2, 'A'));
        expected.add(new Square(3, 'A'));
        expected.add(new Square(4, 'A'));
        expected.add(new Square(3, 'B'));

        assertEquals(expected, occupiedSquares1);
    }
}
