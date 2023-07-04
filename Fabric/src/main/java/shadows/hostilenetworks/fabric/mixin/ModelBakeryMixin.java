package shadows.hostilenetworks.fabric.mixin;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shadows.hostilenetworks.event.client.RegisterAdditionalModelsEvent;
import shadows.placebo.Placebo;

import java.util.Map;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {
    @Shadow public abstract UnbakedModel getModel(ResourceLocation resourceLocation);

    @Shadow @Final private Map<ResourceLocation, UnbakedModel> unbakedCache;

    @Shadow @Final private Map<ResourceLocation, UnbakedModel> topLevelModels;

    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;SPYGLASS_IN_HAND_MODEL:Lnet/minecraft/client/resources/model/ModelResourceLocation;", shift = At.Shift.AFTER), method = "<init>")
    private void hnn$addAdditionalModels(BlockColors blockColors, ProfilerFiller profilerFiller, Map map, Map map2, CallbackInfo ci) {
        Placebo.BUS.post(new RegisterAdditionalModelsEvent(rl -> {
            UnbakedModel unbakedmodel = this.getModel(rl); // loadTopLevel(...), but w/o ModelResourceLocation limitation
            this.unbakedCache.put(rl, unbakedmodel);
            this.topLevelModels.put(rl, unbakedmodel);
        }));
    }
}
