/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.core.view;

import com.zygon.rl.core.model.Game;

import java.util.Objects;
import java.util.function.Supplier;

/**
 *
 * @author zygon
 */
public abstract class GameComponent extends ComponentBase {

    private final Supplier<Game> gameSupplier;

    public GameComponent(Style style, Supplier<Game> gameSupplier) {
        super(style);
        this.gameSupplier = Objects.requireNonNull(gameSupplier);
    }

    protected final Game getGame() {
        return gameSupplier.get();
    }
}
