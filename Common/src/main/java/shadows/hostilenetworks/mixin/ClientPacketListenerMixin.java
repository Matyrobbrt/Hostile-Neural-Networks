package shadows.hostilenetworks.mixin;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shadows.hostilenetworks.tile.UpdateableBlockEntity;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Shadow @Final private Connection connection;

    @Inject(at = @At("HEAD"), method = {"lambda$handleBlockEntityData$6", "method_38542", "m_205555_"}, remap = false)
    private void hnn$handleBlockEntityData(ClientboundBlockEntityDataPacket clientboundBlockEntityDataPacket, BlockEntity blockEntity, CallbackInfo ci) {
        if (blockEntity instanceof UpdateableBlockEntity updateableBlockEntity) {
            updateableBlockEntity.handleDataPacket(connection, clientboundBlockEntityDataPacket);
        }
    }
}
