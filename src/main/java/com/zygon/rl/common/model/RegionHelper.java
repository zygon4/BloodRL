/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.common.model;

import com.github.czyzby.noise4j.map.Grid;
import com.github.czyzby.noise4j.map.generator.room.dungeon.DungeonGenerator;
import com.zygon.rl.core.model.Entity;
import com.zygon.rl.core.model.Location;
import com.zygon.rl.core.model.Region;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author zygon
 */
public class RegionHelper {

    @Deprecated
    public Region generateRegion(Location start, int maxX, int maxY) {
        Region region = new Region();
        List<Location> availablePlayerLocations = new ArrayList<>();
        Random rand = new Random();

        for (int y = start.getY(); y < start.getY() + maxY; y++) {
            for (int x = start.getX(); x < start.getX() + maxX; x++) {
                Location location = Location.create(x, y);

                double random = rand.nextDouble();
                Entity entity = Entities.DIRT;
                region = region.add(entity, location);
                availablePlayerLocations.add(location);

                Entity optionalEntity = null;
                if (random > 0.98) {
                    optionalEntity = Entities.createMonster();
                } else if (random > 0.95) {
                    optionalEntity = Entities.SIGN;
                } else if (random > 0.85) {
                    optionalEntity = Entities.PUDDLE;
                } else if (random > 0.75) {
                    optionalEntity = Entities.TREE;
                }

                if (optionalEntity != null) {
                    if (!optionalEntity.getAttributes(CommonAttributes.IMPASSABLE.name()).isEmpty()) {
                        availablePlayerLocations.remove(location);
                    }
                    region = region.add(optionalEntity, location);
                }
            }
        }

        int randomLocation = rand.nextInt(availablePlayerLocations.size());
        Location randomPlayerLoc = availablePlayerLocations.get(randomLocation);
        region = region.add(Entities.PLAYER, randomPlayerLoc);

        return region;
    }

    // TODO pass in entity name/id -> weighted random chance
    @Deprecated
    public Region generateRegion(int maxX, int maxY) {
        return generateRegion(Location.create(0, 0), maxX, maxY);
    }

    // TODO:
//    private Region smooth(Region region, int times) {
//        Tile[][] tiles2 = new Tile[width][height];
//
//        for (int time = 0; time < times; time++) {
//
//            for (int x = 0; x < width; x++) {
//                for (int y = 0; y < height; y++) {
//                    int floors = 0;
//                    int rocks = 0;
//
//                    for (int ox = -1; ox < 2; ox++) {
//                        for (int oy = -1; oy < 2; oy++) {
//                            if (x + ox < 0 || x + ox >= width || y + oy < 0
//                                    || y + oy >= height) {
//                                continue;
//                            }
//
//                            if (tiles[x + ox][y + oy] == Tile.FLOOR) {
//                                floors++;
//                            } else {
//                                rocks++;
//                            }
//                        }
//                    }
//                    tiles2[x][y] = floors >= rocks ? Tile.FLOOR : Tile.WALL;
//                }
//            }
//
//            tiles = tiles2;
//        }
//
//        return this;
//    }
    // Lots of API possibilities here, for now, just return Regions, eventually return Regions
    public static Region generateCity(int maxX, int maxY, boolean withPlayer) {
        return generateCity(Location.create(0, 0), maxX, maxY, withPlayer);
    }

