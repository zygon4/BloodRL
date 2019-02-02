/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.context.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.zygon.rl.common.model.CommonAttributes;
import com.zygon.rl.common.model.Tile;
import com.zygon.rl.common.view.FOVHelper;
import com.zygon.rl.common.view.RegionView;
import com.zygon.rl.core.model.Attribute;
import com.zygon.rl.core.model.DoubleAttribute;
import com.zygon.rl.core.model.Entity;
import com.zygon.rl.core.model.Game;
import com.zygon.rl.core.model.Location;
import com.zygon.rl.core.model.Region;
import com.zygon.rl.core.model.Regions;
import com.zygon.rl.core.view.Style;
import com.stewsters.util.shadow.twoDimention.LitMap2d;
import com.stewsters.util.shadow.twoDimention.ShadowCaster2d;

import java.util.Set;
import java.util.function.Supplier;

/**
 *
 * @author zygon
 */
class GDXRegionRenderer extends GDXComponent {

    private final FOVHelper fovHelper = new FOVHelper();
    private final BitmapFont font = new BitmapFont();
    private final Supplier<Location> withRespectLocationSupplier;
    private final SpriteBatch spriteBatch;
    private final OrthographicCamera camera;

    public GDXRegionRenderer(Supplier<Game> gameSupplier, Supplier<Location> withRespectLocationSupplier,
            BitmapFont font, SpriteBatch spriteBatch, OrthographicCamera camera) {
        super(Style.BORDERED, Color.TAN, gameSupplier, font, spriteBatch);
        this.withRespectLocationSupplier = withRespectLocationSupplier;
        this.spriteBatch = spriteBatch;
        this.camera = camera;

        // TODO: use set font, this needs updating when introducing a new symbol, which is dumb
        this.font.setFixedWidthGlyphs("F^#_,. @m+()");
    }

    @Override
    protected void renderComponent(int xx, int yy, int maxWidth, int maxHeight) {

        Location withRespectTo = withRespectLocationSupplier.get();
        Region viewRegion = createDisplayRegion(getGame(), withRespectTo, maxWidth, maxHeight);

        // Note these are zero-based
        float[][] lightResistances = fovHelper.generateSimpleResistances(viewRegion);
        LitMap2d lightMap = new LitMap2DImpl(lightResistances);
        ShadowCaster2d shadowCaster = new ShadowCaster2d(lightMap);
        // TODO: set actual radius based on entity
        try {
            // I think this is better, but still has edge cases when it comes to scroll mapping/sides
            shadowCaster.recalculateFOV(lightMap.getXSize() / 2, lightMap.getYSize() / 2, 25, .3f);
        } catch (java.lang.ArrayIndexOutOfBoundsException aioob) {
            throw new RuntimeException(aioob);
        }
        float density = Gdx.graphics.getDensity();
        int fontSize = (int) (density * 12);
        int fontBuffer = fontSize + 8;

        RegionView regionView = new RegionView(viewRegion, withRespectTo, 10);
        int viewWidthMax = (maxWidth / fontBuffer);
        int viewHeightMax = (maxHeight / fontBuffer);

        Location viewCenter = Location.create(viewWidthMax / 2, viewHeightMax / 2);

        for (int viewHeightIdx = 0; viewHeightIdx < viewHeightMax; viewHeightIdx++) {
            for (int viewWidthIdx = 0; viewWidthIdx < viewWidthMax; viewWidthIdx++) {

                Location viewLocation = Location.create(viewWidthIdx, viewHeightIdx);

                Set<Entity> entities = regionView.get(viewLocation, viewCenter);
                if (!entities.isEmpty()) {

                    char symbol = ' ';

                    double locationLightLevelPct = 1.0;
                    try {
                        if (viewWidthIdx < lightMap.getXSize() && viewHeightIdx < lightMap.getYSize()) {
                            // TODO: ooh, is this x/y not based on the wrt
                            locationLightLevelPct = lightMap.getLight(viewWidthIdx, viewHeightIdx);
                        }
                    } catch (java.lang.ArrayIndexOutOfBoundsException ex) {
                        throw new RuntimeException(ex);
                    }

                    if (locationLightLevelPct > .25) {
                        Entity mostViewBlockingEntity = getViewBlockingEntity(entities);

                        Tile tile = Tile.get(mostViewBlockingEntity);
                        symbol = tile.getGlyph();
                        font.setColor(tile.getColor().getRed(), tile.getColor().getGreen(),
                                tile.getColor().getBlue(), tile.getColor().getAlpha());

                        int pixelX = viewWidthIdx * fontBuffer + xx;
                        int pixelY = viewHeightIdx * fontBuffer + yy;

                        // TODO: print fancier font/glyph
                        font.draw(spriteBatch, symbol + "", pixelX, pixelY);
                    }
                }
            }
        }
    }

