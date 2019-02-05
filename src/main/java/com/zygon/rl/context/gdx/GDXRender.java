/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.context.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
import com.zygon.rl.core.view.HBFComponent;

import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 *
 * @author zygon
 */
public class GDXRender implements GameRenderer {

    private final SpriteBatch spriteBatch;
    private final OrthographicCamera camera;
    private final BitmapFont font = new BitmapFont();
    private final Component gameComponent;

    public GDXRender(Supplier<Game> gameSupplier, SpriteBatch spriteBatch, OrthographicCamera camera) {
        this.spriteBatch = spriteBatch;
        this.camera = camera;

        // TODO: move these, need a more specific text area
        Component header = new TextComponent(gameSupplier, (g, w, h) -> {
            return g.getDisplayName() + "| " + g.getDate();
        }, font, spriteBatch);

        Component body = new GDXRegionRenderer(gameSupplier,
                () -> gameSupplier.get().getRegions().find(Entities.PLAYER).stream().findAny().get(),
                font, spriteBatch, camera);

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

        Component hbfComponent = new HBFComponent(header, body, footer);

        gameComponent = new ColumnComponent(
                hbfComponent,
                new TextComponent(gameSupplier, (g, w, h) -> {
                    // TODO: convert width (pixels) to a number of spaces to add as buffer
                    String log = g.getLog().stream()
                            .map(l -> String.format("%-" + (l.length() > w ? l : w - l.length()) + "s", l))
                            .collect(Collectors.joining());
                    return log;
                }, font, spriteBatch),
                .80);
    }

    @Override
    public void render(Game game) {

        spriteBatch.setColor(Color.BLACK);

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        // TBD: need to "dispose"?
        font.setColor(Color.RED);

        gameComponent.render(0, 0, (int) w, (int) h);
    }
}
