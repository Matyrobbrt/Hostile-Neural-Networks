package shadows.hostilenetworks.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import shadows.hostilenetworks.util.Color;

import java.util.List;

public class BlankDataModelItem extends Item {

	public BlankDataModelItem(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> list, TooltipFlag pFlag) {
		list.add(Component.translatable("hostilenetworks.info.click_to_attune", Color.withColor("hostilenetworks.color_text.rclick", ChatFormatting.WHITE.getColor()), Color.withColor("hostilenetworks.color_text.build", ChatFormatting.GOLD.getColor())).withStyle(ChatFormatting.GRAY));
	}

}
