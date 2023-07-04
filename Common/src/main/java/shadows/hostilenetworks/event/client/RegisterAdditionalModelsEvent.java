package shadows.hostilenetworks.event.client;

import io.github.matyrobbrt.eventdispatcher.Event;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public record RegisterAdditionalModelsEvent(Consumer<ResourceLocation> registrar) implements Event {
}
