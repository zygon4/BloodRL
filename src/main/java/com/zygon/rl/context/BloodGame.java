/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.context;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.zygon.rl.common.controller.OuterworldActionProvider;
import com.zygon.rl.common.controller.OuterworldGameActionProvider;
import com.zygon.rl.common.model.RegionHelper;
import com.zygon.rl.context.gdx.GDXInputAdapter;
import com.zygon.rl.core.model.Context;
import com.zygon.rl.core.model.Location;
import com.zygon.rl.core.model.Regions;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 *
 * @author zygon
 */
public class BloodGame extends Game {

    private BloodGameScreen feedGameScreen;
    private com.zygon.rl.core.model.Game game;

    @Override
    public void create() {

        RegionHelper regionHelper = new RegionHelper();

        Set<Integer> gameInputs = new HashSet<>();

        // examine
        gameInputs.add(Keys.E);
        // close
        gameInputs.add(Keys.C);
        // open
        gameInputs.add(Keys.O);
        // quit
        gameInputs.add(Keys.ESCAPE);
        // movement
        gameInputs.add(Keys.H);
        gameInputs.add(Keys.J);
        gameInputs.add(Keys.K);
        gameInputs.add(Keys.L);

        gameInputs.add(Input.Keys.NUMPAD_7);
        gameInputs.add(Input.Keys.NUMPAD_8);
        gameInputs.add(Input.Keys.NUMPAD_9);
        gameInputs.add(Input.Keys.NUMPAD_4);
        // 5??
        gameInputs.add(Input.Keys.NUMPAD_6);
        gameInputs.add(Input.Keys.NUMPAD_1);
        gameInputs.add(Input.Keys.NUMPAD_2);
        gameInputs.add(Input.Keys.NUMPAD_3);

        GDXInputAdapter outerworldGameInputAdapter = new GDXInputAdapter(gameInputs);

        Regions regions = Regions.create();

        for (int y = 0; y < 400; y += 20) {
            for (int x = 0; x < 400; x += 20) {
                Location loc = Location.create(x, y);
                boolean addPlayer = x == 200 && y == 200;
                regions = regions.add(regionHelper.generateCity(loc, 20, 20, addPlayer));
            }
        }

        game = com.zygon.rl.core.model.Game.builder()
                .setName("Blood")
                .setDescription("Blood")
                .setDisplayName("Blood")
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

        feedGameScreen = new BloodGameScreen(getGame, setGame);
        setScreen(feedGameScreen);
    }
}
