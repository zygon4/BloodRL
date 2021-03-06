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
public class BooleanAttribute extends Attribute {

    private final boolean value;

    private BooleanAttribute(Attribute attribute) {
        super(attribute.copy());
        this.value = Boolean.valueOf(attribute.getValue());
    }

    public static BooleanAttribute create(Attribute attribute) {
        return new BooleanAttribute(attribute);
    }

    public boolean getBooleanValue() {
        return value;
    }
}
