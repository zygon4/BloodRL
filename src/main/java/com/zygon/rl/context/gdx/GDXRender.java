package com.zygon.rl.context.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.zygon.rl.common.model.Entities;
import com.zygon.rl.core.model.Entity;
import com.zygon.rl.core.model.Game;
import com.zygon.rl.core.model.Location;
import com.zygon.rl.core.model.Regions;
import com.zygon.rl.core.view.ColumnComponent;
import com.zygon.rl.core.view.Component;
import com.zygon.rl.core.view.GameRenderer;
import com.zygon.rl.core.view.RowComponent;

import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 *
 * @author zygon
 */
public class GDXRender implements GameRenderer {

    private final BitmapFont font = new BitmapFont();
    private final Component gameComponent;

    public GDXRender(Supplier<Game> gameSupplier, SpriteBatch spriteBatch, OrthographicCamera camera) {
        // TODO: move these, need a more specific text area
        Component header = new TextComponent(gameSupplier, (g, w, h) -> {
            return g.getDisplayName() + "| " + g.getDate();
        }, font, spriteBatch);

        Component body = new GDXRegionRenderer(gameSupplier,
                () -> gameSupplier.get().getRegions().find(Entities.PLAYER).stream().findAny().get(),
                font, spriteBatch);

        Component footer = new TextComponent(gameSupplier, (g, w, h) -> {
            // TBD: don't like having this implementation here
            Regions regions = g.getRegions();
            Location playerLocation = regions.find(Entities.PLAYER).stream()
                    .findAny().get();

            Entity playerLocEntity = regions.get(playerLocation).stream()
                    .filter(e -> !e.getName().equals("PLAYER"))
                    .findFirst().orElse(null);

            String contextDisplay = g.getContext().getDisplayName() != null
                    ? g.getContext().getDisplayName() : g.getContext().getName();
            return playerLocation.toString() + " - " + playerLocEntity.getDisplayName() + " - " + contextDisplay;
        }, font, spriteBatch);

        Component headerBody = new RowComponent(header, body, 0.85);
        Component mainScreen = new RowComponent(headerBody, footer, 0.10);

        Component sideBar = new TextComponent(gameSupplier, (g, w, h) -> {
            // TODO: convert width (pixels) to a number of spaces to add as buffer
            String log = g.getLog().stream()
                    .map(l -> String.format("%-" + (l.length() > w ? l : w - l.length()) + "s", l))
                    .collect(Collectors.joining());
            return log;
        }, font, spriteBatch);

        gameComponent = new ColumnComponent(mainScreen, sideBar, .80);
    }

    @Override
    public void render(Game game) {

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        gameComponent.render(0, 0, (int) w, (int) h);
    }
}
