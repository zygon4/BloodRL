package com.zygon.rl.core.model.living;

import com.zygon.rl.common.model.CommonAttributes;
import com.zygon.rl.core.model.Attribute;
import com.zygon.rl.core.model.BooleanAttribute;
import com.zygon.rl.core.model.DoubleAttribute;
import com.zygon.rl.core.model.Entity;

/**
 *
 * @author zygon
 */
public class Living {

    private final Entity livingEntity;

    public Living(Entity playerEntity) {
        this.livingEntity = playerEntity;
    }

    public String getDescription() {
        return livingEntity.getDescription();
    }

    public String getDisplayName() {
        return livingEntity.getDisplayName();
    }

    public Entity getEntity() {
        return livingEntity;
    }

    public String getName() {
        return livingEntity.getName();
    }

    public double getHealth() {
        return DoubleAttribute.getValue(getAttribute(CommonAttributes.HEALTH));
    }

    public boolean isLiving() {
        Attribute living = getAttribute(CommonAttributes.LIVING);
        return living != null && BooleanAttribute.create(living).getBooleanValue();
    }

    private Attribute getAttribute(CommonAttributes attribute) {
        return livingEntity.getAttributes(attribute.name()).stream().findAny().orElse(null);
    }
}
