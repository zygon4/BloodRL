package com.zygon.rl.core.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author zygon
 */
public class Regions {

    public static final int REGION_EDGE_SIZE = 20;

    // Bottom left oriented
    private final Map<Location, Region> regionsByLocation;

    private final Location minValues;
    private final Location maxValues;

    private Regions(Map<Location, Region> regionsByLocation, Location minValues, Location maxValues) {
        this.regionsByLocation = regionsByLocation != null
                ? Collections.unmodifiableMap(regionsByLocation)
                : Collections.emptyMap();
        this.minValues = minValues;
        this.maxValues = maxValues;
    }

    public static Regions create() {
        return new Regions(null, Region.DEFAULT_MIN_LOCATION, Region.DEFAULT_MAX_LOCATION);
    }

    public Regions add(Entity entity, Location location) {
        Map<Location, Region> regions = new HashMap<>(regionsByLocation);

        Region withAdd = getRegion(location).add(entity, location);
        add(withAdd, regions);

        return new Regions(regions, minValues, maxValues);
    }

    public Regions add(Region region) {

        Map<Location, Region> regions = new HashMap<>(regionsByLocation);
        add(region, regions);

        Location newMin = null;
        Location newMax = null;

        for (Region loc : regions.values()) {
            newMin = newMin == null ? loc.getMinValues()
                    : Region.getMin(newMin, loc.getMinValues());
            newMax = newMax == null ? loc.getMaxValues()
                    : Region.getMax(newMax, loc.getMaxValues());
        }

        return new Regions(regions, newMin, newMax);
    }

    public Set<Location> find(String entityName, Function<Location, Boolean> entityFilter) {
        return regionsByLocation.values().parallelStream()
                .map(r -> r.find(entityName))
                .flatMap(s -> s.parallelStream())
                .filter(loc -> entityFilter.apply(loc))
                .collect(Collectors.toSet());
    }

    public Set<Location> find(String entityName) {
        return find(entityName, loc -> true);
    }

    public Set<Location> find(Entity entity, Function<Location, Boolean> entityFilter) {
        return find(entity.getName(), entityFilter);
    }

    public Set<Location> find(Entity entity) {
        return find(entity.getName());
    }

    public List<Entity> get(Location location) {
        return getRegion(location).get(location);
    }

    public Location getMaxValues() {
        return maxValues;
    }

    public Location getMinValues() {
        return minValues;
    }

    public Region getRegion(Location location) {
        // TODO: could have this indexed, would be faster
        return regionsByLocation.entrySet().parallelStream()
                .filter(e -> e.getValue().contains(location))
                .map(e -> e.getValue())
                .findAny().orElse(null);
    }

    public Region getView(Location startLocation, int width, int height) {
        Region regionView = new Region();

        return getView(startLocation, width, height, regionView);
    }

