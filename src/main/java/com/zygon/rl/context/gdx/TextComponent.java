/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.context.gdx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.zygon.rl.core.model.Game;
import com.zygon.rl.core.view.Style;

import java.util.Optional;
import java.util.function.Supplier;

/**
 *
 * @author zygon
 */
final class TextComponent extends GDXComponent {

    // Should be configurable
    private final Color color = new Color(Color.CYAN);
    private final TriFunction<Game, Integer, Integer, String> getDisplayString;

    // all text is bordered for now
    public TextComponent(Supplier<Game> gameSupplier, TriFunction<Game, Integer, Integer, String> getDisplayString,
            BitmapFont font, SpriteBatch spriteBatch) {
        super(Style.BORDERED, Color.WHITE, gameSupplier, font, spriteBatch);
        this.getDisplayString = getDisplayString;
    }

    @Override
    public void renderComponent(int x, int y, int maxWidth, int maxHeight) {

        String displayString = getDisplayString.apply(getGame(), maxWidth, maxHeight);

        DrawUtil.draw(getFont(), color, Optional.of(Color.FOREST), getBatch(),
                //x, (int) (y + maxHeight - (getFont().getLineHeight() * 2)),
                x, y,
                maxWidth, maxHeight, displayString);
    }
}
