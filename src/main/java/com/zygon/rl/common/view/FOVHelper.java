/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.common.view;

import com.zygon.rl.common.model.CommonAttributes;
import com.zygon.rl.core.model.Attribute;
import com.zygon.rl.core.model.DoubleAttribute;
import com.zygon.rl.core.model.Entity;
import com.zygon.rl.core.model.Location;
import com.zygon.rl.core.model.Region;

import java.util.List;
import java.util.Set;

/**
 *
 * @author zygon
 */
public class FOVHelper {

    public static final String VIEW_BLOCK_NAME = CommonAttributes.VIEW_BLOCK.name();

    public float[][] generateSimpleResistances(Region region) {

        Location minValues = region.getMinValues();

        int width = region.getWidth();
        int height = region.getHeight();
        float[][] portion = new float[width][height];

        for (int x = minValues.getX(); x < minValues.getX() + width; x++) {
            for (int y = minValues.getY(); y < minValues.getY() + height; y++) {
                List<Entity> entities = region.get(Location.create(x, y));

                // TBD: "view blocking" as first attempt at light layering
                double viewBlocking = 0.0;
                for (Entity entity : entities) {
                    double val = FOVHelper.getMaxViewBlock(entity);
                    if (val > viewBlocking) {
                        viewBlocking = val;
                    }
                }

                try {
                    portion[x - minValues.getX()][y - minValues.getY()] = (float) viewBlocking;
                } catch (java.lang.ArrayIndexOutOfBoundsException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        return portion;
    }

    public static double getMaxViewBlock(Entity entity) {

        double max = 0.0;
        Set<Attribute> viewBlocking = entity.getAttributes(VIEW_BLOCK_NAME);

        for (Attribute attr : viewBlocking) {
            double attrVal = DoubleAttribute.getValue(attr);
            if (attrVal > max) {
                max = attrVal;
            }
        }

        return max;
    }
}
