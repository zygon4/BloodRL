/*
 * ========================================================================
 *
 * Copyright (c) by Hitachi Vantara, 2018. All rights reserved.
 *
 * ========================================================================
 */
package com.zygon.rl.core.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Location
 *
 */
public class Location {

    // singleton cache :/
    private static final Map<Integer, Location> KNOWN_LOCATIONS = new HashMap<>();

    private final int x;
    private final int y;
    private final int z;
    private final int hashCode;

    private Location(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;

        this.hashCode = createHashCode(this.x, this.y, this.z);
    }

    public double getDistance(Location o) {

        double total = 0.0;

        total += Math.pow(this.x - o.x, 2);
        total += Math.pow(this.y - o.y, 2);
        total += Math.pow(this.z - o.z, 2);

        return Math.sqrt(total);
    }

    public Set<Location> getNeighbors() {
        Set<Location> neighors = new HashSet<>();

        neighors.add(Location.create(x + 1, y, z));
        neighors.add(Location.create(x + 1, y + 1, z));

        neighors.add(Location.create(x - 1, y, z));
        neighors.add(Location.create(x - 1, y - 1, z));

        neighors.add(Location.create(x, y + 1, z));
        neighors.add(Location.create(x - 1, y + 1, z));

        neighors.add(Location.create(x, y - 1, z));
        neighors.add(Location.create(x + 1, y - 1, z));

        return neighors;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public static Location create(int x, int y, int z) {
        int hash = createHashCode(x, y, z);
        if (KNOWN_LOCATIONS.containsKey(hash)) {
            Location test = KNOWN_LOCATIONS.get(hash);
            if (test.getX() != x || test.getY() != y) {
                throw new RuntimeException();
            }

            return KNOWN_LOCATIONS.get(hash);
        } else {
            Location location = new Location(x, y, z);
            KNOWN_LOCATIONS.put(hash, location);
            return location;
        }
    }

    public static Location create(int x, int y) {
        return create(x, y, 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Location that = (Location) o;

        return x == that.x && y == that.y && z == that.z;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return getDisplay(x, y, z);
    }

    private static String getDisplay(int x, int y, int z) {
        return "[" + x + "," + y + "," + z + "]";
    }

    private static int createHashCode(int x, int y, int z) {
        return getDisplay(x, y, z).hashCode();
    }
}
