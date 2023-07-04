package shadows.hostilenetworks.jei;

import java.text.DecimalFormat;
import java.util.List;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import shadows.hostilenetworks.Hostile;
import shadows.hostilenetworks.HostileNetworks;
import shadows.hostilenetworks.data.ModelTier;
import shadows.hostilenetworks.util.Color;

public class SimChamberCategory implements IRecipeCategory<TickingDataModelWrapper> {

	public static final ResourceLocation UID = new ResourceLocation(HostileNetworks.MODID, "sim_chamber");
	public static final RecipeType<TickingDataModelWrapper> TYPE = RecipeType.create(HostileNetworks.MODID, "sim_chamber", TickingDataModelWrapper.class);

	private final IDrawable background;
	private final IDrawable icon;
	private final Component name;

	private int ticks = 0;
	private long lastTickTime = 0;
	static List<TickingDataModelWrapper> recipes;
	private ModelTier currentTier = ModelTier.BASIC;

	public SimChamberCategory(IGuiHelper guiHelper) {
		ResourceLocation location = new ResourceLocation(HostileNetworks.MODID, "textures/jei/sim_chamber.png");
		this.background = guiHelper.createDrawable(location, 0, 0, 116, 43);
		this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Hostile.Blocks.SIM_CHAMBER.get()));
		this.name = Component.translatable(Hostile.Blocks.SIM_CHAMBER.get().getDescriptionId());
	}

	@Override
	public IDrawable getBackground() {
		return this.background;
	}

	@Override
	public IDrawable getIcon() {
		return this.icon;
	}

	@Override
	public Component getTitle() {
		return this.name;
	}

	@Override
	public RecipeType<TickingDataModelWrapper> getRecipeType() {
		return TYPE;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, TickingDataModelWrapper recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 4, 4).addIngredient(VanillaTypes.ITEM_STACK, recipe.model);
		builder.addSlot(RecipeIngredientRole.INPUT, 28, 4).addIngredient(VanillaTypes.ITEM_STACK, recipe.input);
		builder.addSlot(RecipeIngredientRole.OUTPUT, 96, 4).addIngredient(VanillaTypes.ITEM_STACK, recipe.baseDrop);
		builder.addSlot(RecipeIngredientRole.OUTPUT, 66, 26).addIngredient(VanillaTypes.ITEM_STACK, recipe.prediction);
	}

	@Override
	public void draw(TickingDataModelWrapper recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics stack, double mouseX, double mouseY) {
		Minecraft mc = Minecraft.getInstance();
		Font font = mc.font;
		long time = mc.level.getGameTime();

		int width = Mth.ceil(35F * (this.ticks % 40 + mc.getDeltaFrameTime()) / 40);

		// stack.blit(52, 9, 0, 43, width, 6, 256, 256); TODO

		if (time != this.lastTickTime) {
			if (++this.ticks % 30 == 0) {
				ModelTier next = this.currentTier.next();
				if (next == this.currentTier) next = ModelTier.BASIC;
				for (TickingDataModelWrapper t : recipes)
					t.setTier(next);
				this.currentTier = next;
			}
			this.lastTickTime = time;
		}
		Component comp = recipe.currentTier.getComponent();
		width = font.width(comp);
		stack.drawString(font, recipe.currentTier.getComponent(), 33 - width / 2, 30, recipe.currentTier.color.getColor());
		DecimalFormat fmt = new DecimalFormat("##.##%");
		String msg = fmt.format(recipe.currentTier.accuracy);
		width = font.width(msg);
		stack.drawString(font, msg, 114 - width, 30, Color.WHITE, true);
	}

}