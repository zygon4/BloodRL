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
public class ColumnComponent implements Component {

    private final Component left;
    private final Component right;
    private final double leftPct;

    /**
     *
     * @param left
     * @param right
     * @param leftPct
     */
    public ColumnComponent(Component left, Component right, double leftPct) {
        this.left = left;
        this.right = right;
        if (leftPct <= 0.0 || leftPct > 1.0) {
            throw new IllegalArgumentException("leftPct must between 0 and 1.0");
        }
        this.leftPct = leftPct;
    }

    @Override
    public void render(int x, int y, int width, int height) {

        int totalWidth = width;
        int totalHeight = height;

        double xMargin = totalWidth * leftPct;

        left.render(x, y, (int) xMargin, totalHeight);
        right.render(x + (int) xMargin, y, totalWidth - (int) xMargin, totalHeight);
    }
}
