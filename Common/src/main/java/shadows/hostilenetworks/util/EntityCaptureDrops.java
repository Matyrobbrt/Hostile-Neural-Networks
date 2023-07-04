package shadows.hostilenetworks.util;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface EntityCaptureDrops {
    void hnn$setCapture(@Nullable Consumer<ItemStack> consumer);
}
