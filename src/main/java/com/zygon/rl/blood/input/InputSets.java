package com.zygon.rl.blood.input;

import com.badlogic.gdx.Input;

import java.util.HashSet;
import java.util.Set;

/**
 * So the "blood" package (ie conceptually separate project space) now also
 * depends on gdx. This is not ideal, these keys should come from the core
 * system through another abstraction.
 */
public class InputSets {

    private InputSets() {
    }

    private static final Set<Integer> OUTERWORLD_GAME_INPUTS = new HashSet<>();
    private static final Set<Integer> GAME_DIRECTION_INPUTS = new HashSet<>();

    static {
        // examine
        OUTERWORLD_GAME_INPUTS.add(Input.Keys.E);
        // close
        OUTERWORLD_GAME_INPUTS.add(Input.Keys.C);
        // open
        OUTERWORLD_GAME_INPUTS.add(Input.Keys.O);
        // quit
        OUTERWORLD_GAME_INPUTS.add(Input.Keys.ESCAPE);

        // movement
        GAME_DIRECTION_INPUTS.add(Input.Keys.H);
        GAME_DIRECTION_INPUTS.add(Input.Keys.J);
        GAME_DIRECTION_INPUTS.add(Input.Keys.K);
        GAME_DIRECTION_INPUTS.add(Input.Keys.L);

        GAME_DIRECTION_INPUTS.add(Input.Keys.NUMPAD_7);
        GAME_DIRECTION_INPUTS.add(Input.Keys.NUMPAD_8);
        GAME_DIRECTION_INPUTS.add(Input.Keys.NUMPAD_9);
        GAME_DIRECTION_INPUTS.add(Input.Keys.NUMPAD_4);
        GAME_DIRECTION_INPUTS.add(Input.Keys.NUMPAD_5);
        GAME_DIRECTION_INPUTS.add(Input.Keys.NUMPAD_6);
        GAME_DIRECTION_INPUTS.add(Input.Keys.NUMPAD_1);
        GAME_DIRECTION_INPUTS.add(Input.Keys.NUMPAD_2);
        GAME_DIRECTION_INPUTS.add(Input.Keys.NUMPAD_3);

        // same as '5', for same location
        GAME_DIRECTION_INPUTS.add(Input.Keys.PERIOD);
    }

    public static Set<Integer> getOuterworldGameInputs() {
        return OUTERWORLD_GAME_INPUTS;
    }

    public static Set<Integer> getGameDirectionInputs() {
        return GAME_DIRECTION_INPUTS;
    }
}
