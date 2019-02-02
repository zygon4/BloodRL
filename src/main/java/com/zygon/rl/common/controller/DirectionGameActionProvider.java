/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.common.controller;

import com.badlogic.gdx.Gdx;
import com.zygon.rl.common.model.Entities;
import com.zygon.rl.core.model.Action;
import com.zygon.rl.core.model.Direction;
import com.zygon.rl.core.model.Entity;
import com.zygon.rl.core.model.Game;
import com.zygon.rl.core.model.Location;
import com.zygon.rl.core.model.Regions;

import java.util.function.BiFunction;

/**
 *
 * @author zygon
 */
public class DirectionGameActionProvider implements BiFunction<Action, Game, Game> {

    @Override
    public Game apply(Action a, Game g) {
        Game newGame = g;

        switch (a.getName()) {
            case "CANCEL":
                newGame = newGame.copy()
                        .removeContext()
                        .addLog("Nevermind")
                        .build();
                // TODO: remove gdx from

                Gdx.app.log("action", "examine: nevermind");

                break;
            case "DIRECTION":
                Regions regions = newGame.getRegions();
                Location playerLoc = regions.find(Entities.PLAYER).stream().findAny().get();

                int nextX = playerLoc.getX();
                int nextY = playerLoc.getY();
                int nextZ = playerLoc.getZ();

                Direction dir = Direction.valueOf(a.getValue());

                switch (dir) {
                    case WEST:
                        nextX--;
                        break;
                    case EAST:
                        nextX++;
                        break;
                    case NORTH:
                        nextY--;
                        break;
                    case SOUTH:
                        nextY++;
                        break;
                }

                Location destination = Location.create(nextX, nextY, nextZ);

                newGame = handle(newGame, destination);

                // TODO: update context/game menu to show the information.
                newGame = newGame.copy()
                        .removeContext()
                        .build();
        }

        return newGame;
    }

    /**
     * Override for your case, don't remove the context, that'll be taken care
     * of.
     *
     * @param game
     * @param destination
     * @return
     */
    protected Game handle(Game game, Location destination) {
        Regions regions = game.getRegions();
        // TODO: find the most viewblocking thing
        Entity entity = regions.get(destination).stream().findAny().orElse(null);

        // TODO: remove gdx from
        Gdx.app.log("action", "examine: " + entity.getDisplayName());

        return game;
    }
}
