package shadows.hostilenetworks.event;

import io.github.matyrobbrt.eventdispatcher.Event;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public record EntityKilledEntityEvent(Entity attacker, LivingEntity victim) implements Event {
}
