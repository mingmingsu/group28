package cs361.battleships.models;

import java.util.List;

public interface Weapon {
    public Result attack(Square s, List<Ship> ships, List<Result> attacks);
}
