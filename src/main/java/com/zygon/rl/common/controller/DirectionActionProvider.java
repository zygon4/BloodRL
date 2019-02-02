/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.common.controller;

import com.zygon.rl.core.model.Action;
import com.zygon.rl.core.model.Direction;
import com.zygon.rl.core.model.Input;

import java.util.function.Function;

/**
 *
 * is this common enough?
 */
public class DirectionActionProvider implements Function<Input, Action> {

    @Override
    public Action apply(Input i) {
        Action.Builder builder = null;

        // TODO: add all locations
        switch (i.getInput()) {
            case com.badlogic.gdx.Input.Keys.H:
            case com.badlogic.gdx.Input.Keys.NUMPAD_4:
                builder = Action.builder();
                setDirectionFields(builder, Direction.WEST.name());
                break;
            case com.badlogic.gdx.Input.Keys.J:
            case com.badlogic.gdx.Input.Keys.NUMPAD_2:
                builder = Action.builder();
                setDirectionFields(builder, Direction.NORTH.name());
                break;
            case com.badlogic.gdx.Input.Keys.K:
            case com.badlogic.gdx.Input.Keys.NUMPAD_8:
                builder = Action.builder();
                setDirectionFields(builder, Direction.SOUTH.name());
                break;
            case com.badlogic.gdx.Input.Keys.L:
            case com.badlogic.gdx.Input.Keys.NUMPAD_6:
                builder = Action.builder();
                setDirectionFields(builder, Direction.EAST.name());
                break;
            case com.badlogic.gdx.Input.Keys.Q:
                builder = Action.builder();
                builder.setDescription("Cancel")
                        .setDisplayName("Cancel")
                        .setName("CANCEL")
                        .setValue("CANCEL");
                break;
            default:
                // TBD: input exception?
                break;
        }
        return builder != null ? builder.build() : null;
    }

    private static Action.Builder setDirectionFields(Action.Builder builder, String direction) {
        return builder
                .setDescription("Direction " + direction)
                .setDisplayName(direction)
                .setName("DIRECTION")
                .setValue(direction);
    }
}
