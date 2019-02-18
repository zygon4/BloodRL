/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.context.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.zygon.rl.core.model.Game;
import com.zygon.rl.core.model.Location;
import com.zygon.rl.core.view.GameComponent;

import java.util.function.Supplier;

/**
 *
 * TODO: add a label to be show along the top left border
 */
public final class BorderedComponent extends GameComponent {

    private static final char CORNER_GLYPH = ' ';

    private final BitmapFont font;
    private final Batch batch;

    private Location minPixels = null;
    private Location maxPixels = null;

    public BorderedComponent(Supplier<Game> gameSupplier, BitmapFont font, Batch batch) {
        super(null, gameSupplier);
        this.font = font;
        this.batch = batch;
    }

    public Location getMinPixelLocations() {
        return minPixels;
    }

    public Location getMaxPixelLocations() {
        return maxPixels;
    }

    @Override
    public void render(int xx, int yy, int maxWidth, int maxHeight) {
        float density = Gdx.graphics.getDensity();
        int fontSize = (int) (density * 12);
        int fontBuffer = fontSize + 12;
        int maxArrayY = maxHeight / fontBuffer;
        int maxArrayX = maxWidth / fontBuffer;

        int minX = 0;
        int minY = 0;
        int maxX = 0;
        int maxY = 0;

        Color origColor = new Color(font.getColor().r, font.getColor().g,
                font.getColor().b, font.getColor().a);
        // All borders are RED for now
        font.setColor(Color.RED);

        for (int y = 0; y < maxArrayY; y++) {
            for (int x = 0; x < maxArrayX; x++) {
                int pixelX = x * fontBuffer;
                int pixelY = y * fontBuffer;
                char glyph = ' ';
                // if x & y = 0, x & y = max,
                if (y == 0) {

                    minY = pixelY + fontBuffer;

                    if (x == 0) {
//                        glyph = '╚';
                        glyph = CORNER_GLYPH;
                    } else if (x == maxArrayX - 1) {
//                        glyph = '╝';
                        glyph = CORNER_GLYPH;
                    } else {
//                        glyph = '═';
                        glyph = '-';
                    }
                } else if (y == maxArrayY - 1) {

                    maxY = pixelY - fontBuffer;

                    if (x == 0) {
//                        glyph = '╔';
                        glyph = CORNER_GLYPH;
                    } else if (x == maxArrayX - 1) {
//                        glyph = '╗';
                        glyph = CORNER_GLYPH;
                    } else {
//                        glyph = '═';
                        glyph = '-';
                    }
                } else if (x == 0) {

                    minX = pixelX + fontBuffer;

                    if (y == 0) {
//                        glyph = '╚';
                        glyph = CORNER_GLYPH;
                    } else if (y == maxArrayY - 1) {
//                        glyph = '╝';
                        glyph = CORNER_GLYPH;
                    } else {
//                        glyph = '═';
                        glyph = '|';
                    }
                } else if (x == maxArrayX - 1) {

                    maxX = pixelX - fontBuffer;

                    if (y == 0) {
//                        glyph = '╔';
                        glyph = CORNER_GLYPH;
                    } else if (y == maxArrayY - 1) {
//                        glyph = '╗';
                        glyph = CORNER_GLYPH;
                    } else {
//                        glyph = '═';
                        glyph = '|';
                    }
                }

                String value = glyph + "";
                font.draw(batch, value, xx + 8 + pixelX, yy + 20 + pixelY);
            }
        }

        font.setColor(origColor);

        minPixels = Location.create(xx + 8 + minX, yy + 20 + minY);
        maxPixels = Location.create(maxX, maxY);
    }
}
