/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.common.controller;

import com.badlogic.gdx.Input;
import com.zygon.rl.common.model.CommonAttributes;
import com.zygon.rl.common.model.Entities;
import com.zygon.rl.common.model.Openable;
import com.zygon.rl.common.model.RegionHelper;
import com.zygon.rl.common.system.SimpleAI;
import com.zygon.rl.context.gdx.GDXInputAdapter;
import com.zygon.rl.core.model.Action;
import com.zygon.rl.core.model.Context;
import com.zygon.rl.core.model.Direction;
import com.zygon.rl.core.model.Entity;
import com.zygon.rl.core.model.Game;
import com.zygon.rl.core.model.Location;
import com.zygon.rl.core.model.Region;
import com.zygon.rl.core.model.Regions;
import com.zygon.rl.core.system.GameSystem;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

/**
 *
 * @author zygon
 */
public class OuterworldGameActionProvider implements BiFunction<Action, Game, Game> {

    private static final String WHICH_DIRECTION = "which direction?";

    private final Set<Integer> gameInputs = new HashSet<>();
    private final GDXInputAdapter directionInputAdapter;
    private final GameSystem simpleAi = new SimpleAI();

    {
        //TODO: Again, can't have gdx here

        // escape/quit
        gameInputs.add(Input.Keys.Q);
        // movement
        gameInputs.add(Input.Keys.H);
        gameInputs.add(Input.Keys.J);
        gameInputs.add(Input.Keys.K);
        gameInputs.add(Input.Keys.L);

        gameInputs.add(Input.Keys.NUMPAD_7);
        gameInputs.add(Input.Keys.NUMPAD_8);
        gameInputs.add(Input.Keys.NUMPAD_9);
        gameInputs.add(Input.Keys.NUMPAD_4);
        // 5??
        gameInputs.add(Input.Keys.NUMPAD_6);
        gameInputs.add(Input.Keys.NUMPAD_1);
        gameInputs.add(Input.Keys.NUMPAD_2);
        gameInputs.add(Input.Keys.NUMPAD_3);
        directionInputAdapter = new GDXInputAdapter(gameInputs);
    }

    @Override
    public Game apply(Action a, Game g) {
        Game newGame = g;

        switch (a.getName()) {
            case "QUIT":
                System.exit(0);
            case "CLOSE":
                Context closeCtx = Context.builder()
                        .setActionProvider(new DirectionActionProvider())
                        .setGameActionProvider(new CloseDirectionGameActionProvider())
                        .setInputSupplier(directionInputAdapter)
                        .setName("close")
                        .setDisplayName("Close " + WHICH_DIRECTION)
                        .build();
                newGame = newGame.copy()
                        .moveTime(6, TimeUnit.SECONDS)
                        .addContext(closeCtx)
                        .build();
                break;
            case "OPEN":
                Context openCtx = Context.builder()
                        .setActionProvider(new DirectionActionProvider())
                        .setGameActionProvider(new OpenDirectionGameActionProvider())
                        .setInputSupplier(directionInputAdapter)
                        .setName("open")
                        .setDisplayName("Open " + WHICH_DIRECTION)
                        .build();
                newGame = newGame.copy()
                        .moveTime(6, TimeUnit.SECONDS)
                        .addContext(openCtx)
                        .build();
                // TODO: need to set a text window
                break;
            case "MOVE":
                Regions regions = g.getRegions();
                //
                // TBD: bump to attack
                //
                regions = movePlayer(a, regions);
                newGame = newGame.copy()
                        .moveTime(6, TimeUnit.SECONDS)
                        .setRegions(regions)
                        .build();
                break;
            case "EXAMINE":
                Context examineCtx = Context.builder()
                        .setActionProvider(new DirectionActionProvider())
                        .setGameActionProvider(new DirectionGameActionProvider())
                        .setInputSupplier(directionInputAdapter)
                        .setName("examine")
                        .setDisplayName("Examine " + WHICH_DIRECTION)
                        .build();
                newGame = newGame.copy()
                        .moveTime(6, TimeUnit.SECONDS)
                        .addContext(examineCtx)
                        .build();
                break;
            // TODO:
            //  tons of other stuff
        }

        // TBD: where should the systems be plugged in????
        newGame = simpleAi.getGameUpdate().apply(newGame);

        return newGame;
    }

