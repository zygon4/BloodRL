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
public class HBFComponent implements Component {

    private final Component header;
    private final Component body;
    private final Component footer;

    public HBFComponent(Component header, Component content, Component footer) {
        this.header = header;
        this.body = content;
        this.footer = footer;
    }

    @Override
    public void render(int x, int y, int width, int height) {

        // Enforcing arbitrary HBF ratios
        int totalWidth = width;
        int totalHeight = height;

        double yMargin = totalHeight * 0.15;
        // Inclusive
        double absoluteYHeaderMax = totalHeight - yMargin;

        double absoluteYBodyMin = absoluteYHeaderMax - yMargin;

        double xMargin = totalWidth * 0.15;
        // Exclusive
        double absoluteXHeaderMax = xMargin;
        // Inclusive
        double absoluteXFooterMax = totalWidth - absoluteXHeaderMax;

        // Needs:
        header.render(x, (int) (y + absoluteYHeaderMax), width, (int) yMargin);
        body.render(x, (int) (y + yMargin), width, (int) absoluteYBodyMin);
        footer.render(x, y, width, (int) yMargin);
    }
}
