package shadows.hostilenetworks.forge;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import shadows.hostilenetworks.HostileNetworks;
import shadows.hostilenetworks.event.EntityInteractSpecificEvent;
import shadows.hostilenetworks.event.EntityKilledEntityEvent;
import shadows.hostilenetworks.tile.HNNBlockEntity;
import shadows.placebo.Placebo;
import shadows.placebo.events.ModEventBus;

@Mod(shadows.hostilenetworks.HostileNetworks.MODID)
public class HostileNetworksForge {
    public HostileNetworksForge() {
        new HostileNetworks(ModEventBus.grabBus(HostileNetworks.MODID));
        MinecraftForge.EVENT_BUS.addGenericListener(BlockEntity.class, (final AttachCapabilitiesEvent<BlockEntity> event) -> {
            if (event.getObject() instanceof HNNBlockEntity entity) {
                event.addCapability(new ResourceLocation(HostileNetworks.MODID, "inventory"), new ICapabilityProvider() {
                    private final LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> new InvWrapper(entity) {
                        @Override
                        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                            if (amount == 0)
                                return ItemStack.EMPTY;

                            ItemStack stackInSlot = getInv().getItem(slot);

                            if (stackInSlot.isEmpty())
                                return ItemStack.EMPTY;

                            if (!getInv().canTakeItem(getInv(), slot, stackInSlot))
                                return ItemStack.EMPTY;

                            if (simulate) {
                                if (stackInSlot.getCount() < amount) {
                                    return stackInSlot.copy();
                                } else {
                                    ItemStack copy = stackInSlot.copy();
                                    copy.setCount(amount);
                                    return copy;
                                }
                            } else {
                                int m = Math.min(stackInSlot.getCount(), amount);

                                ItemStack decrStackSize = getInv().removeItem(slot, m);
                                getInv().setChanged();
                                return decrStackSize;
                            }
                        }
                    });
                    private final LazyOptional<IEnergyStorage> energyStorage = LazyOptional.of(() -> (IEnergyStorage) entity.energy);

                    @Override
                    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                        if (cap == ForgeCapabilities.ITEM_HANDLER) {
                            return itemHandler.cast();
                        } else if (cap == ForgeCapabilities.ENERGY) {
                            return energyStorage.cast();
                        }
                        return LazyOptional.empty();
                    }
                });
            }
        });
        MinecraftForge.EVENT_BUS.addListener((final LivingDeathEvent event) -> {
            if (event.getSource().getEntity() != null) {
                Placebo.BUS.post(new EntityKilledEntityEvent(event.getSource().getEntity(), event.getEntity()));
            }
        });
        MinecraftForge.EVENT_BUS.addListener((final PlayerInteractEvent.EntityInteractSpecific event) -> {
            final var customEvent = new EntityInteractSpecificEvent(event.getEntity(), event.getTarget(), event.getHand());
            Placebo.BUS.post(customEvent);
            if (customEvent.getResult() != InteractionResult.PASS) {
                event.setCancellationResult(customEvent.getResult());
                event.setResult(Event.Result.DENY);
            }
        });
    }
}
