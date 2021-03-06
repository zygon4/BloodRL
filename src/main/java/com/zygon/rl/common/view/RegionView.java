package com.zygon.rl.common.view;

import com.zygon.rl.core.model.Entity;
import com.zygon.rl.core.model.Location;
import com.zygon.rl.core.model.Regions;

import java.util.List;
import java.util.Objects;

/**
 */
public class RegionView {

    // TODO:
//    public static enum Shape {
//        CIRCLE,
//        SQUARE
//    }
//
    private final Regions regions;
    private final Location center;
    private final int radius;

    public RegionView(Regions regions, Location center, int radius) {
        this.regions = Objects.requireNonNull(regions);
        this.center = Objects.requireNonNull(center);
        this.radius = radius;
    }

    /**
     * Returns the Set of Entity's that reside at the desired location FROM THE
     * USER'S PERSPECTIVE. E.g. viewLocation 0/0 means the region passed in,
     * centered at "center", and "radius" subtracted.
     *
     * @param viewLocation
     * @param viewCenter
     * @return the Set of Entity's that reside at the desired location FROM THE
     * USER'S PERSPECTIVE.
     */
    public List<Entity> get(Location viewLocation, Location viewCenter) {

        int xDistanceFromCenter = viewCenter.getX() - viewLocation.getX();
        int yDistanceFromCenter = viewCenter.getY() - viewLocation.getY();

        // <= ?
        int regionLocX = center.getX() - xDistanceFromCenter;
        int regionLocY = center.getY() - yDistanceFromCenter;

        Location reviewLocation = Location.create(regionLocX, regionLocY);

        return regions.get(reviewLocation);
    }
}
