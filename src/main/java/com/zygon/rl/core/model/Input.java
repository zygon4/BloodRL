/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.core.model;

/**
 *
 * @author zygon
 */
public class Input {

    public static Input getUnknown() {
        return new Input(-1);
    }

    // So input shouldn't have a character, it should TAKE a
    // character(for example) and turn it into a logical input
    private final int input;

    public Input(int character) {
        this.input = character;
    }

    public char getCharacter() {
        return (char) input;
    }

    public int getInput() {
        return input;
    }

    @Override
    public String toString() {
        return "" + getCharacter();
    }
}
