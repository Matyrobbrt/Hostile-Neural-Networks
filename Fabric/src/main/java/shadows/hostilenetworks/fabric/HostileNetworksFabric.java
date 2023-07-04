package shadows.hostilenetworks.fabric;

import io.github.matyrobbrt.eventdispatcher.EventBus;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.loader.impl.entrypoint.EntrypointUtils;
import shadows.hostilenetworks.Hostile;
import shadows.hostilenetworks.HostileNetworks;
import shadows.hostilenetworks.event.EntityInteractSpecificEvent;
import shadows.hostilenetworks.event.EntityKilledEntityEvent;
import shadows.placebo.Placebo;
import shadows.placebo.events.CommonSetupEvent;
import shadows.placebo.fabric.api.PlaceboInitEntrypoint;
import team.reborn.energy.api.EnergyStorage;

public class HostileNetworksFabric implements PlaceboInitEntrypoint {
    @Override
    public void run(EventBus bus) {
        new HostileNetworks(bus);
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) ->
                Placebo.BUS.post(new EntityKilledEntityEvent(entity, killedEntity)));
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            final var event = new EntityInteractSpecificEvent(player, entity, hand);
            Placebo.BUS.post(event);
            return event.getResult();
        });
        Placebo.BUS.addListener((final CommonSetupEvent event) -> {
            EnergyStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> (EnergyStorage) blockEntity.energy, Hostile.TileEntities.LOOT_FABRICATOR.get());
            EnergyStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> (EnergyStorage) blockEntity.energy, Hostile.TileEntities.SIM_CHAMBER.get());
        });
    }
}
