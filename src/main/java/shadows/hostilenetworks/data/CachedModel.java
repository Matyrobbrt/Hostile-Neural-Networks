package shadows.hostilenetworks.data;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import shadows.hostilenetworks.item.DataModelItem;

/**
 * A Cached model is a temporarily-deserialized model. It retains space for a cached entity for rendering.
 */
public class CachedModel {

	protected final ItemStack stack;
	protected final int slot;
	protected final DataModel model;
	private final Entity[] cachedEntities;

	protected int data;
	protected ModelTier tier;

	public CachedModel(ItemStack stack, int slot) {
		this.stack = stack;
		this.slot = slot;
		this.model = DataModelItem.getStoredModel(stack);
		this.cachedEntities = model == null ? null : new Entity[this.model.subtypes.size() + 1];
		this.data = DataModelItem.getData(stack);
		this.tier = ModelTier.getByData(this.model, this.data);
	}

	public DataModel getModel() {
		return this.model;
	}

	public int getData() {
		return this.data;
	}

	public ModelTier getTier() {
		return this.tier;
	}

	public int getDataPerKill() {
		return this.model.getDataPerKill(this.tier);
	}

	public int getTierData() {
		return this.model.getTierData(this.tier);
	}

	public int getNextDataPerKill() {
		return this.model.getDataPerKill(this.tier.next());
	}

	public int getNextTierData() {
		return this.model.getTierData(this.tier.next());
	}

	public void setData(int data) {
		this.data = data;
		if (this.data > this.getNextTierData()) this.tier = this.tier.next();
		DataModelItem.setData(this.stack, data);
	}

	public int getSlot() {
		return this.slot;
	}

	public float getAccuracy() {
		ModelTier next = this.tier.next();
		if (this.tier == next) return next.accuracy;
		int diff = this.getNextTierData() - this.getTierData();
		float tDiff = next.accuracy - this.tier.accuracy;
		return this.tier.accuracy + tDiff * (diff - (this.getNextTierData() - this.data)) / diff;
	}

	public int getKillsNeeded() {
		return Mth.ceil((this.getNextTierData() - this.data) / (float) this.getDataPerKill());
	}
	
	public LivingEntity getEntity(Level world) {
		return getEntity(world, 0);
	}

	public LivingEntity getEntity(Level world, int variant) {
		if (this.cachedEntities[variant] == null) {
			EntityType<?> type = variant == 0 ? this.model.type : this.model.subtypes.get(variant - 1);
			this.cachedEntities[variant] = type.create(world);
		}
		return this.cachedEntities[variant] instanceof LivingEntity le ? le : null;
	}

	public ItemStack getPredictionDrop() {
		return this.model.getPredictionDrop();
	}

	public ItemStack getSourceStack() {
		return this.stack;
	}

}
