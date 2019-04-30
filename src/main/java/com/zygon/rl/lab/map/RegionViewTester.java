/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.lab.map;

import com.zygon.rl.common.model.RegionHelper;
import com.zygon.rl.core.model.Location;
import com.zygon.rl.core.model.Region;
import com.zygon.rl.core.model.Regions;

/**
 *
 * @author zygon
 */
public class RegionViewTester {

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        Regions regionManager = Regions.create();

        RegionHelper regionHelper = new RegionHelper();

        Region r1 = regionHelper.generateDeprecatedRegion(Location.create(0, 0),
                Regions.REGION_EDGE_SIZE, Regions.REGION_EDGE_SIZE);
        Region r2 = regionHelper.generateDeprecatedRegion(Location.create(10, 0),
                Regions.REGION_EDGE_SIZE, Regions.REGION_EDGE_SIZE);
        Region r3 = regionHelper.generateDeprecatedRegion(Location.create(0, 10),
                Regions.REGION_EDGE_SIZE, Regions.REGION_EDGE_SIZE);
        Region r4 = regionHelper.generateDeprecatedRegion(Location.create(10, 10),
                Regions.REGION_EDGE_SIZE, Regions.REGION_EDGE_SIZE);

        regionManager = regionManager.add(r1);
        regionManager = regionManager.add(r2);
        regionManager = regionManager.add(r3);
        regionManager = regionManager.add(r4);

        // exact; no stitch
        Region v1 = regionManager.getView(Location.create(0, 0), 10, 10);
        validate(v1, 0, 0, 10, 10);

        // east stitch only
        Region v2 = regionManager.getView(Location.create(1, 0), 10, 10);
        validate(v2, 1, 0, 10, 10);

        // north stitch only
        Region v3 = regionManager.getView(Location.create(0, 1), 10, 10);
        validate(v3, 0, 1, 10, 10);

        // north and east stitch
        Region v4 = regionManager.getView(Location.create(1, 1), 10, 10);
        validate(v4, 1, 1, 10, 10);
    }

    private static void validate(Region region, int x, int y, int w, int h) throws Exception {
        if (region.getMinValues().getX() != x) {
            throw new Exception("Expected x = " + x + " was " + region.getMinValues().getX());
        }
        if (region.getMinValues().getY() != y) {
            throw new Exception("Expected y = " + y + " was " + region.getMinValues().getY());
        }
        int actualWidth = region.getMaxValues().getX() - region.getMinValues().getX() + 1;
        if (actualWidth != w) {
            throw new Exception("Expected w = " + w + " was " + actualWidth);
        }
        int actualHeight = region.getMaxValues().getY() - region.getMinValues().getY() + 1;
        if (actualHeight != h) {
            throw new Exception("Expected h = " + h + " was " + actualHeight);
        }
    }
}
