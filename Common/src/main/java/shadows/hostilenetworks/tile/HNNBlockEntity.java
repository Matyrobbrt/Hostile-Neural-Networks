package shadows.hostilenetworks.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import shadows.placebo.transfer.PlaceboEnergyStorage;

import java.util.Set;
import java.util.function.Predicate;

public abstract class HNNBlockEntity extends BlockEntity implements Container {
    public SimpleContainer inventory;
    public final PlaceboEnergyStorage energy = createEnergy();
    public HNNBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    protected abstract PlaceboEnergyStorage createEnergy();

    @Override
    public int getContainerSize() {
        return inventory.getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    @Override
    public ItemStack getItem(int i) {
        return inventory.getItem(i);
    }

    @Override
    public ItemStack removeItem(int i, int j) {
        return inventory.removeItem(i, j);
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        return inventory.removeItemNoUpdate(i);
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
        inventory.setItem(i, itemStack);
    }

    @Override
    public int getMaxStackSize() {
        return inventory.getMaxStackSize();
    }

    @Override
    public boolean stillValid(Player player) {
        return inventory.stillValid(player);
    }

    @Override
    public void startOpen(Player player) {
        inventory.startOpen(player);
    }

    @Override
    public void stopOpen(Player player) {
        inventory.stopOpen(player);
    }

    @Override
    public boolean canPlaceItem(int i, ItemStack itemStack) {
        return inventory.canPlaceItem(i, itemStack);
    }

    @Override
    public boolean canTakeItem(Container container, int i, ItemStack itemStack) {
        return this.inventory.canTakeItem(container, i, itemStack);
    }

    @Override
    public int countItem(Item item) {
        return inventory.countItem(item);
    }

    @Override
    public boolean hasAnyOf(Set<Item> set) {
        return inventory.hasAnyOf(set);
    }

    @Override
    public boolean hasAnyMatching(Predicate<ItemStack> predicate) {
        return inventory.hasAnyMatching(predicate);
    }

    @Override
    public void clearContent() {
        inventory.clearContent();
    }
}
