package cs361.battleships.models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RadarWeapon implements Weapon {
    public RadarWeapon() {
    }

    public Result attack(Square s, List<Ship> ships, List<Result> attacks) {
        Result attackResult = new Result(s);

        // check has ship sunk
		List<Ship> shipsAtLocation = ships.stream().filter(ship -> ship.isSunk()).collect(Collectors.toList());
		if (shipsAtLocation.size() == 0) {
			attackResult.setResult(AtackStatus.INVALID);
			return attackResult;
		}

        // check radar use times <= 2
        List<Result> radarResults = attacks.stream().filter(radar -> radar.getResult() == AtackStatus.RADAR).collect(Collectors.toList());

        if (radarResults.size() >= 2) {
			attackResult.setResult(AtackStatus.INVALID);
			return attackResult;
        }

        attackResult.setResult(AtackStatus.RADAR);
		return attackResult;
    }
}
