package shadows.hostilenetworks.forge.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import shadows.hostilenetworks.HostileClient;
import shadows.hostilenetworks.HostileNetworks;
import shadows.hostilenetworks.event.client.RegisterAdditionalModelsEvent;
import shadows.hostilenetworks.event.client.RegisterOverlayEvent;
import shadows.placebo.Placebo;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = HostileNetworks.MODID)
public class HNNForgeClient {
    static {
        Placebo.BUS.register(HostileClient.class);
    }

    @SubscribeEvent
    static void registerAdditional(ModelEvent.RegisterAdditional event) {
        Placebo.BUS.post(new RegisterAdditionalModelsEvent(event::register));
    }

    @SubscribeEvent
    static void registerItemColors(final RegisterColorHandlersEvent.Item event) {
        Placebo.BUS.post(new shadows.hostilenetworks.event.client.RegisterColorHandlersEvent.Item(event::register));
    }

    @SubscribeEvent
    static void registerOverlay(final RegisterGuiOverlaysEvent event) {
        Placebo.BUS.post(new RegisterOverlayEvent((location, guiOverlay) -> event.registerAboveAll(location, guiOverlay::render)));
    }
}
