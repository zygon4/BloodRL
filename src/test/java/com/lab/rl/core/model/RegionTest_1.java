/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lab.rl.core.model;

import com.zygon.rl.core.model.Region;
import com.zygon.rl.core.model.Entity;
import com.zygon.rl.core.model.Location;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author zygon
 */
public class RegionTest_1 {

    @Test
    public void testSimpleAdd() {
        Location l1 = Location.create(0, 0);
        Entity e1 = Entity.builder()
                .setName("test")
                .setDisplayName("Test")
                .build();

        Region r = new Region();
        r = r.add(e1, l1);

        Assert.assertEquals(1, r.get(l1).size());
        Assert.assertEquals("test", r.get(l1).stream().findAny().get().getName());
        Assert.assertEquals("Test", r.get(l1).stream().findAny().get().getDisplayName());
    }

    @Test
    public void testDoubleAdd() {
        Location l1 = Location.create(0, 0);
        Entity e1 = Entity.builder()
                .setName("test1")
                .setDisplayName("Test1")
                .build();

        Entity e2 = Entity.builder()
                .setName("test2")
                .setDisplayName("Test2")
                .build();

        Region r = new Region();
        r = r.add(e1, l1);
        r = r.add(e2, l1);

        Assert.assertEquals(2, r.get(l1).size());
    }

    @Test
    public void testMove() {
        Location l1 = Location.create(0, 0);
        Location l2 = Location.create(0, 1);

        Entity e1 = Entity.builder()
                .setName("test1")
                .setDisplayName("Test1")
                .build();

        Entity e2 = Entity.builder()
                .setName("test2")
                .setDisplayName("Test2")
                .build();

        Region r = new Region();
        r = r.add(e1, l1);
        r = r.add(e2, l1);

        Assert.assertEquals(2, r.get(l1).size());

        r = r.move(e1, l2);

        Assert.assertEquals(1, r.get(l1).size());
        Assert.assertEquals(1, r.get(l2).size());

        Assert.assertEquals("test1", r.get(l2).stream().findAny().get().getName());
        Assert.assertEquals("test2", r.get(l1).stream().findAny().get().getName());

        r = r.move(e2, l2);

        Assert.assertEquals(0, r.get(l1).size());
        Assert.assertEquals(2, r.get(l2).size());
    }
}
