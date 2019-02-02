/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.core.controller;

import com.zygon.rl.core.model.Action;
import com.zygon.rl.core.model.Game;
import com.zygon.rl.core.model.Input;

/**
 *
 * @author zygon
 */
public interface GameController {

    Action convertInput(Game game, Input input);

    Game handleAction(Game game, Action action);

    Game handleInvalidInput(Game game, Input input);
}
