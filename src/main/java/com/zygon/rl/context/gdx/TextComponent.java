/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.context.gdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.zygon.rl.core.model.Game;
import com.zygon.rl.core.view.Style;

import java.util.function.Supplier;

/**
 *
 * @author zygon
 */
final class TextComponent extends GDXComponent {

    private final TriFunction<Game, Integer, Integer, String> getDisplayString;

    // all text is bordered for now
    public TextComponent(Supplier<Game> gameSupplier, TriFunction<Game, Integer, Integer, String> getDisplayString,
            BitmapFont font, SpriteBatch spriteBatch) {
        super(Style.BORDERED, Color.WHITE, gameSupplier, font, spriteBatch);
        this.getDisplayString = getDisplayString;
    }

    private final Color color = new Color(Color.CYAN);

    @Override
    public void renderComponent(int x, int y, int maxWidth, int maxHeight) {

        String displayString = getDisplayString.apply(getGame(), maxWidth, maxHeight);

        Color originalColor = new Color(getFont().getColor());
        getFont().setColor(color);
        try {
            LabelStyle style = new Label.LabelStyle(getFont(), getFont().getColor());
            Label label = new Label(displayString, style);
            label.setWrap(true);
            label.setWidth(maxWidth);
            label.setX(x);
            label.setY(y + maxHeight - (getFont().getLineHeight() * 2));
            label.draw(getSpriteBatch(), 1);
        } finally {
            getFont().setColor(originalColor);
        }
    }
}
