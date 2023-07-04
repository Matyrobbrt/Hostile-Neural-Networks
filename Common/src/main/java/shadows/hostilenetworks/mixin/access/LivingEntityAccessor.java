package shadows.hostilenetworks.mixin.access;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Invoker("getExperienceReward")
    int hnn$getExpReward();

    @Invoker("dropAllDeathLoot")
    void hnn$dropAllDeathLoot(DamageSource source);

    @Invoker("shouldDropLoot")
    boolean hnn$dropsLoot();
}
