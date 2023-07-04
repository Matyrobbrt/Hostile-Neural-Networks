package shadows.hostilenetworks.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import shadows.hostilenetworks.util.EntityCaptureDrops;

import java.util.function.Consumer;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityCaptureDrops {
    @Unique private Consumer<ItemStack> hnn$captureDrops;

    @Override
    public void hnn$setCapture(@Nullable Consumer<ItemStack> consumer) {
        this.hnn$captureDrops = consumer;
    }

    @Inject(at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/item/ItemEntity;"), method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;", cancellable = true)
    private void hnn$captureDrops(ItemStack itemStack, float f, CallbackInfoReturnable<ItemEntity> cir) {
        if (hnn$captureDrops != null) {
            hnn$captureDrops.accept(itemStack);
            cir.setReturnValue(null);
        }
    }
}
