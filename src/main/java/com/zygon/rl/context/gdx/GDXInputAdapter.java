/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.context.gdx;

import com.badlogic.gdx.Gdx;
import com.zygon.rl.core.model.Input;

import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;

/**
 *
 * @author zygon
 */
public class GDXInputAdapter implements Supplier<Input> {

    private final Set<Integer> keyDown;

    public GDXInputAdapter(Set<Integer> keyDown) {
        this.keyDown = keyDown != null
                ? Collections.unmodifiableSet(keyDown) : Collections.emptySet();
    }

    @Override
    public Input get() {
        // TBD: this approach (vs explicit input adapter) is probably slow
        for (int key : this.keyDown) {
            if (Gdx.input.isKeyJustPressed(key)) {
                Gdx.app.log("Input", "key down: " + key);
                return new Input(key);
            }
        }
        return Input.getUnknown();
    }
}
