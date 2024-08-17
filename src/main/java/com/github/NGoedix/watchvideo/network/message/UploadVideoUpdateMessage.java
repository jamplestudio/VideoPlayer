package com.github.NGoedix.watchvideo.network.message;

import com.github.NGoedix.watchvideo.block.entity.custom.TVBlockEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class UploadVideoUpdateMessage implements IMessage<UploadVideoUpdateMessage> {

    private BlockPos blockPos;
    private String url;
    private int volume;
    private int tick;
    private boolean isPlaying;
    private boolean stopped;
    private boolean exit;

    public UploadVideoUpdateMessage() {}

    public UploadVideoUpdateMessage(BlockPos blockPos, String url, int volume, int tick, boolean isPlaying, boolean stopped, boolean exit) {
        this.blockPos = blockPos;
        this.url = url;
        this.volume = volume;
        this.tick = tick;
        this.isPlaying = isPlaying;
        this.stopped = stopped;
        this.exit = exit;
    }

    @Override
    public void encode(UploadVideoUpdateMessage message, PacketBuffer buffer) {
        buffer.writeBlockPos(message.blockPos);
        buffer.writeUtf(message.url);
        buffer.writeInt(message.volume);
        buffer.writeInt(message.tick);
        buffer.writeBoolean(message.isPlaying);
        buffer.writeBoolean(message.stopped);
        buffer.writeBoolean(message.exit);
    }

    @Override
    public UploadVideoUpdateMessage decode(PacketBuffer buffer) {
        return new UploadVideoUpdateMessage(buffer.readBlockPos(), buffer.readUtf(), buffer.readInt(), buffer.readInt(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean());
    }

    @Override
    public void handle(UploadVideoUpdateMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player == null) return;
            if (player.level.getBlockEntity(message.blockPos) instanceof TVBlockEntity) {
                TVBlockEntity tvBlockEntity = (TVBlockEntity) player.level.getBlockEntity(message.blockPos);
                if (tvBlockEntity == null) return;

                if (message.exit)
                    tvBlockEntity.setBeingUsed(new UUID(0, 0));
                else {
                    tvBlockEntity.setUrl(message.url);
                    tvBlockEntity.setVolume(message.volume);

                    if (message.tick != -1)
                        tvBlockEntity.setTick(message.tick);

                    tvBlockEntity.setPlaying(message.isPlaying);

                    if (message.stopped)
                        tvBlockEntity.stop();

                    tvBlockEntity.notifyPlayer();
                }}
        });
    }
}
