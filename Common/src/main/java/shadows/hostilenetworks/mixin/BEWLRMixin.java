package shadows.hostilenetworks.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shadows.hostilenetworks.client.DataModelItemStackRenderer;
import shadows.hostilenetworks.item.DataModelItem;

@Mixin(BlockEntityWithoutLevelRenderer.class)
public class BEWLRMixin {
    @Inject(method = "renderByItem", at = @At("HEAD"), cancellable = true)
    private void hnn$onRender(ItemStack stack, ItemDisplayContext mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay, CallbackInfo info) {
        if (stack.getItem() instanceof DataModelItem) {
            DataModelItemStackRenderer.renderByItem(stack, mode, matrices, vertexConsumers, light, overlay);
            info.cancel();
        }
    }
}
