/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.common.controller;

import com.zygon.rl.core.controller.GameController;
import com.zygon.rl.core.model.Action;
import com.zygon.rl.core.model.Attribute;
import com.zygon.rl.core.model.Context;
import com.zygon.rl.core.model.Game;
import com.zygon.rl.core.model.Input;

/**
 *
 * @author zygon
 */
public class GameControllerImpl implements GameController {

    @Override
    public Action convertInput(Game game, Input input) {
        return game.match(input);
    }

    @Override
    public Game handleAction(Game game, Action action) {
        // maybe basic logic?
        // input is of player context
        // if no game context or context=map, then player movement
        // if context is menu (dialogue/fight/inventory), then input to that
        // For now no menus, just handle movement, then simple behaviors

        return game.handleAction(action);
    }

    @Override
    public Game handleInvalidInput(Game game, Input input) {
        // TODO: handle
        Context ctx = game.getContext();
        ctx = ctx.copy().addAttribute(Attribute.builder()
                .setName("ERROR")
                .setValue("" + input.getCharacter())
                .build()).build();

        return game.setLeafContext(ctx);
    }
}
