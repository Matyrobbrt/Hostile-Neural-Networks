package shadows.hostilenetworks;

import io.github.matyrobbrt.eventdispatcher.SubscribeEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import shadows.hostilenetworks.Hostile.Items;
import shadows.hostilenetworks.command.GenerateModelCommand;
import shadows.hostilenetworks.data.DataModel;
import shadows.hostilenetworks.data.DataModelManager;
import shadows.hostilenetworks.data.ModelTier;
import shadows.hostilenetworks.event.EntityInteractSpecificEvent;
import shadows.hostilenetworks.event.EntityKilledEntityEvent;
import shadows.hostilenetworks.item.DataModelItem;
import shadows.hostilenetworks.item.DeepLearnerItem;
import shadows.hostilenetworks.util.HNNContainerUtil;
import shadows.placebo.events.RegisterCommandsEvent;

public class HostileEvents {

	@SubscribeEvent
	public static void cmds(RegisterCommandsEvent e) {
		var builder = Commands.literal("hostilenetworks");
		GenerateModelCommand.register(builder);
		e.dispatcher().register(builder);
	}

	@SubscribeEvent
	public static void modelAttunement(EntityInteractSpecificEvent e) {
		Player player = e.player;
		ItemStack stack = player.getItemInHand(e.hand);
		if (stack.getItem() == Hostile.Items.BLANK_DATA_MODEL.get()) {
			if (!player.level().isClientSide) {
				DataModel model = DataModelManager.INSTANCE.getForEntity(e.interactedEntity.getType());
				if (model == null) {
					Component msg = Component.translatable("hostilenetworks.msg.no_model").withStyle(ChatFormatting.RED);
					player.sendSystemMessage(msg);
					return;
				}

				Component msg = Component.translatable("hostilenetworks.msg.built", model.getName()).withStyle(ChatFormatting.GOLD);
				player.sendSystemMessage(msg);

				ItemStack modelStack = new ItemStack(Hostile.Items.DATA_MODEL.get());
				DataModelItem.setStoredModel(modelStack, model);
				player.setItemInHand(e.hand, modelStack);
			}
			e.setResult(InteractionResult.CONSUME);
		}
	}

	@SubscribeEvent
	public static void kill(EntityKilledEntityEvent e) {
		if (e.attacker() instanceof ServerPlayer p) {
			p.getInventory().items.stream().filter(s -> s.getItem() == Items.DEEP_LEARNER.get()).forEach(dl -> updateModels(dl, e.victim().getType(), 0));
			if (p.getOffhandItem().getItem() == Items.DEEP_LEARNER.get()) updateModels(p.getOffhandItem(), e.victim().getType(), 0);
//			if (ModList.get().isLoaded("curios")) {
//				ItemStack curioStack = CuriosCompat.getDeepLearner(p);
//				if (curioStack.getItem() == Items.DEEP_LEARNER.get()) updateModels(curioStack, e.victim().getType(), 0);
//			}
		}
	}

	public static void updateModels(ItemStack learner, EntityType<?> type, int bonus) {
		Container handler = DeepLearnerItem.getItemHandler(learner);
		for (int i = 0; i < 4; i++) {
			ItemStack model = handler.getItem(i);
			if (model.isEmpty()) continue;
			DataModel dModel = DataModelItem.getStoredModel(model);
			if (dModel.getType() == type || dModel.getSubtypes().contains(type)) {
				int data = DataModelItem.getData(model);
				ModelTier tier = ModelTier.getByData(dModel, data);
				DataModelItem.setData(model, data + dModel.getDataPerKill(tier) + bonus);
			}
		}
		HNNContainerUtil.saveItems(learner, handler);
	}
}
