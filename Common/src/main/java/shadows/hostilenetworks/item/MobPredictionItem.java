package shadows.hostilenetworks.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import shadows.hostilenetworks.data.DataModel;

import javax.annotation.Nullable;

public class MobPredictionItem extends Item {

	public static final String TIER = "tier";

	public MobPredictionItem(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public Component getName(ItemStack pStack) {
		DataModel model = getStoredModel(pStack);
		Component modelName;
		if (model == null) {
			modelName = Component.literal("BROKEN").withStyle(ChatFormatting.OBFUSCATED);
		} else modelName = model.getName();
		return Component.translatable(this.getDescriptionId(pStack), modelName);
	}

	@Nullable
	public static DataModel getStoredModel(ItemStack stack) {
		return DataModelItem.getStoredModel(stack);
	}

	public static void setStoredModel(ItemStack stack, DataModel model) {
		DataModelItem.setStoredModel(stack, model);
	}

}