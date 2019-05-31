package controllers;

import com.google.inject.Singleton;
import cs361.battleships.models.Game;
import cs361.battleships.models.Ship;
import cs361.battleships.models.Minesweeper;
import cs361.battleships.models.Destroyer;
import cs361.battleships.models.Battleship;
import cs361.battleships.models.Submarine;
import cs361.battleships.models.Weapon;
import cs361.battleships.models.BombWeapon;
import cs361.battleships.models.RadarWeapon;
import cs361.battleships.models.LaserWeapon;
import ninja.Context;
import ninja.Result;
import ninja.Results;

@Singleton
public class ApplicationController {

    private Weapon bomb = new BombWeapon();
    private Weapon radar = new RadarWeapon();
    private Weapon laser = new LaserWeapon();

    public Result index() {
        return Results.html();
    }

    public Result newGame() {
        Game g = new Game();
        return Results.json().render(g);
    }

    public Result placeShip(Context context, PlacementGameAction g) {
        Game game = g.getGame();

        //Ship ship = new Ship(g.getShipType());
        Ship ship = null;

        String type = g.getShipType();

        if (type.equals("MINESWEEPER")) {
            ship = new Minesweeper();
        } else if (type.equals("DESTROYER")) {
            ship = new Destroyer();
        } else if (type.equals("BATTLESHIP")) {
            ship = new Battleship();
        } else if (type.equals("SUBMARINE")) {
            ship = new Submarine();
        }

        boolean result = game.placeShip(ship, g.getActionRow(), g.getActionColumn(), g.isVertical());

        if (result) {
            return Results.json().render(game);
        } else {
            return Results.badRequest();
        }
    }

    public Result attack(Context context, AttackGameAction g) {
        Game game = g.getGame();
        boolean result;
        if (g.isRadar()) { // use radar or not
            game.changeWeapon(radar);
        } else if (g.isLaser()) {
            game.changeWeapon(laser);
        } else {
            game.changeWeapon(bomb);
        }

        result = game.attack(g.getActionRow(), g.getActionColumn());

        if (result) {
            return Results.json().render(game);
        } else {
            return Results.badRequest();
        }
    }

    public Result move(Context context, MoveGameAction g) {
        Game game = g.getGame();
        boolean result;
        result = game.move(g.getMoveDir());

        if (result) {
            return Results.json().render(game);
        } else {
            return Results.badRequest();
        }
    }
}
