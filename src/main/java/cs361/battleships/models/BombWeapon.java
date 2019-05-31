package cs361.battleships.models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BombWeapon implements Weapon {
    public BombWeapon() {
    }

    public Result attack(Square s, List<Ship> ships, List<Result> attacks) {
        // has ship in square or not
		List<Ship> shipsAtLocation = ships.stream().filter(ship -> ship.isAtLocation(s)).collect(Collectors.toList());
		if (shipsAtLocation.size() == 0) {
			Result attackResult = new Result(s);
			return attackResult;
		}

        // find a ship
		Ship hitShip = shipsAtLocation.get(0);

        // do ship attack
		Result attackResult = hitShip.attack(s.getRow(), s.getColumn());
		if (attackResult.getResult() == AtackStatus.SUNK) {
			if (ships.stream().allMatch(ship -> ship.isSunk())) {
				attackResult.setResult(AtackStatus.SURRENDER);
			}
		}

		return attackResult;
    }
}