    // boolean withPlayer - hack for now
    public static Region generateCity(Location start, int maxX, int maxY, boolean withPlayer) {

        Region region = new Region();

        Grid grid = new Grid(maxX, maxY);
        CityGenerator cityGenerator = new CityGenerator();
        cityGenerator.setMinRoomSize(5);
        int roomGenAttempts = (grid.getWidth() / cityGenerator.getMaxRoomSize())
                * (grid.getHeight() / cityGenerator.getMaxRoomSize());
        cityGenerator.setRoomGenerationAttempts(roomGenAttempts * 2);
        cityGenerator.generate(grid);

        List<Location> availablePlayerLocations = new ArrayList<>();
        Random rand = new Random();

        for (int y = 0; y < maxY; y++) {
            for (int x = 0; x < maxX; x++) {

                // Don't use this to add to the region, it's always zero based.
                Location location = Location.create(x, y);
                Map<Location, Set<Entity>> entitiesByLocation = cityGenerator.getRoomsEntitiesByLocation();
                Set<Entity> entities = entitiesByLocation.get(location);

                // Location in grid is zero based. So create an offset location
                // for adding to the actual region.
                Location regionOffsetLoc = Location.create(
                        location.getX() + start.getX(),
                        location.getY() + start.getY());

                if (entities != null) {
                    for (Entity entity : entities) {
                        region = region.add(entity, regionOffsetLoc);
                    }
                } else {
                    double random = rand.nextDouble();
                    // this is stupid
                    Entity entity = rand.nextBoolean() ? Entities.DIRT : (rand.nextBoolean() ? Entities.GRASS : Entities.PUDDLE);
                    region = region.add(entity, regionOffsetLoc);
                    availablePlayerLocations.add(regionOffsetLoc);

                    Entity optionalEntity = null;
                    if (random > 0.99) {
                        optionalEntity = Entities.createMonster();
                    } else if (random > 0.95) {
                        optionalEntity = Entities.SIGN;
                    } else if (random > 0.90) {
                        optionalEntity = Entities.TREE;
                    }

                    if (optionalEntity != null) {
                        if (!optionalEntity.getAttributes(CommonAttributes.IMPASSABLE.name()).isEmpty()) {
                            availablePlayerLocations.remove(regionOffsetLoc);
                        }
                        region = region.add(optionalEntity, regionOffsetLoc);
                    }
                }
            }
        }

        if (withPlayer) {
            int randomLocation = rand.nextInt(availablePlayerLocations.size());
            Location randomPlayerLoc = availablePlayerLocations.get(randomLocation);
            region = region.add(Entities.PLAYER, randomPlayerLoc);
        }

        return region;
    }

    public static Region generateCity(Location start, int maxX, int maxY) {
        return generateCity(start, maxX, maxY, false);
    }

    private static class CityGenerator extends DungeonGenerator {

        private final Random rand = new Random();
        // Not immutable for performance
        private final Map<Location, Set<Entity>> roomsEntitiesByLocation = new HashMap<>();

        // Includes walls
        private Map<Location, Set<Entity>> getRoomsEntitiesByLocation() {
            return roomsEntitiesByLocation;
        }

        // From AbstractRoomGenerator
        @Override
        protected void carveRoom(Grid grid, Room room, float value) {
            super.carveRoom(grid, room, value);

            for (int y = room.getY(); y < room.getY() + room.getHeight(); y++) {
                for (int x = room.getX(); x < room.getX() + room.getWidth(); x++) {
                    Location location = Location.create(x, y);

                    if (room.isBorder(x, y)) {
                        Set<Entity> entities = new HashSet<>();
                        entities.add(Entities.DIRT);

                        double random = rand.nextDouble();
                        if (random > 0.95) {
                            entities.add(Entities.createDoor());
                        } else if (random > .90) {
                            entities.add(Entities.createWindow());
                        } else {
                            entities.add(Entities.WALL);
                        }

                        // TODO: random door (at most 1 or 2, at least 1)
                        roomsEntitiesByLocation.put(location, entities);
                    } else {
                        Set<Entity> entities = new HashSet<>();
                        entities.add(Entities.FLOOR);
                        roomsEntitiesByLocation.put(location, entities);
                    }
                }
            }
        }

        @Override
        protected void spawnCorridors(Grid grid) {
            // No op
        }

        @Override
        protected void joinRegions(Grid grid) {
            // No op
        }

        @Override
        protected void removeDeadEnds(Grid grid) {
            // No op
        }
    }
}
