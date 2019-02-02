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
public interface Component {

    /**
     * Renders the component starting at the absolute pixel x and pixel y and
     * going to the max of the height, width.
     *
     * @param x
     * @param y
     * @param maxWidth
     * @param maxHeight
     */
    void render(int x, int y, int maxWidth, int maxHeight);
}
