package shadows.hostilenetworks.event.client;

import io.github.matyrobbrt.eventdispatcher.Event;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.level.ItemLike;

public class RegisterColorHandlersEvent {
    public record Item(Registrar<ItemColor, ItemLike> registrar) implements Event {
    }


    @FunctionalInterface
    public interface Registrar<COLOR, TYPE> {
        void register(COLOR color, TYPE... target);
    }
}
