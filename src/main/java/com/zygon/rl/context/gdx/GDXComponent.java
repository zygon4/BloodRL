/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.context.gdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.zygon.rl.core.model.Game;
import com.zygon.rl.core.model.Location;
import com.zygon.rl.core.view.GameComponent;
import com.zygon.rl.core.view.Style;

import java.util.function.Supplier;

/**
 *
 * @author zygon
 */
public abstract class GDXComponent extends GameComponent {

    // libgdx color so don't push up the chain
    private final Color backgroundColor;
    private final Supplier<Game> gameSupplier;
    private final BitmapFont font;
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private final SpriteBatch spriteBatch;

    public GDXComponent(Style style, Color color, Supplier<Game> gameSupplier,
            BitmapFont font, SpriteBatch spriteBatch) {
        super(style, gameSupplier);
        this.backgroundColor = color;
        this.gameSupplier = gameSupplier;
        this.font = font;
        this.spriteBatch = spriteBatch;
    }

    protected final BitmapFont getFont() {
        return font;
    }

    protected final ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }

    protected final SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    @Override
    public final void render(int x, int y, int maxWidth, int maxHeight) {

        Location minPixels = null;
        Location maxPixels = null;

        if (getStyle() == Style.BORDERED) {
            BorderedComponent bordered = new BorderedComponent(gameSupplier, font, spriteBatch);
            bordered.render(x, y, maxWidth, maxHeight);

            minPixels = bordered.getMinPixelLocations();
            maxPixels = bordered.getMaxPixelLocations();
        }

        if (minPixels == null) {
            renderComponent(x, y, maxWidth, maxHeight);
        } else {
            renderComponent(minPixels.getX(), minPixels.getY(), maxPixels.getX(), maxPixels.getY());
        }
    }

    protected abstract void renderComponent(int x, int y, int maxWidth, int maxHeight);
}
