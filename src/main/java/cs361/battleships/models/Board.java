package cs361.battleships.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Board {

	@JsonProperty private List<Ship> ships;
	@JsonProperty private List<Result> attacks;

    private Weapon weapon = new BombWeapon();

	/*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
	public Board() {
		ships = new ArrayList<>();
		attacks = new ArrayList<>();
	}

	/*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
	public boolean placeShip(Ship ship, int x, char y, boolean isVertical) {
		if (ships.size() >= 4) {
			return false;
		}
		if (ships.stream().anyMatch(s -> s.getKind().equals(ship.getKind()))) {
			return false;
		}
		//final var placedShip = new Ship(ship.getKind());
		//final Ship placedShip = new Ship(ship.getKind());
        Ship placedShip1 = null;
        if (ship.getKind().equals("MINESWEEPER")) {
            placedShip1 = new Minesweeper();
        } else if (ship.getKind().equals("DESTROYER")) {
            placedShip1 = new Destroyer();
        } else if (ship.getKind().equals("BATTLESHIP")) {
            placedShip1 = new Battleship();
        } else if (ship.getKind().equals("SUBMARINE")) {
            placedShip1 = new Submarine();
        }

        final Ship placedShip = placedShip1;

		placedShip.place(y, x, isVertical);
		if (ships.stream().anyMatch(s -> s.overlaps(placedShip))) {
			return false;
		}
		if (placedShip.getOccupiedSquares().stream().anyMatch(s -> s.isOutOfBounds())) {
			return false;
		}
		ships.add(placedShip);
		return true;
	}

	/*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
	public Result attack(int x, char y) {
		Result attackResult = attack(new Square(x, y));
		attacks.add(attackResult);
		return attackResult;
	}

    /**
     * @param w new weapon
     */
    public void setWeapon(Weapon w) {
        weapon = w;
    }

    /**
     * @param s square to attack
     * attack by different weapon
     */
    private Result attack(Square s) {
        return weapon.attack(s, ships, attacks);
    }

    public void move(int dir) {
        for (Ship ship : ships) {
            if (!ship.isSunk()) {
                if (ship.move(dir)) {
                    boolean overlap = false;
                    for (Ship other : ships) { // find overlap ship
                        if (other != ship && !other.isSunk()) {
                            if (ship.overlaps(other)) {
                                overlap = true;
                                break;
                            }
                        }
                    }

                    if (overlap) { // rollback if overlap
                        if (dir == 1) dir = 2;
                        if (dir == 2) dir = 1;
                        if (dir == 3) dir = 4;
                        if (dir == 4) dir = 3;
                        ship.move(dir);
                    }
                }
            }
        }
    }

	List<Ship> getShips() {
		return ships;
	}
}
