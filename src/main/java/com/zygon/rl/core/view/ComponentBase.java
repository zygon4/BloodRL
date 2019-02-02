/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.core.view;

/**
 *
 * @author zygon
 */
public abstract class ComponentBase implements Component {

    private final Style style;

    public ComponentBase(Style style) {
        this.style = style;
    }

    public Style getStyle() {
        return style;
    }
}
