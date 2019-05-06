package com.zygon.rl.common.model;

import java.util.Map;

/**
 *
 * @author zygon
 */
public class TerrainInfo {

    private final Terrain terrain;
    private final Map<Double, String> entitiesByWeight;

    public TerrainInfo(Terrain terrain, Map<Double, String> entitiesByWeight) {
        this.terrain = terrain;
        this.entitiesByWeight = entitiesByWeight;
    }

    public Map<Double, String> getEntitiesByWeight() {
        return entitiesByWeight;
    }

    public Terrain getTerrain() {
        return terrain;
    }
}
