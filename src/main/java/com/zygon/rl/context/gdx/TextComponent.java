/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.context.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.zygon.rl.core.model.Game;
import com.zygon.rl.core.view.Style;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 *
 * @author zygon
 */
final class TextComponent extends GDXComponent {

    // TODO: probably needs to be a bifunction, so the implementor can size the text
    private final Function<Game, String> getDisplayString;

    // all text is bordered for now
    public TextComponent(Supplier<Game> gameSupplier, Function<Game, String> getDisplayString,
            BitmapFont font, SpriteBatch spriteBatch) {
        super(Style.BORDERED, Color.WHITE, gameSupplier, font, spriteBatch);
        this.getDisplayString = getDisplayString;
    }

    // TODO: this is experimental
    private boolean tint = false;
    private final Color color = new Color(Color.CYAN);
    // TODO: this is experimental

    @Override
    public void renderComponent(int x, int y, int maxWidth, int maxHeight) {

        float density = Gdx.graphics.getDensity();
        int fontSize = (int) (density * 12);
        int fontBuffer = fontSize + 12;

        String displayString = getDisplayString.apply(getGame());
        int idx = 0;

        // TODO: this is experimental
        if (tint) {
            color.set(color.r, color.g, color.b + 50, color.a);
        } else {
            color.set(color.r, color.g, color.b - 50, color.a);
        }

        tint = !tint;
        // TODO: this is experimental

        // TODO: wrapping, alignment, newlines
        Color originalColor = new Color(getFont().getColor());
        getFont().setColor(color);
        try {
            for (int i = 0; i < displayString.length(); i++) {
                String character = displayString.charAt(idx++) + "";
                getFont().draw(getSpriteBatch(), character, x + (idx * fontBuffer), y);
            }
        } finally {
            getFont().setColor(originalColor);
        }
    }
}
