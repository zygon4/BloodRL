package com.zygon.rl.common.controller;

import com.zygon.rl.core.model.Action;
import com.zygon.rl.core.model.Direction;
import com.zygon.rl.core.model.Input;

import java.util.function.Function;

/**
 *
 * @author zygon
 */
public class OuterworldActionProvider implements Function<Input, Action> {

    @Override
    public Action apply(Input i) {
        Action.Builder builder = null;

        switch (i.getInput()) {
            case com.badlogic.gdx.Input.Keys.NUMPAD_7:
                builder = Action.builder();
                setMovementFields(builder, Direction.NORTHWEST.name());
                break;
            case com.badlogic.gdx.Input.Keys.NUMPAD_9:
                builder = Action.builder();
                setMovementFields(builder, Direction.NORTHEAST.name());
                break;
            case com.badlogic.gdx.Input.Keys.NUMPAD_1:
                builder = Action.builder();
                setMovementFields(builder, Direction.SOUTHWEST.name());
                break;
            case com.badlogic.gdx.Input.Keys.NUMPAD_3:
                builder = Action.builder();
                setMovementFields(builder, Direction.SOUTHEAST.name());
                break;
            case com.badlogic.gdx.Input.Keys.H:
            case com.badlogic.gdx.Input.Keys.NUMPAD_4:
                builder = Action.builder();
                setMovementFields(builder, Direction.WEST.name());
                break;
            case com.badlogic.gdx.Input.Keys.J:
            case com.badlogic.gdx.Input.Keys.NUMPAD_2:
                builder = Action.builder();
                setMovementFields(builder, Direction.NORTH.name());
                break;
            case com.badlogic.gdx.Input.Keys.K:
            case com.badlogic.gdx.Input.Keys.NUMPAD_8:
                builder = Action.builder();
                setMovementFields(builder, Direction.SOUTH.name());
                break;
            case com.badlogic.gdx.Input.Keys.L:
            case com.badlogic.gdx.Input.Keys.NUMPAD_6:
                builder = Action.builder();
                setMovementFields(builder, Direction.EAST.name());
                break;
            case com.badlogic.gdx.Input.Keys.C:
                builder = Action.builder();
                builder.setDescription("Close")
                        .setDisplayName("Close")
                        .setName("CLOSE")
                        .setValue("CLOSE");
                break;
            case com.badlogic.gdx.Input.Keys.O:
                builder = Action.builder();
                builder.setDescription("Open")
                        .setDisplayName("Open")
                        .setName("OPEN")
                        .setValue("OPEN");
                break;
            case com.badlogic.gdx.Input.Keys.ESCAPE:
                builder = Action.builder();
                builder.setDescription("Quit")
                        .setDisplayName("Quit")
                        .setName("QUIT")
                        .setValue("QUIT");
                break;
            case com.badlogic.gdx.Input.Keys.E:
                builder = Action.builder();
                builder.setDescription("Examine")
                        .setDisplayName("Examine")
                        .setName("EXAMINE")
                        .setValue("EXAMINE");
                break;
            default:
                // TBD: input exception?
                break;
        }
        return builder != null ? builder.build() : null;
    }

    private static Action.Builder setMovementFields(Action.Builder builder, String direction) {
        return builder
                .setDescription("Movement " + direction)
                .setDisplayName(direction)
                .setName("MOVE")
                .setValue(direction);
    }
}
