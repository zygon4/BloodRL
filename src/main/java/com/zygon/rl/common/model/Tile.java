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
import java.util.function.Function;

/**
 *
 * @author zygon
 */
public enum Tile {

    // TODO: need to inject conditional logic to provide a door glyph/color
    // based on door attributes and game status (e.g. light/dark)
    FLOOR(Entities.FLOOR, (e) -> '.', Color.YELLOW),
    DIRT(Entities.DIRT, (e) -> '.', Color.LIGHT_GRAY),
    DOOR(Entities.DOOR, (t) -> {
        Openable openable = new Openable(t);
        return openable.isClosed() ? '+' : '\\';
    }, Color.ORANGE),
    GRASS(Entities.GRASS, (e) -> '"', Color.GREEN),
    MONSTER(Entities.MONSTER, (e) -> 'm', Color.CYAN),
    PUDDLE(Entities.PUDDLE, (e) -> ',', Color.BLUE),
    PLAYER(Entities.PLAYER, (e) -> '@', Color.MAGENTA),
    SIGN(Entities.SIGN, (e) -> '^', Color.YELLOW),
    TREE(Entities.TREE, (e) -> '4', Color.GREEN),
    WALL(Entities.WALL, (e) -> '#', Color.DARK_GRAY),
    WINDOW(Entities.WINDOW, (t) -> {
        Openable openable = new Openable(t);
        return openable.isClosed() ? '*' : '/';
    }, Color.LIGHT_GRAY);

    private static final Map<String, Tile> tilesByEntityName = new HashMap<>();

    static {
        for (Tile t : Tile.values()) {
            tilesByEntityName.put(t.getEntity().getName(), t);
        }
    }

    private final Entity entity;
    private final Function<Entity, Character> getGlyphFn;

    // base color? We need a foreground/background?
    private final Color color;

    public Entity getEntity() {
        return entity;
    }

    public char getGlyph(Entity entity) {
        return getGlyphFn.apply(entity);
    }

    // Also need a getColor(float lightPct)?
    public Color getColor() {
        return color;
    }

    public static Tile get(Entity entity) {
        return tilesByEntityName.get(entity.getName());
    }

    private Tile(Entity entity, Function<Entity, Character> getGlyphFn, Color color) {
        this.entity = entity.copy().build();
        this.getGlyphFn = getGlyphFn;
        this.color = color;
    }
}
