package shadows.hostilenetworks.tile;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import shadows.hostilenetworks.Hostile;
import shadows.hostilenetworks.HostileConfig;
import shadows.hostilenetworks.data.DataModel;
import shadows.hostilenetworks.data.DataModelManager;
import shadows.hostilenetworks.item.MobPredictionItem;
import shadows.hostilenetworks.util.HNNContainerUtil;
import shadows.hostilenetworks.util.HNNService;
import shadows.placebo.block_entity.TickingBlockEntity;
import shadows.placebo.container.EasyContainerData;
import shadows.placebo.container.EasyContainerData.IDataAutoRegister;
import shadows.placebo.recipe.VanillaPacketDispatcher;
import shadows.placebo.transfer.PlaceboEnergyStorage;

import java.util.function.Consumer;

public class LootFabTileEntity extends HNNBlockEntity implements TickingBlockEntity, IDataAutoRegister, UpdateableBlockEntity {

	protected final Object2IntMap<DataModel> savedSelections = new Object2IntOpenHashMap<>();
	protected final EasyContainerData data = new EasyContainerData();

	protected int runtime = 0;
	protected int currentSel = -1;

	public LootFabTileEntity(BlockPos pos, BlockState state) {
		super(Hostile.TileEntities.LOOT_FABRICATOR.get(), pos, state);
		this.savedSelections.defaultReturnValue(-1);
		this.data.addData(() -> this.runtime, v -> this.runtime = v);
		this.data.addEnergy(this.energy);
		this.inventory = new FabItemHandler();
	}

	@Override
	protected PlaceboEnergyStorage createEnergy() {
		return HNNService.SERVICE.createEnergyStorage(HostileConfig.fabPowerCap, HostileConfig.fabPowerCap);
	}

	@Override
	public void registerSlots(Consumer<DataSlot> consumer) {
		this.data.register(consumer);
	}

	@Override
	public void serverTick(Level level, BlockPos pos, BlockState state) {
		DataModel dm = MobPredictionItem.getStoredModel(this.inventory.getItem(0));
		if (dm != null) {
			int selection = this.savedSelections.getInt(dm);
			if (this.currentSel != selection) {
				this.currentSel = selection;
				this.runtime = 0;
				return;
			}
			if (selection != -1) {
				if (this.runtime == 0) {
					ItemStack out = dm.getFabDrops().get(selection).copy();
					if (this.insertInOutput(out, true)) this.runtime = 60;
				} else {
					if (this.energy.getEnergy() < HostileConfig.fabPowerCost) return;
					this.energy.setEnergy(this.energy.getEnergy() - HostileConfig.fabPowerCost);
					if (--this.runtime == 0) {
						this.insertInOutput(dm.getFabDrops().get(selection).copy(), false);
						this.inventory.getItem(0).shrink(1);
					}
					this.setChanged();
				}
			} else this.runtime = 0;
		} else this.runtime = 0;
	}

	protected boolean insertInOutput(ItemStack stack, boolean sim) {
		for (int i = 1; i < 17; i++) {
			stack = HNNContainerUtil.insertItemForced(i, inventory, stack, sim);
			if (stack.isEmpty()) return true;
		}
		return false;
	}

	public void setSelection(DataModel model, int pId) {
		if (pId == -1) this.savedSelections.removeInt(model);
		else this.savedSelections.put(model, Mth.clamp(pId, 0, model.getFabDrops().size() - 1));
		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
		this.setChanged();
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("saved_selections", this.writeSelections(new CompoundTag()));
		tag.put("inventory", HNNContainerUtil.serialize(this.inventory));
		tag.putInt("energy", this.energy.getEnergy());
		tag.putInt("runtime", this.runtime);
		tag.putInt("selection", this.currentSel);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.readSelections(tag.getCompound("saved_selections"));
		HNNContainerUtil.deserializeNBT(tag.getCompound("inventory"), inventory);
		this.energy.setEnergy(tag.getInt("energy"));
		this.runtime = tag.getInt("runtime");
		this.currentSel = tag.getInt("selection");
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this, t -> ((LootFabTileEntity) t).writeSync());
	}

	private CompoundTag writeSync() {
		CompoundTag tag = new CompoundTag();
		saveAdditional(tag);
		return tag;
	}

	@Override
	public void handleDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		this.readSelections(pkt.getTag().getCompound("saved_selections"));
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = super.getUpdateTag();
		tag.put("saved_selections", this.writeSelections(new CompoundTag()));
		return tag;
	}

	private CompoundTag writeSelections(CompoundTag tag) {
		for (Object2IntMap.Entry<DataModel> e : this.savedSelections.object2IntEntrySet()) {
			tag.putInt(e.getKey().getId().toString(), e.getIntValue());
		}
		return tag;
	}

	private void readSelections(CompoundTag tag) {
		this.savedSelections.clear();
		for (String s : tag.getAllKeys()) {
			DataModel dm = DataModelManager.INSTANCE.getValue(new ResourceLocation(s));
			this.savedSelections.put(dm, Mth.clamp(tag.getInt(s), 0, dm.getFabDrops().size() - 1));
		}
	}

	public int getEnergyStored() {
		return this.energy.getEnergy();
	}

	public int getRuntime() {
		return this.runtime;
	}

	public int getSelectedDrop(DataModel model) {
		return model == null ? -1 : this.savedSelections.getInt(model);
	}

	public class FabItemHandler extends SimpleContainer {

		public FabItemHandler() {
			super(17);
			addListener(container -> LootFabTileEntity.this.setChanged());
		}

		@Override
		public boolean canPlaceItem(int slot, ItemStack stack) {
			if (slot == 0) return stack.getItem() == Hostile.Items.PREDICTION.get();
			return true;
		}
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack itemStack) {
		if (slot > 0) return false;
		return super.canPlaceItem(slot, itemStack);
	}

	@Override
	public boolean canTakeItem(Container container, int slot, ItemStack itemStack) {
		return slot != 0;
	}
}
