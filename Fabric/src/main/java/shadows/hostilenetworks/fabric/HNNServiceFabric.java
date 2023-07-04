package shadows.hostilenetworks.fabric;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.world.item.CreativeModeTab;
import shadows.hostilenetworks.util.HNNService;
import shadows.placebo.fabric.cap.FabricEnergyStorage;
import shadows.placebo.transfer.PlaceboEnergyStorage;

public class HNNServiceFabric implements HNNService {
    @Override
    public PlaceboEnergyStorage createEnergyStorage(int capacity, int transfer) {
        final var storage = new FabricEnergyStorage();
        storage.setCapacity(capacity);
        storage.setMaxInsert(transfer);
        storage.setMaxExtract(transfer);
        return storage;
    }

    @Override
    public CreativeModeTab.Builder tabBuilder() {
        return FabricItemGroup.builder();
    }
}
