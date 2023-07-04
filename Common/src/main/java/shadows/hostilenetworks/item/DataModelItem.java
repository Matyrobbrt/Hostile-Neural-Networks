package shadows.hostilenetworks.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import shadows.hostilenetworks.data.CachedModel;
import shadows.hostilenetworks.data.DataModel;
import shadows.hostilenetworks.data.DataModelManager;
import shadows.hostilenetworks.data.ModelTier;
import shadows.hostilenetworks.util.Color;

import javax.annotation.Nullable;
import java.util.List;

public class DataModelItem extends Item {

	public static final String DATA_MODEL = "data_model";
	public static final String ID = "id";
	public static final String DATA = "data";
	public static final String ITERATIONS = "iterations";

	public DataModelItem(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> list, TooltipFlag pFlag) {
		if (Screen.hasShiftDown()) {
			CachedModel cModel = new CachedModel(pStack, 0);
			if (cModel.getModel() == null) {
				list.add(Component.translatable("Error: %s", Component.literal("Broke_AF").withStyle(ChatFormatting.OBFUSCATED, ChatFormatting.GRAY)));
				return;
			}
			int data = getData(pStack);
			ModelTier tier = ModelTier.getByData(cModel.getModel(), data);
			list.add(Component.translatable("hostilenetworks.info.tier", tier.getComponent()));
			int dProg = data - cModel.getTierData();
			int dMax = cModel.getNextTierData() - cModel.getTierData();
			if (tier != ModelTier.SELF_AWARE) {
				list.add(Component.translatable("hostilenetworks.info.data", Component.translatable("hostilenetworks.info.dprog", dProg, dMax).withStyle(ChatFormatting.GRAY)));
				list.add(Component.translatable("hostilenetworks.info.dpk", Component.literal("" + cModel.getDataPerKill()).withStyle(ChatFormatting.GRAY)));
			}
			list.add(Component.translatable("hostilenetworks.info.sim_cost", Component.translatable("hostilenetworks.info.rft", cModel.getModel().getSimCost()).withStyle(ChatFormatting.GRAY)));
			List<EntityType<? extends LivingEntity>> subtypes = cModel.getModel().getSubtypes();
			if (!subtypes.isEmpty()) {
				list.add(Component.translatable("hostilenetworks.info.subtypes"));
				for (EntityType<?> t : subtypes) {
					list.add(Component.translatable("hostilenetworks.info.sub_list", t.getDescription()).withStyle(Style.EMPTY.withColor(Color.LIME)));
				}
			}
		} else {
			list.add(Component.translatable("hostilenetworks.info.hold_shift", Color.withColor("hostilenetworks.color_text.shift", ChatFormatting.WHITE.getColor())).withStyle(ChatFormatting.GRAY));
		}
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

	/**
	 * Retrieves the data model from a data model itemstack.
	 * @return The contained data model.  Realisitcally should never be null.
	 */
	@Nullable
	public static DataModel getStoredModel(ItemStack stack) {
		if (!stack.hasTag()) return null;
		String dmKey = stack.getOrCreateTagElement(DATA_MODEL).getString(ID);
		return DataModelManager.INSTANCE.getValue(new ResourceLocation(dmKey));
	}

	public static void setStoredModel(ItemStack stack, DataModel model) {
		stack.removeTagKey(DATA_MODEL);
		stack.getOrCreateTagElement(DATA_MODEL).putString(ID, model.getId().toString());
	}

	public static int getData(ItemStack stack) {
		return stack.getOrCreateTagElement(DATA_MODEL).getInt(DATA);
	}

	public static void setData(ItemStack stack, int data) {
		stack.getOrCreateTagElement(DATA_MODEL).putInt(DATA, data);
	}

	public static int getIters(ItemStack stack) {
		return stack.getOrCreateTagElement(DATA_MODEL).getInt(ITERATIONS);
	}

	public static void setIters(ItemStack stack, int data) {
		stack.getOrCreateTagElement(DATA_MODEL).putInt(ITERATIONS, data);
	}

	public static boolean matchesInput(ItemStack model, ItemStack stack) {
		DataModel dModel = getStoredModel(model);
		if (dModel == null) return false;
		ItemStack input = dModel.getInput();
		boolean item = input.getItem() == stack.getItem();
		if (input.hasTag()) {
			if (stack.hasTag()) {
				CompoundTag t1 = input.getTag();
				CompoundTag t2 = stack.getTag();
				for (String s : t1.getAllKeys()) {
					if (!t1.get(s).equals(t2.get(s))) return false;
				}
				return true;
			} else return false;
		} else return item;
	}

}
