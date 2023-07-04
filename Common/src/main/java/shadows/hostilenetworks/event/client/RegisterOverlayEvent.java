package shadows.hostilenetworks.event.client;

import io.github.matyrobbrt.eventdispatcher.Event;
import shadows.hostilenetworks.client.GuiOverlay;

import java.util.function.BiConsumer;

public record RegisterOverlayEvent(BiConsumer<String, GuiOverlay> registrar) implements Event {
}
