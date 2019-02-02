package com.zygon.rl.core.system;

import com.zygon.rl.core.model.Game;

import java.util.function.Function;

/**
 * This is just to start the conversation..
 */
public interface GameSystem {

    // More for logging than anything
    String getName();

    // TBD:
    Function<Game, Game> getGameUpdate();

    // TBD: where is state stored? in the game? In the "system"?
}