    private boolean canMove(Location destination, Regions regions) {

        Region region = regions.getRegion(destination);

        // TODO: This is a workaround for borders until they auto-magically grow at runtime.
        if (region == null) {
            return false;
        }

        // TODO: needs detailed terrain/heights to block entities
        Set<Entity> entities = region.get(destination);
        if (entities.stream()
                .filter(e -> !e.getAttributes(CommonAttributes.IMPASSABLE.name()).isEmpty())
                .findAny().isPresent()) {
            return false;
        }

        return true;
    }

    // TODO: convert to "system"
    // TODO: target tracking. Random walk for now.
//    private Region moveAllMonsters(Region r) {
//        Set<Location> monsterLocations = r.find(Entities.MONSTER.getName());
//
//        for (Location monsterLocation : monsterLocations) {
//            boolean moved = false;
//
//            // TBD: moving all monsters exposes lag, which is probably a good thing, but we don't need
//            // to ALWAYS move
//            List<Location> shuffledNeighbors = new ArrayList<>(monsterLocation.getNeighbors().stream()
//                    .collect(Collectors.collectingAndThen(Collectors.toList(), collected -> {
//                        Collections.shuffle(collected);
//                        return collected.stream();
//                    }))
//                    .collect(Collectors.toList()));
//
//            for (Location monsterNeighbor : shuffledNeighbors) {
//                if (!moved) {
//                    if (canMove(monsterNeighbor, r)) {
//                        Entity monster = r.get(monsterLocation).stream()
//                                .filter(e -> e.getName().equals("MONSTER"))
//                                .findAny().orElse(null);
//
//                        r = r.move(monster, monsterNeighbor);
//                        moved = true;
//                    }
//                }
//            }
//        }
//
//        return r;
//    }
//
    private Regions movePlayer(Action action, Regions regions) {
        Location playerLoc = regions.find(Entities.PLAYER).stream()
                .findAny().get();
        int nextX = playerLoc.getX();
        int nextY = playerLoc.getY();
        int nextZ = playerLoc.getZ();

        Direction dir = Direction.valueOf(action.getValue());

        switch (dir) {
            case NORTHEAST:
                nextX++;
                nextY++;
                break;
            case NORTHWEST:
                nextX--;
                nextY++;
                break;
            case SOUTHEAST:
                nextX++;
                nextY--;
                break;
            case SOUTHWEST:
                nextX--;
                nextY--;
                break;
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

        // Could become more robust "getActionsAtDestination"
        if (canMove(destination, regions)) {
            regions = regions.move(Entities.PLAYER, destination);

            // Will probably need to increase this
            int growRegionTolerance = Regions.REGION_EDGE_SIZE * 2;

            // Logic to grow the region space
            if (regions.getMaxValues().getX() - destination.getX() <= growRegionTolerance - 1) {
                regions = grow(regions, Direction.EAST);
            }

            if (regions.getMaxValues().getY() - destination.getY() <= growRegionTolerance - 1) {
                regions = grow(regions, Direction.NORTH);
            }

            if (destination.getX() - regions.getMinValues().getX() <= growRegionTolerance - 1) {
                regions = grow(regions, Direction.WEST);
            }

            if (destination.getY() - regions.getMinValues().getY() <= growRegionTolerance - 1) {
                regions = grow(regions, Direction.SOUTH);
            }
        }

        return regions;
    }

    private static Regions grow(final Regions regions, Direction direction) {
        Regions newRegions = regions;
        int numberOfRegions = calcSideRegions(newRegions, direction);

        switch (direction) {
            case NORTH: {
                int newLocationY = newRegions.getMaxValues().getY() + 1;
                int startingX = newRegions.getMinValues().getX();

                for (int i = 0; i < numberOfRegions; i++) {
                    if (i > 0) {
                        startingX += Regions.REGION_EDGE_SIZE;
                    }

                    Location loc = Location.create(startingX, newLocationY);
                    Region newRegion = RegionHelper.generateCity(loc,
                            Regions.REGION_EDGE_SIZE, Regions.REGION_EDGE_SIZE);
                    newRegions = newRegions.add(newRegion);
                }
            }
            break;
            case SOUTH: {
                int newLocationY = newRegions.getMinValues().getY() - Regions.REGION_EDGE_SIZE;
                int startingX = newRegions.getMinValues().getX();

                for (int i = 0; i < numberOfRegions; i++) {
                    if (i > 0) {
                        startingX += Regions.REGION_EDGE_SIZE;
                    }

                    Location loc = Location.create(startingX, newLocationY);
                    Region newRegion = RegionHelper.generateCity(loc,
                            Regions.REGION_EDGE_SIZE, Regions.REGION_EDGE_SIZE);
                    newRegions = newRegions.add(newRegion);
                }
            }
            break;
            case EAST: {
                int newLocationX = newRegions.getMaxValues().getX() + 1;
                int startingY = newRegions.getMinValues().getY();

                for (int i = 0; i < numberOfRegions; i++) {
                    if (i > 0) {
                        startingY += Regions.REGION_EDGE_SIZE;
                    }

                    Location loc = Location.create(newLocationX, startingY);
                    Region newRegion = RegionHelper.generateCity(loc,
                            Regions.REGION_EDGE_SIZE, Regions.REGION_EDGE_SIZE);
                    newRegions = newRegions.add(newRegion);
                }
            }
            break;
            case WEST: {
                int newLocationX = newRegions.getMinValues().getX() - Regions.REGION_EDGE_SIZE;
                int startingY = newRegions.getMinValues().getY();

                for (int i = 0; i < numberOfRegions; i++) {
                    if (i > 0) {
                        startingY += Regions.REGION_EDGE_SIZE;
                    }

                    Location loc = Location.create(newLocationX, startingY);
                    Region newRegion = RegionHelper.generateCity(loc,
                            Regions.REGION_EDGE_SIZE, Regions.REGION_EDGE_SIZE);
                    newRegions = newRegions.add(newRegion);
                }
            }
            break;
        }

        return newRegions;
    }

    private static int calcSideRegions(Regions regions, Direction direction) {

        int max = -1;
        int min = -1;

        switch (direction) {
            case NORTH:
            case SOUTH:
                min = regions.getMinValues().getX();
                max = regions.getMaxValues().getX();
                break;
            case EAST:
            case WEST:
                min = regions.getMinValues().getY();
                max = regions.getMaxValues().getY();
                break;
        }

        return (max - min + 1) / Regions.REGION_EDGE_SIZE;
    }

    private final class CloseDirectionGameActionProvider extends DirectionGameActionProvider {

        @Override
        protected Game handle(Game game, Location destination) {

            Regions regions = game.getRegions();
            game = super.handle(game, destination);

            // TBD: handle multiple "closeable" things in the same location?
            Entity closeable = regions.get(destination).stream()
                    .filter(e -> e.getAttributeValue(CommonAttributes.CLOSED.name()) != null)
                    .findAny().orElse(null);

            if (closeable != null) {
                Openable toClose = new Openable(closeable);
                if (!toClose.isClosed()) {
                    regions = regions.remove(closeable, destination);
                    closeable = toClose.close().getEntity();
                    regions = regions.add(closeable, destination);

                    game = game.copy()
                            .setRegions(regions)
                            .build();
                } else {
                    String log = closeable.getDisplayName() + " is already closed";
                    game = game.copy()
                            .addLog(log)
                            .build();
                }
            } else {
                String log = "Nothing to close";
                game = game.copy()
                        .addLog(log)
                        .build();
            }

            return game;
        }
    }

    private final class OpenDirectionGameActionProvider extends DirectionGameActionProvider {

        @Override
        protected Game handle(Game game, Location destination) {
            Regions regions = game.getRegions();
            game = super.handle(game, destination);

            // TBD: handle multiple "openable" things in the same location?
            Entity openable = regions.get(destination).stream()
                    .filter(e -> e.getAttributeValue(CommonAttributes.CLOSED.name()) != null)
                    .findAny().orElse(null);

            if (openable != null) {
                Openable toOpen = new Openable(openable);
                if (toOpen.isClosed()) {
                    regions = regions.remove(openable, destination);
                    openable = toOpen.open().getEntity();
                    regions = regions.add(openable, destination);

                    game = game.copy()
                            .setRegions(regions)
                            .build();
                } else {
                    String log = openable.getDisplayName() + " is already open";
                    game = game.copy()
                            .addLog(log)
                            .build();
                }
            } else {
                String log = "Nothing to open";
                game = game.copy()
                        .addLog(log)
                        .build();
            }

            return game;
        }
    }
}
