package com.zygon.rl.core.view;

import com.zygon.rl.core.model.Location;
import com.zygon.rl.core.model.Region;

/**
 * RegionRenderer
 *
 */
public interface RegionRenderer {

    // x/y are absolute pixel locations for the
    void render(Region region, Location withRespectTo);
}
