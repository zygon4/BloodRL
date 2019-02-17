/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.context;

import com.badlogic.gdx.Game;
import com.zygon.rl.common.controller.OuterworldActionProvider;
import com.zygon.rl.common.controller.OuterworldGameActionProvider;
import com.zygon.rl.common.model.RegionHelper;
import com.zygon.rl.context.gdx.GDXInputAdapter;
import com.zygon.rl.core.model.Context;
import com.zygon.rl.core.model.Location;
import com.zygon.rl.core.model.Regions;

import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 *
 * @author zygon
 */
public class BloodGame extends Game {

    private final GameContext gameContext;
    private final Set<Integer> outerworldGameInputs;

    private GameScreen bloodGameScreen;
    private com.zygon.rl.core.model.Game game;

    public BloodGame(GameContext gameContext) {
        this.gameContext = gameContext;
        this.outerworldGameInputs = gameContext.getInitialInputSet() != null
                ? Collections.unmodifiableSet(gameContext.getInitialInputSet())
                : Collections.emptySet();
    }

    @Override
    public void create() {

        RegionHelper regionHelper = new RegionHelper();

        GDXInputAdapter outerworldGameInputAdapter = new GDXInputAdapter(outerworldGameInputs);

        Regions regions = Regions.create();

        for (int y = 0; y < 400; y += 20) {
            for (int x = 0; x < 400; x += 20) {
                Location loc = Location.create(x, y);
                boolean addPlayer = x == 200 && y == 200;
                regions = regions.add(regionHelper.generateCity(loc, 20, 20, addPlayer));
            }
        }

        game = com.zygon.rl.core.model.Game.builder()
                .setName(gameContext.getGameTitle())
                .setDescription(gameContext.getGameTitle())
                .setDisplayName(gameContext.getGameTitle())
                .addContext(Context.builder()
                        .setActionProvider(new OuterworldActionProvider())
                        .setGameActionProvider(new OuterworldGameActionProvider())
                        //  .addAttribute(attribute)
                        .setInputSupplier(outerworldGameInputAdapter)
                        .setName("outer")
                        .build())
                .setRegions(regions)
                .build();

        Supplier<com.zygon.rl.core.model.Game> getGame = () -> game;
        Consumer<com.zygon.rl.core.model.Game> setGame = (newGame) -> game = newGame;

        bloodGameScreen = new GameScreen(getGame, setGame);
        setScreen(bloodGameScreen);
    }
}