    // REMEMBER! maxWidthPixels/maxHeightPixels are the DISPLAY coords, NOTHING to do with the REGION
    private Region createDisplayRegion(Game game, Location withRespectTo, int maxWidthPixels, int maxHeightPixels) {

        Regions regions = game.getRegions();

        int wrtX = withRespectTo.getX();
        int wrtY = withRespectTo.getY();

        // view height is the number of columns that will "pixel out" into the screen
        // copy/pasta for now, remove for performance
        float density = Gdx.graphics.getDensity();
        int fontSize = (int) (density * 12);
        int fontBuffer = fontSize + 8;

        int spacesX = (maxWidthPixels / 2) / fontBuffer;
        int spacesY = (maxHeightPixels / 2) / fontBuffer;

        Location bullshitLoc = Location.create(wrtX - spacesX, wrtY - spacesY);

        int viewWidthMax = maxWidthPixels / fontBuffer;
        int viewHeightMax = maxHeightPixels / fontBuffer;

        return regions.getView(bullshitLoc, viewWidthMax, viewHeightMax);
    }

    private Entity getViewBlockingEntity(Set<Entity> entities) {
        Entity mostViewBlockingEntity = null;
        double entityViewBlocking = 0.0;
        for (Entity entity : entities) {
            Set<Attribute> viewBlocking = entity.getAttributes(CommonAttributes.VIEW_BLOCK.name());
            if (!viewBlocking.isEmpty()) {
                double maxViewBlocking = viewBlocking.parallelStream()
                        .map(DoubleAttribute::create)
                        .map(DoubleAttribute::getDoubleValue)
                        .mapToDouble((v) -> v).max()
                        .orElse(0.0);
                if (maxViewBlocking > entityViewBlocking) {
                    mostViewBlockingEntity = entity;
                    entityViewBlocking = maxViewBlocking;
                }
            }
            if (mostViewBlockingEntity == null) {
                mostViewBlockingEntity = entity;
            }
        }
        return mostViewBlockingEntity;
    }

    private static final class LitMap2DImpl implements LitMap2d {

        private final float[][] lightResistances;
        private final float[][] light;

        public LitMap2DImpl(float[][] lightResistances) {
            this.lightResistances = lightResistances;
            this.light = new float[this.lightResistances.length][this.lightResistances[0].length];
        }

        @Override
        public void setLight(int startx, int starty, float force) {
            if (startx < this.light.length && starty < this.light[startx].length) {
                this.light[startx][starty] = force;
            }
        }

        @Override
        public int getXSize() {
            return lightResistances.length;
        }

        @Override
        public int getYSize() {
            return lightResistances[0].length;
        }

        @Override
        public float getLight(int currentX, int currentY) {
            return this.light[currentX][currentY];
        }

        @Override
        public float getResistance(int currentX, int currentY) {
            return lightResistances[currentX][currentY];
        }

        @Override
        public void addLight(int currentX, int currentY, float bright) {
            this.light[currentX][currentY] = bright;
        }
    }
}
