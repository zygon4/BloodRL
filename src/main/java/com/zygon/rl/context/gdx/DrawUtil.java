/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.context.gdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import java.util.Optional;

/**
 *
 * @author zygon
 */
class DrawUtil {

    private DrawUtil() {
    }

    public static void draw(BitmapFont font, Color fontColor, Optional<Color> backgroundColor,
            SpriteBatch spriteBatch, int x, int y, String displayString) {

        Pixmap labelColor = null;

        // TBD if these color changing shenanigans are all needed
        Color originalColor = new Color(font.getColor());
        font.setColor(fontColor);
        try {
            Label.LabelStyle style = new Label.LabelStyle(font, font.getColor());
            Label label = new Label(displayString, style);
            label.setWrap(true);
            label.setX(x);
            label.setY(y);

            if (backgroundColor.isPresent()) {
                labelColor = new Pixmap((int) label.getWidth(), (int) label.getHeight(), Pixmap.Format.RGB888);
                labelColor.setColor(backgroundColor.get());
                labelColor.fill();
                label.getStyle().background = new Image(new Texture(labelColor)).getDrawable();
            }

            label.draw(spriteBatch, 1);
        } finally {
            font.setColor(originalColor);
            if (labelColor != null) {
                labelColor.dispose();
            }
        }
    }
}
