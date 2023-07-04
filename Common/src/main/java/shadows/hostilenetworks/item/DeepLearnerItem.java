package shadows.hostilenetworks.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import shadows.hostilenetworks.data.CachedModel;
import shadows.hostilenetworks.gui.DeepLearnerContainer;
import shadows.hostilenetworks.util.Color;
import shadows.hostilenetworks.util.HNNContainerUtil;
import shadows.placebo.container.ContainerUtil;

import java.util.List;

public class DeepLearnerItem extends Item {

	public DeepLearnerItem(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		if (!ctx.getLevel().isClientSide) {
			ContainerUtil.openScreen((ServerPlayer) ctx.getPlayer(), new Provider(ctx.getHand()), buf -> buf.writeBoolean(ctx.getHand() == InteractionHand.MAIN_HAND));
		}
		return InteractionResult.CONSUME;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
		if (!pPlayer.level().isClientSide) {
			ContainerUtil.openScreen((ServerPlayer) pPlayer, new Provider(pHand), buf -> buf.writeBoolean(pHand == InteractionHand.MAIN_HAND));
		}
		return InteractionResultHolder.consume(pPlayer.getItemInHand(pHand));
	}

	@Override
	public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> list, TooltipFlag pFlag) {
		list.add(Component.translatable("hostilenetworks.info.deep_learner", Color.withColor("hostilenetworks.color_text.hud", Color.WHITE)).withStyle(ChatFormatting.GRAY));
		if (Screen.hasShiftDown()) {
			Container inv = getItemHandler(pStack);
			boolean empty = true;
			for (int i = 0; i < 4; i++)
				if (!inv.getItem(i).isEmpty()) empty = false;
			if (empty) return;
			list.add(Component.translatable("hostilenetworks.info.dl_contains").withStyle(ChatFormatting.GRAY));
			for (int i = 0; i < 4; i++) {
				ItemStack stack = inv.getItem(i);
				if (stack.isEmpty()) continue;
				CachedModel model = new CachedModel(stack, 0);
				if (model.getModel() == null) continue;
				list.add(Component.translatable("- %s %s", model.getTier().getComponent(), stack.getItem().getName(stack)).withStyle(ChatFormatting.GRAY));
			}
		} else {
			Container inv = getItemHandler(pStack);
			boolean empty = true;
			for (int i = 0; i < 4; i++)
				if (!inv.getItem(i).isEmpty()) empty = false;
			if (empty) return;
			list.add(Component.translatable("hostilenetworks.info.hold_shift", Color.withColor("hostilenetworks.color_text.shift", Color.WHITE)).withStyle(ChatFormatting.GRAY));
		}
	}

//	@Override
//	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
//		return oldStack.getItem() != newStack.getItem();
//	}

	public static Container getItemHandler(ItemStack stack) {
		if (stack.isEmpty() || !stack.hasTag()) return new SimpleContainer(4);
		SimpleContainer handler = new SimpleContainer(4);
		HNNContainerUtil.deserializeNBT(stack.getTag().getCompound("learner_inv"), handler);
		return handler;
	}

	protected class Provider implements MenuProvider {

		private final InteractionHand hand;

		protected Provider(InteractionHand hand) {
			this.hand = hand;
		}

		@Override
		public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
			return new DeepLearnerContainer(id, inv, this.hand);
		}

		@Override
		public Component getDisplayName() {
			return Component.translatable("hostilenetworks.title.deep_learner");
		}
	}
}
