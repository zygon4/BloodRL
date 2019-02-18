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

    FLOOR(Entities.FLOOR, (e) -> '.', Color.YELLOW),
    DIRT(Entities.DIRT, (e) -> '.', new Color(109, 57, 4, 1), Color.RED),
    DOOR(Entities.DOOR, (t) -> {
        Openable openable = new Openable(t);
        return openable.isClosed() ? '+' : '\'';
    }, Color.ORANGE),
    GRASS(Entities.GRASS, (e) -> '.', Color.GREEN),
    MONSTER(Entities.MONSTER, (e) -> 'm', Color.CYAN),
    PUDDLE(Entities.PUDDLE, (e) -> ',', Color.BLUE),
    PLAYER(Entities.PLAYER, (e) -> '@', Color.MAGENTA),
    SIGN(Entities.SIGN, (e) -> '^', new Color(109, 57, 4, 1), Color.YELLOW),
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
    private final Color backgroundColor;
    private final Color foregroundColor;

    public Entity getEntity() {
        return entity;
    }

    public char getGlyph(Entity entity) {
        return getGlyphFn.apply(entity);
    }

    // TODO: this is too static, needs to be based on entities at the location,
    // a monster doesn't know it's background color
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    // Also need a getColor(float lightPct)?
    public Color getForegroundColor() {
        return foregroundColor;
    }

    public static Tile get(Entity entity) {
        return tilesByEntityName.get(entity.getName());
    }

    private Tile(Entity entity, Function<Entity, Character> getGlyphFn,
            Color backgroundColor, Color foregroundColor) {
        this.entity = entity.copy().build();
        this.getGlyphFn = getGlyphFn;
        this.backgroundColor = backgroundColor;
        this.foregroundColor = foregroundColor;
    }

    private Tile(Entity entity, Function<Entity, Character> getGlyphFn, Color foregroundColor) {
        this(entity, getGlyphFn, foregroundColor, foregroundColor);
    }
}
