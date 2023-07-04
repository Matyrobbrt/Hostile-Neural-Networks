package shadows.hostilenetworks.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;
import shadows.hostilenetworks.Hostile;
import shadows.hostilenetworks.HostileNetworks;
import shadows.hostilenetworks.data.CachedModel;
import shadows.hostilenetworks.data.ModelTier;
import shadows.hostilenetworks.item.DeepLearnerItem;
import shadows.placebo.compat.TrinketInventory;
import shadows.placebo.events.client.RegisterOverlaysEvent;

import java.util.ArrayList;
import java.util.List;

public class DeepLearnerHudRenderer implements RegisterOverlaysEvent.GuiOverlay {

	private static final ResourceLocation DL_HUD = new ResourceLocation(HostileNetworks.MODID, "textures/gui/deep_learner_hud.png");

	@Override
	public void render(Gui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight) {
		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		if (player == null || !(mc.screen instanceof ChatScreen) && mc.screen != null) return;

		ItemStack stack = player.getMainHandItem();
		if (stack.getItem() != Hostile.Items.DEEP_LEARNER.get()) stack = player.getOffhandItem();
		if (stack.getItem() != Hostile.Items.DEEP_LEARNER.get()) {
			stack = TrinketInventory.probe(player).flatMap(inv -> inv.findFirstMatching(Hostile.Items.DEEP_LEARNER.get())).orElse(ItemStack.EMPTY);
		}
		if (stack.isEmpty()) return;

		Container inv = DeepLearnerItem.getItemHandler(stack);
		List<Pair<CachedModel, ItemStack>> renderable = new ArrayList<>(4);
		for (int i = 0; i < 4; i++) {
			ItemStack model = inv.getItem(i);
			if (model.isEmpty()) continue;
			CachedModel cModel = new CachedModel(model, 0);
			if (cModel.getModel() == null) continue;
			renderable.add(Pair.of(cModel, model));
		}

		if (renderable.isEmpty()) return;

		int spacing = 28;
		int x = 6;
		int y = 6;

		WeirdRenderThings.TRANSLUCENT_TRANSPARENCY.setupRenderState();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		graphics.blit(DL_HUD, 3, 3, 0, 23, 113, 1, 256, 256);
		for (int i = 0; i < renderable.size(); i++) {
			graphics.blit(DL_HUD, 3, 4 + spacing * i, 0, 24, 113, spacing, 256, 256);
			CachedModel cModel = renderable.get(i).getLeft();
			graphics.blit(DL_HUD, x + 18, y + i * spacing + 10, 0, 0, 89, 12, 256, 256);
			int width = 87;
			if (cModel.getTier() != ModelTier.SELF_AWARE) {
				int prev = cModel.getTierData();
				width = Mth.ceil(width * (cModel.getData() - prev) / (float) (cModel.getNextTierData() - prev));
			}
			graphics.blit(DL_HUD,  x + 19, y + i * spacing + 11, 0, 12, width, 10, 256, 256);
		}
		graphics.blit(DL_HUD, 3, 4 + spacing * renderable.size(), 0, 122, 113, 2, 256, 256);
		WeirdRenderThings.TRANSLUCENT_TRANSPARENCY.clearRenderState();

		for (int i = 0; i < renderable.size(); i++) {
			ItemStack dModel = renderable.get(i).getRight();
			renderAndDecorateItem(graphics, dModel, x, y + i * spacing + 9, gui.getFont());
		}

		for (int i = 0; i < renderable.size(); i++) {
			CachedModel cModel = renderable.get(i).getLeft();
			Component comp = cModel.getTier().getComponent();
			graphics.drawString(gui.getFont(), comp, x + 4, y + spacing * i, 0xFFFFFF, true);
			graphics.drawString(gui.getFont(), Component.translatable("hostilenetworks.hud.model"), x + mc.font.width(comp) + 4, y + spacing * i, 0xFFFFFF, true);
			if (cModel.getTier() != ModelTier.SELF_AWARE) graphics.drawString(gui.getFont(), I18n.get("hostilenetworks.hud.kills", cModel.getKillsNeeded()), x + 21, y + 12 + i * spacing, 0xFFFFFF, true);
		}
	}

	public static void drawModel(Minecraft mc, GuiGraphics graphics, int x, int y, ItemStack stack, CachedModel model) {
		renderAndDecorateItem(graphics, stack, x, y + 9, mc.font);
		Component comp = model.getTier().getComponent();
		graphics.drawString(mc.font, comp, x + 4, y, 0xFFFFFF, true);
		graphics.drawString(mc.font, Component.translatable("hostilenetworks.hud.model"), x + mc.font.width(comp) + 4, y, 0xFFFFFF, true);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		graphics.blit(DL_HUD, x + 18, y + 10, 0, 0, 89, 12, 256, 256);
		int width = 87;
		if (model.getTier() != ModelTier.SELF_AWARE) {
			width = Mth.ceil(width * model.getData() / (float) model.getNextTierData());
		}
		graphics.blit(DL_HUD, x + 19, y + 11, 0, 12, width, 10, 256, 256);
	}

	public static void renderAndDecorateItem(GuiGraphics graphics, ItemStack stack, int x, int y, Font font) {
		graphics.renderItem(stack, x, y);
		graphics.renderItemDecorations(font, stack, x, y);
	}
}
