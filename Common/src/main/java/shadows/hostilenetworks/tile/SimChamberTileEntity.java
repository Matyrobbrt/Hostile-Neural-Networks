package shadows.hostilenetworks.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import shadows.hostilenetworks.Hostile;
import shadows.hostilenetworks.HostileConfig;
import shadows.hostilenetworks.data.CachedModel;
import shadows.hostilenetworks.data.DataModel;
import shadows.hostilenetworks.data.ModelTier;
import shadows.hostilenetworks.item.DataModelItem;
import shadows.hostilenetworks.util.HNNContainerUtil;
import shadows.hostilenetworks.util.HNNService;
import shadows.placebo.block_entity.TickingBlockEntity;
import shadows.placebo.container.EasyContainerData;
import shadows.placebo.container.EasyContainerData.IDataAutoRegister;
import shadows.placebo.transfer.PlaceboEnergyStorage;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class SimChamberTileEntity extends HNNBlockEntity implements TickingBlockEntity, IDataAutoRegister {

	protected final EasyContainerData data = new EasyContainerData();

	protected CachedModel currentModel = null;
	protected int runtime = 0;
	protected boolean predictionSuccess = false;
	protected FailureState failState = FailureState.NONE;

	public SimChamberTileEntity(BlockPos pos, BlockState state) {
		super(Hostile.TileEntities.SIM_CHAMBER.get(), pos, state);
		this.inventory = new SimItemHandler();
		this.data.addData(() -> this.runtime, v -> this.runtime = v);
		this.data.addData(() -> this.predictionSuccess, v -> this.predictionSuccess = v);
		this.data.addData(() -> this.failState.ordinal(), v -> this.failState = FailureState.values()[v]);
		this.data.addEnergy(this.energy);
	}

	@Override
	protected PlaceboEnergyStorage createEnergy() {
		return HNNService.SERVICE.createEnergyStorage(HostileConfig.simPowerCap, HostileConfig.simPowerCap);
	}

	@Override
	public void registerSlots(Consumer<DataSlot> consumer) {
		this.data.register(consumer);
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("inventory", HNNContainerUtil.serialize(inventory));
		tag.putInt("energy", this.energy.getEnergy());
		tag.putString("model", this.currentModel == null ? "null" : this.currentModel.getModel().getId().toString());
		tag.putInt("runtime", this.runtime);
		tag.putBoolean("predSuccess", this.predictionSuccess);
		tag.putInt("failState", this.failState.ordinal());
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		HNNContainerUtil.deserializeNBT(tag.getCompound("inventory"), inventory);
		this.energy.setEnergy(tag.getInt("energy"));
		ItemStack model = this.inventory.getItem(0);
		CachedModel cModel = this.getOrLoadModel(model);
		String modelId = tag.getString("model");
		if (cModel != null && cModel.getModel().getId().toString().equals(modelId)) {
			this.currentModel = cModel;
		}
		this.runtime = tag.getInt("runtime");
		this.predictionSuccess = tag.getBoolean("predSuccess");
		this.failState = FailureState.values()[tag.getInt("failState")];
	}

	@Override
	public void serverTick(Level level, BlockPos pos, BlockState state) {
		ItemStack model = this.inventory.getItem(0);
		if (!model.isEmpty()) {
			CachedModel oldModel = this.currentModel;
			this.currentModel = this.getOrLoadModel(model);
			if (oldModel != this.currentModel) {
				this.runtime = 0;
			}
			if (this.currentModel != null) {
				if (this.currentModel.getTier() == ModelTier.FAULTY) {
					this.failState = FailureState.FAULTY;
					this.runtime = 0;
					return;
				}
				if (this.runtime == 0) {
					if (this.canStartSimulation()) {
						this.runtime = 300;
						this.predictionSuccess = this.level.random.nextFloat() <= this.currentModel.getAccuracy();
						this.inventory.getItem(1).shrink(1);
						this.setChanged();
					}
				} else if (hasPowerFor(this.currentModel.getModel())) {
					this.failState = FailureState.NONE;
					if (--this.runtime == 0) {
						ItemStack stk = this.inventory.getItem(2);
						if (stk.isEmpty()) this.inventory.setItem(2, this.currentModel.getModel().getBaseDrop().copy());
						else stk.grow(1);
						if (this.predictionSuccess) {
							stk = this.inventory.getItem(3);
							if (stk.isEmpty()) this.inventory.setItem(3, this.currentModel.getPredictionDrop());
							else stk.grow(1);
						}
						ModelTier tier = this.currentModel.getTier();
						if (tier != tier.next()) {
							this.currentModel.setData(this.currentModel.getData() + 1);
						}
						DataModelItem.setIters(model, DataModelItem.getIters(model) + 1);
						this.setChanged();
					} else if (this.runtime != 0) {
						this.energy.setEnergy(this.energy.getEnergy() - this.currentModel.getModel().getSimCost());
						this.setChanged();
					}
				} else {
					this.failState = FailureState.ENERGY_MID_CYCLE;
				}
				return;
			}
		}
		this.failState = FailureState.MODEL;
		this.runtime = 0;
	}

	/**
	 * Checks if the output slots are clear and there is enough power for a sim run.
	 */
	public boolean canStartSimulation() {
		if (this.inventory.getItem(1).isEmpty()) {
			this.failState = FailureState.INPUT;
			return false;
		}

		DataModel model = this.currentModel.getModel();
		ItemStack nOut = this.inventory.getItem(2);
		ItemStack pOut = this.inventory.getItem(3);
		ItemStack nOutExp = model.getBaseDrop();
		ItemStack pOutExp = this.currentModel.getPredictionDrop();

		if (this.canStack(nOut, nOutExp) && this.canStack(pOut, pOutExp)) {
			if (this.hasPowerFor(model)) {
				this.failState = FailureState.NONE;
				return true;
			} else {
				this.failState = FailureState.ENERGY;
				return false;
			}
		} else {
			this.failState = FailureState.OUTPUT;
			return false;
		}
	}

	public boolean canStack(ItemStack a, ItemStack b) {
		if (a.isEmpty()) return true;
		return ItemStack.isSameItemSameTags(a, b) && a.getCount() < a.getMaxStackSize();
	}

	/**
	 * Checks if the system has the power required for the tick cost of a model.
	 * @param model The model being checked.
	 * @return If the chamber has more power than the sim cost of the model.
	 */
	public boolean hasPowerFor(DataModel model) {
		return this.energy.getEnergy() >= model.getSimCost();
	}

	@Nullable
	protected CachedModel getOrLoadModel(ItemStack stack) {
		if (this.currentModel == null || this.currentModel.getSourceStack() != stack) {
			CachedModel model = new CachedModel(stack, 0);
			if (model.getModel() != null) return model;
			else return null;
		}
		return this.currentModel;
	}

	public Container getInventory() {
		return this.inventory;
	}

	public int getEnergyStored() {
		return this.energy.getEnergy();
	}

	public int getRuntime() {
		return this.runtime;
	}

	public boolean didPredictionSucceed() {
		return this.predictionSuccess;
	}

	public FailureState getFailState() {
		return this.failState;
	}

	public class SimItemHandler extends SimpleContainer {

		public SimItemHandler() {
			super(4);
			addListener(container -> SimChamberTileEntity.this.setChanged());
		}

		@Override
		public boolean canPlaceItem(int slot, ItemStack stack) {
			if (slot == 0) return stack.getItem() instanceof DataModelItem;
			else if (slot == 1) return DataModelItem.matchesInput(this.getItem(0), stack);
			return false;
		}

		@Override
		public boolean canTakeItem(Container container, int slot, ItemStack itemStack) {
			return slot > 1;
		}
	}

	@Override
	public boolean canTakeItem(Container container, int slot, ItemStack itemStack) {
		return slot > 1;
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack itemStack) {
		return slot <= 1;
	}

	public enum FailureState {
		NONE("none"),
		OUTPUT("output"),
		ENERGY("energy"),
		INPUT("input"),
		MODEL("model"),
		FAULTY("faulty"),
		ENERGY_MID_CYCLE("energy_mid_cycle");

		private final String name;

		FailureState(String name) {
			this.name = name;
		}

		public String getKey() {
			return "hostilenetworks.fail." + this.name;
		}
	}

}
