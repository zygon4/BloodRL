/*
 * ========================================================================
 *
 * Copyright (c) by Hitachi Vantara, 2018. All rights reserved.
 *
 * ========================================================================
 */
package com.zygon.rl.core.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Region
 *
 */
public final class Region {

    static final Location DEFAULT_MIN_LOCATION = Location.create(
            Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    static final Location DEFAULT_MAX_LOCATION = Location.create(
            Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);

    private final Map<Location, Set<Entity>> entitiesByLocation;
    private final Map<Entity, Set<Location>> locationsByEntity;

    private final Location minValues;
    private final Location maxValues;

    private Region(Map<Location, Set<Entity>> entitiesByLocation,
            Map<Entity, Set<Location>> locationByentity,
            Location minValues, Location maxValues) {
//        this.entitiesByLocation = Collections.unmodifiableMap(entitiesByLocation);
//        this.locationsByEntity = Collections.unmodifiableMap(locationByentity);

        // TBD: mutable, ick
        this.entitiesByLocation = entitiesByLocation;
        this.locationsByEntity = locationByentity;

        this.minValues = minValues;
        this.maxValues = maxValues;
    }

    public Region() {
//        this(Collections.emptyMap(), Collections.emptyMap(), DEFAULT_MIN_LOCATION, DEFAULT_MAX_LOCATION);
        this(new HashMap<>(), new HashMap<>(), DEFAULT_MIN_LOCATION, DEFAULT_MAX_LOCATION);
    }

    public Region move(Entity entity, Location destination) {
        Set<Location> entityLocations = find(entity);

        // could throw state exception instead
        if (entityLocations != null && !entityLocations.isEmpty()) {
            Region added = null;
            Location newMin = null;
            Location newMax = null;

            for (Location loc : entityLocations) {
                Region removed = remove(entity, loc);

                added = removed.add(entity, destination);
                newMin = getMin(minValues, destination);
                newMax = getMax(maxValues, destination);
            }

            return new Region(added.entitiesByLocation, added.locationsByEntity, newMin, newMax);

        } else {
            return this;
        }
    }

    // No longer immutable :(
    public Region add(Map<Location, Set<Entity>> newEntitiesByLocation) {
        // Add to loc -> entity mapping
//        Map<Location, Set<Entity>> entsByLoc = new ConcurrentHashMap<>();
        Map<Location, Set<Entity>> entsByLoc = entitiesByLocation;

//        entitiesByLocation.entrySet().parallelStream()
//                .forEach(entry -> {
//                    entsByLoc.put(entry.getKey(), entry.getValue());
//                });
        newEntitiesByLocation.entrySet().parallelStream()
                .forEach(entry -> {
                    Location location = entry.getKey();
                    Set<Entity> ents = entsByLoc.get(location);
                    if (ents == null) {
                        ents = new HashSet<>();
                        entsByLoc.put(location, ents);
                    }
                    ents.addAll(entry.getValue());
                });

        // Add to entity -> loc mapping
//        Map<Entity, Set<Location>> locByEntity = new ConcurrentHashMap<>();
        Map<Entity, Set<Location>> locByEntity = locationsByEntity;
//        locationsByEntity.entrySet().parallelStream()
//                .forEach(entry -> {
//                    locByEntity.put(entry.getKey(), entry.getValue());
//                });

        Location newMin = null;
        Location newMax = null;

        for (Map.Entry<Location, Set<Entity>> entry : newEntitiesByLocation.entrySet()) {
            Location location = entry.getKey();
            entry.getValue().forEach(entity -> {
                Set<Location> entityLocs = locByEntity.get(entity);
                if (entityLocs == null) {
                    entityLocs = new HashSet<>();
                    locByEntity.put(entity, entityLocs);
                }
                entityLocs.add(location);
            });

            newMin = getMin(newMin == null ? minValues : newMin, location);
            newMax = getMax(newMax == null ? maxValues : newMax, location);
        }

        return new Region(entsByLoc, locByEntity, newMin, newMax);
    }

    public Region add(Set<Entity> entities, Location location) {
        return add(Collections.singletonMap(location, entities));
    }

    public Region add(Entity entity, Location location) {
        return add(Collections.singleton(entity), location);
    }

    public boolean contains(Location location) {
        return location.getX() >= minValues.getX() && location.getX() <= maxValues.getX()
                && location.getY() >= minValues.getY() && location.getY() <= maxValues.getY();
    }

    public Set<Entity> get(Location location) {
        Set<Entity> entities = entitiesByLocation.get(location);
        return entities == null ? Collections.emptySet() : Collections.unmodifiableSet(entities);
    }

    public int getHeight() {
        return maxValues.getY() - minValues.getY() + 1;
    }

    public int getWidth() {
        return maxValues.getX() - minValues.getX() + 1;
    }

    public Set<Location> find(String entityName) {
        return Collections.unmodifiableSet(locationsByEntity.entrySet().stream()
                .filter(e -> e.getKey().getName().equals(entityName))
                .map(Map.Entry::getValue)
                .flatMap(e -> e.stream())
                .collect(Collectors.toSet()));
    }

    public Set<Location> find(Entity entity) {
        Set<Location> locations = locationsByEntity.get(entity);
        return locations != null
                ? Collections.unmodifiableSet(locations) : Collections.emptySet();
    }

    public Region remove(Entity entity, Location location) {
        // Remove from loc -> entity mapping
        Map<Location, Set<Entity>> entsByLoc = new HashMap<>(entitiesByLocation);
        Set<Entity> ents = entsByLoc.get(location);

        if (ents != null) {
            ents.remove(entity);
        }

        // Remove from entity -> loc mapping
        Map<Entity, Set<Location>> locByEntity = new HashMap<>(locationsByEntity);
        locByEntity.remove(entity);

        Location newMin = getMin(minValues, location);
        Location newMax = getMax(maxValues, location);

        return new Region(entsByLoc, locByEntity, newMin, newMax);
    }

    public Location getMaxValues() {
        return maxValues;
    }

    public Location getMinValues() {
        return minValues;
    }

    @Override
    public String toString() {
        return getMinValues() + ":" + getMaxValues();
    }

    static Location getMin(Location minValues, Location incoming) {
        final int incomingX = incoming.getX();
        final int incomingY = incoming.getY();
        final int incomingZ = incoming.getZ();

        int newXMin = minValues.getX();
        int newYMin = minValues.getY();
        int newZMin = minValues.getZ();

        if (incomingX < minValues.getX()) {
            newXMin = incomingX;
        }

        if (incomingY < minValues.getY()) {
            newYMin = incomingY;
        }

        if (incomingZ < minValues.getZ()) {
            newZMin = incomingZ;
        }

        return Location.create(newXMin, newYMin, newZMin);
    }

    static Location getMax(Location maxValues, Location location) {
        final int incomingX = location.getX();
        final int incomingY = location.getY();
        final int incomingZ = location.getZ();

        int newXMax = maxValues.getX();
        int newYMax = maxValues.getY();
        int newZMax = maxValues.getZ();

        if (incomingX > maxValues.getX()) {
            newXMax = incomingX;
        }

        if (incomingY > maxValues.getY()) {
            newYMax = incomingY;
        }

        if (incomingZ > maxValues.getZ()) {
            newZMax = incomingZ;
        }

        return Location.create(newXMax, newYMax, newZMax);
    }
}
