/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

    private static final Set<Integer> outerworldGameInputs = new HashSet<>();
    private static final Set<Integer> gameDirectionInputs = new HashSet<>();

    static {
        // examine
        outerworldGameInputs.add(Input.Keys.E);
        // close
        outerworldGameInputs.add(Input.Keys.C);
        // open
        outerworldGameInputs.add(Input.Keys.O);
        // quit
        outerworldGameInputs.add(Input.Keys.ESCAPE);

        // movement
        gameDirectionInputs.add(Input.Keys.H);
        gameDirectionInputs.add(Input.Keys.J);
        gameDirectionInputs.add(Input.Keys.K);
        gameDirectionInputs.add(Input.Keys.L);

        gameDirectionInputs.add(Input.Keys.NUMPAD_7);
        gameDirectionInputs.add(Input.Keys.NUMPAD_8);
        gameDirectionInputs.add(Input.Keys.NUMPAD_9);
        gameDirectionInputs.add(Input.Keys.NUMPAD_4);
        // 5??
        gameDirectionInputs.add(Input.Keys.NUMPAD_6);
        gameDirectionInputs.add(Input.Keys.NUMPAD_1);
        gameDirectionInputs.add(Input.Keys.NUMPAD_2);
        gameDirectionInputs.add(Input.Keys.NUMPAD_3);
    }

    public static Set<Integer> getOuterworldGameInputs() {
        return outerworldGameInputs;
    }

    public static Set<Integer> getGameDirectionInputs() {
        return gameDirectionInputs;
    }
}
