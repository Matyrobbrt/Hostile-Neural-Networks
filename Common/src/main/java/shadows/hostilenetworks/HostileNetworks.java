package shadows.hostilenetworks;

import com.google.common.collect.ImmutableSet;
import io.github.matyrobbrt.eventdispatcher.EventBus;
import io.github.matyrobbrt.eventdispatcher.SubscribeEvent;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shadows.hostilenetworks.block.LootFabBlock;
import shadows.hostilenetworks.block.SimChamberBlock;
import shadows.hostilenetworks.data.DataModelManager;
import shadows.hostilenetworks.gui.DeepLearnerContainer;
import shadows.hostilenetworks.gui.LootFabContainer;
import shadows.hostilenetworks.gui.SimChamberContainer;
import shadows.hostilenetworks.item.BlankDataModelItem;
import shadows.hostilenetworks.item.DataModelItem;
import shadows.hostilenetworks.item.DeepLearnerItem;
import shadows.hostilenetworks.item.MobPredictionItem;
import shadows.hostilenetworks.mixin.access.MenuTypeAccessor;
import shadows.hostilenetworks.tile.LootFabTileEntity;
import shadows.hostilenetworks.tile.SimChamberTileEntity;
import shadows.hostilenetworks.util.HNNService;
import shadows.placebo.Placebo;
import shadows.placebo.container.ContainerUtil;
import shadows.placebo.container.PlaceboContainerFactory;
import shadows.placebo.events.CommonSetupEvent;
import shadows.placebo.events.ModEventBus;
import shadows.placebo.json.TypeKeyed;
import shadows.placebo.loot.LootSystem;
import shadows.placebo.network.NetworkChannel;
import shadows.placebo.platform.Services;
import shadows.placebo.util.RegistryEvent;

import java.util.Comparator;

public class HostileNetworks {

	public static final String MODID = "hostilenetworks";
	public static final String VERSION = "1.0.0"; // TODO
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
	//Formatter::off
    public static final NetworkChannel CHANNEL = Services.PLATFORM.createChannel(new ResourceLocation(MODID, MODID), VERSION);
    //Formatter::on

	public static final CreativeModeTab TAB = HNNService.SERVICE.tabBuilder()
			.title(Component.translatable("itemGroup.hostilenetworks"))
			.displayItems((itemDisplayParameters, output) -> {
				output.accept(Hostile.Items.BLANK_DATA_MODEL.get());
				output.accept(Hostile.Items.DEEP_LEARNER.get());
				output.accept(Hostile.Items.EMPTY_PREDICTION.get());

				DataModelManager.INSTANCE.getValues().stream().sorted(Comparator.comparing(TypeKeyed.TypeKeyedBase::getId)).forEach(model -> {
					final ItemStack s = new ItemStack(Hostile.Items.PREDICTION.get());
					MobPredictionItem.setStoredModel(s, model);
					output.accept(s);
				});

				output.accept(Hostile.Items.DATA_MODEL.get());

				DataModelManager.INSTANCE.getValues().stream().sorted(Comparator.comparing(TypeKeyed.TypeKeyedBase::getId)).forEach(model -> {
					final ItemStack s = new ItemStack(Hostile.Items.DATA_MODEL.get());
					DataModelItem.setStoredModel(s, model);
					output.accept(s);
				});

				output.accept(Hostile.Blocks.SIM_CHAMBER.get());
				output.accept(Hostile.Blocks.LOOT_FABRICATOR.get());
			})
			.icon(() -> Hostile.Blocks.SIM_CHAMBER.get().asItem().getDefaultInstance())
			.build();

	public static EventBus modBus;

	public HostileNetworks(EventBus modBus) {
		HostileNetworks.modBus = modBus;
		modBus.register(this);
		Placebo.BUS.register(HostileNetworks.class);
		Placebo.BUS.register(HostileEvents.class);
		HostileConfig.load();
		DataModelManager.INSTANCE.registerToBus();
		Hostile.Items.init();
	}

	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Block> e) {
		e.getRegistry().register(new SimChamberBlock(Block.Properties.of().lightLevel(s -> 1).strength(4, 3000).noOcclusion()), "sim_chamber");
		e.getRegistry().register(new LootFabBlock(Block.Properties.of().lightLevel(s -> 1).strength(4, 3000).noOcclusion()), "loot_fabricator");
	}

	@SubscribeEvent
	public void registerTiles(RegistryEvent.Register<BlockEntityType<?>> e) {
		e.getRegistry().register(Services.PLATFORM.createTickingBEType(SimChamberTileEntity::new, ImmutableSet.of(Hostile.Blocks.SIM_CHAMBER.get()), false, true), "sim_chamber");
		e.getRegistry().register(Services.PLATFORM.createTickingBEType(LootFabTileEntity::new, ImmutableSet.of(Hostile.Blocks.LOOT_FABRICATOR.get()), false, true), "loot_fabricator");
	}

	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> e) {
		interface RegHelper {
			void register(String name, Item item);
		}
		final RegHelper reg = (name, item) -> e.getRegistry().register(item, name);
		reg.register("overworld_prediction", new Item(new Item.Properties()));
		reg.register("nether_prediction", new Item(new Item.Properties()));
		reg.register("end_prediction", new Item(new Item.Properties()));
		reg.register("twilight_prediction", new Item(new Item.Properties()));
		reg.register("sim_chamber", new BlockItem(Hostile.Blocks.SIM_CHAMBER.get(), new Item.Properties()));
		reg.register("loot_fabricator", new BlockItem(Hostile.Blocks.LOOT_FABRICATOR.get(), new Item.Properties()));
	}

	@SubscribeEvent
	public void registerMenuTypes(RegistryEvent.Register<MenuType<?>> e) {
		e.getRegistry().register(MenuTypeAccessor.hnn$createMenu((PlaceboContainerFactory<DeepLearnerContainer>)(id, inv, buf) -> new DeepLearnerContainer(id, inv, buf.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND), FeatureFlags.DEFAULT_FLAGS), "deep_learner");
		e.getRegistry().register(ContainerUtil.makeType(SimChamberContainer::new), "sim_chamber");
		e.getRegistry().register(ContainerUtil.makeType(LootFabContainer::new), "loot_fabricator");
	}

	@SubscribeEvent
	public void registerTabs(RegistryEvent.Register<CreativeModeTab> e) {
		e.getRegistry().register(TAB, MODID);
	}

	@SubscribeEvent
	public static void setup(CommonSetupEvent e) {
		LootSystem.defaultBlockTable(Hostile.Blocks.LOOT_FABRICATOR.get());
		LootSystem.defaultBlockTable(Hostile.Blocks.SIM_CHAMBER.get());
	}

//	@SubscribeEvent
//	public void imcEvent(InterModEnqueueEvent e) {
//		if (ModList.get().isLoaded("curios")) CuriosCompat.sendIMC();
//	}
}
