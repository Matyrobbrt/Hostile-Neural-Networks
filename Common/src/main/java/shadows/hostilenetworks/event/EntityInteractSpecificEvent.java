package shadows.hostilenetworks.event;

import io.github.matyrobbrt.eventdispatcher.Event;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class EntityInteractSpecificEvent implements Event {
    private InteractionResult result = InteractionResult.PASS;
    public final Player player;
    public final Entity interactedEntity;
    public final InteractionHand hand;

    public EntityInteractSpecificEvent(Player player, Entity interactedEntity, InteractionHand hand) {
        this.player = player;
        this.interactedEntity = interactedEntity;
        this.hand = hand;
    }

    public void setResult(InteractionResult result) {
        this.result = result;
    }

    public InteractionResult getResult() {
        return result;
    }
}
