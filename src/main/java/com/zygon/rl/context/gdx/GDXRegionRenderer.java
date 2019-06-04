package com.zygon.rl.context.gdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Align;
import com.stewsters.util.shadow.twoDimention.LitMap2d;
import com.stewsters.util.shadow.twoDimention.ShadowCaster2d;
import com.zygon.rl.common.model.Tile;
import com.zygon.rl.common.view.FOVHelper;
import com.zygon.rl.common.view.RegionView;
import com.zygon.rl.core.model.Attribute;
import com.zygon.rl.core.model.Entity;
import com.zygon.rl.core.model.Game;
import com.zygon.rl.core.model.Location;
import com.zygon.rl.core.model.Region;
import com.zygon.rl.core.model.Regions;
import com.zygon.rl.core.view.Style;

import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 *
 * @author zygon
 */
class GDXRegionRenderer extends GDXComponent {

    private final FOVHelper fovHelper = new FOVHelper();
    private final BitmapFont font;
    private final Supplier<Location> withRespectLocationSupplier;
    private final Batch batch;
    private final Calendar cal = Calendar.getInstance();

    // experimental, could also (possibly) use shape render
    private final Texture block = new Texture(1, 1, Pixmap.Format.RGBA8888);

    public GDXRegionRenderer(Supplier<Game> gameSupplier, Supplier<Location> withRespectLocationSupplier,
            BitmapFont font, Batch batch) {
        super(Style.BORDERED, Color.TAN, gameSupplier, font, batch);
        this.withRespectLocationSupplier = withRespectLocationSupplier;
        this.font = font;
        this.batch = batch;

        // TODO: use set font, this needs updating when introducing a new symbol, which is dumb
        font.setFixedWidthGlyphs("F^#_,. @m+()");

        Pixmap temp = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        temp.setColor(Color.WHITE);
        temp.fill();
        block.draw(temp, 0, 0);
        temp.dispose();
    }

    @Override
    protected void renderComponent(int xx, int yy, int maxWidth, int maxHeight) {

        Location withRespectTo = withRespectLocationSupplier.get();
        Region viewRegion = createDisplayRegion(getGame(), withRespectTo, maxWidth, maxHeight);

        // Note these are zero-based
        float[][] lightResistances = fovHelper.generateSimpleResistances(viewRegion);
        LitMap2d lightMap = new LitMap2DImpl(lightResistances);
        ShadowCaster2d shadowCaster = new ShadowCaster2d(lightMap);

        int radius = 50; // TODO: base on player's perception

        cal.setTime(getGame().getDate());
        int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);

        // Crude radius adjustment based on time. Note this is odd because the initial
        // implementation is a vampire game where vampires can see in the dark, that
        // needs to be addressed eventually.
        if (hourOfDay < 3 || hourOfDay > 21) {
            radius *= 0.25;
        } else if (hourOfDay < 6 || hourOfDay > 18) {
            radius *= 0.5;
        } else if (hourOfDay < 9 || hourOfDay > 15) {
            radius *= 0.75;
        }

        try {
            shadowCaster.recalculateFOV(lightMap.getXSize() / 2, lightMap.getYSize() / 2, radius, .5f);
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

                List<Entity> entities = regionView.get(viewLocation, viewCenter);
                if (!entities.isEmpty()) {

                    char symbol = ' ';

                    double locationLightLevelPct = 1.0;
                    try {
                        if (viewWidthIdx < lightMap.getXSize() && viewHeightIdx < lightMap.getYSize()) {
                            locationLightLevelPct = lightMap.getLight(viewWidthIdx, viewHeightIdx);
                        }
                    } catch (java.lang.ArrayIndexOutOfBoundsException ex) {
                        throw new RuntimeException(ex);
                    }

                    if (locationLightLevelPct > .25) {
                        List<Entity> sortedViewBlockingEntity = sortViewBlockingEntity(entities);
                        Entity mostViewBlockingEntity = sortedViewBlockingEntity.get(0);

                        int pixelX = viewWidthIdx * fontBuffer + xx;
                        int pixelY = viewHeightIdx * fontBuffer + yy;

                        Tile foregroundTile = Tile.get(mostViewBlockingEntity);
                        java.awt.Color backgroundColor = foregroundTile.getColor();

                        if (sortedViewBlockingEntity.size() > 1) {
                            backgroundColor = Tile.get(sortedViewBlockingEntity.get(1)).getColor();
                        }

                        // TODO: Draw the background color - it's too slow and ugly right now
//                        float orig = batch.getPackedColor();
//                        batch.setColor(backgroundColor.getRed(), backgroundColor.getGreen(),
//                                backgroundColor.getBlue(), 0.5f);
//
//                        batch.draw(block, pixelX, pixelY - fontBuffer, fontBuffer, fontBuffer);
//                        batch.setPackedColor(orig);
                        // Then draw the character
                        symbol = foregroundTile.getGlyph(mostViewBlockingEntity);
                        font.setColor(foregroundTile.getColor().getRed(), foregroundTile.getColor().getGreen(),
                                foregroundTile.getColor().getBlue(), 1);
                        font.draw(batch, symbol + "", pixelX, pixelY, fontBuffer, Align.center, false);
                    }
                }
            }
        }
    }

    // REMEMBER! maxWidthPixels/maxHeightPixels are the DISPLAY coords, NOTHING to do with the REGION
    private Region createDisplayRegion(Game game, Location withRespectTo,
            int maxWidthPixels, int maxHeightPixels) {

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

        Location startLoc = Location.create(wrtX - spacesX, wrtY - spacesY);

        int viewWidthMax = maxWidthPixels / fontBuffer;
        int viewHeightMax = maxHeightPixels / fontBuffer;

        return regions.getView(startLoc, viewWidthMax, viewHeightMax);
    }

    private static final Comparator<Entity> ENTITY_COMPARE = (e1, e2) -> {
        Set<Attribute> vb1 = e1.getAttributes(FOVHelper.VIEW_BLOCK_NAME);
        Set<Attribute> vb2 = e2.getAttributes(FOVHelper.VIEW_BLOCK_NAME);

        if (vb1 != null && vb2 == null) {
            return -1;
        } else if (vb1 == null && vb2 != null) {
            return 1;
        } else if (vb1 == null && vb2 == null) {
            return 0;
        }

        // Both entities have view blocking, sort by which entity
        // has the highest view-blocking entity
        double e1MaxViewBlock = FOVHelper.getMaxViewBlock(e1);
        double e2MaxViewBlock = FOVHelper.getMaxViewBlock(e2);
        return e1MaxViewBlock > e2MaxViewBlock
                ? -1 : (e1MaxViewBlock < e2MaxViewBlock ? 1 : 0);
    };

    private List<Entity> sortViewBlockingEntity(List<Entity> entities) {
        return entities.stream()
                .sorted(ENTITY_COMPARE)
                .collect(Collectors.toList());
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
