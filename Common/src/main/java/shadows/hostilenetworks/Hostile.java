package shadows.hostilenetworks;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import shadows.hostilenetworks.gui.DeepLearnerContainer;
import shadows.hostilenetworks.gui.LootFabContainer;
import shadows.hostilenetworks.gui.SimChamberContainer;
import shadows.hostilenetworks.item.BlankDataModelItem;
import shadows.hostilenetworks.item.DataModelItem;
import shadows.hostilenetworks.item.DeepLearnerItem;
import shadows.hostilenetworks.item.MobPredictionItem;
import shadows.hostilenetworks.tile.LootFabTileEntity;
import shadows.hostilenetworks.tile.SimChamberTileEntity;
import shadows.placebo.reg.RegistrationProvider;
import shadows.placebo.reg.RegistryObject;
import shadows.placebo.util.RegObjHelper;

public class Hostile {

	private static final RegObjHelper R = new RegObjHelper(HostileNetworks.MODID);

	public static class Items {
		public static final RegistrationProvider<Item> ITEMS = RegistrationProvider.get(Registries.ITEM, HostileNetworks.MODID);
		public static final RegistryObject<Item> BLANK_DATA_MODEL = ITEMS.register("blank_data_model", () -> new BlankDataModelItem(new Item.Properties().stacksTo(1)));
		public static final RegistryObject<Item> EMPTY_PREDICTION = ITEMS.register("empty_prediction", () -> new Item(new Item.Properties()));
		public static final RegistryObject<Item> DEEP_LEARNER = ITEMS.register("deep_learner", () -> new DeepLearnerItem(new Item.Properties().stacksTo(1)));
		public static final RegistryObject<DataModelItem> DATA_MODEL = ITEMS.register("data_model", () -> new DataModelItem(new Item.Properties().stacksTo(1)));
		public static final RegistryObject<MobPredictionItem> PREDICTION = ITEMS.register("prediction", () -> new MobPredictionItem(new Item.Properties()));
		static void init() {}
	}

	public static class Blocks {
		public static final RegistryObject<Block> SIM_CHAMBER = R.block("sim_chamber");
		public static final RegistryObject<Block> LOOT_FABRICATOR = R.block("loot_fabricator");
	}

	public static class TileEntities {
		public static final RegistryObject<BlockEntityType<SimChamberTileEntity>> SIM_CHAMBER = R.blockEntity("sim_chamber");
		public static final RegistryObject<BlockEntityType<LootFabTileEntity>> LOOT_FABRICATOR = R.blockEntity("loot_fabricator");
	}

	public static class Containers {
		public static final RegistryObject<MenuType<DeepLearnerContainer>> DEEP_LEARNER = R.menu("deep_learner");
		public static final RegistryObject<MenuType<SimChamberContainer>> SIM_CHAMBER = R.menu("sim_chamber");
		public static final RegistryObject<MenuType<LootFabContainer>> LOOT_FABRICATOR = R.menu("loot_fabricator");
	}

}
