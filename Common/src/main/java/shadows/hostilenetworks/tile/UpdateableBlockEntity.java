package shadows.hostilenetworks.tile;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;

public interface UpdateableBlockEntity {
    void handleDataPacket(Connection net, ClientboundBlockEntityDataPacket packet);
}
