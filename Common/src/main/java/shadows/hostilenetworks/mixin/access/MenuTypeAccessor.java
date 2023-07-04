package shadows.hostilenetworks.mixin.access;

import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MenuType.class)
public interface MenuTypeAccessor {
    @Invoker("<init>")
    static <T extends AbstractContainerMenu> MenuType<T> hnn$createMenu(MenuType.MenuSupplier<T> supplier, FeatureFlagSet flags) {
        throw null;
    }
}
