package cs361.battleships.models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LaserWeapon implements Weapon {
    public LaserWeapon() {
    }

    public Result attack(Square s, List<Ship> ships, List<Result> attacks) {
        Result attackResult = new Result(s);

        // check has ship sunk
		List<Ship> shipsAtLocation = ships.stream().filter(ship -> ship.isSunk()).collect(Collectors.toList());
		if (shipsAtLocation.size() == 0) {
			attackResult.setResult(AtackStatus.INVALID);
			return attackResult;
		}

        // has ship in square or not
		shipsAtLocation = ships.stream().filter(ship -> ship.isAtLocation(s)).collect(Collectors.toList());
		if (shipsAtLocation.size() == 0) { // miss
			attackResult = new Result(s);
			return attackResult;
		}

        boolean surrender = false;
        boolean sunk = false;

        // find all the ship
        for (Ship hitShip : shipsAtLocation) {
            // do ship attack
            attackResult = hitShip.attack(s.getRow(), s.getColumn());
            if (attackResult.getResult() == AtackStatus.SUNK) {
                if (ships.stream().allMatch(ship -> ship.isSunk())) {
                    attackResult.setResult(AtackStatus.SURRENDER);
                    surrender = true;
                }
                sunk = true;
            }
        }

        if (surrender) {
            attackResult.setResult(AtackStatus.SURRENDER);
        } else if (sunk) {
            attackResult.setResult(AtackStatus.SUNK);
        }

		return attackResult;
    }
}
