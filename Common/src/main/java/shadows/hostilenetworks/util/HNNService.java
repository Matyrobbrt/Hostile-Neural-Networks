package shadows.hostilenetworks.util;

import net.minecraft.world.item.CreativeModeTab;
import shadows.placebo.platform.Services;
import shadows.placebo.transfer.PlaceboEnergyStorage;

public interface HNNService {
    HNNService SERVICE = Services.load(HNNService.class);
    PlaceboEnergyStorage createEnergyStorage(int capacity, int transfer);
    CreativeModeTab.Builder tabBuilder();
}
