package shadows.hostilenetworks.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class HNNContainerUtil {
    public static CompoundTag serialize(Container handler) {
        ListTag nbtTagList = new ListTag();
        for (int i = 0; i < handler.getContainerSize(); i++) {
            final var s = handler.getItem(i);
            if (!s.isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                s.save(itemTag);
                nbtTagList.add(itemTag);
            }
        }
        CompoundTag nbt = new CompoundTag();
        nbt.put("Items", nbtTagList);
        nbt.putInt("Size", handler.getContainerSize());
        return nbt;
    }

    public static void saveItems(ItemStack stack, Container handler) {
        stack.getOrCreateTag().put("learner_inv", serialize(handler));
    }

    public static void deserializeNBT(CompoundTag nbt, Container container) {
        int size = nbt.contains("Size", Tag.TAG_INT) ? nbt.getInt("Size") : container.getContainerSize();
        ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); i++) {
            CompoundTag itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt("Slot");

            if (slot >= 0 && slot < size) {
                container.setItem(slot, ItemStack.of(itemTags));
            }
        }
        container.setChanged();
    }

    public static ItemStack insertItemForced(int slot, Container container, @NotNull ItemStack stack, boolean simulate) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        ItemStack existing = container.getItem(slot);

        int limit = stack.getMaxStackSize();

        if (!existing.isEmpty()) {
            if (!ItemStack.isSameItemSameTags(stack, existing))
                return stack;

            limit -= existing.getCount();
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate) {
            if (existing.isEmpty()) {
                container.setItem(slot, reachedLimit ? copyStackWithSize(stack, limit) : stack);
            } else {
                existing.grow(reachedLimit ? limit : stack.getCount());
            }
        }

        return reachedLimit ? copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
    }

    @NotNull
    public static ItemStack copyStackWithSize(@NotNull ItemStack itemStack, int size) {
        if (size == 0)
            return ItemStack.EMPTY;
        ItemStack copy = itemStack.copy();
        copy.setCount(size);
        return copy;
    }
}
