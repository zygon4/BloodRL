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
public class RowComponent implements Component {

    private final Component top;
    private final Component bottom;
    private final double bottomPct;

    /**
     *
     * @param top
     * @param bottom
     * @param bottomPct
     */
    public RowComponent(Component top, Component bottom, double bottomPct) {
        this.top = top;
        this.bottom = bottom;
        if (bottomPct <= 0.0 || bottomPct > 1.0) {
            throw new IllegalArgumentException("bottomPct must between 0 and 1.0");
        }
        this.bottomPct = bottomPct;
    }

    @Override
    public void render(int x, int y, int width, int height) {

        int totalWidth = width;
        int totalHeight = height;

        double yMargin = totalHeight * bottomPct;

        bottom.render(x, y, totalWidth, (int) yMargin);
        top.render(x, y + (int) yMargin, totalWidth, totalHeight - (int) yMargin);
    }
}
