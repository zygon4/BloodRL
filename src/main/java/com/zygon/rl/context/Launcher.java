/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.context;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

/**
 *
 * TBD: logging
 */
public class Launcher {

    private final GameContext gameContext;

    public Launcher(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    public void run() {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = gameContext.getGameTitle();
        config.width = gameContext.getInitialWidth();
        config.height = gameContext.getInitialHeight();

        Game game = new BloodGame(gameContext);

        // Construction launches game
        new LwjglApplication(game, config);
    }
}