    // Returns a possibly stitched together region from different surrounding regions, depending
    // on where the location is.
    private Region getView(Location startLocation, int width, int height, Region regionView) {

        if (width == 0 && height == 0) {
            return regionView;
        }

        Region startRegion = getRegion(startLocation);

        boolean stitchEast = false;
        boolean stitchNorth = false;

        int localStartX = startLocation.getX();
        int localStartY = startLocation.getY();
        int localStripWidth = Math.min(startRegion.getMaxValues().getX() - localStartX + 1, Math.min(width, REGION_EDGE_SIZE));
        int localStripHeight = Math.min(startRegion.getMaxValues().getY() - localStartY + 1, Math.min(height, REGION_EDGE_SIZE));
        Map<Location, List<Entity>> outEntitiesByLocation = new HashMap<>();

        add(outEntitiesByLocation, regionView, startRegion,
                localStartX, localStartY, localStripWidth, localStripHeight);

        if ((startLocation.getX() - startRegion.getMinValues().getX()) + width > startRegion.getWidth()) {
            stitchEast = true;

            int remainingWidth = width - localStripWidth;
            int locationOffset = startLocation.getX() + localStripWidth;
            while (remainingWidth > 0) {
                Region eastRegion = getRegion(
                        Location.create(locationOffset, startLocation.getY()));

                int startX = eastRegion.getMinValues().getX();
                int startY = localStartY;

                int stripWidth = remainingWidth >= REGION_EDGE_SIZE ? REGION_EDGE_SIZE : remainingWidth;
                // doesn't need to be calc'd each time
                final int stripHeight = Math.min(height, REGION_EDGE_SIZE)
                        - (startLocation.getY() - startRegion.getMinValues().getY());

                locationOffset += REGION_EDGE_SIZE;
                remainingWidth -= stripWidth;

                add(outEntitiesByLocation, regionView, eastRegion, startX, startY, stripWidth, stripHeight);
            }
        }

        if ((startLocation.getY() - startRegion.getMinValues().getY()) + height > startRegion.getHeight()) {
            stitchNorth = true;

            int remainingHeight = height - localStripHeight;
            int locationOffset = startLocation.getY() + localStripHeight;
            while (remainingHeight > 0) {
                Region northRegion = getRegion(
                        Location.create(startLocation.getX(), locationOffset));

                int startX = localStartX;
                int startY = northRegion.getMinValues().getY();

                int stripHeight = remainingHeight >= REGION_EDGE_SIZE ? REGION_EDGE_SIZE : remainingHeight;
                final int stripWidth = Math.min(width, REGION_EDGE_SIZE)
                        - (startLocation.getX() - startRegion.getMinValues().getX());

                locationOffset += REGION_EDGE_SIZE;
                remainingHeight -= stripHeight;

                add(outEntitiesByLocation, regionView, northRegion, startX, startY, stripWidth, stripHeight);
            }
        }

        if (stitchEast && stitchNorth) {
            // northeast
            Location start = Location.create(startRegion.getMaxValues().getX() + 1,
                    startRegion.getMaxValues().getY() + 1);

            int startX = startRegion.getMaxValues().getX() + 1;
            int startY = startRegion.getMaxValues().getY() + 1;
            int stripWidth = width - (startRegion.getMaxValues().getX() - startLocation.getX() + 1);
            int stripHeight = height - (startRegion.getMaxValues().getY() - startLocation.getY() + 1);

            // Call recursively
            Region neRegion = getView(start, stripWidth, stripHeight, regionView);
            add(outEntitiesByLocation, regionView, neRegion, startX, startY, stripWidth, stripHeight);
        }

        regionView = regionView.add(outEntitiesByLocation);

        return regionView;
    }

    public Regions move(Entity entity, Location destination) {

        Set<Location> sourceLocations = regionsByLocation.entrySet().parallelStream()
                .map(entry -> entry.getValue().find(entity))
                .flatMap(l -> l.parallelStream())
                .collect(Collectors.toSet());

        Map<Location, Region> sourceRegionsByLocation = sourceLocations.parallelStream()
                .collect(Collectors.toMap(l -> l, l -> getRegion(l)));

        Region targetRegion = regionsByLocation.entrySet().parallelStream()
                .filter(e -> e.getValue().contains(destination))
                .map(e -> e.getValue())
                .findAny().orElse(null);

        Map<Location, Region> regions = new HashMap<>(regionsByLocation);

        for (Location l : sourceLocations) {
            Region sourceRegion = sourceRegionsByLocation.get(l);

            // actual object reference equality
            if (sourceRegion == targetRegion) {
                Region singleRegionMove = sourceRegion.move(entity, destination);
                add(singleRegionMove, regions);
            } else {
                sourceRegion = sourceRegion.remove(entity, l);
                add(sourceRegion, regions);

                targetRegion = targetRegion.add(entity, destination);
                add(targetRegion, regions);
            }
        }

        return new Regions(regions, minValues, maxValues);
    }

    public Regions remove(Entity entity, Location location) {
        Map<Location, Region> regions = new HashMap<>(regionsByLocation);

        Region withRemoval = getRegion(location).remove(entity, location);
        add(withRemoval, regions);

        return new Regions(regions, minValues, maxValues);
    }

    private void add(Map<Location, List<Entity>> outEntitiesByLocation,
            Region toRegion, Region fromRegion, int startX, int startY, int stripWidth, int stripHeight) {

        for (int y = startY; y < startY + stripHeight; y++) {
            for (int x = startX; x < startX + stripWidth; x++) {
                Location l = Location.create(x, y);

                List<Entity> entities = fromRegion.get(l);
                toRegion = toRegion.add(entities, l);
                outEntitiesByLocation.put(l, entities);
            }
        }
    }

    private void add(Region region, Map<Location, Region> regions) {
        Location minLocation = region.getMinValues();
        regions.put(minLocation, region);
    }
}
