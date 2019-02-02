/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.common.controller;

import com.zygon.rl.core.controller.GameController;
import com.zygon.rl.core.model.Action;
import com.zygon.rl.core.model.Game;
import com.zygon.rl.core.model.Input;
import com.zygon.rl.core.view.GameRenderer;

import java.util.function.Supplier;

/**
 *
 * @author zygon
 */
@Deprecated
public class MainLoop {

    private final Game game;
    private final Supplier<Input> inputSupplier;
    private final GameController gameController;
    private final GameRenderer gameRenderer;
    private boolean running = true;

    public MainLoop(Game game, Supplier<Input> inputSupplier, GameController gameController, GameRenderer gameRenderer) {
        this.game = game;
        this.inputSupplier = inputSupplier;
        this.gameController = gameController;
        this.gameRenderer = gameRenderer;
    }

    public void init() {
        // TBD: set up shutdown hooks if possible?
    }

    public void runLoop() {

        // Render the initial game state
        gameRenderer.render(game);

        Game lastGame = game;
        while (running) {
            // Get input
            Input input = inputSupplier.get();
            if (input != null) {
                Action action = gameController.convertInput(lastGame, input);
                if (action == null) {
                    lastGame = gameController.handleInvalidInput(lastGame, input);
                } else {
                    lastGame = gameController.handleAction(lastGame, action);
                }
                gameRenderer.render(lastGame);
            }
        }
    }

    public void shutdown() {
        running = false;
    }
}
