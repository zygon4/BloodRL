/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.common.view;

import com.zygon.rl.common.model.CommonAttributes;
import com.zygon.rl.core.model.DoubleAttribute;
import com.zygon.rl.core.model.Entity;
import com.zygon.rl.core.model.Location;
import com.zygon.rl.core.model.Region;

import java.util.Set;

/**
 *
 * @author zygon
 */
public class FOVHelper {

    public float[][] generateSimpleResistances(Region region) {

        Location minValues = region.getMinValues();

        int width = region.getWidth();
        int height = region.getHeight();
        float[][] portion = new float[width][height];

        for (int x = minValues.getX(); x < minValues.getX() + width; x++) {
            for (int y = minValues.getY(); y < minValues.getY() + height; y++) {
                Set<Entity> entities = region.get(Location.create(x, y));

                // TBD: "view blocking" as first attempt at light layering
                // Note this should be parallelStream(), but the runtime kept crashing inexplicably
                double viewBlocking = entities.stream()
                        .map(e -> e.getAttributes(CommonAttributes.VIEW_BLOCK.name()))
                        .map(a -> {
                            return a.stream()
                                    .map(DoubleAttribute::create)
                                    .map(DoubleAttribute::getDoubleValue)
                                    .mapToDouble(v -> v)
                                    .max().orElseGet(() -> 0.0);
                        })
                        .mapToDouble(v -> v)
                        .max().orElseGet(() -> 0.0);

                try {
                    portion[x - minValues.getX()][y - minValues.getY()] = (float) viewBlocking;
                } catch (java.lang.ArrayIndexOutOfBoundsException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        return portion;
    }
}
