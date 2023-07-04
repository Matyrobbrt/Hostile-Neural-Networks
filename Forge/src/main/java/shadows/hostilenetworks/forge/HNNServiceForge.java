package shadows.hostilenetworks.forge;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import shadows.hostilenetworks.util.HNNService;
import shadows.placebo.forge.cap.ModifiableEnergyStorage;
import shadows.placebo.transfer.PlaceboEnergyStorage;

public class HNNServiceForge implements HNNService {
    @Override
    public PlaceboEnergyStorage createEnergyStorage(int capacity, int transfer) {
        return new ModifiableEnergyStorage(capacity, transfer);
    }

    @Override
    public CreativeModeTab.Builder tabBuilder() {
        return CreativeModeTab.builder().withTabsBefore(CreativeModeTabs.SPAWN_EGGS);
    }
}
