package shadows.hostilenetworks.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import shadows.hostilenetworks.HostileNetworks;
import shadows.hostilenetworks.data.DataModel;
import shadows.hostilenetworks.item.DataModelItem;
import shadows.hostilenetworks.mixin.access.ModelManagerAccessor;
import shadows.hostilenetworks.util.ClientEntityCache;

public class DataModelItemStackRenderer {

	private static final MultiBufferSource.BufferSource GHOST_ENTITY_BUF = MultiBufferSource.immediate(new BufferBuilder(256));
	private static final ResourceLocation DATA_MODEL_BASE = new ResourceLocation(HostileNetworks.MODID, "item/data_model_base");

	public static void renderByItem(ItemStack stack, ItemDisplayContext type, PoseStack matrix, MultiBufferSource buf, int light, int overlay) {
		ItemRenderer irenderer = Minecraft.getInstance().getItemRenderer();
		BakedModel base = ((ModelManagerAccessor)irenderer.getItemModelShaper().getModelManager()).hnn$getBakedRegistry().get(DATA_MODEL_BASE);
		matrix.pushPose();
		if (type == ItemDisplayContext.FIXED) {
			matrix.translate(1, 1, 0);
			float scale = 0.5F;
			matrix.scale(scale, scale, scale);
			matrix.translate(-1.5F, -0.5F, 0.5F);
			matrix.mulPose(Axis.XP.rotationDegrees(90));
			matrix.mulPose(Axis.XP.rotationDegrees(90));
			matrix.translate(0, 0, -1);
		} else if (type != ItemDisplayContext.GUI) {
			matrix.translate(1, 1, 0);
			float scale = 0.5F;
			matrix.scale(scale, scale, scale);
			matrix.translate(-1.5F, -0.5F, 0.5F);
			matrix.mulPose(Axis.XP.rotationDegrees(90));
		} else {
			matrix.translate(0, -.5F, -.5F);
			matrix.mulPose(Axis.XN.rotationDegrees(75));
			matrix.mulPose(Axis.ZP.rotationDegrees(45));
			float scale = 0.9F;
			matrix.scale(scale, scale, scale);
			matrix.translate(0.775, 0, -0.0825);
		}
		irenderer.renderModelLists(base, stack, light, overlay, matrix, ItemRenderer.getFoilBufferDirect(GHOST_ENTITY_BUF, ItemBlockRenderTypes.getRenderType(stack, true), true, false));
		GHOST_ENTITY_BUF.endBatch();
		matrix.popPose();
		DataModel model = DataModelItem.getStoredModel(stack);
		if (model != null) {
			LivingEntity ent = ClientEntityCache.computeIfAbsent(model.getType(), Minecraft.getInstance().level, model.getDisplayNbt());
			if (Minecraft.getInstance().player != null) ent.tickCount = Minecraft.getInstance().player.tickCount;
			if (ent != null) {
				renderEntityInInventory(matrix, type, ent, model);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static void renderEntityInInventory(PoseStack matrix, ItemDisplayContext type, LivingEntity pLivingEntity, DataModel model) {
		matrix.pushPose();
		matrix.translate(0.5, 0.5, 0.5);
		if (type == ItemDisplayContext.FIXED) {
			matrix.translate(0, -0.5, 0);
			float scale = 0.4F;
			scale *= model.getScale();
			matrix.scale(scale, scale, scale);
			matrix.translate(0, 1.45, 0);
			matrix.mulPose(Axis.XN.rotationDegrees(90));
			matrix.mulPose(Axis.YN.rotationDegrees(180));
		} else if (type == ItemDisplayContext.GUI) {
			matrix.translate(0, -0.5, 0);
			float scale = 0.4F;
			scale *= model.getScale();
			matrix.scale(scale, scale, scale);
			matrix.translate(0, 0.45, 0);
		} else {
			float scale = 0.25F;
			scale *= model.getScale();
			matrix.scale(scale, scale, scale);
			matrix.translate(0, 0.12 + 0.05 * Math.sin((pLivingEntity.tickCount + Minecraft.getInstance().getDeltaFrameTime()) / 12), 0);
		}

		float rotation = -30;
		if (type == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || type == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) rotation = 30;
		if (type == ItemDisplayContext.FIXED) rotation = 180;
		matrix.mulPose(Axis.YP.rotationDegrees(rotation));
		pLivingEntity.setYRot(0);
		pLivingEntity.yBodyRot = pLivingEntity.getYRot();
		pLivingEntity.yHeadRot = pLivingEntity.getYRot();
		pLivingEntity.yHeadRotO = pLivingEntity.getYRot();
		EntityRenderDispatcher entityrenderermanager = Minecraft.getInstance().getEntityRenderDispatcher();
		entityrenderermanager.setRenderShadow(false);
		MultiBufferSource.BufferSource rtBuffer = GHOST_ENTITY_BUF;
		WeirdRenderThings.fullbright_tesr = true;
		WeirdRenderThings.translucent = true;
		RenderSystem.runAsFancy(() -> {
			entityrenderermanager.render(pLivingEntity, model.getXOffset(), model.getYOffset(), model.getZOffset(), 0.0F, Minecraft.getInstance().getDeltaFrameTime(), matrix, new WrappedRTBuffer(rtBuffer), 15728880);
		});
		rtBuffer.endBatch();
		WeirdRenderThings.translucent = false;
		WeirdRenderThings.fullbright_tesr = false;
		entityrenderermanager.setRenderShadow(true);
		matrix.popPose();
	}

}
