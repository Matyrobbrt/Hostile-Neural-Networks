package shadows.hostilenetworks;

import io.github.matyrobbrt.eventdispatcher.SubscribeEvent;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import shadows.hostilenetworks.client.DeepLearnerHudRenderer;
import shadows.hostilenetworks.data.DataModel;
import shadows.hostilenetworks.gui.DeepLearnerScreen;
import shadows.hostilenetworks.gui.LootFabScreen;
import shadows.hostilenetworks.gui.SimChamberScreen;
import shadows.hostilenetworks.item.MobPredictionItem;
import shadows.hostilenetworks.util.ClientEntityCache;
import shadows.placebo.Placebo;
import shadows.placebo.events.client.ClientSetupEvent;
import shadows.placebo.events.client.ClientTickEvent;
import shadows.placebo.events.client.RegisterAdditionalModelsEvent;
import shadows.placebo.events.client.RegisterColorHandlersEvent;
import shadows.placebo.events.client.RegisterOverlaysEvent;

public class HostileClient {

    @SubscribeEvent
    public static void init(ClientSetupEvent e) {
        e.enqueue(() -> {
            MenuScreens.register(Hostile.Containers.DEEP_LEARNER.get(), DeepLearnerScreen::new);
            MenuScreens.register(Hostile.Containers.SIM_CHAMBER.get(), SimChamberScreen::new);
            MenuScreens.register(Hostile.Containers.LOOT_FABRICATOR.get(), LootFabScreen::new);
        });
        Placebo.BUS.addListener(HostileClient::tick);
    }

    @SubscribeEvent
    public static void mrl(RegisterAdditionalModelsEvent e) {
        e.register(new ResourceLocation(HostileNetworks.MODID, "item/data_model_base"));
    }

    @SubscribeEvent
    public static void colors(RegisterColorHandlersEvent.Item e) {
        e.register((stack, tint) -> {
            DataModel model = MobPredictionItem.getStoredModel(stack);
            int color = 0xFFFFFF;
            if (model != null) {
                color = model.getNameColor();
            }
            return color;
        }, Hostile.Items.PREDICTION.get());
    }

    static {
        HostileNetworks.modBus.addListener((final RegisterOverlaysEvent event) ->
                event.register("deep_learner", new DeepLearnerHudRenderer()));
    }
    public static void init() {}

    public static void tick(ClientTickEvent e) {
        if (e.phase() == ClientTickEvent.Phase.START) ClientEntityCache.tick();
    }

}
