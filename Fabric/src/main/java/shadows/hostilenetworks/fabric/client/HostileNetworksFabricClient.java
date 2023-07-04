package shadows.hostilenetworks.fabric.client;

import io.github.matyrobbrt.eventdispatcher.EventBus;
import shadows.hostilenetworks.HostileClient;
import shadows.placebo.Placebo;
import shadows.placebo.fabric.api.PlaceboClientInitEntrypoint;

public class HostileNetworksFabricClient implements PlaceboClientInitEntrypoint {
    @Override
    public void run(EventBus bus) {
        Placebo.BUS.register(HostileClient.class);
        HostileClient.init();
    }
}
