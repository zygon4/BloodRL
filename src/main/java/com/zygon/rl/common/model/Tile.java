/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.common.model;

import com.zygon.rl.core.model.Entity;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author zygon
 */
public enum Tile {

    FLOOR(Entities.FLOOR, '.', Color.YELLOW),
    DIRT(Entities.DIRT, '.', Color.LIGHT_GRAY),
    // TODO: need to inject conditional logic to provide a door glyph/color
    // based on door attributes and game status (e.g. light/dark)
    DOOR(Entities.DOOR, '+', Color.ORANGE),
    GRASS(Entities.GRASS, '"', Color.GREEN),
    MONSTER(Entities.MONSTER, 'm', Color.CYAN),
    PUDDLE(Entities.PUDDLE, ',', Color.BLUE),
    PLAYER(Entities.PLAYER, '@', Color.MAGENTA),
    SIGN(Entities.SIGN, '^', Color.YELLOW),
    TREE(Entities.TREE, '4', Color.GREEN),
    WALL(Entities.WALL, '#', Color.DARK_GRAY),
    WINDOW(Entities.WINDOW, '*', Color.LIGHT_GRAY);

    private static final Map<String, Tile> tilesByEntityName = new HashMap<>();

    static {
        for (Tile t : Tile.values()) {
            tilesByEntityName.put(t.getEntity().getName(), t);
        }
    }

    private final Entity entity;
    private final char glyph;
    // base color? We need a foreground/background?
    private final Color color;

    public Entity getEntity() {
        return entity;
    }

    public char getGlyph() {
        return glyph;
    }

    // Also need a getColor(float lightPct)?
    public Color getColor() {
        return color;
    }

    public static Tile get(Entity entity) {
        return tilesByEntityName.get(entity.getName());
    }

    private Tile(Entity entity, char glyph, Color color) {
        this.entity = entity.copy().build();
        this.glyph = glyph;
        this.color = color;
    }
}
