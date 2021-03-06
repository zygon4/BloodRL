package com.zygon.rl.context;

import com.badlogic.gdx.Game;
import com.zygon.rl.common.controller.OuterworldActionProvider;
import com.zygon.rl.common.controller.OuterworldGameActionProvider;
import com.zygon.rl.common.model.Entities;
import com.zygon.rl.common.model.RegionHelper;
import com.zygon.rl.context.gdx.GDXInputAdapter;
import com.zygon.rl.core.model.Context;
import com.zygon.rl.core.model.Location;
import com.zygon.rl.core.model.Regions;

import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

        // This creates the initial map
        // TBD: I think when the world goes fullscreen that it messes with the "grow" algorithm
        // which is causing a crash/NPE
        for (int y = 0; y < 400; y += 20) {
            for (int x = 0; x < 400; x += 20) {
                Location loc = Location.create(x, y);
                boolean addPlayer = x == 200 && y == 200;
                regions = regions.add(regionHelper.generateRegion(loc, 20, 20, addPlayer));
            }
        }

        // TBD: this is a mild hack, removing the extra player entities after
        // all the initial regions are generated.
        Set<Location> removePlayers = regions.find(Entities.PLAYER).stream()
                .skip(1)
                .collect(Collectors.toSet());
        for (Location removePlayerLoc : removePlayers) {
            regions = regions.remove(Entities.PLAYER, removePlayerLoc);
        }

        if (regions.find(Entities.PLAYER).isEmpty()) {
            throw new RuntimeException("No player generated");
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
