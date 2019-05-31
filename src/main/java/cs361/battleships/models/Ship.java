package cs361.battleships.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;
import com.mchange.v1.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

public class Ship {

	@JsonProperty protected String kind;
	@JsonProperty protected List<Square> occupiedSquares;
	@JsonProperty protected int size;

	public Ship() {
		occupiedSquares = new ArrayList<>();
	}
	
	public Ship(String kind) {
		this();
		this.kind = kind;
		switch(kind) {
			case "MINESWEEPER":
				size = 2;
				break;
			case "DESTROYER":
				size = 3;
				break;
			case "BATTLESHIP":
				size = 4;
				break;
            case "SUBMARINE":
                size = 5;
                break;
		}
	}

	public List<Square> getOccupiedSquares() {
		return occupiedSquares;
	}

	public void place(char col, int row, boolean isVertical) {
		for (int i=0; i<size; i++) {
			if (isVertical) {
                if (size - i == 2) {
                    Square s = new CaptainQuarter(row+i, col);
                    if (this.kind.equals("MINESWEEPER"))
                        s.setMaxHits(1);
                    occupiedSquares.add(s);
                } else {
                    occupiedSquares.add(new Square(row+i, col));
                }
			} else {
                if (size - i == 2) {
                    Square s = new CaptainQuarter(row,(char)(col+i));
                    if (this.kind.equals("MINESWEEPER"))
                        s.setMaxHits(1);
                    occupiedSquares.add(s);
                } else {
                    occupiedSquares.add(new Square(row, (char) (col + i)));
                }
			}
		}
	}

	public boolean overlaps(Ship other) {
		//Set<Square> thisSquares = Set.copyOf(getOccupiedSquares());
		//Set<Square> otherSquares = Set.copyOf(other.getOccupiedSquares());
		//Set<Square> thisSquares = getOccupiedSquares().stream().collect(Collectors.toSet());
		//Set<Square> otherSquares = other.getOccupiedSquares().stream().collect(Collectors.toSet());

        // submarine can be underwater
        if (other.getKind().equals("SUBMARINE") &&
                !this.getKind().equals("SUBMARINE"))
            return false;

        if (this.getKind().equals("SUBMARINE") &&
                !other.getKind().equals("SUBMARINE"))
            return false;

		Set<Square> thisSquares = new HashSet<Square>(getOccupiedSquares());
		Set<Square> otherSquares = new HashSet<Square>(other.getOccupiedSquares());

		Sets.SetView<Square> intersection = Sets.intersection(thisSquares, otherSquares);
		return intersection.size() != 0;
	}

	public boolean isAtLocation(Square location) {
		return getOccupiedSquares().stream().anyMatch(s -> s.equals(location));
	}

	public String getKind() {
		return kind;
	}


	public Result attack(int x, char y) {
		//var attackedLocation = new Square(x, y);
		Square attackedLocation = new Square(x, y);
		//var square = getOccupiedSquares().stream().filter(s -> s.equals(attackedLocation)).findFirst();
		Optional<Square> square = getOccupiedSquares().stream().filter(s -> s.equals(attackedLocation)).findFirst();
		if (!square.isPresent()) {
			return new Result(attackedLocation);
		}
		//var attackedSquare = square.get();
		Square attackedSquare = square.get();
		if (attackedSquare.isHit()) {
			//var result = new Result(attackedLocation);
			Result result = new Result(attackedLocation);
			result.setResult(AtackStatus.INVALID);
			return result;
		}
		attackedSquare.hit();
		//var result = new Result(attackedLocation);
		Result result = new Result(attackedLocation);
		result.setShip(this);
		if (isSunk()) {
			result.setResult(AtackStatus.SUNK);
		} else {
            if (attackedSquare.isHit()) {
                result.setResult(AtackStatus.HIT);
                if (attackedSquare.isQuarter()) // quarter is attacked 
                    result.setResult(AtackStatus.SUNK);
            } else { // captain's first hit
                result.setResult(AtackStatus.MISS);
            }
		}
		return result;
	}

	@JsonIgnore
	public boolean isSunk() {
		return getOccupiedSquares().stream().allMatch(s -> s.isHit())
            || getOccupiedSquares().stream().anyMatch(s -> s.isQuarter() && s.isHit());
	}

    public boolean move(int dir) {
        if (getOccupiedSquares().stream().allMatch(s -> s.canMove(dir))) {
            for (Square s : getOccupiedSquares()) {
                s.move(dir);
            }
            return true; // moved ok
        }
        return false;
    }

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Ship)) {
			return false;
		}
		//var otherShip = (Ship) other;
		Ship otherShip = (Ship) other;

		return this.kind.equals(otherShip.kind)
				&& this.size == otherShip.size
				&& this.occupiedSquares.equals(otherShip.occupiedSquares);
	}

	@Override
	public int hashCode() {
		return 33 * kind.hashCode() + 23 * size + 17 * occupiedSquares.hashCode();
	}

	@Override
	public String toString() {
		return kind + occupiedSquares.toString();
	}
}
