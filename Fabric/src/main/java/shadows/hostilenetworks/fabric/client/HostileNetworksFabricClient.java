package shadows.hostilenetworks.fabric.client;

import io.github.matyrobbrt.eventdispatcher.EventBus;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import shadows.hostilenetworks.HostileClient;
import shadows.hostilenetworks.client.GuiOverlay;
import shadows.hostilenetworks.event.client.RegisterColorHandlersEvent;
import shadows.hostilenetworks.event.client.RegisterOverlayEvent;
import shadows.placebo.Placebo;
import shadows.placebo.fabric.api.PlaceboClientInitEntrypoint;

import java.util.ArrayList;
import java.util.List;

public class HostileNetworksFabricClient implements PlaceboClientInitEntrypoint {
    @Override
    public void run(EventBus bus) {
        Placebo.BUS.register(HostileClient.class);
        Placebo.BUS.post(new RegisterColorHandlersEvent.Item(ColorProviderRegistry.ITEM::register));

        final List<GuiOverlay> overlays = new ArrayList<>();
        Placebo.BUS.post(new RegisterOverlayEvent((location, guiOverlay) -> overlays.add(guiOverlay)));
        HudRenderCallback.EVENT.register((graphics, partialTick) -> {
            for (final GuiOverlay overlay : overlays) {
                overlay.render(Minecraft.getInstance().gui, graphics, partialTick, graphics.guiWidth(), graphics.guiHeight());
            }
        });
    }
}
