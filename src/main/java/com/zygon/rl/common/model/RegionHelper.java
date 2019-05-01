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
import com.zygon.rl.lab.rng.family.FamilyTreeGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author zygon
 */
public class RegionHelper {

    @Deprecated
    public Region generateDeprecatedRegion(Location start, int maxX, int maxY) {
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
                    optionalEntity = Entities.createMonster(
                            FamilyTreeGenerator.create().getName().toString());
                } else if (random > 0.95) {
                    optionalEntity = Entities.ROCK;
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

    // TODO: for all of these adjustable weights, put them into JSON!!
    public Region generateForestRegion(Location start, int maxX, int maxY, boolean withPlayer) {

        // TODO: make static OR add noise
        Map<Double, Entity> entitiesByWeight = new HashMap<>();
        entitiesByWeight.put(0.69, Entities.GRASS);
        entitiesByWeight.put(0.20, Entities.TREE);
        entitiesByWeight.put(0.06, Entities.DIRT);
        entitiesByWeight.put(0.05, Entities.PUDDLE);

        return generateRegion(start, maxX, maxY, entitiesByWeight, false, withPlayer);
    }

    public Region generateFieldRegion(Location start, int maxX, int maxY, boolean withPlayer) {

        // TODO: make static OR add noise
        Map<Double, Entity> entitiesByWeight = new HashMap<>();
        entitiesByWeight.put(0.82, Entities.GRASS);
        entitiesByWeight.put(0.07, Entities.TREE);
        entitiesByWeight.put(0.06, Entities.DIRT);
        entitiesByWeight.put(0.05, Entities.PUDDLE);

        return generateRegion(start, maxX, maxY, entitiesByWeight, false, withPlayer);
    }

    public Region generateSwampRegion(Location start, int maxX, int maxY, boolean withPlayer) {

        // TODO: make static OR add noise
        Map<Double, Entity> entitiesByWeight = new HashMap<>();
        entitiesByWeight.put(0.40, Entities.PUDDLE);
        entitiesByWeight.put(0.35, Entities.GRASS);
        entitiesByWeight.put(0.20, Entities.DIRT);
        entitiesByWeight.put(0.05, Entities.TREE);

        return generateRegion(start, maxX, maxY, entitiesByWeight, false, withPlayer);
    }

    public Region generateCity(Location start, int maxX, int maxY, boolean withPlayer) {

        // TODO: make static OR add noise
        Map<Double, Entity> entitiesByWeight = new HashMap<>();
        entitiesByWeight.put(0.82, Entities.GRASS);
        entitiesByWeight.put(0.07, Entities.TREE);
        entitiesByWeight.put(0.06, Entities.DIRT);
        entitiesByWeight.put(0.05, Entities.PUDDLE);

        return generateRegion(start, maxX, maxY, entitiesByWeight, true, withPlayer);
    }

    // boolean withPlayer - hack for now
    private Region generateRegion(Location start, int maxX, int maxY,
            Map<Double, Entity> entitiesByWeight, boolean fillCity, boolean withPlayer) {

        Region region = new Region();

        // TODO: fix this "city" concept, it's an invasive concept here
        Map<Location, Set<Entity>> cityEntitiesByLocation = Collections.emptyMap();
        if (fillCity) {
            Grid grid = new Grid(maxX, maxY);
            CityGenerator cityGenerator = new CityGenerator();
            cityGenerator.setMinRoomSize(5);
            int roomGenAttempts = (grid.getWidth() / cityGenerator.getMaxRoomSize())
                    * (grid.getHeight() / cityGenerator.getMaxRoomSize());
            cityGenerator.setRoomGenerationAttempts(roomGenAttempts * 2);
            cityGenerator.generate(grid);
            cityEntitiesByLocation = cityGenerator.getRoomsEntitiesByLocation();
        }

        List<Location> availablePlayerLocations = new ArrayList<>();
        Random rand = new Random();
        RandomCollection<Entity> randomCollection = new RandomCollection<>();

        entitiesByWeight.forEach((k, v) -> {
            randomCollection.add(k, v);
        });

        for (int y = 0; y < maxY; y++) {
            for (int x = 0; x < maxX; x++) {

                // Don't use this to add to the region, it's always zero based.
                Location location = Location.create(x, y);

                // Location in grid is zero based. So create an offset location
                // for adding to the actual region.
                Location regionOffsetLoc = Location.create(
                        location.getX() + start.getX(),
                        location.getY() + start.getY());

                Set<Entity> entities = cityEntitiesByLocation.get(location);
                if (entities != null) {
                    for (Entity entity : entities) {
                        region = region.add(entity, regionOffsetLoc);
                    }
                } else {
                    Entity entity = randomCollection.next();
                    region = region.add(entity, regionOffsetLoc);
                    availablePlayerLocations.add(regionOffsetLoc);

                    // TBD: how to handle monsters??
                    // TBD: how to layer???
                    Entity optionalEntity = null;
//                    if (random > 0.99) {
//                        optionalEntity = Entities.createMonster(
//                                FamilyTreeGenerator.create().getName().toString());
//                    }

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

    // TBD: could move out
    private enum Terrain {
        CITY,
        FIELD,
        FOREST,
        SWAMP
    }

    // Main method to generate random terrain regions
    public Region generateRegion(Location start, int maxX, int maxY, boolean withPlayer) {

        RandomCollection<Terrain> randomTerrain = new RandomCollection<>();

        randomTerrain.add(0.05, Terrain.CITY);
        randomTerrain.add(0.65, Terrain.FIELD);
        randomTerrain.add(0.20, Terrain.FOREST);
        randomTerrain.add(0.10, Terrain.SWAMP);

        switch (randomTerrain.next()) {
            case CITY:
                return generateCity(start, maxX, maxY, withPlayer);
            case FIELD:
                return generateFieldRegion(start, maxX, maxY, withPlayer);
            case FOREST:
                return generateForestRegion(start, maxX, maxY, withPlayer);
            case SWAMP:
                return generateSwampRegion(start, maxX, maxY, withPlayer);
        }

        return null;
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

                    // TODO: also use weighted random
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

    private final class RandomCollection<E> {

        // Ugh, this collapses on the weight so you can't have multiple
        // terrains with the exact same weight.
        // TODO: fix this
        private final NavigableMap<Double, E> map = new TreeMap<>();
        private final Random random;
        private double total = 0;

        public RandomCollection() {
            this(new Random());
        }

        public RandomCollection(Random random) {
            this.random = random;
        }

        public RandomCollection<E> add(double weight, E result) {
            if (weight <= 0) {
                return this;
            }
            total += weight;
            map.put(total, result);
            return this;
        }

        public E next() {
            double value = random.nextDouble() * total;
            return map.higherEntry(value).getValue();
        }
    }
}
