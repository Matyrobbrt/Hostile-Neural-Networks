package shadows.hostilenetworks.util;

import net.minecraft.world.entity.LivingEntity;
import shadows.hostilenetworks.mixin.access.LivingEntityAccessor;

public class ReflectionThings {

    public static int getExperienceReward(LivingEntity ent) {
        return ((LivingEntityAccessor) ent).hnn$getExpReward();
    }

}
